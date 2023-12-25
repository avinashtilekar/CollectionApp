package com.example.ajspire.collection.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "हे प्रोफाइल नोंद (Profile Entry) पेज आहे"
    }
    val text: LiveData<String> = _text
}