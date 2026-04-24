package com.curve.delivery.ui.home

import android.annotation.SuppressLint
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.curve.delivery.R
import com.curve.delivery.new_architecture.helper.CommonUtils
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.showProgress
import com.curve.delivery.viewModel.M1ViewModel

class RateAndReview : ComponentActivity() {
    private val viewModel: M1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RateAndReview() }
    }

    @SuppressLint("NotConstructor")
    @Composable
    fun RateAndReview() {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            @Suppress("DEPRECATION") window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)

        val context = LocalContext.current
        val rateReview  by viewModel.rateReviewResponse.observeAsState()
        val showPress by viewModel.progress.observeAsState()
        showProgress(showPress ?: false)

        Log.d("TAG", "RateAndReview:Rate ${rateReview?.data} ")

        LaunchedEffect(Unit) {
            viewModel.progress.value = true
            viewModel.rateReviewResponse(SharedPreference.get(context).accessToken)
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)) {
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
                    text = stringResource(R.string.rating_reviews),
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

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)) {

                if (rateReview?.data?.isNullOrEmpty() == true) {
                    Text(
                        text = stringResource(R.string.no_rating_available),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        modifier = Modifier.align(Alignment.Center))
                } else {

                    LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),

                ) {
                        items(rateReview?.data?.size ?: 0) { index ->
                            val reviewData = rateReview?.data?.get(index)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 15.dp, bottom = 15.dp),
                                colors = CardDefaults.cardColors(Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFE8F8F2))

                                    ) {
                                        Column(
                                            modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                                        ) {
                                            Text(
                                                text = stringResource(R.string.order_id),
                                                color = Color(0XFF787878),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(
                                                    Font(
                                                        R.font.montserrat_regular,
                                                        FontWeight.Normal)))
                                            Text(
                                                text = "#${reviewData?.orderId?.orderId ?: "0"}",
                                                color = Color(0XFF33BD8C),
                                                modifier = Modifier.padding(bottom = 10.dp),
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily(
                                                    Font(
                                                        R.font.montserrat_semibold,
                                                        FontWeight.Bold)))
                                        }
                                        Spacer(
                                            modifier = Modifier.weight(1f))

                                        var rating2 by remember {
                                            mutableStateOf(
                                                reviewData?.deliveryBoyRating ?: 0)
                                        }

                                        if (rating2 > 0) {
                                            Row(
                                                horizontalArrangement = Arrangement.Start,
                                                modifier = Modifier.padding(
                                                    start = 16.dp,
                                                    top = 20.dp,
                                                    end = 16.dp,
                                                    bottom = 10.dp)) {
                                                for (i in 1..5) {
                                                    Image(
                                                        painter = painterResource(id = if (i <= rating2) R.drawable.ratting_star_yellow else R.drawable.ratting_star_grey),
                                                        contentDescription = "rating star",
                                                        modifier = Modifier.size(15.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                            }
                                        }
                                    }

                                    //Middle Row

                                    var dateState = CommonUtils.formatTimestampToDateString(
                                        reviewData?.createdAt ?: 1728566050363)
                                    Log.d("TAG", "ViewOrderHistory:Da  $dateState")
                                    Row {

                                        Column(
                                            modifier = Modifier.padding(
                                                start = 15.dp,
                                                top = 10.dp)) {
                                            Text(
                                                text = stringResource(R.string.delivery_date),
                                                color = Color(0XFF787878),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(
                                                    Font(
                                                        R.font.montserrat_regular,
                                                        FontWeight.Normal)))
                                            Text(
                                                text = dateState,
                                                color = Color.Black,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily(
                                                    Font(
                                                        R.font.montserrat_semibold,
                                                        FontWeight.Bold)))
                                        }


                                        Spacer(
                                            modifier = Modifier.weight(1f))
                                        Column(
                                            modifier = Modifier.padding(end = 10.dp),
                                            horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = stringResource(R.string.service_type),
                                                modifier = Modifier.padding(
                                                    start = 10.dp,
                                                    top = 10.dp),
                                                color = Color(0XFF787878),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(
                                                    Font(
                                                        R.font.montserrat_regular,
                                                        FontWeight.Normal))

                                            )
                                            Text(
                                                text = stringResource(R.string.food_order),
                                                modifier = Modifier.padding(start = 10.dp),
                                                color = Color.Black,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily(
                                                    Font(
                                                        R.font.montserrat_semibold,
                                                        FontWeight.Bold)))
                                        }


                                    }
                                    DottedDivider()

                                    Log.d("TAG", "RateAndReview: ${reviewData?.review},${reviewData?.driverReview}")

                                    if(reviewData?.review!=null){
                                        Text(
                                            text = reviewData?.review ?: "No Reviews",
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(
                                                Font(R.font.montserrat_regular, FontWeight.Normal)),
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 10.dp),
                                            color = Color(0XFF9D9D9D))
                                    }else {
                                        Text(
                                            text = reviewData?.driverReview ?: "No Reviews",
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(
                                                Font(
                                                    R.font.montserrat_regular, FontWeight.Normal)),
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp, vertical = 10.dp),
                                            color = Color(0XFF9D9D9D))
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @Composable
    fun DottedDivider() {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(1.dp)
        ) {
            val canvasWidth = size.width
            val dotSpacing = 4.dp.toPx()
            val dotRadius = 1.dp.toPx()
            var currentX = 0f

            while (currentX < canvasWidth) {
                drawCircle(
                    color = Color(0xFFE1E1E1),
                    radius = dotRadius,
                    center = Offset(currentX, size.height / 2)
                )
                currentX += dotSpacing + dotRadius * 2
            }
        }
    }

}

