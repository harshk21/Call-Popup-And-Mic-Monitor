package com.hk210.callmicmonitor.callpopup

import android.telephony.TelephonyManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CallPopupViewModel(private val telephonyManager: TelephonyManager) : ViewModel() {
    private val _callState = MutableLiveData<String>()
    val callState: LiveData<String> get() = _callState

    fun monitorCalls() {

    }
}
