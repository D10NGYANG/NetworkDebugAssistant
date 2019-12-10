package com.dlong.networkdebugassistant.thread

import android.os.Handler
import android.os.Message
import com.dlong.networkdebugassistant.activity.BaseSendReceiveActivity
import com.dlong.networkdebugassistant.bean.ReceiveInfo
import com.dlong.networkdebugassistant.utils.DateUtils
import java.lang.Exception
import java.net.Socket

/**
 * TCP 客户端
 *
 * @author D10NG
 * @date on 2019-12-09 14:37
 */
class TcpClientThread constructor(
    private val mHandler: Handler,
    private val ipAddress: String,
    private val port: Int
) : BaseThread() {

    private var socket: Socket? = null

    override fun run() {
        super.run()
        try {
            socket = Socket(ipAddress, port)
        } catch (e: Exception) {
            socket = Socket()
            return
        }
        val inputStream = socket?.getInputStream()

        while (socket?.isConnected == true){
            val buffer = ByteArray(1024)
            var len: Int
            try {
                len = inputStream?.read(buffer)?: 0
            } catch (e: Exception) {
                break
            }
            if (len > 0) {
                // 包装
                val receive = ReceiveInfo()
                receive.byteData = buffer.copyOfRange(0, len)
                receive.time = DateUtils.curTime
                receive.ipAddress = ipAddress
                receive.port = port

                val m = Message.obtain()
                m.what = BaseSendReceiveActivity.RECEIVE_MSG
                m.obj = receive
                mHandler.sendMessage(m)
            }
        }
    }

    override fun send(address: String, toPort: Int, data: ByteArray) {
        send(data)
    }

    override fun send(data: ByteArray) {
        Thread(Runnable {
            if (socket?.isConnected == true) {
                socket?.getOutputStream()?.write(data)
                socket?.getOutputStream()?.flush()
            }
        }).start()
    }

    override fun close() {
        socket?.close()
    }
}