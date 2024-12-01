package com.hk210.callmicmonitor.features.settings

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hk210.callmicmonitor.CallMicMonitorActivity
import com.hk210.callmicmonitor.R
import com.hk210.callmicmonitor.alert.Alert
import com.hk210.callmicmonitor.databinding.SettingsFragmentBinding
import com.hk210.callmicmonitor.features.settings.broadcast_receiver.CallReceiver
import com.hk210.callmicmonitor.util.permissions.PermissionHelper
import com.hk210.callmicmonitor.util.permissions.PermissionStates
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var permissionHelper: PermissionHelper

    private var callReceiver: BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as CallMicMonitorActivity).setToolbarTitle(requireContext().getString(R.string.settings_menu))
        onGrantClicked()
        binding.darkModeSwitch.isChecked = isDarkMode()
        viewModel.getBannerOverlayState()
        onDarkModeToggle()
        onAllowCallBannerToggle()
        observeBannerOverlayState()
    }

    override fun onResume() {
        super.onResume()

        permissionHelper.checkOverlayPermission()
        observeOverlayPermissionState()
    }

    private fun observeBannerOverlayState() {
        viewModel.isBannerOverlayEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.callBannerSwitch.isChecked = isEnabled
        }
    }

    private fun onDarkModeToggle() {
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isDarkMode()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private fun onAllowCallBannerToggle() {
        binding.callBannerSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setBannerOverlayState(isChecked)
            if (isChecked) {
                registerCallReceiver()
            } else {
                unregisterCallReceiver()
            }
        }
    }

    private fun onGrantClicked() {
        binding.overlayPermissionButton.setOnClickListener {
            permissionHelper.requestOverlayPermission(requireActivity())
        }
    }

    fun isDarkMode(): Boolean {
        val darkModeFlag = requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
    }

    private fun observeOverlayPermissionState() {
        lifecycleScope.launch {
            permissionHelper.isOverlayPermissionGranted.collectLatest { permissionStates ->
                when (permissionStates) {
                    PermissionStates.PERMISSION_GRANTED -> {
                        binding.overlayPermissionLayout.visibility = View.GONE
                        binding.callBannerSwitch.isEnabled = true
                    }
                    PermissionStates.PERMISSION_RATIONALE -> {
                        Alert.showDialog(
                            requireContext(),
                            title = requireContext().getString(R.string.allow_draw_over_apps_permission),
                            message = requireContext().getString(R.string.draw_over_apps_message),
                            positiveButtonText = requireContext().getString(R.string.grant_permission),
                            negativeButtonText = requireContext().getString(R.string.deny_permission),
                            { dialog ->
                                dialog.dismiss()
                                permissionHelper.requestOverlayPermission(requireActivity())
                            },
                            { dialog ->
                                dialog.dismiss()
                                binding.overlayPermissionLayout.visibility = View.VISIBLE
                                binding.callBannerSwitch.isEnabled = false
                            }
                        )
                    }
                }
            }
        }
    }

    private fun registerCallReceiver() {
        Log.e("SettingsScreen","Register receiver")
        if (callReceiver == null) {
            callReceiver = CallReceiver()
            val filter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
            requireContext().registerReceiver(callReceiver, filter)
        }
    }

    private fun unregisterCallReceiver() {
        Log.e("SettingsScreen","Unregister receiver")
        callReceiver?.let {
            requireContext().unregisterReceiver(it)
            callReceiver = null
        }
    }
}