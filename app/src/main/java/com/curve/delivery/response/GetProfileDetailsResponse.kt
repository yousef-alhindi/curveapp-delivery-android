package com.curve.delivery.response

data class GetProfileDetailsResponse(
    val `data`: Data,
    val message: String,
    var success: Boolean
) {
    data class Data(
        val _id: String,
        val accessToken: String,
        val addressDetails: AddressDetails,
        val bankDetails: BankDetails,
        val countryCode: String,
        val createdAt: Long,
        val deviceToken: String,
        val deviceType: Int,
        val dob: String,
        val documents: Documents,
        val email: String,
        val fullyVerify: Int,
        val gender: Int,
        val isBankDetailsUpdated: Boolean,
        val isBlocked: Boolean,
        val isDeleted: Boolean,
        val isDelivery: Boolean,
        val isDocumentsUploaded: Boolean,
        val isLocationDetails: Boolean,
        val isOtpVerified: Boolean,
        val isPickUp: Boolean,
        val isSmoke: Int,
        val isVechileDocUploaded: Boolean,
        val language: String,
        val location: Location,
        val mobileNumber: String,
        val name: String,
        val otp: Int,
        val password: String,
        val profileImage: String,
        val rating: Int,
        val rejected_reason: String,
        val status: Boolean,
        val updatedAt: Long,
        val vechileDetails: VechileDetails
    ) {
        data class AddressDetails(
            val address: String,
            val building: String,
            val postalCode: String,
            val street: String
        )

        data class BankDetails(
            val accHolderName: String,
            val bankAccountNo: String,
            val bankCode: String,
            val bankName: String
        )

        data class Documents(
            val idProofBack: String,
            val idProofFront: String,
            val policeBackground: String
        )

        data class Location(
            val coordinates: List<Double>,
            val type: String
        )

        data class VechileDetails(
            val RegistrationCertificateBack: String,
            val RegistrationCertificateFront: String,
            val drivingLicenseBack: String,
            val drivingLicenseFront: String,
            val imageBack: String,
            val imageFront: String,
            val vechileName: String
        )
    }
}