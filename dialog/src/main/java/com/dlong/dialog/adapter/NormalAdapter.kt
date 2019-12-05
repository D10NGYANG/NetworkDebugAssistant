package com.dlong.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dlong.dialog.R
import com.dlong.dialog.databinding.DialogNormalItemViewBinding

/**
 * @author D10NG
 * @date on 2019-11-25 16:46
 */
class NormalAdapter constructor(
    select: String,
    list: List<String>
) : RecyclerView.Adapter<NormalAdapter.ViewHolder>() {

    /** 选中字符串 */
    var selectStr = select
    /** 字符串数据列表 */
    var mList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding : DialogNormalItemViewBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_normal_item_view, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = this.mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.mList[position])
        holder.binding.text.setOnClickListener {
            // 改变选中字符串
            this.selectStr = this.mList[position]
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder constructor(
        val binding: DialogNormalItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.itemText = text
            binding.isSelected = text == selectStr
            binding.executePendingBindings()
        }
    }
}