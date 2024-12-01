package com.hk210.callmicmonitor.features.apps_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hk210.callmicmonitor.features.apps_list.model.AppsInfo
import com.hk210.callmicmonitor.features.apps_list.repository.AppsRepository
import com.hk210.callmicmonitor.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsListViewModel @Inject constructor(private val appsRepository: AppsRepository) :
    ViewModel() {

    private val _appList = MutableLiveData<Result<List<AppsInfo>>>()
    val appList: LiveData<Result<List<AppsInfo>>> get() = _appList

    fun getAppsList() {
        viewModelScope.launch {
            appsRepository.getAppsList().distinctUntilChanged().flowOn(Dispatchers.IO)
                .collect { result ->
                    _appList.value = result
                }
        }
    }
}
