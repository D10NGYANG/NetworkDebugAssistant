package com.dlong.networkdebugassistant.activity

import android.os.Bundle
import android.os.Message
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.adapter.HistoryAdapter
import com.dlong.networkdebugassistant.bean.HistoryInfo
import com.dlong.networkdebugassistant.databinding.ActivityHistoryBinding
import com.dlong.networkdebugassistant.model.HistoryModel
import com.dlong.networkdebugassistant.utils.AppUtils

class HistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        viewModel = ViewModelProvider(this).get(HistoryModel::class.java)
        historyAdapter = HistoryAdapter(mHandler, emptyList())

        // 设置返回按钮
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // 初始化列表
        binding.rcv.layoutManager = LinearLayoutManager(this)
        binding.rcv.adapter = historyAdapter

        // 绑定数据
        viewModel.getAllData().observe(this, Observer { list ->
            list?.let {
                historyAdapter.update(it)
            }
        })
    }

    override fun callBack(msg: Message) {
        super.callBack(msg)
        when(msg.what) {
            HistoryAdapter.CLICK_COPY -> {
                val info = msg.obj as HistoryInfo
                AppUtils.copyToClipboard(this, info.text)
                showToast(resources.getString(R.string.copy_to_clip_success))
            }
        }
    }
}
