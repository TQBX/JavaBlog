# ELK

是Elastic公司拥有的三个开源项目：ElasticSearch、Logstash、Kibana，三者形成了ELK软件栈。他们各自的基本职能：

- Elasticsearch是核心，可以对数据进行快速搜索及分析。
- Logstash负责数据的采集，处理。
- Kibana负责数据展示，分析及管理。

# 中文分词插件下载

下载地址：[https://github.com/medcl/elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik)

![Elastic产品生态](https://img-blog.csdnimg.cn/20190919085503769.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1VidW50dVRvdWNo,size_16,color_FFFFFF,t_70)



# 简单介绍

Elaaticsearch是一个分布式的使用REST接口的搜索引擎。

Elasticsearch是一个基于Apache Lucene(TM)的开源搜索引擎，无论在开源还是专有领域，Lucene可以被认为是迄今为止最先进、性能最好的、功能最全的搜索引擎库。但是，Lucene只是一个库。Lucene本身并不提供高可用性及分布式部署。想要发挥其强大的作用，你需使用Java并要将其集成到你的应用中。Elasticsearch也是使用Java编写并使用Lucene来建立索引并实现搜索功能，但是它的目的是通过简单连贯的RESTful API让全文搜索变得简单并隐藏Lucene的复杂性。



## Elasticsearch的特点

![Elasticsearch的特点](https://img-blog.csdnimg.cn/20190909144042553.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1VidW50dVRvdWNo,size_16,color_FFFFFF,t_70)

- **Speed**：极速的搜索体验，相对于其它大数据引擎，Es可以实现秒级搜索。
- **Scale**：Es的cluster是一种极易扩展的分布式部署，很容易处理petabytes的数据库容量。
- **Relevance**：Es搜索的结果可以按照分数进行排序，提供我们最相关的结果。

# 相关概念





# 安装过程中的报错



# 参考连接

文中部分图片来自于：[Elastic 中国社区官方文档](https://blog.csdn.net/UbuntuTouch/article/details/98871531)

