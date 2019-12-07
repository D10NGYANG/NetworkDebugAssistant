package com.dlong.networkdebugassistant.utils

import java.lang.StringBuilder

/**
 * byte 转换工具
 *
 * @author D10NG
 * @date on 2019-11-23 16:52
 */
object ByteUtils {

    /**
     * 检验校验和
     * @param data 数据，最后一个byte为校验和
     */
    fun checkEndNum(data: ByteArray) : Boolean {
        var num = (0).toByte()
        for (i in 0 until data.size -1) {
            num = (num + data[i]).toByte()
        }
        return num == data[data.size -1]
    }

    /**
     * 获取校验和
     * @param data 数据
     */
    fun getEndNum(data: ByteArray) : Byte {
        var num = (0).toByte()
        for (element in data) {
            num = (num + element).toByte()
        }
        return num
    }

    /**
     * 将 byte 转为 8位二进制字符串 "00110011"
     * @param byte
     */
    fun getBinFromByte(byte: Byte) : String {
        val str = Integer.toBinaryString(byte.toInt())
        return StringUtils.upToNString(str, 8)
    }

    /**
     * 将 boolean 数组 转换为 byte
     * @param bools
     */
    fun getByteFromBool(vararg bools: Boolean) : Byte {
        val builder = StringBuilder()
        for (b in bools.iterator()) {
            builder.append(if (b) "1" else "0")
        }
        return getByteFromBin(builder.toString())
    }

    /**
     * 将二进制字符串 "00110011" 转为 byte
     * @param bin
     */
    fun getByteFromBin(bin: String) : Byte {
        val value = Integer.valueOf(bin, 2)
        return value.toByte()
    }

    /**
     * 将两个字节的byte数组转换成有符号整型
     * @param byte1 高位
     * @param byte2 低位
     */
    fun convertSignInt(byte1: Byte, byte2: Byte): Int =
        (byte1.toInt() shl 8) or (byte2.toInt() and 0xFF)

    /**
     * 将两个字节的byte数组转换成无符号整型
     * @param byte1 高位
     * @param byte2 低位
     */
    fun convertUnSignInt(byte1: Byte, byte2: Byte): Int =
        (byte1.toInt() and 0xFF) shl 8 or (byte2.toInt() and 0xFF)

    /**
     * 获取整型数据的 高位 byte
     * @param value 整型数据
     */
    fun convertUnSignByteHeight(value: Int): Byte =
        value.ushr(8).toByte()

    /**
     * 获取整型数据的 低位 byte
     * @param value 整型数据
     */
    fun convertUnSignByteLow(value: Int): Byte =
        (value and 0xff).toByte()
}