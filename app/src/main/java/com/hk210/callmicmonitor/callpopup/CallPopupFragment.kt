package com.hk210.callmicmonitor.callpopup

import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hk210.callmicmonitor.CallMicMonitorActivity
import com.hk210.callmicmonitor.R
import com.hk210.callmicmonitor.alert.Alert
import com.hk210.callmicmonitor.databinding.CallPopupFragmentBinding
import com.hk210.callmicmonitor.util.permissions.PermissionHelper
import com.hk210.callmicmonitor.util.permissions.PermissionStates
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallPopupFragment : Fragment() {

    private var _binding: CallPopupFragmentBinding? = null
    private val binding: CallPopupFragmentBinding
        get() = _binding!!

    @Inject
    lateinit var permissionHelper: PermissionHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = CallPopupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as CallMicMonitorActivity).setToolbarTitle(requireContext().getString(R.string.call_popup_title))
        observeOverlayPermissionState()
    }

    override fun onResume() {
        super.onResume()

        permissionHelper.checkOverlayPermission()
    }

    private fun observeOverlayPermissionState() {
        lifecycleScope.launch {
            permissionHelper.isOverlayPermissionGranted.distinctUntilChanged().collectLatest { permissionState ->
                when(permissionState) {
                    PermissionStates.PERMISSION_GRANTED -> {
                        binding.callPopupSwitch.isEnabled = true
                    }
                    PermissionStates.PERMISSION_DENIED -> {
                        permissionHelper.requestOverlayPermission(requireActivity())
                    }
                    PermissionStates.PERMISSION_RATIONALE -> {
                        Alert.showDialog(
                            requireContext(),
                            title = requireContext().getString(R.string.draw_over_apps_title),
                            message = requireContext().getString(R.string.draw_over_apps_message),
                            positiveButtonText = requireContext().getString(R.string.grant_permission),
                            negativeButtonText = requireContext().getString(R.string.deny_permission),
                            { dialog ->
                                dialog.dismiss()
                                permissionHelper.requestOverlayPermission(requireActivity()) },
                            {
                                dialog ->
                                dialog.dismiss()
                                findNavController().popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
