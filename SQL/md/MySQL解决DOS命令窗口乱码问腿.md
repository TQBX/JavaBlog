乱码问题如下：

![3LyC6J.png](https://s2.ax1x.com/2020/03/06/3LyC6J.png)

导致问题产生的原因很简单，无非是命令行的编码和MySQL内部设置的编码出现了偏差。我们右键属性查看以下命令行的编码方式是GBK。

解决：

```sql
mysql> show variables like 'character%'; -- 模糊查询character开头的全局变量
```

![3Ly9l4.png](https://s2.ax1x.com/2020/03/06/3Ly9l4.png)

character_set_client：客户端的字符集

character_set_connection：连接的字符集

character_set_results：查询结果的字符集



可以将上面三者分别设置为GBK：`set xxx = gbk;`

可以简便地：`set names gbk;`

当然，上面两步操作都是暂时性的，下次开启还是会这样，所以图形化界面真香。

至此，就已经结束：

![3LyPX9.png](https://s2.ax1x.com/2020/03/06/3LyPX9.png)