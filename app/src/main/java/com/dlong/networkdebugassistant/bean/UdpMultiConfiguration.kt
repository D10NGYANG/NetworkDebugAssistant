package com.dlong.networkdebugassistant.bean

import com.dlong.jsonentitylib.annotation.DLField

/**
 * Udp 组播配置
 *
 * @author D10NG
 * @date on 2019-12-09 10:49
 */
data class UdpMultiConfiguration(
    /** 本地端口 */
    @DLField
    var localPort: Int = 4032,

    /** 目标多播地址 224.0.0.0 ～ 239.255.255.255 */
    @DLField
    var targetIpAddress: String = "234.255.255.255",

    /** 目标端口 */
    @DLField
    var targetPort: Int = 4032

) : BaseConfiguration() {
}