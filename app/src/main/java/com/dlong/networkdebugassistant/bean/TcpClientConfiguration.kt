package com.dlong.networkdebugassistant.bean

import com.dlong.jsonentitylib.annotation.DLField

/**
 * Tcp 客户端配置
 *
 * @author D10NG
 * @date on 2019-12-09 14:44
 */
data class TcpClientConfiguration(

    /** 服务器地址 */
    @DLField
    var serverIpAddress: String = "192.168.1.11",

    /** 服务器端口 */
    @DLField
    var serverPort: Int = 8126
) : BaseConfiguration() {
}