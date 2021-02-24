# NetworkDebugAssistant

网络调试助手[![](https://jitpack.io/v/D10NGYANG/NetworkDebugAssistant.svg)](https://jitpack.io/#D10NGYANG/NetworkDebugAssistant)

博客链接：[Android TCP/UDP开源库NetworkDebugAssistant使用教程](https://blog.csdn.net/sinat_38184748/article/details/107616704)

**从0.1.4版本开始支持Lambda语法**

# 前言

之前开发的通讯工具，已经上架酷安和Google play store，链接在下方：

[酷安 - Network Debug Assistant](https://www.coolapk.com/apk/257820)

[Google play store - Network Debug Assistant](https://play.google.com/store/apps/details?id=com.dlong.networkdebugassistant)



![在这里插入图片描述](https://img-blog.csdnimg.cn/20200727172751943.png)

包括以下功能：

1. udp广播；
2. udp组播；
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
   // 必须
   implementation 'com.github.D10NGYANG:NetworkDebugAssistant:0.1.6'
   
   // 如果需要用到PING接口需要引入以下库
   // Lifecycle components
   implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
   // Coroutines
   api "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
   api "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2"
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
thread.start()
// 发送数据
thread.send("192.168.1.123", 12345, data)
```

lambda：

```kotlin
val thread = UdpBroadThread(this, 12345) {
    onThreadAcceptSocket {
        // 不需要
    }
    onThreadConnectFailed {
        // 打开端口失败
    }
    onThreadConnected {
        // 打开端口成功
    }
    onThreadDisconnect {
        // 关闭UDP
    }
    onThreadError { ipAddress, error ->
        // 发生错误
    }
    onThreadReceive { ipAddress, port, time, data ->
        // 接受到信息，ipAddress消息来源地址，port消息来源端口，time消息到达时间，data消息内容
    }
}
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
thread.start()
// 发送数据
thread.send("192.168.11.255", "12345", data)
```

lambda：

```kotlin
val thread = UdpMultiThread(this, "192.168.11.255", 12345) {
    onThreadAcceptSocket {
        // 不需要
    }
    onThreadConnectFailed {
        // 打开端口失败
    }
    onThreadConnected {
        // 打开端口成功
    }
    onThreadDisconnect {
        // 关闭UDP
    }
    onThreadError { ipAddress, error ->
        // 发生错误
    }
    onThreadReceive { ipAddress, port, time, data ->
        // 接受到信息，ipAddress消息来源地址，port消息来源端口，time消息到达时间，data消息内容
    }
}
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
thread.start()
// 发送数据
thread.send(data)
```

lambda：

```kotlin
val thread = TcpClientThread("192.168.1.123", 12345) {
    onThreadAcceptSocket {
        // 不需要
    }
    onThreadConnectFailed {
        // 连接服务器失败
    }
    onThreadConnected {
        // 连接服务器成功
    }
    onThreadDisconnect {
        // 服务器连接断开
    }
    onThreadError { ipAddress, error ->
        // 发生错误
    }
    onThreadReceive { ipAddress, port, time, data ->
        // 接受到信息，ipAddress消息来源地址，port消息来源端口，time消息到达时间，data消息内容
    }
}
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
thread.start()
// 发送数据给特定客户端
thread.send("192.168.11.255", "12345", data)
// 发送数据给全部已连接的客户端
thread.send(data)
// 断开客户端
thread.disconnectSocket(socket)
```

lambda：

```kotlin
val thread = TcpServerThread(12345) {
    onThreadAcceptSocket {
        // 有客户端连接上来了，ipAddress客户端IP地址
    }
    onThreadConnectFailed {
        // 启动服务器失败
    }
    onThreadConnected {
        // 启动服务器成功
    }
    onThreadDisconnect {
        // 关闭服务器
    }
    onThreadError { ipAddress, error ->
        // 发生错误
    }
    onThreadReceive { ipAddress, port, time, data ->
        // 接受到信息，ipAddress消息来源地址，port消息来源端口，time消息到达时间，data消息内容
    }
}
```


## PING
PING并且查看详细结果
```kotlin
// 连续PING几次，监听返回内容，进行打印显示
ping(address, 6).observe(this, {
   // 显示PING数据
   binding.txtReceive.append(it)
})
```
PING一次得到是否通过的结果
```kotlin
GlobalScope.launch {
   // 这里可能耗费挺长时间，所以在子线程工作
   val isSuccess = pingOnce(address)
   withContext(Dispatchers.Main) {
       showToast("PING测试结果=$isSuccess")
   }
}
```

# 混淆规则

```kotlin
# 保留UDP/TCP库
-keep class com.dlong.dl10netassistant.** {*;}
-dontwarn com.dlong.dl10netassistant.**
```
