package com.hk210.callmicmonitor.features.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hk210.callmicmonitor.util.datastore.DataStoreHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val dataStoreHelper: DataStoreHelper) : ViewModel() {

    private var _isBannerOverlayEnabled = MutableLiveData<Boolean>()
    val isBannerOverlayEnabled: LiveData<Boolean> get() = _isBannerOverlayEnabled

    fun setBannerOverlayState(isEnabled: Boolean) {
        viewModelScope.launch {
            dataStoreHelper.setCallerIdEnabled(isEnabled)
        }
    }

    fun getBannerOverlayState() {
        viewModelScope.launch {
            _isBannerOverlayEnabled.value = dataStoreHelper.isCallerIdEnabled().first()
        }
    }
}