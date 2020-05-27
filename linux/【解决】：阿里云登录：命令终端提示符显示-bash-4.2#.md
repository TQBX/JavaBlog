# linux 命令终端提示符显示-bash-4.2#解决方法

转载：[https://developer.aliyun.com/ask/233505?spm=a2c6h.13524658](https://developer.aliyun.com/ask/233505?spm=a2c6h.13524658)

昨天在配置linux，突然发现root登录的CRT的终端提示符显示的是`-bash-4.2#` 而不是`root@主机名 + 路径`的显示方式。搞了半天也不知道为什么出现这种情况。今天终于搞定这个问题，原因是root在/root下面的几个配置文件丢失，丢失文件如下：

1. `.bash_profile`
2. `.bashrc`

以上这些文件是每个用户都必备的文件。
使用以下命令从主默认文件重新拷贝一份配置信息到/root目录下。

```bash
cp /etc/skel/.bashrc /root/
cp /etc/skel/.bash_profile /root/
```

注销root，重新登录就可以恢复正常。

