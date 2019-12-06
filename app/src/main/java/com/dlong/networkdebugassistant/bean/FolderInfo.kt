package com.dlong.networkdebugassistant.bean

import com.dlong.networkdebugassistant.R
import java.io.Serializable

/**
 * 文件夹信息
 *
 * @author D10NG
 * @date on 2019-12-06 10:04
 */
data class FolderInfo(
    /** 图标 */
    var iconRes: Int = R.mipmap.icon_folder,
    /** 名称 */
    var name: String = "",
    /** 子文件个数 */
    var sonNum: Long = 0L,
    /** 最后修改时间 */
    var lastEditTime: String = "",
    /** 路径 */
    var path: String = ""
) : Serializable {
}