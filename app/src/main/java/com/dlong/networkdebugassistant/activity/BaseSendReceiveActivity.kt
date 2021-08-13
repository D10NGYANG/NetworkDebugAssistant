package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.os.Message
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.d10ng.stringlib.toHexString
import com.dlong.dialog.*
import com.dlong.dl10netassistant.BaseNetThread
import com.dlong.dl10netassistant.OnNetThreadListener
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.BaseConfiguration
import com.dlong.networkdebugassistant.bean.HistoryInfo
import com.dlong.networkdebugassistant.bean.ReceiveInfo
import com.dlong.networkdebugassistant.databinding.ActivitySendReceiveBinding
import com.dlong.networkdebugassistant.model.HistoryModel
import com.dlong.networkdebugassistant.utils.*
import kotlinx.coroutines.*

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

    protected var thread: BaseNetThread? = null
    protected var connectDialog: ButtonDialog? = null
    protected var disConnectDialog: JustLoadDialog? = null

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
        // 显示当前IP地址
        startCheckIpJob()
    }

    override fun onPause() {
        super.onPause()
        checkIpJob.cancel()
    }

    override fun onDestroy() {
        thread?.close()
        super.onDestroy()
    }

    private var checkIpJob: Job = Job()
    private fun startCheckIpJob() {
        checkIpJob = GlobalScope.launch {
            while (isActive) {
                if (binding.localIpAddress != NetUtils.localIPAddress) {
                    withContext(Dispatchers.Main) {
                        // 显示当前IP地址
                        binding.localIpAddress = NetUtils.localIPAddress
                    }
                }
                delay(1000)
            }
        }
    }

    /** 监听器 */
    protected val netListener = object : OnNetThreadListener {
        override fun onConnected(ipAddress: String) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    connectDialog?.dismiss()
                    showConnect(true)
                    showToast(ipAddress + resources.getString(R.string.connect_success))
                }
            }
        }

        override fun onConnectFailed(ipAddress: String) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    disConnectDialog?.dismiss()
                    showConnect(false)
                    showToast(ipAddress + resources.getString(R.string.connect_failed))
                }
            }
        }

        override fun onDisconnect(ipAddress: String) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    disConnectDialog?.dismiss()
                    showConnect(thread?.isConnected() == true)
                    showToast(ipAddress + resources.getString(R.string.disconnect_success))
                    updateSocketList()
                }
            }
        }

        override fun onError(ipAddress: String, error: String) {
            Log.e("onError", "ip: $ipAddress, error: $error")
        }

        override fun onAcceptSocket(ipAddress: String) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    updateSocketList()
                }
            }
        }

        override fun onReceive(ipAddress: String, port: Int, time: Long, data: ByteArray) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val info = ReceiveInfo(data, time, ipAddress, port)
                    showReceive(info)
                }
            }
        }
    }

    open fun updateSocketList() {

    }

    override fun callBack(msg: Message) {
        super.callBack(msg)
        when(msg.what) {
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
            disConnectDialog = JustLoadDialog(this).create()
            thread?.close()
            thread = null
            mHandler.sendEmptyMessageDelayed(CHECK_THREAD_ALIVE, 100)
        } else {
            // 连接
            connect()
        }
    }

    /**
     * 初始化线程
     */
    open fun initThread() {}

    /**
     * 建立连接
     */
    open fun connect() {
        initThread()
        thread?.start()
        showConnect(true)
        // 弹窗
        connectDialog = ButtonDialog(this).create()
            .setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.connect_ing))
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL) {
                onClick { dialog, _ ->
                    thread = null
                    showConnect(false)
                    showToast(resources.getString(R.string.connect_cancel))
                    dialog.dismiss()
                }
            }
            .startLoad(true)
            .show()
    }

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
     * 断开接入链接
     * @param view View
     */
    fun disconnectSocket(view: View) {
        disconnectSocket()
    }

    open fun disconnectSocket() {}

    /**
     * 显示是否连接
     */
    protected fun showConnect(con: Boolean) {
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
                builder.append(byte.toHexString())
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