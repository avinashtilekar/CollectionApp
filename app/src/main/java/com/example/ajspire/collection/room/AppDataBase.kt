package com.example.ajspire.collection.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ajspire.collection.room.dao.TransactionTableDAO
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.utility.AppUtility


@Database(entities = [TransactionTable::class], version = 2)
abstract class AppDataBase : RoomDatabase() {

    abstract fun TransactionTableDAO(): TransactionTableDAO

    companion object {
        private var instance: AppDataBase? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDataBase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, AppDataBase::class.java,
                    AppUtility.ROOM_DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(roomCallback)
                    .build()

            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }

        var MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                database.execSQL("ALTER TABLE ${AppUtility.TRANSACTION_TABLE_NAME} ADD COLUMN reprint INTEGER DEFAULT 0")
                database.setTransactionSuccessful()
                database.endTransaction()
            }
        }
    }

    fun closeDatabase() {
        instance?.let {
            if (it.isOpen) {
                it.openHelper.close()
            }
        }
    }

    fun reOpenDataBase() {
        if (instance!!.isOpen) {
            instance!!.openHelper.writableDatabase
        }
    }
}