Elasticsearch是一个分布式搜索服务，提供Restful API，底层基于Lucene，采用多shard（分片）的方式保证数据安全，并且提供自动resharding的功能，github等大型的站点也是采用了ElasticSearch作为其搜索服务

docker安装elasticsearch

```bash
docker pull elasticsearch
docker images
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300 --name es01 5acf0e8da90b

```

云服务可能需要手动配置9200和9300安全组。

在配置`elasticsearch.yml`时，遇到了一个坑，我按着顺序记录以下：

```bash
docker exec -it {containerid} /bin/bash #(退出的时候 exit)
cd config
vim elasticsearch.yml
```

结果？？vim指令无法使用，这时我就想到用yum安装一下，结果yum指令也无法使用，后来参考博客：[https://blog.csdn.net/qq_32101993/article/details/100021002](https://blog.csdn.net/qq_32101993/article/details/100021002)，使用apt-get解决。

```bash
apt-get update
apt-get install vim
```

接下来就可以正常操作了，参考方志朋老师的博客：[https://www.fangzhipeng.com/springboot/2020/06/01/sb-es.html](https://www.fangzhipeng.com/springboot/2020/06/01/sb-es.html)，进行配置。

```yml
cluster.name: "docker-cluster"
network.host: 0.0.0.0

# custom config
node.name: "node-1"
discovery.seed_hosts: ["127.0.0.1", "[::1]"]
cluster.initial_master_nodes: ["node-1"]
# 开启跨域访问支持，默认为false
http.cors.enabled: true
# 跨域访问允许的域名地址，(允许所有域名)以上使用正则
http.cors.allow-origin: /.*/ 
```

接着退出交互模式

```bahs
exit
```

