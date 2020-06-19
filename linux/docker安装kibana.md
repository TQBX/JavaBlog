拉取kibana镜像。

```bash
docker pull kibana #下载kibina镜像
docker images  #查看
```

创建kibana.yml文件，并编辑内容：

```bash
mkdir -p /usr/local/kibana/config
# 进入config目录
vim kibana.yml
```

```yml
server.host: '0.0.0.0'
elasticsearch.url: 'http://121.199.16.33:9200'
xpack:
  apm.ui.enabled: false
  graph.enabled: false
  ml.enabled: false
  monitoring.enabled: false
  reporting.enabled: false
  security.enabled: false
  grokdebugger.enabled: false
  searchprofiler.enabled: false
```

运行容器，挂载配置文件 。

```bash
docker run -d --restart=always --privileged=true --name=kibana -p 5601:5601 -v /usr/local/kibana/config/kibana.yml:/usr/share/kibana/config/kibana.yml a674d23325b0
```

开启防火墙，并使其生效：

```bash
firewall-cmd --zone=public --add-port=5601/tcp --permanent
firewall-cmd --reload
```

访问`121.199.16.33:5601`即可。

如果不行，如果无法访问，有可能是部署的云服务器需要配置安全组规则。

