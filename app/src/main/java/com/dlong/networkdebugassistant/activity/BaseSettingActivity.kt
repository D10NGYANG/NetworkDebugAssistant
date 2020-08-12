package com.dlong.networkdebugassistant.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.CompoundButton
import com.dlong.dialog.BaseDialog
import com.dlong.dialog.ButtonStyle
import com.dlong.dialog.EditDialog
import com.dlong.dialog.OnBtnClick
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.databinding.ContentBaseSettingBinding

/**
 * 基础设置布局
 *
 * @author D10NG
 * @date on 2019-12-09 16:46
 */
open class BaseSettingActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener {

    protected lateinit var contentBind: ContentBaseSettingBinding

    companion object{
        const val SELECT_LOCAL_PATH = 1001
        const val P_WR_EXTERNAL_STORAGE = 200
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when(requestCode) {
            SELECT_LOCAL_PATH -> {
                val path = data?.getStringExtra("path")?: "NULL"
                contentBind.config?.receiveSaveLocalPath = path
                updateConfigShow()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val pass = !grantResults.contains(PackageManager.PERMISSION_DENIED)
        when(requestCode) {
            P_WR_EXTERNAL_STORAGE -> {
                // 获取读写权限
                contentBind.config?.isAutoSaveToLocal = pass
                updateConfigShow()
            }
        }
    }

    /**
     * 初始化基础设置布局
     */
    fun initContentBinding(bind: ContentBaseSettingBinding) {
        contentBind = bind
        contentBind.swSendHex.setOnCheckedChangeListener(this)
        contentBind.swAutoAddCheck.setOnCheckedChangeListener(this)
        contentBind.swReceiveHex.setOnCheckedChangeListener(this)
        contentBind.swShowTime.setOnCheckedChangeListener(this)
        contentBind.swShowIpAddress.setOnCheckedChangeListener(this)
        contentBind.swShowPort.setOnCheckedChangeListener(this)
        contentBind.swAutoSaveLocal.setOnCheckedChangeListener(this)
    }

    /**
     * 初始化配置信息
     */
    open fun initConfig() {}

    /**
     * 刷新页面显示
     */
    open fun updateConfigShow() {}

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        val sw = p0?: return
        when(sw.id) {
            R.id.sw_send_hex -> contentBind.config?.isSendHex = p1
            R.id.sw_auto_add_check -> contentBind.config?.isAutoAddHexCheck = p1
            R.id.sw_receive_hex -> contentBind.config?.isReceiveHex = p1
            R.id.sw_show_time -> contentBind.config?.isReceiveShowTime = p1
            R.id.sw_show_ip_address -> contentBind.config?.isReceiveShowIpAddress = p1
            R.id.sw_show_port -> contentBind.config?.isReceiveShowPort = p1
            R.id.sw_auto_save_local -> {
                if (p1) {
                    if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        !checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        reqPermission(arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE),
                            P_WR_EXTERNAL_STORAGE
                        )
                        return
                    }
                }
                contentBind.config?.isAutoSaveToLocal = p1
            }
        }
        updateConfigShow()
    }

    fun setLoopTime(view: View) {
        val tag = "time"
        EditDialog(this)
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.setting_auto_send_loop_time))
            .addEdit(tag, "${contentBind.config?.autoSendTime?: 1000}", resources.getString(R.string.please_input_loop_time))
            .setInputType(tag, InputType.TYPE_CLASS_NUMBER)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME) {
                onClick { dialog, _ ->
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_loop_time_can_not_empty))
                    } else {
                        val num = value.toLongOrNull()?: -1
                        if (num in 1..999999999) {
                            contentBind.config?.autoSendTime = num
                            updateConfigShow()
                            dialog.dismiss()
                        } else {
                            dialog.setError(tag, resources.getString(R.string.input_loop_time_can_not_over_range))
                        }
                    }
                }
            }
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    fun openLocalPathManager(view: View) {
        val intent = getClearTopIntent(SelectFolderActivity::class.java)
        var path = contentBind.config?.receiveSaveLocalPath
        if (path != null && path.equals("NULL", true)) {
            path = null
        }
        intent.putExtra("path", path)
        startActivityForResult(intent, SELECT_LOCAL_PATH)
    }
}