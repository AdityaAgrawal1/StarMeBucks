
package com.example.starmebucks.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.starmebucks.mapscreen.MapScreen
import com.example.starmebucks.starbuckslist.StarbucksListScreen
import com.example.starmebucks.ui.theme.StarMeBucksTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            StarMeBucksTheme {
                LocationPermissionScreen(viewModel = viewModel)
            }
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
            else -> {
                viewModel.setLocationPermissionGranted(true)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                print("LatLng::")
                print(location)
                location?.let {
                    viewModel.setLatLong(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener {}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            val isGranted =
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            viewModel.setLocationPermissionGranted(
                isGranted
            )
        }
    }

    @Composable
    fun LocationPermissionScreen(viewModel: MainViewModel) {
        val context = LocalContext.current
        val locationPermissionGranted by viewModel.locationPermissionGranted.observeAsState()
        val longitude by viewModel.longitude.observeAsState()

        if (locationPermissionGranted == true) {
            fetchLastKnownLocation()
        }

        when (locationPermissionGranted) {
            true -> {
                if (longitude != 0.0) App()
                else Text("Fetching Location...")
            }

            false, null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("You need to give location permission to use this app.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        })
                    }) {
                        Text("Open Settings")
                    }
                }
            }
        }
    }


    @Composable
    fun App() {
        val navController = rememberNavController()
        val latitude by viewModel.latitude.observeAsState()
        val longitude by viewModel.longitude.observeAsState()
        NavHost(
            navController = navController,
            startDestination = "starbucks_list_screen"
        ) {
            composable("starbucks_list_screen") {
                StarbucksListScreen(
                    latitude = latitude?:0.0,
                    longitude = longitude?:0.0,
                    navController = navController
                )

            }
            composable(
                "map_screen/{lat}/{lng}/{name}",
                arguments = listOf(
                    navArgument("lat") {
                        type = NavType.FloatType
                    },
                    navArgument("lng") {
                        type = NavType.FloatType
                    },
                    navArgument("name") {
                        type = NavType.StringType
                    }
                )
            ) {
                val lat = remember {
                    it.arguments?.getFloat("lat")
                }
                val lng = remember {
                    it.arguments?.getFloat("lng")
                }
                val name = remember {
                    it.arguments?.getString("name")
                }
                MapScreen(
                    lat = lat ?: 0.0,
                    lng = lng ?: 0.0,
                    name = name?:"",
                    navController = navController
                )
            }
        }
    }
}


