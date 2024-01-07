package com.example.ajspire.collection

import android.app.Application
import com.example.ajspire.collection.api.model.response.LoginResponse
import com.example.ajspire.collection.room.AppDataBase
import com.example.ajspire.collection.room.repository.TransactionTableRespository
import com.example.ajspire.collection.utility.PrinterType
import com.example.ajspire.collection.utility.UserPrinters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val appDataBase by lazy { AppDataBase.getInstance(this) }
    val repository by lazy { TransactionTableRespository(appDataBase.TransactionTableDAO()) }
    var loginUserDetails: LoginResponse? = null
    var invoiceNumberPrefix: String = ""
    var userPrinters: UserPrinters? =
        if (BuildConfig.BUILD_TYPE_NAME.isBlank()) PrinterType.VriddhiDefault else PrinterType.VriddhiExternal

    override fun onCreate() {
        super.onCreate()
    }
}