package com.example.ajspire.collection.ui.model
import com.example.ajspire.collection.utility.AppUtility

data class ItemModel (
   val id: Int=0,
    val fee_type: String,
    val amount: String,
    val mobile_tran_key: String,
    val customer_mobile_number: String?=null,
    val customer_name: String?=null,
    val server_tran_id: String?=null,
    val createdAt: String?= AppUtility.currentDateTime,
    val updatedAt: String?=null
)