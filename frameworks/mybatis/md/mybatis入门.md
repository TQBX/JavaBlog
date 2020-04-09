# Maven构建MyBatis项目

# 导入Maven依赖

```xml
<!-- MyBatis依赖 -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.5</version>
</dependency>
```

# 基于xml配置

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--mybatis主配置文件-->
<configuration>
    <!--加载类路径下的配置文件-->
    <properties resource="jdbc.properties"></properties>
    <!--配置环境-->
    <environments default="mysql">
        <!--配置mysql环境-->
        <environment id="mysql">
            <!--配置jdbc事务管理方式-->
            <transactionManager type="JDBC"></transactionManager>
            <!--配置数据源，使用连接池-->
            <dataSource type="POOLED">
                <!--配置连接数据库的四个基本信息-->
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.username}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>
    <!--指定映射配置文件的位置,映射配置文件指的是每个dao独立的配置文件-->
    <mappers>
        <mapper resource="com/smday/dao/Userdao.xml"></mapper>
    </mappers>
</configuration>
```

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql:///spring
jdbc.username=root
jdbc.password=123456
```

# 配置映射

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace名称空间唯一-->
<mapper namespace="com.smday.dao.UserDao">
    <!--配置查询所有-->
    <select id="findAll" resultType="com.smday.domain.User">
        select * from user
    </select>
</mapper>
```

# 获取sqlSession操作数据库

```java
/**
 * @author Summerday
 */
public class MybatisTest {

    public static void main(String[] args) throws Exception{

        String resource = "SqlMapConfig.xml";
        //读取配置文件
        InputStream inputStream = Resources.getResourceAsStream(resource);
        //创建sqlSessionFactory工厂(建造者模式)
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);
        //使用工厂生产sqlSession对象(工厂模式)
        SqlSession sqlSession = factory.openSession();
        //使用sqlSession创建dao接口的代理对象(代理模式)
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        //使用代理对象执行方法
        List<User> users = userDao.findAll();
        System.out.println(users);
        //释放资源
        sqlSession.close();
        inputStream.close();
    }

}
```

# 基于注解

将UserDao.xml移除，在dao接口的方法上使用@Select注解，并且指定SQL语句。

```java
public interface UserDao {
    /**
     * 查询所有
     * @return
     */
    @Select("select * from user")
    List<User> findAll();
}
```

SqlMapConfig.xml中的mapper标签，使用class属性指定指定dao接口的全限定名。

```xml
<!--
	指定映射配置文件的位置,映射配置文件指的是每个dao独立的配置文件
    注解配置应该使用class属性指定被注解的dao全限定类名
-->    
<mappers>
    <mapper class="com.smday.dao.UserDao"></mapper>
</mappers>
```

# MybatisUtil.java工具类

```java
package com.smday.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Summerday
 */
public class MyBatisUtil {

    /**
     * 利用ThreadLocal类为每个线程都提供独立的sqlSession
     */
    private static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<SqlSession>();
    /**
     * 创建sqlSession的工厂
     */
    private static SqlSessionFactory sqlSessionFactory;

    //加载配置文件
    static {
        try {
            InputStream in = Resources.getResourceAsStream("SqlMapConfig.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("配置文件加载失败!");
        }
    }
    private MyBatisUtil(){
    }

    /**
     * 获取SqlSession
     * @return
     */
    public static SqlSession getSqlSession(){
        //从当前线程获取用SqlSession对象
        SqlSession sqlSession = threadLocal.get();
        //如果SqlSession对象为空
        if(sqlSession == null){
            //利用sqlSessionFactory创建SqlSession对象
            sqlSession = sqlSessionFactory.openSession();
            //将SqlSession对象与当前线程绑定在一起
            threadLocal.set(sqlSession);
        }
        return sqlSession;
    }

    /**
     * 将当前线程与SqlSession解绑
     */
    public static void closeSqlSession(){
        //从当前线程中获取SqlSession对象
        SqlSession sqlSession = threadLocal.get();
        if(sqlSession!=null){
            //关闭SqlSession
            sqlSession.close();
            //移除map中的key,也就是当前的ThreadLocal实例
            threadLocal.remove();
        }
    }
}
```

关于ThreadLocal的文章，必须收藏这一篇：[JAVA并发-自问自答学ThreadLocal](https://www.jianshu.com/p/807686414c11)