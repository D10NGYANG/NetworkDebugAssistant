package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.UdpMultiConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant
import com.dlong.networkdebugassistant.thread.UdpMultiThread

/**
 * @author D10NG
 * @date on 2019-12-09 11:09
 */
class UdpMultiActivity : BaseSendReceiveActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTittle(resources.getString(R.string.main_udp_multi))
    }

    override fun initConfig() {
        super.initConfig()
        config = DBConstant.getInstance(this).getUdpMultiConfiguration()
    }

    override fun initThread() {
        super.initThread()
        val cc = config as UdpMultiConfiguration
        thread = UdpMultiThread(this, mHandler, cc.targetIpAddress, cc.localPort)
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