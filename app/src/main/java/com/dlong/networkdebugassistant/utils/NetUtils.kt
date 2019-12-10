package com.dlong.networkdebugassistant.utils

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*


/**
 * @author D10NG
 * @date on 2019-12-10 14:37
 */
object NetUtils {

    /**
     * 获取内网IP地址
     */
    val localIPAddress: String
        get() {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress.toString()
                    }
                }
            }
            return "null"
        }


}