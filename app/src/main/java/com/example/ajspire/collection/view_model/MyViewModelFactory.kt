package com.example.ajspire.collection.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.extensions.UserPreferencesRepository

class MyViewModelFactory(private val mApplication: Application, private val userPreferencesRepository: UserPreferencesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DataStoreViewModel(mApplication, userPreferencesRepository) as T
    }
}