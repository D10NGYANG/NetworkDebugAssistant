package com.dlong.dl10netassistant

import android.content.Context
import android.net.wifi.WifiManager
import java.net.*

/**
 * udp 组播线程
 *
 * @author D10NG
 * @date on 2019-12-09 10:24
 */
class UdpMultiThread constructor(
    // 上下文
    private val mContext: Context,
    // 组播地址
    private val multiAddress: String,
    // 端口
    private val mPort: Int,
    // 监听器
    private val mListener: OnNetThreadListener
) : BaseNetThread(mListener) {

    private lateinit var mcSocket: MulticastSocket
    /** Wi-Fi锁 */
    private lateinit var mLock: WifiManager.MulticastLock
    /** 运行标记位 */
    private var isRun = false

    override fun run() {
        super.run()

        // 创建锁
        val manager = mContext.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        mLock = manager.createMulticastLock("udp multi wifi")

        try {
            // 启动端口
            mcSocket = MulticastSocket(mPort)
        } catch (e: Exception) {
            // 启动失败
            mListener.onConnectFailed("")
            return
        }
        // 启动成功
        mListener.onConnected("")
        // 加入组
        val group = InetAddress.getByName(multiAddress)
        mcSocket.joinGroup(group)
        mcSocket.timeToLive = 5
        mcSocket.loopbackMode = false

        isRun = true
        var packet: DatagramPacket
        val by = ByteArray(1024)
        while (isRun) {
            // 循环等待接收
            acquireLock()
            packet = DatagramPacket(by, by.size)
            mcSocket.receive(packet)

            // 拿到广播地址
            val address = packet.address.toString().replace("/", "")
            // 拿到数据
            val data = packet.data.copyOfRange(0, packet.length)

            mListener.onReceive(address, packet.port, curTime, data)
        }
        // 关闭
        mcSocket.leaveGroup(group)
        mcSocket.disconnect()
        mcSocket.close()
        releaseLock()
        mListener.onDisconnect("")
    }

    @Synchronized
    private fun acquireLock() {
        if (!mLock.isHeld) {
            mLock.acquire()
        }
    }

    @Synchronized
    private fun releaseLock() {
        if (!mLock.isHeld) {
            mLock.release()
        }
    }

    override fun send(address: String, toPort: Int, data: ByteArray) {
        super.send(address, toPort, data)
        acquireLock()
        Thread(Runnable {
            try {
                val packet = DatagramPacket(data, data.size, InetAddress.getByName(address), toPort)
                mcSocket.send(packet)
            } catch (e: Exception) {
                mListener.onError(address, e.toString())
            }
        }).start()
    }

    override fun isConnected(): Boolean {
        return isRun
    }

    override fun close() {
        isRun = false
    }
}