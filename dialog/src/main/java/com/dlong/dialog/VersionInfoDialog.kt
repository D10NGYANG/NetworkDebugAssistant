package com.dlong.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.dlong.dialog.databinding.DialogVersionViewBinding

/**
 * 版本信息弹窗
 *
 * @author D10NG
 * @date on 2019-11-27 14:28
 */
class VersionInfoDialog constructor(
    private val context: Context
) : BaseDialog<VersionInfoDialog>(context) {

    init {
        // 改变宽度
        val params = binding.contentLayout.layoutParams
        params.width = LinearLayout.LayoutParams.MATCH_PARENT
        binding.contentLayout.layoutParams = params
    }

    /**
     * 添加版本信息显示
     */
    fun addVersionInfo(version: String, time: String, author: String, content: String) : VersionInfoDialog {
        val viewBinding: DialogVersionViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_version_view, null, false
        )
        viewBinding.version = version
        viewBinding.time = time
        viewBinding.author = author
        viewBinding.content = content
        binding.contentLayout.addView(viewBinding.root)
        return this
    }
}