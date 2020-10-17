package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dlong.dialog.ButtonStyle
import com.dlong.dialog.EditDialog
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.UdpMultiConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant
import com.dlong.networkdebugassistant.databinding.ActivityUdpMultiSettingBinding
import com.dlong.networkdebugassistant.utils.StringUtils

/**
 * @author D10NG
 * @date on 2019-12-09 11:14
 */
class UdpMultiSettingActivity : BaseSettingActivity() {

    private lateinit var binding: ActivityUdpMultiSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_udp_multi_setting)

        // 设置返回按钮
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        initContentBinding(binding.contentBase)
        initConfig()
    }

    override fun initConfig() {
        super.initConfig()
        // 初始化配置信息
        binding.contentBase.config = DBConstant.getInstance(this).getUdpMultiConfiguration()
        updateConfigShow()
    }

    override fun updateConfigShow() {
        binding.contentBase.config = binding.contentBase.config?: UdpMultiConfiguration()
        val cc = binding.contentBase.config as UdpMultiConfiguration
        binding.localPort = "${cc.localPort}"
        binding.targetIpAddress = cc.targetIpAddress
        binding.targetPort = "${cc.targetPort}"
        // 保存配置信息
        DBConstant.getInstance(this).setUdpMultiConfiguration(cc)
    }

    fun setLocalPort(view: View) {
        val cc = binding.contentBase.config as UdpMultiConfiguration
        val tag = "port"
        EditDialog(this)
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.local_port))
            .addEdit(tag, "${cc.localPort}", resources.getString(R.string.please_input_port))
            .setInputType(tag, InputType.TYPE_CLASS_NUMBER)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME) {
                onClick { dialog, _ ->
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_port_can_not_empty))
                    } else {
                        val num = value.toIntOrNull()?: -1
                        if (num in 0x0000..0xffff) {
                            cc.localPort = num
                            updateConfigShow()
                            dialog.dismiss()
                        } else {
                            dialog.setError(tag, resources.getString(R.string.input_port_can_not_over_range))
                        }
                    }
                }
            }
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    fun setTargetIpAddress(view: View) {
        val cc = binding.contentBase.config as UdpMultiConfiguration
        val tag = "ip"
        EditDialog(this)
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.target_multi_ip_address))
            .addEdit(tag, cc.targetIpAddress, resources.getString(R.string.please_input_multi_ip_address))
            .setInputType(tag, InputType.TYPE_NUMBER_FLAG_DECIMAL)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME) {
                onClick { dialog, _ ->
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_multi_ip_address_wrong_format))
                        return@onClick
                    }
                    val items = value.split(".")
                    if (items.size != 4) {
                        dialog.setError(tag, resources.getString(R.string.input_multi_ip_address_wrong_format))
                        return@onClick
                    }
                    val builder = StringBuilder()
                    for (i in items.indices) {
                        if (!StringUtils.isNumeric(items[i])) {
                            dialog.setError(tag, resources.getString(R.string.input_multi_ip_address_wrong_format))
                            return@onClick
                        }
                        val num = items[i].toIntOrNull()?: -1
                        if (i == 0 && (num < 224 || num > 239)) {
                            dialog.setError(tag, resources.getString(R.string.input_multi_ip_address_wrong_format))
                            return@onClick
                        }
                        if (num < 0 || num > 255) {
                            dialog.setError(tag, resources.getString(R.string.input_multi_ip_address_wrong_format))
                            return@onClick
                        }
                        builder.append(num).append(".")
                    }
                    cc.targetIpAddress = builder.substring(0, builder.length -1)
                    updateConfigShow()
                    dialog.dismiss()
                }
            }
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    fun setTargetPort(view: View) {
        val cc = binding.contentBase.config as UdpMultiConfiguration
        val tag = "port"
        EditDialog(this)
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.target_port))
            .addEdit(tag, "${cc.targetPort}", resources.getString(R.string.please_input_port))
            .setInputType(tag, InputType.TYPE_CLASS_NUMBER)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME) {
                onClick { dialog, _ ->
                    val value = dialog.getInputText(tag)
                    if (value.isEmpty()) {
                        dialog.setError(tag, resources.getString(R.string.input_port_can_not_empty))
                    } else {
                        val num = value.toIntOrNull()?: -1
                        if (num in 0x0000..0xffff) {
                            cc.targetPort = num
                            updateConfigShow()
                            dialog.dismiss()
                        } else {
                            dialog.setError(tag, resources.getString(R.string.input_port_can_not_over_range))
                        }
                    }
                }
            }
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }
}