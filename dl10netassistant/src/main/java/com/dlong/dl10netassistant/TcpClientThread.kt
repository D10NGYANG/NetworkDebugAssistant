package com.dlong.dl10netassistant

import java.net.Socket

/**
 * TCP客户端
 *
 * @author D10NG
 * @date on 2020/4/27 4:56 PM
 */
class TcpClientThread constructor(
    // 地址
    private val mAddress: String,
    // 端口
    private val mPort: Int,
    // 监听器
    private val mListener: OnNetThreadListener
) : BaseNetThread(mListener) {

    private lateinit var socket: Socket

    override fun run() {
        super.run()
        try {
            // 连接服务器
            socket = Socket(mAddress, mPort)
            socket.reuseAddress = true
        } catch (e: Exception) {
            // 连接失败
            socket = Socket()
            mListener.onConnectFailed(mAddress)
            return
        }
        // 连接成功
        mListener.onConnected(mAddress)

        // 获取输入流
        val inputStream = socket.getInputStream()
        while (socket.isConnected){
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
                // 接收到数据
                mListener.onReceive(mAddress, mPort, curTime, buffer.copyOfRange(0, len))
            }
        }
        // 已断开连接
        mListener.onDisconnect(mAddress)
    }

    override fun isConnected(): Boolean {
        return socket.isConnected
    }

    override fun send(data: ByteArray) {
        super.send(data)
        if (!isConnected()) return
        Thread(Runnable {
            try {
                socket.getOutputStream()?.write(data)
                socket.getOutputStream()?.flush()
            } catch (e: Exception) {
                mListener.onError(mAddress, e.toString())
            }
        }).start()
    }

    override fun close() {
        super.close()
        socket.close()
    }
}