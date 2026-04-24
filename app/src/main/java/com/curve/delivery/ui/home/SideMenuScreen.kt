package com.curve.delivery.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.*
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.curve.delivery.R
import com.curve.delivery.ui.createPassword.Aboutus
import com.curve.delivery.ui.createPassword.ChangePassScreen
import com.curve.delivery.ui.createPassword.ContactUs
import com.curve.delivery.ui.createPassword.Faqs
import com.curve.delivery.ui.createPassword.PrivacyPolicy
import com.curve.delivery.ui.createPassword.TermsCondition
import com.curve.delivery.ui.login.LoginScreen
import com.curve.delivery.ui.signup.startActivity
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.showProgress
import com.curve.delivery.viewModel.M1ViewModel

class SideMenuScreen:ComponentActivity(){
    private val viewModel: M1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        setContent { SideMenu() }
    }

    @Composable
    fun SideMenu(){
        val scrollState = rememberScrollState()
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            @Suppress("DEPRECATION") window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)

        val showPress by viewModel.progress.observeAsState()
        showProgress(showPress ?: false)

        val deleteResponse by viewModel.deleteAccountResponse.observeAsState()

        val logoutResponse by viewModel.deleteResponse.observeAsState()

        if(logoutResponse?.success==true){
            logoutResponse?.success=false
            startActivity(Intent(this, LoginScreen::class.java))
            finishAffinity()
        }
        if(deleteResponse?.status==true){
            deleteResponse?.status=false
            SharedPreference.get(this).deliveryId = ""
            SharedPreference.get(this).accessToken = ""
            SharedPreference.get(this).isLogin = false
            startActivity(Intent(this, LoginScreen::class.java))
            finishAffinity()
        }


        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)) {
            Row(modifier = Modifier
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

                Text(text = stringResource(R.string.logout),
                    modifier = Modifier.clickable {
                        showLogoutDialog(this@SideMenuScreen,viewModel)
                    },
                    color = Color(0xFFD63636),
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontSize = 14.sp)

            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState), ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        /* Image(painter = painterResource(id = R.drawable.profile_placeholder),
                             contentDescription = "Profile Image",
                             modifier = Modifier
                                 .size(60.dp)
                                 .clip(CircleShape),
                             contentScale = ContentScale.Crop
                         )
 */
                        AsyncImage(
                            model = SharedPreference.get(this@SideMenuScreen).profileImage,
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(top = 0.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = SharedPreference.get(this@SideMenuScreen).profileName, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.montserrat_semibold)), style = TextStyle(color = Color(0xFF051117)))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.clickable {
                                startActivity(Intent(this@SideMenuScreen,ViewProfile::class.java))
                            }) {
                                Text(text = stringResource(R.string.view_profile),
                                    color = Color(0XFF33BD8C),
                                    fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                                    fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))

                                Image(painter = painterResource(id = R.drawable.prfl_arrow_right_green),
                                    contentDescription =null, modifier = Modifier
                                        .size(12.dp)
                                        .padding(top = 4.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                ProfileItemCreate(icon = R.drawable.prfl_order_history, label = "Order History", onClick = {
                    startActivity(Intent(this@SideMenuScreen,OrderHistoryView::class.java))
                })
                ProfileItemCreate(icon = R.drawable.prfl_my_earnings, label = "My Earnings", onClick = {
                    startActivity(Intent(this@SideMenuScreen,MyEarning::class.java))
                })
                ProfileItemCreate(
                    icon = R.drawable.prfl_login_history, label = "Login History"
                ){}
                ProfileItemCreate(
                    icon = R.drawable.prfl_ratings_review, label = "Rating & Reviews", onClick = {
                        startActivity(Intent(this@SideMenuScreen,RateAndReview::class.java))
                    })
                ProfileItemCreate(
                    icon = R.drawable.prfl_change_password, label = "Change Password", onClick = {
                        startActivity(Intent(this@SideMenuScreen,ChangePassScreen::class.java))
                    }
                )

                ProfileItemCreate(
                    icon = R.drawable.logout, label = "Delete Account"){
                    showDeleteDialog(context = this@SideMenuScreen,viewModel)
                }


                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFFF1F1F1),
                    thickness = 5.dp)
                ProfileItemCreate(
                    icon = R.drawable.prfl_share_app, label = "Share App", onClick = {
                        shareApp(context = this@SideMenuScreen)
                    })
                val context = LocalContext.current
                ProfileItemCreate(
                    icon =R.drawable.prfl_rate_us, label = "Rate Us"
                ){
                    openPlayStore(context)
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFFF1F1F1),
                    thickness = 5.dp
                )
                ProfileItemCreate(
                    icon = R.drawable.prfl_faqs, label = "FAQs", onClick = {
                        startActivity(Intent(this@SideMenuScreen,Faqs::class.java))
                    }
                )
                ProfileItemCreate(
                    icon = R.drawable.prfl_contact_us, label = "Contact Us", onClick = {
                        startActivity(Intent(this@SideMenuScreen,ContactUs::class.java))
                    }
                )
                ProfileItemCreate(
                    icon =R.drawable.prfl_about_us, label = "About Us", onClick = {
                        startActivity(Intent(this@SideMenuScreen,Aboutus::class.java))
                    }
                )
                ProfileItemCreate(
                    icon = R.drawable.prfl_terms_conditions, label = "Terms & Conditions", onClick = {
                        startActivity(Intent(this@SideMenuScreen,TermsCondition::class.java))
                    }
                )
                ProfileItemCreate(
                    icon = R.drawable.prfl_privacy_policy, label = "Privacy Policy", onClick = {
                        startActivity(Intent(this@SideMenuScreen,PrivacyPolicy::class.java))
                    }
                )
            }
        }
    }

    fun openPlayStore(context: Context) {
        val appPackageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (e: Exception) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

}

