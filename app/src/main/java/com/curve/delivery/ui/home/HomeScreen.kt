package com.curve.delivery.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.curve.delivery.R
import com.curve.delivery.databinding.GpsDialogBinding
import com.curve.delivery.new_architecture.helper.CustomLoader
import com.curve.delivery.response.AcceptRequest
import com.curve.delivery.response.RejectRequest
import com.curve.delivery.response.UpdateDeliveryAddressRequest
import com.curve.delivery.ui.login.LoginScreen
import com.curve.delivery.ui.orderhistory.OrderDetails
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.getAddress
import com.curve.delivery.util.isGPSEnabled
import com.curve.delivery.util.moveActivity
import com.curve.delivery.util.showToast
import com.curve.delivery.viewModel.M1ViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class HomeScreen : ComponentActivity() {
    private var pressedTime: Long = 0
    private val viewModel: M1ViewModel by viewModels()
    companion object {
        var lat = 0.0
        var lng = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer()
        setContent { HomeScreenContent() }
    }

    override fun onResume() {
        super.onResume()

        viewModel.progress.value = true
        viewModel.getProfileDetails(SharedPreference.get(this).accessToken)

    }

    fun observer() {
        viewModel.progress.observe(this) {
            if (viewModel.progress.value == true) {
                CustomLoader.showLoader(this)
            } else {
                CustomLoader.hideLoader()
            }
        }

        viewModel.getPendingOrdersResponse.observe(this) {
            Log.d("TAG", "observer: accept..pending")
        }

        viewModel.acceptOrdersResponse.observe(this) {
            Log.d("TAG", "observer: accept")
//            showToast(it.message)
            SharedPreference.get(this).deliveryParticularId = it.data?._id.toString() ?: ""
            viewModel.mDeliveryId.value = it.data._id.toString() ?: ""
            moveActivity(OrderDetails())
        }

        viewModel.mError.observe(this) {
            viewModel.progress.value = false
            if (it.contains("Session Expired") || it.contains("Please provide access token") || it.contains("Invalid Access Token")) {
                showToast(it)
                startActivity(Intent(this, LoginScreen::class.java))
            } else {
                showToast(it)
            }
        }
    }

    @Composable
    fun verticalSpacer(value: Int) {
        Spacer(modifier = Modifier.height(value.dp))
    }

    @Composable
    fun horizontalSpacer(value: Int) {
        Spacer(modifier = Modifier.width(value.dp))
    }

    @Composable
    fun textViewSemiBold(text: String,
                         fontSize: Int,
                         color: Int,
                         modifier1: Modifier = Modifier,
                         textAlign1: TextAlign? = null
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
            color = colorResource(color),
            modifier = modifier1,
            textAlign = textAlign1)
    }

    @Composable
    fun textViewMedium(text: String,
                       fontSize: Int,
                       color: Int,
                       modifier1: Modifier = Modifier,
                       textAlign1: TextAlign? = null) {
        Text(text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
            color = colorResource(color),
            modifier = modifier1,
            textAlign = textAlign1)
    }

    @Composable
    fun textViewRegular(text: String, fontSize: Int, color: Int) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
            color = colorResource(color))
    }

    @Composable
    fun imageView(image: Int, modifier1: Modifier = Modifier) {
        Image(painter = painterResource(id = image),
            contentDescription = null,
            modifier = modifier1.clickable {
                SharedPreference.get(this).deliveryId = ""
                SharedPreference.get(this).accessToken = ""
                SharedPreference.get(this).isLogin = false
               // startActivity(Intent(this, LoginScreen::class.java))
            })
    }


    @Composable
    fun imageViewTint(image: Int, tint: Int) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorResource(id = tint)))
    }


    @Composable
    @Preview(showSystemUi = true)
    fun HomeScreenContent() {
        var location by remember { mutableStateOf("") }
        val extras = intent.extras
        val value = extras?.getString("key")

        Log.d("TAG", "onCreate:Locationn ${value}")
        Log.d("TAG", "onCreate:Access ${SharedPreference.get(this).accessToken}")
        Log.d("TAG", "onCreate:Access ID ${SharedPreference.get(this).deliveryId}")

        val dutyResponse = viewModel.getDutyResponse.observeAsState()
        val updatedutyResponse = viewModel.updateDutyResponse.observeAsState()

        if (dutyResponse.value?.success == true) {
            dutyResponse.value?.success = false
            Log.d("TAG", "orderReceivedLayout: Status ${dutyResponse?.value?.data?.status}")
            SharedPreference.get(this).dutyStatus = dutyResponse?.value?.data?.status ?: false
            Log.d("TAG", "HomeScreenContent: Update Status ${SharedPreference.get(this).dutyStatus}")
            if(dutyResponse?.value?.data?.status==true) {
                viewModel.getPendingOrders(SharedPreference.get(this@HomeScreen).deliveryId, SharedPreference.get(this@HomeScreen).accessToken)
            }
        }

        if (updatedutyResponse?.value?.success == true) {
            updatedutyResponse?.value?.success = false
            viewModel.getDutyApi(SharedPreference.get(this@HomeScreen).accessToken)
        }


        LaunchedEffect(key1 = Unit) {
            viewModel.progress.value = true
            viewModel.getDutyApi(SharedPreference.get(this@HomeScreen).accessToken)
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        val onBackPressedDispatcherOwner = lifecycleOwner as? OnBackPressedDispatcherOwner
        val backCallback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitluncher()
                }
            }
        }
        DisposableEffect(onBackPressedDispatcherOwner) {
            onBackPressedDispatcherOwner?.onBackPressedDispatcher?.addCallback(
                lifecycleOwner, backCallback)

            onDispose {
                backCallback.remove()
            }
        }

        var selectedLication = ""
        if (extras != null) {
            selectedLication = value.toString()
        } else {
            requestLocationUpdates() {
                location = it
                Log.d("TAG", "HomeScreenContent: $location")
            }
            selectedLication = location
        }

        viewModel.progress.value = true
        val coordinates = listOf(HomeScreen.lng, HomeScreen.lat)
        val model = UpdateDeliveryAddressRequest(coordinates)
        viewModel.updateDeliveryAddressResponse(
            SharedPreference.get(this@HomeScreen).accessToken, model
        )
        Log.d("TAG", "HomeScreenContent:hgh  ${HomeScreen.lat},${HomeScreen.lng},$model")

        val scroll = rememberScrollState()
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding()) {
            titleLayout(selectedLication)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .heightIn(max = 2000.dp)
                    .padding(horizontal = 20.dp)) {
                cardLayout()
                orderLayout()
                verticalSpacer(value = 30)
                orderReceivedLayout()
            }
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun orderReceivedLayout() {
        val data = viewModel.getPendingOrdersResponse.observeAsState()
        val rejectData = viewModel.rejectOrdersResponse.observeAsState()
        var orderIndex by remember { mutableStateOf(0) }

        if (data?.value?.status == true) {
            data?.value?.status = false
            if (data.value?.data?.any { it.accepted == true } == true) {
                Log.d("TAG", "orderReceivedLayout: accept Order")
               val orderIdAccept = data.value?.data?.filter { it.accepted == true }?.map { it._id }
                SharedPreference.get(this).deliveryParticularId = orderIdAccept?.get(0).toString()
                viewModel.mDeliveryId.value = orderIdAccept?.get(0).toString()
                moveActivity(OrderDetails())
            }
        }

        if (rejectData.value?.status == true) {
            rejectData.value?.status = false
            viewModel.progress.value = true
            viewModel.getPendingOrders(SharedPreference.get(this@HomeScreen).deliveryId, SharedPreference.get(this@HomeScreen).accessToken)
        }

        var selectedOrderType = remember { mutableStateOf("Change of Mind") }
        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        var label by remember { mutableStateOf("") }
        val orderTypes = listOf(
            "Change of Mind",
            "Incorrect Delivery Address",
            "Payment Issues",
            "Restuarant Closure",
            "Delivery Restrictions",
            "High Volume",
            "Other")

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                dragHandle = {},
                sheetState = sheetState,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .background(
                            Color.White, shape = RoundedCornerShape(
                                topStart = 19.dp, topEnd = 19.dp
                            )
                        )) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(
                                    color = Color(0xFFF7F7F7)
                                )) {
                            Box(
                                modifier = Modifier
                                    .align(
                                        Alignment.TopStart
                                    )
                                    .padding(
                                        start = 16.dp, top = 16.dp
                                    )
                                    .size(18.dp)) {

                                Image(
                                    painter = painterResource(
                                        id = R.drawable.back_ic
                                    ),
                                    contentDescription = stringResource(R.string.back_button),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            showBottomSheet = false
                                        },
                                )
                            }

                            Text(
                                text = stringResource(R.string.cancel_reason),
                                fontSize = 14.sp,
                                color = Color(0xFF333333),
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(
                                    Font(R.font.montserrat_semibold)),
                                modifier = Modifier
                                    .align(
                                        Alignment.Center
                                    )
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp))
                        }

                        Column(
                            modifier = Modifier.padding(
                                16.dp)) {

                            orderTypes.forEach { orderType ->
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedOrderType.value = orderType
                                        }
                                        .padding(vertical = 4.dp)) {
                                    Image(
                                        painter = painterResource(
                                            id = if (selectedOrderType.value == orderType) R.drawable.radio_button_active
                                            else R.drawable.radio_button),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp))

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = orderType,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                        color = Color(0xFF333333))
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }


                        if (selectedOrderType.value == "Other") {
                            TextField(
                                value = label,
                                onValueChange = { label = it },
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp)
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.enter_reason),
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                        fontSize = 12.sp,
                                        color = colorResource(id = R.color.gray_9D9D9D),
                                    )
                                },

                                textStyle = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.black_333333)),

                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF6F6F6),
                                    unfocusedContainerColor = Color(0xFFF6F6F6),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                showCancelOrder(
                                    this@HomeScreen,
                                    viewModel,
                                    selectedOrderType.value,
                                    data?.value?.data?.get(orderIndex)?._id.toString() ?: "") {
                                    selectedOrderType.value = "Change of Mind"
                                }
                                showBottomSheet = false
                            },
                            modifier = Modifier
                                .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0XFF33BD8C)),

                            contentPadding = PaddingValues(10.dp)) {
                            Text(
                                text = stringResource(R.string.submit),
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 5.dp),
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_medium)))
                        }
                    }
                }
            }
        }

        LaunchedEffect(key1 = Unit) {
            viewModel.progress.value = true
         //   viewModel.getPendingOrders(SharedPreference.get(this@HomeScreen).deliveryId, SharedPreference.get(this@HomeScreen).accessToken)
            viewModel.getDashboardData(SharedPreference.get(this@HomeScreen).accessToken, SharedPreference.get(this@HomeScreen).deliveryId.toString())
        }

        if (SharedPreference.get(this).dutyStatus == true) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(data.value?.data?.size ?: 0) { index ->
                    val orderList = data.value?.data?.get(index)
                    Log.d("TAG", "orderReceivedLayout: ID ${SharedPreference.get(this@HomeScreen).deliveryId},${orderList?._id}")

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White, contentColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                            Column(
                                modifier = Modifier.fillMaxWidth()) {
                                textViewMedium(
                                    text = stringResource(R.string.you_received_new_request),
                                    fontSize = 14,
                                    color = R.color.white,
                                    modifier1 = Modifier
                                        .fillMaxWidth()
                                        .background(colorResource(id = R.color.green))
                                        .padding(14.dp),
                                    textAlign1 = TextAlign.Center
                                )
                                Row(modifier = Modifier.padding(12.dp)) {

                                    if(orderList?.restId?.addressDetails?.resLogo==null){
                                        AsyncImage(
                                            model = if (orderList?.restId?.addressDetails?.supLogo.isNullOrEmpty()) R.drawable.logo_symbol else orderList?.restId?.addressDetails?.supLogo,
                                            placeholder = painterResource(
                                                id = R.drawable.logo_login),
                                            contentDescription = "",
                                            contentScale = ContentScale.FillBounds,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(shape = RoundedCornerShape(12.dp)),
                                        )

                                    }else{
                                        AsyncImage(
                                            model = if (orderList?.restId?.addressDetails?.resLogo.isNullOrEmpty()) R.drawable.logo_symbol else orderList?.restId?.addressDetails?.resLogo,
                                            placeholder = painterResource(
                                                id = R.drawable.logo_login),
                                            contentDescription = "",
                                            contentScale = ContentScale.FillBounds,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(shape = RoundedCornerShape(12.dp)),
                                        )

                                    }


                                    horizontalSpacer(value = 10)
                                    Column {
                                        textViewRegular(
                                            text = stringResource(R.string.food_order),
                                            fontSize = 12,
                                            color = R.color.gray_6B6B6B)
                                        verticalSpacer(value = 8)

                                        if(orderList?.restId?.resName==null){
                                            textViewSemiBold(
                                                text = orderList?.restId?.name ?: "",
                                                fontSize = 14,
                                                color = R.color.black_333333)
                                        }else{
                                            textViewSemiBold(
                                                text = orderList?.restId?.resName ?: "",
                                                fontSize = 14,
                                                color = R.color.black_333333)
                                        }
                                        verticalSpacer(value = 4)
                                        Row {
                                            imageView(image = R.drawable.baseline_location_on_24)
                                            horizontalSpacer(value = 5)
                                            textViewRegular(
                                                text = orderList?.restId?.addressDetails?.address
                                                    ?: "",
                                                fontSize = 12,
                                                color = R.color.gray_6B6B6B)
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    textViewSemiBold(text = stringResource(R.string.accept),
                                        fontSize = 12,
                                        color = R.color.green_39C166,
                                        textAlign1 = TextAlign.Center,
                                        modifier1 = Modifier
                                            .weight(1f)
                                            .background(colorResource(id = R.color.green_C8EED4))
                                            .padding(15.dp)
                                            .clickable {
                                                viewModel.progress.value = true
                                                val model =
                                                    AcceptRequest(SharedPreference.get(this@HomeScreen).deliveryId)
                                                viewModel.acceptOrders(
                                                    orderList?._id?.toString() ?: "",
                                                    SharedPreference.get(this@HomeScreen).accessToken,
                                                    model
                                                )
                                            })
                                    textViewSemiBold(text = stringResource(R.string.reject),
                                        fontSize = 12,
                                        color = R.color.red_D63636,
                                        textAlign1 = TextAlign.Center,
                                        modifier1 = Modifier
                                            .clickable {
                                                orderIndex = index
                                                showBottomSheet = true
                                            }
                                            .weight(1f)
                                            .background(colorResource(id = R.color.red_F8DFDF))
                                            .padding(15.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Composable
    private fun orderLayout() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)) {
            textViewSemiBold(
                text = stringResource(R.string.your_today_s_jobs_earnings),
                fontSize = 14,
                color = R.color.black_333333
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                totalOrderLayout()
                totalEarnLayout()

            }

        }

    }

    @Composable
    private fun totalOrderLayout() {
        val dashboardDataResponse by viewModel.getDashboardDataResponse.observeAsState()

        Column(modifier = Modifier
            .width(170.dp)
            .height(135.dp)
            .padding(top = 8.dp)
            .background(
                shape = RoundedCornerShape(14.dp),
                color = colorResource(id = R.color.sky_color)
            )
            .padding(16.dp)) {

            val text = "${dashboardDataResponse?.data?.totalOrders?.toString() ?: "0"}"
            val fontSize = if (text.length > 3) 16.sp else 36.sp
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    fontSize = fontSize,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    color = colorResource(R.color.black_333333),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start)
                imageView(image = R.drawable.jbs_ern_orders)
            }
            verticalSpacer(value = 10)
            textViewSemiBold(text = stringResource(R.string.total_orders),
                fontSize = 12, color = R.color.black_333333)
            verticalSpacer(value = 8)
            Row(
                verticalAlignment = Alignment.CenterVertically) {
                textViewSemiBold(text = stringResource(R.string.view_history), fontSize = 11, color = R.color.blue_003)
                horizontalSpacer(value = 2)
                imageView(image = R.drawable.baseline_arrow_forward_ios_24)
            }


        }
    }


    @Composable
    private fun totalEarnLayout() {
        val dashboardDataResponse by viewModel.getDashboardDataResponse.observeAsState()

        Column(
            modifier = Modifier
                .width(170.dp)
                .height(135.dp)
                .padding(top = 8.dp)
                .background(
                    shape = RoundedCornerShape(14.dp),
                    color = colorResource(id = R.color.yellow_light)
                )
                .padding(16.dp)) {

            val text = "${dashboardDataResponse?.data?.totalEarnings?.toString() ?: "0"}"
            val fontSize = if (text.length > 3) 16.sp else 36.sp

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = text,
                    fontSize = fontSize,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    color = colorResource(R.color.black_333333),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start)
                imageView(image = R.drawable.jbs_ern_earned)
            }
            verticalSpacer(value = 10)
            textViewSemiBold(
                text = stringResource(R.string.total_earned_in_kd),
                fontSize = 12,
                color = R.color.black_333333)
            verticalSpacer(value = 8)
            Row(
                verticalAlignment = Alignment.CenterVertically) {
                textViewSemiBold(text =stringResource(R.string.view_history), fontSize = 11, color = R.color.yellow)
                horizontalSpacer(value = 2)
                imageViewTint(image = R.drawable.baseline_arrow_forward_ios_24, R.color.yellow)
            }


        }
    }


    @Composable
    fun cardLayout() {
        val data by viewModel.getProfileDetailsResponse.observeAsState()
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White, contentColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -10.dp)
                .padding(top = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,)

            {

                AsyncImage(
                    model = SharedPreference.get(this@HomeScreen).profileImage,
                    placeholder = painterResource(
                        id = R.drawable.logo_login),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = Color.Gray, shape = CircleShape)
                        .size(80.dp),
                    contentScale = ContentScale.Crop,
                )

                verticalSpacer(value = 8)
                textViewMedium(data?.data?.name?:"", 16, R.color.black_05)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    textViewMedium("Duty", 12, R.color.black_05)

                   ImageToggleSwitch()
                   // ToggleSwitch()
                }

            }
        }
    }

    @Composable
    fun ImageToggleSwitch() {
        Log.d("TAG", "ToggleSwitch: ${SharedPreference.get(this).dutyStatus}")
        var isChecked by remember { mutableStateOf(SharedPreference.get(this).dutyStatus) }

        Log.d("TAG", "ToggleSwitch:isCheck ${isChecked}")

        val toggleImage = if (SharedPreference.get(this).dutyStatus) {
            R.drawable.toggle_on
        } else {
            R.drawable.toggle_off
        }

        Image(
            painter = painterResource(id = toggleImage),
            contentDescription = if (isChecked) "Checked" else "Unchecked",
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp)
                .clickable {
                    isChecked = !isChecked
                    viewModel.progress.value = true
                    viewModel.updateDutyApi(
                        SharedPreference.get(this).deliveryId,
                        isChecked,
                        SharedPreference.get(this).accessToken
                    )
                    viewModel.getPendingOrders(
                        SharedPreference.get(this).deliveryId,
                        SharedPreference.get(this).accessToken
                    )
                    /* if (isChecked) {
                        viewModel.getPendingOrders(
                            SharedPreference.get(this).deliveryId,
                            SharedPreference.get(this).accessToken)
                    }*/
                }
        )
    }


    @Composable
    fun ToggleSwitch() {
        Log.d("TAG", "ToggleSwitch: ${SharedPreference.get(this).dutyStatus}")
        var isChecked by remember { mutableStateOf(SharedPreference.get(this).dutyStatus) }
        Switch(
            modifier = Modifier.scale(0.6f),
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                viewModel.progress.value = true
                viewModel.updateDutyApi(SharedPreference.get(this).deliveryId, isChecked, SharedPreference.get(this).accessToken)
                if (isChecked == true) {
                    viewModel.getPendingOrders(SharedPreference.get(this).deliveryId, SharedPreference.get(this).accessToken)
                }
            },

            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(id = R.color.thumb_color),
                checkedTrackColor = colorResource(id = R.color.thumb_bg),
                uncheckedThumbColor = Color(0xFFBDBDBD),
                uncheckedTrackColor = Color(0xFFE0E0E0)),
        )
    }

    @Composable
    private fun titleLayout(location: String) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .zIndex(10f)
            .background(Color.White)
            .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.lateral_menu),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        startActivity(Intent(this@HomeScreen,SideMenuScreen::class.java))
                    })
                horizontalSpacer(12)
                imageView(R.drawable.location)
                horizontalSpacer(5)
                Text(
                    text = location,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                    color = colorResource(R.color.black_333333),
                    modifier = Modifier.clickable {
                        val intent = Intent(this@HomeScreen, LocationSearchActivity::class.java)
                        startActivity(intent)
                    },
                    textAlign = TextAlign.Start)

                // textViewMedium(location, 12, R.color.black_333333)
                horizontalSpacer(5)
                imageView(image = R.drawable.arrow_drop_down)
            }
            imageView(image = R.drawable.notificatons)
        }
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White, contentColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -10.dp)
                .rotate(180f)
                .padding(top = 10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            textViewMedium("", 12, R.color.white)
            textViewMedium("", 12, R.color.white)

        }
    }


    @Composable
    fun requestLocationUpdates(onFetch: (String) -> Unit) {
        val context = LocalContext.current
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                val address = getAddress(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0,
                    context as Activity).toString()
                onFetch(address)/*  viewModel.lat.value = location?.latitude ?: 0.0
            viewModel.lng.value = location?.longitude ?: 0.0*/
                lat = location?.latitude ?: 0.0
                lng = location?.longitude ?: 0.0

                HomeScreen.lat = lat
                HomeScreen.lng = lng
                Log.d("TAG", "Location updated: ${location?.latitude}, ${location?.longitude}")

            }
        }

        if (isGPSEnabled(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), 111)
            } else {
                // Request location updates
                fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.getMainLooper())
            }
        } else {
            showGPSDialog(context)
        }
    }


    private fun exitluncher() {
        if (System.currentTimeMillis() - pressedTime > 2000) {
            showToast(getString(R.string.press_again_to_exit_the_app))
            pressedTime = System.currentTimeMillis()
            return
        } else moveTaskToBack(true)
    }

}

