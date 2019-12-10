package com.dlong.networkdebugassistant.thread

/**
 * 基础线程
 *
 * @author D10NG
 * @date on 2019-12-09 15:32
 */
abstract class BaseThread : Thread() {

    abstract fun send(address: String, toPort: Int, data: ByteArray)

    abstract fun send(data: ByteArray)

    abstract fun close()
}