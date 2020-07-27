package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.view.View
import com.dlong.dl10netassistant.UdpMultiThread
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.UdpMultiConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant

/**
 * @author D10NG
 * @date on 2019-12-09 11:09
 */
class UdpMultiActivity : BaseSendReceiveActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTittle(resources.getString(R.string.main_udp_multi))
        binding.spSocket.visibility = View.GONE
    }

    override fun initConfig() {
        super.initConfig()
        config = DBConstant.getInstance(this).getUdpMultiConfiguration()
    }

    override fun initThread() {
        super.initThread()
        val cc = config as UdpMultiConfiguration
        thread = UdpMultiThread(this, cc.targetIpAddress, cc.localPort, netListener)
    }

    override fun openSetting() {
        super.openSetting()
        clearGoTo(UdpMultiSettingActivity::class.java)
    }

    override fun sendData(data: ByteArray) {
        super.sendData(data)
        val cc = config as UdpMultiConfiguration
        thread?.send(cc.targetIpAddress, cc.targetPort, data)
    }
}