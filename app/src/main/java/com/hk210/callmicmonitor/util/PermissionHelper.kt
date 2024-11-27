package com.hk210.callmicmonitor.util

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionHelper @Inject constructor(private val context: Context) {

    /**
     * Check if a permission is granted.
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Request permissions using a launcher.
     */
    fun requestPermissions(
        permissions: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        launcher.launch(permissions)
    }

    /**
     * Check if the user has denied any of the required permissions.
     */
    fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(context as AppCompatActivity, permission)
    }
}
