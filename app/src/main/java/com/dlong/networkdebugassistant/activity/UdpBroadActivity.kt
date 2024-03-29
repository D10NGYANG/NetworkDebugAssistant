package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import com.d10ng.net.assistant.UdpBroadThread
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.UdpBroadConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant

class UdpBroadActivity : BaseSendReceiveActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTittle(resources.getString(R.string.main_udp_broad))
        binding.isShowSocketList = false
    }

    override fun initConfig() {
        super.initConfig()
        config = DBConstant.getInstance(this).getUdpBroadConfiguration()
    }

    override fun initThread() {
        super.initThread()
        val cc = config as UdpBroadConfiguration
        thread = UdpBroadThread( cc.localPort, netListener)
    }

    override fun openSetting() {
        super.openSetting()
        clearGoTo(UdpBroadSettingActivity::class.java)
    }

    override fun sendData(data: ByteArray) {
        super.sendData(data)
        val cc = config as UdpBroadConfiguration
        thread?.send(cc.targetIpAddress, cc.targetPort, data)
    }
}
