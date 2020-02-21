# Windows：解决MySQL登录ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using passwor=YES)问题

- 找到安装目录下的`my.ini`配置文件，找到`[mysqld]`，在这行下面加入如下语句：

```
skip-grant-tables
```

- 需要特别注意，如果安装目录在c系统盘下，修改是需要权限的，可以先将文件剪切到其他盘，然后修改，接着剪切回来。
- 以上的步骤网上有许多，我修改之后，兴奋地又去尝试登录来着，还是不行，后来发现修改完配置文件之后，需要重新启动一下MySQL。
- 可以打开服务（win+R），输入`services.msc`，进去之后找到MySQL服务，重启动。（ps:启动关闭mysql的方法还有许多，比如以管理员身份打开cmd，输入以下命令：

```
net stop mysql  # 关闭服务
net start mysql  # 开启服务
```

- 接着就能够免密登录了，接下来修改密码的方法有很多，我随便找了一种试了下：

```
mysql> update mysql.user set password=PASSWORD('123456') where User='root';# 修改密码
mysql> flush privileges;#刷新权限
mysql> quit
```

- 完事之后，依旧重启MySQL。
- 在cmd黑窗口输入以下语句：

```
mysql -u root -p  # 访问本地MySQL
```

- 最后输入修改之后的密码，就能看到welcom啥啥啥的了。

