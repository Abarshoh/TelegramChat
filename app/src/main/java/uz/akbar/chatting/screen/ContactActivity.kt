package uz.akbar.chatting.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.akbar.chatting.R
import uz.akbar.chatting.model.UserData
import uz.akbar.chatting.screen.ui.theme.ChattingTheme


class ContactActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uid = intent.getStringExtra("uid")

                    val userList = remember { mutableStateListOf(UserData())
                    }

                    val reference = Firebase.database.reference.child("users")
                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val u = snapshot.children
                            userList.clear()
                            u.forEach {
                                val userData = it.getValue(UserData::class.java)
                                if (userData != null && uid != userData.uid) {
                                    userList.add(userData)
                                }
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("TAG", "onCancelled: ${error.message}")
                        }

                    })
                    Column(Modifier.fillMaxSize()) {
                        Row(
                            Modifier
                                .height(60.dp)
                                .background(color = Color(0xFF00A2FF))
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick={
                                    val intent = Intent(this@ContactActivity, ProfileActivity::class.java)
                                    intent.putExtra("uid", uid )
                                    startActivity(intent)

                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(60.dp)
                            ){
                                Icon(Icons.Rounded.Settings, contentDescription = "", tint = Color.White)
                            }

                            Text(text = "Messenger", color = Color.White, fontSize = 25.sp)
                            Spacer(modifier = Modifier
                                .width(30.dp))

                        }
                        LazyColumn() {
                            items(userList) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .clickable {
                                            val i = Intent(
                                                this@ContactActivity,
                                                MessageActivity::class.java
                                            )
                                            i.putExtra("uid", uid)
                                            i.putExtra("user", it)
                                            i.putExtra("useruid", it.uid)
                                            startActivity(i)
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(it.photo)
                                            .crossfade(true)
                                            .build(),
                                        placeholder = painterResource(R.drawable.user),
                                        contentDescription = ("no image"),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.clip(CircleShape)
                                    )
                                    Text(
                                        text = it.name ?: "",
                                        Modifier.padding(start = 12.dp),
                                        fontSize = 22.sp
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

