package com.hk210.callmicmonitor.appslist.model

import android.graphics.drawable.Drawable

data class AppsInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    val hasMicrophoneAccess: Boolean,
    val backgroundAccessDetected: Boolean
)
