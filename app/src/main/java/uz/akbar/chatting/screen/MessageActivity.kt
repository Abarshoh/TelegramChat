package uz.akbar.chatting.screen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
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
import uz.akbar.chatting.model.Message
import uz.akbar.chatting.model.UserData
import uz.akbar.chatting.screen.ui.theme.ChattingTheme
import uz.akbar.chatting.ui.theme.Text3
import java.text.SimpleDateFormat
import java.util.Date

class MessageActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    val messageList = remember {
                        mutableStateListOf(Message())
                    }

                    var text = remember {
                        mutableStateOf(TextFieldValue(""))
                    }

                    val uid = intent.getStringExtra("uid")
                    val useruid = intent.getStringExtra("useruid")
                    val user = intent.getSerializableExtra("user") as UserData
                    val messagee = Message(useruid, uid, text.value.text, getDate())

                    val reference =
                        Firebase.database.reference.child("users").child(uid ?: "").child("message")
                            .child(useruid ?: "")

                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val xabar = snapshot.children
                            messageList.clear()
                            xabar.forEach {
                                val message = it.getValue(Message::class.java)
                                if (message != null) {
                                    messageList.add(message)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("TAG", "error:${error.message}")
                        }
                    })




                    Column(Modifier.fillMaxSize()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .size(80.dp)
                                .background(Color(0xFF00A2FF))
                        ) {

                            Image(painter = painterResource(id = R.drawable.back),
                                contentDescription = null,
                                Modifier
                                    .size(60.dp)
                                    .padding(horizontal = 10.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        onBackPressed()
                                    })

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(user.photo)
                                    .crossfade(true).build(),
                                placeholder = painterResource(R.drawable.logo),
                                contentDescription = ("no image"),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(40.dp)
                                    .align(Alignment.CenterVertically)
                            )
                            Text(
                                text = user.name ?: "asdasdasd",
                                Modifier.padding(start = 12.dp)
                                    .align(Alignment.CenterVertically),


                                fontSize = 22.sp,
                                color = White,

                            )


                        }
                        LazyColumn(
                            Modifier
                                .fillMaxWidth()
                                .weight(9f)
                        ) {
                            items(messageList) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    if (uid == it.from) {

                                        Card(
                                            modifier = Modifier.align(Alignment.End),
                                            shape = RoundedCornerShape(28.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(R.color.purple_500)
                                            )
                                        ) {
                                            Text(
                                                text = it.text ?: "",
                                                fontSize = 20.sp,
                                                color = White,
                                                modifier = Modifier.padding(
                                                    horizontal = 15.dp, vertical = 10.dp
                                                )
                                            )
                                        }
                                        Text(
                                            text = it.date ?: "",
                                            fontSize = 11.sp,
                                            modifier = Modifier.align(Alignment.End),
                                        )
                                    } else {
                                        Card(
                                            modifier = Modifier.align(Alignment.Start),
                                            shape = RoundedCornerShape(28.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.Blue
                                            )
                                        ) {
                                            Text(
                                                text = it.text ?: "",
                                                fontSize = 20.sp,
                                                color = White,
                                                modifier = Modifier.padding(
                                                    horizontal = 15.dp, vertical = 10.dp
                                                )
                                            )
                                        }
                                        Text(
                                            text = it.date ?: "",
                                            fontSize = 11.sp,
                                            modifier = Modifier.align(Alignment.Start),
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            OutlinedTextField(
                                text.value,
                                onValueChange = {
                                    text.value = it
                                },
                                Modifier.weight(5f),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Blue, unfocusedBorderColor = Gray
                                )
                            )


                            Image(painter = painterResource(id = R.drawable.jonatish),
                                contentDescription = null,
                                Modifier
                                    .size(40.dp)
                                    .padding(horizontal = 6.dp)
                                    .clickable {
                                        val reference = Firebase.database.reference.child("users")
                                        val key = reference.push().key.toString()
                                        text.value = TextFieldValue("")
                                        reference
                                            .child(uid ?: "")
                                            .child("message")
                                            .child(useruid ?: "")
                                            .child(key)
                                            .setValue(messagee)
                                        reference
                                            .child(useruid ?: "")
                                            .child("message")
                                            .child(uid ?: "")
                                            .child(key)
                                            .setValue(messagee)
                                    })
                        }
                    }
                }
            }


        }
    }

    fun getDate(): String {
        val d = Date()
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
        return simpleDateFormat.format(d)
    }
}

