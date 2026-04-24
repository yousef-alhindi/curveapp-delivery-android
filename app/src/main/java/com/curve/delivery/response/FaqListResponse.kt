package com.curve.delivery.response

data class FaqListResponse(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val _id: String,
        val answer: String,
        val createdAt: String,
        val isDeleted: Boolean,
        val question: String,
        val service: Int,
        val status: Boolean,
        val updatedAt: String
    )
}