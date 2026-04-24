package com.curve.delivery.ui.home

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.curve.delivery.R
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.viewModel.M1ViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

class ViewProfile : ComponentActivity() {
    private val viewModel: M1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ViewProfileTabs() }
    }

    @RequiresApi(VERSION_CODES.Q)
    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun ViewProfileTabs() {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            @Suppress("DEPRECATION") window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)
        var selectedTab by remember { mutableStateOf(0) }


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
                    text = stringResource(R.string.my_profile),
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


            Column(modifier = Modifier.background(color = Color.White)) {
                ActivePastTabs(selectedTab = selectedTab) { tab ->
                    selectedTab = tab
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 ->PeronslDetailsScreen(viewModel)
                    1 -> VehicleDetails(viewModel,this@ViewProfile)
                }
            }
        }
    }

    @Composable
    fun ActivePastTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
        val tabTitles = listOf("Personal Details", "Vehicle Details")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFF33BD8C)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tabTitles.forEachIndexed { index, title ->
                val isSelected = index == selectedTab

                Box(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .weight(1f)
                        .clip(
                            if (isSelected) {
                                RoundedCornerShape(
                                    topStart = 8.dp,
                                    topEnd = 8.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            } else {
                                RoundedCornerShape(0.dp)
                            }
                        )
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color(0xFF33BD8C) else Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium))
                    )
                }
            }
        }
    }

}
