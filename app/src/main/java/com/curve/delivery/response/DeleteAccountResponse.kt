package com.curve.delivery.response

import com.curve.delivery.response.MyEarningResponse.Data

data class DeleteAccountResponse(
    val `data`: Data?=null,
    val message: String,
    var status: Boolean
)