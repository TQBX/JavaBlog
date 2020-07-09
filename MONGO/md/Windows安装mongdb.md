一、MongoDB安装包地址：[https://fastdl.mongodb.org/win32/mongodb-win32-x86_64-2008plus-ssl-3.2.21-signed.msi](https://fastdl.mongodb.org/win32/mongodb-win32-x86_64-2008plus-ssl-3.2.21-signed.msi)

二、自定义安装路径：我是存放在`D:\Program Files\MongoDB`目录下。

可以发现`D:\Program Files\MongoDB\bin`目录下有以下两个运行程序：mongo.exe是客户端运行程序，mongod.exe是服务断运行程序。

三、在安装路径下，创建两个文件夹：db和log。

四、将`D:\Program Files\MongoDB\bin`加入到path环境变量中。

五、以管理员模式打开cmd，执行以下命令：

```shell
mongod --dbpath "D:\Program Files\MongoDB\data\db" --logpath "D:\Program Files\MongoDB\data\log\mongo.log" --install --serviceName "MongoDB"
```

- `--dbpath`：开始新建的db目录。
- `--logpath`：指定日志目录，mongo.log为日志文件。
- `--serviceName`：服务名。

> 需要注意的是：路径名需要是绝对路径。

六、服务相关命令

```shell
启动服务：net start MongoDB
关闭服务：net stop MongoDB
移除服务：D:\Program Files\MongoDB\bin\mongod.exe --remove
```

路径有空格可能会存在错误，可以选择cd进入该目录。

