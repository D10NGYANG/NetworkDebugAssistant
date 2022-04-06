package com.dlong.networkdebugassistant.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dlong.networkdebugassistant.bean.HistoryInfo

/**
 * @author D10NG
 * @date on 2019-12-07 11:13
 */
@Dao
interface HistoryDao {

    // 查询所有数据
    @Query("SELECT * FROM history_table ORDER BY time desc")
    fun getAllData() : LiveData<List<HistoryInfo>>

    // 插入
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: HistoryInfo): Long

    // 修改
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(info: HistoryInfo)

    // 删除
    @Delete
    suspend fun delete(info: HistoryInfo)
}