package com.curve.delivery.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.curve.delivery.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class CurrentLocationActivity : ComponentActivity(),OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)
            Box(modifier = Modifier
                .fillMaxSize()
                .zIndex(2f), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = R.drawable.location), contentDescription = "", modifier = Modifier.size(50.dp))
            }
            MapScreen()
        }
    }

    override fun onMapReady(p0: GoogleMap) {

    }


    @Preview
    @Composable
    fun MapScreen() {
        var myLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
        var currentLat by remember { mutableStateOf(LatLng(0.0, 0.0)) }
        var currentAddress by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                (myLatLng), 10f
            )
        }
        LaunchedEffect(Unit) {
            if (ActivityCompat.checkSelfPermission(
                    this@CurrentLocationActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        // Update current location and move camera
                       myLatLng=LatLng(it.latitude,it.longitude)
                       currentLat=LatLng(it.latitude,it.longitude)
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(currentLat, 18f)

                    }
                }
            }
        }


        LaunchedEffect(cameraPositionState.isMoving) {
            snapshotFlow { cameraPositionState.position.target }
                .collect { newPosition ->
                    myLatLng = newPosition
                    if(getAddressFromLatLng(this@CurrentLocationActivity,myLatLng.latitude,myLatLng.longitude).toString().contains("city")) {
                        currentAddress = getAddressFromLatLng(
                            this@CurrentLocationActivity,
                            myLatLng.latitude,
                            myLatLng.longitude
                        ).split("city")[1]
                        city = getAddressFromLatLng(
                            this@CurrentLocationActivity,
                            myLatLng.latitude,
                            myLatLng.longitude
                        ).split("city")[0]
                    }
                    // Show a toast with the new position

                }
        }
        val context = LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {

           /* CommonBackButton22(
                title = "Location",
                context = context as Activity,
                R.drawable.back_btn,
                R.font.montserrat_semibold,
                R.color.text_black
            )*/
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2f)
            ) {
                Box (modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFFF7F7F7))){

                        Image(
                            painter = painterResource(id = R.drawable.back_ic),
                            contentDescription = "",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .clickable { this@CurrentLocationActivity.finish() }
                                .padding(start = 16.dp) // Add padding for better touch targets
                        )


                   Text(
                        text = stringResource(R.string.location),
                        color = colorResource(id = R.color.black),
                        fontSize = 14.sp,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.montserrat_semibold))),
                        modifier = Modifier
                            .align(Alignment.Center)

                            .fillMaxWidth()
                            .wrapContentHeight()

                    )

                // Placeholder for symmetry

}
                TabRowDefaults.Divider(
                    color = Color(0xFFF7F7F7), // Set a shadow color
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState

            ) {

            }

            val context = LocalContext.current
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier

                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                                .clickable {
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(currentLat, 18f)
                                }
                                .background(
                                    color = Color.White, shape = RoundedCornerShape(
                                        10.dp
                                    )
                                )
                                .padding(15.dp)
                                .zIndex(2f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ord_location_navigation),
                                contentDescription = ""
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()

                            .background(Color.White),
                        shape = RoundedCornerShape(20.dp), // Rounded corners for the card
                        elevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.your_location),
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 4.dp),
                                fontFamily = FontFamily(Font((R.font.montserrat_bold)))
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ord_location_navigation),
                                    contentDescription = "",
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                                Text(
                                    text = city,
                                    fontSize = 14.sp,
                                    color = Color(0XFF333333),
                                    fontFamily = FontFamily(Font(R.font.montserrat_semibold))
                                )
                                
                                Spacer(modifier = Modifier.weight(1f))

//                Text(
//                    text = "Change",
//                    color = Color.Black,
//                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
//                    fontSize = 12.sp,
//                    modifier = Modifier.background(color = Color(0xffEEEEEE), shape = CircleShape)
//                        .padding(vertical = 12.dp, horizontal = 20.dp)
//                )
                            }
                            Text(
                                text = currentAddress,
                                fontSize = 12.sp,
                                color = Color(0XFF9D9D9D),
                                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                            )


                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    HomeScreen.lat = myLatLng.latitude
                                    HomeScreen.lng = myLatLng.longitude
                                    val intent = Intent(context, HomeScreen::class.java)
                                    intent.putExtra("key",currentAddress)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(
                                        0xFF33BD8C))) {
                                Text(
                                    stringResource(R.string.confirm_location),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.montserrat_medium))
                                )
                            }
                        }
                    }
                }
            }
        }
    }


/*    @Composable
    fun BottomLocationCard(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Card(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White),
            shape = RoundedCornerShape(20.dp), // Rounded corners for the card
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Your Location",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp),
                    fontFamily = FontFamily(Font((R.font.montserrat_bold)))
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.location_search_pin),
                        contentDescription = "",
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(
                        text = "XYZ Street, Dubai",
                        fontSize = 14.sp,
                        color = Color(0XFF333333),
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold))
                    )
                    Spacer(modifier = Modifier.weight(1f))  // Pushes the button to the right

//                Text(
//                    text = "Change",
//                    color = Color.Black,
//                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
//                    fontSize = 12.sp,
//                    modifier = Modifier.background(color = Color(0xffEEEEEE), shape = CircleShape)
//                        .padding(vertical = 12.dp, horizontal = 20.dp)
//                )
                }
                Text(
                    text = "Little Acres Lane, Baldwinville, 13027",
                    fontSize = 12.sp,
                    color = Color(0XFF9D9D9D),
                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                    modifier = Modifier.padding(bottom = 16.dp)
                )


                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val intent = Intent(context, BottomNavigationActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(50), // Highly rounded corners for the button
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF33BD8C)) // Green button
                ) {
                    Text(
                        "Confirm Location",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium))
                    )
                }
            }
        }
    }*/

    suspend fun getAddressFromLatLng(
        context: android.content.Context,
        latitude: Double,
        longitude: Double
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address: Address = addresses[0]
                    val city = address.locality?:""
                    val addressLines =
                        (0..address.maxAddressLineIndex).map { address.getAddressLine(it) }
                    "$city city ${addressLines.joinToString(separator = "\n")}"
                } else {
                    getString(R.string.address_not_found)
                }
            } catch (e: Exception){
                return@withContext ""
            }
        }
    }
}