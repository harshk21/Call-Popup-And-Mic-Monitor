package com.hk210.callmicmonitor.features.apps_list.repository

import android.content.pm.PackageManager
import android.os.Build
import com.hk210.callmicmonitor.features.apps_list.model.AppsInfo
import com.hk210.callmicmonitor.util.Result
import com.hk210.callmicmonitor.util.UsageAccessHelper
import com.hk210.callmicmonitor.util.permissions.PermissionHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppsRepository @Inject constructor(
    private val packageManager: PackageManager,
    private val permissionHelper: PermissionHelper,
    private val usageAccessHelper: UsageAccessHelper
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
                val hasMicrophoneAccess = permissionHelper.checkMicrophonePermission(packageInfo.packageName)

                // CHeck if app ran in background in last 24 hours
                val hasBackgroundAccess = usageAccessHelper.isAppRunningBackgroundService(packageInfo.packageName)


                apps.add(
                    AppsInfo(
                        appName = packageManager.getApplicationLabel(packageInfo).toString(),
                        packageName = packageInfo.packageName,
                        icon = packageManager.getApplicationIcon(packageInfo),
                        hasMicrophoneAccess = hasMicrophoneAccess,
                        hasBackgroundAccess = hasBackgroundAccess
                    )
                )
            }
        }

        return apps
    }
}
