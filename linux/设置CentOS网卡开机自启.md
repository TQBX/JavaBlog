# Linux网络环境配置

【启动网卡】

```bash
ifup eth0 #表示启动CentOS系统中名为eth0的网卡。
```

【设置CentOS的网卡开机自启】

```bash
vim /etc/sysconfig/network-scripts/ifcfg-eth0
```

vim：linux系统内核自带的文本编辑器

etc ：Linux系统中所有的配置文件存放目录

sysconfig ：系统配置文件的存放目录

network-scripts ：网络配置文件的存放目录

ifcfg-eth0 ：具体的网卡配置文件

ifconfig:：用来查看当前系统的网络连接，类似于Windows的ipconfig

【修改文件内容】

设置网卡开机自启：`ONBOOT=no` ->  `ONBOOT=yes`

设置静态ip：

- `BOOTPROTO=dhcp`  -> `BOOTPROTO=static`

- 指定ip，网关，和DNS。

  ```bash
  IPADDR=192.168.213.128
  GATEWAY=192.168.213.2
  DNS1=192.168.213.2
  ```

>  dhcp和static的区别：
>
> - 选择DHCP，Linux启动后会自动获取IP，每次自动获取的IP地址可能会不一样，对于服务器而言，IP往往需要固定，也就是选用static，固定静态IP地址。

【重启服务并重启系统】

```bash
service network restart
reboot
```



