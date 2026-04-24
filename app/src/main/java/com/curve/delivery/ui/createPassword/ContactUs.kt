package com.curve.delivery.ui.createPassword

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.curve.delivery.R
import com.curve.delivery.new_architecture.helper.CustomLoader
import com.curve.delivery.ui.home.VerticalSpacer
import com.curve.delivery.ui.resetPassword.ResetScreen
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.viewModel.M1ViewModel

@Suppress("ANNOTATIONS_ON_BLOCK_LEVEL_EXPRESSION_ON_THE_SAME_LINE",
    "WRAPPED_LHS_IN_ASSIGNMENT_WARNING"
)
class ContactUs:ComponentActivity(){
    private val viewModel: M1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ContactUsScreen(this) }
    }

    @Composable
    fun ContactUsScreen(contactUs: Context) {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            @Suppress("DEPRECATION") window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)
        LaunchedEffect(key1 = Unit) {
            viewModel.cmsManagement(
                SharedPreference.get(this@ContactUs).accessToken,
                Enums.CMSTYPE.CONTACT_US.type.toString(),
                "5")
        }

        val cmsData by viewModel.cmsManagementResponse.observeAsState()

        if(cmsData?.data!=null) {
            CustomLoader.hideLoader()
            Column(
                modifier = Modifier
                    .padding(top = 0.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = Color.White),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top) {

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
                        text = stringResource(R.string.contact_us),
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)) {

                    Text(
                        text = stringResource(R.string.we_re_here_to_help_if_you_have_any_questions_concerns_or_feedback_regarding_the_curve_driver_app_please_reach_out_to_us_through_any_of_the_following_methods),
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                        color = colorResource(id = R.color.gray_9D9D9D))


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .background(
                                color = Color(0xFFE8F8F2), shape = RoundedCornerShape(10.dp)
                            )) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                                .background(
                                    color = Color.White, shape = RoundedCornerShape(10.dp)
                                )
                                .padding(8.dp)) {

                            Row {
                                Image(
                                    painter = painterResource(id = R.drawable.cont_email),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clickable {
                                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                data =
                                                    Uri.parse("mailto:${cmsData?.data?.contactDetail?.email ?: ""}")
                                            }
                                            startActivity(
                                                Intent.createChooser(
                                                    emailIntent,
                                                    "Send feedback"
                                                )
                                            )
                                        })
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(
                                        text = stringResource(R.string.support_email),
                                        color = Color(0xFF787878),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular)))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = cmsData?.data?.contactDetail?.email ?: "",
                                        color = Color(0xFF333333),
                                        fontSize = 14.sp,
                                        modifier = Modifier.clickable {
                                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:${cmsData?.data?.contactDetail?.email ?: ""}")
                                            }
                                            startActivity(Intent.createChooser(emailIntent, "Send feedback"))
                                           // sendEmail(this@ContactUs,cmsData?.data?.contactDetail?.email ?: "")
                                        },
                                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .background(
                                    color = Color.White, shape = RoundedCornerShape(10.dp)
                                )
                                .padding(8.dp)) {

                            Row {
                                Image(
                                    painter = painterResource(id = R.drawable.cont_number),
                                    contentDescription = null,
                                    modifier = Modifier.size(46.dp))
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(
                                        text = stringResource(R.string.support_contact_number),
                                        color = Color(0xFF787878),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular)))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = cmsData?.data?.contactDetail?.phoneNumber ?: "",
                                        color = Color(0xFF333333),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
                                }

                                Spacer(modifier = Modifier.weight(1f))
                                Image(
                                    painter = painterResource(id = R.drawable.cont_call),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(top = 15.dp)
                                        .size(24.dp)
                                        .clickable {
                                            val mobie =
                                                cmsData?.data?.contactDetail?.phoneNumber ?: ""
                                            val intent =
                                                Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:$mobie")
                                                }
                                            startActivity(intent)
                                        })
                            }
                        }
                    }

                }
            }
        }else{
            CustomLoader.showLoader(this)
        }

    }
}

