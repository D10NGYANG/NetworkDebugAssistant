package com.dlong.networkdebugassistant.activity

import android.os.Bundle
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
}
