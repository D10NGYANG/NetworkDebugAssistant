package com.dlong.networkdebugassistant.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dlong.networkdebugassistant.bean.HistoryInfo
import com.dlong.networkdebugassistant.db.HistoryDB
import kotlinx.coroutines.launch

/**
 * @author D10NG
 * @date on 2019-12-07 14:47
 */
class HistoryModel(application: Application) : AndroidViewModel(application) {

    private val db = HistoryDB.getDatabase(application)
    private var allData: LiveData<List<HistoryInfo>>

    init {
        allData = db.getHistoryDao().getAllData()
    }

    fun getAllData() = allData

    fun insertHistory(info : HistoryInfo) = viewModelScope.launch{
        db.getHistoryDao().insert(info)
    }

    fun updateHistory(info: HistoryInfo) = viewModelScope.launch {
        db.getHistoryDao().update(info)
    }

    fun deleteHistory(info: HistoryInfo) = viewModelScope.launch {
        db.getHistoryDao().delete(info)
    }
}