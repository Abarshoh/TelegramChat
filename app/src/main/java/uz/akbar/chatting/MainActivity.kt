package uz.akbar.chatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.akbar.chatting.model.UserData
import uz.akbar.chatting.screen.ContactActivity
import uz.akbar.chatting.ui.theme.ChattingTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    auth = FirebaseAuth.getInstance()

                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestEmail()
                        .build()

                    val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.messanger), contentDescription ="" )
                        Button(onClick = {


                            val signInIntent = mGoogleSignInClient.signInIntent
                            startActivityForResult(signInIntent, 1)
                        },
                            modifier = Modifier,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2FF))
                        ) {
                            Text(text = "Sign In with Google")
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
                Log.d("TAG", "onActivityResult: ")
            } catch (e: ApiException) {
                Log.d("TAG", "error: $e")

            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    val userData = UserData(
                        user?.displayName,
                        user?.uid,
                        user?.email,
                        user?.photoUrl.toString()
                    )
                    val reference = Firebase.database.reference.child("users")
                    var b = true
                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val children = snapshot.children
                            children.forEach {
                                val user = it.getValue(UserData::class.java)
                                if (user != null && user.uid == userData.uid) {
                                    b=false
                                }
                            }
                            if(b){
                                setUser(userData)
                            }

                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.d("TAG", "onCancelled: ${error.message}")
                        }

                    })

                    val i = Intent(this, ContactActivity::class.java)
                    i.putExtra("uid", userData.uid)
                    startActivity(i)


                } else {
                    Log.d("TAG", "error: Authentication Failed.")
                }
            }
    }

    private fun setUser(userData: UserData) {
        val userIdReference = Firebase.database.reference
            .child("users").child(userData.uid?:"")
        userIdReference.setValue(userData).addOnSuccessListener {
            val i = Intent(this, ContactActivity::class.java)
            i.putExtra("uid", userData.uid)
            startActivity(i)
        }
    }
}

