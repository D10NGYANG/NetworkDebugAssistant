package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.view.View
import com.dlong.dl10netassistant.TcpClientThread
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.TcpClientConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant

/**
 * @author D10NG
 * @date on 2019-12-09 14:49
 */
class TcpClientActivity : BaseSendReceiveActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTittle(resources.getString(R.string.main_tcp_client))
        binding.spSocket.visibility = View.GONE
    }

    override fun initConfig() {
        super.initConfig()
        config = DBConstant.getInstance(this).getTcpClientConfiguration()
    }

    override fun initThread() {
        super.initThread()
        val cc = config as TcpClientConfiguration
        thread = TcpClientThread(cc.serverIpAddress, cc.serverPort, netListener)
    }

    override fun openSetting() {
        super.openSetting()
        clearGoTo(TcpClientSettingActivity::class.java)
    }

    override fun sendData(data: ByteArray) {
        super.sendData(data)
        thread?.send(data)
    }
}