package com.curve.delivery.ui.chatscreenss

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.curve.delivery.R
import com.curve.delivery.new_architecture.helper.CommonUtils
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.viewModel.M1ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.licotex.driver.adapter.ChatModel


class ChatScreenS : ComponentActivity() {
    private lateinit var dbReference: DatabaseReference
    private val viewModel: M1ViewModel by viewModels()

    private var mOrderId: String = ""
    private var mUserId: String = ""
    private var receiverImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOrderId = intent.getStringExtra("orderID").toString()
        mUserId = intent.getStringExtra("userId").toString()
        receiverImage = intent.getStringExtra("receiverImage").toString()

        setContent {
            ChatScreen(activity = this, viewModel = viewModel)
        }
    }

    @Composable
    fun ChatScreen(activity: Activity, viewModel: M1ViewModel) {
        LaunchedEffect(key1 = Unit) {
            dbReference = FirebaseDatabase.getInstance().getReference(mOrderId)
            viewModel.chatFetch(dbReference)
        }
        /*  Column(modifier = Modifier
              .fillMaxSize().imePadding()
              .background(color = Color.White)) {
              Column(modifier = Modifier
                  .fillMaxSize()
                      .background(color = Color.White))
              {*/


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFF7F7F7))
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Image(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "Back Button",
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { finish() })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.chat),
                        color = Color(0xFF333333),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
                    )
                }
            }
            ChatScreenDisplay(viewModel, activity, viewModel)
        }

    }
    // }


    @Composable
    fun ChatScreenDisplay(model: M1ViewModel, context: Activity, viewModel: M1ViewModel) {
        var messageText by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(model.messagesList.size) { index ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        if (model.messagesList[index].senderId == SharedPreference.get(context).deliveryId.toString()) {
                            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                Column(modifier = Modifier) {
                                    Box(modifier = Modifier) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .background(
                                                    color = Color(0xFFE8F8F2),
                                                    shape = RoundedCornerShape(
                                                        topStart = 16.dp,
                                                        topEnd = 16.dp,
                                                        bottomStart = 16.dp
                                                    )
                                                )
                                                .padding(12.dp)
                                        ) {

                                            Text(
                                                text = model.messagesList[index].message,
                                                color = Color.Black,
                                                modifier = Modifier.widthIn(0.dp, 300.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = CommonUtils.formatTimestamp(model.messagesList[index].timestamp),
                                        color = Color(0xFF9B9B9B),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular))
                                    )
                                }

                                AsyncImage(
                                    model = if (model.messagesList[index].senderImage.isNullOrEmpty()) R.drawable.profile_placeholder else model.messagesList[index].senderImage,
                                    placeholder = painterResource(
                                        id = R.drawable.profile_placeholder
                                    ),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .padding(start = 0.dp, top = 17.dp)
                                        .clip(CircleShape)
                                        .background(color = Color.Gray, shape = CircleShape)
                                        .size(30.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }


                        } else {
                            Row(modifier = Modifier.align(Alignment.CenterStart)) {
                                AsyncImage(
                                    model = if (model.messagesList[index].senderImage.isNullOrEmpty()) R.drawable.profile_placeholder else model.messagesList[index].senderImage,
                                    placeholder = painterResource(id = R.drawable.profile_placeholder),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .padding(end = 4.dp, top = 17.dp)
                                        .clip(CircleShape)
                                        .background(color = Color.Gray, shape = CircleShape)
                                        .size(30.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier) {
                                    Box(modifier = Modifier) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .background(
                                                    color = Color(0xFF33BD8C),
                                                    shape = RoundedCornerShape(
                                                        topStart = 16.dp,
                                                        topEnd = 16.dp,
                                                        bottomEnd = 16.dp
                                                    )
                                                )
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                text = model.messagesList[index].message,
                                                color = Color.White,
                                                modifier = Modifier.widthIn(0.dp, 300.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = CommonUtils.formatTimestamp(model.messagesList[index].timestamp),
                                        color = Color(0xFF9B9B9B),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular))
                                    )
                                }
                            }
                        }

                    }

                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                MessageInputBox(
                    messageText = messageText,
                    context = context,
                    viewModel,
                    onMessageTextChange = { messageText = it },
                    onSendMessage = {
                        // Add a new message to the list
                        if (messageText.isNotBlank()) {
                            messageText = ""
                        }
                    })
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MessageInputBox(
        messageText: String, context: Activity,
        viewModel: M1ViewModel,
        onMessageTextChange: (String) -> Unit,
        onSendMessage: () -> Unit
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFFEBEBEB),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .weight(1f)
            ) {
                TextField(
                    value = messageText,
                    onValueChange = onMessageTextChange,
                    modifier = Modifier
                        .wrapContentHeight(),
                    placeholder = {
                        Text(
                            "Type messages...",
                            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.gray_9D9D9D),
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.black)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF6F6F6),
                        unfocusedContainerColor = Color(0xFFF6F6F6),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onSend = {
                        onSendMessage()
                    }),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            updateChatToFirebase(
                                messageText, 1, mOrderId, context, viewModel
                            )
                            onSendMessage()
                        },
                    painter = painterResource(id = R.drawable.send_button),
                    contentDescription = null
                )
            }
        }
    }

    private fun updateChatToFirebase(
        messageUser: String,
        messageType: Int,
        orderId: String,
        context: Activity,
        viewModel: M1ViewModel
    ) {
        val key = dbReference.child(orderId).child("chat").child("messages").push().key
        val message = ChatModel(
            message = messageUser.trim(),
            messageType = messageType,
            receiverId = mUserId,
            senderId = SharedPreference.get(context).deliveryId.toString(),
            timestamp = System.currentTimeMillis(),
            receiverImage = receiverImage,
            senderImage = SharedPreference.get(this).profileImage
        )
        dbReference.child("chat").child("messages").child(key.toString()).setValue(message)
    }
}