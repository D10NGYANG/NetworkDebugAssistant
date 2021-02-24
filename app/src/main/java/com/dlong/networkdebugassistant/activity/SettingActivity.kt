package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dlong.dialog.*
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.databinding.ActivitySettingBinding
import com.dlong.networkdebugassistant.utils.AppUtils

class SettingActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        // 设置返回按钮
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // 显示版本号
        binding.txtVersion = AppUtils.getAppVersion(this)
    }

    /**
     * 显示版本信息弹窗
     */
    fun openVersionDialog(view: View) {
        val info = AppUtils.getVersionReadMe(this, binding.txtVersion?: "")
        VersionInfoDialog(this).create()
            .setTittle(resources.getString(R.string.setting_version))
            .setIcon(R.mipmap.ic_launcher_web)
            .addVersionInfo(info.version, info.time, info.author, info.content)
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME)
            .show()
    }
}
