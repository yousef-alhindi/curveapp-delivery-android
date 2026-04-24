package com.curve.delivery.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.abi.simplecountrypicker.DialogCountryPicker
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.curve.delivery.MyApplication
import com.curve.delivery.R
import com.curve.delivery.databinding.LayoutCameraGallery1Binding
import com.curve.delivery.databinding.LayoutCameraGalleryBinding
import com.curve.delivery.databinding.LayoutSelectGenderBinding
import com.curve.delivery.model.OTPVerifyRequest
import com.curve.delivery.new_architecture.helper.CommonUtils
import com.curve.delivery.new_architecture.helper.CommonUtils.changeDateFormat
import com.curve.delivery.new_architecture.helper.CustomLoader
import com.curve.delivery.response.GetProfileDetailsResponse
import com.curve.delivery.response.UpdateProfileRequest
import com.curve.delivery.ui.login.PdfRenderActivity
import com.curve.delivery.ui.signup.startActivity
import com.curve.delivery.util.SharedPreference
import com.curve.delivery.util.checkPermissions
import com.curve.delivery.util.showProgress
import com.curve.delivery.util.showToast
import com.curve.delivery.util.showToastC
import com.curve.delivery.viewModel.M1ViewModel
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

fun convertFileToMultipartBody(partName: String, file: File): MultipartBody.Part {
    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
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

fun Context.getImageUri(inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        this.contentResolver, inImage, "IMG_${System.currentTimeMillis()}", null)
    return path?.let { Uri.parse(it) }
}

private fun requestPermissions(context: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
            context, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_EXTERNAL_STORAGE), 111)
    } else {
        ActivityCompat.requestPermissions(
            context, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE), 111)
    }
}

