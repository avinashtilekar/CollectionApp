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

    @Delete
    fun delete(note: TransactionTable)

    @Query("delete from ${AppUtility.TRANSACTION_TABLE_NAME}")
    fun deleteAllTransaction()

    @Query("select * from ${AppUtility.TRANSACTION_TABLE_NAME} order by id desc")
    fun getAllTransaction(): LiveData<List<TransactionTable>>
    @Query("select * from ${AppUtility.TRANSACTION_TABLE_NAME} where substr(createdAt,1,10) like strftime('%d-%m-%Y','now') order by id desc")
    fun getTodaysTransaction(): LiveData<List<TransactionTable>>

    @Query("Select GROUP_CONCAT(('<b>'||T.tranDate ||'</b> : ₹ '|| T.totalCollection),'<br> ') as summary " +
            "from  (select sum(amount) as totalCollection ,substr(createdAt,1,10) tranDate " +
            "from ${AppUtility.TRANSACTION_TABLE_NAME} " +
            "group by tranDate order by id asc) T ")
    fun getTransactionSummaryDateWise(): LiveData<String>

    @Query("Select GROUP_CONCAT(('<b>'||T.tranDate ||'</b> : ₹ '|| T.totalCollection),'<br> ') as summary " +
            "from  (select sum(amount) as totalCollection ,substr(createdAt,1,10) tranDate " +
            "from ${AppUtility.TRANSACTION_TABLE_NAME} " +
            "where  substr(createdAt,1,10) like strftime('%d-%m-%Y','now')) T ")
    fun getTransactionSummaryTodayOnly(): LiveData<String>
}