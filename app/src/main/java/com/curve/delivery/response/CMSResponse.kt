package com.curve.delivery.response

data class CMSResponse(
    val `data`: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val _id: String,
        val contactDetail: ContactDetail?=null,
        val createdAt: String,
        val description: String,
        val isDeleted: Boolean,
        val service: Int,
        val status: Boolean,
        val type: Int,
        val updatedAt: String
    ) {
        data class ContactDetail(
            val email: String,
            val phoneNumber: String
        )
    }
}