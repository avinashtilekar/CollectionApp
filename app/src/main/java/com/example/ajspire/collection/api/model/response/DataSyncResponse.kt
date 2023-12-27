package com.example.ajspire.collection.api.model.response

data class DataSyncResponse(
    val `data`: List<Data>
)
data class Data(
    val mobile_tran_key: String,
    val server_tran_id: Int
)