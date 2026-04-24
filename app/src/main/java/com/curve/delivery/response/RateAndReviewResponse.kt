package com.curve.delivery.response

data class RateAndReviewResponse(
    val `data`: List<Data>?=null,
    val message: String,
    val status: Boolean
) {
    data class Data(
        val _id: String,
        val createdAt: Long,
        val deliveryBoyRating: Int,
        val driverReview: String,
        val isDeleted: Boolean,
        val items: List<Any>,
        val orderId: OrderId,
        val restId: String,
        val review: String,
        val star: Int,
        val status: Boolean,
        val updatedAt: Long,
        val userId: String
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
    }
}