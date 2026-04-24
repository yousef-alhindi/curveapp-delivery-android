package com.curve.delivery.response

data class UpdatePasswordResponse(
    val `data`: Data,
    val message: String,
    var success: Boolean
) {
    class Data
}