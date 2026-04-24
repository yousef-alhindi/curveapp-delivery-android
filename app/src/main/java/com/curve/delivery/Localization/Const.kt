package com.curve.delivery.Localization

import android.content.Context
import android.widget.Toast

object Const {
    var lang = "en"
    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}