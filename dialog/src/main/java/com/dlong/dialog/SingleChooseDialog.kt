package com.dlong.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlong.dialog.adapter.NormalAdapter
import com.dlong.dialog.databinding.DialogRecycleViewBinding

/**
 * 单选弹窗
 *
 * @author D10NG
 * @date on 2019-11-25 16:36
 */
class SingleChooseDialog constructor(
    private val context: Context
) : BaseDialog<SingleChooseDialog>(context) {

    init {
        // 改变内容排列方向
        binding.contentLayout.orientation = LinearLayout.HORIZONTAL
        // 改变固定高度
        val params = binding.contentLayout.layoutParams
        params.height = 600
        binding.contentLayout.layoutParams = params
    }

    /** 选择项列表 */
    private val recyclerMap: MutableMap<String, DialogRecycleViewBinding> = mutableMapOf()

    /**
     * 添加选择列表
     * @param tag 标签
     * @param selectItem 选择项
     * @param list 全部选项
     * @param start 开始文本
     * @param end 结束文本
     */
    fun addSelectionList(tag: String, selectItem: String, list: List<String>, start: String, end: String) : SingleChooseDialog {
        val viewBinding: DialogRecycleViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_recycle_view, null, false
        )
        viewBinding.startText = start
        viewBinding.endText = end
        viewBinding.rcv.layoutManager = LinearLayoutManager(context)
        val adapter = NormalAdapter(selectItem, list)
        viewBinding.rcv.adapter = adapter
        viewBinding.rcv.post {
            viewBinding.rcv.smoothScrollToPosition(list.indexOf(selectItem) + 3)
        }
        binding.contentLayout.addView(viewBinding.root)
        recyclerMap[tag] = viewBinding
        return this
    }

    /**
     * 获取选中项文本内容
     */
    fun getSelectOnTag(tag: String) : String {
        val viewBinding = recyclerMap[tag]?: return ""
        val adapter = viewBinding.rcv.adapter as NormalAdapter
        return adapter.selectStr
    }
}