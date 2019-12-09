package com.dlong.networkdebugassistant.activity

import android.Manifest
import android.os.Bundle
import android.os.Message
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dlong.dialog.ButtonDialog
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.HistoryInfo
import com.dlong.networkdebugassistant.bean.ReceiveInfo
import com.dlong.networkdebugassistant.bean.UdpBroadConfiguration
import com.dlong.networkdebugassistant.constant.DBConstant
import com.dlong.networkdebugassistant.databinding.ActivityUdpBroadBinding
import com.dlong.networkdebugassistant.model.HistoryModel
import com.dlong.networkdebugassistant.thread.UdpBroadThread
import com.dlong.networkdebugassistant.utils.AppUtils
import com.dlong.networkdebugassistant.utils.ByteUtils
import com.dlong.networkdebugassistant.utils.DateUtils
import com.dlong.networkdebugassistant.utils.StringUtils
import java.lang.StringBuilder

class UdpBroadActivity : BaseActivity() {

    private lateinit var binding: ActivityUdpBroadBinding
    private lateinit var config: UdpBroadConfiguration
    private lateinit var viewModel: HistoryModel

    private var thread: UdpBroadThread? = null
    private var disConnectDialog: ButtonDialog? = null

    companion object{
        private const val CHECK_THREAD_ALIVE = 1
        private const val LOOP_SEND = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_udp_broad)
        viewModel = ViewModelProvider(this).get(HistoryModel::class.java)
        config = DBConstant.getInstance(this).getUdpBroadConfiguration()
        // 初始化连接
        thread = UdpBroadThread(this, mHandler, config.localPort)

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

    override fun onResume() {
        super.onResume()
        // 更新配置
        config = DBConstant.getInstance(this).getUdpBroadConfiguration()
    }

    override fun callBack(msg: Message) {
        super.callBack(msg)
        when(msg.what) {
            UdpBroadThread.RECEIVE_MSG -> {
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
        clearGoTo(UdpBroadSettingActivity::class.java)
    }

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
            thread = UdpBroadThread(this, mHandler, config.localPort)
            thread?.start()
            showConnect(true)
            showToast(resources.getString(R.string.connect_success))
        }
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
            thread?.send(config.targetIpAddress, config.targetPort, data.toByteArray())
            // 插入数据库
            viewModel.insertHistory(HistoryInfo(0, text, DateUtils.curTime))
        }
        if (binding.swLoop.isChecked) {
            // 循环发送
            mHandler.sendEmptyMessageDelayed(LOOP_SEND, config.autoSendTime)
        }
    }

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
