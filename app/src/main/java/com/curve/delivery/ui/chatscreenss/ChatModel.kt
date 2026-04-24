package com.licotex.driver.adapter

data class ChatModel(
    var message: String? = "",
    var messageType: Int? =0,
    var receiverId: String? = "",
    var senderId: String? = "",
    var timestamp: Long? = 0,
    var receiverImage:String?="",
    var senderImage:String?=""

    )
data class  ReadUnreadModel(var hasUnreadMessages:Boolean)
data class Message(
    val senderId: String,
    val receiverId: String,
    val messageType: Int,
    val message: String,
    val timestamp: Long,
    var receiverImage:String?="",
    var senderImage:String?=""
)