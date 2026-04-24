package com.curve.delivery.ui.home

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.PersistableBundle
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.curve.delivery.R
import com.curve.delivery.new_architecture.helper.CommonUtils
import com.curve.delivery.ui.login.LoginScreen
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.showProgress
import com.curve.delivery.viewModel.M1ViewModel

class OrderHistoryView:ComponentActivity() {
    private val viewModel: M1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ViewOrderHistory() }
    }

    @Composable
    fun ViewOrderHistory(){
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            @Suppress("DEPRECATION") window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)

        val orderHistoryResponse by viewModel.getOrderHistoryResponse.observeAsState()

        Log.d("TAG", "ViewOrderHistory:Details $orderHistoryResponse ")
        val showPress by viewModel.progress.observeAsState()
        showProgress(showPress ?: false)

        LaunchedEffect(Unit) {
            viewModel.progress.value = true
            viewModel.getOrderHistory(SharedPreference.get(this@OrderHistoryView).accessToken)
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White))
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(color = Color(0xFFF7F7F7))
                    .padding(start = 16.dp, end = 24.dp, top = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Image(painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onBackPressed()
                        })

                Text(
                    text = stringResource(R.string.order_history),
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

                if (orderHistoryResponse?.data?.isNullOrEmpty() == true) {
                    Text(
                        text = stringResource(R.string.no_orders_found),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        modifier = Modifier.align(Alignment.Center))
                } else {


                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp) // spacing between items
                    ) {
                        items(orderHistoryResponse?.data?.size ?: 0) { index ->
                            val orderHistory = orderHistoryResponse?.data?.get(index)

                            Card(modifier = Modifier
                                .clickable {
                                    SharedPreference.get(this@OrderHistoryView).deliveryParticularId =
                                        orderHistory?._id.toString()
                                    startActivity(
                                        Intent(
                                            this@OrderHistoryView,
                                            OrderHistoryDetailsSide::class.java
                                        )
                                    )
                                }
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 10.dp),
                                colors = CardDefaults.cardColors(Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)) {
                                Column {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFE8F8F2))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(
                                                start = 15.dp,
                                                top = 10.dp,
                                                bottom = 10.dp),
                                        ) {
                                            Text(
                                                text = stringResource(R.string.order_id),
                                                color = Color(0XFF787878),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(
                                                    Font(R.font.montserrat_regular)))
                                            Text(
                                                text = "#${orderHistory?.orderId?.orderId ?: ""}",
                                                color = Color(0XFF33BD8C),
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
                                        }
                                        Spacer(modifier = Modifier.weight(1f))

                                        var deliveredStatus = ""
                                        if (orderHistory?.isDelivered == true) {
                                            deliveredStatus = "Delivered"
                                        } else {
                                            deliveredStatus = "Pending"
                                        }
                                        Text(
                                            text = deliveredStatus,
                                            modifier = Modifier
                                                .padding(start = 10.dp, top = 20.dp)
                                                .padding(end = 10.dp),
                                            fontSize = 11.sp,
                                            color = Color(0XFF33BD8C),
                                            fontFamily = FontFamily(
                                                Font(
                                                    R.font.montserrat_semibold,
                                                    FontWeight.Normal)))
                                    }

                                    //Middle Row
                                    Row {
                                        Column(
                                            modifier = Modifier.padding(
                                                start = 15.dp,
                                                top = 10.dp)) {
                                            Text(
                                                text = stringResource(R.string.delivery_date_time),
                                                color = Color(0XFF787878),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(
                                                    Font(R.font.montserrat_regular)))

                                            var dateState = CommonUtils.formatTimestampToDateString(
                                                orderHistory?.deliveredTime ?: 1728566050363)
                                            Log.d("TAG", "ViewOrderHistory:Da  $dateState")
                                            Text(
                                                text = "${dateState}",
                                                color = Color.Black,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily(
                                                    Font(
                                                        R.font.montserrat_semibold,
                                                        FontWeight.Bold)))
                                        }

                                        Spacer(modifier = Modifier.weight(1f))
                                        Column(
                                            modifier = Modifier.padding(end = 10.dp),
                                            horizontalAlignment = Alignment.End) {
                                            Text(text = stringResource(R.string.service_type),
                                                modifier = Modifier.padding(
                                                    start = 10.dp,
                                                    top = 10.dp),
                                                color = Color(0XFF787878),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(
                                                    Font(R.font.montserrat_regular, FontWeight.Normal)))

                                            var serviceType =""
                                            if(orderHistory?.orderType.equals("1")){
                                                serviceType= "Food Order"
                                            }else if(orderHistory?.orderType.equals("2")){
                                                serviceType="Package"
                                            }else if(orderHistory?.orderType.equals("3")){
                                                serviceType="Supplement"
                                            }
                                            else{
                                                serviceType="Food Order"
                                            }

                                            Text(text = serviceType,
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
                                    //Bottom Row

                                    ListItem(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ListItemDefaults.colors(
                                            containerColor = Color.White),
                                        headlineContent = {


                                            Text(
                                                text =if(orderHistory?.restId?.resName.isNullOrEmpty())
                                                    orderHistory?.restId?.name?:"" else  orderHistory?.restId?.resName ?: "",
                                                color = Color(0xFF333333),
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily(
                                                    Font(R.font.montserrat_bold, FontWeight.Bold)))
                                        },
                                        leadingContent = {

                                            if(orderHistory?.restId?.addressDetails?.resLogo==null){
                                                AsyncImage(
                                                    model = orderHistory?.restId?.addressDetails?.supLogo?:R.drawable.appicon,
                                                    placeholder = painterResource(
                                                        id = R.drawable.logo_login),
                                                    contentDescription = "",
                                                    modifier = Modifier
                                                        .clip(CircleShape)
                                                        .background(
                                                            color = Color.Gray,
                                                            shape = CircleShape
                                                        )
                                                        .size(50.dp),
                                                    contentScale = ContentScale.Crop,
                                                )
                                            }else{
                                            AsyncImage(
                                                model = orderHistory?.restId?.addressDetails?.resLogo?:R.drawable.appicon,
                                                placeholder = painterResource(
                                                    id = R.drawable.logo_login),
                                                contentDescription = "",
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(
                                                        color = Color.Gray,
                                                        shape = CircleShape
                                                    )
                                                    .size(50.dp),
                                                contentScale = ContentScale.Crop,
                                            )
                                            }
                                        },
                                        supportingContent = {
                                            Row {
                                                Icon(imageVector = Icons.Filled.LocationOn,
                                                    tint = Color(0xFF33BD8C),
                                                    contentDescription = "location icon",
                                                )
                                                Text(
                                                    text = orderHistory?.restId?.addressDetails?.address
                                                        ?: "Location",
                                                    color = Color(0xFF6B6B6B),
                                                    fontSize = 13.sp,
                                                    fontFamily = FontFamily(
                                                        Font(
                                                            R.font.montserrat_regular,
                                                            FontWeight.Normal)))
                                            }
                                        },
                                    )

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
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
        .height(1.dp)) {

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