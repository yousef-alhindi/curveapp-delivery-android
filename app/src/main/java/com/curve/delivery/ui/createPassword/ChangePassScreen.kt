package com.curve.delivery.ui.createPassword


import android.app.AlertDialog
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abi.simplecountrypicker.DialogCountryPicker
import com.curve.delivery.R
import com.curve.delivery.databinding.LanguageSelectionDialogBinding
import com.curve.delivery.databinding.LayoutOrdereredDeliveredBinding
import com.curve.delivery.databinding.LayoutResetPasswordPopupBinding
import com.curve.delivery.databinding.LayoutThankYouBinding
import com.curve.delivery.model.NewPasswordRequest
import com.curve.delivery.model.ResetPassRequest
import com.curve.delivery.response.UpdatePassword
import com.curve.delivery.ui.home.HomeScreen
import com.curve.delivery.ui.login.LoginScreen
import com.curve.delivery.ui.otp.OTPScreen
import com.curve.delivery.ui.resetPassword.ResetScreen
import com.curve.delivery.ui.signup.SignupScreen
import com.curve.delivery.ui.underReview.UnderReview
import com.curve.delivery.util.Constant
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.ShowCustomToast
import com.curve.delivery.util.moveActivity
import com.curve.delivery.util.showToast
import com.curve.delivery.util.showToastC
import com.curve.delivery.util.showToastLong
import com.curve.delivery.viewModel.M1ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangePassScreen : ComponentActivity() {
    private val viewModel: M1ViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LoginScreenContent(this) }
        observer()
    }
    fun observer() {
        viewModel.mNewPasswordResp.observe(this) {
            showToast(it.message)
            showDialog1(this)
//            moveActivity(LoginScreen())
//            finishAffinity()

        }
        viewModel.mError.observe(this) {
            showToast(it)
        }
    }

    fun showDialog1(context: Context?) {
        val binding = LayoutResetPasswordPopupBinding.inflate(LayoutInflater.from(context))
        val mBuilder = AlertDialog.Builder(context).setView(binding.root).create()
        mBuilder.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        mBuilder.setCancelable(false)
        mBuilder.show()
        binding.mOkay.setOnClickListener {
            mBuilder.dismiss()
            moveActivity(LoginScreen())
            finishAffinity()
        }
    }


    @Composable
    fun textViewBold(
        text: String,
        fontSize: Int,
        color: Int,
        textAlign1: TextAlign?,
        modifier1: Modifier = Modifier)
    {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_bold)),
            color = colorResource(id = color),
            textAlign = textAlign1,
            modifier = modifier1
        )
    }
    @Composable
    fun textViewSemiBold(
        text: String,
        fontSize: Int,
        color: Int,
        modifier1: Modifier = Modifier,
        textAlign1: TextAlign? = null)
    {
        Text(
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
        textAlign1: TextAlign? = null)
    {
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
    fun textViewRegular(text: String, fontSize: Int, color: Int)
    {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
            color = colorResource(color)
        )
    }

    @Composable
    fun imageView(image: Int, modifier1: Modifier = Modifier)
    {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = modifier1
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun LoginScreenContent(myContext: Context? = null) {
        var mobileNumber by remember { mutableStateOf(value = "") }
        var countryCode by remember { mutableStateOf("") }
/*
        val model = UpdatePassword()
        LaunchedEffect(key1 = Unit) {

        }*/

        val updatePasswordData by viewModel.updatePassword.observeAsState()

        if(updatePasswordData?.success==true){
            updatePasswordData?.success=false
            showOrderDeliveredAddress(this)
        }

        Column(modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top)
        {
            var mobileNumber by remember { mutableStateOf(value = "") }
            var passwordCurrent by remember { mutableStateOf(value = "") }
            var passwordNew by remember { mutableStateOf("") }
            var passwordConfirm by remember { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }
            var passwordVisible1 by remember { mutableStateOf(false) }
            var passwordVisible2 by remember { mutableStateOf(false) }
            val keyboardController = LocalSoftwareKeyboardController.current


            Image(modifier = Modifier
                .padding(start = 24.dp)
                .clickable {
                    onBackPressed()
                },
                painter = painterResource(id = R.drawable.back_ic), contentDescription = "")
            Image(modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.logo_login), contentDescription = "")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                VerticalSpacer(value = 36)

                Text(
                    text = stringResource(R.string.create_password),
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                    color = colorResource(id = R.color.black_333333)
                )

                VerticalSpacer(value = 12)

                Text(
                    text    = "Lorem ipsum dolor sit amet, consectetur adipiscing.",
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                    color = colorResource(id = R.color.gray_9D9D9D)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .background(
                            color = colorResource(id = R.color.white_F6F6F6),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = passwordCurrent,
                            onValueChange = { if (it.length <= 20) { passwordCurrent = it } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),

                            placeholder = {
                                Text(
                                    stringResource(R.string.current_password),

                                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                    color = colorResource(id = R.color.gray_9D9D9D),
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.montserrat_regular))
                                    ),
                                )
                            },
                            textStyle = TextStyle(fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))),

                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            trailingIcon = {
                                val icon = if (passwordVisible1) {
                                    painterResource(id = R.drawable.visibility_on)
                                } else {
                                   painterResource(id = R.drawable.visibility_off)
                                }
                                IconButton(onClick = { passwordVisible1 = !passwordVisible1 }) {
                                    Image(
                                        painter = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible1) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },

                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        VerticalSpacer(value = 12)
                        TextField(
                            value = passwordNew,
                            onValueChange = { if (it.length <= 20) { passwordNew = it } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            placeholder = {
                                Text(
                                    stringResource(R.string.new_password),
                                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                    color = colorResource(id = R.color.gray_9D9D9D),
                                    style = TextStyle(fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))),
                                )
                            },
                            textStyle = TextStyle(fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))),

                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            trailingIcon = {
                                val icon = if (passwordVisible) {
                                    painterResource(id = R.drawable.visibility_on)
                                } else {
                                    painterResource(id = R.drawable.visibility_off)
                                }
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Image(
                                        painter = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            singleLine = true,
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        VerticalSpacer(value = 12)
                        TextField(
                            value = passwordConfirm,
                            onValueChange = { if (it.length <= 20) { passwordConfirm = it } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            placeholder = {
                                Text(stringResource(R.string.confirm_password),
                                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                                    color = colorResource(id = R.color.gray_9D9D9D),
                                    style = TextStyle(fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))), )
                            },
                            textStyle = TextStyle(fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))),

                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            trailingIcon = {
                                val icon = if (passwordVisible2) {
                                    painterResource(id = R.drawable.visibility_on)
                                } else {
                                    painterResource(id = R.drawable.visibility_off)
                                }
                                IconButton(onClick = { passwordVisible2 = !passwordVisible2 }) {
                                    Image(
                                        painter = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible2) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            singleLine = true,
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                        VerticalSpacer(18)
                        Button(
                            onClick = {
                                if(validation(passwordCurrent,passwordNew,passwordConfirm))
                                {
                                    keyboardController?.hide()
                                    myContext?.let { context ->
                                        val request = NewPasswordRequest(currentPassword = passwordCurrent, password = passwordNew)
                                        Log.d("daya", "LoginScreenContent: $request")
                                        viewModel.updatePassword(SharedPreference.get(this@ChangePassScreen).accessToken,request)
                                    }
                                }

                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.green)),
                            contentPadding = PaddingValues(10.dp),
                            shape =RoundedCornerShape(25.dp),
                            elevation = null,
                            border = null
                        ) {
                            Text(text = stringResource(R.string.save), color = Color.White, modifier = Modifier.padding(vertical = 5.dp),
                                style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.montserrat_medium)))
                            )
                        }
                    }
                }
            }
        }
    }

    fun showOrderDeliveredAddress(context: Context) {
        val binding = LayoutOrdereredDeliveredBinding.inflate(LayoutInflater.from(context))
        val mBuilder = AlertDialog.Builder(context).setView(binding.root).create()
        mBuilder.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        mBuilder.setCancelable(false)
        mBuilder.show()

        binding.thankYouBuddy.text = getString(R.string.password_changed)
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            mBuilder.dismiss()
            finish()
        }
    }

    fun validation(password: String,confirmPassword: String,passwordConfirm:String): Boolean
    {
        if (password.isEmpty()) {
            showToast(getString(R.string.please_enter_current_password))
            return false
        }

        if (confirmPassword.isEmpty())
        {
           showToast(getString(R.string.please_enter_new_password))
            return false
        }

        if(confirmPassword.equals(password)){
            showToast(getString(R.string.current_password_and_new_password_should_not_be_same))
            return false
        }

        if(!passwordConfirm.equals(confirmPassword)){
            showToast(getString(R.string.confirm_password_and_new_password_should_be_same))
            return false
        }

        else if (!confirmPassword.matches(Constant.PASSWORD_PATTERN.toRegex())) {
            ShowCustomToast(getString(R.string.new_password_must_be_7_15_characters_long_with_at_least_1_uppercase_1_lowercase_1_special_character_and_1_number))
            return false
        }
        return true

    }

    @Composable
    fun VerticalSpacer(value: Int) {
        Spacer(modifier = Modifier.height(value.dp))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Preview(showBackground = true)
    @Composable
    fun LoginScreenPreview() {
        LoginScreenContent()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
