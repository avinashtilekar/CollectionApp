package com.example.ajspire.collection.room.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ajspire.collection.api.helper.NetworkResult
import com.example.ajspire.collection.api.model.response.LoginResponse
import com.example.ajspire.collection.room.dao.TransactionTableDAO
import com.example.ajspire.collection.room.entity.TransactionTable

class TransactionTableRespository(private val transactionTableDAO: TransactionTableDAO) {
    val allTransactions: LiveData<List<TransactionTable>> = transactionTableDAO.getTodaysTransaction()
    val transactionSummary: LiveData<String> = transactionTableDAO.getTransactionSummaryTodayOnly()
    val maxInvoiceNumber: LiveData<Int?> = transactionTableDAO.getMaxInvoiceNumber()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(transactionTable: TransactionTable) {
        transactionTableDAO.insert(transactionTable)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateList(transactionTable: List<TransactionTable>) {
        transactionTableDAO.updateList(transactionTable)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllUnSyncTransactions(dataUploadLimit:Int):List<TransactionTable> {
        return transactionTableDAO.getUnSyncTransaction(dataUploadLimit)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteSyncItems() {
        transactionTableDAO.deleteSyncItems()
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getTransactionViaInvoiceNumber(invoiceNumber:Int):TransactionTable? {
        return transactionTableDAO.getTransactionViaInvoiceNumber(invoiceNumber)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateReprint(invoiceNumber:Int) {
        transactionTableDAO.updateReprint(invoiceNumber)
    }
}