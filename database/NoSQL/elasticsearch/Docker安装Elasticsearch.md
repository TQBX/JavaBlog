本文参考：macrozheng，[Elasticsearch快速入门，掌握这些刚刚好！](https://mp.weixin.qq.com/s/cohWZy_eUOUqbmUxhXzzNA)

下载镜像；

```shell
docker pull elasticsearch:6.4.0
```

修改虚拟机内存区域大小，否则会因为过小而无法启动。

```shell
sysctl -w vm.max_map_count=262144
```

使用docker命令启动；

```shell
docker run -p 9200:9200 -p 9300:9300 --name elasticsearch \
-e "discovery.type=single-node" \
-e "cluster.name=elasticsearch" \
-v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
-d elasticsearch:6.4.0
```

此时其实docker容器并没有启动成功，使用docker logs命令查看错误日志；

```shell
docker logs -f elasticsearch
```

报错如下：

```java
Caused by: java.nio.file.AccessDeniedException: /usr/share/elasticsearch/data/nodes
	at sun.nio.fs.UnixException.translateToIOException(UnixException.java:90) ~[?:?]
	at sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:111) ~[?:?]
	at sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:116) ~[?:?]
	at sun.nio.fs.UnixFileSystemProvider.createDirectory(UnixFileSystemProvider.java:385) ~[?:?]

```

可以知道是权限的问题，参考网上大多数博客，发现应该是挂载的宿主机权限不够，此时修改权限：

```shell
chmod 777 /mydata/elasticsearch/data/
chmod 777 /mydata/elasticsearch/plugins/
```

其实我修改权限之后还是没有启动成功，依旧报错，几次尝试之后，选择了重启，结果再试一次，成功启动。