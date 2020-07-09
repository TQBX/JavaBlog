基于Lucence的搜索服务器，提供了一个分布式多用户能力的全文搜索引擎，基于RestFul web接口。

Java语言开发，并作为Apache许可条款的开放源码发布，是一种流行的企业级搜索引擎。





| 原先                                | 使用Rest风格完成商品操作              |
| ----------------------------------- | ------------------------------------- |
| /product/manage/save                | /product/{prodId}  -X PUT             |
| /product/manage/update              | /product/manage/{prodId} -X POST      |
| /product/manage/itemquery/{prodId}  | /product/manage/{prodId}    -X GET    |
| /product/manage/itemdelete/{prodId} | /product/manage/{prodId}    -X DELETE |



# es中的DSL操作

## 索引操作

**新建索引**：PUT  `http://ip:9200/index01`

在es接收到DSL创建索引的指令的请求时，底层就会创建一个索引名称，并将索引切分为5片，并计算5个分片，每一个分片存储两份。

```json
{
  "acknowledged": true,
  "shards_acknowledged": true,
  "index": "index01"
}
```

**索引打开或关闭**

POST `http://ip:9200/index01/_close`        POST `http://ip:9200/index01/_open`

在es中每次启动时都会加载index，针对每个索引都启动线程来监听状态。对应es中数据使用时间较长且数据不常用的索引，可以进行关闭，需要时打开，仍可以正常使用。

**索引控制读写权限**  （虽然索引中没有数据，仍能够返回查询时间等信息）

`curl -XPUT -d '{xxx}' http://ip:9200/index01/`

- `blocks.read_only:true` 当前索引只读模式，不允许写和更新

- `blocks.read:true` 禁止读

- `blocks.write:true` 禁止写

**查看索引信息**：`curl -XGET http://ip:9200/index01/_settings`

- true为开启禁止读取
- false为关闭禁止读取

```json
{
    "index": {
        "settings": {
            "index": {
                "number_of_shards": "5",
                "blocks": {
                    "read": "true"
                },
                "provided_name": "index",
                "creation_date": "1594297834367",
                "number_of_replicas": "1",
                "uuid": "teisH_SySwGiGlKjc-CeIQ",
                "version": {
                    "created": "6020299"
                }
            }
        }
    }
}
```

## 文档操作

## 文档新增

es支持客户端传递数据,以文档结构存储在索引文件中.

`curl -XPUT -d {xxx} http://ip:9200/book/article/01`

book为index，article为type，01为文档id。

文档新增时，

```json
{
  "_index": "book",
  "_type": "article",
  "_id": "01",
  "_version": 1,
  "result": "created",
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 0,
  "_primary_term": 1
}
```

**es如何保证高可用分布式的数据一致性？**

对文档对象的任何操作，哪怕是删除_version的值都会在es中保存自增1，每次获取都选取较高版本操作，维护整体数据的乐观锁一致性；

在分布式高可用集群中，所有document数据不会因为同步的失败导致最终使用数据(高可用替换，读取等)的错误出现;

## 文档查询

`curl -XGET http://ip:9200/book/article/01`

```json
{
  "_index": "book",
  "_type": "article",
  "_id": "01",
  "_version": 3,
  "found": true,
  "_source": {
    "id": 1,
    "title": "fireworks interface doc",
    "content": "今天天气不错"
  }
}
```

## 查询多个文档

`curl -XGET -d {xxx} http://ip:9200/_mget`

```json
{
  "docs": [
    {
      "_index": "book",
      "_type": "article",
      "_id": "01"
    },
    {
      "_index": "book1",
      "_type": "article",
      "_id": "01"
    }
  ]
}
```

## 删除文档

`curl -XDELETE http://ip:9200/book/article/1`



# 搜索

es将原来复杂的lucence操作的api封装形成了接口，

## 查询所有 match all

GET `http://localhost:9200/index02/_search`

```json
{
	"query":{
		"match_all":{}
	}
}
```

```json
{
	"took": 2,
	"timed_out": false,
	"_shards": {
		"total": 5,
		"successful": 5,
		"skipped": 0,
		"failed": 0
	},
	"hits": {
		"total": 3,
		"max_score": 1.0,
		"hits": [
			{
				"_index": "index02",
				"_type": "article",
				"_id": "2",
				"_score": 1.0,
				"_source": {
					"id": 2,
					"name": "大数据"
				}
			},
			{
				"_index": "index02",
				"_type": "article",
				"_id": "1",
				"_score": 1.0,
				"_source": {
					"id": 1,
					"name": "java编程"
				}
			},
			{
				"_index": "index02",
				"_type": "article",
				"_id": "3",
				"_score": 1.0,
				"_source": {
					"id": 3,
					"name": "python"
				}
			}
		]
	}
}
```

## term query 词项查询

`GET http://localhost:9200/index02/_search`

```json
{
	"query":{
		"term":{
			"name":"java" //找到name为中有java的项
		}
	}
}
```

```json
{
	"took": 15,
	"timed_out": false,
	"_shards": {
		"total": 5,
		"successful": 5,
		"skipped": 0,
		"failed": 0
	},
	"hits": {
		"total": 1,
		"max_score": 0.2876821,
		"hits": [
			{
				"_index": "index02",
				"_type": "article",
				"_id": "1",
				"_score": 0.2876821,
				"_source": {
					"id": 1,
					"name": "java编程"
				}
			}
		]
	}
}
```

但是查询name为"编程"的项，将会查询不到，原因是因为没有指定分词器，es默认使用standardAnalyzer，无法识别中文词。

## match query

`GET http://localhost:9200/index02/_search`

```json
{
	"query":{
		"match":{
			"name":"编程"
		}
	}
}
```

```json
{
	"took": 19,
	"timed_out": false,
	"_shards": {
		"total": 5,
		"successful": 5,
		"skipped": 0,
		"failed": 0
	},
	"hits": {
		"total": 1,
		"max_score": 0.5753642,
		"hits": [
			{
				"_index": "index02",
				"_type": "article",
				"_id": "1",
				"_score": 0.5753642,
				"_source": {
					"id": 1,
					"name": "java编程"
				}
			}
		]
	}
}
```

# ik分词器

# 中文分词插件下载

下载地址：[https://github.com/medcl/elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik)

