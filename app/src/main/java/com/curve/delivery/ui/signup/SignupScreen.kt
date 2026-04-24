package com.curve.delivery.ui.signup

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.abi.simplecountrypicker.DialogCountryPicker
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.curve.delivery.MyApplication
import com.curve.delivery.R
import com.curve.delivery.R.color
import com.curve.delivery.R.drawable
import com.curve.delivery.R.font
import com.curve.delivery.databinding.LayoutCameraGallery1Binding
import com.curve.delivery.databinding.LayoutCameraGalleryBinding
import com.curve.delivery.databinding.LayoutSelectGenderBinding
import com.curve.delivery.databinding.LayoutSelectLanguageBinding
import com.curve.delivery.databinding.LayoutThankYouBinding
import com.curve.delivery.model.AddBankRequest
import com.curve.delivery.model.AddVehicleRequest
import com.curve.delivery.model.RegisterRequest
import com.curve.delivery.new_architecture.helper.CommonUtils.changeDateFormat
import com.curve.delivery.new_architecture.helper.CustomLoader
import com.curve.delivery.new_architecture.viewmodel.AuthViewModel
import com.curve.delivery.ui.login.LoginScreen
import com.curve.delivery.ui.login.PdfRenderActivity
import com.curve.delivery.ui.otp.OTPScreen
import com.curve.delivery.ui.underReview.UnderReview
import com.curve.delivery.util.Constant
import com.curve.delivery.util.ErrorUtil
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.ShowCustomToast
import com.curve.delivery.util.checkPermissions
import com.curve.delivery.util.compressImage
import com.curve.delivery.util.moveActivity
import com.curve.delivery.util.moveActivityHaxExtra
import com.curve.delivery.util.showProgress
import com.curve.delivery.util.showToast
import com.curve.delivery.util.showToastC
import com.curve.delivery.util.showToastLong
import com.curve.delivery.util.toMultipartBody
import com.curve.delivery.viewModel.M1ViewModel
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.BufferedSink
import okio.source
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import androidx.compose.material.icons.filled.Check as Check1


