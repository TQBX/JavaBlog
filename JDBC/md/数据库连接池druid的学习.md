# 一、数据库连接池

部分参考自：

[https://blog.csdn.net/crankz/article/details/82874158](https://blog.csdn.net/crankz/article/details/82874158)

[https://blog.csdn.net/dzy21/article/details/51952138](https://blog.csdn.net/dzy21/article/details/51952138)

##  1、数据库连接池是啥

我们之前通过`DriverManager.getConnection()`方法获得的Connection数据库连接对象与数据库进行沟通。

而每次获取一次连接对象，就会开启一次物理连接，用完关闭连接，连接次数一多，就会造成系统性能低下。

而数据连接池的设计一定程度上解决了一部分问题：数据库连接池在应用程序启动时建立足够的数据库连接，并将这些连接组成一个连接池(简单说：在一个“池”里放了好多半成品的数据库联接对象)，由应用程序动态地对池中的连接进行申请、使用和释放。

>  请求连接的时候取出连接，不需要的时候，归还连接。如果请求连接数>连接池中连接的数量，那么后面来的需要在请求队列中等待。

## 2、数据库连接池的优点

使用数据库连接池之后，只有第一次访问的时候，需要建立连接。之后的访问，将会复用之前创建的连接，直接执行SQL语句，减少了网络开销，提升了系统的性能。

# 二、具体用法

连接池技术有许多实现，像经典的C3P0，DBCP啥的，就暂且放一放了。今天主要学习阿里巴巴开发的Druid，在网上查阅了很多资料，据说是在功能、性能方面、扩展性方面都比较优秀，而且大都结合Spring之类应用，瑟瑟发抖，目前还没学到，以后学到再做补充。

想要使用druid首先需要将相应的jar包导入项目，然后，我们暂时关注com.alibaba.druid.pool.DruidDataSourceFactory这个类，见名知义，

稍微记录几个基础的用法：

> public static DataSource createDataSource(Properties properties)

通过传入Properties配置文件对象配置数据库连接池，返回一个DataSource对象，而这个对象便是数据库连接池的核心，官方文档是这么说的：

> A factory for connections to the physical data source that this  `DataSource` object represents.
>
> The `DataSource` interface is implemented by a driver vendor

大概意思就是，定义的连接池实现类得遵循这这接口定义的一些规则，实现类往往由驱动程序供应商提供，而且实现该接口的对象往往会依据Java Naming and Directory（JNDI）的命名服务注册，初看不太明白，结合一下百度百科：

> 包含了大量的命名和目录服务，使用通用接口来访问不同种类的服务，JNDI架构提供了一组标准于命名系统地API。这些API构建在与命名系统有关的驱动上，有助于企业级应用与实际数据源分离。（ps：先摘抄这，以后会明白的）

# 三、源码学习



