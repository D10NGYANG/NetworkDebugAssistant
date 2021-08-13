package com.dlong.networkdebugassistant.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.d10ng.stringlib.toHexString
import com.dlong.networkdebugassistant.app.BaseHandler
import com.dlong.networkdebugassistant.bean.ReceiveInfo
import com.dlong.networkdebugassistant.utils.DateUtils
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/**
 * @author D10NG
 * @date on 2019-12-05 11:03
 */
open class BaseActivity : AppCompatActivity(), BaseHandler.BaseHandlerCallBack {

    lateinit var mHandler: BaseHandler

    override fun callBack(msg: Message) {
        // 回调
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandler = BaseHandler(this, this)
    }

    fun finishGoTo(clz: Class<*>) {
        clearGoTo(clz)
        finish()
    }

    fun clearGoTo(clz: Class<*>) {
        val intent = getClearTopIntent(clz)
        startActivity(intent)
    }

    fun getClearTopIntent(clz: Class<*>) : Intent {
        val intent = Intent(this, clz)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return intent
    }

    // 检查权限
    fun checkPermission(permission: String) : Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    // 请求权限
    fun reqPermission(permissions: Array<String>, reqCode: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        requestPermissions(permissions, reqCode)
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    // 保存数据到本地
    fun saveReceiveDataToLocal(receive: ReceiveInfo, path: String, isHex: Boolean) {
        // 检查权限
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
            !checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) return
        // 检查路径是否存在
        val parentPath = File(path)
        if (!parentPath.exists()) return
        // 创建以日期为参数的txt文本
        val fileName = "receive_text_${DateUtils.getCurDateStr("yyyy_MM_dd")}.txt"
        val file = File("$path/$fileName")
        if (!file.exists()) {
            file.createNewFile()
        }
        // 创建新加文本
        val builder = StringBuilder()
        builder.append("[")
        builder.append(DateUtils.getDateStr(receive.time, "yyyy-MM-dd hh:mm:ss"))
        builder.append("][")
        builder.append(receive.ipAddress).append(":")
        builder.append(receive.port).append("]")
        if (isHex) {
            for (byte in receive.byteData.iterator()) {
                builder.append(byte.toHexString())
                builder.append(" ")
            }
        } else {
            builder.append(String(receive.byteData, 0, receive.byteData.size))
        }
        builder.append("\r\n")
        Log.e("测试","接收到：$builder")
        // 写入文本内容
        val outputStream = FileOutputStream(file, true)
        val writer = BufferedWriter(OutputStreamWriter(outputStream))
        writer.write(builder.toString())
        writer.close()
    }
}