package com.dlong.networkdebugassistant.adapter

import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.HistoryInfo
import com.dlong.networkdebugassistant.databinding.ItemHistoryBinding
import com.dlong.networkdebugassistant.utils.AppUtils
import kotlinx.android.synthetic.main.item_file.view.*

/**
 * @author D10NG
 * @date on 2019-12-07 14:53
 */
class HistoryAdapter constructor(
    private val mHandler: Handler,
    private var mList: List<HistoryInfo>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    companion object{
        const val CLICK_COPY = 1
        const val LONG_CLICK = 2
    }

    fun update(list: List<HistoryInfo>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder constructor(
        val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(info: HistoryInfo) {
            binding.historyInfo = info
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemHistoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_history, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = this.mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.mList[position])
        holder.binding.btnCopy.setOnClickListener {
            val m = Message.obtain()
            m.what = CLICK_COPY
            m.obj = this.mList[position]
            mHandler.sendMessage(m)
        }
        holder.binding.txtContent.setOnClickListener(object : View.OnClickListener{
            private var isShowAll = false
            override fun onClick(p0: View?) {
                isShowAll = !isShowAll
                if (isShowAll) {
                    holder.binding.txtContent.isSingleLine = false
                    holder.binding.txtContent.ellipsize = null
                } else {
                    holder.binding.txtContent.isSingleLine = true
                    holder.binding.txtContent.ellipsize = TextUtils.TruncateAt.END
                }
            }
        })
        holder.binding.txtContent.setOnLongClickListener {
            val m = Message.obtain()
            m.what = LONG_CLICK
            m.obj = this.mList[position]
            mHandler.sendMessage(m)
        }
    }
}