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
}