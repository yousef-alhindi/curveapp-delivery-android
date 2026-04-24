package com.curve.delivery.ui.orderhistory

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.curve.delivery.R
import com.curve.delivery.response.GetOrdersDetailsResponse
import com.curve.delivery.ui.home.showCancelOrder
import com.curve.delivery.viewModel.M1ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

class NavigateTrack:ComponentActivity() {
    private val viewModel: M1ViewModel by viewModels()
    private var order1 =""
    private var order2 =""
    private var order3 =""
    private var order4 =""
    companion object{
        var navigateData: GetOrdersDetailsResponse.Data?=null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigateMapTrackOrder(activity = this, viewModel = viewModel)
        }
    }


    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun NavigateMapTrackOrder(activity: Activity, viewModel: M1ViewModel) {
        Log.d("TAG", "NavigateMapTrackOrder: $navigateData")
        Log.d("TAG", "NavigateMapTrackOrder:Track ${navigateData?.restId?.location?.coordinates?.get(1)}")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            @Suppress("DEPRECATION") activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.hhhh)
        val directionsResponse by viewModel.directionMapApiResponse.observeAsState()
        LaunchedEffect(key1 =Unit) {
           //  viewModel.directionMapApi("28.5708,77.3211","28.4089,77.3178","AIzaSyB7d1vfxWa_ZYhWBFIZvQzFpSlq5aR9-E0")
           /* viewModel.directionMapApi(  "${navigateData?.restId?.location?.coordinates?.get(1)},${navigateData?.restId?.location?.coordinates?.get(0)}",
                "${navigateData?.orderId?.addressId?.location?.coordinates?.get(1)},${navigateData?.orderId?.addressId?.location?.coordinates?.get(0)}",
                "AIzaSyB7d1vfxWa_ZYhWBFIZvQzFpSlq5aR9")*/

            viewModel.directionMapApi("${navigateData?.restId?.location?.coordinates?.get(1)},${navigateData?.restId?.location?.coordinates?.get(0)}",
                "${navigateData?.orderId?.addressId?.location?.coordinates?.get(1)},${navigateData?.orderId?.addressId?.location?.coordinates?.get(0)}",
                "AIzaSyBz-rTWg0OHg5Xb4OBdqn93G8kiVwfllWI")
        }

        Surface(modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF7F7F7))) {

            BackHandler {
               finish()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFF7F7F7))) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {

                        Image(painter = painterResource(id = R.drawable.back_ic),
                            contentDescription = "Back Button",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    finish()
                                })

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center) {
                            Text(
                                text = stringResource(R.string.track_order),
                                color = Color(0xFF333333),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                style = TextStyle(fontFamily = FontFamily(Font(R.font.montserrat_semibold))))
                        }
                    }
                }

                val  origin = LatLng(37.4219983, -122.084)
                val   destination = LatLng(37.4274761, -122.169719)

                Column(
                    modifier = Modifier
                        .background(color = Color.White)
                        .fillMaxWidth()
                        .height(500.dp)) {
                    val cameraPositionState = rememberCameraPositionState()
                    val permissionState = rememberMultiplePermissionsState(
                        permissions = listOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION))


                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapLoaded = {
                            // Map loaded callback
                        }
                    ) {
                        if (!directionsResponse?.routes.isNullOrEmpty()){
                            directionsResponse?.let {
                            val path = mutableListOf<LatLng>()
                            val origin = LatLng(
                                it.routes?.get(0)?.legs?.get(0)?.start_location?.lat ?: 0.0,
                                it.routes?.get(0)?.legs?.get(0)?.start_location?.lng ?: 0.0)
                            val destination = LatLng(
                                it.routes?.get(0)?.legs?.get(0)?.end_location?.lat ?: 0.0,
                                it.routes?.get(0)?.legs?.get(0)?.end_location?.lng ?: 0.0)

                            for (step in it.routes?.get(0)?.legs?.get(0)?.steps!!) {
                                path.add(LatLng(step.start_location.lat, step.start_location.lng))
                                path.add(LatLng(step.end_location.lat, step.end_location.lng))
                            }

                            Polyline(points = path, color = Color(0xFF33BD8C), width = 10f)
                            val originalBitmap = BitmapFactory.decodeResource(
                                activity.resources,
                                R.drawable.location_pin_resto)
                            val originalBitmapDes = BitmapFactory.decodeResource(
                                activity.resources,
                                R.drawable.location_delivery_boy)
                            val markerOrigin =
                                BitmapDescriptorFactory.fromBitmap(originalBitmap.resize(100, 100))
                            val markerDestination =
                                BitmapDescriptorFactory.fromBitmap(originalBitmapDes.resize(60, 60))

                            Marker(
                                state = MarkerState(
                                    position = origin), title = "Origin", icon = markerOrigin)

                            Marker(
                                state = MarkerState(
                                    position = destination),
                                title = "Destination",
                                icon = markerDestination)

                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(origin, 14f)
                        }
                    }
                    }

                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFF7F7F7),
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        ),
                    verticalArrangement = Arrangement.Center
                ) {
                    // First Row: Order Information
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.you_are_navigating_to),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFF051117),
                                fontFamily = FontFamily(Font(R.font.montserrat_semibold))
                            )
                        )
                        Text(
                            text = "",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = Color(0xFF051117),
                                fontFamily = FontFamily(Font(R.font.montserrat_semibold))
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                /*    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp)
                            .background(color = Color.White), colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)) {*/
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(start = 16.dp, end = 16.dp, top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ord_location_navigation),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        color = Color.White, shape = RoundedCornerShape(12.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Column(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = navigateData?.restId?.resName?:"",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color(0xFF051117),
                                        fontFamily = FontFamily(Font(R.font.montserrat_semibold))
                                    )
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = navigateData?.restId?.addressDetails?.address?:"",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color(0xFF051117),
                                        fontFamily = FontFamily(Font(R.font.montserrat_semibold))
                                    ))

                            }
                        }

                   // }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            finish()
                        },
                        modifier = Modifier
                            .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0XFF33BD8C)),

                        contentPadding = PaddingValues(10.dp)) {
                        Text(
                            text = stringResource(R.string.exit),
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 5.dp),
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium)))
                    }
                }
            }
        }
    }

    fun Bitmap.resize(newWidth: Int, newHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
    }

}