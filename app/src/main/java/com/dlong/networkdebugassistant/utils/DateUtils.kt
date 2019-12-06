package com.dlong.networkdebugassistant.utils

import android.annotation.SuppressLint

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * 时间工具
 *
 * @author D10NG
 * @date on 2019-10-08 11:28
 */
object DateUtils {

    /**
     * 获取系统时间戳
     * @return
     */
    val curTime: Long
        get() = System.currentTimeMillis()

    val curYear: Int
        get() = Integer.parseInt(getCurDateStr("yyyy"))

    val curMonth: Int
        get() = Integer.parseInt(getCurDateStr("MM"))

    val curDay: Int
        get() = Integer.parseInt(getCurDateStr("dd"))

    /**
     * 时间戳转换成字符窜
     * @param milSecond
     * @param pattern
     * @return
     */
    fun getDateStr(milSecond: Long, pattern: String): String {
        val date = Date(milSecond)
        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    /**
     * 获取当前时间字符串
     * @param pattern
     * @return
     */
    fun getCurDateStr(pattern: String): String {
        return getDateStr(curTime, pattern)
    }

    /**
     * 将字符串转为时间戳
     * @param dateString
     * @param pattern
     * @return
     */
    fun getDateFromStr(dateString: String, pattern: String): Long {
        @SuppressLint("SimpleDateFormat")
        val dateFormat = SimpleDateFormat(pattern)
        var date: Date? = Date()
        try {
            date = dateFormat.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return date?.time ?: 0
    }

    /**
     * 根据年月日获取时间戳
     */
    fun getDateFromYMD(year: Int, month: Int, day: Int) : Long {
        val builder = StringBuilder()
        builder.append(StringUtils.upToNString(year.toString(), 4)).append("-")
        builder.append(StringUtils.upToNString(month.toString(), 2)).append("-")
        builder.append(StringUtils.upToNString(day.toString(), 2))
        return getDateFromStr(builder.toString(), "yyyy-MM-dd")
    }

    /**
     * 获取指定月份的天数
     * @param year
     * @param month
     * @return
     */
    fun getDaysOfMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            calendar.time = sdf.parse(
                StringUtils.upToNString(year.toString() + "", 4) + "-" +
                        StringUtils.upToNString(month.toString() + "", 2) + "-01"
            )!!
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getDateYear(time: Long): Int {
        return Integer.parseInt(getDateStr(time, "yyyy"))
    }

    fun getDateMonth(time: Long): Int {
        return Integer.parseInt(getDateStr(time, "MM"))
    }

    fun getDateDay(time: Long): Int {
        return Integer.parseInt(getDateStr(time, "dd"))
    }
}
