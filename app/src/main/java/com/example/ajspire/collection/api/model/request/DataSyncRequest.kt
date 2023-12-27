package com.example.ajspire.collection.api.model.request

data class DataSyncRequest(
    val `data`: List<Data>,
    val user_id: String
)
data class Data(
    val amount: String,
    val customer_mobile_number: String,
    val customer_name: String,
    val fee_type: String,
    val mobile_tran_key: String,
    val trans_date: String
)