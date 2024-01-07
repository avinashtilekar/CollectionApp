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
) : AndroidViewModel(application) {
    private val userDataStorePreferencesRepository = userPreferencesRepository
    private val gson = Gson()

    private val _userDetails = MutableLiveData<LoginResponse?>()
    val userDetails: LiveData<LoginResponse?> = _userDetails

    private val _invoicePrefix = MutableLiveData<String?>()
    val invoicePrefix: LiveData<String?> = _invoicePrefix

    private val _lastInvoiceNumber = MutableLiveData<Int?>()
    val lastInvoiceNumber: LiveData<Int?> = _lastInvoiceNumber

    private val _userPrinter = MutableLiveData<String?>()
    val userPrinter: LiveData<String?> = _userPrinter
    fun updateUserDetails(loginResponse: LoginResponse?) = viewModelScope.launch {
        var userdetailsString = ""
        userdetailsString = gson.toJson(loginResponse)
        userDataStorePreferencesRepository.updateUserDetails(userdetailsString)
    }

    fun getUserDetails() = viewModelScope.launch {
        userDataStorePreferencesRepository.getUserDetails().let {
            if (!it.isNullOrEmpty()) {
                val userDetails = gson.fromJson(it, LoginResponse::class.java)
                _userDetails.value = userDetails
            } else {
                _userDetails.value = null
            }
        }
    }

    fun updateInvoicePrefix(invoicePrefix: String) = viewModelScope.launch {
        userDataStorePreferencesRepository.updateInvoicePrefix(invoicePrefix)
    }

    fun getInvoicePrefix() = viewModelScope.launch {
        userDataStorePreferencesRepository.getInvoicePrefix().let {
            if (!it.isNullOrEmpty()) {
                _invoicePrefix.value = it
            } else {
                _invoicePrefix.value = ""
            }
        }
    }

    fun updateLastInvoiceNumber(lastInvoiceNumber: Int) = viewModelScope.launch {
        userDataStorePreferencesRepository.updateLastInvoiceNumber(lastInvoiceNumber)
        _lastInvoiceNumber.postValue(lastInvoiceNumber)
    }

    fun getLastInvoiceNumber() = viewModelScope.launch {
        userDataStorePreferencesRepository.getLastInvoiceNumber().let {
            _lastInvoiceNumber.value = it
        }
    }

    fun updateUserPrinter(userPrinter: String) = viewModelScope.launch {
        userDataStorePreferencesRepository.updateUserPrinter(userPrinter)
    }

    fun getUserPrinter() = viewModelScope.launch {
        userDataStorePreferencesRepository.getUserPrinter().let {
            if (!it.isNullOrEmpty()) {
                _userPrinter.value = it
            } else {
                _userPrinter.value = ""
            }
        }
    }
}

class DataStoreViewModelFactory(
    private val application: Application,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataStoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DataStoreViewModel(application, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}