package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.databinding.ActivityUdpBroadBinding

class UdpBroadActivity : BaseActivity() {

    private lateinit var binding: ActivityUdpBroadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_udp_broad)

        // 设置返回按钮
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    fun openSetting(view: View) {
        clearGoTo(UdpBroadSettingActivity::class.java)
    }

    fun clean(view: View) {
        when(view.id) {
            R.id.btn_send_clean -> binding.edtSend.setText("")
            R.id.btn_receive_clean -> binding.edtReceive.setText("")
        }
    }

    fun openHistory(view: View) {

    }

    fun send(view: View) {

    }
}
