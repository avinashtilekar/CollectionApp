package com.example.ajspire.collection.api.model.request

data class DataSyncRequest(
    val `data`: List<TransactionDataForUpload>,
    val user_id: String
)
data class TransactionDataForUpload(
    val amount: String,
    val customer_mobile_number: String?,
    val customer_name: String?,
    val fee_type: String,
    val mobile_tran_key: String,
    val invoice_number: Int,
    val trans_date: String?=null
)