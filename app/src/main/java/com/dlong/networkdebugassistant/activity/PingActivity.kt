package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dlong.dialog.JustLoadDialog
import com.dlong.dl10netassistant.ping
import com.dlong.dl10netassistant.pingOnce
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.databinding.ActivityPingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * PING
 * @Author: D10NG
 * @Time: 2021/2/24 11:21 上午
 */
class PingActivity : BaseActivity() {

    private lateinit var binding: ActivityPingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ping)

        // 接收文本滚动方式
        binding.txtReceive.movementMethod = ScrollingMovementMethod.getInstance()

        // 点击返回
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    fun sure(view: View) {
        val address = binding.edtAddress.text?.toString()
        if (address.isNullOrEmpty()) {
            return
        }
        GlobalScope.launch {
            ping(address).collect {
                withContext(Dispatchers.Main) {
                    binding.txtReceive.append(it)
                }
            }
            val isSuccess = pingOnce(address)
            withContext(Dispatchers.Main) {
                showToast("PING测试结果=$isSuccess")
            }
        }
    }

    fun clean(view: View) {
        binding.txtReceive.text = ""
        binding.txtReceive.scrollTo(0,  0)
    }
}