fun openSheetCameraGallery(context: Activity,onCameraClick: () -> Unit,
                           onGalleryClick: () -> Unit,
                           onPdfClick: (() -> Unit)?
) {
    val dialog = BottomSheetDialog(context)
    val binding = LayoutCameraGalleryBinding.inflate(context.layoutInflater)
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


fun openSheetCameraGallery1(context: Activity,onCameraClick: () -> Unit,
                            onGalleryClick: () -> Unit,
                            onPdfClick: (() -> Unit)? = null) {
    val dialog = BottomSheetDialog(context)
    val binding = LayoutCameraGallery1Binding.inflate(context.layoutInflater)
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

private fun FetchPDFImageFromUrl(
    pdfUrl: String,
    context: Context,
    onImageLoaded: (ImageBitmap) -> Unit
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
                100,
                100,
                Bitmap.Config.ARGB_8888
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

@Composable
fun UploadFrontBackDocument(
    mBasicFrontUrl: String,
    mBasicBackUrl: String,
    viewModel: M1ViewModel,isEdit: Boolean) {

    val context = LocalContext.current
    val cropImage =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                val imageFile = result.uriContent?.let { uriToFile(context, it) }

                val maxFileSizeInBytes = 10 * 1024 * 1024

                imageFile?.let { file ->
                    val fileSizeInBytes = imageFile.length()
                    if (fileSizeInBytes > maxFileSizeInBytes) {
                        // File size exceeds 10 MB
                        showToastC(context, context.getString(R.string.the_file_size_is_too_large))
                    } else {
                        if (viewModel.checkInternetConnection()) {
                            val multipartBody = convertFileToMultipartBody("upload_delivery_file", file)
                            viewModel.uploadFile(multipartBody)
                            //  viewModel.progress.value = true
                        } else {
                            viewModel.progress.value = false
                        }
                    }

                }
            } else {
                // Handle the error
                val exception = result.error
                exception?.printStackTrace()
            }
        }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            cropImage.launch(options(uri = uri) {
                setGuidelines(CropImageView.Guidelines.ON)
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            })
        }
    }
    var docName: String = ""
    val activity = LocalContext.current as Activity

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { pdfUri ->
            var docFile: MultipartBody.Part? = null

            if (pdfUri.toString().startsWith("content://")) {
                activity.contentResolver.query(pdfUri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val docName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                        // Take persistable URI permission
                        activity.contentResolver.takePersistableUriPermission(
                            pdfUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                        val requestBody = object : RequestBody() {
                            override fun contentType(): MediaType? = "application/pdf".toMediaTypeOrNull()

                            override fun writeTo(sink: BufferedSink) {
                                activity.contentResolver.openInputStream(pdfUri)?.use { inputStream ->
                                    sink.writeAll(inputStream.source())
                                } ?: throw IOException("Could not open $pdfUri")
                            }
                        }

                        // Create MultipartBody
                        docFile = MultipartBody.Part.createFormData(
                            "upload_delivery_file", docName, requestBody
                        )

                        if (viewModel.checkInternetConnection()) {
                            viewModel.uploadFile(docFile)
                            // Show loader (if applicable)
                        } else {
                            Toast.makeText(
                                activity, activity.getString(R.string.please_check_your_internet_connection_and_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val image = result.data?.extras?.get("data") as Bitmap?

                var imgUri = image?.let { context?.getImageUri(it) }
                cropImage.launch(
                    options(uri = imgUri) {
                        setGuidelines(CropImageView.Guidelines.ON)
                        setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                    }
                )
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
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp))
    {
        Column(modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .size(150.dp)
            .padding(end = 4.dp)
            .background(
                color = colorResource(id = R.color.white), shape = RoundedCornerShape(10.dp)
            )
            .drawBehind {
                drawRoundRect(
                    color = Color.Green, style = stroke, cornerRadius = CornerRadius(10f)
                )
            }
            .clickable {
                if (mBasicFrontUrl
                        .toString()
                        .contains("pdf")
                ) {
                    context?.startActivity(
                        Intent(context, PdfRenderActivity::class.java).putExtra(
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
                    viewModel.mImageType.value = 2
                    val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    pdfIntent.type = "application/pdf"
                    pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    openSheetCameraGallery(
                        activity,
                        onCameraClick = {
                            cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
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
                    Image(painter = painterResource(id = R.drawable.cross),
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
                            )
                            .clickable {
                                if (isEdit) {
                                    viewModel.mBasicFrontUrl.value = ""
                                }
                            })
                } else if (!mBasicFrontUrl.isNullOrEmpty()) {
                    showFrontImage = true
                }
                if (viewModel.mBasicFrontUrl.value.isNullOrEmpty()) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.upload_ic), contentDescription = "")
                        VerticalSpacer(value = 5)
                        Text(
                            "Upload",
                            color = colorResource(id = R.color.green),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                        Text(
                            "Front Side",
                            color = colorResource(id = R.color.gray_9D9D9D),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                    }
                }
                var pdfImg by remember { mutableStateOf<ImageBitmap?>(null) }

                if (mBasicFrontUrl.toString().contains("pdf", true)) {
                    FetchPDFImageFromUrl(mBasicFrontUrl, context) { img ->
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
        Column(modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .size(150.dp)
            .padding(start = 4.dp)
            .background(
                color = colorResource(id = R.color.white), shape = RoundedCornerShape(10.dp)
            )
            .drawBehind {
                drawRoundRect(
                    color = Color.Green, style = stroke, cornerRadius = CornerRadius(10f)
                )
            }

            .clickable {
                if (mBasicBackUrl.toString().contains("pdf")) {
                    context?.startActivity(
                        Intent(
                            context, PdfRenderActivity::class.java
                        ).putExtra(
                            "url", mBasicBackUrl.toString()
                        )
                    )
                } else if (!mBasicBackUrl.isNullOrEmpty()) {
                    showBackImage = true
                } else {
                    viewModel.mImageType.value = 3
                    val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    pdfIntent.type = "application/pdf"
                    pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)

                    openSheetCameraGallery(
                        activity,
                        onCameraClick = {
                            cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                        },
                        onGalleryClick = { galleryLauncher.launch("image/*") },
                        onPdfClick = { pdfLauncher.launch(pdfIntent) })
                }
            },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()) {
                if (!mBasicBackUrl.isNullOrEmpty()) {
                    Image(painter = painterResource(id = R.drawable.cross),
                        contentDescription = "",
                        alignment = Alignment.TopEnd,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .zIndex(2f)
                            .width(20.dp)
                            .height(20.dp)
                            .align(Alignment.TopEnd)
                            .offset(
                                x = (10).dp,
                                y = -5.dp
                            ) // Adjusting the position to overlap the first image's border
                            .clickable {
                                if (isEdit) {
                                    viewModel.mBasicBackUrl.value = ""
                                }
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
                            painter = painterResource(id = R.drawable.upload_ic),
                            contentDescription = ""
                        )
                        VerticalSpacer(value = 5)
                        Text(
                            "Upload",
                            color = colorResource(id = R.color.green),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                        Text(
                            "Back Side",
                            color = colorResource(id = R.color.gray_9D9D9D),
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_regular))
                        )
                    }
                }

                var pdfImg by remember { mutableStateOf<ImageBitmap?>(null) }

                if (mBasicBackUrl.toString().contains("pdf", true)) {
                    FetchPDFImageFromUrl(mBasicBackUrl, context) { img ->
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

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun previewImage(imageUrl: String): Boolean {
    // State to control visibility of the image dialog
    var showImageDialog by remember { mutableStateOf(true) }

    val systemUiController: SystemUiController = rememberSystemUiController()
    systemUiController.isStatusBarVisible = false
    systemUiController.setStatusBarColor(Color.Transparent)

    // Full-screen dialog with animation
    AnimatedVisibility(visible = showImageDialog, modifier = Modifier.background(color= Color.White)) {
        Dialog(
            onDismissRequest = { showImageDialog = false }, // Allow dismissing the dialog
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
            )
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                        Image(painter = painterResource(id = R.drawable.ic_close),
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

@Composable
fun CaptureProfilePhoto(viewModel: M1ViewModel,isEdit: Boolean) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val cropImage =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                val imageFile = result.uriContent?.let { uriToFile(context, it) }
                imageFile?.let { file ->
                    val maxFileSizeInBytes = 10 * 1024 * 1024
                    val fileSizeInBytes = imageFile.length()
                    if (fileSizeInBytes > maxFileSizeInBytes) {
                        // File size exceeds 10 MB
                        showToastC(context, context.getString(R.string.file_size_is_too_large))
                    } else {
                        if (viewModel.checkInternetConnection()) {
                            val multipartBody = convertFileToMultipartBody("upload_delivery_file", file)
                            viewModel.uploadFile(multipartBody)
                            // viewModel.progress.value = true
                        } else {
                            viewModel.progress.value = false
                        }
                    }
                }
            } else {
                // Handle the error
                val exception = result.error
                exception?.printStackTrace()
            }
        }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            cropImage.launch(options(uri = uri) {
                setGuidelines(CropImageView.Guidelines.ON)
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            })
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val image = result.data?.extras?.get("data") as Bitmap?
            var imgUri = image?.let { context.getImageUri(it) }
            cropImage.launch(options(uri = imgUri) {
                setGuidelines(CropImageView.Guidelines.ON)
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            })
        }
    }

    Box(modifier = Modifier.clickable {
        activity?.let {
            if (it.checkPermissions()) {

                openSheetCameraGallery(activity,onCameraClick = {
                    cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                }, onGalleryClick = { galleryLauncher.launch("image/*") }, onPdfClick = {})

            } else {
                requestPermissions(activity)
            }
        }
    }) {
        when {
            viewModel.mProfileImgUrl.value!= "" -> Image(painter = rememberImagePainter(
                request = coil.request.ImageRequest.Builder(LocalContext.current)
                    .data(viewModel.mProfileImgUrl.value)
                    .placeholder(R.drawable.profile_placeholder).build()),

                contentDescription = null, modifier = Modifier
                    .size(100.dp)
                    .clickable {
                        if (isEdit) {
                            activity?.let {
                                if (it.checkPermissions()) {
                                    viewModel.mImageType.value = 1
                                    openSheetCameraGallery1(
                                        activity,
                                        onCameraClick = {
                                            cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                                        },
                                        onGalleryClick = { galleryLauncher.launch("image/*") },
                                        onPdfClick = {})
                                } else {
                                    requestPermissions(activity)
                                }
                            }
                        }

                    }
                    .clip(CircleShape), contentScale = ContentScale.Crop)


            else -> Image(
                painter = painterResource(id = R.drawable.profile_placeholder),
                contentDescription = null)
        }
    }
}


//view section with edit
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PeronslDetailsScreen(viewModel: M1ViewModel) {
    val context = LocalContext.current

    var isEdit by remember { mutableStateOf(false) }
    val getPersonalDetails by viewModel.getProfileDetailsResponse.observeAsState()
    val uploadDoc by viewModel.mUploadImageResp.observeAsState()
    val updateProfile by viewModel.updateProfileResponse.observeAsState()

    Log.d("TAG", "PeronslDetailsScreen: $getPersonalDetails")

    val showPress by viewModel.progress.observeAsState()
    showProgress(showPress ?: false)

    LaunchedEffect(Unit) {
        viewModel.progress.value = true
        viewModel.getProfileDetails(SharedPreference.get(context).accessToken)
    }

    if(uploadDoc?.success==true){
        uploadDoc?.success=false
        Log.d("TAG", "PeronslDetailsScreen:Upload ${uploadDoc?.data}")
        when (viewModel.mImageType.value) {
            1 -> viewModel.mProfileImgUrl.value = uploadDoc?.data?:""
            2->viewModel.mBasicFrontUrl.value= uploadDoc?.data?:""
            3->viewModel.mBasicBackUrl.value= uploadDoc?.data?:""
            4->viewModel.mPoliceBacjground.value=uploadDoc?.data?:""
            else-> viewModel.mProfileImgUrl.value = uploadDoc?.data?:""
        }
    }

    if(updateProfile?.success==true){
        Log.d("TAG", "PeronslDetailsScreen: hvgs")
        isEdit=false
        updateProfile?.success =false
     //   viewModel.progress.value = true
        SharedPreference.get(context).profileName= updateProfile?.data?.name?:""
        SharedPreference.get(context).profileImage= updateProfile?.data?.profileImage?:""
        viewModel.getProfileDetails(SharedPreference.get(context).accessToken)
    }


    if(getPersonalDetails!=null) {
        val scrollState = rememberScrollState()
        var fullName by remember { mutableStateOf(getPersonalDetails?.data?.name ?: "") }
        var mobileNumber by remember {
            mutableStateOf(getPersonalDetails?.data?.mobileNumber ?: "")
        }

        var selectGenderName by remember { mutableStateOf("") }
        when(getPersonalDetails?.data?.gender?:""){
            1->selectGenderName="Male"
            2->selectGenderName="Female"
            else->selectGenderName="Other"
        }

        var countryCode by remember { mutableStateOf("") }
        var emailAddress by remember { mutableStateOf(getPersonalDetails?.data?.email?:"") }
        var selectGender by remember { mutableStateOf(selectGenderName?:"") }
        var selectGenderType by remember { mutableStateOf(getPersonalDetails?.data?.gender?:"") }
        var enterDOB by remember { mutableStateOf(CommonUtils.formatTimestampToDateString(getPersonalDetails?.data?.dob?.toLong()?:0L)) }
        var dobTimeStamp by remember { mutableStateOf(getPersonalDetails?.data?.dob?:"") }
        var selectLangType by remember { mutableStateOf("") }
        var selectLang by remember { mutableStateOf(getPersonalDetails?.data?.language?:"") }
        var createPassword by remember { mutableStateOf("") }
        var confrimPassword by remember { mutableStateOf("") }
        var isSmoke by remember { mutableStateOf(false) }
        var isAgreeTerm by remember { mutableStateOf(false) }
        val activity = context as? ComponentActivity

        LaunchedEffect(key1 =Unit) {
            viewModel.mProfileImgUrl.value = getPersonalDetails?.data?.profileImage ?: ""
            viewModel.mBasicFrontUrl.value = getPersonalDetails?.data?.documents?.idProofFront ?: ""
            viewModel.mBasicBackUrl.value = getPersonalDetails?.data?.documents?.idProofBack ?: ""
        }

        val cropImage =
            rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
                if (result.isSuccessful) {
                    val imageFile = result.uriContent?.let { uriToFile(context, it) }
                    imageFile?.let { file ->
                        val maxFileSizeInBytes = 10 * 1024 * 1024
                        val fileSizeInBytes = imageFile.length()
                        if (fileSizeInBytes > maxFileSizeInBytes) {
                            // File size exceeds 10 MB
                            showToastC(context, context.getString(R.string.file_size_is_too_large))
                        } else {
                            if (viewModel.checkInternetConnection()) {
                                val multipartBody = convertFileToMultipartBody("upload_delivery_file", file)
                                viewModel.uploadFile(multipartBody)
                                //viewModel.progress.value = true
                            } else {
                                viewModel.progress.value = false
                            }
                        }
                    }
                } else {
                    val exception = result.error
                    exception?.printStackTrace()
                }
            }

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                cropImage.launch(options(uri = uri) {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                })
            }
        }
        var docName: String = ""

        val pdfLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { pdfUri ->
                var docFile: MultipartBody.Part? = null

                if (pdfUri.toString().startsWith("content://")) {
                    context?.contentResolver?.query(pdfUri, null, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            docName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                            context?.contentResolver?.takePersistableUriPermission(
                                pdfUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                            val requestBody = object : RequestBody() {
                                override fun contentType(): MediaType? =
                                    "application/pdf".toMediaTypeOrNull()

                                override fun writeTo(sink: BufferedSink) {
                                    context?.contentResolver?.openInputStream(pdfUri)
                                        ?.use { inputStream ->
                                            sink.writeAll(inputStream.source())
                                        } ?: throw IOException("Could not open $pdfUri")
                                }
                            }

                            docFile = MultipartBody.Part.createFormData(
                                "upload_delivery_file", docName, requestBody)

                            if (viewModel.checkInternetConnection()) {
                                viewModel.uploadFile(docFile)
                                // Show loader
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please check your internet connection and try again",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val image = result.data?.extras?.get("data") as Bitmap?
                var imgUri = image?.let { context.getImageUri(it) }
                cropImage.launch(options(uri = imgUri) {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                })
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {

                var imageIcon=0
                var imageType ="edit"
                if(isEdit){
                    imageIcon = R.drawable.camera_ic
                    imageType ="camera"
                }else{
                    imageIcon = R.drawable.prfl_edit_profile
                    imageType="editKey"
                }

                CaptureProfilePhoto(viewModel,isEdit)
                Image(painter = painterResource(id = imageIcon),
                    contentDescription = "",
                    modifier = Modifier
                        .offset(y = -15.dp)
                        .clickable {
                            if (isEdit) {
                                activity?.let {
                                    if (it.checkPermissions()) {
                                        openSheetCameraGallery1(
                                            activity,
                                            onCameraClick = {
                                                cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                                            },
                                            onGalleryClick = { galleryLauncher.launch("image/*") },
                                            onPdfClick = {})

                                    } else {
                                        requestPermissions(context)
                                    }
                                }
                            }
                            isEdit = true
                        })
            }

            VerticalSpacer(value = 10)

            fillDetailsLayout(
                isEdit,
                getPersonalDetails,
                viewModel,
                fullName = fullName,
                onFullNameChange = { fullName = it.take(50) },
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
                onDobChangeTimeStamp = {
                    dobTimeStamp = it },
            )
        }

    }
}

fun selectGenderSheet(
    context: Activity,
    onGenderChangeValue: (String) -> Unit,
    onMaleClick: (String) -> Unit,
    onFemaleClick: (String) -> Unit,
    onOtherClick: (String) -> Unit) {
    val dialog = BottomSheetDialog(context)
    val binding = LayoutSelectGenderBinding.inflate(context.layoutInflater)
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


val hh = mutableStateOf(false)

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fillDetailsLayout(
    isEdit:Boolean,
    getProfileResponse:GetProfileDetailsResponse?,
    viewModel: M1ViewModel,
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

    val context = LocalContext.current
    fun mDateToTimestamp(value: String): Long {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val mDate = sdf.parse(value) as Date
        return mDate.time
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
                fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                fontSize = 12.sp,
                color = colorResource(id = R.color.black_161616)))
    }


    val myFontFamily = FontFamily(
        Font(R.font.montserrat_medium))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.white_F6F6F6), shape = RoundedCornerShape(10.dp)
            )
            .padding(top = 20.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.background(
                    Color.White, shape = RoundedCornerShape(10.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start) {
                TextField(value = mobileNumber,
                    onValueChange = onMobileNumberChange,
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    placeholder = {
                        Text(
                            text =mobileNumber,
                            color = colorResource(id = R.color.gray_9D9D9D),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_semibold))), )

                    },
                    shape = RoundedCornerShape(10.dp),
                    readOnly = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next),
                    leadingIcon = {
                        Box {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DialogCountryPicker(
                                    defaultCountryIdentifier = "kw",
                                    pickedCountry = { onCountryCodeChange(it.countryCode) },
                                    countryCodeTextColorAndIconColor = colorResource(id = R.color.black_161616),
                                    trailingIconComposable = {},
                                    isCircleShapeFlag = false,
                                    isCountryFlagVisible = false,
                                    isEnabled = false)
                                VerticalDivider(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .padding(vertical = 8.dp),
                                    color = colorResource(id = R.color.line_color))
                                Spacer(
                                    modifier = Modifier.padding(
                                        horizontal = 5.dp,
                                        vertical = 5.dp))

                            }
                        }
                    })
            }

            VerticalSpacer(12)

            TextField(
                value = fullName,
                onValueChange = onFullNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                readOnly = if(isEdit) false else true,
                placeholder = {
                    Text(fullName,
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.gray_9D9D9D),)
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.black_333333)),

                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next))
            VerticalSpacer(12)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp)
                    .clickable {
                        if (isEdit) {
                            selectGenderSheet((context as Activity), onGenderChangeValue = {
                                onGenderChangeType(it)
                            }, onMaleClick = {
                                onGenderChange(it)
                            }, onFemaleClick = {
                                onGenderChange(it)
                            }, onOtherClick = {
                                onGenderChange(it)
                            })
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(selectGender.toString(),
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.black_333333))

                if(isEdit) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow_drop_down),
                        contentDescription = "")
                }
            }
            VerticalSpacer(12)

            TextField(
                value = emailAddress,
                onValueChange = onEmailAddressChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                readOnly = if(isEdit) false else true,
                placeholder = {
                    Text(
                        emailAddress,
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.gray_9D9D9D))
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.black_333333)),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))

            VerticalSpacer(12)

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            var dobTimeStamp =0L

            val datePickerDialog =
                DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    onDobChange("$dayOfMonth/${month + 1}/$year")
                    if (dayOfMonth < 10) {
                        if (month < 10) {
                            onDobChange("0$dayOfMonth/0${month + 1}/$year")
                            dobTimeStamp = mDateToTimestamp("0$dayOfMonth/0${month + 1}/$year")
                            onDobChangeTimeStamp(dobTimeStamp.toString())
                        } else {
                            onDobChange("0$dayOfMonth/${month + 1}/$year")
                            val dobTimeStamp = mDateToTimestamp("0$dayOfMonth/${month + 1}/$year")
                            onDobChangeTimeStamp(dobTimeStamp.toString())
                        }
                    } else {
                        if (month < 10) {
                            onDobChange("$dayOfMonth/0${month + 1}/$year")
                            val dobTimeStamp = mDateToTimestamp("$dayOfMonth/0${month + 1}/$year")
                            onDobChangeTimeStamp(dobTimeStamp.toString())
                        } else {
                            onDobChange("$dayOfMonth/${month + 1}/$year")
                            val dobTimeStamp = mDateToTimestamp("$dayOfMonth/${month + 1}/$year")
                            onDobChangeTimeStamp(dobTimeStamp.toString())
                        }
                    }

                }, year, month, day)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp)
                    .clickable {
                        if (isEdit) {
                            datePickerDialog.show()
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {

                DatePickerText(enterDOB, datePickerDialog)

                if(isEdit) {
                    Image(painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            datePickerDialog.show()
                        })
                }
            }
            VerticalSpacer(12)
            TextField(
                value = selectLang,
                onValueChange = { onLangChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                readOnly = if(isEdit) false else true,
                placeholder = {
                    Text(
                        selectLang,
                        fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.gray_9D9D9D),
                    )
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.black_333333)),

                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    KeyboardCapitalization.Words, imeAction = ImeAction.Next))

            VerticalSpacer(12)
            Text(
                "ID Proof",
                color = colorResource(id = R.color.black_333333),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
            VerticalSpacer(12)
            UploadFrontBackDocument(
                mBasicFrontUrl = viewModel.mBasicFrontUrl.value, mBasicBackUrl = viewModel.mBasicBackUrl.value,
                viewModel = viewModel,isEdit
            )
            VerticalSpacer(12)
            /* Text(
                 "Police Background",
                 color = colorResource(id = R.color.black_333333),
                 fontSize = 14.sp,
                 fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
             VerticalSpacer(12)
             UploadFrontBackDocumentPoliceBg(
                 mBasicFrontUrl = viewModel.mPoliceBacjground.value,
                 viewModel = viewModel
             )
             VerticalSpacer(12)*/

            if(isEdit) {
                Button(
                    onClick = {
                        var dob = 0L
                        if (enterDOB != null) {
                            dob = CommonUtils.dateToTimestamp(enterDOB)
                        } else {
                            dob = 0L
                        }
                        var genderType = 0
                        when (selectGender) {
                            "Male" -> genderType = 1
                            "Female" -> genderType = 2
                            else -> genderType = 3
                        }
                        Log.d("TAG", "fillDetailsLayout:Details ${CommonUtils.dateToTimestamp(enterDOB)},$selectGender,$enterDOB,${viewModel.mProfileImgUrl.value}")
                        val model = UpdateProfileRequest(
                            dob.toString(),
                            emailAddress,
                            genderType,
                            selectLang,
                            fullName,
                            viewModel.mProfileImgUrl.value,
                            UpdateProfileRequest.Documents(
                                viewModel.mBasicBackUrl.value,
                                viewModel.mBasicFrontUrl.value,
                                viewModel.mPoliceBacjground.value))
                        viewModel.updateProfile(SharedPreference.get(context).accessToken, model)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                    shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF33BD8C),
                    )) {
                    Text(
                        stringResource(R.string.save),
                        fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                        fontSize = 14.sp)
                }

            }

        }
    }
}



