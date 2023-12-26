package com.example.ajspire.collection.ui.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.room.repository.TransactionTableRespository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryViewModel (private val transactionTableRespository: TransactionTableRespository): ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "हे व्यवहार नोंद (Transaction Entry) पेज आहे"
    }
    val text: LiveData<String> = _text

    val allTransactions: LiveData<List<TransactionTable>> = transactionTableRespository.allTransactions

    fun insert(transaction: TransactionTable)  {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.insert(transaction)
        }

    }
}

class EntryViewModelFactory(private val repository: TransactionTableRespository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}