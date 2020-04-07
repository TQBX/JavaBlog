# 基于xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--mybatis主配置文件-->
<configuration>
    <!--配置环境-->
    <environments default="mysql">
        <!--配置mysql环境-->
        <environment id="mysql">
            <transactionManager type="JDBC"></transactionManager>
            <!--配置数据源-->
            <dataSource type="POOLED">
                <!--配置连接数据库的四个基本信息-->
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/spring"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>
    <!--指定映射配置文件的位置,映射配置文件指的是每个dao独立的配置文件-->
    <mappers>
        <mapper resource="com/smday/dao/Userdao.xml"></mapper>
    </mappers>
</configuration>
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.smday.dao.UserDao">

    <!--配置查询所有-->
    <select id="findAll" resultType="com.smday.domain.User">
        select * from user
    </select>

</mapper>
```

```java
/**
 * @author Summerday
 */
public class MybatisTest {

    public static void main(String[] args) throws Exception{

        String resource = "SqlMapConfig.xml";
        //读取配置文件
        InputStream inputStream = Resources.getResourceAsStream(resource);
        //创建sqlSessionFactory工厂
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);
        //使用工厂生产sqlSession对象
        SqlSession sqlSession = factory.openSession();
        //使用sqlSession创建dao接口的代理对象
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

