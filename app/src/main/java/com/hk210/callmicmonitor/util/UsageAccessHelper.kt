package com.hk210.callmicmonitor.util

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import com.hk210.callmicmonitor.util.permissions.PermissionHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageAccessHelper @Inject constructor(
    private val usageStatsManager: UsageStatsManager,
    private val permissionHelper: PermissionHelper
) {

    fun isAppRunningBackgroundService(packageName: String): Boolean {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 60 * 24 // Last 10 minutes

        // Query usage events within the time window
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.packageName == packageName && event.eventType == UsageEvents.Event.FOREGROUND_SERVICE_START) {
                // The app has an active foreground service; check if it uses the microphone
                return permissionHelper.hasMicrophonePermission(packageName)
            }
        }

        return false
    }
}