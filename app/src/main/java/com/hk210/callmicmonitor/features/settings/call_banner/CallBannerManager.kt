package com.hk210.callmicmonitor.features.settings.call_banner

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.hk210.callmicmonitor.databinding.CallPopupLayoutBinding
import com.hk210.callmicmonitor.features.settings.service.CallDetectionService
import com.hk210.callmicmonitor.util.datastore.DataStoreHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

const val CALLER_NAME_KEY = "CALLER_NAME"

@Singleton
class CallBannerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreHelper: DataStoreHelper,
) {

    private var _binding: CallPopupLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var bannerView: View
    private var viewParams: WindowManager.LayoutParams? = null

    suspend fun handleCallEvent(callerName: String) {
        val isCallerIdEnabled = dataStoreHelper.isCallerIdEnabled().first()
        if (isCallerIdEnabled) {
            startCallPopupService(callerName)
        }
    }

    private fun startCallPopupService(callerName: String) {
        val intent = Intent(context, CallDetectionService::class.java).apply {
            putExtra(CALLER_NAME_KEY, callerName)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun createPopup(callerName: String): View {
        _binding = CallPopupLayoutBinding.inflate(LayoutInflater.from(context))

        binding.callPopupCallerName.text = callerName

        viewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        viewParams?.gravity = Gravity.TOP
        bannerView = binding.root
        return bannerView
    }

    fun onCloseClicked(onClose:() -> Unit) {
        binding.callPopupClose.setOnClickListener {
            onClose.invoke()
        }
    }

    fun getParams() = viewParams

    fun destroyCallPopupView() {
        _binding = null
    }
}