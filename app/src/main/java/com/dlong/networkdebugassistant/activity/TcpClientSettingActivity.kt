package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dlong.dialog.*
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.TcpClientConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant
import com.dlong.networkdebugassistant.databinding.ActivityTcpClientSettingBinding
import com.dlong.networkdebugassistant.utils.StringUtils

class TcpClientSettingActivity : BaseSettingActivity() {

    private lateinit var binding: ActivityTcpClientSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tcp_client_setting)

        // 设置返回按钮
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        initContentBinding(binding.contentBase)
        initConfig()
    }

    override fun initConfig() {
        super.initConfig()
        // 初始化配置信息
        binding.contentBase.config = DBConstant.getInstance(this).getTcpClientConfiguration()
        updateConfigShow()
    }

    override fun updateConfigShow() {
        binding.contentBase.config = binding.contentBase.config?: TcpClientConfiguration()
        val cc = binding.contentBase.config as TcpClientConfiguration
        binding.serverPort = "${cc.serverPort}"
        binding.serverIpAddress = cc.serverIpAddress
        // 保存配置信息
        DBConstant.getInstance(this).setTcpClientConfiguration(cc)
    }

    fun setTargetIpAddress(view: View) {
        val cc = binding.contentBase.config as TcpClientConfiguration
        val tag = "ip"
        EditDialog(this).create()
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.target_ip_address))
            .addEdit(tag, cc.serverIpAddress, resources.getString(R.string.please_input_ip_address))
            .setInputType(tag, InputType.TYPE_NUMBER_FLAG_DECIMAL)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME) {
                onClick { dialog, _ ->
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_ip_address_wrong_format))
                        return@onClick
                    }
                    val items = value.split(".")
                    if (items.size != 4) {
                        dialog.setError(tag, resources.getString(R.string.input_ip_address_wrong_format))
                        return@onClick
                    }
                    val builder = StringBuilder()
                    for (str in items.iterator()) {
                        if (!StringUtils.isNumeric(str)) {
                            dialog.setError(tag, resources.getString(R.string.input_ip_address_wrong_format))
                            return@onClick
                        }
                        val num = str.toIntOrNull()?: -1
                        if (num < 0 || num > 255) {
                            dialog.setError(tag, resources.getString(R.string.input_ip_address_wrong_format))
                            return@onClick
                        }
                        builder.append(num).append(".")
                    }
                    cc.serverIpAddress = builder.substring(0, builder.length -1)
                    updateConfigShow()
                    dialog.dismiss()
                }
            }
            .addAction(resources.getString(R.string.cancel))
            .show()
    }

    fun setTargetPort(view: View) {
        val cc = binding.contentBase.config as TcpClientConfiguration
        val tag = "port"
        EditDialog(this).create()
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.target_port))
            .addEdit(tag, "${cc.serverPort}", resources.getString(R.string.please_input_port))
            .setInputType(tag, InputType.TYPE_CLASS_NUMBER)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME) {
                onClick { dialog, _ ->
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_port_can_not_empty))
                    } else {
                        val num = value.toIntOrNull()?: -1
                        if (num in 0x0000..0xffff) {
                            cc.serverPort = num
                            updateConfigShow()
                            dialog.dismiss()
                        } else {
                            dialog.setError(tag, resources.getString(R.string.input_port_can_not_over_range))
                        }
                    }
                }
            }
            .addAction(resources.getString(R.string.cancel))
            .show()
    }
}
