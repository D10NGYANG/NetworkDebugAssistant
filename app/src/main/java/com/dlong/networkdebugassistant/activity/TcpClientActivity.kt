package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.os.Message
import android.view.View
import com.dlong.dialog.BaseDialog
import com.dlong.dialog.ButtonDialog
import com.dlong.dialog.ButtonStyle
import com.dlong.dialog.OnBtnClick
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.TcpClientConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant
import com.dlong.networkdebugassistant.thread.TcpClientThread

/**
 * @author D10NG
 * @date on 2019-12-09 14:49
 */
class TcpClientActivity : BaseSendReceiveActivity() {

    private var connectDialog: ButtonDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTittle(resources.getString(R.string.main_tcp_client))
        binding.spSocket.visibility = View.GONE
    }

    override fun initConfig() {
        super.initConfig()
        config = DBConstant.getInstance(this).getTcpClientConfiguration()
    }

    override fun initThread() {
        super.initThread()
        val cc = config as TcpClientConfiguration
        thread = TcpClientThread(mHandler, cc.serverIpAddress, cc.serverPort)
    }

    override fun openSetting() {
        super.openSetting()
        clearGoTo(TcpClientSettingActivity::class.java)
    }

    override fun connect() {
        initThread()
        thread?.start()
        // 弹窗
        connectDialog = ButtonDialog(this).setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.connect_ing))
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, object : OnBtnClick{
                override fun click(d0: BaseDialog<*>, text: String) {
                    thread = null
                    showConnect(false)
                    showToast(resources.getString(R.string.connect_cancel))
                    d0.dismiss()
                }
            })
            .create()
        connectDialog?.startLoad(true, 0, 1)
        connectDialog?.show()
    }

    override fun sendData(data: ByteArray) {
        super.sendData(data)
        thread?.send(data)
    }

    override fun callBack(msg: Message) {
        super.callBack(msg)
        when(msg.what) {
            TcpClientThread.CONNECT_SUCCESS -> {
                // 连接成功
                connectDialog?.dismiss()
                showConnect(true)
                showToast(resources.getString(R.string.connect_success))
            }
            TcpClientThread.CONNECT_FAILED -> {
                // 连接失败
                connectDialog?.stopLoad()
                connectDialog?.setMsg(resources.getString(R.string.connect_failed))
                connectDialog?.removeAllButtons()
                connectDialog?.addAction(resources.getString(R.string.sure), ButtonStyle.THEME, null)
                thread?.close()
                thread = null
                showConnect(false)
            }
            TcpClientThread.DISCONNECT -> {
                // 断开连接
                thread?.close()
                thread = null
                showConnect(false)
                showToast(resources.getString(R.string.disconnect_success))
            }
        }
    }
}