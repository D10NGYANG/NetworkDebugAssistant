package com.dlong.dialog

/**
 * 弹窗按钮点击接口
 *
 * @author D10NG
 * @date on 2019-10-26 15:40
 */
interface OnBtnClick {
    fun click(d0: BaseDialog<*>, text: String)
}