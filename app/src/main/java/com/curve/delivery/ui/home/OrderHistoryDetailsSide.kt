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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.curve.delivery.R
import com.curve.delivery.new_architecture.helper.CommonUtils
import com.curve.delivery.ui.commonscreen.verticalSpacer
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.showProgress
import com.curve.delivery.viewModel.M1ViewModel

@SuppressLint("NotConstructor")
class OrderHistoryDetailsSide : ComponentActivity() {
    private val viewModel: M1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrderHistoryDetailsSide()
        }
    }

        @Composable
        fun OrderHistoryDetailsSide() {
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            } else {
                @Suppress("DEPRECATION") window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)

            val orderDetailsData = viewModel.getOrdersDetailsResponse.observeAsState()

            Log.d("TAG", "OrderHistoryDetailsSide:OrderDetail ${orderDetailsData.value}")
            val context = LocalContext.current
            val scroll = rememberScrollState()


            val showPress by viewModel.progress.observeAsState()
            showProgress(showPress ?: false)

            LaunchedEffect(key1 = Unit) {
                viewModel.progress.value = true
                viewModel.getOrdersDetails(SharedPreference.get(context).deliveryParticularId.toString(),
                    SharedPreference.get(context).accessToken)
            }

            Column(modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)) {
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
                        text = stringResource(R.string.order_details),
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scroll)
                        .heightIn(max = 700.dp)
                        .padding()) {

                    verticalSpacer(value = 10)

                    var deliverName = ""
                    if (orderDetailsData?.value?.data?.isDelivered == true) {
                        deliverName = "Delivered"
                    } else {
                        deliverName = "Pending"
                    }
                    Text(
                        text = deliverName,
                        modifier = Modifier.padding(start = 20.dp),
                        fontSize = 20.sp,
                        color = Color(0xFF33BD8C),
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
                    verticalSpacer(value = 16)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .background(color = Color(0xFFE8F8F2))) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.order_id),
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.montserrat_bold)))
                                Text(
                                    text = orderDetailsData.value?.data?.orderId?.orderId ?: "",
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.montserrat_bold)))
                            }

                            if (orderDetailsData?.value?.data?.deliveredTime != null) {
                                var dateState = CommonUtils.formatTimestampToDateString(
                                    orderDetailsData?.value?.data?.deliveredTime ?: 1728566050363)
                                Log.d("TAG", "ViewOrderHistory:Da  $dateState")
                                Text(
                                    text = dateState,
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 20.dp),
                                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
                            }
                        }
                    }

                    verticalSpacer(value = 10)
                    OrderSection(
                        title = "Order Pickup From",
                        name =if(orderDetailsData?.value?.data?.restId?.resName.isNullOrEmpty())orderDetailsData?.value?.data?.restId?.name?:"" else orderDetailsData?.value?.data?.restId?.resName ?: "",
                        address = orderDetailsData?.value?.data?.restId?.addressDetails?.address
                            ?: "",
                        image = if(orderDetailsData?.value?.data?.restId?.addressDetails?.resLogo.isNullOrEmpty())orderDetailsData?.value?.data?.restId?.addressDetails?.supLogo?:"" else orderDetailsData?.value?.data?.restId?.addressDetails?.resLogo
                            ?: "")

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = Color(0xFF9D9D9D)))
                    Spacer(modifier = Modifier.height(16.dp))

                    var image = ""
                    if (orderDetailsData?.value?.data?.userData != null) {
                        image = orderDetailsData?.value?.data?.userData?.profileImage ?: ""
                    }

                    OrderSection(title = "Order Deliver To",
                        name = orderDetailsData?.value?.data?.userData?.fullName ?: "",
                        address = orderDetailsData?.value?.data?.orderId?.addressId?.address ?: "",
                        image = image)

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = Color(0xFF9D9D9D)))
                    Spacer(modifier = Modifier.height(16.dp))

                    if (orderDetailsData?.value?.data?.deliveryBoyRating != 0) {
                        Text(
                        text = stringResource(R.string.rating_review_received),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp),
                        fontSize = 13.sp,
                        fontFamily = FontFamily(
                            Font(R.font.montserrat_semibold)))
                    Spacer(modifier = Modifier.height(8.dp))
                    var rating2 by remember {
                        mutableStateOf(
                            orderDetailsData?.value?.data?.deliveryBoyRating ?: 0)
                    }

                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        Log.d(
                            "TAG",
                            "OrderHistoryDetailsSide:Rating ${orderDetailsData?.value?.data?.deliveryBoyRating} ")

                        if ((orderDetailsData?.value?.data?.deliveryBoyRating ?: 0) > 0) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp)) {
                                for (i in 1..5) {
                                    Image(
                                        painter = painterResource(
                                            id = if (i <= (orderDetailsData?.value?.data?.deliveryBoyRating
                                                    ?: 0)) R.drawable.ratting_star_yellow else R.drawable.ratting_star_grey),
                                        contentDescription = "rating star",
                                        modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                            }
                        }
                    }


                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text =orderDetailsData?.value?.data?.driverReview?:"No Review",
                        color = Color.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        fontFamily = FontFamily(
                            Font(R.font.montserrat_regular)))
                }
            }

        }


      @Composable
      fun OrderSection(title: String, name: String, address: String, image: String) {
          Column(modifier = Modifier
              .fillMaxWidth()
              .padding(start = 16.dp, end = 16.dp)) {
              Text(
                  text = title,
                  color = Color(0xFF39C166),
                  fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                  fontSize = 10.sp)
              Spacer(modifier = Modifier.height(8.dp))

              Row(verticalAlignment = Alignment.CenterVertically) {
                  Column(modifier = Modifier.weight(1f)) {
                      Row(
                          horizontalArrangement = Arrangement.spacedBy(8.dp),
                          verticalAlignment = Alignment.CenterVertically) {
                          Image(
                              painter = painterResource(id = R.drawable.ord_location_navigation),
                              contentDescription = null,
                              Modifier.size(11.dp))
                          Text(
                              text = name,
                              fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                              fontSize = 16.sp,
                              color = Color(0xFF333333))
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                          Image(
                              painter = painterResource(id = R.drawable.ord_location_navigation),
                              contentDescription = null,
                              Modifier.size(11.dp))
                          Text(
                              text = address,
                              fontSize = 12.sp,
                              fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                              color = Color(0xFF6B6B6B))
                      }
                  }
                  Spacer(modifier = Modifier.width(16.dp))
                  if(image.isNullOrEmpty() && image.equals("")){
                      Image(painter = painterResource(id = R.drawable.profile_placeholder),
                          contentDescription = null,
                          modifier = Modifier
                              .width(75.dp)
                              .height(59.dp)
                              .clip(RoundedCornerShape(8.dp)))
                  }else {
                      AsyncImage(
                          model = image,
                          placeholder = painterResource(
                              id = R.drawable.logo_login),
                          contentDescription = "",
                          modifier = Modifier
                              .clip(CircleShape)
                              .background(color = Color.White, shape = CircleShape)
                              .size(80.dp),
                          contentScale = ContentScale.FillBounds,
                      )
                  }
            }
        }
    }
}