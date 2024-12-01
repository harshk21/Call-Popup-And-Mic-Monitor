package com.hk210.callmicmonitor.features.settings.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.hk210.callmicmonitor.features.settings.call_banner.CALLER_NAME_KEY
import com.hk210.callmicmonitor.features.settings.call_banner.CallBannerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallDetectionService : Service() {

    @Inject
    lateinit var callBannerManager: CallBannerManager

    @Inject
    lateinit var windowManager: WindowManager

    var bannerView: View? = null

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, getForegroundNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val callerName = intent?.getStringExtra(CALLER_NAME_KEY) ?: "John Doe"
        Log.d("CallDetectionService", "Incoming call from: $callerName")
        handleIncomingCall(callerName)
        return START_STICKY
    }

    private fun handleIncomingCall(callerName: String) {
        bannerView = callBannerManager.createPopup(callerName)
        windowManager.addView(bannerView, callBannerManager.getParams())
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CallServiceChannel",
                "Call Detection Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun getForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, "CallServiceChannel")
            .setContentTitle("Call Detection Running")
            .setContentText("Detecting incoming calls...")
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CallDetectionService", "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}