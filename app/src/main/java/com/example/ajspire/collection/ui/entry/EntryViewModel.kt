package com.example.ajspire.collection.ui.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EntryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "हे व्यवहार नोंद (Transaction Entry) पेज आहे"
    }
    val text: LiveData<String> = _text
}