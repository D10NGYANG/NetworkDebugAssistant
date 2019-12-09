package com.dlong.networkdebugassistant.bean

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dlong.jsonentitylib.BaseJsonEntity
import com.dlong.jsonentitylib.annotation.DLField
import com.dlong.networkdebugassistant.utils.DateUtils

/**
 * 历史发送
 *
 * @author D10NG
 * @date on 2019-12-07 11:05
 */
@Entity(tableName = "history_table", indices = [Index(value = ["text"], unique = true)])
data class HistoryInfo(

    // ID，主键，自增长
    @PrimaryKey(autoGenerate = true)
    @DLField
    var hId : Int = -1 ,

    /** 发送文本 */
    @DLField
    var text: String = "",

    /** 发送时间 */
    @DLField
    var time: Long = 0L
) : BaseJsonEntity() {

    companion object{
        @JvmStatic
        @BindingAdapter("setTime")
        fun setTime(textView: TextView, time: Long) {
            textView.text = DateUtils.getDateStr(time, "yyyy-MM-dd hh:mm:ss")
        }
    }
}