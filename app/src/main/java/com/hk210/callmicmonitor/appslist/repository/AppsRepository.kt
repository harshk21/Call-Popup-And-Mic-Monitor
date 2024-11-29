package com.hk210.callmicmonitor.appslist.repository

import android.content.pm.PackageManager
import android.os.Build
import com.hk210.callmicmonitor.appslist.model.AppsInfo
import com.hk210.callmicmonitor.util.Result
import com.hk210.callmicmonitor.util.permissions.PermissionHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppsRepository @Inject constructor(
    private val packageManager: PackageManager,
    private val permissionHelper: PermissionHelper
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
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        } else {
            packageManager.getInstalledApplications(0)
        }

        for (packageInfo in packageList) {

            // Check if the app has microphone permission
            val hasMicrophonePermission =
                permissionHelper.hasMicrophonePermission(packageInfo.packageName)


            if (hasMicrophonePermission) {

                // Check if the app is allowed to access microphone
                val hasMicrophoneAccess =
                    permissionHelper.checkMicrophonePermission(packageInfo.packageName)

                apps.add(
                    AppsInfo(
                        appName = packageManager.getApplicationLabel(packageInfo).toString(),
                        packageName = packageInfo.packageName,
                        icon = packageManager.getApplicationIcon(packageInfo),
                        hasMicrophoneAccess = hasMicrophoneAccess,
                        hasBackgroundAccess = false
                    )
                )
            }
        }

        return apps
    }
}
