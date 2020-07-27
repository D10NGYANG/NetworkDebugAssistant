# NetworkDebugAssistant
网络调试助手

博客链接：[Android TCP/UDP开源库NetworkDebugAssistant使用教程](https://blog.csdn.net/sinat_38184748/article/details/107616704)

# 前言
之前开发的通讯工具，已经上架酷安和Google play store，链接在下方：

[酷安 - Network Debug Assistant](https://www.coolapk.com/apk/257820)

[Google play store - Network Debug Assistant](https://play.google.com/store/apps/details?id=com.dlong.networkdebugassistant)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200727172751943.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3NpbmF0XzM4MTg0NzQ4,size_16,color_FFFFFF,t_70)
包括以下功能：
1. udp广播；
2. udp组播测试；
3. Tcp客户端；
4. Tcp服务器；

现在，终于有时间将它整理成一个单独的库，能直接在其他项目中快捷接入使用。
# 使用方法
## 添加依赖
1. 将JitPack存储库添加到您的构建文件中
将其添加到存储库末尾的root build.gradle中：
```kotlin
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
        
    }
}
```
2. 添加依赖项

```kotlin
dependencies {
	implementation 'com.github.D10NGYANG:NetworkDebugAssistant:0.1.3'
}
```
## UDP广播

```kotlin
        val thread = UdpBroadThread(this, 12345, object : OnNetThreadListener{
            override fun onAcceptSocket(ipAddress: String) {
                // 不需要
            }

            override fun onConnectFailed(ipAddress: String) {
                // 打开端口失败
            }

            override fun onConnected(ipAddress: String) {
                // 打开端口成功
            }

            override fun onDisconnect(ipAddress: String) {
                // 关闭UDP
            }

            override fun onError(ipAddress: String, error: String) {
                // 发生错误
            }

            override fun onReceive(ipAddress: String, port: Int, time: Long, data: ByteArray) {
                // 接受到信息，ipAddress消息来源地址，port消息来源端口，time消息到达时间，data消息内容
            }
        })
        // 发送数据
        thread.send("192.168.1.123", 12345, data)
```
## UDP组播

```kotlin
        val thread = UdpMultiThread(this, "192.168.11.255", 12345, object : OnNetThreadListener{
            override fun onAcceptSocket(ipAddress: String) {
                // 不需要
            }

            override fun onConnectFailed(ipAddress: String) {
                // 打开端口失败
            }

            override fun onConnected(ipAddress: String) {
                // 打开端口成功
            }

            override fun onDisconnect(ipAddress: String) {
                // 关闭UDP
            }

            override fun onError(ipAddress: String, error: String) {
                // 发生错误
            }

            override fun onReceive(ipAddress: String, port: Int, time: Long, data: ByteArray) {
                // 接受到信息，ipAddress消息来源地址，port消息来源端口，time消息到达时间，data消息内容
            }
        })
        // 发送数据
        thread.send("192.168.11.255", "12345", data)
```
## TCP客户端
```kotlin
        val thread = TcpClientThread("192.168.1.123", 12345, object : OnNetThreadListener{
            override fun onAcceptSocket(ipAddress: String) {
                // 不需要
            }

            override fun onConnectFailed(ipAddress: String) {
                // 连接服务器失败
            }

            override fun onConnected(ipAddress: String) {
                // 连接服务器成功
            }

            override fun onDisconnect(ipAddress: String) {
                // 服务器连接断开
            }

            override fun onError(ipAddress: String, error: String) {
                // 发生错误
            }

            override fun onReceive(ipAddress: String, port: Int, time: Long, data: ByteArray) {
                // 接受到信息，ipAddress消息来源地址，port消息来源端口，time消息到达时间，data消息内容
            }
        })
        // 发送数据
        thread.send(data)
```
## TCP服务器
```kotlin
        val thread = TcpServerThread(12345, object : OnNetThreadListener{
            override fun onAcceptSocket(ipAddress: String) {
                // 有客户端连接上来了，ipAddress客户端IP地址
            }

            override fun onConnectFailed(ipAddress: String) {
                // 启动服务器失败
            }

            override fun onConnected(ipAddress: String) {
                // 启动服务器成功
            }

            override fun onDisconnect(ipAddress: String) {
                // 关闭服务器
            }

            override fun onError(ipAddress: String, error: String) {
                // 发生错误
            }

            override fun onReceive(ipAddress: String, port: Int, time: Long, data: ByteArray) {
                // 接受到信息，ipAddress消息来源地址，port消息来源端口，time消息到达时间，data消息内容
            }
        })
        // 发送数据给特定客户端
        thread.send("192.168.11.255", "12345", data)
        // 发送数据给全部已连接的客户端
        thread.send(data)
```
# 混淆规则

```kotlin
# 保留UDP/TCP库
-keep class com.dlong.dl10netassistant.** {*;}
-dontwarn com.dlong.dl10netassistant.**
```
