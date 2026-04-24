package com.curve.delivery.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.zIndex
import com.curve.delivery.R

@Composable
fun showProgress(isVisble:Boolean) {
    if (isVisble) {
        Column(
            Modifier
                .fillMaxSize()
                .zIndex(3f)
                .clickable { },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            CircularProgressIndicator(
                color = colorResource(id = R.color.green)
            )
        }
    }
}