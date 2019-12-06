package com.dlong.networkdebugassistant.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * 轻量键值参数存储
 *
 * @author D10NG
 * @date on 2019-11-07 15:26
 */
class SpfUtils constructor(context: Context) {

    private val mSpf = context.getSharedPreferences("config_data", Context.MODE_PRIVATE)

    companion object {

        @Volatile
        private var INSTANCE: SpfUtils? = null

        @JvmStatic
        fun getInstance(context: Context) : SpfUtils =
            INSTANCE?: synchronized(this) {
                INSTANCE?: SpfUtils(context).also {
                    INSTANCE = it
                }
            }
    }

    fun getSpf() : SharedPreferences {
        return mSpf
    }
}
