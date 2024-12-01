package com.hk210.callmicmonitor.util.permissions

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PermissionHelper @Inject constructor(
    private val context: Context,
    private val packageManager: PackageManager
) {

    private val _isPermissionGranted = MutableStateFlow(PermissionStates.PERMISSION_DENIED)
    val isPermissionGranted: Flow<@PermissionStates.PermissionState Int> get() = _isPermissionGranted.asStateFlow()

    private val _isOverlayPermissionGranted = MutableStateFlow(PermissionStates.PERMISSION_RATIONALE)
    val isOverlayPermissionGranted: Flow<@PermissionStates.PermissionState Int> get() = _isOverlayPermissionGranted.asStateFlow()

    /**
     * Checks if a regular permission is granted.
     * @param packageName to check the mic permission for a specific package
     * @return true if granted else false
     */
    fun checkMicrophonePermission(packageName: String): Boolean {

        // Check if the microphone permission is granted
        return packageManager.checkPermission(
            Manifest.permission.RECORD_AUDIO,
            packageName
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if the app has a specific permission.
     * @param packageName to check if specific package includes mic permission or not
     */
    fun hasMicrophonePermission(packageName: String): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS
            )
            packageInfo.requestedPermissions?.contains(Manifest.permission.RECORD_AUDIO) == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks if Usage Access permission is granted.
     */
    fun checkUsageAccessPermission() {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {

            // used a deprecated code because didn't find any alternate solution below API 29
            appOpsManager.noteOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        _isPermissionGranted.value = when(mode) {
            AppOpsManager.MODE_ALLOWED -> PermissionStates.PERMISSION_GRANTED
            AppOpsManager.MODE_IGNORED -> PermissionStates.PERMISSION_DENIED
            else -> PermissionStates.PERMISSION_RATIONALE
        }
    }

    /**
     * Redirects the user to the Usage Access Settings page.
     */
    fun requestUsageAccessPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Redirects the user to the app's settings page for manual permission granting.
     */
    fun redirectToAppSettings(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${packageName}")
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Checks if the app has overlay permission.
     * @return true if granted else false.
     */
    fun checkOverlayPermission() {
        val isGranted =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
        _isOverlayPermissionGranted.value = when (isGranted) {
            true -> PermissionStates.PERMISSION_GRANTED
            else -> PermissionStates.PERMISSION_RATIONALE
        }
    }

    /**
     * Requests overlay permission by redirecting the user to the settings page.
     * @param activity The activity context for handling permissions.
     */
    fun requestOverlayPermission(activity: FragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            activity.startActivity(intent)
        }
    }
}
