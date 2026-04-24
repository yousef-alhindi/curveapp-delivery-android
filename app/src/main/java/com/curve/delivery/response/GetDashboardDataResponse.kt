package com.curve.delivery.response

data class GetDashboardDataResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean
) {
    data class Data(
        val totalEarnings: String,
        val totalOrders: String
    )
}