package com.curve.delivery.ui.createPassword

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.curve.delivery.R
import com.curve.delivery.new_architecture.helper.CustomLoader
import com.curve.delivery.ui.home.VerticalSpacer
import com.curve.delivery.ui.resetPassword.ResetScreen
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.viewModel.M1ViewModel
import okhttp3.internal.wait

class Faqs:ComponentActivity(){
    private val viewModel: M1ViewModel by viewModels()

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
            viewModel.faqListResponse(SharedPreference.get(this@Faqs).accessToken,"5")
        }

        val cmsData by viewModel.faqListResponse.observeAsState()
        Log.d("TAG", "ContactUsScreen:Data $cmsData")

        if(cmsData?.data!=null) {
            CustomLoader.hideLoader()
            Column(
                modifier = Modifier
                    .padding(top = 0.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = Color.White),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top) {

                Row(modifier = Modifier
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
                        text = stringResource(R.string.faq_s),
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
                data class FAQ(val question: String, val answer: String)

                val faqs = listOf(
                    FAQ(
                        "What is Lorem Ipsum?",
                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry."),
                    FAQ(
                        "Why do we use it?",
                        "It is a long established fact that a reader will be distracted by the readable content."),
                    FAQ(
                        "Where does it come from?",
                        "Contrary to popular belief, Lorem Ipsum is not simply random text."),
                    FAQ(
                        "Where can I get some?",
                        "There are many variations of passages of Lorem Ipsum available."))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
                    items(cmsData?.data?.size?:0) { faq ->
                        ExpandableCard(question = cmsData?.data?.get(faq)?.question?:"", answer = cmsData?.data?.get(faq)?.answer?:"")
                    }
                }
            }
        }else{
            CustomLoader.showLoader(this)
        }

    }

    @Composable
    fun ExpandableCard(question: String, answer: String) {
        // State to toggle between expanded and collapsed
        var isExpanded by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .background(color = Color.White)
                    .padding(16.dp)
            ) {
                // Question row with expand/collapse arrow
             /*   Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = question,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(end = 16.dp),
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)), )

                    Image(painter = painterResource(id =if(isExpanded) R.drawable.arrow_drop_down else R.drawable.calender_right_arrow), contentDescription =null,
                        modifier = Modifier.size(16.dp))
                }*/

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = question,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        modifier = Modifier
                            .weight(1f) // Take up remaining space while respecting padding
                            .padding(end = 16.dp),
                        maxLines = 2, // Optional: limit to a max number of lines
                        overflow = TextOverflow.Ellipsis // Optional: Handle overflow gracefully
                    )

                    Image(
                        painter = painterResource(id = if (isExpanded) R.drawable.arrow_up_view else R.drawable.arrow_right_view),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }


                // Expanded content (answer text)
                if (isExpanded) {
                    Spacer(modifier = Modifier
                        .height(8.dp)
                        .padding(end = 16.dp))
                    Text(
                        text = answer,
                        fontSize = 12.sp,
                        color = Color(0xFF9D9D9D),
                        fontFamily = FontFamily(Font(R.font.montserrat_regular))
                    )
                }
            }
        }
    }

}

