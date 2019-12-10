package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.ArrayAdapter
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.TcpServerConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant
import com.dlong.networkdebugassistant.thread.TcpServerThread

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
        binding.spSocket.visibility = View.VISIBLE

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
        thread = TcpServerThread(this, mHandler, cc.localPort)
    }

    override fun openSetting() {
        super.openSetting()
        clearGoTo(TcpServerSettingActivity::class.java)
    }

    override fun callBack(msg: Message) {
        super.callBack(msg)
        when(msg.what) {
            TcpServerThread.ACCEPT_SOCKET -> {
                updateSocketList()
            }
        }
    }

    /**
     * 更新客户端列表
     */
    private fun updateSocketList() {
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

    override fun sendData(data: ByteArray) {
        super.sendData(data)
        val all = resources.getString(R.string.tcp_server_all_connect_socket)
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