@Composable
fun ProfileItemCreate(icon: Int, label: String,onClick:()->Unit) {
    Row(modifier = Modifier
        .clickable {
            onClick()
        }
        .fillMaxWidth()
        .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Card(shape = CircleShape,
            modifier = Modifier.size(40.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFECF8F5))) {

            Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier
                .padding(8.dp)
                .size(30.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
    }
}

private fun shareApp(context: Context) {
    val appPackageName = "com.curve.delivery"
    val shareText = "Check out this app: https://play.google.com/store/apps/details?id=$appPackageName"

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

@SuppressLint("MissingInflatedId")
fun showLogoutDialog(context: Context,viewModel: M1ViewModel) {
    val dialog = android.app.AlertDialog.Builder(context).create()
    val v = LayoutInflater.from(context).inflate(R.layout.logout_popup, null)

    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.apply {
        setView(v)
        setCancelable(false)
        val btnCancel = v.findViewById<AppCompatButton>(R.id.cancel)
        val btnAccept = v.findViewById<AppCompatButton>(R.id.accept)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnAccept.setOnClickListener {
            dialog?.dismiss()
            val activityContext = context as? Activity
            activityContext?.let {
                viewModel.logout(SharedPreference.get(context).accessToken)
                SharedPreference.get(context).deliveryId = ""
                SharedPreference.get(context).accessToken = ""
                SharedPreference.get(context).isLogin = false
//                context?.startActivity(Intent(context, LoginScreen::class.java))
//                context?.finishAffinity()

            }
        }
    }.show()
}


@SuppressLint("MissingInflatedId")
fun showDeleteDialog(context: Context,viewModel: M1ViewModel) {
    val dialog = android.app.AlertDialog.Builder(context).create()
    val v = LayoutInflater.from(context).inflate(R.layout.delete_popup, null)

    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.apply {
        setView(v)
        setCancelable(false)
        val btnCancel = v.findViewById<AppCompatButton>(R.id.cancel)
        val btnAccept = v.findViewById<AppCompatButton>(R.id.accept)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnAccept.setOnClickListener {
            val activityContext = context as? Activity
            activityContext?.let {
                viewModel.progress.value
                viewModel.deleteAccountResponse(SharedPreference.get(context).accessToken)
            }
        }
    }.show()
}

