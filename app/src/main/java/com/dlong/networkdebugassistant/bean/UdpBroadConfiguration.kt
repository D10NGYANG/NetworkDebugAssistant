package com.dlong.networkdebugassistant.bean

import com.dlong.jsonentitylib.annotation.DLField

/**
 * Udp广播配置
 *
 * @author D10NG
 * @date on 2019-12-05 15:11
 */
data class UdpBroadConfiguration (

    /** 本地端口 */
    @DLField
    var localPort: Int = 8089,

    /** 目标地址 */
    @DLField
    var targetIpAddress: String = "255.255.255.255",

    /** 目标端口 */
    @DLField
    var targetPort: Int = 8089

) : BaseConfiguration() {
}