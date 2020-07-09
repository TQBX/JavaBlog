[toc]

# Elasticsearch的特点

ELK是Elastic公司拥有的三个开源项目：ElasticSearch、Logstash、Kibana，三者形成了ELK软件栈。他们各自的基本职能：

- Elasticsearch是核心，可以对数据进行快速搜索及分析。
- Logstash负责数据的采集，处理。
- Kibana负责数据展示，分析及管理。

![Elastic产品生态](https://img-blog.csdnimg.cn/20190919085503769.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1VidW50dVRvdWNo,size_16,color_FFFFFF,t_70)

Elaaticsearch是一个分布式的使用REST接口的搜索引擎。

Elasticsearch是一个基于Apache Lucene(TM)的开源搜索引擎，无论在开源还是专有领域，Lucene可以被认为是迄今为止**最先进、性能最好的、功能最全的搜索引擎库**。但是，Lucene只是一个库。Lucene本身并不提供高可用性及分布式部署。想要发挥其强大的作用，你需使用Java并要将其集成到你的应用中。Elasticsearch也是使用Java编写并使用Lucene来建立索引并实现搜索功能，但是它的目的是通过简单连贯的**RESTful API**让全文搜索变得简单并隐藏Lucene的复杂性。

![Elasticsearch的特点](https://img-blog.csdnimg.cn/20190909144042553.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1VidW50dVRvdWNo,size_16,color_FFFFFF,t_70)

- **Speed**：极速的搜索体验，相对于其它大数据引擎，Es可以实现秒级搜索。
- **Scale**：Es的cluster是一种极易扩展的分布式部署，很容易处理petabytes的数据库容量。
- **Relevance**：Es搜索的结果可以按照分数进行排序，提供我们最相关的结果。

# ElasticSearch的结构

**存储层**：底层保存数据（索引），**海量索引文件的维护**，利用分布式数据切分存储，既可以存储在本地中还可以存储在分布式文件系统中（hdfs）。

**扩展层**：在Lucence原有的基础上，封装了一些扩展功能，**集群发现节点的功能**，支持各种资源管理（启动线程监听索引）。

**接口层**：给用户提供的访问结构，**基于http协议的REST风格**，用户可以使用命令或者DSL直接访问，然后对索引完成CRUD

# Es与数据库对应概念对比

|  数据库  |         ES         |
| :------: | :----------------: |
| database |    index索引库     |
| table表  |      type类型      |
|   rows   |      document      |
| columns  |      field域       |
| sql语句  | DSL（restful请求） |
|  insert  |    put请求方式     |
|  delete  |   delete请求方式   |
|  update  |    post请求方式    |
|  select  |    get请求方式     |

> `recovered [1] indices into cluster_state` 有1个索引恢复到集群中。

# ElasticSearch.yml的基本配置

```yml
# 节点所在的集群名称
cluster.name: elasticsearch
# 使用默认的名称，是随机字符串
node.name: es01
# 关闭bootstrap插件加载
bootstrap.memory_lock: false
bootstrap.system_call_filter: false
# 配置对外开启的ip访问地址,不配置只能本地访问
# 可以写主机名，但需要加“”，如“summerday”
network.host: 127.0.0.1
# Set a custom port for HTTP:
# http协议请求的访问端口(javaclient代码访问端口9300)
http.port: 9200
# 配置http插件访问es的功能开启,末尾添加
http.cors.enabled: true
http.cors.allow-origin: "*"
http.cors.allow-methods: OPTIONS, HEAD, GET, POST, PUT, DELETE
http.cors.allow-headers: "X-Requested-With, Content-Type, Content-Length, X-User"
```

# ElasticSearch索引文件的管理

Elasticsearch是分布式及高可用性的搜索引擎表现在以下三个方面：

1. 每个索引index都使用可配置数量的分片进行完全分片。默认情况下，将索引切分为5个分片。
2. 每个分片都可以由一个或多个副本。
3. 在任何副本分片上执行的读取/搜索操作。

# 附录：REST

**HTTP请求的八种方式**

options：返回服务器针对特定资源所支持的html请求方法或web服务器发送测试服务器功能。

**get**：向特定资源发出请求。（实现查询操作）

**pos**t：向指定资源提交数据进行处理请求。（实现修改操作）

**put**：向指定资源位置上上传其最新内容。（实现新增操作）

**delete**：请求服务器删除request-url所标识的资源。（实现删除操作）

head：与服务器与get请求一致的响应，响应体不会返回，获取包含在小消息头的原信息。

trace：回显服务器收到的请求，用于测试与诊断。

connect：http/1.1协议中能够将连接改为管道方式的代理服务器。

| 原先                                | 使用Rest风格完成商品操作              |
| ----------------------------------- | ------------------------------------- |
| /product/manage/save                | /product/{prodId}  -X PUT             |
| /product/manage/update              | /product/manage/{prodId} -X POST      |
| /product/manage/itemquery/{prodId}  | /product/manage/{prodId}    -X GET    |
| /product/manage/itemdelete/{prodId} | /product/manage/{prodId}    -X DELETE |

REST风格URL定义是资源，而请求方式用于决定请求资源的操作。

# 参考连接

文中部分图片来自于：[Elastic 中国社区官方文档](https://blog.csdn.net/UbuntuTouch/article/details/98871531)

