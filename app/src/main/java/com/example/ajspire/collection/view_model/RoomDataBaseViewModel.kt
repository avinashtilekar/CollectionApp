package com.example.ajspire.collection.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.room.repository.TransactionTableRespository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomDataBaseViewModel constructor(
    private val transactionTableRespository: TransactionTableRespository,
    application: Application
) :
    AndroidViewModel(application) {

    val allTransactions: LiveData<List<TransactionTable>> =
        transactionTableRespository.allTransactions
    val transactionSummary: LiveData<String> = transactionTableRespository.transactionSummary

    val maxInvoiceNumber: LiveData<Int?> = transactionTableRespository.maxInvoiceNumber

    private var _allUnSyncTransactions = MutableLiveData<List<TransactionTable>?>()
    val allUnSyncTransactions: LiveData<List<TransactionTable>?>
        get() = _allUnSyncTransactions

    private val _transactionTableViaInvoiceNumber = MutableLiveData<TransactionTable?>()
    var transactionTableViaInvoiceNumber: LiveData<TransactionTable?> =
        _transactionTableViaInvoiceNumber

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

    fun getTransactionViaInvoiceNumber(invoiceNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.getTransactionViaInvoiceNumber(invoiceNumber).let {
                it?.let {
                    if (_transactionTableViaInvoiceNumber.value != it) {
                        _transactionTableViaInvoiceNumber.postValue(it)
                    }

                }
            }
        }
    }

    fun deleteSyncItems() {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.deleteSyncItems()
        }
    }

    fun updateReprint(invoiceNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionTableRespository.updateReprint(invoiceNumber)
        }
    }

    fun destroyViewModelData() {
        super.onCleared()
        _transactionTableViaInvoiceNumber.postValue(null)
        _allUnSyncTransactions.postValue(null)
    }
}

class EntryViewModelFactory(
    private val repository: TransactionTableRespository,
    private val application: Application
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoomDataBaseViewModel(repository, application) as T
    }
}