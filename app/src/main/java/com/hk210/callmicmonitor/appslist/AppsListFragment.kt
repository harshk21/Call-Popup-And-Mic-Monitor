package com.hk210.callmicmonitor.appslist

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hk210.callmicmonitor.alert.Alert
import com.hk210.callmicmonitor.appslist.adpater.AppsListAdapter
import com.hk210.callmicmonitor.appslist.model.AppsInfo
import com.hk210.callmicmonitor.databinding.AppsListFragmentBinding
import com.hk210.callmicmonitor.util.Result
import com.hk210.callmicmonitor.util.loader.LoaderUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppsListFragment : Fragment() {

    private var _binding: AppsListFragmentBinding? = null
    private val binding: AppsListFragmentBinding
        get() = _binding!!

    private val viewModel: AppsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AppsListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAppsList()
        observeAppsList()
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
        val adapter = AppsListAdapter { packageName ->
            launchAppSettings(packageName)
        }
        adapter.submitList(appsList)
        binding.appsList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun launchAppSettings(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
