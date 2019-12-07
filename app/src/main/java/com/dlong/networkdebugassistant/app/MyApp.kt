package com.dlong.networkdebugassistant.app

import android.app.Application
import com.dlong.networkdebugassistant.R
import com.simple.spiderman.SpiderMan

/**
 * @author D10NG
 * @date on 2019-12-05 10:17
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        //调试工具初始化
        SpiderMan.init(this) //设置主题样式，内置了两种主题样式light和dark
            .setTheme(R.style.SpiderManTheme_Dark)
    }
}