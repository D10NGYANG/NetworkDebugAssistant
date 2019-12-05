package com.dlong.networkdebugassistant.activity

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dlong.networkdebugassistant.app.BaseHandler
import com.google.android.material.snackbar.Snackbar

/**
 * @author D10NG
 * @date on 2019-12-05 11:03
 */
open class BaseActivity : AppCompatActivity(), BaseHandler.BaseHandlerCallBack {

    lateinit var mHandler: BaseHandler

    override fun callBack(msg: Message) {
        // 回调
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandler = BaseHandler(this, this)
    }

    fun finishGoTo(clz: Class<*>) {
        clearGoTo(clz)
        finish()
    }

    fun clearGoTo(clz: Class<*>) {
        val intent = getClearTopIntent(clz)
        startActivity(intent)
    }

    fun getClearTopIntent(clz: Class<*>) : Intent {
        val intent = Intent(this, clz)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return intent
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }
}