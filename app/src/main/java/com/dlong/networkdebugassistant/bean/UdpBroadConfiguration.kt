package com.dlong.networkdebugassistant.bean

import com.dlong.jsonentitylib.BaseJsonEntity
import com.dlong.jsonentitylib.annotation.DLField

/**
 * Udp广播配置
 *
 * @author D10NG
 * @date on 2019-12-05 15:11
 */
data class UdpBroadConfiguration (

    /** 端口 */
    @DLField
    var port: Int = 8089,

    /** 发送 是否以16进制格式 */
    @DLField
    var isSendHex: Boolean = false,

    /** 发送 是否自动添加最后一位校验位 */
    @DLField
    var isAutoAddHexCheck: Boolean = false,

    /** 发送 是否持续循环发送 */
    @DLField
    var isAutoLoopSend: Boolean = false,

    /** 发送 自动循环发送时间间隔 毫秒 */
    @DLField
    var autoSendTime: Long = 1000L,

    /** 接收 是否以16进制格式 */
    @DLField
    var isReceiveHex: Boolean = false,

    /** 接收 是否显示接收时间 */
    @DLField
    var isReceiveShowTime: Boolean = false,

    /** 接收 是否显示IP地址 */
    @DLField
    var isReceiveShowIpAddress: Boolean = false,

    /** 接收 是否显示端口 */
    @DLField
    var isReceiveShowPort: Boolean = false,

    /** 接收 存储本地地址 */
    @DLField
    var receiveSaveLocalPath: String = "NULL"

) : BaseJsonEntity() {
}