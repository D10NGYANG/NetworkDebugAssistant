package com.d10ng.net.assistant

import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket

/**
 * udp 组播线程
 *
 * @author D10NG
 * @date on 2019-12-09 10:24
 */
class UdpMultiThread constructor(
    // 组播地址
    private val multiAddress: String,
    // 端口
    private val mPort: Int
) : BaseNetThread() {

    constructor(multiAddress: String, mPort: Int, listener: OnNetThreadListener): this(multiAddress, mPort) {
        super.setThreadListener(listener)
    }

    constructor(multiAddress: String, mPort: Int, listener: NetThreadListener.() -> Unit): this(multiAddress, mPort) {
        super.setThreadListener(listener)
    }

    private var mcSocket: MulticastSocket? = null
    /** 运行标记位 */
    private var isRun = false

    override fun run() {
        super.run()

        try {
            // 启动端口
            mcSocket = MulticastSocket(mPort)
        } catch (e: Exception) {
            // 启动失败
            e.printStackTrace()
            listener?.onConnectFailed("")
            listenerLambda?.onConnectFailed("")
            return
        }
        // 启动成功
        listener?.onConnected("")
        listenerLambda?.onConnected("")
        // 加入组
        val group = InetAddress.getByName(multiAddress)
        mcSocket?.joinGroup(group)
        mcSocket?.timeToLive = 5
        mcSocket?.loopbackMode = false

        isRun = true
        var packet: DatagramPacket
        val by = ByteArray(1024)
        while (isRun) {
            // 循环等待接收
            packet = DatagramPacket(by, by.size)
            mcSocket?.receive(packet)

            // 拿到广播地址
            val address = packet.address.toString().replace("/", "")
            // 拿到数据
            val data = packet.data.copyOfRange(0, packet.length)

            listener?.onReceive(address, packet.port, curTime, data)
            listenerLambda?.onReceive(address, packet.port, curTime, data)
        }
        // 关闭
        mcSocket?.leaveGroup(group)
        mcSocket?.disconnect()
        mcSocket?.close()
        listener?.onDisconnect("")
        listenerLambda?.onDisconnect("")
    }
    override fun send(address: String, toPort: Int, data: ByteArray) {
        super.send(address, toPort, data)
        Thread {
            try {
                val packet = DatagramPacket(data, data.size, InetAddress.getByName(address), toPort)
                mcSocket?.send(packet)
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.onError(address, e.toString())
                listenerLambda?.onError(address, e.toString())
            }
        }.start()
    }

    override fun isConnected(): Boolean {
        return isRun
    }

    override fun close() {
        isRun = false
    }
}