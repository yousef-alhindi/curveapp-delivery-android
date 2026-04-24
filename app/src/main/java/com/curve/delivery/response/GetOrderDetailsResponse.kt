package com.curve.delivery.response

data class GetOrderDetailsResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Boolean
) {
    data class Data(
        val _id: String,
        val accepted: Boolean,
        val createdAt: Long,
        val deliveredTime: Long,
        val deliveryBoyId: String,
        val deliveryBoyRating: Int,
        val isDeleted: Boolean,
        val isDelivered: Boolean,
        val isPickUp: Boolean,
        val orderId: OrderId,
        val pickupTime: Long,
        val rejectedBy: List<Any>,
        val restId: RestId,
        val updatedAt: Long,
        val orderType:String?=null
    ) {
        data class OrderId(
            val _id: String,
            val deliveryAmount: Int,
            val deliveryOption: Int,
            val discountedAmount: Int,
            val orderId: String,
            val orderType: Int,
            val totalAmount: Int,
            val totalItemAmount: Int
        )

        data class RestId(
            val _id: String,
            val addressDetails: AddressDetails,
            val location: Location,
            val ownerName: String,
            val resName: String?=null,
            val name:String?=null
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