package com.curve.delivery.response

data class OrderPickupResponse(
    val `data`: Data,
    val message: String,
    var status: Boolean
) {
    data class Data(
        val _id: String,
        val accepted: Boolean,
        val createdAt: Long,
        val deliveryBoyId: String,
        val isDeleted: Boolean,
        val isDelivered: Boolean,
        val isPickUp: Boolean,
        val orderId: String,
        val pickupTime: Long,
        val rejectedBy: List<Any>,
        val restId: String,
        val updatedAt: Long
    )
}