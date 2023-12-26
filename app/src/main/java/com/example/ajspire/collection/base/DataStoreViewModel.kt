package com.example.ajspire.collection.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ajspire.collection.api.model.response.LoginResponse
import com.example.ajspire.collection.extensions.UserPreferencesRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class DataStoreViewModel constructor(
    private val application: Application,
    private val userPreferencesRepository: UserPreferencesRepository
): AndroidViewModel(application)  {
    private val userDataStorePreferencesRepository = userPreferencesRepository
    private val gson = Gson()

    private val _userDetails = MutableLiveData<LoginResponse>()
    val userDetails: LiveData<LoginResponse> = _userDetails

    fun updateUserDetails(loginResponse: LoginResponse?) = viewModelScope.launch {
        var userdetailsString=""
        userdetailsString=gson.toJson(loginResponse)
        userDataStorePreferencesRepository.updateUserDetails(userdetailsString)
    }

    fun getUserDetails() = viewModelScope.launch {
        userDataStorePreferencesRepository.getUserDetails()?.let {
            val userDetails = gson.fromJson(it, LoginResponse::class.java)
            _userDetails.value = userDetails
        }
    }
}