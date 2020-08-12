package com.dlong.dl10netassistant

import android.util.Log
import java.net.ServerSocket
import java.net.Socket

/**
 * TCP 服务器
 *
 * @author D10NG
 * @date on 2020/4/27 5:16 PM
 */
class TcpServerThread constructor(
    // 服务器端口
    private val mPort: Int
) : BaseNetThread() {

    constructor(mPort: Int, listener: OnNetThreadListener): this(mPort) {
        super.setThreadListener(listener)
    }

    constructor(mPort: Int, listener: NetThreadListener.() -> Unit): this(mPort) {
        super.setThreadListener(listener)
    }

    private lateinit var serverSocket: ServerSocket
    /** 连接列表 */
    private val socketList: MutableMap<String, Socket> = mutableMapOf()
    /** 运行标记位 */
    private var isRun = false

    companion object {
        const val ALL_CLIENT_NAME = "all clients"
    }

    override fun run() {
        super.run()
        try {
            // 打开服务器
            serverSocket = ServerSocket(mPort)
        } catch (e: Exception) {
            // 打开服务器失败
            serverSocket = ServerSocket()
            listener?.onConnectFailed("")
            listenerLambda?.onConnectFailed("")
            return
        }
        // 打开服务器成功
        listener?.onConnected("")
        listenerLambda?.onConnected("")
        serverSocket.reuseAddress = true

        isRun = true
        while (isRun) {
            val socket =
                try {
                    serverSocket.accept()
                } catch (e: Exception) {
                    listener?.onError("", e.toString())
                    listenerLambda?.onError("", e.toString())
                    break
                }
            if (socket != null) {
                val key = socket.remoteSocketAddress.toString()
                Log.e("Tcp Server Accept", "address=$key")
                socketList[key] = socket
                // 开始循环监听
                startAcceptSocket(key, socket)
            }
        }
        // 服务器关闭
        serverSocket.close()
        listener?.onDisconnect("")
        listenerLambda?.onDisconnect("")
    }

    /** 启动循环监听连接消息 */
    private fun startAcceptSocket(key: String, socket: Socket) {
        // 有客户端连接进来
        listener?.onAcceptSocket(socket.remoteSocketAddress.toString())
        listenerLambda?.onAcceptSocket(socket.remoteSocketAddress.toString())
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
                    // 接受数据
                    listener?.onReceive(socket.remoteSocketAddress.toString(),
                        socket.port, curTime, buffer.copyOfRange(0, len))
                    listenerLambda?.onReceive(socket.remoteSocketAddress.toString(),
                        socket.port, curTime, buffer.copyOfRange(0, len))
                }
            }
            // 断开连接
            Log.e("TCP SERVER", "$key 已断开连接")
            listener?.onDisconnect(socket.remoteSocketAddress.toString())
            listenerLambda?.onDisconnect(socket.remoteSocketAddress.toString())
            socketList.remove(key)
        }).start()
    }

    /** 获取名字列表 */
    fun getSocketNameList() : List<String> {
        val list: MutableList<String> = mutableListOf()
        list.add(ALL_CLIENT_NAME)
        for (socket in socketList.values) {
            list.add(socket.remoteSocketAddress.toString().replace("/", ""))
        }
        return list.toList()
    }

    override fun isConnected(): Boolean {
        return isRun
    }

    override fun send(address: String, toPort: Int, data: ByteArray) {
        super.send(address, toPort, data)
        Thread(Runnable {
            try {
                val socket = socketList["/$address:$toPort"]?: return@Runnable
                socket.getOutputStream().write(data)
                socket.getOutputStream().flush()
            } catch (e: Exception) {
                listener?.onError(address, e.toString())
                listenerLambda?.onError(address, e.toString())
            }
        }).start()
    }

    override fun send(data: ByteArray) {
        super.send(data)
        Thread(Runnable {
            try {
                for (socket in socketList.values) {
                    socket.getOutputStream().write(data)
                    socket.getOutputStream().flush()
                }
            } catch (e: Exception) {
                listener?.onError("", e.toString())
                listenerLambda?.onError("", e.toString())
            }
        }).start()
    }

    override fun close() {
        super.close()
        isRun = false
    }
}