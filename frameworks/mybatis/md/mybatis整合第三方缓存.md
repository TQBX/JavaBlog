一、引入相关日志包依赖

```xml
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.30</version>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.6.6</version>
        </dependency>
```

二、引入ehcache依赖

```xml
        <dependency>
            <groupId>net.sf.ehcache</groupId>
            <artifactId>ehcache-core</artifactId>
            <version>2.6.11</version>
        </dependency>
```

三、引入mybatis和ehcache适配依赖

```xml
        <dependency>
            <groupId>org.mybatis.caches</groupId>
            <artifactId>mybatis-ehcache</artifactId>
            <version>1.0.3</version>
        </dependency>
```

四、在类路径下创建ehcache.xml

如果ehcache.xml文件报错可以参考：[[https://my.oschina.net/zhouchenglin/blog/1594574](https://my.oschina.net/zhouchenglin/blog/1594574)]([https://my.oschina.net/zhouchenglin/blog/1594574](https://my.oschina.net/zhouchenglin/blog/1594574))，在idea中 设置schemas and dtds，添加：http://ehcache.org/ehcache.xsd即可。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd">
 <!-- 磁盘保存路径 -->
 <diskStore path="D:\44\ehcache" />
 
 <defaultCache 
   maxElementsInMemory="10000" 
   maxElementsOnDisk="10000000"
   eternal="false" 
   overflowToDisk="true" 
   timeToIdleSeconds="120"
   timeToLiveSeconds="120" 
   diskExpiryThreadIntervalSeconds="120"
   memoryStoreEvictionPolicy="LRU">
 </defaultCache>
</ehcache>
```

【属性说明】
diskStore：指定数据在磁盘中的存储位置。

defaultCache：当借助CacheManager.add("demoCache")创建Cache时，EhCache便会采用`<defalutCache/>`指定的的管理策略。

【以下属性是必须的】
maxElementsInMemory：在内存中缓存的element的最大数目 。

maxElementsOnDisk：在磁盘上缓存的element的最大数目，若是0表示无穷大。

eternal：设定缓存的elements是否永远不过期。如果为true，则缓存的数据始终有效，如果为false那么还要根据timeToIdleSeconds，timeToLiveSeconds判断。

overflowToDisk：设定当内存缓存溢出的时候是否将过期的element缓存到磁盘上。

【以下属性是可选的】
timeToIdleSeconds：当缓存在EhCache中的数据前后两次访问的时间超过timeToIdleSeconds的属性取值时，这些数据便会删除，默认值是0,也就是可闲置时间无穷大。

timeToLiveSeconds：缓存element的有效生命期，默认是0，也就是element存活时间无穷大。

 diskSpoolBufferSizeMB 这个参数设置DiskStore(磁盘缓存)的缓存区大小.默认是30MB.每个Cache都应该有自己的一个缓冲区。

diskPersistent：在VM重启的时候是否启用磁盘保存EhCache中的数据，默认是false。

diskExpiryThreadIntervalSeconds：磁盘缓存的清理线程运行间隔，默认是120秒。每个120s，相应的线程会进行一次EhCache中数据的清理工作。

memoryStoreEvictionPolicy：当内存缓存达到最大，有新的element加入的时候， 移除缓存中element的策略。默认是LRU（最近最少使用），可选的有LFU（最不常使用）和FIFO（先进先出）。

[https://www.open-open.com/doc/6abf169c03dc4e859991479b5bd76dc8.html](https://www.open-open.com/doc/6abf169c03dc4e859991479b5bd76dc8.html)

五、主配置文件中配置

```xml
<configuration>   
		<settings>
        <!--全局开启缓存配置,默认为true-->
        <setting name="cacheEnabled" value="true"/>
    </settings>
</configuration>
```

六、映射配置文件中设置

```xml
    <!--使用ehcache缓存-->
    <cache type="org.mybatis.caches.ehcache.EhcacheCache"></cache>
    <select id="findById" resultType="user" useCache="true" >
        select * from user where id = #{id}
    </select>
```

七、默认情况下，ecache中存放的Element对象直接保存了原对象的引用，没有序列化到磁盘上，实体类不是一定要实现serializable接口，因为貌似不存在序列化的一步。

心存疑惑，遂debug查看了一波源码，发现如果配置第三方缓存库，创建的缓存是这样的：

![image-20200421100320556](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200421100320556.png)

![image-20200421100841405](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200421100841405.png)

默认情况下copyOnread和copyOnWrite都是关闭的，操作的都是原对象的引用，而不是一份拷贝。

关于copyonread和copyonwrite可以参考：[ehcache2拾遗之copyOnRead，copyOnWrite](https://www.bbsmax.com/A/ZOJPRNeydv/)

八、测试

```java
    /**
     * 测试二级缓存
     */
    @Test
    public void testFirstLevelCache2(){
        SqlSession sqlSession1 = factory.openSession();
        IUserDao userDao1 = sqlSession1.getMapper(IUserDao.class);
        User user1 = userDao1.findById(41);
        System.out.printf("==> %s\n", user1);
        sqlSession1.commit();
        //sqlSession1.close();


        SqlSession sqlSession2 = factory.openSession();
        IUserDao userDao2 = sqlSession2.getMapper(IUserDao.class);
        User user2 = userDao2.findById(41);
        System.out.printf("==> %s\n", user2);
        sqlSession2.close();
        System.out.println("user1 == user2:"+(user1 == user2));

        SqlSession sqlSession3 = factory.openSession();
        IUserDao userDao3 = sqlSession3.getMapper(IUserDao.class);
        User user3 = userDao3.findById(41);
        System.out.printf("==> %s\n", user3);
        sqlSession2.close();
        System.out.println("user2 == user3:"+(user2 == user3));
    }
```

![image-20200420231625071](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200420231625071.png)

我们也可以通过测试，发现两次取出的对象完全相同，是同一份地址的引用。

参考：

[http://www.ehcache.org/documentation/](http://www.ehcache.org/documentation/)

[ehcache2拾遗之copyOnRead，copyOnWrite](https://www.bbsmax.com/A/ZOJPRNeydv/)

[https://www.ablanxue.com/prone_10367_1.html](https://www.ablanxue.com/prone_10367_1.html)