package com.curve.delivery.response

data class UpdateProfileRequest(
    val dob: String,
    val email: String,
    val gender: Int,
    val language: String,
    val name: String,
    val profileImage: String,
    val documents: Documents,
    ) {
    data class Documents(
        val idProofBack: String,
        val idProofFront: String,
        val policeBackground: String
    )
}