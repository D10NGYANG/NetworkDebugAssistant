package com.dlong.dl10netassistant

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * PING
 * @param address String 地址
 * @param count Int 测试次数
 * @return MutableLiveData<String>
 */
fun ping(address: String, count: Int = 6): MutableLiveData<String> {
    var live: MutableLiveData<String>? = MutableLiveData("")
    GlobalScope.launch {
        try {
            val process = Runtime.getRuntime().exec("ping -c $count $address")
            val inS = process.inputStream
            val reader = BufferedReader(InputStreamReader(inS))
            var line: String?
            do {
                line = reader.readLine()
                if (line != null) {
                    live?.postValue("$line\n")
                } else {
                    break
                }
            } while (true)
        } catch (e: Exception) {
            e.printStackTrace()
            live?.postValue(e.message)
        } finally {
            live = null
        }
    }
    return live!!
}

/**
 * PING 一次
 * @param address String
 * @return Boolean
 */
suspend fun pingOnce(address: String): Boolean {
    return suspendCoroutine { cont ->
        GlobalScope.launch {
            try {
                val process = Runtime.getRuntime().exec("ping -c 1 $address")
                cont.resume(process.waitFor() == 0)
            } catch (e: Exception) {
                e.printStackTrace()
                cont.resume(false)
            }
        }
    }
}