package com.example.ajspire.collection.api.model.response

data class DataSyncResponse(
    val mobile_tran_key: String,
    val server_tran_id: Int,
    val user_id: String
)