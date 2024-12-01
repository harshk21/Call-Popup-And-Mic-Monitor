package com.hk210.callmicmonitor.features.settings.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.hk210.callmicmonitor.features.settings.call_banner.CallBannerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var callBannerManager: CallBannerManager

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CallReceiver", "onReceive: ${intent.action}")
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        callBannerManager.handleCallEvent("John Doe")
                    }
                    Log.d("CallReceiver", "Phone is ringing")
                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Log.d("CallReceiver", "Phone is off-hook (call answered or dialing).")
                }

                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.d("CallReceiver", "Phone is idle (call ended or no activity).")
                }
            }
        }
    }
}