package com.dlong.networkdebugassistant.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dlong.networkdebugassistant.bean.HistoryInfo
import com.dlong.networkdebugassistant.db.dao.HistoryDao

/**
 * @author D10NG
 * @date on 2019-12-07 11:12
 */
@Database(entities = [HistoryInfo::class], version = 1)
abstract class HistoryDB : RoomDatabase() {

    abstract fun getHistoryDao() : HistoryDao

    companion object {

        // 单例
        @Volatile
        private var INSTANCE : HistoryDB? = null

        @JvmStatic
        fun getDatabase(context : Context) : HistoryDB {
            val temp = INSTANCE
            if (null != temp) {
                return temp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDB::class.java,
                    "history_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}