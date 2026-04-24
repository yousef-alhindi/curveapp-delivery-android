package com.curve.delivery.ui.home

import android.app.DatePickerDialog
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.curve.delivery.R
import com.curve.delivery.new_architecture.helper.CommonUtils
import com.curve.delivery.new_architecture.helper.CustomLoader
import com.curve.delivery.response.EarningRequest
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.showProgress
import com.curve.delivery.viewModel.M1ViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

class MyEarning : ComponentActivity() {
    private val viewModel: M1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EarningScreen() }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EarningScreen() {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            @Suppress("DEPRECATION") window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)

        var selectedDate by remember { mutableStateOf("From") }
        var selectedToDate by remember { mutableStateOf("To") }

        var selectedFromDateTimestamp by remember { mutableStateOf(0L) }
        var selectedToDateTimestamp by remember { mutableStateOf(0L) }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.CustomDatePicker,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDate = String.format(
                    "%02d/%02d/%02d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear % 100)

                selectedFromDateTimestamp=CommonUtils.convertDateToTimestampFrom(selectedDate)?:0L

                Log.d("TAG", "EarningScreen:From $selectedDate")
            },
            year,
            month,
            day
        )
        val datePickerToDialog = DatePickerDialog(
            this,
            R.style.CustomDatePicker,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedToDate = String.format(
                    "%02d/%02d/%02d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear % 100
                )

                selectedToDateTimestamp=CommonUtils.convertDateToTimestampTo(selectedToDate)?:0L

                Log.d("TAG", "EarningScreen:To $selectedToDate")

            },
            year,
            month,
            day
        )

        //    datePickerDialog.datePicker.minDate = calendar.timeInMillis
        //  datePickerToDialog.datePicker.minDate = calendar.timeInMillis

        var showBottomSheetMoreDetails1 by remember {
            mutableStateOf(
                false)
        }
        val sheetStateMoreDetails1 = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()


        if (showBottomSheetMoreDetails1) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheetMoreDetails1 = false
                    scope.launch { sheetStateMoreDetails1.hide() }
                },
                dragHandle = {},
                sheetState = sheetStateMoreDetails1,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(
                            Color.White, shape = RoundedCornerShape(
                                topStart = 19.dp, topEnd = 19.dp
                            )
                        )) {
                    Column(
                        modifier = Modifier.fillMaxWidth()) {
                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFAEEEC4),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .background(
                                    color = Color(0xFFFFFFFF)
                                )) {
                            // Back Button
                            Box(
                                modifier = Modifier
                                    .align(
                                        Alignment.TopCenter
                                    )
                                    .padding(
                                        start = 18.dp, top = 16.dp
                                    )) {
                                Text(
                                    text = stringResource(R.string.filter), fontSize = 16.sp, fontFamily = FontFamily(
                                        Font(R.font.montserrat_semibold)), color = Color(0xFF2A2A2A))
                            }

                            Text(text = "X",
                                fontSize = 16.sp,
                                color = Color(0xFF333333),
                                textAlign = TextAlign.End,
                                fontFamily = FontFamily(
                                    Font(R.font.montserrat_semibold)),
                                modifier = Modifier
                                    .align(
                                        Alignment.Center
                                    )
                                    .fillMaxWidth()

                                    .padding(bottom = 8.dp, end = 16.dp)
                                    .clickable {
                                        viewModel.progress.value = false
                                        val model = EarningRequest(0L, 0L)
                                        viewModel.getEarning(
                                            SharedPreference.get(this@MyEarning).accessToken, model
                                        )

                                        scope.launch {
                                            if (sheetStateMoreDetails1.isVisible) {
                                                sheetStateMoreDetails1.hide()
                                                showBottomSheetMoreDetails1 = false
                                            } else {
                                                showBottomSheetMoreDetails1 = true
                                                sheetStateMoreDetails1.show()
                                            }
                                        }
                                    })
                        }

                        Column(modifier = Modifier.padding(
                                16.dp)) {

                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {

                                Box(modifier = Modifier
                                    .clickable {
                                        datePickerDialog.show()
                                    }
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(
                                        color = Color(0xFFF6F6F6),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        0.5.dp,
                                        Color(0xFFF6F6F6),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                                    contentAlignment = Alignment.CenterStart) {

                                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)) {
                                        Text(text = selectedDate,
                                            color = Color(0xFF6C7278),
                                            fontSize = 18.sp,
                                            fontFamily = FontFamily(Font(R.font.montserrat_regular)))

                                        Image(
                                            painter = painterResource(id = R.drawable.calendar),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))

                                Box(modifier = Modifier
                                    .clickable {
                                        datePickerToDialog.show()
                                    }
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(
                                        color = Color(0xFFF6F6F6),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        0.5.dp,
                                        Color(0xFFF6F6F6),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(
                                        start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp
                                    ),
                                    contentAlignment = Alignment.CenterStart) {

                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)) {
                                        Text(
                                            text = selectedToDate,
                                            color = Color(0xFF6C7278),
                                            fontSize = 18.sp,
                                            fontFamily = FontFamily(Font(R.font.montserrat_regular)))

                                        Image(
                                            painter = painterResource(id = R.drawable.calendar),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                scope.launch {
                                    viewModel.progress.value=false
                                    val model = EarningRequest(selectedFromDateTimestamp,selectedToDateTimestamp)
                                    viewModel.getEarning(SharedPreference.get(this@MyEarning).accessToken,model)

                                    var fromDate = selectedDate
                                    Log.d("TAG", "ManageTimeSlot: $fromDate")
                                    if (sheetStateMoreDetails1.isVisible) {
                                        sheetStateMoreDetails1.hide()
                                        showBottomSheetMoreDetails1 = false
                                    } else {
                                        showBottomSheetMoreDetails1 = true
                                        sheetStateMoreDetails1.show()
                                    }
                                }

                            },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clickable {},
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0XFF33BD8C)),
                                contentPadding = PaddingValues(10.dp),
                                shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    text = stringResource(R.string.apply),
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
                            }
                        }
                    }
                }
            }
        }

        val earningResponse by viewModel.getEarningResponse.observeAsState()
        Log.d("TAG", "ViewOrderHistory:Details $earningResponse ")
        val showPress by viewModel.progress.observeAsState()
        showProgress(showPress ?: false)

        LaunchedEffect(Unit) {
            viewModel.progress.value=true
            val model = EarningRequest(0L,0L)
            viewModel.getEarning(SharedPreference.get(this@MyEarning).accessToken,model)
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
                    text = stringResource(R.string.my_earning),
                    modifier = Modifier.clickable {},
                    color = Color(0xFF333333),
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontSize = 14.sp)

                Image(painter = painterResource(id = R.drawable.myearn_filter),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            showBottomSheetMoreDetails1 = true
                        })

            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp)
                            .height(170.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.myearn_bg),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                        Card(
                            colors = CardDefaults.cardColors(Color.Transparent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    modifier = Modifier.padding(50.dp),
                                    text = "KD ${earningResponse?.data?.statesData?.totalEarned ?: "0"}",
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.montserrat_bold,
                                            FontWeight.Bold
                                        )
                                    ),
                                    color = Color.Black,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = stringResource(R.string.total_amount_earned),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start=100.dp,bottom = 20.dp),
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.montserrat_regular,
                                            FontWeight.Normal
                                        )
                                    ),
                                    color = Color.Black.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Statistics Cards Row 1
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)) {
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            colors = CardDefaults.cardColors(Color.White),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()) {

                                Image(painter = painterResource(id = R.drawable.myearn_admin_commission), contentDescription = null,
                                    modifier = Modifier.size(44.dp))
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.admin_commission),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                    textAlign = TextAlign.Center,
                                    color = Color(0XFF555555))
                                Text(
                                    text = "KD ${earningResponse?.data?.statesData?.adminCommission?:"0"}",
                                    fontSize = 16.sp,
                                    color = Color(0XFF333333),
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.montserrat_bold,
                                            FontWeight.Bold)))
                            }
                        }
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            colors = CardDefaults.cardColors(Color.White),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()) {
                                Image(painter = painterResource(id = R.drawable.myearn_pending_amt), contentDescription = null,
                                    modifier = Modifier.size(44.dp))

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.pending_namount),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                    color = Color(0XFF555555))
                                Text(
                                    text = "KD ${earningResponse?.data?.statesData?.pendingAmount?:"0"}",
                                    fontSize = 16.sp,
                                    color = Color(0XFF333333),
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.montserrat_bold,
                                            FontWeight.Bold)))
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)) {
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            colors = CardDefaults.cardColors(Color.White),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()) {
                                Image(painter = painterResource(id = R.drawable.myearn_total_rides), contentDescription = null,
                                    modifier = Modifier.size(44.dp))

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.total_orders),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                    color = Color(0XFF555555))
                                Text(
                                    text = "${earningResponse?.data?.statesData?.totalOrders.toString()?:""}",
                                    fontSize = 16.sp,
                                    color = Color(0XFF333333),
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.montserrat_bold,
                                            FontWeight.Bold)))
                            }
                        }
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .weight(1f),
                            colors = CardDefaults.cardColors(Color.White),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()) {
                                Image(painter = painterResource(id = R.drawable.myearn_kilometter), contentDescription = null,
                                    modifier = Modifier.size(44.dp))
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.total_kilometter),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                    color = Color(0XFF555555))
                                Text(
                                    text = "${earningResponse?.data?.statesData?.totalKMTravel?:""}",
                                    fontSize = 16.sp,
                                    color = Color(0XFF333333),
                                    fontFamily = FontFamily(
                                        Font(R.font.montserrat_bold,
                                            FontWeight.Bold)))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.transactions_history),
                        color = Color(0xFF333333),
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                }
                items(earningResponse?.data?.orderTransactionHistory?.size?:0) {index->
                    val earningData = earningResponse?.data?.orderTransactionHistory?.get(index)
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 20.dp),
                        colors = CardDefaults.cardColors(Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column() {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F8F2))
                            ) {
                                Column(
                                    modifier = Modifier.padding(start = 15.dp, top = 10.dp, bottom = 10.dp), ) {
                                    Text(
                                        text = stringResource(R.string.order_id),
                                        color = Color(0XFF787878),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.montserrat_regular, FontWeight.Normal)))
                                    Text(
                                        text = "#${earningData?.orderId?.orderId?:""}",
                                        color = Color(0XFF33BD8C),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.montserrat_semibold, FontWeight.Bold)))
                                }
                                Spacer(
                                    modifier = Modifier.weight(1f))
                                Text(
                                    text = "KD ${earningData?.orderId?.totalAmount?:""}",
                                    modifier = Modifier
                                        .padding(start = 10.dp, top = 20.dp)
                                        .padding(end = 10.dp),
                                    fontSize = 11.sp,
                                    color = Color(0XFF051117),
                                    fontFamily = FontFamily(
                                        Font(R.font.montserrat_bold, FontWeight.Bold)))
                            }

                            //Middle Row
                            var dateState = CommonUtils.formatTimestampToDateString(earningData?.deliveredTime?:1728566050363)
                            Log.d("TAG", "ViewOrderHistory:Da  $dateState")
                            Row {

                                Column(modifier = Modifier.padding(start = 15.dp, top = 10.dp)) {
                                    Text(
                                        text = stringResource(R.string.delivery_date_time),
                                        color = Color(0XFF787878),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.montserrat_regular, FontWeight.Normal)))
                                    Text(
                                        text = dateState,
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.montserrat_semibold, FontWeight.Bold)))
                                }


                                var serviceType=""
                                if(earningData?.orderId?.orderType.equals("1")){
                                    serviceType= "Food Order"
                                }else if(earningData?.orderId?.orderType.equals("2")){
                                    serviceType="Package"
                                }else{
                                    serviceType="Supplement"
                                }
                                Spacer(
                                    modifier = Modifier.weight(1f))
                                Column(
                                    modifier = Modifier.padding(end = 10.dp),
                                    horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = stringResource(R.string.service_type),
                                        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                        color = Color(0XFF787878),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.montserrat_regular, FontWeight.Normal))

                                    )
                                    Text(
                                        text = serviceType,
                                        modifier = Modifier.padding(start = 10.dp),
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.montserrat_semibold, FontWeight.Bold)))
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp),

                                ) {
                                Text(
                                    text = stringResource(R.string.status),
                                    color = Color(0XFF787878),
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                        .weight(1f),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.montserrat_regular, FontWeight.Normal)),
                                )
                                Text(
                                    text = if(earningData?.isDelivered==true) "Completed" else "Pending",
                                    color = if(earningData?.isDelivered==true) Color(0xFF33BD8C) else Color(0XFFEA4335),
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                        .weight(1f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.End,
                                    fontFamily = FontFamily(
                                        Font(R.font.montserrat_semibold, FontWeight.SemiBold)),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}