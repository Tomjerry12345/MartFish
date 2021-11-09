package com.martfish.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.martfish.BuildConfig
import com.martfish.R

object Maps {
    private lateinit var activity: ComponentActivity
    private lateinit var placesClient: PlacesClient
    @SuppressLint("StaticFieldLeak")
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    val locationPermissionGranted = MutableLiveData<Boolean>()

    fun initMaps(activity: ComponentActivity){
        this.activity = activity
        Places.initialize(activity, activity.getString(R.string.maps_api_key))
        placesClient = Places.createClient(activity)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    fun getDeviceLocation(permission: Boolean): LiveData<HashMap<String, Double>> {
        val location = MutableLiveData<HashMap<String, Double>>()
        showLogAssert("permission", "$permission")
        try {
            if (permission) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            showLogAssert(
                                "last maps",
                                "latitude: ${lastKnownLocation.latitude} longitude ${lastKnownLocation.longitude}"
                            )
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
            } else {
                showLogAssert("permission denied", "error.")
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