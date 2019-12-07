package com.dlong.networkdebugassistant.bean

import java.io.Serializable

/**
 * 接收到的信息实体类
 *
 * @author D10NG
 * @date on 2019-12-06 15:03
 */
data class ReceiveInfo(
    /** 字节数据 */
    var byteData: ByteArray = byteArrayOf(),
    /** 时间 */
    var time: Long = 0L,
    /** 地址 */
    var ipAddress: String = "",
    /** 端口 */
    var port: Int = 0
) : Serializable {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReceiveInfo

        if (!byteData.contentEquals(other.byteData)) return false
        if (time != other.time) return false
        if (ipAddress != other.ipAddress) return false
        if (port != other.port) return false
        return true
    }

    override fun hashCode(): Int {
        return (byteData.contentHashCode() + time + ipAddress.hashCode() + port).toInt()
    }
}