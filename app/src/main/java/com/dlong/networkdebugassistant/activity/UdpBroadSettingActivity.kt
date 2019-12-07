package com.dlong.networkdebugassistant.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import com.dlong.dialog.BaseDialog
import com.dlong.dialog.ButtonStyle
import com.dlong.dialog.EditDialog
import com.dlong.dialog.OnBtnClick
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.UdpBroadConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant
import com.dlong.networkdebugassistant.databinding.ActivityUdpBroadSettingBinding
import com.dlong.networkdebugassistant.utils.FileProviderUtils
import com.dlong.networkdebugassistant.utils.StringUtils
import java.io.File
import java.lang.StringBuilder

class UdpBroadSettingActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivityUdpBroadSettingBinding

    companion object{
        private const val SELECT_LOCAL_PATH = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_udp_broad_setting)

        // 设置返回按钮
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.swSendHex.setOnCheckedChangeListener(this)
        binding.swAutoAddCheck.setOnCheckedChangeListener(this)
        binding.swReceiveHex.setOnCheckedChangeListener(this)
        binding.swShowTime.setOnCheckedChangeListener(this)
        binding.swShowIpAddress.setOnCheckedChangeListener(this)
        binding.swShowPort.setOnCheckedChangeListener(this)

        // 初始化配置信息
        binding.config = DBConstant.getInstance(this).getUdpBroadConfiguration()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when(requestCode) {
            SELECT_LOCAL_PATH -> {
                val path = data?.getStringExtra("path")?: "NULL"
                binding.config?.receiveSaveLocalPath = path
                updateConfigShow()
            }
        }
    }

    override fun callBack(msg: Message) {
        super.callBack(msg)
        when(msg.what) {

        }
    }

    /**
     * 更新页面显示
     */
    private fun updateConfigShow() {
        binding.config = binding.config?: UdpBroadConfiguration()
        // 保存配置信息
        DBConstant.getInstance(this).setUdpBroadConfiguration(binding.config?: UdpBroadConfiguration())
    }

    fun setLocalPort(view: View) {
        val tag = "port"
        EditDialog(this)
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.local_port))
            .addEdit(tag, "${binding.config?.localPort?: 0}", resources.getString(R.string.please_input_port))
            .setInputType(tag, InputType.TYPE_CLASS_NUMBER)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME, object : OnBtnClick{
                override fun click(d0: BaseDialog<*>, text: String) {
                    val dialog = d0 as EditDialog
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_port_can_not_empty))
                    } else {
                        val num = value.toIntOrNull()?: -1
                        if (num in 0x0000..0xffff) {
                            binding.config?.localPort = num
                            updateConfigShow()
                            dialog.dismiss()
                        } else {
                            dialog.setError(tag, resources.getString(R.string.input_port_can_not_over_range))
                        }
                    }
                }
            })
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    fun setTargetIpAddress(view: View) {
        val tag = "ip"
        EditDialog(this)
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.target_ip_address))
            .addEdit(tag, "${binding.config?.targetIpAddress?: 0}", resources.getString(R.string.please_input_ip_address))
            .setInputType(tag, InputType.TYPE_NUMBER_FLAG_DECIMAL)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME, object : OnBtnClick{
                override fun click(d0: BaseDialog<*>, text: String) {
                    val dialog = d0 as EditDialog
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_ip_address_wrong_format))
                        return
                    }
                    val items = value.split(".")
                    if (items.size != 4) {
                        dialog.setError(tag, resources.getString(R.string.input_ip_address_wrong_format))
                        return
                    }
                    val builder = StringBuilder()
                    for (str in items.iterator()) {
                        if (!StringUtils.isNumeric(str)) {
                            dialog.setError(tag, resources.getString(R.string.input_ip_address_wrong_format))
                            return
                        }
                        val num = str.toIntOrNull()?: -1
                        if (num < 0 || num > 255) {
                            dialog.setError(tag, resources.getString(R.string.input_ip_address_wrong_format))
                            return
                        }
                        builder.append(num).append(".")
                    }
                    binding.config?.targetIpAddress = builder.substring(0, builder.length -1)
                    updateConfigShow()
                    dialog.dismiss()
                }
            })
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    fun setTargetPort(view: View) {
        val tag = "port"
        EditDialog(this)
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.target_port))
            .addEdit(tag, "${binding.config?.targetPort?: 0}", resources.getString(R.string.please_input_port))
            .setInputType(tag, InputType.TYPE_CLASS_NUMBER)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME, object : OnBtnClick{
                override fun click(d0: BaseDialog<*>, text: String) {
                    val dialog = d0 as EditDialog
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_port_can_not_empty))
                    } else {
                        val num = value.toIntOrNull()?: -1
                        if (num in 0x0000..0xffff) {
                            binding.config?.targetPort = num
                            updateConfigShow()
                            dialog.dismiss()
                        } else {
                            dialog.setError(tag, resources.getString(R.string.input_port_can_not_over_range))
                        }
                    }
                }
            })
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    fun setLoopTime(view: View) {
        val tag = "time"
        EditDialog(this)
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.setting_auto_send_loop_time))
            .addEdit(tag, "${binding.config?.autoSendTime?: 1000}", resources.getString(R.string.please_input_loop_time))
            .setInputType(tag, InputType.TYPE_CLASS_NUMBER)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME, object : OnBtnClick{
                override fun click(d0: BaseDialog<*>, text: String) {
                    val dialog = d0 as EditDialog
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_loop_time_can_not_empty))
                    } else {
                        val num = value.toLongOrNull()?: -1
                        if (num in 1..999999999) {
                            binding.config?.autoSendTime = num
                            updateConfigShow()
                            dialog.dismiss()
                        } else {
                            dialog.setError(tag, resources.getString(R.string.input_loop_time_can_not_over_range))
                        }
                    }
                }
            })
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    fun openLocalPathManager(view: View) {
        val intent = getClearTopIntent(SelectFolderActivity::class.java)
        var path = binding.config?.receiveSaveLocalPath
        if (path != null && path.equals("NULL", true)) {
            path = null
        }
        intent.putExtra("path", path)
        startActivityForResult(intent, SELECT_LOCAL_PATH)
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        val sw = p0?: return
        when(sw.id) {
            R.id.sw_send_hex -> binding.config?.isSendHex = p1
            R.id.sw_auto_add_check -> binding.config?.isAutoAddHexCheck = p1
            R.id.sw_receive_hex -> binding.config?.isReceiveHex = p1
            R.id.sw_show_time -> binding.config?.isReceiveShowTime = p1
            R.id.sw_show_ip_address -> binding.config?.isReceiveShowIpAddress = p1
            R.id.sw_show_port -> binding.config?.isReceiveShowPort = p1
        }
        updateConfigShow()
    }
}
