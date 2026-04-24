package com.curve.delivery.ui.otp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curve.delivery.R
import com.curve.delivery.model.OTPVerifyRequest
import com.curve.delivery.model.ResetPassRequest
import com.curve.delivery.new_architecture.helper.CustomLoader
import com.curve.delivery.ui.createPassword.CreatePassScreen
import com.curve.delivery.ui.signup.SignupScreen
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.moveActivity
import com.curve.delivery.util.showToast
import com.curve.delivery.viewModel.M1ViewModel
import kotlinx.coroutines.delay

class OTPScreen : ComponentActivity() {
    val viewModel: M1ViewModel by viewModels()

    companion object {
        var COUNTRY_CODE = ""
        var PHONE_NUMBER = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OTPScreenContent() }
        observer()
    }

    private fun observer() {
        viewModel.mOtpVerifyResp.observe(this) {
            CustomLoader.hideLoader()
            Log.d("Response", it.toString())
            showToast(it.message)
            SharedPreference.get(this@OTPScreen).accessToken = it.data.accessToken
            SharedPreference.get(this@OTPScreen).isLogin = true
            if (intent.hasExtra("reset")) {
                moveActivity(CreatePassScreen())
            }

            if (intent.hasExtra("signup")) {
                startActivity(
                    Intent(this@OTPScreen, SignupScreen::class.java).putExtra(
                        "vehicle",
                        "vehicle"
                    )
                )
                finish()
            }

        }
        viewModel.mError.observe(this) {
            CustomLoader.hideLoader()
            Log.d("Error", it.toString())
            showToast(it.toString())
        }
    }

    @Composable
    fun OTPScreenContent() {
        var otpValue by remember { mutableStateOf("") }
        var timeLeft by remember { mutableStateOf(30) }
        var startTimer by remember { mutableStateOf(true) }
        var resend by remember { mutableStateOf("") }
        var color by remember { mutableStateOf(Color(0XFF848484)) }
        var focous = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        )
        {
            Image(
                modifier = Modifier
                    .padding(start = 16.dp, top = 12.dp)
                    .clickable {
                        onBackPressedDispatcher.onBackPressed()
                    },
                painter = painterResource(id = R.drawable.back_ic),
                contentDescription = "otp"
            )
            VerticalSpacer(5)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_login),
                    contentDescription = "otp"
                )
            }
            VerticalSpacer(36)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.otp_verification),
                    fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.black_333333)
                )

                VerticalSpacer(12)
                Text(
                    text = stringResource(R.string._6_digit_otp_has_been_sent_to_your_registered_mobile_number),
                    fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.gray_9D9D9D)
                )
                VerticalSpacer(value = 20)
                //var otpValue by remember { mutableStateOf("") }

                var otpValue by remember { mutableStateOf(List(6) { "" }) }

                var otpHint = listOf("", "", "", "", "", "")

                var currentFocusedIndex by remember { mutableStateOf(0) }
                val focusRequesters = remember { Array(6) { FocusRequester() } }
                // Handle Android back button press to move focus backward
