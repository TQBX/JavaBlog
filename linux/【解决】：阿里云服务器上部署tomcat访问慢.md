转载大佬的博客：[https://blog.csdn.net/qq_40386113/article/details/84837881](https://blog.csdn.net/qq_40386113/article/details/84837881)，虽然不知道每一步都是干啥的，但是效果杠杠的。

【查看服务器熵池】：我的一开始只有56，访问速度慢，该值也会很小。

```bash
cat /proc/sys/kernel/random/entropy_avail
```

【安装rngd】服务

```bash
yum install rng-tools
```

【启动服务】

```bash
systemctl start rngd
```

【拷贝】

```bash]
cp /usr/lib/systemd/system/rngd.service /etc/systemd/system
```

【修改rngd.service的内容】

```bash
/etc/systemd/system/rngd.service
```

将`ExecStart=/sbin/rngd -f`改为`ExecStart=/sbin/rngd -f -r /dev/urandom`。

【重新载入服务】

```bash
systemctl daemon-reload
```

【再次查看】：此时已经有3000左右，访问速度飞快。

```bash
cat /proc/sys/kernel/random/entropy_avail
```

