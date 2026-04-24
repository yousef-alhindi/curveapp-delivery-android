package com.curve.delivery.ui.commonscreen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.curve.delivery.R
import com.curve.delivery.ui.login.LoginScreen


@Composable
fun CommonScreen() {

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
fun textViewMedium(
    text: String,
    fontSize: Int,
    color: Int,
    modifier1: Modifier = Modifier,
    textAlign1: TextAlign? = null) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
        color = colorResource(color),
        modifier = modifier1,
        textAlign = textAlign1
    )
}

@Composable
fun imageView(image: Int, modifier1: Modifier = Modifier) {
    Image(
        painter = painterResource(id = image),
        contentDescription = null,
        modifier = modifier1
    )
}



@Composable
fun imageViewTint(image: Int, tint: Int) {
    Image(
        painter = painterResource(id = image),
        contentDescription = null,
        colorFilter = ColorFilter.tint(colorResource(id = tint))
    )
}