//                BackHandler {
//                    // If the current field is not the first, move focus back
//                    if (currentFocusedIndex > 0) {
//                        currentFocusedIndex--
//                        focusRequesters[currentFocusedIndex].requestFocus()
//                    }else{
//
//                    }
//                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val focusRequesters = remember {
                        Array(6) { FocusRequester() }
                    }

                    for (i in otpHint.indices) {
                        TextField(
                            value = otpValue[i],

                            onValueChange = { newValue ->
                                if (newValue.length < 1) {
                                    if (i > 0) {
                                        focusRequesters[i - 1].requestFocus()
                                    }
                                }
                                if (newValue.length < 2) {
                                    otpValue = otpValue.toMutableList().apply {
                                        if (newValue.toString().length < 6) {
                                            this[i] = newValue
                                        }
                                    }
                                    if (otpValue[i].toString().length == 1 && i < 5) {
                                        focusRequesters[i + 1].requestFocus()
                                    }
                                }
                            },

                            /*onValueChange = { newValue ->
                                // Handle empty input and backspace
                                if (newValue.isEmpty() && i > 0) {
                                    otpValue = otpValue.toMutableList().apply {
                                        this[i] = ""  // Clear the current field
                                    }
                                    currentFocusedIndex = i - 1
                                    focusRequesters[currentFocusedIndex].requestFocus()
                                } else {
                                    // Ensure only one digit is entered
                                    val singleChar = newValue.take(1)
                                    otpValue = otpValue.toMutableList().apply {
                                        this[i] = singleChar
                                    }

                                    // If a digit is entered and it's not the last field, move to the next field
                                    if (singleChar.isNotEmpty() && i < otpValue.lastIndex) {
                                        currentFocusedIndex = i + 1
                                        focusRequesters[currentFocusedIndex].requestFocus()
                                    }
                                }
                            },*/

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                            ),
                            shape = RoundedCornerShape(10.dp),
                            visualTransformation = OneDigitVisualTransformation(),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequesters[i])
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        currentFocusedIndex = i
                                    }
                                },
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_regular))
                            ),
                            singleLine = true,
                            placeholder = { Text(otpHint[i]) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0XFFF6F6F6),
                                unfocusedContainerColor = Color(0XFFF6F6F6),
                                focusedIndicatorColor = Color(0XFF33BD8C),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedLabelColor = Color.Black,
                                disabledContainerColor = Color.White,
                                cursorColor = Color(0XFF161616),
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }

                VerticalSpacer(value = 20)
                Row {
                    ResendOtp() {
                        if (it) {
                            otpValue = listOf("", "", "", "", "", "")
                            // otpValue= ""
                            focous.clearFocus(true)
                        }
                    }
                }
                VerticalSpacer(value = 40)

                Button(
                    onClick = {

                        // val singleNumber: Int = otpValues.toString().toInt()
                        if (!otpValue.contains("")) {

                            val cleanedInput =
                                otpValue.toString().removeSurrounding("[", "]").split(", ")
                                    .map { it.trim() }

                            try {
                                val numberString = cleanedInput.joinToString(separator = "")
                                val resultNumber = numberString.toInt()
                                println(resultNumber)
                                val request =
                                    OTPVerifyRequest(otp = otpValue.joinToString("").toInt())
                                viewModel.hitVerifyOtp(
                                    SharedPreference.get(this@OTPScreen).accessToken,
                                    request
                                )
                                CustomLoader.showLoader(this@OTPScreen)
                            } catch (e: NumberFormatException) {
                                showToast(getString(R.string.please_enter_otp))
                                println("Error: Input contains non-integer values or could not be converted to an integer.")
                            } catch (e: Exception) {
                                println("An unexpected error occurred: ${e.message}")
                            }

                        } else {
                            showToast(getString(R.string.please_enter_otp))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF33BD8C),
                    )
                ) {
                    Text(
                        stringResource(R.string.submit),
                        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                        fontSize = 14.sp
                    )
                }
            }


            /* OtpView(
                 otpText = otpValue,
                 otpCount = 6,
                 onOtpTextChange = {
                     otpValue = it
                     Log.d("Actual Value", otpValue)
                 },
                 charBackground = colorResource(id = R.color.white_F6F6F6),
                 type = OTP_VIEW_TYPE_NONE,
                 password = true,
                 containerSize = 50.dp,
                 passwordChar = "*",
                 keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                 charColor = Color.Gray,
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(50.dp)
                     .focusRequester(FocusRequester()),
             )
 */


            /*Button(
                onClick = {
                    val request = OTPVerifyRequest(otp = otpValue.toInt())
                    val token  = SharedPreference.get(this@OTPScreen).accessToken
                    viewModel.hitVerifyOtp(token,request)
                    Log.d("TAG", "LoginScreenContent: $token $request")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = colorResource(id = R.color.green)
                )
            ) {
                Text(
                    text = "Submit",
                    fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                    fontSize = 14.sp
                )
            }*/
        }
    }

    class OneDigitVisualTransformation : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val transformedText = if (text.text.length > 1) {
                text.text.subSequence(0, 1)
            } else {
                text.text
            }
            val offsetMapping = OneDigitOffsetMapping(transformedText.length)
            return TransformedText(AnnotatedString(transformedText.toString()), offsetMapping)
        }
    }

    class OneDigitOffsetMapping(private val transformedLength: Int) : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return if (offset <= transformedLength) offset else transformedLength
        }

        override fun transformedToOriginal(offset: Int): Int {
            return offset
        }
    }


    @Composable
    fun ResendOtp(onResent: (Boolean) -> Unit) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            var timeLeft by remember { mutableStateOf(30) }
            var startTimer by remember { mutableStateOf(true) }
            var resend by remember { mutableStateOf("") }
            var color by remember { mutableStateOf(Color(0XFF848484)) }
            Text(
                text = resend,
                fontSize = 12.sp,
                color = color,
                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                modifier = Modifier.clickable {
                    if (resend.toString().contains("Resend")) {
                        startTimer = true
                        onResent(true)
                        Log.d("TAG", "ResendOtp: $COUNTRY_CODE")
                        Log.d("TAG", "ResendOtp: $PHONE_NUMBER")
                        viewModel.hitResetPassword(
                            token = SharedPreference.get(this@OTPScreen).accessToken,
                            userRequest = ResetPassRequest(
                                countryCode = COUNTRY_CODE,
                                mobileNumber = PHONE_NUMBER
                            )
                        )

                    } else {
                        startTimer = false


                    } // Only clickable, no align needed here
                })
            val context = LocalContext.current
            if (startTimer) {
                LaunchedEffect(key1 = Unit) {
                    val countDownTimer = object : CountDownTimer(90000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            timeLeft = (millisUntilFinished / 1000).toInt()
                            color = Color(0XFF848484)
                            resend = "Resend OTP in $timeLeft seconds"

//                            if (timeLeft.toString().length<2){
//                                resend="00:0$timeLeft"
//                            }
//                            else {
//                                resend = "00:"+timeLeft.toString()
//                            }

                        }

                        override fun onFinish() {
                            timeLeft = 0
                            startTimer = false
                        }
                    }
                    countDownTimer.start()

                    // Delay for 1 second after countdown finishes to show 0
                    delay(1000)

                }
            } else {
                color = Color(0XFF33BD8C)
                resend = "Resend OTP"
            }

        }
    }

    @Composable
    fun VerticalSpacer(value: Int) {
        Spacer(modifier = Modifier.height(value.dp))
    }

    @Composable
    fun HorizontalSpacer(value: Int) {
        Spacer(modifier = Modifier.width(value.dp))
    }

    @Preview(showSystemUi = true)
    @Composable
    fun PreviewOTPScreenContent() {
        OTPScreenContent()
    }

    /* override fun onBackPressed() {
         super.onBackPressed()
 //        startActivity(Intent(this,ResetScreen::class.java))
 //        finish()
     }*/
}
