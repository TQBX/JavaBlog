[toc]

# 一、Error starting userland proxy: listen tcp 0.0.0.0:3306: bind: address already in use.

查看占用3306端口号的进程。

```bash
netstat -tanlp
```

`tcp6   0   0 :::3306   :::*     LISTEN  17023/mysqld `

杀死该进程。

```bash
kill 17203
```

# 二、docker删除image

[https://www.cnblogs.com/twyth/articles/7240226.html](https://www.cnblogs.com/twyth/articles/7240226.html)