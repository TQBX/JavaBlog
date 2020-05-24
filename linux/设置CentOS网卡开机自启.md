【启动网卡】

```bash
ifup eth0 #表示启动CentOS系统中名为eth0的网卡。
```

【设置CentOS的网卡开机自启】

```bash
vi /etc/sysconfig/network-scripts/ifcfg-eth0
```

vi：linux系统内核自带的文本编辑器

etc ：Linux系统中所有的配置文件存放目录

sysconfig ：系统配置文件的存放目录

network-scripts ：网络配置文件的存放目录

ifcfg-eth0 ：具体的网卡配置文件

ifconfig:：用来查看当前系统的网络连接，类似于Windows的ipconfig

【修改文件内容】

按下键盘的"i"，进入到了编辑模式，通过方向键移动到该行，将

内容修改：

ONBOOT=no     ----->   ONBOOT=yes

按键盘esc键，退出编辑模式。输入"**:wq**"，保存退出。如果不想

保存，可以输入q!，来强制退出。