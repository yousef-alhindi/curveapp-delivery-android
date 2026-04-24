package com.curve.delivery.ui.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.curve.delivery.MyApplication
import com.curve.delivery.R
import com.curve.delivery.new_architecture.helper.InternetConnection
import com.curve.delivery.util.showToast
import com.curve.delivery.util.showToastC
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import kotlinx.coroutines.delay

class PdfRenderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
          //  if(InternetConnection.checkConnection(MyApplication.appContext)){
                showPdf(intent.getStringExtra("url").toString())
          //  }else{
           //     showToastC(MyApplication.appContext,"Please check your internet connection and try again")
           // }
        }
    }

    @Composable
    private fun showPdf(url: String) {
        if (!url.isNullOrEmpty()) {
            // State to manage loading state
            var isLoading by remember { mutableStateOf(true) }

            // Simulate a loading period
            LaunchedEffect(url) {
                delay(2000) // Simulate a delay for loading, adjust as necessary
                isLoading = false
            }

            // PDF reader state
            val pdfState = rememberVerticalPdfReaderState(
                resource = ResourceType.Remote(url),
                isZoomEnable = true
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(top = 20.dp)
            ) {
                // Close Icon
                Image(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .zIndex(2f)
                        .align(Alignment.TopEnd) // Aligns the icon at the top-end of the Box
                        .padding(16.dp) // Add padding to avoid overlap with the status bar and edges
                        .size(35.dp) // Set a consistent size for the cross icon
                        .clickable {
                            onBackPressed()
                        }
                )

                if(InternetConnection.checkConnection(MyApplication.appContext)){
                    VerticalPDFReader(
                        state = pdfState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(top = 15.dp)
                    )
                }else{
                    showToastC(MyApplication.appContext,
                        stringResource(R.string.please_check_your_internet_connection_and_try_again)
                    )
                }

                // PDF Reader

                // Loader
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.White), // Semi-transparent background
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = colorResource(id = R.color.green), // Customize color if needed
                            strokeWidth = 4.dp
                        )
                    }
                }
            }
        }
    }
}
