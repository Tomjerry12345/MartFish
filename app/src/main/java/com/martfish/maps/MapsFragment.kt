package com.martfish.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.martfish.R
//import com.martfish.utils.MapsObject
import com.martfish.utils.showLogAssert

class MapsFragment : Fragment(R.layout.fragment_maps) {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var locationPermissionGranted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

//        MapsObject.initMaps(requireContext(), requireActivity())
//        MapsObject.getLocationPermission()
//        MapsObject.getDeviceLocation()
//
//        if (MapsObject.location != null) {
//            val latitude = MapsObject.location?.get("latitude")
//            val longitude = MapsObject.location?.get("longitude")
//            MapsObject.intentToMaps(latitude!!, longitude!!)
//        }


//        Places.initialize(requireContext(), getString(R.string.maps_api_key))
//        placesClient = Places.createClient(requireContext())

        // Construct a FusedLocationProviderClient.
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

//        getLocationPermission()
//        getDeviceLocation()
    }

//    private fun getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) {
//            locationPermissionGranted = true
//        } else {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
//        }
//    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
//        MapsObject.locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    MapsObject.locationPermissionGranted = true
                }
            }
        }
//        updateLocationUI()
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        val lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            showLogAssert("last maps", "latitude: ${lastKnownLocation.latitude} longitude ${lastKnownLocation.longitude}")
                            intentToMaps(lastKnownLocation.latitude, lastKnownLocation.longitude)
//                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                LatLng(lastKnownLocation!!.latitude,
//                                    lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                        }
                    } else {
                        showLogAssert("error maps", "Current location is null. Using defaults.")
                        showLogAssert("messages error", "${task.exception}")
//                        map?.moveCamera(CameraUpdateFactory
//                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
//                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            showLogAssert("Exception: %s", "${e.message}")
        }
    }

    fun intentToMaps(latitude: Double, longitude: Double) {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=5.2058,119.4973")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }


    companion object {
//        private val TAG = MapsActivityCurrentPlace::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        // [START maps_current_place_state_keys]
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        // [END maps_current_place_state_keys]

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
    }


}