class SignupScreen : ComponentActivity() {
    private val viewModel: M1ViewModel by viewModels()
    private val viewModel1: AuthViewModel by viewModels()
    var mImageType = 1
    private var moveTo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(color.white)
        getIntentDat()/*if (intent.hasExtra("vehicle")){
            viewModel.signupType.value=2
        }
        else {
            viewModel.signupType.value = 1
        }*/
        setContent {
//            MyApp(viewModel = viewModel)
            SignupScreenContent(viewModel)
        }
        observer()
    }

    /*@Composable
    fun BackHandler(onBack: () -> Unit) {
        val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        BackHandler(enabled = true) {
            onBack()
        }
    }

    @Composable
    fun MyApp(viewModel: M1ViewModel) {
        val currentComposable by viewModel.currentComposable.collectAsState()

        when (currentComposable) {
            M1ViewModel.ComposableType.First -> SignupScreenContent(viewModel)
            M1ViewModel.ComposableType.Second -> VehicleDetailsUI(viewModel)
            M1ViewModel.ComposableType.Third -> BankDetailsUI()
        }
    }
*/

    fun observer() {
        viewModel.mUploadImageResp.observe(this) {
            CustomLoader.hideLoader()
            when (mImageType) {
                1 -> viewModel.mProfileImgUrl.value = it.data
                2 -> viewModel.mBasicFrontUrl.value = it.data
                3 -> viewModel.mBasicBackUrl.value = it.data
                4 -> viewModel.mVehicleUrl1.value = it.data
                5 -> viewModel.mVehicleUrl2.value = it.data
                6 -> viewModel.mVehicleUrl3.value = it.data
            }
        }
        viewModel.mError.observe(this) {
            CustomLoader.hideLoader()
            showToast("$it")
            Log.d("daya", "observerAAA:$it ::::::::::")
        }

        viewModel.mAddVehicleResp.observe(this) {
            CustomLoader.hideLoader()

            //showToast(it.message.toString())
            if (it.message.toString().equals("Updated Successfully")) {
                Log.d("TAG", "observer Vehicle: ...add vehicle dedtails")
            } else {
                showToast(it.message.toString())
            }
            viewModel.updateSignupType(3)
        }

        viewModel.mSignupResp.observe(this) {
            // if (moveTo) {
            //      moveTo=false
            CustomLoader.hideLoader()
            if (it.message.toString().equals("Updated Successfully")) {

            } else {
                showToast(it.message.toString())
            }
            // showToast(it.message.toString())
            OTPScreen.COUNTRY_CODE = it.data.countryCode
            OTPScreen.PHONE_NUMBER = it.data.mobileNumber
            SharedPreference.get(this@SignupScreen).accessToken = it.data.accessToken
            moveActivityHaxExtra("signup", OTPScreen())
            // }
        }

        viewModel.mAddBankResp.observe(this) {
            CustomLoader.hideLoader()
            if (it.message.toString().equals("Updated Successfully")) {

            } else {
                showToast(it.message.toString())
            }
            // showToast(it.message.toString())
            showDialog1(this)
        }
    }

    var docname: String = ""

    val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { pdfUri ->
                val contentResolver = this.contentResolver
                var docFile: MultipartBody.Part? = null

                if (pdfUri.toString().startsWith("content://")) {
                    contentResolver.query(pdfUri, null, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            docname =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                            // Take persistable URI permission if required
                            contentResolver.takePersistableUriPermission(
                                pdfUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )

                            val requestBody = object : RequestBody() {
                                override fun contentType(): MediaType? =
                                    "application/pdf".toMediaTypeOrNull()

                                override fun writeTo(sink: BufferedSink) {
                                    contentResolver.openInputStream(pdfUri)?.use { inputStream ->
                                        sink.writeAll(inputStream.source())
                                    } ?: throw IOException("Could not open $pdfUri")
                                }
                            }

                            docFile = MultipartBody.Part.createFormData(
                                "upload_delivery_file", docname, requestBody
                            )

                            // Assuming you have a ViewModel and function to handle the file upload
                            if (viewModel.checkInternetConnection()) {

                                viewModel.uploadFile(docFile)
                                CustomLoader.showLoader(this)
                            } else {
                                showToastC(
                                    MyApplication.appContext,
                                    "Please check your internet connection and try again"
                                )
                            }
                        }
                    }
                }
            }
        }


    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val imageFile = result.uriContent?.let { uriToFile(this, it) }
            imageFile?.let {
                if (viewModel.checkInternetConnection()) {
                    val multipartBody =
                        convertFileToMultipartBody("upload_delivery_file", imageFile)
                    viewModel.uploadFile(multipartBody)
                    viewModel.progress.value = true
                } else {
                    viewModel.progress.value = false
                    showToastC(
                        MyApplication.appContext,
                        "Please check your internet connection and try again"
                    )
                }
            }
        } else {
            // An error occurred.
            val exception = result.error
        }
    }


    /*

        private val cropImage = registerForActivityResult(CropImageContract()) { result ->

            if (!result.isSuccessful) {
                viewModel.progress.value = false
                Log.e("CropImage", result.error?.message ?: "Crop failed")
                return@registerForActivityResult
            }

            val uri = result.uriContent ?: return@registerForActivityResult
            val originalFile = uriToFile(this, uri) ?: return@registerForActivityResult

            if (!viewModel.checkInternetConnection()) {
                viewModel.progress.value = false
                showToastC(
                    MyApplication.appContext,
                    "Please check your internet connection and try again"
                )
                return@registerForActivityResult
            }

            //  COMPRESS IMAGE (MAIN FIX)
            val compressedFile = originalFile.compressImage(
                maxWidth = 1080,
                quality = 75
            )

            //  MULTIPART
            val multipartBody =
                compressedFile.toMultipartBody("upload_delivery_file")

            //  API CALL
            viewModel.progress.value = true
            viewModel.uploadFile(multipartBody)
        }
    */


    fun ComponentActivity.getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            this.contentResolver, inImage, "IMG_${System.currentTimeMillis()}", null
        )
        return Uri.parse(path)
    }

    @Composable
    fun UploadFrontBackDocument(
        mBasicFrontUrl: String, mBasicBackUrl: String
    ) {
        val context = LocalContext.current
        val galleryLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    cropImage.launch(
                        options(uri = uri) {
                            setGuidelines(CropImageView.Guidelines.ON)
                            setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                        })
                }
            }
        val cameraLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val image = result.data?.extras?.get("data") as Bitmap?

                    var imgUri = image?.let { getImageUri(it) }
                    cropImage.launch(
                        options(uri = imgUri) {
                            setGuidelines(CropImageView.Guidelines.ON)
                            setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                        })
                }
            }

        var showFrontImage by remember { mutableStateOf(false) }
        var showBackImage by remember { mutableStateOf(false) }

        if (showFrontImage) {
            showFrontImage = previewImage(imageUrl = viewModel.mBasicFrontUrl.value)
        }
        if (showBackImage) {
            showBackImage = previewImage(imageUrl = viewModel.mBasicBackUrl.value)
        }

        val stroke =
            Stroke(width = 5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .size(150.dp)
                    .padding(end = 4.dp)
                    .background(
                        color = colorResource(id = color.white), shape = RoundedCornerShape(10.dp)
                    )
                    .drawBehind {
                        drawRoundRect(
                            color = Color.Green, style = stroke, cornerRadius = CornerRadius(10f)
                        )
                    }
                    .clickable {
                        if (mBasicFrontUrl.toString().contains("pdf")) {
                            startActivity(
                                Intent(this@SignupScreen, PdfRenderActivity::class.java).putExtra(
                                    "url", mBasicFrontUrl.toString()
                                )
                            )
                        } else if (!mBasicFrontUrl.isNullOrEmpty()) {
                            showFrontImage = true
                        }
//                    else if (!mBasicBackUrl.isNullOrEmpty()) {
//                        showBackImage = true
//                    }
                        else {
                            mImageType = 2

                            val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                            pdfIntent.type = "application/pdf"
                            pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)

                            openSheetCameraGallery(
                                onCameraClick = {
                                    cameraLauncher.launch(
                                        Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE
                                        )
                                    )
                                },
                                onGalleryClick = { galleryLauncher.launch("image/*") },
                                onPdfClick = { pdfLauncher.launch(pdfIntent) })
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {

                    if (!mBasicFrontUrl.isNullOrEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.cross),
                            contentDescription = "",
                            alignment = Alignment.TopEnd,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .zIndex(2f)
                                .width(20.dp)
                                .height(20.dp)
                                .align(Alignment.TopEnd)
                                .offset(
                                    x = (10).dp, y = -5.dp
                                ) // Adjusting the position to overlap the first image's border
                                .clickable {
                                    viewModel.mBasicFrontUrl.value = ""
                                })
                    } else if (!mBasicFrontUrl.isNullOrEmpty()) {
                        showFrontImage = true
                    }
                    if (viewModel.mBasicFrontUrl.value.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = drawable.upload_ic),
                                contentDescription = ""
                            )
                            VerticalSpacer(value = 5)
                            Text(
                                text = stringResource(R.string.upload),
                                color = colorResource(id = color.green),
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(font.montserrat_regular))
                            )
                            Text(
                                text = stringResource(R.string.front_side),
                                color = colorResource(id = color.gray_9D9D9D),
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(font.montserrat_regular))
                            )
                        }
                    }
                    var pdfImg by remember { mutableStateOf<ImageBitmap?>(null) }

                    if (mBasicFrontUrl.toString().contains("pdf", true)) {
                        FetchPDFImageFromUrl(mBasicFrontUrl, this@SignupScreen) { img ->
                            pdfImg = img
                        }
                        pdfImg?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Image(
                            painter = rememberImagePainter(
                                request = coil.request.ImageRequest.Builder(
                                    LocalContext.current
                                ).data(mBasicFrontUrl).build()
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            //  Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .size(150.dp)
                    .padding(start = 4.dp)
                    .background(
                        color = colorResource(id = color.white), shape = RoundedCornerShape(10.dp)
                    )
                    .drawBehind {
                        drawRoundRect(
                            color = Color.Green, style = stroke, cornerRadius = CornerRadius(10f)
                        )
                    }

                    .clickable {
                        if (mBasicBackUrl.toString().contains("pdf")) {
                            startActivity(
                                Intent(
                                    this@SignupScreen, PdfRenderActivity::class.java
                                ).putExtra("url", mBasicBackUrl.toString())
                            )
                        } else if (!mBasicBackUrl.isNullOrEmpty()) {
                            showBackImage = true
                        } else {
                            mImageType = 3

                            val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                            pdfIntent.type = "application/pdf"
                            pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)

                            openSheetCameraGallery(
                                onCameraClick = { cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) },
                                onGalleryClick = { galleryLauncher.launch("image/*") },
                                onPdfClick = { pdfLauncher.launch(pdfIntent) })
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    if (!mBasicBackUrl.isNullOrEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.cross),
                            contentDescription = "",
                            alignment = Alignment.TopEnd,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .zIndex(2f)
                                .width(20.dp)
                                .height(20.dp)
                                .align(Alignment.TopEnd)
                                .offset(
                                    x = (10).dp, y = -5.dp
                                ) // Adjusting the position to overlap the first image's border
                                .clickable {
                                    viewModel.mBasicBackUrl.value = ""
                                })
                    } else if (!mBasicBackUrl.isNullOrEmpty()) {
                        showBackImage = true
                    }
                    if (viewModel.mBasicBackUrl.value.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Image(
                                painter = painterResource(id = drawable.upload_ic),
                                contentDescription = ""
                            )
                            VerticalSpacer(value = 5)
                            Text(
                                text = stringResource(R.string.upload),
                                color = colorResource(id = color.green),
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(font.montserrat_regular))
                            )
                            Text(
                                text = stringResource(R.string.back_side),
                                color = colorResource(id = color.gray_9D9D9D),
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(font.montserrat_regular))
                            )
                        }
                    }

                    var pdfImg by remember { mutableStateOf<ImageBitmap?>(null) }

                    if (mBasicBackUrl.toString().contains("pdf", true)) {
                        FetchPDFImageFromUrl(mBasicBackUrl, this@SignupScreen) { img ->
                            pdfImg = img
                        }
                        pdfImg?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Image(
                            painter = rememberImagePainter(
                                request = coil.request.ImageRequest.Builder(
                                    LocalContext.current
                                ).data(mBasicBackUrl).build()
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun CaptureProfilePhoto() {
        //  var showImg by remember { mutableStateOf(false) }
//        if (showImg) {
//            showImg = previewImage(imageUrl = viewModel.mProfileImgUrl.value.toString())
//        }
        val context = LocalContext.current
        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {
                cropImage.launch(
                    options(uri = uri) {
                        setGuidelines(CropImageView.Guidelines.ON)
                        setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                    })
            }
        }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val image = result.data?.extras?.get("data") as Bitmap?
                var imgUri = image?.let { getImageUri(it) }
                cropImage.launch(
                    options(uri = imgUri) {
                        setGuidelines(CropImageView.Guidelines.ON)
                        setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                    })
            }
        }


        Box(modifier = Modifier.clickable {
            if (checkPermissions()) {
                mImageType = 1
                openSheetCameraGallery1(onCameraClick = {
                    cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                }, onGalleryClick = { galleryLauncher.launch("image/*") }, onPdfClick = {})

            } else {
                requestPermissions()
            }
        }) {
            when {
                viewModel.mProfileImgUrl.value != "" -> Image(
                    painter = rememberImagePainter(
                        request = coil.request.ImageRequest.Builder(LocalContext.current)
                            .data(viewModel.mProfileImgUrl.value)
                            .placeholder(R.drawable.profile_placeholder).build()
                    ),

                    contentDescription = null, modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            if (checkPermissions()) {
                                mImageType = 1
                                openSheetCameraGallery(
                                    onCameraClick = {
                                        cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                                    },
                                    onGalleryClick = { galleryLauncher.launch("image/*") },
                                    onPdfClick = {})

                            } else {
                                requestPermissions()
                            }


                            //   if (!viewModel.mProfileImgUrl.value.isNullOrBlank()) showImg = true
                        }
                        .clip(CircleShape), contentScale = ContentScale.Crop)

                else -> Image(
                    painter = painterResource(id = R.drawable.profile_placeholder),
                    contentDescription = null
                )
            }
        }
    }


    fun bitmapToFile(context: Context, bitmap: Bitmap): File? {
        // Create a file in the cache directory
        var time = System.currentTimeMillis()
        val file = File(context.cacheDir, "img$time.jpg")
        return try {
            // Compress the bitmap and save it to the file
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun showDialog1(context: Context?) {
        val binding = LayoutThankYouBinding.inflate(LayoutInflater.from(context))
        val mBuilder = AlertDialog.Builder(context).setView(binding.root).create()
        mBuilder.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        mBuilder.setCancelable(false)
        mBuilder.show()
        binding.mOkay.setOnClickListener {
            mBuilder.dismiss()
            moveActivity(UnderReview())
            finishAffinity()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun BankDetailsUI() {
        val context = LocalContext.current

        BackHandler {
            if (intent.hasExtra("bank")) {
                startActivity(Intent(context, LoginScreen::class.java))
                finish()
            } else {
                viewModel.signupType.value = 2
            }
        }

        Column {
            Text(
                text = stringResource(R.string.enter_your_correct_bank_details_you_ll_receive_earning_in),
                color = colorResource(id = color.black_333333),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(font.montserrat_medium)),
                textAlign = TextAlign.Start,
            )
            VerticalSpacer(value = 10)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colorResource(id = color.white_F6F6F6), shape = RoundedCornerShape(10.dp)
                    )
                    .padding(all = 16.dp)
            ) {


                var holderName by remember { mutableStateOf("") }
                var bankName by remember { mutableStateOf("") }
                var accountNumber by remember { mutableStateOf("") }
                var confirmAccountNumber by remember { mutableStateOf("") }
                var bankCode by remember { mutableStateOf("") }



                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = holderName,
                    onValueChange = { holderName = it.take(50) },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(
                        fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))
                    ),
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Words, imeAction = ImeAction.Next
                    ),
                    placeholder = {
                        Text(
                            "Account Holder Name",
                            color = colorResource(id = color.gray_9D9D9D),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )

                    })

                VerticalSpacer(value = 12)

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = bankName,
                    onValueChange = { bankName = it.take(50) },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(
                        fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))
                    ),
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Words, imeAction = ImeAction.Next
                    ),
                    placeholder = {
                        Text(
                            "Bank Name",
                            color = colorResource(id = color.gray_9D9D9D),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                    })

                VerticalSpacer(value = 12)

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                    ),
                    textStyle = TextStyle(
                        fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))
                    ),
                    placeholder = {
                        Text(
                            "Account Number",
                            color = colorResource(id = color.gray_9D9D9D),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                    })

                VerticalSpacer(value = 12)

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = confirmAccountNumber,
                    onValueChange = { confirmAccountNumber = it.take(20) },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                    ),
                    textStyle = TextStyle(
                        fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))
                    ),
                    placeholder = {
                        Text(
                            "Confirm Account Number",
                            color = colorResource(id = color.gray_9D9D9D),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(
                                Font(R.font.montserrat_regular)
                            )
                        )

                    })

                VerticalSpacer(value = 12)

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = bankCode,
                    onValueChange = { bankCode = it.take(15) },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(
                        fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))
                    ),
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Characters, imeAction = ImeAction.Done
                    ),
                    placeholder = {
                        Text(
                            "Bank Code",
                            color = colorResource(id = color.gray_9D9D9D),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )

                    })

                VerticalSpacer(value = 30)

                Button(
                    onClick = {
                        if (validationBank(
                                holderName, bankName, accountNumber, confirmAccountNumber, bankCode
                            )
                        ) {
                            var requrest = AddBankRequest(
                                accHolderName = holderName,
                                bankAccountNo = accountNumber,
                                bankCode = bankCode,
                                bankName = bankName
                            )

                            Log.d("daya", "BankDetailsUI: $requrest")
                            viewModel.hitAddBank(
                                token = SharedPreference.get(this@SignupScreen).accessToken,
                                requrest
                            )
                            CustomLoader.showLoader(this@SignupScreen)

                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = color.green)
                    )
                ) {
                    Text(
                        text = stringResource(R.string._continue),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            111 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Permission denied
                    Toast.makeText(
                        this,
                        getString(R.string.please_allow_the_camera_permission_from_phone_settings),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun validationBank(
        holderName: String,
        bankName: String,
        accountNumber: String,
        confirmAccountNumber: String,
        bankCode: String
    ): Boolean {
        if (holderName.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_account_holder_name))
            return false
        } else if (bankName.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_bank_name))
            return false
        } else if (accountNumber.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_account_number))
            return false
        } else if (accountNumber.length < 7) {
            showToast(getString(R.string.please_enter_valid_account_number))
            return false
        } else if (accountNumber != confirmAccountNumber) {
            showToast(getString(R.string.account_number_and_confirm_account_number_should_be_same))
            return false
        } else if (confirmAccountNumber.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_confirm_account_holder_number))
            return false
        } else if (bankCode.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_bank_code))
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun VehicleDetailsUI(viewModel: M1ViewModel) {
        var vehicleNumber by remember { mutableStateOf("") }
        val nameV by viewModel.name.collectAsState()

        val scrollState = rememberScrollState()
        val maxChar = 50
        val context = LocalContext.current

        BackHandler {
            context.startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalSpacer(value = 10)
            Text(
                text = stringResource(R.string.select_a_vehicle_type_fill_other_vehicle_details),
                color = colorResource(id = color.black_333333),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(font.montserrat_medium)),
                textAlign = TextAlign.Start,
            )
            VerticalSpacer(value = 10)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        colorResource(id = color.white_F6F6F6), shape = RoundedCornerShape(10.dp)
                    )
                    .padding(all = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.vehicle_number),
                    color = colorResource(id = color.black_333333),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(font.montserrat_semibold)),
                    textAlign = TextAlign.Start
                )
                VerticalSpacer(value = 5)
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = nameV,
                    onValueChange = { newValue ->
                        viewModel.updateName(newValue)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(
                        fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))
                    ),
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Characters, imeAction = ImeAction.Done
                    ),
                    placeholder = {
                        Text(
                            "Vehicle Number",
                            color = colorResource(id = color.gray_9D9D9D),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                            )
                        )
                    })
                VerticalSpacer(value = 16)
                Text(
                    text = stringResource(R.string.registration_certification),
                    color = colorResource(id = color.black_333333),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(font.montserrat_semibold)),
                    textAlign = TextAlign.Start
                )
                VerticalSpacer(value = 5)
                val stroke = Stroke(
                    width = 5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                val context = LocalContext.current
                val galleryLauncher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->

                        uri?.let {
                            cropImage.launch(
                                options(uri = uri) {
                                    setGuidelines(CropImageView.Guidelines.ON)
                                    setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                                })
                        }
                    }

                val cameraLauncher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val image = result.data?.extras?.get("data") as Bitmap?

                            val imgUri = image?.let { getImageUri(it) }
                            cropImage.launch(
                                options(uri = imgUri) {
                                    setGuidelines(CropImageView.Guidelines.ON)
                                    setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                                })
                        }
                    }

                var showFrontImageV by remember { mutableStateOf(false) }
                var showBackImageV by remember { mutableStateOf(false) }
                var showRegistrationImage by remember { mutableStateOf(false) }

                if (showFrontImageV) {
                    showFrontImageV = previewImage(imageUrl = viewModel.mVehicleUrl2.value)
                }
                if (showBackImageV) {
                    showBackImageV = previewImage(imageUrl = viewModel.mVehicleUrl3.value)
                }

                if (showRegistrationImage) {
                    showRegistrationImage = previewImage(imageUrl = viewModel.mVehicleUrl1.value)
                }

                val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                pdfIntent.type = "application/pdf"
                pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color.White)
                        .drawBehind {
                            drawRoundRect(
                                color = Color.Green,
                                style = stroke,
                                cornerRadius = CornerRadius(10f)
                            )
                        }
                        .clickable {
                            if (viewModel.mVehicleUrl1.value.toString().contains("pdf")) {
                                startActivity(
                                    Intent(
                                        this@SignupScreen, PdfRenderActivity::class.java
                                    ).putExtra("url", viewModel.mVehicleUrl1.value.toString())
                                )
                            } else if (!viewModel.mVehicleUrl1.value.isNullOrEmpty()) {
                                showRegistrationImage = true
                            } else {
                                mImageType = 4

                                val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                                pdfIntent.type = "application/pdf"
                                pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)

                                openSheetCameraGallery(
                                    onCameraClick = { cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) },
                                    onGalleryClick = { galleryLauncher.launch("image/*") },
                                    onPdfClick = { pdfLauncher.launch(pdfIntent) })
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {

                        if (!viewModel.mVehicleUrl1.value.isNullOrEmpty()) {
                            Image(
                                painter = painterResource(id = R.drawable.cross),
                                contentDescription = "",
                                alignment = Alignment.TopEnd,
                                modifier = Modifier
                                    .zIndex(2f)
                                    .width(20.dp)
                                    .height(20.dp)
//                                .graphicsLayer(shadowElevation = 4f, shape = CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(
                                        x = (10).dp, y = -5.dp
                                    ) // Adjusting the position to overlap the first image's border
                                    .clickable {
                                        viewModel.mVehicleUrl1.value = ""
                                    })
                        } else if (!viewModel.mVehicleUrl1.value.isNullOrEmpty()) {
                            showFrontImageV = true
                        }

                        if (viewModel.mVehicleUrl1.value.isNullOrEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = drawable.upload_ic),
                                    contentDescription = ""
                                )
                                VerticalSpacer(value = 5)
                                Text(
                                    "Upload",
                                    color = colorResource(id = color.green),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(font.montserrat_regular))
                                )
                                Text(
                                    "Registration Certification",
                                    color = colorResource(id = color.gray_9D9D9D),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(font.montserrat_regular))
                                )
                            }
                        }

                        var pdfImg by remember { mutableStateOf<ImageBitmap?>(null) }

                        if (viewModel.mVehicleUrl1.value.toString().contains("pdf", true)) {
                            FetchPDFImageFromUrl(
                                viewModel.mVehicleUrl1.value, this@SignupScreen
                            ) { img ->
                                pdfImg = img
                            }
                            pdfImg?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            Image(
                                painter = rememberImagePainter(
                                    request = coil.request.ImageRequest.Builder(
                                        LocalContext.current
                                    ).data(viewModel.mVehicleUrl1.value).build()
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                VerticalSpacer(value = 18)

                Text(
                    text = stringResource(R.string.driving_license),
                    color = colorResource(id = color.black_333333),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(font.montserrat_semibold)),
                    textAlign = TextAlign.Start,
                )

                VerticalSpacer(value = 5)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .size(150.dp)
                            .padding(end = 4.dp)
                            .background(
                                color = colorResource(id = color.white),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .drawBehind {
                                drawRoundRect(
                                    color = Color.Green,
                                    style = stroke,
                                    cornerRadius = CornerRadius(10f)
                                )
                            }
                            .clickable {
                                if (viewModel.mVehicleUrl2.value.toString().contains("pdf")) {
                                    startActivity(
                                        Intent(
                                            this@SignupScreen, PdfRenderActivity::class.java
                                        ).putExtra(
                                            "url", viewModel.mVehicleUrl2.value.toString()
                                        )
                                    )
                                } else if (!viewModel.mVehicleUrl2.value.isNullOrEmpty()) {
                                    showFrontImageV = true
                                }
//                            else if (!viewModel.mVehicleUrl3.value.isNullOrEmpty()) {
//                                showBackImageV = true
//                            }
                                else {
                                    mImageType = 5

                                    val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                                    pdfIntent.type = "application/pdf"
                                    pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)

                                    openSheetCameraGallery(
                                        onCameraClick = { cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) },
                                        onGalleryClick = { galleryLauncher.launch("image/*") },
                                        onPdfClick = { pdfLauncher.launch(pdfIntent) })
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {

                            if (!viewModel.mVehicleUrl2.value.isNullOrEmpty()) {
                                Image(
                                    painter = painterResource(id = R.drawable.cross),
                                    contentDescription = "",
                                    alignment = Alignment.TopEnd,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .zIndex(2f)
                                        .width(20.dp)
                                        .height(20.dp)
//                                .graphicsLayer(shadowElevation = 4f, shape = CircleShape)
                                        .align(Alignment.TopEnd)
                                        .offset(
                                            x = (10).dp, y = -5.dp
                                        ) // Adjusting the position to overlap the first image's border
                                        .clickable {
                                            viewModel.mVehicleUrl2.value = ""
                                        })
                            } else if (!viewModel.mVehicleUrl2.value.isNullOrEmpty()) {
                                showFrontImageV = true
                            }

                            if (viewModel.mVehicleUrl2.value.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = drawable.upload_ic),
                                        contentDescription = ""
                                    )
                                    VerticalSpacer(value = 5)
                                    Text(
                                        "Upload",
                                        color = colorResource(id = color.green),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(font.montserrat_regular))
                                    )
                                    Text(
                                        "Front Side",
                                        color = colorResource(id = color.gray_9D9D9D),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(font.montserrat_regular))
                                    )
                                }
                            }

                            var pdfImg by remember { mutableStateOf<ImageBitmap?>(null) }

                            if (viewModel.mVehicleUrl2.value.toString().contains("pdf", true)) {
                                FetchPDFImageFromUrl(
                                    viewModel.mVehicleUrl2.value, this@SignupScreen
                                ) { img ->
                                    pdfImg = img
                                }
                                pdfImg?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                Image(
                                    painter = rememberImagePainter(
                                        request = coil.request.ImageRequest.Builder(
                                            LocalContext.current
                                        ).data(viewModel.mVehicleUrl2.value).build()
                                    ),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    //  Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .size(150.dp)
                            .padding(start = 4.dp)
                            .background(
                                color = colorResource(id = color.white),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .drawBehind {
                                drawRoundRect(
                                    color = Color.Green,
                                    style = stroke,
                                    cornerRadius = CornerRadius(10f)
                                )
                            }

                            .clickable {
                                if (viewModel.mVehicleUrl3.value.toString().contains("pdf")) {
                                    startActivity(
                                        Intent(
                                            this@SignupScreen, PdfRenderActivity::class.java
                                        ).putExtra(
                                            "url", viewModel.mVehicleUrl3.value.toString()
                                        )
                                    )
                                } else if (!viewModel.mVehicleUrl3.value.isNullOrEmpty()) {
                                    showBackImageV = true
                                } else {
                                    mImageType = 6

                                    val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                                    pdfIntent.type = "application/pdf"
                                    pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)

                                    openSheetCameraGallery(
                                        onCameraClick = { cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) },
                                        onGalleryClick = { galleryLauncher.launch("image/*") },
                                        onPdfClick = { pdfLauncher.launch(pdfIntent) })
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {
                            if (!viewModel.mVehicleUrl3.value.isNullOrEmpty()) {
                                Image(
                                    painter = painterResource(id = R.drawable.cross),
                                    contentDescription = "",
                                    alignment = Alignment.TopEnd,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .zIndex(2f)
                                        .width(20.dp)
                                        .height(20.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(
                                            x = (10).dp, y = -5.dp
                                        ) // Adjusting the position to overlap the first image's border
                                        .clickable {
                                            viewModel.mVehicleUrl3.value = ""
                                        })
                            } else if (!viewModel.mVehicleUrl3.value.isNullOrEmpty()) {
                                showBackImageV = true
                            }

                            if (viewModel.mVehicleUrl3.value.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Image(
                                        painter = painterResource(id = drawable.upload_ic),
                                        contentDescription = ""
                                    )
                                    VerticalSpacer(value = 5)
                                    Text(
                                        "Upload",
                                        color = colorResource(id = color.green),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(font.montserrat_regular))
                                    )
                                    Text(
                                        "Back Side",
                                        color = colorResource(id = color.gray_9D9D9D),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(font.montserrat_regular))
                                    )
                                }
                            }

                            var pdfImg by remember { mutableStateOf<ImageBitmap?>(null) }

                            if (viewModel.mVehicleUrl3.value.toString().contains("pdf", true)) {
                                FetchPDFImageFromUrl(
                                    viewModel.mVehicleUrl3.value, this@SignupScreen
                                ) { img ->
                                    pdfImg = img
                                }
                                pdfImg?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                Image(
                                    painter = rememberImagePainter(
                                        request = coil.request.ImageRequest.Builder(
                                            LocalContext.current
                                        ).data(viewModel.mVehicleUrl3.value).build()
                                    ),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
                VerticalSpacer(value = 18)
                Button(
                    onClick = {
                        if (vehicleValidation(
                                vehicleNumber = viewModel.name.value,
                                vehicleDoc1 = viewModel.mVehicleUrl1.value,
                                vehicleDoc2 = viewModel.mVehicleUrl2.value,
                                vehicleDoc3 = viewModel.mVehicleUrl3.value
                            )
                        ) {
                            val userRequest = AddVehicleRequest(
                                vechileName = viewModel.name.value,
                                RegistrationCertificateFront = viewModel.mVehicleUrl1.value,
                                drivingLicenseFront = viewModel.mVehicleUrl2.value,
                                drivingLicenseBack = viewModel.mVehicleUrl3.value
                            )

                            Log.d("daya", "VehicleDetailsUI: $userRequest")

                            viewModel.hitAddVehicle(
                                token = SharedPreference.get(this@SignupScreen).accessToken,
                                userRequest
                            )

                            CustomLoader.showLoader(this@SignupScreen)

                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = color.green))
                ) {
                    Text(
                        text = getString(R.string._continue),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(font.montserrat_regular))
                    )
                }
            }
        }
    }

    class UppercaseTransformation : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val transformedText = text.text.split(" ").joinToString(" ") { word ->
                if (word.isNotEmpty()) {
                    word.replaceFirstChar { it.uppercase() }
                } else {
                    word
                }
            }

            // Return the transformed text as an AnnotatedString with an identity offset mapping
            return TransformedText(
                AnnotatedString(transformedText), OffsetMapping.Identity
            )
        }
    }

    fun getIntentDat() {
        if (intent.hasExtra("basic")) {
            viewModel.updateSignupType(1)
        }
        if (intent.hasExtra("vehicle")) {
            viewModel.updateSignupType(2)
        }
        if (intent.hasExtra("bank")) {
            viewModel.updateSignupType(3)
        }
    }

    private fun vehicleValidation(
        vehicleNumber: String, vehicleDoc1: String, vehicleDoc2: String, vehicleDoc3: String
    ): Boolean {
        if (viewModel.name.value.toString().trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_vehicle_number))
            return false
        } else if (vehicleDoc1.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_upload_registration_certification))
            return false
        } else if (vehicleDoc2.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_upload_front_side_driving_license))
            return false
        } else if (vehicleDoc3.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_upload_back_side_driving_license))
            return false
        }
        return true
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun selectLanguageLayout(
        mProfileImgUrl: String,
        mBasicFrontUrl: String,
        mBasicBackUrl: String,
        name: String,
        phone: String,
        email: String,
        gender: String,
        dob: String,
        dobTimeStamp: String,
        password: String,
        onPasswordChange: (String) -> Unit,
        confrimPassword: String,
        onConfrimPasswordChange: (String) -> Unit,
        countryCode: String,
        isAgreeTerm: Boolean,
        onIsAgreeTermChange: (Boolean) -> Unit,
        selectLang: String,
        mSelectedLanguageType: (String) -> Unit,
        onChangeLanguage: (String) -> Unit,
        onIsSmokeChange1: (Boolean) -> Unit,

        ) {
        var passwordVisible by remember { mutableStateOf(false) }
        var passwordVisible1 by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = color.white_F6F6F6),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(top = 20.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.enter_language),
                    color = colorResource(id = color.black_333333),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(font.montserrat_semibold))
                )
                VerticalSpacer(value = 10)

                TextField(
                    value = selectLang,
                    onValueChange = { mSelectedLanguageType(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    placeholder = {
                        Text(
                            getString(R.string.enter_language),
                            fontFamily = FontFamily(Font(font.montserrat_regular)),
                            fontSize = 12.sp,
                            color = colorResource(id = color.gray_9D9D9D),
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily(Font(font.montserrat_regular)),
                        fontSize = 12.sp,
                        color = colorResource(id = color.black_333333)
                    ),

                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Words, imeAction = ImeAction.Next
                    )
                )

//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(60.dp)
//                        .padding(top = 10.dp)
//                        .background(
//                            color = colorResource(id = color.white),
//                            shape = RoundedCornerShape(10.dp)
//                        )
//                        .clickable(
//                            indication = null,
//                            interactionSource = remember { MutableInteractionSource() })
//                        {
//                            selectLanguageSheet(
//                                mSelectedLanguageType = { mSelectedLanguageType(it) },
//                                mSelectedLanguage = { onChangeLanguage(it) },
//                            )
//                        }
//                        .padding(horizontal = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                )
//                {
//
//                    Text(
//                        selectLang,
//                        fontFamily = FontFamily(Font(font.montserrat_regular)),
//                        fontSize = 14.sp,
//                        color = colorResource(id = color.black_333333)
//                    )
//
//                    Image(
//                        painter = painterResource(id = drawable.arrow_drop_down),
//                        contentDescription = ""
//                    )
//
//
//                }
                VerticalSpacer(12)
                Text(
                    text = stringResource(R.string.upload_id_proof),
                    color = colorResource(id = color.black_333333),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(font.montserrat_semibold))
                )
                VerticalSpacer(12)
                UploadFrontBackDocument(
                    mBasicFrontUrl = mBasicFrontUrl, mBasicBackUrl = mBasicBackUrl,
                )
                VerticalSpacer(12)
                TextField(
                    value = password,
                    onValueChange = {
                        onPasswordChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.create_password),
                            color = colorResource(id = color.gray_9D9D9D),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily(Font(font.montserrat_regular)),
                        fontSize = 12.sp,
                        color = colorResource(id = color.black_161616),
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        val icon = if (passwordVisible) {
                            painterResource(id = R.drawable.visibility_on) // please change icon
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                    )
                )
                VerticalSpacer(value = 12)
                TextField(
                    value = confrimPassword,
                    onValueChange = {
                        onConfrimPasswordChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.confirm_password),
                            color = colorResource(id = color.gray_9D9D9D),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily(Font(font.montserrat_regular)),
                        fontSize = 12.sp,
                        color = colorResource(id = color.black_161616)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        val icon = if (passwordVisible1) {
                            painterResource(id = R.drawable.visibility_on) // please change icon
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                    )
                )

                VerticalSpacer(value = 12)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.do_you_smoke),
                        color = colorResource(id = color.black_161616),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(font.montserrat_semibold))
                    )
                    ToggleSwitch(onIsSmokeChange = { onIsSmokeChange1(it) })

                }
                CustomCheckbox(onIsAgreeTermChange = { onIsAgreeTermChange(it) })
                VerticalSpacer(20)
                Button(
                    onClick = {
                        if (basicValidation(
                                mProfileImgUrl = mProfileImgUrl,
                                mBasicFrontUrl = mBasicFrontUrl,
                                mBasicBackUrl = mBasicBackUrl,
                                name = name,
                                phone = phone,
                                email = email,
                                gender = gender,
                                dobTimeStamp = dobTimeStamp,
                                selectLang = selectLang,
                                password = password,
                                confrimPassword = confrimPassword,
                                isTermCheck = isAgreeTerm
                            )
                        ) {
                            var genderType = 0
                            when (gender) {
                                "Male" -> genderType = 1
                                "Female" -> genderType = 2
                                else -> genderType = 3
                            }
                            //BasicClick
                            var requrest = RegisterRequest(
                                name = name,
                                profileImage = mProfileImgUrl,
                                countryCode = countryCode,
                                email = email,
                                language = selectLang,
                                dob = dobTimeStamp,
                                mobileNumber = phone,
                                deviceToken = "a",
                                deviceType = 1,
                                lat = 0.0,
                                long = 0.0,
                                isSmoke = 0,
                                gender = genderType,
                                password = password,
                                documents = RegisterRequest.Documents(
                                    mBasicFrontUrl, mBasicBackUrl
                                ),
//                                idProofFront = mBasicFrontUrl,
//                                idProofBack = mBasicBackUrl
                            )

                            SharedPreference.get(this@SignupScreen).name = name
                            Log.d("daya", "Data Request: $requrest")
                            viewModel.progress.value = true

                            viewModel.hitRegister(requrest)
                            //  moveTo=true
                            CustomLoader.showLoader(this@SignupScreen)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = color.green)
                    )
                ) {
                    Text(
                        text = stringResource(R.string._continue),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(font.montserrat_regular))
                    )
                }

            }

        }
    }


    private fun basicValidation(
        mProfileImgUrl: String,
        mBasicFrontUrl: String,
        mBasicBackUrl: String,
        name: String,
        phone: String,
        email: String,
        gender: String,
        dobTimeStamp: String,
        selectLang: String,
        password: String,
        confrimPassword: String,
        isTermCheck: Boolean,
    ): Boolean {

        if (mProfileImgUrl.isNullOrEmpty()) {
            showToastLong(getString(R.string.please_upload_profile_picture_in_png_jpg_and_jpeg_format))
            return false
        } else if (name.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_full_name))
            return false
        } else if (phone.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_mobile_number))
            return false

        } else if (phone.length < 7) {
            showToastLong(getString(R.string.mobile_number_should_be_of_minimum_7_digits_long))
            return false
        } else if (email.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_email_address))
            return false
        } else if (!email.trim().matches(Constant.EMAIL_PATTERN.toRegex())) {
            showToast(getString(R.string.please_enter_valid_email_address))
            return false

        } else if (gender == getString(R.string.select_gender)) {
            showToast(getString(R.string.please_select_gender))
            return false

        } else if (dobTimeStamp == "") {
            showToast(getString(R.string.please_enter_dob))
            return false

        } else if (selectLang == "Select Language") {
            showToast(getString(R.string.please_select_language))
            return false

        } else if (mBasicFrontUrl == "") {
            showToast(getString(R.string.please_upload_front_side_id_proof))
            return false
        } else if (mBasicBackUrl == "") {
            showToast(getString(R.string.please_upload_back_side_id_proof))
            return false
        } else if (password.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_password))
            return false
        } else if (!password.matches(Constant.PASSWORD_PATTERN.toRegex())) {
            ShowCustomToast(getString(R.string.password_must_be_7_15_characters_long_with_1_uppercase_1_lowercase_1_special_character_number))
            return false
        } else if (confrimPassword.trim().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_confirm_password))
            return false
        } else if (password != confrimPassword) {
            showToast(getString(R.string.password_and_confirm_password_should_be_same))
            return false
        } else if (!isTermCheck) {
            showToast(getString(R.string.please_accept_terms_conditions))
            return false
        }

        return true
    }


    fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver ?: return null
        var time = System.currentTimeMillis()
        val tempFile = File(context.cacheDir, "img$time")
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    fun uriToFile1(context: Context, uri: Uri, fileName: String): File? {
        val file = File(context.cacheDir, fileName)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream: OutputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(4 * 1024) // Buffer size
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                    output.flush()
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun convertFileToMultipartBody(partName: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun convertFileToMultipartBody1(partName: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun SignupScreenContent(viewModel: M1ViewModel) {
        val showPress by viewModel.progress.observeAsState()

        showProgress(showPress ?: false)

        val context = LocalContext.current
        val signupType by viewModel.signupType.observeAsState(initial = 1)
        val mAddVehicleResp by viewModel.mAddVehicleResp.observeAsState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White),
        ) {
            TitleLayout()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(15.dp)
                    .background(Color.White)
            ) {
                /* mAddVehicleResp?.message?.let {
                     Text(
                         text = it,
                         color = colorResource(id = color.gray_9D9D9D),
                         fontSize = 12.sp,
                         fontFamily = FontFamily(Font(font.montserrat_regular)),
                         textAlign = TextAlign.Start
                     )
                 }
 */
                Text(
                    text = stringResource(R.string.hey_it_s_so_simple_to_create_a_new_account_just_in_3_steps),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    color = colorResource(
                        id = color.gray_9D9D9D
                    ),
                    fontFamily = FontFamily(Font(R.font.montserrat_regular))
                )
                progressLayout(signupType ?: 1)
                VerticalSpacer(value = 20)
                when (signupType) {
                    1 -> {
                        basicDetailsUI(viewModel)
                    }

                    2 -> {
                        VehicleDetailsUI(viewModel)
                    }

                    3 -> BankDetailsUI()
                    else -> {
                        basicDetailsUI(viewModel)
                    }
                }

            }

        }
    }

    fun openSheetCameraGallery1(
        onCameraClick: () -> Unit, onGalleryClick: () -> Unit, onPdfClick: (() -> Unit)? = null
    ) {
        val dialog = BottomSheetDialog(this)
        val binding = LayoutCameraGallery1Binding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()
        binding.mCamera.setOnClickListener {
            onCameraClick()
            dialog.dismiss()
        }
        binding.mGallery.setOnClickListener {
            onGalleryClick()
            dialog.dismiss()
        }

        binding.mPdf.setOnClickListener {
            if (onPdfClick != null) {
                onPdfClick()
            }
            dialog.dismiss()
        }
    }

    fun openSheetCameraGallery(
        onCameraClick: () -> Unit, onGalleryClick: () -> Unit, onPdfClick: (() -> Unit)? = null
    ) {
        val dialog = BottomSheetDialog(this)
        val binding = LayoutCameraGalleryBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()
        binding.mCamera.setOnClickListener {
            onCameraClick()
            dialog.dismiss()
        }
        binding.mGallery.setOnClickListener {
            onGalleryClick()
            dialog.dismiss()
        }

        binding.mPdf.setOnClickListener {
            if (onPdfClick != null) {
                onPdfClick()
            }
            dialog.dismiss()
        }
    }

    fun selectGenderSheet(
        onGenderChangeValue: (String) -> Unit,
        onMaleClick: (String) -> Unit,
        onFemaleClick: (String) -> Unit,
        onOtherClick: (String) -> Unit
    ) {
        val dialog = BottomSheetDialog(this)
        val binding = LayoutSelectGenderBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()
        binding.mMale.setOnClickListener {
            onMaleClick("Male")
            onGenderChangeValue("1")
            dialog.dismiss()
        }
        binding.mFemale.setOnClickListener {
            onFemaleClick("Female")
            onGenderChangeValue("2")

            dialog.dismiss()
        }
        binding.mOther.setOnClickListener {
            onOtherClick("Other")
            onGenderChangeValue("3")

            dialog.dismiss()
        }
    }


    fun selectLanguageSheet(
        mSelectedLanguageType: (String) -> Unit, mSelectedLanguage: (String) -> Unit
    ) {
        val dialog = BottomSheetDialog(this)
        val binding = LayoutSelectLanguageBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()
        binding.mArabic.setOnClickListener {
            mSelectedLanguageType("ar")
            mSelectedLanguage("Arabic")
            dialog.dismiss()
        }
        binding.mEnglish.setOnClickListener {
            mSelectedLanguageType("en")
            mSelectedLanguage("English")
            dialog.dismiss()
        }


    }


    @Composable
    private fun basicDetailsUI(viewModel: M1ViewModel) {
        val scrollState = rememberScrollState()
        var fullName by remember { mutableStateOf("") }
        var mobileNumber by remember { mutableStateOf("") }
        var countryCode by remember { mutableStateOf("") }
        var emailAddress by remember { mutableStateOf("") }
        var selectGender by remember { mutableStateOf(getString(R.string.select_gender)) }
        var selectGenderType by remember { mutableStateOf("0") }
        var enterDOB by remember { mutableStateOf("Enter DOB") }
        var dobTimeStamp by remember { mutableStateOf("") }
        var selectLangType by remember { mutableStateOf("") }
        var selectLang by remember { mutableStateOf("") }
        var createPassword by remember { mutableStateOf("") }
        var confrimPassword by remember { mutableStateOf("") }
        var isSmoke by remember { mutableStateOf(false) }
        var isAgreeTerm by remember { mutableStateOf(false) }


        val maxchar = 50
        val cameraLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val image = result.data?.extras?.get("data") as Bitmap?

                    var imgUri = image?.let { getImageUri(it) }
                    cropImage.launch(
                        options(uri = imgUri) {
                            setGuidelines(CropImageView.Guidelines.ON)
                            setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                        })
                }
            }

        val galleryLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->

                uri?.let {
                    cropImage.launch(
                        options(uri = uri) {
                            setGuidelines(CropImageView.Guidelines.ON)
                            setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                        })/*   val imageFile = uriToFile(context, uri)
                       imageFile?.let {
                           val multipartBody = convertFileToMultipartBody("upload_delivery_file", imageFile)
                           viewModel.uploadFile(multipartBody) }*/

                }
            }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CaptureProfilePhoto()
                Image(
                    painter = painterResource(id = drawable.add_upload),
                    contentDescription = "",
                    modifier = Modifier
                        .offset(y = -15.dp)
                        .clickable {
                            if (checkPermissions()) {
                                mImageType = 1
                                openSheetCameraGallery(
                                    onCameraClick = {
                                        cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                                    },
                                    onGalleryClick = { galleryLauncher.launch("image/*") },
                                    onPdfClick = {})

                            } else {
                                requestPermissions()
                            }
                        })
            }

            VerticalSpacer(value = 10)
            fillDetailsLayout(
                fullName = fullName,
                onFullNameChange = { fullName = it.take(maxchar) },
                mobileNumber = mobileNumber,
                onMobileNumberChange = { mobileNumber = it.take(15) },
                emailAddress = emailAddress,
                onEmailAddressChange = { emailAddress = it },
                selectGender = selectGender,
                onGenderChange = { selectGender = it },
                onGenderChangeType = { selectGenderType = it },
                enterDOB = enterDOB,
                onDobChange = { enterDOB = changeDateFormat(it) },
                selectLang = selectLang,
                onLangChange = { selectLang = it },
                uploadFrontDoc = viewModel.mBasicFrontUrl.value,
                onUploadFrontDocChange = { },
                uploadBacDoc = viewModel.mBasicBackUrl.value,
                onUploadBacDocChange = { },
                createPassword = createPassword,
                onCreatePasswordChange = { createPassword = it },
                confrimPassword = confrimPassword,
                onConfrimPasswordChange = { confrimPassword = it },
                isSmoke = isSmoke,
                onIsSmokeChange = { isSmoke = it },
                isAgreeTerm = isAgreeTerm,
                onIsAgreeTermChange = { isAgreeTerm = it },
                onCountryCodeChange = { countryCode = it },
                onDobChangeTimeStamp = { dobTimeStamp = it },


                )

            VerticalSpacer(value = 20)
            selectLanguageLayout(
                mProfileImgUrl = viewModel.mProfileImgUrl.value,
                mBasicFrontUrl = viewModel.mBasicFrontUrl.value,
                mBasicBackUrl = viewModel.mBasicBackUrl.value,
                name = fullName,
                phone = mobileNumber,
                email = emailAddress,
                gender = selectGender,
                dob = enterDOB,
                password = createPassword,
                confrimPassword = confrimPassword,
                onPasswordChange = { createPassword = it },
                onConfrimPasswordChange = { confrimPassword = it },
                countryCode = countryCode,
                isAgreeTerm = isAgreeTerm,
                onIsAgreeTermChange = { isAgreeTerm = it },
                selectLang = selectLang,
                mSelectedLanguageType = { selectLang = it },
                onChangeLanguage = { selectLang = it },
                onIsSmokeChange1 = { isSmoke = it }, dobTimeStamp = dobTimeStamp,

                )
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 111
            )
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 111
            )
        }
    }


    @Composable
    fun progressLayout(signupType: Int) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 0.dp, start = 25.dp, end = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = if (signupType == 1) R.drawable.progress_bar_a else if (signupType == 2) R.drawable.progress_bar_b else R.drawable.progress_bar_c),
                        contentDescription = ""
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = stringResource(R.string.basic_details),
                    color = colorResource(id = color.black_333333),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(font.montserrat_medium)),
                    textAlign = TextAlign.Center,
                )
                Text(

                    text = stringResource(R.string.vehicle_details),
                    color = colorResource(id = color.black_333333),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(font.montserrat_medium)),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.bank_details),
                    color = colorResource(id = color.black_333333),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(font.montserrat_medium)),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }


    @Composable
    fun CustomCheckbox(onIsAgreeTermChange: (Boolean) -> Unit) {
        var isChecked by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(20.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        isChecked = !isChecked
                        if (isChecked == true) {
                            onIsAgreeTermChange(true)
                        } else {
                            onIsAgreeTermChange(false)
                        }
                    },
                painter = if (isChecked) painterResource(id = drawable.checked_mark) else painterResource(
                    id = drawable.check_mark
                ),
                contentDescription = if (isChecked) "Checked" else "Unchecked"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.i_accept_the_terms_conditions),
                fontSize = 12.sp,
                color = colorResource(id = color.gray_9D9D9D),
                style = TextStyle(
                    fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))
                )
            )
        }
    }

    @Composable
    fun ToggleSwitch(onIsSmokeChange: (Boolean) -> Unit) {
        var isChecked by remember { mutableStateOf(false) }
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                if (isChecked == true) {
                    onIsSmokeChange(true)
                } else {
                    onIsSmokeChange(false)
                }

            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(id = color.thumb_color),
                checkedTrackColor = colorResource(id = color.thumb_bg),
                uncheckedThumbColor = Color(0xFFBDBDBD),
                uncheckedTrackColor = Color(0xFFE0E0E0),
                uncheckedBorderColor = Color.Transparent
            ),
        )
    }

    fun mDateToTimestamp(value: String): Long {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val mDate = sdf.parse(value) as Date
        return mDate.time
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun fillDetailsLayout(
        fullName: String,
        onFullNameChange: (String) -> Unit,
        mobileNumber: String,
        onMobileNumberChange: (String) -> Unit,
        emailAddress: String,
        onEmailAddressChange: (String) -> Unit,
        selectGender: String,
        onGenderChange: (String) -> Unit,
        onGenderChangeType: (String) -> Unit,
        enterDOB: String,
        onDobChange: (String) -> Unit,
        onDobChangeTimeStamp: (String) -> Unit,
        selectLang: String,
        onLangChange: (String) -> Unit,
        uploadFrontDoc: String,
        onUploadFrontDocChange: (String) -> Unit,
        uploadBacDoc: String,
        onUploadBacDocChange: (String) -> Unit,
        createPassword: String,
        onCreatePasswordChange: (String) -> Unit,
        confrimPassword: String,
        onConfrimPasswordChange: (String) -> Unit,
        isSmoke: Boolean,
        onIsSmokeChange: (Boolean) -> Unit,
        isAgreeTerm: Boolean,
        onIsAgreeTermChange: (Boolean) -> Unit,
        onCountryCodeChange: (String) -> Unit,
    ) {

        val myFontFamily = FontFamily(
            Font(R.font.montserrat_medium)
        )

        val myTextStyle = TextStyle(
            fontFamily = myFontFamily, fontSize = 16.sp, color = Color.Black
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = color.white_F6F6F6),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(top = 20.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = fullName,
                    onValueChange = onFullNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.enter_full_name),
                            fontFamily = FontFamily(Font(font.montserrat_regular)),
                            fontSize = 12.sp,
                            color = colorResource(id = color.gray_9D9D9D),
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily(Font(font.montserrat_regular)),
                        fontSize = 12.sp,
                        color = colorResource(id = color.black_333333)
                    ),

                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )
                VerticalSpacer(12)
                Row(
                    modifier = Modifier.background(
                        Color.White, shape = RoundedCornerShape(10.dp)
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextField(
                        value = mobileNumber,
                        onValueChange = onMobileNumberChange,
                        textStyle = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.enter_mobile_number),
                                color = colorResource(id = R.color.gray_9D9D9D),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Start,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.montserrat_regular))
                                ),
                            )

                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Box {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    DialogCountryPicker(
                                        defaultCountryIdentifier = "kw",
                                        pickedCountry = { onCountryCodeChange(it.countryCode) },
                                        countryCodeTextColorAndIconColor = colorResource(id = color.black_161616),
                                        trailingIconComposable = {},
                                        isCircleShapeFlag = false,
                                        isCountryFlagVisible = false,
                                        isEnabled = false
                                    )
                                    VerticalDivider(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .padding(vertical = 8.dp),
                                        color = colorResource(id = R.color.line_color)
                                    )
                                    Spacer(
                                        modifier = Modifier.padding(
                                            horizontal = 5.dp, vertical = 5.dp
                                        )
                                    )

                                }
                            }
                        })
                }

                VerticalSpacer(12)
                TextField(
                    value = emailAddress,
                    onValueChange = onEmailAddressChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.email_address),
                            fontFamily = FontFamily(Font(font.montserrat_regular)),
                            fontSize = 12.sp,
                            color = colorResource(id = color.gray_9D9D9D)
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily(Font(font.montserrat_regular)),
                        fontSize = 12.sp,
                        color = colorResource(id = color.black_333333)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                    )
                )
                VerticalSpacer(12)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp)
                        .clickable {
                            selectGenderSheet(
                                onGenderChangeValue = { onGenderChangeType(it) },
                                onMaleClick = { onGenderChange(it) },
                                onFemaleClick = { onGenderChange(it) },
                                onOtherClick = { onGenderChange(it) })
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        selectGender,
                        fontFamily = FontFamily(Font(font.montserrat_regular)),
                        fontSize = 12.sp,
                        color = colorResource(id = color.black_333333)
                    )
                    Image(
                        painter = painterResource(id = drawable.arrow_drop_down),
                        contentDescription = ""
                    )
                }
                VerticalSpacer(12)

                val context = LocalContext.current

                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        onDobChange("$dayOfMonth/${month + 1}/$year")
                        if (dayOfMonth < 10) {
                            if (month < 10) {
                                onDobChange("0$dayOfMonth/0${month + 1}/$year")
                                val dobTimeStamp =
                                    mDateToTimestamp("0$dayOfMonth/0${month + 1}/$year")
                                onDobChangeTimeStamp(dobTimeStamp.toString())
                            } else {
                                onDobChange("0$dayOfMonth/${month + 1}/$year")
                                val dobTimeStamp =
                                    mDateToTimestamp("0$dayOfMonth/${month + 1}/$year")
                                onDobChangeTimeStamp(dobTimeStamp.toString())
                            }
                        } else {
                            if (month < 10) {
                                onDobChange("$dayOfMonth/0${month + 1}/$year")
                                val dobTimeStamp =
                                    mDateToTimestamp("$dayOfMonth/0${month + 1}/$year")
                                onDobChangeTimeStamp(dobTimeStamp.toString())
                            } else {
                                onDobChange("$dayOfMonth/${month + 1}/$year")
                                val dobTimeStamp =
                                    mDateToTimestamp("$dayOfMonth/${month + 1}/$year")
                                onDobChangeTimeStamp(dobTimeStamp.toString())
                            }
                        }


                    }, year, month, day
                )
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp)
                        .clickable {
                            datePickerDialog.show()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {


                    DatePickerText(enterDOB, datePickerDialog)
                    Image(
                        painter = painterResource(id = drawable.calendar),
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            datePickerDialog.show()
                        })
                }
                VerticalSpacer(12)
            }
        }
    }

    @Composable
    fun DatePickerText(enterDOB: String, datePickerDialog: DatePickerDialog) {
        Text(
            text = enterDOB,
            modifier = Modifier
                .clickable {
                    datePickerDialog.show()
                }
                .padding(top = 16.dp, bottom = 16.dp, start = 0.dp, end = 16.dp),
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                fontSize = 12.sp,
                color = colorResource(id = R.color.black_161616)
            ))
    }

    @Composable
    fun TitleLayout() {/*val lifecycleOwner = LocalLifecycleOwner.current
        val onBackPressedDispatcherOwner = lifecycleOwner as? OnBackPressedDispatcherOwner

        val backCallback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    if (viewModel.signupType.value==3){
                        viewModel.signupType.value=2
                    }
                    if (viewModel.signupType.value==2){
                        finish()
                    }
                }
            }
        }

        DisposableEffect(onBackPressedDispatcherOwner) {
            onBackPressedDispatcherOwner?.onBackPressedDispatcher?.addCallback(
                lifecycleOwner,
                backCallback
            )

            onDispose {
                backCallback.remove()
            }
        }*/


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(colorResource(id = color.status_bar_gray))
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = drawable.back_ic),
                contentDescription = "",
                Modifier.clickable {
                    onBackPressed()
                })
            Text(
                text = stringResource(R.string.sign_up),
                color = colorResource(id = color.black_333333),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(font.montserrat_semibold)),
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
            Image(
                painter = painterResource(drawable.back_button),
                contentDescription = "",
                colorFilter = ColorFilter.tint(colorResource(id = color.status_bar_gray)),
                modifier = Modifier.clickable {
                    //this@SignupScreen.onBackPressedDispatcher.onBackPressed()
                })
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

    /*@Composable
    private fun previewImage(imageUrl: String): Boolean {
        var showImageDialog by remember { mutableStateOf(true) }
        AnimatedVisibility(
            visible = showImageDialog,

            ) {

            Dialog(onDismissRequest = {}) {
                ElevatedCard(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(11.dp),
                    colors = CardColors(
                        containerColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Black,
                        contentColor = Color.Black
                    ), elevation = CardDefaults.cardElevation(9.dp, 9.dp, 9.dp, 9.dp, 9.dp, 9.dp)

                )
                {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Image(painter = painterResource(id = R.drawable.cross),
                            contentDescription = "",
                            alignment = Alignment.TopEnd,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .graphicsLayer(shadowElevation = 4f, shape = CircleShape)
                                .padding(10.dp)
                                .width(12.dp).height(12.dp)
                                .align(Alignment.TopEnd)
                                .clickable {
                                    showImageDialog = false
                                })

                    }
                }
            }

        }
        return showImageDialog
    }*/

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun previewImage(imageUrl: String): Boolean {
        // State to control visibility of the image dialog
        var showImageDialog by remember { mutableStateOf(true) }

        val systemUiController: SystemUiController = rememberSystemUiController()
        systemUiController.isStatusBarVisible = false
        systemUiController.setStatusBarColor(Color.Transparent)

        // Full-screen dialog with animation
        AnimatedVisibility(
            visible = showImageDialog, modifier = Modifier.background(color = Color.White)
        ) {
            Dialog(
                onDismissRequest = { showImageDialog = false }, // Allow dismissing the dialog
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                )
            ) {
                // Full-screen card container
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White), // Occupy entire screen
                    shape = RoundedCornerShape(0.dp), // No rounded corners
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color.White
                    ), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    // Box to hold the image and toolbar
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White) // Background color
                    ) {
                        // Display the image
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        // Custom toolbar with close button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorResource(id = R.color.white)) // Toolbar background with transparency
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Title text (if needed)
                            Text(
                                text = "",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                                modifier = Modifier.padding(start = 8.dp)
                            )

                            // Close button
                            Image(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "",
                                alignment = Alignment.TopEnd,
                                modifier = Modifier
                                    .width(35.dp)
                                    .height(35.dp)
                                    .clickable {
                                        showImageDialog = false
                                    })
                        }
                    }
                }
            }
        }

        return showImageDialog
    }

    private fun FetchPDFImageFromUrl(
        pdfUrl: String, context: Context, onImageLoaded: (ImageBitmap) -> Unit
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            try {

                val url = URL(pdfUrl)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()

                // Create a temporary file to store the PDF data
                val tempFile = File.createTempFile("temp_pdf", ".pdf")
                val outputStream = FileOutputStream(tempFile)
                val buffer = ByteArray(1024)
                var bytesRead = inputStream.read(buffer)
                while (bytesRead != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    bytesRead = inputStream.read(buffer)
                }
                outputStream.close()

                // Open the PDF using PdfRenderer
                val parcelFileDescriptor: ParcelFileDescriptor =
                    ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                val pageCount = pdfRenderer.pageCount
                val bitmap = Bitmap.createBitmap(
                    100, 100, Bitmap.Config.ARGB_8888
                ) // adjust bitmap size as needed
                val renderer = pdfRenderer.openPage(0)
                renderer.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                renderer.close()
                pdfRenderer.close()
                parcelFileDescriptor.close()

                // Delete the temporary file
                tempFile.delete()

                onImageLoaded(bitmap.asImageBitmap())
            } catch (e: Exception) {

            }
        }
    }
}







