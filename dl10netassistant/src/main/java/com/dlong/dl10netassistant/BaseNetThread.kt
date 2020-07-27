package com.dlong.dl10netassistant

/**
 * 基础线程工具
 *
 * @author D10NG
 * @date on 2020/4/27 4:41 PM
 */
open class BaseNetThread constructor(
    listener: OnNetThreadListener
) : Thread() {

    /**
     * 获取系统时间戳
     * @return
     */
    val curTime: Long
        get() = System.currentTimeMillis()

    /** 是否连接 */
    open fun isConnected() : Boolean = false

    /** 发送数据 */
    open fun send(data: ByteArray) {}

    /** 给指定地址的指定端口发送数据 */
    open fun send(address: String, toPort: Int, data: ByteArray) {}

    /** 关闭线程 */
    open fun close() {}
}

/** 通讯线程监听器 */
interface OnNetThreadListener {

    /** 链接成功 */
    fun onConnected(ipAddress: String)

    /** 链接失败 */
    fun onConnectFailed(ipAddress: String)

    /** 断开链接 */
    fun onDisconnect(ipAddress: String)

    /** 出错 */
    fun onError(ipAddress: String, error: String)

    /** 接收到客户端链接 */
    fun onAcceptSocket(ipAddress: String)

    /** 接收到数据 */
    fun onReceive(ipAddress: String, port: Int, time: Long, data: ByteArray)
}