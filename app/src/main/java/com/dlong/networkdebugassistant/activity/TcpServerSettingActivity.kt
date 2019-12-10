package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dlong.dialog.BaseDialog
import com.dlong.dialog.ButtonStyle
import com.dlong.dialog.EditDialog
import com.dlong.dialog.OnBtnClick
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.TcpServerConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant
import com.dlong.networkdebugassistant.databinding.ActivityTcpServerSettingBinding

class TcpServerSettingActivity : BaseSettingActivity() {

    private lateinit var binding: ActivityTcpServerSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tcp_server_setting)

        // 设置返回按钮
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        initContentBinding(binding.contentBase)
        initConfig()
    }

    override fun initConfig() {
        super.initConfig()
        // 初始化配置信息
        binding.contentBase.config = DBConstant.getInstance(this).getTcpServerConfiguration()
        updateConfigShow()
    }

    override fun updateConfigShow() {
        binding.contentBase.config = binding.contentBase.config?: TcpServerConfiguration()
        val cc = binding.contentBase.config as TcpServerConfiguration
        binding.localPort = "${cc.localPort}"
        // 保存配置信息
        DBConstant.getInstance(this).setTcpServerConfiguration(cc)
    }

    fun setLocalPort(view: View) {
        val cc = binding.contentBase.config as TcpServerConfiguration
        val tag = "port"
        EditDialog(this)
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.local_port))
            .addEdit(tag, "${cc.localPort}", resources.getString(R.string.please_input_port))
            .setInputType(tag, InputType.TYPE_CLASS_NUMBER)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME, object : OnBtnClick {
                override fun click(d0: BaseDialog<*>, text: String) {
                    val dialog = d0 as EditDialog
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
            })
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }
}
