package com.d10ng.net.assistant

/**
 * 基础线程工具
 *
 * @author D10NG
 * @date on 2020/4/27 4:41 PM
 */
open class BaseNetThread constructor() : Thread() {

    constructor(listener: NetThreadListener.() -> Unit): this() {
        this.listenerLambda = NetThreadListener()
        this.listenerLambda?.listener()
    }

    constructor(listener: OnNetThreadListener): this() {
        this.listener = listener
    }

    protected var listener: OnNetThreadListener? = null
    protected var listenerLambda: NetThreadListener? = null

    open fun setThreadListener(listener: OnNetThreadListener) {
        this.listener = listener
    }

    open fun setThreadListener(listener: NetThreadListener.() -> Unit) {
        this.listenerLambda = NetThreadListener()
        this.listenerLambda?.listener()
    }

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

class NetThreadListener: OnNetThreadListener {
    private lateinit var listener1: (ipAddress: String) -> Unit
    fun onThreadConnected(listener: (ipAddress: String) -> Unit) {
        this.listener1 = listener
    }
    override fun onConnected(ipAddress: String) {
        this.listener1.invoke(ipAddress)
    }

    private lateinit var listener2: (ipAddress: String) -> Unit
    fun onThreadConnectFailed(listener: (ipAddress: String) -> Unit) {
        this.listener2 = listener
    }
    override fun onConnectFailed(ipAddress: String) {
        this.listener2.invoke(ipAddress)
    }

    private lateinit var listener3: (ipAddress: String) -> Unit
    fun onThreadDisconnect(listener: (ipAddress: String) -> Unit) {
        this.listener3 = listener
    }
    override fun onDisconnect(ipAddress: String) {
        this.listener3.invoke(ipAddress)
    }

    private lateinit var listener4: (ipAddress: String, error: String) -> Unit
    fun onThreadError(listener: (ipAddress: String, error: String) -> Unit) {
        this.listener4 = listener
    }
    override fun onError(ipAddress: String, error: String) {
        this.listener4.invoke(ipAddress, error)
    }

    private lateinit var listener5: (ipAddress: String) -> Unit
    fun onThreadAcceptSocket(listener: (ipAddress: String) -> Unit) {
        this.listener5 = listener
    }
    override fun onAcceptSocket(ipAddress: String) {
        this.listener5.invoke(ipAddress)
    }

    private lateinit var listener6: (ipAddress: String, port: Int, time: Long, data: ByteArray) -> Unit
    fun onThreadReceive(listener: (ipAddress: String, port: Int, time: Long, data: ByteArray) -> Unit) {
        this.listener6 = listener
    }
    override fun onReceive(ipAddress: String, port: Int, time: Long, data: ByteArray) {
        this.listener6.invoke(ipAddress, port, time, data)
    }
}