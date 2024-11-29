package com.hk210.callmicmonitor.appslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hk210.callmicmonitor.R
import com.hk210.callmicmonitor.alert.Alert
import com.hk210.callmicmonitor.appslist.adpater.AppsListAdapter
import com.hk210.callmicmonitor.appslist.model.AppsInfo
import com.hk210.callmicmonitor.databinding.AppsListFragmentBinding
import com.hk210.callmicmonitor.util.Result
import com.hk210.callmicmonitor.util.loader.LoaderUtils
import com.hk210.callmicmonitor.util.permissions.PermissionHelper
import com.hk210.callmicmonitor.util.permissions.PermissionStates
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppsListFragment : Fragment() {

    private var _binding: AppsListFragmentBinding? = null
    private val binding: AppsListFragmentBinding
        get() = _binding!!

    private val viewModel: AppsListViewModel by viewModels()

    @Inject
    lateinit var permissionHelper: PermissionHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AppsListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAppsList()
    }

    override fun onResume() {
        super.onResume()
        permissionHelper.checkUsageAccessPermission()
        observePermissionState()
    }

    private fun observePermissionState() {
        lifecycleScope.launch {
            permissionHelper.isPermissionGranted.collect { permissionState ->
                when (permissionState) {
                    PermissionStates.PERMISSION_GRANTED -> {
                        viewModel.getAppsList()
                    }

                    PermissionStates.PERMISSION_DENIED -> {
                        permissionHelper.requestUsageAccessPermission()
                    }

                    PermissionStates.PERMISSION_RATIONALE -> {
                        Alert.showDialog(
                            requireContext(),
                            title = requireContext().getString(R.string.usage_access_dialog_title),
                            message = requireContext().getString(R.string.usage_access_rationale_message),
                            positiveButtonText = requireContext().getString(R.string.grant_permission),
                            negativeButtonText = requireContext().getString(R.string.deny_permission),
                            { dialog ->
                                dialog.dismiss()
                                permissionHelper.requestUsageAccessPermission()
                            },
                            { dialog ->
                                dialog.dismiss()
                                requireActivity().finish()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun observeAppsList() {
        viewModel.appList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> LoaderUtils.showDialog(requireContext(), false)
                is Result.Error -> {
                    LoaderUtils.hideDialog()
                    Alert.showSnackBar(binding.root, result.message.toString())
                }

                is Result.Success -> {
                    LoaderUtils.hideDialog()
                    result.data?.let { setAppsList(it) }
                }
            }
        }
    }

    private fun setAppsList(appsList: List<AppsInfo>) {
        val adapter = AppsListAdapter(requireContext()) { packageName ->
            permissionHelper.redirectToAppSettings(packageName)
        }
        adapter.submitList(appsList)
        binding.appsList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
