package com.example.ajspire.collection.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ajspire.collection.room.dao.TransactionTableDAO
import com.example.ajspire.collection.room.entity.TransactionTable
import com.example.ajspire.collection.utility.AppUtility

@Database(entities = [TransactionTable::class], version = 1)
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
                    .addCallback(roomCallback)
                    .build()

            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }
}