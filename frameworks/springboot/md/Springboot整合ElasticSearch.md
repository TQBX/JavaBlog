docker安装elasticsearch

```bash
docker pull elasticsearch
docker images
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300 --name es01 5acf0e8da90b

```

云服务可能需要手动配置9200和9300安全组。

