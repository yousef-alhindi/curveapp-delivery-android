package com.curve.delivery.response

data class GetOrdersDetailsResponse(
    val `data`: Data?=null,
    val message: String,
    var status: Boolean
) {
    data class Data(
        val _id: String,
        val accepted: Boolean,
        val createdAt: String?=null,
        val deliveredTime: Long?=null,
        val deliveryBoyId: String,
        val deliveryBoyRating: Int,
        val driverReview: String,
        val isDeleted: Boolean,
        val isDelivered: Boolean,
        val isPickUp: Boolean,
        val orderId: OrderId?=null,
        val pickupTime: String?=null,
        val rejectedBy: List<Any>?=null,
        val restId: RestId,
        val updatedAt: String?=null,
        val userData: UserData?=null) {

        data class OrderId(
            val _id: String,
            val addressId: AddressId?=null,
            val orderId: String,
            val userId: String) {

            data class AddressId(
                val _id: String,
                val address: String,
                val addressLabel: Int,
                val buildingName: String,
                val houseNo: String,
                val landmarkName: String,
                val location: Location?=null) {
                data class Location(
                    val coordinates: List<Double>?=null,
                    val type: String)
            }
        }

        data class RestId(
            val _id: String,
            val addressDetails: AddressDetails?=null,
            val location: Location?=null,
            val profileImage: String,
            val resName: String?=null,
            val name:String?=null,
            ) {

            data class AddressDetails(
                val address: String,
                val building: String,
                val postalCode: String,
                val resLogo: String,
                val supLogo:String?=null,
                val street: String)

            data class Location(
                val coordinates: List<Double>,
                val type: String
            )
        }

        data class UserData(
            val countryCode: String,
            val fullName: String,
            val mobileNumber: String,
            val profileImage: String
        )
    }
}