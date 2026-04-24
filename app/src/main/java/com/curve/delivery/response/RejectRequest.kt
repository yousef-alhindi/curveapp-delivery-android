package com.curve.delivery.response

data class RejectRequest(
    val deliveryBoyId: String,
    val rejectReason: String
)