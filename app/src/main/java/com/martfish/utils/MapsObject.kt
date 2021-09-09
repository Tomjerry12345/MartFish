package com.martfish.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.martfish.BuildConfig
import com.martfish.utils.Constant.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION

object Maps {
    private lateinit var activity: AppCompatActivity
    private lateinit var placesClient: PlacesClient
    @SuppressLint("StaticFieldLeak")
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var locationPermissionGranted = false

    fun initMaps(activity: AppCompatActivity){
        this.activity = activity
        Places.initialize(activity, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(activity)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    fun getDeviceLocation(): LiveData<HashMap<String, Double>> {
        val location = MutableLiveData<HashMap<String, Double>>()
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            showLogAssert("last maps", "latitude: ${lastKnownLocation.latitude} longitude ${lastKnownLocation.longitude}")
                            location.value = hashMapOf(
                                "latitude" to lastKnownLocation.latitude,
                                "longitude" to lastKnownLocation.longitude
                            )
                        }
                    } else {
                        showLogAssert("error maps", "Current location is null. Using defaults.")
                        showLogAssert("messages error", "${task.exception}")
                    }
                }
            }
        } catch (e: SecurityException) {
            showLogAssert("Exception: %s", "${e.message}")
        }

        return location
    }

    fun intentToMaps(latitude: Double, longitude: Double, context: Context) {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }

}