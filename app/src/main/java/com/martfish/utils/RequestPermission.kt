package com.martfish.utils

import androidx.activity.ComponentActivity

class RequestPermission(activity: ComponentActivity,
                        private val permission: String,
                        onDenied: () -> Unit = {},
                        onShowRationale: () -> Unit = {}) {
    private var onGranted: () -> Unit = {}

    private val launcher =
        activity.registerForActivityResult(RequestPermission()) { isGranted ->
            when {
                isGranted -> onGranted()
                activity.shouldShowRequestPermissionRationale(permission) -> onShowRationale()
                else -> onDenied()
            }
        }

    fun runWithPermission(onGranted: () -> Unit) {
        this.onGranted = onGranted
        launcher.launch(permission)
    }
}