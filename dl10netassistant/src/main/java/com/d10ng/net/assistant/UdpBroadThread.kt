package com.d10ng.net.assistant

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 * udp 广播线程
 *
 * @author D10NG
 * @date on 2019-12-06 14:59
 */
class UdpBroadThread constructor(
    // 端口
    private val mPort: Int
) : BaseNetThread() {

    constructor(mPort: Int, listener: OnNetThreadListener): this(mPort) {
        super.setThreadListener(listener)
    }

    constructor(mPort: Int, listener: NetThreadListener.() -> Unit): this(mPort) {
        super.setThreadListener(listener)
    }

    private var dgSocket: DatagramSocket? = null
    /** 运行标记位 */
    private var isRun = false

    override fun run() {
        super.run()

        try {
            // 启动端口
            dgSocket = DatagramSocket(null).apply {
                reuseAddress = true
                bind(InetSocketAddress(mPort))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 启动失败
            listener?.onConnectFailed("")
            listenerLambda?.onConnectFailed("")
            return
        }
        // 启动成功
        listener?.onConnected("")
        listenerLambda?.onConnected("")

        isRun = true
        var packet: DatagramPacket
        val by = ByteArray(1024)
        while (isRun) {
            println("test, ${dgSocket?.isConnected}")
            // 循环等待接收
            packet = DatagramPacket(by, by.size)
            dgSocket?.receive(packet)
            // 拿到广播地址
            val address = packet.address.toString().replace("/", "")
            // 拿到数据
            val data = packet.data.copyOfRange(0, packet.length)

            listener?.onReceive(address, packet.port, curTime, data)
            listenerLambda?.onReceive(address, packet.port, curTime, data)
        }
        // 关闭
        dgSocket?.disconnect()
        dgSocket?.close()
        listener?.onDisconnect("")
        listenerLambda?.onDisconnect("")
    }

    override fun isConnected(): Boolean {
        return isRun
    }

    override fun send(address: String, toPort: Int, data: ByteArray) {
        super.send(address, toPort, data)
        Thread {
            try {
                val packet = DatagramPacket(data, data.size, InetAddress.getByName(address), toPort)
                dgSocket?.send(packet)
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.onError(address, e.toString())
                listenerLambda?.onError(address, e.toString())
            }
        }.start()
    }

    override fun close() {
        isRun = false
        super.close()
    }
}