package com.curve.delivery.new_architecture.helper

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.curve.delivery.R
import com.curve.delivery.util.Constant.showSnackBar
import com.curve.delivery.util.ErrorUtil.parseApiError
import com.curve.delivery.util.ErrorUtil.parseApiError1
import com.curve.delivery.util.showToastC
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


object ErrorUtil {
    fun handlerGeneralError(context: Context, throwable: Throwable) {
        throwable.printStackTrace()
        when (throwable) {
            is ConnectException -> showToastC(context,
                context.getString(R.string.please_turn_on_internet))
            is SocketTimeoutException -> showToastC(context,
                context.getString(R.string.socket_time_out_exception))
            is UnknownHostException -> showToastC(context,
                context.getString(R.string.no_internet_connection))
            is InternalError -> showToastC(context,
                context.getString(R.string.internal_server_error))
            is HttpException -> {
                val errorMessage = parseApiError1(throwable.response()?.errorBody())
                showToastC(context, errorMessage)
            }
            else -> {
                showToastC(context, context.getString(R.string.something_went_wrong))
            }
        }
    }

}


