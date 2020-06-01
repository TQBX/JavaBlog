可以配置使用阿里云镜像，提升拉取速度：[https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors](https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors)

```bash
vim /etc/docker/daemon.json

{
  "registry-mirrors": ["https://xxx.mirror.aliyuncs.com"]
}

systemctl daemon-reload
systemctl restart docker
```

