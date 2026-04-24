package com.curve.delivery.response

data class GetPendingOrdersResponse(
    val `data`: List<Data>?=null,
    val message: String,
    var status: Boolean
) {
    data class Data(
        val _id: String,
        val accepted: Boolean,
        val createdAt: Long,
        val deliveryBoyRating: Int,
        val driverReview: String,
        val isDeleted: Boolean,
        val isDelivered: Boolean,
        val isPickUp: Boolean,
        val orderId: String,
        val rejectedBy: List<Any>,
        val restId: RestId,
        val updatedAt: Long
    ) {
        data class RestId(
            val _id: String,
            val addressDetails: AddressDetails,
            val location: Location,
            val resName: String?=null,
            val name:String?=null,

        ) {
            data class AddressDetails(
                val address: String,
                val building: String,
                val postalCode: String,
                val resLogo: String?=null,
                val supLogo:String?=null,
                val street: String
            )

            data class Location(
                val coordinates: List<Double>,
                val type: String
            )
        }
    }
}