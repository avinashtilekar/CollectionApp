package com.example.ajspire.collection.room.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.ajspire.collection.room.dao.TransactionTableDAO
import com.example.ajspire.collection.room.entity.TransactionTable

class TransactionTableRespository(private val transactionTableDAO: TransactionTableDAO) {
    val allTransactions: LiveData<List<TransactionTable>> = transactionTableDAO.getAllTransaction()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(transactionTable: TransactionTable) {
        transactionTableDAO.insert(transactionTable)
    }
}