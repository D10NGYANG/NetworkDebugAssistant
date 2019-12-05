package com.dlong.networkdebugassistant.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.lang.StringBuilder

/**
 * @author D10NG
 * @date on 2019-11-27 11:32
 */
object AppUtils {

    /**
     * 获取当前应用的版本号
     *
     * @return
     */
    fun getAppVersion(context: Context): String {
        // 获取packagemanager的实例
        val packageManager = context.packageManager
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return packInfo.versionName
    }

    /**
     * 版本信息实体
     */
    data class VersionReadMe(
        var version: String = "",
        var author: String = "",
        var time: String = "",
        var content: String = ""
    ) : Serializable {

        fun parseFromJson(json: JSONObject, version: String) {
            this.version = version
            this.author = json.optString("author")
            this.time = json.optString("time")
            val array = json.optJSONArray("content")?: JSONArray()
            val builder = StringBuilder()
            for (i in 0 until array.length()) {
                builder.append("${i + 1}、").append(array.getString(i)).append("\n")
            }
            this.content = builder.toString()
        }
    }

    /**
     * 获取版本信息
     */
    fun getVersionReadMe(context: Context, version: String) : VersionReadMe {
        val jsonObject = JSONObject(AssetsUtils.getJsonString(context, "readme.json"))
        val versionObj = jsonObject.optJSONObject(version)?: return VersionReadMe()
        val vrm = VersionReadMe()
        vrm.parseFromJson(versionObj, version)
        return vrm
    }
}