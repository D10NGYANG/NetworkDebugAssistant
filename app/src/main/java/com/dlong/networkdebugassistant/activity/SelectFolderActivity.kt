package com.dlong.networkdebugassistant.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlong.dialog.*
import com.dlong.networkdebugassistant.R
import com.dlong.networkdebugassistant.adapter.FolderAdapter
import com.dlong.networkdebugassistant.bean.FolderInfo
import com.dlong.networkdebugassistant.databinding.ActivitySelectFolderBinding
import com.dlong.networkdebugassistant.utils.DateUtils
import java.io.File
import java.util.*


class SelectFolderActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectFolderBinding
    private lateinit var folderAdapter: FolderAdapter

    private lateinit var baseDirectory: String

    companion object{
        private const val P_READ_EXTERNAL_STORAGE = 1
        private const val P_WRITE_EXTERNAL_STORAGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_folder)
        baseDirectory = applicationContext.externalCacheDir?.path?: applicationContext.filesDir.path

        // 获取传递过来的路径
        binding.curPath = intent.getStringExtra("path")?: baseDirectory

        // 设置返回按钮
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // 初始化列表
        binding.rcv.layoutManager = LinearLayoutManager(this)
        folderAdapter = FolderAdapter(mHandler, mutableListOf())
        binding.rcv.adapter = folderAdapter

        // 检查读取存储权限
        if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            updateArray()
        } else {
            reqPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), P_READ_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            P_READ_EXTERNAL_STORAGE -> {
                if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    updateArray()
                }
            }
        }
    }

    override fun callBack(msg: Message) {
        super.callBack(msg)
        when(msg.what) {
            FolderAdapter.FOLDER_SELECT -> {
                val info = msg.obj as FolderInfo
                binding.curPath = info.path
                updateArray()
            }
            FolderAdapter.FOLDER_EDIT -> {
                val info = msg.obj as FolderInfo
                var tips = resources.getString(R.string.folder_edit_tips)
                tips = tips.replace("**", info.name)
                ButtonDialog(this).setTittle(resources.getString(R.string.prompt))
                    .setMsg(tips)
                    .addAction(resources.getString(R.string.folder_edit_name), ButtonStyle.THEME) {
                        onClick { dialog, _ ->
                            // 重命名
                            renameFolder(info)
                            dialog.dismiss()
                        }
                    }
                    .addAction(resources.getString(R.string.delete), ButtonStyle.ERROR) {
                        onClick { dialog, _ ->
                            // 删除
                            deleteFolder(info)
                            dialog.dismiss()
                        }
                    }
                    .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
                    .create().show()
            }
        }
    }

    fun lastPage(view: View) {
        // 返回上一页，如果到了尽头提示用户
        val file = File(binding.curPath?: baseDirectory)
        val parentPath = file.parent
        if (parentPath == null) {
            showSnackBar(binding.root, "已经是根目录了")
        } else {
            binding.curPath = parentPath
            updateArray()
        }
    }

    fun sure(view: View) {
        val intent = Intent()
        intent.putExtra("path", binding.curPath?: baseDirectory)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun newFolder(view: View) {
        // 检查写入权限
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            reqPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), P_WRITE_EXTERNAL_STORAGE)
            return
        }
        val tag = "name"
        EditDialog(this).setTittle(resources.getString(R.string.prompt))
            .setMsg(resources.getString(R.string.folder_create_new_folder))
            .addEdit(tag, "", resources.getString(R.string.please_input_new_folder_name))
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME) {
                onClick { dialog, _ ->
                    val value = dialog.getInputText(tag)
                    when {
                        value.isEmpty() -> {
                            dialog.setError(tag, resources.getString(R.string.input_new_folder_name_can_not_empty))
                        }
                        value.length > 50 -> {
                            dialog.setError(tag, resources.getString(R.string.input_new_folder_name_can_not_over_range))
                        }
                        else -> {
                            // 创建文件夹
                            val newPath = "${binding.curPath?: baseDirectory}/$value"
                            val file = File(newPath)
                            if (!file.exists()) {
                                file.mkdirs()
                            }
                            // 更新文件夹列表
                            updateArray()
                            dialog.dismiss()
                        }
                    }
                }
            }
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    /**
     * 重命名文件夹
     */
    private fun renameFolder(folder: FolderInfo) {
        // 检查写入权限
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            reqPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), P_WRITE_EXTERNAL_STORAGE)
            return
        }
        val tag = "name"
        var tips = resources.getString(R.string.folder_edit_new_folder_name)
        tips = tips.replace("**", folder.name)
        EditDialog(this).setTittle(resources.getString(R.string.prompt))
            .setMsg(tips)
            .addEdit(tag, folder.name, resources.getString(R.string.please_input_new_folder_name))
            .addAction(resources.getString(R.string.sure), ButtonStyle.THEME) {
                onClick { dialog, _ ->
                    val value = dialog.getInputText(tag)
                    when {
                        value.isEmpty() -> {
                            dialog.setError(tag, resources.getString(R.string.input_new_folder_name_can_not_empty))
                        }
                        value.length > 50 -> {
                            dialog.setError(tag, resources.getString(R.string.input_new_folder_name_can_not_over_range))
                        }
                        else -> {
                            // 重命名文件夹
                            val oldFile = File(folder.path)
                            val parentPath = folder.path.substring(0, folder.path.lastIndexOf("/"))
                            val newPath = "$parentPath/$value"
                            val newFile = File(newPath)
                            oldFile.renameTo(newFile)
                            // 更新文件夹列表
                            updateArray()
                            dialog.dismiss()
                        }
                    }
                }
            }
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    /**
     * 删除文件夹
     */
    private fun deleteFolder(folder: FolderInfo) {
        var tips = resources.getString(R.string.folder_delete_sure)
        tips = tips.replace("**", folder.name)
        ButtonDialog(this).setTittle(resources.getString(R.string.prompt))
            .setMsg(tips)
            .addAction(resources.getString(R.string.delete), ButtonStyle.ERROR) {
                onClick { dialog, _ ->
                    // 删除
                    val file = File(folder.path)
                    file.delete()
                    // 更新文件夹列表
                    updateArray()
                    dialog.dismiss()
                }
            }
            .addAction(resources.getString(R.string.cancel), ButtonStyle.NORMAL, null)
            .create().show()
    }

    /**
     * 更新文件夹列表
     */
    private fun updateArray() {
        folderAdapter.update(getFolderArray(binding.curPath?: baseDirectory))
    }

    /**
     * 根据路径获取全部文件夹
     */
    private fun getFolderArray(path: String): MutableList<FolderInfo> {
        val files = File(path).listFiles()?: emptyArray()
        val fList = mutableListOf<FolderInfo>()
        val fileList = files.asList()
        // 文件排序--按照名称排序
        Collections.sort(fileList, object : Comparator<File>{
            override fun compare(p0: File?, p1: File?): Int {
                if (p0 == null) return -1
                if (p1 == null) return 1
                return p0.name.compareTo(p1.name)
            }
        })
        for (file in fileList.iterator()) {
            if (file == null) continue
            if (file.isDirectory && !file.isHidden) {
                val info = FolderInfo()
                info.iconRes = R.mipmap.icon_folder
                info.name = file.name
                info.path = file.path
                info.sonNum = getSonFolderNumber(file.path)
                info.lastEditTime = DateUtils.getDateStr(file.lastModified(), "yyyy-MM-dd hh:mm:ss")
                fList.add(info)
            }
        }
        return fList
    }

    /**
     * 获取文件夹的子文件夹数量
     */
    private fun getSonFolderNumber(path: String) : Long {
        var number = 0L
        val files = File(path).listFiles()?: emptyArray()
        for (file in files.iterator()) {
            if (file == null) continue
            if (file.isDirectory && !file.isHidden) {
                number ++
            }
        }
        return number
    }
}
