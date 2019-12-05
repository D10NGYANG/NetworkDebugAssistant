package com.dlong.networkdebugassistant.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Assets资源读取工具
 *
 * @author D10NG
 * @date on 2019-10-31 09:54
 */
object AssetsUtils {

    /**
     * 读取成字符串
     * @param context
     * @param fileName
     * @return
     */
    fun getJsonString(context: Context, fileName: String): String {
        val stringBuilder = StringBuilder()
        //获取assets资源管理器
        val assetManager = context.assets
        //通过管理器打开文件并读取
        val bf = BufferedReader(
            InputStreamReader(
                assetManager.open(fileName)
            )
        )
        var line: String?
        do {
            line = bf.readLine()
            if (line != null) {
                stringBuilder.append(line)
            } else {
                break
            }
        } while (true)
        bf.close()
        return stringBuilder.toString()
    }
}