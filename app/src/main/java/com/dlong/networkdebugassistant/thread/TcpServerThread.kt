package com.dlong.networkdebugassistant.thread

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.activity.BaseSendReceiveActivity
import com.dlong.networkdebugassistant.bean.ReceiveInfo
import com.dlong.networkdebugassistant.utils.DateUtils
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket

/**
 * TCP 服务器
 *
 * @author D10NG
 * @date on 2019-12-10 10:53
 */
class TcpServerThread constructor(
    private val mContext: Context,
    private val mHandler: Handler,
    private val port: Int
) : BaseThread(){

    private lateinit var serverSocket: ServerSocket
    private val socketList: MutableMap<String, Socket> = mutableMapOf()
    private var isRun = false

    companion object{
        private const val TAG = "TcpServerThread"

        const val ACCEPT_SOCKET = 40
    }

    override fun run() {
        super.run()
        serverSocket = ServerSocket(port)
        serverSocket.reuseAddress = true
        isRun = true
        while (isRun) {
            val socket =
            try {
                serverSocket.accept()
            } catch (e: Exception) {
                return
            }
            if (socket != null) {
                val key = socket.remoteSocketAddress.toString()
                Log.e("测试", "key=$key")
                socketList[key] = socket
                // 开始循环监听
                startAcceptSocket(key, socket)
            }
        }
    }

    private fun startAcceptSocket(key: String, socket: Socket) {
        mHandler.sendEmptyMessage(ACCEPT_SOCKET)
        Thread(Runnable {
            val inputStream = socket.getInputStream()
            while (isRun && socket.isConnected) {
                val buffer = ByteArray(1024)
                val len =
                    try {
                        inputStream.read(buffer)
                    } catch (e: Exception) {
                        -1
                    }
                if (len == -1) {
                    break
                }
                if (len > 0) {
                    // 包装
                    val receive = ReceiveInfo()
                    receive.byteData = buffer.copyOfRange(0, len)
                    receive.time = DateUtils.curTime
                    receive.ipAddress = socket.remoteSocketAddress.toString()
                    receive.port = socket.port

                    val m = Message.obtain()
                    m.what = BaseSendReceiveActivity.RECEIVE_MSG
                    m.obj = receive
                    mHandler.sendMessage(m)
                }
            }
            Log.e(TAG, "$key 已断开连接")
            socketList.remove(key)
        }).start()
    }

    /**
     * 获取名字列表
     */
    fun getSocketNameList() : List<String> {
        val list: MutableList<String> = mutableListOf()
        list.add(mContext.resources.getString(R.string.tcp_server_all_connect_socket))
        for (socket in socketList.values) {
            list.add(socket.remoteSocketAddress.toString().replace("/", ""))
        }
        return list.toList()
    }

    override fun send(address: String, toPort: Int, data: ByteArray) {
        Thread(Runnable {
            val socket = socketList["/$address:$toPort"]?: return@Runnable
            socket.getOutputStream().write(data)
            socket.getOutputStream().flush()
        }).start()
    }

    override fun send(data: ByteArray) {
        Thread(Runnable {
            for (socket in socketList.values) {
                socket.getOutputStream().write(data)
                socket.getOutputStream().flush()
            }
        }).start()
    }

    override fun close() {
        isRun = false
        for (socket in socketList.values) {
            socket.close()
        }
        serverSocket.close()
    }
}