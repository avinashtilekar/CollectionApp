package com.example.ajspire.collection.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.room.repository.TransactionTableRespository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataBaseViewModel(private val transactionTableRespository: TransactionTableRespository) :
    ViewModel() {

    val allTransactions: LiveData<List<TransactionTable>> =
        transactionTableRespository.allTransactions
    val transactionSummary: LiveData<String> = transactionTableRespository.transactionSummary

    val maxInvoiceNumber: LiveData<Int?> = transactionTableRespository.maxInvoiceNumber

    var _allUnSyncTransactions = MutableLiveData<List<TransactionTable>>()
    val allUnSyncTransactions: LiveData<List<TransactionTable>>
        get() = _allUnSyncTransactions

    val _transactionTableViaInvoiceNumber = MutableLiveData<TransactionTable?>()
    val transactionTableViaInvoiceNumber: LiveData<TransactionTable?> = _transactionTableViaInvoiceNumber

    fun insert(transaction: TransactionTable) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.insert(transaction)
        }
    }
    fun updateList(transaction: List<TransactionTable>) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.updateList(transaction)
        }
    }

    fun getAllUnSyncTransactions(dataUploadLimit: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.getAllUnSyncTransactions(dataUploadLimit).let {
                _allUnSyncTransactions.postValue(it)
            }
        }
    }
    fun getTransactionViaInvoiceNumber(invoiceNumber:Int) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.getTransactionViaInvoiceNumber(invoiceNumber).let {
                it?.let {
                    _transactionTableViaInvoiceNumber.postValue(it)
                }
            }
        }
    }

    fun deleteSyncItems() {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.deleteSyncItems()
        }
    }
}

class EntryViewModelFactory(private val repository: TransactionTableRespository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataBaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DataBaseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}