package com.dlong.dl10netassistant

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * PING
 * @param address String 地址
 * @param count Int 测试次数
 * @return Flow<String>
 */
fun ping(address: String, count: Int = 6): Flow<String> {
    return flow {
        val process = Runtime.getRuntime().exec("ping -c $count $address")
        val inS = process.inputStream
        val reader = BufferedReader(InputStreamReader(inS))
        var line: String?
        do {
            line = reader.readLine()
            if (line != null) {
                emit("$line\n")
            } else {
                break
            }
        } while (true)
    }.flowOn(Dispatchers.IO)
        .catch { t: Throwable ->
            emit("catch error ${t.message}\n")
        }
}

/**
 * PING 一次
 * @param address String
 * @return Boolean
 */
suspend fun pingOnce(address: String): Boolean {
    return suspendCoroutine { cont ->
        CoroutineScope(Dispatchers.IO).launch {
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