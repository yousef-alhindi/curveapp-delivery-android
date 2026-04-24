package com.curve.delivery.ui.underReview

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.curve.delivery.R
import com.curve.delivery.ui.login.LoginScreen
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.moveActivity

class UnderReview() : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UnderReviewContent() }

    }

    @Composable
    fun UnderReviewContent() {
        cardLayout()

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
    fun textViewBold(text: String, fontSize: Int, color: Int) {
        androidx.compose.material3.Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_bold)),
            color = colorResource(color),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 25.dp, end = 25.dp).fillMaxWidth()
        )
    }

    @Composable
    fun textViewSemiBold(
        text: String,
        fontSize: Int,
        color: Int,
        modifier1: Modifier = Modifier,
        textAlign1: TextAlign? = null
    ) {
        androidx.compose.material3.Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
            color = colorResource(color),
            modifier = modifier1,
            textAlign = textAlign1
        )
    }

    @Composable
    fun textViewMedium(
        text: String,
        fontSize: Int,
        color: Int,
        modifier1: Modifier = Modifier,
        textAlign1: TextAlign? = null
    ) {
        androidx.compose.material3.Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_medium)),
            color = colorResource(color),
            modifier = modifier1,
            textAlign = textAlign1
        )
    }

    @Composable
    fun textViewRegular(text: String, fontSize: Int, color: Int) {
        androidx.compose.material3.Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
            color = colorResource(color),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 40.dp).fillMaxWidth()
        )
    }

    @Composable
    fun imageView(image: Int, modifier1: Modifier = Modifier) {
        Image(
            painter = painterResource(id = image), contentDescription = null, modifier = modifier1
        )
    }


    @Composable
    @Preview(showSystemUi = true)
    fun cardLayout() {
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
            )
        {
            Column {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White, contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
//                        .offset(y = -10.dp)
                        .padding(15.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    )

                    {
                        imageView(R.drawable.profile_placeholder)
                        verticalSpacer(value = 8)
                        textViewSemiBold(SharedPreference.get(this@UnderReview).name
                            , 16, R.color.black_05)
                        verticalSpacer(value = 10)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            textViewSemiBold("Logout", 12, R.color.red_D63636, modifier1 = Modifier.clickable {
                                moveActivity(LoginScreen())
                                finish()
                            })
                        }

                    }
                }
                verticalSpacer(value = 25)
                textViewBold("Your registration details are under review", 18, R.color.black_333333)
                verticalSpacer(value = 10)
                textViewRegular("We'll take 1-2 days to verify all the details,\nafter that you'll be notified.", 12, R.color.gray_9D9D9D)

            }
            GifImageView2()

        }
    }


    @Composable
    fun GifImageView2() {
        val context = LocalContext.current
        val imageResource = context.resources.getIdentifier("details_under_review", "drawable", context.packageName)
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = { ctx ->
                ImageView(ctx).apply {
                    Glide.with(ctx)
                        .asGif()
                        .load(imageResource)
                        .into(this)
                }
            }
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,LoginScreen::class.java))
    }
}
