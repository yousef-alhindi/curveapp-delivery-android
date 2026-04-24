package com.curve.delivery.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.curve.delivery.R
import com.curve.delivery.response.AutocompletePrediction
import com.curve.delivery.util.ApiInterface
import com.curve.delivery.util.RetrofitClient.placesService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            greenStatusBarApp1()
                Surface(modifier = Modifier.fillMaxSize(), ) {
                    LocationSearchScreen()
                }
        }
    }

    @Composable
    fun greenStatusBarApp1() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Optional: Change the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.hhhh)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview()
    @Composable
    fun LocationSearchScreen() {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(2f)
                ) {
                    Box (modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color(0xFFF7F7F7))){

                        Image(
                            painter = painterResource(id = R.drawable.back_ic),
                            contentDescription = "",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .clickable { this@LocationSearchActivity.finish() }
                                .padding(start = 16.dp) // Add padding for better touch targets
                        )


                        androidx.compose.material.Text(
                            text = stringResource(R.string.location),
                            color = colorResource(id = R.color.black),
                            fontSize = 14.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(fontFamily = FontFamily(Font(R.font.montserrat_semibold))),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .wrapContentHeight())
                    }
                    TabRowDefaults.Divider(
                        color = Color(0xFFF7F7F7), // Set a shadow color
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(modifier = Modifier.padding(15.dp)) {
                    SomeUIComponent()
                    Spacer(modifier = Modifier.height(10.dp))
                    ColoredDivider()
                    CurrentLocationOption()
                   // RecentSearches()
                   // SomeUIComponent()
                }
            }
    }
    @Composable
    fun SomeUIComponent() {
        val apiKey = "AIzaSyDArJiWk0n8FTou8_czStx6cr9mTv0M28E"
        PlacesAutocompleteTextField(placesService, apiKey)
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlacesAutocompleteTextField(placesService: ApiInterface, apiKey: String) {
        Column {
            var query by remember { mutableStateOf("") }
            var suggestions by remember { mutableStateOf<List<AutocompletePrediction>>(listOf()) }
            val coroutineScope = rememberCoroutineScope()
            TextField(
                value = query,
                onValueChange = {
                    query = it
                    Log.d("TextFieldUpdate", "Current query: $query")
                    // Added length check to reduce API hits
                        coroutineScope.launch {
                            try {
                                val response = placesService.autocompletePlaces(query, apiKey)
                                suggestions = response.predictions

                                Log.e("response", " ${suggestions}")

                            } catch (e: Exception) {
                                // Handle exceptions like network issues here
                                Log.e(
                                    "AutocompleteError",
                                    "Failed to fetch places: ${e.localizedMessage}"
                                )
                            }
                        }

                },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search Icon", tint = Color.Gray)
                },

                label = { Text(stringResource(R.string.search_location), fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))) },
                placeholder = { Text("",fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_regular))) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp)
                    ),  // Adjust to match your background color exactly
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedLeadingIconColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent))
            var lat = 0.0
            var lng = 0.0
            AnimatedVisibility(visible = suggestions.isNotEmpty()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0XFF33BD8C), shape = RoundedCornerShape(10.dp))
                    .padding(vertical = 10.dp)) {
                LazyColumn {
                    items(suggestions) { suggestion ->
                        Spacer(modifier = Modifier.height(10.dp))

                            Text(suggestion.description, fontFamily = FontFamily(Font(R.font.montserrat_semibold)), color = Color.White, fontSize = 16.sp, modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .clickable {
                                    // Handle click, maybe fill the TextField with this suggestion
                                    query = suggestion.description

                                    var placeId = suggestion.place_id

                                    Log.d("TAG", "PlacesAutocompleteTextField:place Id $placeId ")
                                    CoroutineScope(Dispatchers.Default).launch {
                                        val response =
                                            placesService.getPlaceDetails(placeId, apiKey)
                                        lat = response.result?.geometry?.location?.lat ?: 0.0
                                        lng = response.result?.geometry?.location?.lng ?: 0.0

                                        HomeScreen.lat = lat
                                        HomeScreen.lng = lng

                                        Log.d(
                                            "TAG",
                                            "PlacesAutocompleteTextField: location $lat, $lng"
                                        )
                                        suggestions = listOf()
                                        startActivity(
                                            Intent(
                                                this@LocationSearchActivity, HomeScreen::class.java
                                            )
                                                .putExtra("key", query)
                                                .putExtra("lat", lat.toString())
                                                .putExtra("lng", lng.toString())

                                        )
                                    }
                                })
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier
            .padding(start = 0.dp, top = 6.dp, end = 6.dp, bottom = 6.dp)
            .clickable {
                val intent = Intent(this, CurrentLocationActivity::class.java)
                startActivity(intent)
            }){
            Image(painter = painterResource(id = R.drawable.ord_location_navigation), contentDescription = "")
            Text(text = stringResource(R.string.use_my_current_location), fontFamily = FontFamily(Font(R.font.montserrat_semibold)), fontSize = 12.sp,
                color = Color.Black)
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchBar() {
        var textState = remember { mutableStateOf(TextFieldValue("")) }

        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "", tint = Color.Gray)
            },
            placeholder = { Text(stringResource(R.string.search_location)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0)),  // Adjust to match your background color exactly
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedLeadingIconColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedPlaceholderColor = Color.Gray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent

            )
        )
    }

    @Composable
    fun CurrentLocationOption() {
        val context = LocalContext.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Implement your location logic */ }
                .padding(vertical = 12.dp)
        ) {
//            Icon(
//                Icons.Filled.LocationOn,
//                contentDescription = "Locate Me",
//                tint = Color.Gray,
//                modifier = Modifier.size(24.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
          /*  RecentSearchesHeader( onRowClick = {
                val intent = Intent(context, CurrentLocationActivity::class.java)
                context.startActivity(intent)
            })*/
            //Text("Use my Current Location", color = Color.Black)
        }
    }

    @Composable
    fun RecentSearchesHeader(onRowClick: () -> Unit) {
        Row(
            modifier = Modifier
                .clickable(onClick = onRowClick)
                .fillMaxWidth()
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.recent_searches),
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_semibold))
            )
            TextButton(
                onClick = { /* TODO: Implement the clear action */ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    stringResource(R.string.clear), color = colorResource(id = R.color.app_color),
                    fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
            }
        }
    }

    @Composable
    fun RecentSearches() {
        Column {
            // Sample list, replace with your actual data and logic
            listOf("XYZ Street, Dubai", "XYZ Street, Dubai", "XYZ Street, Dubai").forEach { location ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = "Locate Me",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(location, color = Color.Black,modifier = Modifier.weight(1f), fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium))
                    )
                    IconButton(onClick = { /* Handle remove */ }) {
                        Image(painter = painterResource(id = R.drawable.cross), contentDescription = "")
                       // Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Color.Black)
                    }
                }
            }

        }
    }
    @Composable
    fun ColoredDivider() {
        // Convert hex color to Compose color format
        val lineColor = Color(android.graphics.Color.parseColor("#F6F6F6"))

        Divider(
            color = lineColor,
            thickness =4.dp
        )
    }
}