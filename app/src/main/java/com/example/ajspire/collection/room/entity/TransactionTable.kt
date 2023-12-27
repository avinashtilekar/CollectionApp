package com.example.ajspire.collection.room.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ajspire.collection.utility.AppUtility

@Entity(tableName = AppUtility.TRANSACTION_TABLE_NAME)
data class TransactionTable(
    @PrimaryKey(autoGenerate = true)val id: Int=0,
    val fee_type: String,
    val amount: String,
    val mobile_tran_key: String,
    val customer_mobile_number: String?=null,
    val customer_name: String?=null,
    var server_tran_id: String?=null,
    val createdAt: String?=AppUtility.currentDateTime,
    val updatedAt: String?=null
)