# 一、数据库连接池

> 小白喊话：本文应该有很多不严谨的地方，仅供参考，日后学成归来再做完善。

部分参考自：

[https://blog.csdn.net/crankz/article/details/82874158](https://blog.csdn.net/crankz/article/details/82874158)

[https://blog.csdn.net/dzy21/article/details/51952138](https://blog.csdn.net/dzy21/article/details/51952138)

##  1、数据库连接池是啥

我们之前通过`DriverManager.getConnection()`方法获得的Connection数据库连接对象与数据库进行沟通。而每次获取一次连接对象，就会开启一次物理连接，用完关闭连接，连接次数一多，就会造成系统性能低下。

而数据连接池的设计一定程度上解决了一部分问题：数据库连接池在应用程序启动时建立足够的数据库连接，并将这些连接组成一个连接池，由应用程序动态地对池中的连接进行申请、使用和释放。 通过共享连接，减少开关连接的次数，提高程序的效率。

>  请求连接的时候取出连接，不需要的时候，conn.close()归还连接（注意是归还！！！）。如果请求连接数>连接池中连接的数量，那么后面来的需要在请求队列中等待。

## 2、数据库连接池的优点

使用数据库连接池之后，只有第一次访问的时候，需要建立连接。之后的访问，将会复用之前创建的连接，直接执行SQL语句，减少了网络开销，提升了系统的性能。

# 二、druid简单bb

连接池技术有许多实现，像经典的C3P0，DBCP啥的，就暂且放一放了。今天主要学习阿里巴巴开发的Druid，在网上查阅了很多资料，据说是在功能、性能方面、扩展性方面都比较优秀，而且大都结合Spring之类应用，瑟瑟发抖，目前还没学到，以后学到再做补充。

druid核心实现类就是DruidDataSource，间接实现了DataSource接口。

可以通过一个工厂类`DruidDataSourceFactory`的`public static DataSource createDataSource(Properties properties)`方法，创建DataSource对象。

通过传入Properties配置文件对象配置数据库连接池，返回一个DataSource对象，而这个对象便是数据库连接池的核心，官方文档是这么说的：

> A factory for connections to the physical data source that this  `DataSource` object represents.
>
> The `DataSource` interface is implemented by a driver vendor
>
> There are three types of implementations: Basic implementation,Connection pooling implementation,Distributed transaction implementation.

大概意思就是，DataSource接口主要有三种实现方式：基本的实现、连接池实现和分布式事务的实现，也就是说：定义的连接池实现类得遵循这这接口定义的一些规则，**实现类往往由驱动程序供应商提供**，而且实现该接口的对象往往会依据Java Naming and Directory（JNDI）命名目录服务器注册，初看不太明白，结合一下百度百科：

> 包含了大量的命名和目录服务，使用通用接口来访问不同种类的服务，JNDI架构提供了一组标准于命名系统地API。这些API构建在与命名系统有关的驱动上，有助于企业级应用与实际数据源分离。（ps：先摘抄这，以后会明白的）

DataSource接口中存在与数据库连接的方法getConnection()，也就是说，之后就可以利用这个DataSource**对象获取与数据库的连接**。

之后的操作，就如往常一样。

# 三、简单使用

改良一下封装类JDBCUtils。

```java
package com.my.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @auther Summerday
 * DruidDataSource连接池引入的JDBC工具类
 */
public class JDBCUtils {
    //定义连接池对象
    private static DataSource ds;
    static{
        try {
            Properties prop = new Properties();
            //利用配置文件加载prop对象
            prop.load(JDBCUtils.class.getClassLoader().getResourceAsStream("druid.properties"));
            //通过prop创建连接池对象
            ds = DruidDataSourceFactory.createDataSource(prop);
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    /**
     * 释放资源
     */
    public static void close(Statement statement,Connection connection){
        close(null,statement,connection);
    }
    /**
     * 释放资源
     */
    public static void close(ResultSet resultSet,Statement statement, Connection connection){
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 获取连接池对象
     */
    public static DataSource getDataSource(){
        return ds;
    }

}

```

# 参数配置

连接池的参数配置，暂时学习几个简单一些的，定义在`".properties"`文件中，到时候读取配置文件，配置连接池属性。

可以参考：[https://www.cnblogs.com/kaleidoscope/p/9669753.html](https://www.cnblogs.com/kaleidoscope/p/9669753.html)

```pro
driverClassName=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/mydb2
username=root
password=123456
#初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
initialSize=5
#定义最大连接池数量
maxActive=10
# 超时等待事件（毫秒）
maxWait=3000
```





