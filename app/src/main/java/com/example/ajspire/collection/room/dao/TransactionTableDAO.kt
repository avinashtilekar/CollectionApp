package com.example.ajspire.collection.room.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.utility.AppUtility

@Dao
interface TransactionTableDAO {
    @Insert
    fun insert(note: TransactionTable)

    @Update
    fun update(note: TransactionTable)
    @Update
    fun updateList(notes: List<TransactionTable>)

    @Delete
    fun delete(note: TransactionTable)

    @Query("delete from ${AppUtility.TRANSACTION_TABLE_NAME}")
    fun deleteAllTransaction()
    @Query("delete from ${AppUtility.TRANSACTION_TABLE_NAME} where server_tran_id not null and substr(createdAt,1,10) not like strftime('%Y-%m-%d','now','localtime')")
    fun deleteSyncItems()

    @Query("select * from ${AppUtility.TRANSACTION_TABLE_NAME} order by id desc")
    fun getAllTransaction(): LiveData<List<TransactionTable>>
    @Query("select * from ${AppUtility.TRANSACTION_TABLE_NAME} where substr(createdAt,1,10) like strftime('%Y-%m-%d','now','localtime') order by id desc")
    fun getTodaysTransaction(): LiveData<List<TransactionTable>>

    @Query("select * from ${AppUtility.TRANSACTION_TABLE_NAME} where server_tran_id is null Limit :dataUploadLimit")
    fun getUnSyncTransaction(dataUploadLimit:Int): List<TransactionTable>

    @Query("Select GROUP_CONCAT(('<b>'||T.tranDate ||'</b> : ₹ '|| T.totalCollection),'<br> ') as summary " +
            "from  (select sum(amount) as totalCollection ,substr(createdAt,1,10) tranDate " +
            "from ${AppUtility.TRANSACTION_TABLE_NAME} " +
            "group by tranDate order by id asc) T ")
    fun getTransactionSummaryDateWise(): LiveData<String>

    @Query("Select GROUP_CONCAT(('<b>'||T.tranDate ||'</b> : ₹ '|| T.totalCollection),'<br> ') as summary " +
            "from  (select sum(amount) as totalCollection ,substr(createdAt,1,10) tranDate " +
            "from ${AppUtility.TRANSACTION_TABLE_NAME} " +
            "where  substr(createdAt,1,10) like strftime('%Y-%m-%d','now','localtime')) T ")
    fun getTransactionSummaryTodayOnly(): LiveData<String>

    @Query("select max(invoice_number) as max_invoice from transaction_table")
    fun getMaxInvoiceNumber(): LiveData<Int?>

    @Query("select * from transaction_table where invoice_number=:invoiceNumber")
    fun getTransactionViaInvoiceNumber(invoiceNumber:Int):TransactionTable?
}