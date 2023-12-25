package com.example.ajspire.collection.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "हे सेटिंग (Settings) पेज आहे"
    }
    val text: LiveData<String> = _text
}