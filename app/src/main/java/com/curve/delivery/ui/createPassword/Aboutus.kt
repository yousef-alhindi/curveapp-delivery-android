package com.curve.delivery.ui.createPassword

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.curve.delivery.R
import com.curve.delivery.new_architecture.helper.CustomLoader
import com.curve.delivery.ui.home.VerticalSpacer
import com.curve.delivery.ui.resetPassword.ResetScreen
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.showToast
import com.curve.delivery.viewModel.M1ViewModel
import okhttp3.internal.wait

class Aboutus:ComponentActivity(){
    private val viewModel: M1ViewModel by viewModels()
    private var description =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ContactUsScreen(this) }
    }

    @Composable
    fun ContactUsScreen(contactUs: Context) {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            @Suppress("DEPRECATION") window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)


        LaunchedEffect(key1 = Unit) {
            viewModel.cmsManagement(SharedPreference.get(this@Aboutus).accessToken,Enums.CMSTYPE.ABOUT_US.type.toString(),"5")
        }

        val cmsData by viewModel.cmsManagementResponse.observeAsState()
        Log.d("TAG", "ContactUsScreen:Data $cmsData")

        if(cmsData?.data!=null) {
            CustomLoader.hideLoader()
            Column(
                modifier = Modifier
                    .padding(top = 0.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .background(color = Color.White),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(color = Color(0xFFF7F7F7))
                        .padding(start = 16.dp, end = 24.dp, top = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.back_ic),
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                onBackPressed()
                            })

                    Text(
                        text = stringResource(R.string.about_us),
                        modifier = Modifier.clickable {},
                        color = Color(0xFF333333),
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        fontSize = 14.sp)

                    Text(
                        text = "",
                        modifier = Modifier.clickable {},
                        color = Color(0xFFD63636),
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        fontSize = 14.sp)

                }

                /*Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    painter = painterResource(id = R.drawable.logo_login),
                    contentDescription = "")*/


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, top = 36.dp, bottom = 20.dp, end = 24.dp)) {
                    AndroidView(factory = { context ->
                        TextView(context).apply {
                            text = Html.fromHtml(
                                cmsData?.data?.description ?: "", Html.FROM_HTML_MODE_COMPACT)
                            setTextColor(android.graphics.Color.BLACK)
                            textSize = 14f
                        }
                    }, modifier = Modifier.fillMaxWidth())

                }
            }
        }else{
           CustomLoader.showLoader(this)
        }
    }


    @Composable
    fun WebViewScreen(url: String) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    webViewClient = WebViewClient() 
                    webChromeClient = WebChromeClient() 

                    loadUrl(url)
                }
            },
            update = { webView ->
                webView.loadUrl(url) 
            }
        )
    }

}

