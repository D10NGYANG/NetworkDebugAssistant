package com.dlong.networkdebugassistant.bean

import com.dlong.jsonentitylib.annotation.DLField

/**
 * tcp 服务器配置
 *
 * @author D10NG
 * @date on 2019-12-10 10:48
 */
data class TcpServerConfiguration(
    /** 本地端口 */
    @DLField
    var localPort: Int = 8089
) : BaseConfiguration() {
}