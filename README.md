# bugsystem
  之前应用是集成了腾讯Bugly，由于Bug不能及时上传，每次要等个几天才能看到系统崩溃的Bug，所以自己写了一个Bug系统。
  此系统包括3个端：Android端，PC端和服务器端。
    
## Android端
  Android捕捉到Crash，会自动收集Crash信息，启动一个PendingIntent定时启动一个Service，Service保存到文件，上传到服务器。
    
## Server端
  Server端集成了spring mvc，在初始化时启动一个ServerSocket，如果服务端发现有上传Bug信息，及时保存文件，并通知连接且有效的Socket客户端，提供客户端
下载的HttpUrl。

## PC客户端
  PC客户端是用Java语言写的。打开即连接Server端，一直read数据，等待Bug信息的到来。一旦有Bug路径（HttpUrl），及时下载文件，并打开面板，通知开发人员及时解Bug。

### 3个端的代码都有提供，大家可以根据自己的需求修改，注意修改包名！！
