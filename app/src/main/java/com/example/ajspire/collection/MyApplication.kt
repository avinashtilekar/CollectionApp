package com.example.ajspire.collection

import android.app.Application
import com.example.ajspire.collection.room.AppDataBase
import com.example.ajspire.collection.room.repository.TransactionTableRespository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val appDataBase by lazy { AppDataBase.getInstance(this) }
    val repository by lazy { TransactionTableRespository(appDataBase.TransactionTableDAO()) }
    override fun onCreate() {
        super.onCreate()
    }
}