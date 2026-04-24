package com.curve.delivery.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.abi.simplecountrypicker.DialogCountryPicker
import com.curve.delivery.R
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.viewModel.M1ViewModel

@Composable
fun VehicleDetails(viewModel: M1ViewModel, viewProfile: ViewProfile) {
    val context = LocalContext.current

    val getVehicleDetailsDetails by viewModel.getProfileDetailsResponse.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.progress.value = true
        viewModel.getProfileDetails(SharedPreference.get(context).accessToken)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
            .background(
                color = colorResource(id = R.color.white_F6F6F6), shape = RoundedCornerShape(10.dp)
            )
           ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)) {
            VerticalSpacer(12)
            Text(
                text = stringResource(R.string.vehicle_number),
                color = colorResource(id = R.color.black_333333),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                textAlign = TextAlign.Start,
            )
            VerticalSpacer(12)

            Text(
                text = getVehicleDetailsDetails?.data?.vechileDetails?.vechileName?:"",
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                color = Color(0xFF333333),
                fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
                    .height(36.dp)
                    .background(
                        color = Color(0xFFFFFFFF), shape = RoundedCornerShape(8.dp)
                    )
                    .padding(start = 8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically))

            VerticalSpacer(16)
            Text(
                text = stringResource(R.string.registration_certificate),
                color = colorResource(id = R.color.black_333333),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                textAlign = TextAlign.Start,
            )
            VerticalSpacer(12)

            AsyncImage(
                model = if (getVehicleDetailsDetails?.data?.vechileDetails?.RegistrationCertificateFront.isNullOrEmpty()) R.drawable.appicon else getVehicleDetailsDetails?.data?.vechileDetails?.RegistrationCertificateFront,
                placeholder = painterResource(id = R.drawable.appicon),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(288.dp)
                    .height(157.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = Color(0xFFF6F6F6), shape = RoundedCornerShape(8.dp))
               /* .border(
                    width = 1.dp,
                    color = Color(0xFFF6F6F6),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))*/)

            VerticalSpacer(16)
            Text(
                text = stringResource(R.string.driving_license),
                color = colorResource(id = R.color.black_333333),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                textAlign = TextAlign.Start,
            )
            VerticalSpacer(12)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            ) {

                AsyncImage(
                    model = if (getVehicleDetailsDetails?.data?.vechileDetails?.drivingLicenseFront.isNullOrEmpty()) R.drawable.appicon else getVehicleDetailsDetails?.data?.vechileDetails?.drivingLicenseFront,
                    placeholder = painterResource(id = R.drawable.appicon),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .height(157.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = Color(0xFFF6F6F6), shape = RoundedCornerShape(8.dp))
                        )
                Spacer(modifier = Modifier.width(16.dp))

                AsyncImage(
                    model = if (getVehicleDetailsDetails?.data?.vechileDetails?.drivingLicenseBack.isNullOrEmpty()) R.drawable.appicon else getVehicleDetailsDetails?.data?.vechileDetails?.drivingLicenseBack,
                    placeholder = painterResource(id = R.drawable.appicon),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .height(157.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = Color(0xFFF6F6F6), shape = RoundedCornerShape(8.dp))
                       /* .border(
                            width = 1.dp,
                            color = Color(0xFFF6F6F6),
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )*/)
            }

        }
    }
}

@Composable
fun VerticalSpacer(value: Int) {
    Spacer(modifier = Modifier.height(value.dp))
}