@SuppressLint("MissingInflatedId")
fun showCancelOrder(context: Context,
                    viewModel: M1ViewModel,
                    selectedReson: String,
                    selecetId: String,
                    noClick: (String) -> Unit
) {
    val dialog = android.app.AlertDialog.Builder(context).create() // Use the passed context
    val v = LayoutInflater.from(context).inflate(R.layout.logout_popup, null)

    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.apply {
        setView(v)
        setCancelable(false)
        val btnCancel = v.findViewById<AppCompatButton>(R.id.cancel)
        val btnAccept = v.findViewById<AppCompatButton>(R.id.accept)
        val heading = v.findViewById<TextView>(R.id.timeoutHeading)
        val image = v.findViewById<ImageView>(R.id.textView3)
        image.setImageDrawable(context.getDrawable(R.drawable.order_cance))
        heading.setText(context.getString(R.string.are_you_sure_you_want_to_cancel_the_order))

        btnCancel.setOnClickListener {
            noClick("")
            dialog.dismiss()
        }

        btnAccept.setOnClickListener {
            val activityContext = context as? Activity
            activityContext?.let {
                viewModel.progress.value = true
                val model = RejectRequest(SharedPreference.get(context).deliveryId, selectedReson)
                viewModel.rejectOrders(selecetId, SharedPreference.get(context).accessToken, model)
                dialog.dismiss()
            }
        }
    }.show()
}

fun showGPSDialog(context: Context?) {
    val binding = GpsDialogBinding.inflate(LayoutInflater.from(context))
    val mBuilder = AlertDialog.Builder(context).setView(binding.root).create()
    mBuilder.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    mBuilder.setCancelable(false)
    mBuilder.show()
    binding.mCancel.setOnClickListener {
        mBuilder.dismiss()
    }
    binding.mSetting.setOnClickListener {
        mBuilder.dismiss()
        context?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}



