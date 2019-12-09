package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 左上角点击事件
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { clearGoTo(SettingActivity::class.java) }
    }

    fun goAct(view: View) {
        when(view.id) {
            R.id.btn_udp -> clearGoTo(UdpBroadActivity::class.java)
            R.id.btn_udp_multi -> clearGoTo(UdpMultiActivity::class.java)
        }
    }
}
