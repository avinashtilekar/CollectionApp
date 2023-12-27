package com.example.ajspire.collection.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ajspire.collection.api.ApiRepository
import com.example.ajspire.collection.api.AppRetrofitBuilder
import com.example.ajspire.collection.api.helper.NetworkResult
import com.example.ajspire.collection.api.model.request.DataSyncRequest
import com.example.ajspire.collection.api.model.request.LoginRequest
import com.example.ajspire.collection.api.model.response.DataSyncResponse
import com.example.ajspire.collection.api.model.response.LoginResponse
import kotlinx.coroutines.launch

class ApiCallViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ApiRepository(AppRetrofitBuilder.getApiService())

    private val _responseLogin: MutableLiveData<NetworkResult<LoginResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<LoginResponse>> = _responseLogin

    private val _dataSyncResponse: MutableLiveData<NetworkResult<DataSyncResponse>> = MutableLiveData()
    val responseDataSyncResponse: LiveData<NetworkResult<DataSyncResponse>> = _dataSyncResponse

    fun login(request: LoginRequest?) = viewModelScope.launch {
        _responseLogin.value=NetworkResult.Loading()
        repository.login(request).collect { values ->
            _responseLogin.value = values
        }
    }

    fun dataSync(request: DataSyncRequest?) = viewModelScope.launch {
        _dataSyncResponse.value=NetworkResult.Loading()
        repository.dataSync(request).collect { values ->
            _dataSyncResponse.value = values
        }
    }

}