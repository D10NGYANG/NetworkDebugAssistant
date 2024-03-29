package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import com.d10ng.net.assistant.TcpServerThread
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.TcpServerConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant

/**
 * @author D10NG
 * @date on 2019-12-10 11:21
 */
class TcpServerActivity : BaseSendReceiveActivity() {

    private val socketList: MutableList<String> = mutableListOf()
    private lateinit var socketAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTittle(resources.getString(R.string.main_tcp_server))
        binding.isShowSocketList = true

        socketAdapter = ArrayAdapter(this, R.layout.item_socket_spinner, socketList)
        socketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spSocket.adapter = socketAdapter
    }

    override fun initConfig() {
        super.initConfig()
        config = DBConstant.getInstance(this).getTcpServerConfiguration()
    }

    override fun initThread() {
        super.initThread()
        val cc = config as TcpServerConfiguration
        thread = TcpServerThread(cc.localPort, netListener)
    }

    override fun openSetting() {
        super.openSetting()
        clearGoTo(TcpServerSettingActivity::class.java)
    }

    override fun updateSocketList() {
        super.updateSocketList()
        if (thread == null) return
        val oldSelect = (binding.spSocket.selectedItem as String?)?: ""
        val tt = thread as TcpServerThread
        val list = tt.getSocketNameList()
        socketList.clear()
        socketList.addAll(list)
        socketAdapter.notifyDataSetChanged()
        val pos = 0.coerceAtLeast(socketList.indexOf(oldSelect))
        binding.spSocket.setSelection(pos)
    }

    override fun disconnectSocket() {
        super.disconnectSocket()
        val name = (binding.spSocket.selectedItem as String?)?: ""
        val tt = thread as TcpServerThread
        val socket = tt.getSocketByName(name)?: return
        tt.disconnectSocket(socket)
    }

    override fun sendData(data: ByteArray) {
        super.sendData(data)
        val all = TcpServerThread.ALL_CLIENT_NAME
        val selectSocket = (binding.spSocket.selectedItem as String?)?: all
        if (selectSocket == all) {
            thread?.send(data)
        } else {
            val params = selectSocket.split(":")
            if (params.size != 2) return
            thread?.send(params[0], params[1].toInt(), data)
        }
    }
}