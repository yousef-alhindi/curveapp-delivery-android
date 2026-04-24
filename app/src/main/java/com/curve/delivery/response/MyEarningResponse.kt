package com.curve.delivery.response

data class MyEarningResponse(
    val `data`: Data?=null,
    val message: String,
    val status: Boolean
) {
    data class Data(
        val orderTransactionHistory: List<OrderTransactionHistory>,
        val statesData: StatesData
    ) {
        data class OrderTransactionHistory(
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
            val restId: String,
            val updatedAt: Long
        ) {
            data class OrderId(
                val _id: String,
                val deliveryAmount: String,
                val deliveryOption: String,
                val discountedAmount: String,
                val orderId: String,
                val orderType: String,
                val totalAmount: String,
                val totalItemAmount: String)
        }

        data class StatesData(
            val totalEarned:String,
            val adminCommission: String,
            val pendingAmount: String,
            val totalKMTravel: String,
            val totalOrders: String
        )
    }
}