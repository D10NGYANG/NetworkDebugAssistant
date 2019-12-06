package com.dlong.networkdebugassistant.adapter

import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.bean.FolderInfo
import com.dlong.networkdebugassistant.databinding.ItemFileBinding

/**
 * 文件夹列表适配器
 *
 * @author D10NG
 * @date on 2019-12-06 10:29
 */
class FolderAdapter constructor(
    private val mHandler: Handler,
    private var mList: MutableList<FolderInfo>
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    companion object{
        const val FOLDER_SELECT = 100
        const val FOLDER_EDIT = 101
    }

    fun update(list: MutableList<FolderInfo>) {
        this.mList.clear()
        this.mList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder constructor(
        val binding: ItemFileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(info: FolderInfo) {
            binding.folderInfo = info
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFileBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_file, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = this.mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.mList[position])
        holder.binding.llContent.setOnClickListener {
            val m = Message.obtain()
            m.what = FOLDER_SELECT
            m.obj = this.mList[position]
            mHandler.sendMessage(m)
        }
        holder.binding.llContent.setOnLongClickListener {
            val m = Message.obtain()
            m.what = FOLDER_EDIT
            m.obj = this.mList[position]
            mHandler.sendMessage(m)
        }
    }
}