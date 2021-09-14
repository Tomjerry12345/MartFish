package com.martfish.utils

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

class RequestPermission(activity: ComponentActivity,
                        private val permission: String,
                        onDenied: () -> Unit = {},
                        onShowRationale: () -> Unit = {}) {
    private var onGranted: () -> Unit = {}

    @RequiresApi(Build.VERSION_CODES.M)
    private val launcher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> onGranted()
                activity.shouldShowRequestPermissionRationale(permission) -> onShowRationale()
                else -> onDenied()
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    fun runWithPermission(onGranted: () -> Unit) {
        this.onGranted = onGranted
        launcher.launch(permission)
    }
}