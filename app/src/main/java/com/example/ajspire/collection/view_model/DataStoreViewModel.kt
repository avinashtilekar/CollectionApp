package com.example.ajspire.collection.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

    private val _userDetails = MutableLiveData<LoginResponse?>()
    val userDetails: LiveData<LoginResponse?> = _userDetails

    fun updateUserDetails(loginResponse: LoginResponse?) = viewModelScope.launch {
        var userdetailsString=""
        userdetailsString=gson.toJson(loginResponse)
        userDataStorePreferencesRepository.updateUserDetails(userdetailsString)
    }

    fun getUserDetails() = viewModelScope.launch {
        userDataStorePreferencesRepository.getUserDetails().let {
            if(!it.isNullOrEmpty()) {
                val userDetails = gson.fromJson(it, LoginResponse::class.java)
                _userDetails.value = userDetails
            }else{
                _userDetails.value=null
            }
        }
    }
}
class DataStoreViewModelFactory(private val application: Application,
                                private val userPreferencesRepository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataStoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DataStoreViewModel(application,userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}