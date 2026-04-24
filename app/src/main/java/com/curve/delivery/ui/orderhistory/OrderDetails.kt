package com.curve.delivery.ui.orderhistory

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.curve.delivery.R
import com.curve.delivery.databinding.GpsDialogBinding
import com.curve.delivery.databinding.LayoutOrdereredDeliveredBinding
import com.curve.delivery.new_architecture.helper.CustomLoader
import com.curve.delivery.response.GetOrdersDetailsResponse
import com.curve.delivery.ui.chatscreenss.ChatScreenS
import com.curve.delivery.ui.commonscreen.horizontalSpacer
import com.curve.delivery.ui.commonscreen.textViewMedium
import com.curve.delivery.ui.commonscreen.verticalSpacer
import com.curve.delivery.ui.home.HomeScreen
import com.curve.delivery.ui.home.HomeScreen.Companion.lat
import com.curve.delivery.ui.home.HomeScreen.Companion.lng
import com.curve.delivery.ui.login.LoginScreen
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderDetails : ComponentActivity() {
    private val viewModel: M1ViewModel by viewModels()
    private var pressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer()
        setContent {
            OrderDetailSection()
        }
    }

    fun observer() {
        viewModel.progress.observe(this) {
            if (viewModel.progress.value == true) {
                CustomLoader.showLoader(this)
            } else {
                CustomLoader.hideLoader()
            }
        }

        viewModel.ordersPickupResponse.observe(this) {
            SharedPreference.get(this).orderStatus = it.data.isPickUp ?: false
            SharedPreference.get(this).delivererdStatus = it.data.isDelivered ?: false
        }

        viewModel.ordersDeliveredResponse.observe(this) {
            Log.d("TAG", "observer: Delivered")
            SharedPreference.get(this).delivererdStatus = it.data.isDelivered ?: false
            showOrderDeliveredAddress(this)
        }
        viewModel.mError.observe(this) {
            viewModel.progress.value = false
            showToast(it)
        }
    }

    @Composable
    fun OrderDetailSection() {
        val scroll = rememberScrollState()
        val orderDetailsData = viewModel.getOrdersDetailsResponse.observeAsState()
        val ordereredDeliverede = viewModel.ordersDeliveredResponse.observeAsState()
        val ordereredpickedUp = viewModel.ordersPickupResponse.observeAsState()
        var location by remember { mutableStateOf("") }

        requestLocationUpdates() {
            location = it
            Log.d("TAG", "HomeScreenContent: $location")
        }

        val backCallback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitluncher()
                }
            }
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        val onBackPressedDispatcherOwner = lifecycleOwner as? OnBackPressedDispatcherOwner
        DisposableEffect(onBackPressedDispatcherOwner) {
            onBackPressedDispatcherOwner?.onBackPressedDispatcher?.addCallback(
                lifecycleOwner,
                backCallback
            )

            onDispose {
                backCallback.remove()
            }
        }

        Log.d("TAG", "OrderDetailSection:Details  $orderDetailsData")

        if (orderDetailsData.value?.status == true) {
            orderDetailsData.value?.status = false
            SharedPreference.get(this).orderStatus = orderDetailsData.value?.data?.isPickUp ?: false
            SharedPreference.get(this).delivererdStatus = orderDetailsData.value?.data?.isDelivered ?: false
         //   viewModel.getOrdersDetails(SharedPreference.get(this@OrderDetails).deliveryParticularId.toString(), SharedPreference.get(this@OrderDetails).accessToken)
        }

        if(ordereredpickedUp.value?.status==true){
            Log.d("TAG", "OrderDetailSection: hghg")
            ordereredpickedUp.value?.status=false
            viewModel.getOrdersDetails(SharedPreference.get(this@OrderDetails).deliveryParticularId.toString(), SharedPreference.get(this@OrderDetails).accessToken)
        }

        if (ordereredDeliverede.value?.status == true) {
            ordereredDeliverede.value?.status = false
            showOrderDeliveredAddress(this)
        }

        LaunchedEffect(key1 = Unit) {
            viewModel.progress.value = true
            viewModel.getOrdersDetails(SharedPreference.get(this@OrderDetails).deliveryParticularId.toString(),
                SharedPreference.get(this@OrderDetails).accessToken)
        }

        SharedPreference.get(this).orderStatus = orderDetailsData.value?.data?.isPickUp ?: false
        Log.d("TAG", "OrderDetailSection: $orderDetailsData")

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding()) {
            titleLayout(location)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .heightIn(max = 700.dp)
                    .padding())
            {

                verticalSpacer(value = 10)
                Text(
                    text = stringResource(R.string.ongoing_order),
                    modifier = Modifier.padding(start = 20.dp),
                    fontSize = 20.sp,
                    color = Color(0xFF33BD8C),
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
                verticalSpacer(value = 16)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(color = Color(0xFFE8F8F2))) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.order_id),
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                Font(R.font.montserrat_bold)))
                        Text(
                            text ="#${orderDetailsData.value?.data?.orderId?.orderId.toString()}"
                                ?: "0",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                Font(R.font.montserrat_bold)))
                    }
                }

                verticalSpacer(value = 10)
                OrderDetailsScreen11(orderDetailsData)
            }
        }
    }

    @Composable
    fun OrderDetailsScreen11(orderDetailsData: State<GetOrdersDetailsResponse?>) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            if(orderDetailsData?.value?.data?.restId?.resName==null){
                OrderSection(
                    title = "Order Pickup From",
                    name = orderDetailsData.value?.data?.restId?.name ?: "",
                    address = orderDetailsData.value?.data?.restId?.addressDetails?.address ?: "",
                    image =  if(orderDetailsData?.value?.data?.restId?.addressDetails?.resLogo.isNullOrEmpty())orderDetailsData?.value?.data?.restId?.addressDetails?.supLogo?:"" else orderDetailsData?.value?.data?.restId?.addressDetails?.resLogo
                        ?: "")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color(0xFF9D9D9D)))
            Spacer(modifier = Modifier.height(16.dp))

            if (!SharedPreference.get(this@OrderDetails).orderStatus) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White, contentColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center, // Center content horizontally
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                NavigateTrack.navigateData = orderDetailsData.value?.data
                                val intent = Intent(this@OrderDetails, NavigateTrack::class.java)
                                startActivity(intent)
                            }
                            .padding(horizontal = 16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ord_navigate),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.navigate),
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                            color = Color(0xFFFE934C))
                    }
                }
            }

            if (SharedPreference.get(this@OrderDetails).orderStatus) {
                var image = ""
                if (orderDetailsData?.value?.data?.userData != null) {
                    image = orderDetailsData?.value?.data?.userData?.profileImage ?: ""
                }

                OrderSection(
                    title = "Order Deliver To",
                    name = orderDetailsData?.value?.data?.userData?.fullName ?: "",
                    address = orderDetailsData?.value?.data?.orderId?.addressId?.address ?: "",
                    image = image)

                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White, contentColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                NavigateTrack.navigateData = orderDetailsData.value?.data
                                val intent = Intent(this@OrderDetails, NavigateTrack::class.java)
                                startActivity(intent)
                            }
                            .padding(horizontal = 16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ord_navigate),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.navigate),
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                            color = Color(0xFFFE934C))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.weight(1f)) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White, contentColor = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .padding(top = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    val intent = Intent(this@OrderDetails, ChatScreenS::class.java)
                                    intent.putExtra(
                                        "orderID",
                                        orderDetailsData?.value?.data?.orderId?._id.toString()
                                    )
                                    intent.putExtra(
                                        "userId",
                                        orderDetailsData?.value?.data?.orderId?.userId.toString()
                                    )

                                    if (!orderDetailsData?.value?.data?.restId?.profileImage.isNullOrEmpty()) {
                                        intent.putExtra(
                                            "receiverImage",
                                            orderDetailsData?.value?.data?.restId?.profileImage.toString()
                                        )
                                    }
                                    startActivity(intent)
                                }
                                .padding(horizontal = 16.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.ord_navigate),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.chat),
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                color = Color(0xFFFE934C))
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White, contentColor = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .padding(top = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center, // Center content horizontally
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data =
                                            Uri.parse("tel:${orderDetailsData.value?.data?.userData?.mobileNumber}")
                                    }
                                    startActivity(intent)
                                }
                                .padding(horizontal = 16.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.ord_navigate),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp)) // Space between image and text
                            Text(
                                text = stringResource(R.string.call),
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                color = Color(0xFFFE934C))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.green)),
                contentPadding = PaddingValues(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (SharedPreference.get(this@OrderDetails).orderStatus) {
                                Log.d(
                                    "TAG",
                                    "OrderDetailsScreen11: ${SharedPreference.get(this@OrderDetails).orderStatus}"
                                )
                                viewModel.progress.value = true
                                viewModel.getOrdersDelivered(
                                    SharedPreference.get(this@OrderDetails).deliveryParticularId,
                                    SharedPreference.get(this@OrderDetails).deliveryId,
                                    SharedPreference.get(this@OrderDetails).accessToken
                                )
                            } else {
                                Log.d(
                                    "TAG",
                                    "OrderDetailsScreen11 Nooo: ${SharedPreference.get(this@OrderDetails).orderStatus}"
                                )
                                viewModel.progress.value = true
                                viewModel.getOrdersPickup(
                                    SharedPreference.get(this@OrderDetails).deliveryParticularId,
                                    SharedPreference.get(this@OrderDetails).deliveryId,
                                    SharedPreference.get(this@OrderDetails).accessToken
                                )
                            }
                        }) {

                    var orderStatus = ""
                    if (SharedPreference.get(this@OrderDetails).orderStatus) {
                        orderStatus = "Mark as Delivered"
                    } else {
                        orderStatus = "Order Pickup"
                    }

                    Text(
                        text = orderStatus,
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium))),
                        modifier = Modifier.padding(end = 8.dp))
                    Spacer(modifier = Modifier.width(32.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_triple),
                        contentDescription = "Order Placed Icon",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp))
                }
            }

        }
    }

    @Composable
    fun OrderSection(title: String, name: String, address: String, image: String) {
        Column {
            Text(text = title,
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
                            .width(75.dp)
                            .height(59.dp),
                        contentScale = ContentScale.FillBounds,
                    )
                }
            }
        }
    }

    @Composable
    fun IconButtonWithText(icon: ImageVector, text: String) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .clickable {

            }
            .padding(8.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFFFFA726))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_regular)))
        }
    }

    @Composable
    private fun titleLayout(location: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(10f)
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                imageView(R.drawable.lateral_menu)
                horizontalSpacer(12)
                imageView(R.drawable.location)
                horizontalSpacer(5)
                textViewMedium(location, 12, R.color.black_333333)
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

    fun showOrderDeliveredAddress(context: Context) {
        val binding = LayoutOrdereredDeliveredBinding.inflate(LayoutInflater.from(context))
        val mBuilder = AlertDialog.Builder(context).setView(binding.root).create()
        mBuilder.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        mBuilder.setCancelable(false)
        mBuilder.show()
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            mBuilder.dismiss()
            moveActivity(HomeScreen())
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
                    context as Activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION),
                    111)
            } else {
                // Request location updates
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper())
            }
        } else {
            com.curve.delivery.ui.home.showGPSDialog(context)
        }
    }


    private fun exitluncher() {
        if (System.currentTimeMillis() - pressedTime > 2000) {
            showToast(getString(R.string.press_again_to_exit_the_app))
            pressedTime = System.currentTimeMillis()
            return
        } else {
            moveTaskToBack(true)
        }
    }

    @Composable
    fun imageView(image: Int, modifier1: Modifier = Modifier) {
        Image(painter = painterResource(id = image),
            contentDescription = null,
            modifier = modifier1.clickable {
               // SharedPreference.get(this).deliveryId=""
               // startActivity(Intent(this, LoginScreen::class.java))
            })
    }

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

