package com.example.ajspire.collection.ui.entry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.room.repository.TransactionTableRespository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryViewModel(private val transactionTableRespository: TransactionTableRespository) :
    ViewModel() {

    val allTransactions: LiveData<List<TransactionTable>> =
        transactionTableRespository.allTransactions
    val transactionSummary: LiveData<String> = transactionTableRespository.transactionSummary

    var _allUnSyncTransactions = MutableLiveData<List<TransactionTable>>()
    val allUnSyncTransactions: LiveData<List<TransactionTable>>
        get() = _allUnSyncTransactions


    fun insert(transaction: TransactionTable) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.insert(transaction)
        }
    }

    fun getAllUnSyncTransactions(dataUploadLimit: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.getAllUnSyncTransactions(dataUploadLimit).let {
                _allUnSyncTransactions.postValue(it)
            }


        }
    }
}

class EntryViewModelFactory(private val repository: TransactionTableRespository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}