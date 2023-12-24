package uz.akbar.chatting.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.akbar.chatting.MainActivity
import uz.akbar.chatting.R
import uz.akbar.chatting.model.UserData
import uz.akbar.chatting.screen.ui.theme.ChattingTheme

class ProfileActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uid= intent.getStringExtra("uid")!!
                    var user by remember {
                        mutableStateOf(UserData("", "", "", ""))
                    }
                    var name by remember { mutableStateOf("") }
                    var email by remember { mutableStateOf("") }
                    val gotData = remember { mutableStateOf(false) }

                    val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestEmail()
                        .build()
                    val userRef = Firebase.database.reference.child("users").child(uid)
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val u = snapshot.getValue(UserData::class.java)
                            if (u != null){
                                if (!gotData.value){
                                    name = u.name!!
                                    email = u.email!!
                                    user = u
                                    gotData.value = true
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        Arrangement.spacedBy(6.dp)
                    ) {
                        Spacer(modifier = Modifier.height(120.dp))
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.photo)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.logo),
                            contentDescription = ("no image"),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(100.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        // Text Fields
                        OutlinedTextField(
                            value = name.toString(),
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            )
                        )

                        OutlinedTextField(
                            value = email.toString(),
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            )
                        )



                        // Update Button
                        Button(
                            onClick = {

                                userRef.child("name").setValue(name)
                                gotData.value = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(bottom = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2FF))
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Update")
                        }

                        // Logout Button
                        Button(
                            onClick = {
                                GoogleSignIn.getClient(this@ProfileActivity,gso).signOut()
                                startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(bottom = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A2FF))
                        ) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}

