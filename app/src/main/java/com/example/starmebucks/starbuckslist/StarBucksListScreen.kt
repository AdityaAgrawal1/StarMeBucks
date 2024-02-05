package com.example.starmebucks.starbuckslist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.starmebucks.model.Starbucks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarbucksListScreen(
    navController: NavController,
    latitude:Double,
    longitude:Double
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "Starbucks near you")
                },
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(
                    color = Color.White
                )
        ) {
            StarbucksList(navController = navController, latitude,longitude)
        }
    }
}
fun calculateDistanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val originLat = Math.toRadians(lat1)
    val destinationLat = Math.toRadians(lat2)

    val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(originLat) * cos(destinationLat)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return (earthRadiusKm * c)
}

@Composable
fun StarbucksList(
    navController: NavController,
    latitude: Double,
    longitude: Double,
    viewModel: StarBucksListViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        viewModel.getStarbucksList(latitude, longitude)
    }

    val starbucksList by viewModel.starbucksList.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val loadError by viewModel.loadError.observeAsState("")

    LazyColumn(contentPadding = PaddingValues(16.dp)) {

        val items = starbucksList?.results ?: listOf()
        items(items.size) { index ->
            val starbucksPlace = items[index]
            val starbuck = Starbucks(
                name = starbucksPlace?.name ?: "",
                distance = calculateDistanceInKm(
                    latitude,
                    longitude,
                    starbucksPlace?.geometry?.location?.lat as Double,
                    starbucksPlace.geometry.location.lng as Double).toFloat(),
                address = starbucksPlace.vicinity ?: "",
                imageUrl = starbucksPlace.icon?:"",
                isOpen = if(starbucksPlace.openingHours?.openNow == true) "open now" else "closed"?:"",
                rating = (starbucksPlace.rating as Double),
                lat = starbucksPlace.geometry.location.lat.toDouble(),
                lng = starbucksPlace.geometry.location.lng.toDouble()
            )

            StarbucksCardView(
                entry = starbuck,
                navController = navController
            )
        }
    }

    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.Blue)
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.getStarbucksList(latitude,longitude)
            }
        }
    }
}


@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun StarbucksCardView(
    entry: Starbucks,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
            .clickable {
                navController.navigate(
                    "map_screen/${entry.lat}/${entry.lng}/${entry.name}"
                )
                       },
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .build(),
                contentDescription = entry.name,
                modifier = Modifier
                    .size(80.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = entry.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Blue
                )
                Text(
                    text = "Distance: ${String.format("%.2f",entry.distance)} km | " +
                            entry.isOpen,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rating: ${entry.rating}",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700)
                    )
                }
                Text(
                    text = "Address: ${entry.address}",
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 14.sp
                )
            }
        }
    }
}