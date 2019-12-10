package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.os.Message
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dlong.dialog.ButtonDialog
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.BaseConfiguration
import com.dlong.networkdebugassistant.bean.HistoryInfo
import com.dlong.networkdebugassistant.bean.ReceiveInfo
import com.dlong.networkdebugassistant.databinding.ActivitySendReceiveBinding
import com.dlong.networkdebugassistant.model.HistoryModel
import com.dlong.networkdebugassistant.thread.BaseThread
import com.dlong.networkdebugassistant.utils.AppUtils
import com.dlong.networkdebugassistant.utils.ByteUtils
import com.dlong.networkdebugassistant.utils.DateUtils
import com.dlong.networkdebugassistant.utils.StringUtils
import java.lang.StringBuilder

/**
 * 基本发送和接收页面
 *
 * @author D10NG
 * @date on 2019-12-09 14:55
 */
open class BaseSendReceiveActivity : BaseActivity() {

    protected lateinit var binding: ActivitySendReceiveBinding
    protected lateinit var viewModel: HistoryModel
    protected lateinit var config: BaseConfiguration

    protected var thread: BaseThread? = null
    protected var disConnectDialog: ButtonDialog? = null

    companion object{
        const val CHECK_THREAD_ALIVE = 1
        const val LOOP_SEND = 2
        const val RECEIVE_MSG = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_receive)
        viewModel = ViewModelProvider(this).get(HistoryModel::class.java)
        // 初始化配置
        initConfig()
        // 设置返回按钮
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        // 接收文本滚动方式
        binding.txtReceive.movementMethod = ScrollingMovementMethod.getInstance()
        // 循环发送切换
        binding.swLoop.setOnCheckedChangeListener { p0, p1 ->
            if (p1) {
                send(binding.btnSend)
            } else {
                mHandler.removeMessages(LOOP_SEND)
            }
        }
        // 初始化连接
        showConnect(false)
    }

    /**
     * 获取配置信息
     */
    open fun initConfig() {}

    /**
     * 设置标题
     */
    fun setTittle(tittle: String) {
        binding.toolbar.title = tittle
    }

    override fun onResume() {
        super.onResume()
        initConfig()
    }

    override fun callBack(msg: Message) {
        super.callBack(msg)
        when(msg.what) {
            RECEIVE_MSG -> {
                val info = msg.obj as ReceiveInfo
                showReceive(info)
            }
            CHECK_THREAD_ALIVE -> {
                if (thread?.isAlive == true) {
                    mHandler.sendEmptyMessageDelayed(CHECK_THREAD_ALIVE, 100)
                } else {
                    disConnectDialog?.dismiss()
                    showConnect(false)
                    showToast(resources.getString(R.string.disconnect_success))
                }
            }
            LOOP_SEND -> {
                send(binding.btnSend)
            }
        }
    }

    fun openSetting(view: View) {
        openSetting()
    }

    /**
     * 跳转设置页面
     */
    open fun openSetting() {}

    fun connectOrNot(view: View) {
        if (binding.isConnect) {
            // 断开
            disConnectDialog = ButtonDialog(this).setTittle(resources.getString(R.string.prompt))
                .setMsg(resources.getString(R.string.disconnect_ing))
                .create()
            disConnectDialog?.startLoad(true, 0, 1)
            disConnectDialog?.show()
            thread?.close()
            thread = null
            mHandler.sendEmptyMessageDelayed(CHECK_THREAD_ALIVE, 100)
        } else {
            // 连接
            initThread()
            thread?.start()
            showConnect(true)
            showToast(resources.getString(R.string.connect_success))
        }
    }

    /**
     * 初始化线程
     */
    open fun initThread() {}

    fun clean(view: View) {
        when(view.id) {
            R.id.btn_send_clean -> binding.edtSend.setText("")
            R.id.btn_receive_clean -> {
                binding.txtReceive.text = ""
                binding.txtReceive.scrollTo(0,  0)
            }
        }
    }

    fun openHistory(view: View) {
        clearGoTo(HistoryActivity::class.java)
    }

    fun send(view: View) {
        val text = binding.edtSend.text.toString()
        val temp = if (config.isSendHex) {
            StringUtils.getByteFromHex(text)
        } else {
            text.toByteArray()
        }
        if (temp.isEmpty()) return
        val data = mutableListOf<Byte>()
        data.addAll(temp.asList())
        if (config.isSendHex && config.isAutoAddHexCheck) {
            data.add(ByteUtils.getEndNum(temp))
        }
        if (thread?.isAlive == true) {
            sendData(data.toByteArray())
            // 插入数据库
            viewModel.insertHistory(HistoryInfo(0, text, DateUtils.curTime))
        }
        if (binding.swLoop.isChecked) {
            // 循环发送
            mHandler.sendEmptyMessageDelayed(LOOP_SEND, config.autoSendTime)
        }
    }

    /**
     * 发送数据
     */
    open fun sendData(data: ByteArray) {}

    fun copy(view: View) {
        AppUtils.copyToClipboard(this, binding.txtReceive.text.toString())
        showToast(resources.getString(R.string.copy_to_clip_success))
    }

    /**
     * 显示是否连接
     */
    private fun showConnect(con: Boolean) {
        binding.isConnect = con
        if (binding.isConnect) {
            binding.btnConnect.setImageResource(R.mipmap.icon_connect)
        } else {
            binding.btnConnect.setImageResource(R.mipmap.icon_disconnect)
        }
    }

    /**
     * 显示接收数据
     */
    private fun showReceive(receiveInfo: ReceiveInfo) {
        val builder = StringBuilder()
        if (config.isReceiveShowTime) {
            builder.append("[")
            builder.append(DateUtils.getDateStr(receiveInfo.time, "yyyy-MM-dd hh:mm:ss"))
            builder.append("]")
        }
        if (config.isReceiveShowIpAddress) {
            builder.append("[")
            builder.append(receiveInfo.ipAddress)
            builder.append("]")
        }
        if (config.isReceiveShowPort) {
            builder.append("[")
            builder.append(receiveInfo.port)
            builder.append("]")
        }
        if (config.isReceiveHex) {
            for (byte in receiveInfo.byteData.iterator()) {
                builder.append(StringUtils.upToNString(byte.toUInt().toString(16), 2))
                builder.append(" ")
            }
        } else {
            builder.append(String(receiveInfo.byteData, 0, receiveInfo.byteData.size))
        }
        builder.append("\r\n")
        binding.txtReceive.append(builder.toString())
        val offset = binding.txtReceive.lineCount * (binding.txtReceive.lineHeight)
        if (offset > binding.txtReceive.height) {
            binding.txtReceive.scrollTo(0, offset - binding.txtReceive.height)
        }
        // 自动保存
        if (config.isAutoSaveToLocal) {
            saveReceiveDataToLocal(receiveInfo, config.receiveSaveLocalPath, config.isReceiveHex)
        }
    }
}