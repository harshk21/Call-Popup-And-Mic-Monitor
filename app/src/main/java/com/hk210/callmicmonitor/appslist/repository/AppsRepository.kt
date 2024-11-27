package com.hk210.callmicmonitor.appslist.repository

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.hk210.callmicmonitor.appslist.model.AppsInfo
import com.hk210.callmicmonitor.util.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppsRepository @Inject constructor(
    private val context: Context,
    private val packageManager: PackageManager
) {

    fun getAppsList() = flow<Result<List<AppsInfo>>> {
        val appsList = fetchAppsList()
        emit(Result.Success(appsList))
    }.onStart {
        emit(Result.Loading())
    }.catch {
        emit(Result.Error(it.message))
    }

    private fun fetchAppsList(): List<AppsInfo> {
        val apps = mutableListOf<AppsInfo>()

        val packageList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            packageManager.getInstalledApplications(0)
        }

        for (packageInfo in packageList) {

            // Check if the app has microphone access
            val hasMicrophoneAccess =
                hasPermission(packageInfo.packageName, Manifest.permission.RECORD_AUDIO)

            apps.add(
                AppsInfo(
                    appName = packageManager.getApplicationLabel(packageInfo).toString(),
                    packageName = packageInfo.packageName,
                    icon = packageManager.getApplicationIcon(packageInfo),
                    hasMicrophoneAccess = hasMicrophoneAccess,
                    backgroundAccessDetected = false // Add logic to detect background access if needed
                )
            )
        }

        return apps
    }

    /**
     * Check if the app has a specific permission.
     */
    private fun hasPermission(packageName: String, permission: String): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS
            )
            packageInfo.requestedPermissions?.contains(permission) == true
        } catch (e: Exception) {
            false
        }
    }
}
