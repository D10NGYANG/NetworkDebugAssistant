package com.dlong.networkdebugassistant.thread

import android.os.Handler
import android.os.Message
import android.util.Log
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

    companion object{
        const val CONNECT_SUCCESS = 31
        const val CONNECT_FAILED = 32
        const val DISCONNECT = 33
    }

    override fun run() {
        super.run()

        // 500毫秒后查看连接状态
        /*Thread(Runnable {
            sleep(500)
            if (socket?.isConnected == true) {
                mHandler.sendEmptyMessage(CONNECT_SUCCESS)
            } else {
                mHandler.sendEmptyMessage(CONNECT_FAILED)
            }
        }).start()*/
        try {
            socket = Socket(ipAddress, port)
        } catch (e: Exception) {
            socket = Socket()
            mHandler.sendEmptyMessage(CONNECT_FAILED)
            return
        }
        mHandler.sendEmptyMessage(CONNECT_SUCCESS)

        val inputStream = socket?.getInputStream()

        while (socket?.isConnected == true){
            val buffer = ByteArray(1024)
            val len =
            try {
                inputStream?.read(buffer)?: 0
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
                receive.ipAddress = ipAddress
                receive.port = port

                val m = Message.obtain()
                m.what = BaseSendReceiveActivity.RECEIVE_MSG
                m.obj = receive
                mHandler.sendMessage(m)
            }
        }
        mHandler.sendEmptyMessage(DISCONNECT)
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