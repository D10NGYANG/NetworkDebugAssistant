package com.dlong.networkdebugassistant.app

import android.os.Handler
import android.os.Message

import androidx.appcompat.app.AppCompatActivity

import java.lang.ref.WeakReference

/**
 * 封装Handler子类
 * $ 解决handler内存泄漏问题
 *
 * @author D10NG
 * @date on 2019-09-28 11:11
 */
class BaseHandler(c: AppCompatActivity, private val callBack: BaseHandlerCallBack) : Handler() {

    private val act: WeakReference<AppCompatActivity> = WeakReference(c)

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val c = act.get()
        if (c != null) {
            callBack.callBack(msg)
        }
    }

    interface BaseHandlerCallBack {
        fun callBack(msg: Message)
    }
}

