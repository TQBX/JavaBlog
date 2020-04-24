[toc]

# 一、导入需要的依赖坐标

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.smday</groupId>
    <artifactId>mybatis_generator</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.6</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-core</artifactId>
            <version>1.3.7</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.5</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

# 二、建立相应的目录结构

![image-20200424224059787](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200424224059787.png)

# 三、编写mbg的XML配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <classPathEntry location="E:\Java\maven_repository\mysql\mysql-connector-java\5.1.6\mysql-connector-java-5.1.6.jar" />

    <context id="DB2Tables" targetRuntime="MyBatis3">
        <!-- jdbcConnection：指定如何连接到目标数据库 -->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://localhost:3306/spring"
                        userId="root"
                        password="123456">
        </jdbcConnection>
        <javaTypeResolver >
            <!--Java类型解析器不应该强制使用BigDecimal字段-->
            <property name="forceBigDecimals" value="false" />
        </javaTypeResolver>

        <!--
            javaModelGenerator：指定javaBean的生成策略
            targetPackage="com.smday.domain"：目标包名
            targetProject="src\main\java"：目标工程
        -->
        <javaModelGenerator targetPackage="com.smday.domain" targetProject="src\main\java">
            <!--Java模型生成器应使用子包-->
            <property name="enableSubPackages" value="true" />
            <!--Java模型生成器还应修剪字符串,修剪数据库字符列末尾返回的空白字符-->
            <property name="trimStrings" value="true" />
        </javaModelGenerator>
        <!-- sqlMapGenerator：sql映射生成策略： -->
        <sqlMapGenerator targetPackage="com.smday.dao"  targetProject="src\main\resources">
            <!--SQL Map生成器应使用子包-->
            <property name="enableSubPackages" value="true" />
        </sqlMapGenerator>
        <!-- javaClientGenerator:指定mapper接口所在的位置 -->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.smday.dao"  targetProject="src\main\java" >
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>
        <!-- 指定要逆向分析哪些表：根据表要创建javaBean -->
        <table schema="DB2ADMIN" tableName="user" domainObjectName="User" >

            <!--如果设置为false或者不指定,MBD将会允许驼峰命名的方式定义列名-->
            <!--<property name="useActualColumnNames" value="false"/>-->
            <!--column代表主键列，数据库类型是DB2。这将导致MBG在生成的<insert>语句中生成适当的<selectKey>元素，以便可以返回新生成的键(使用DB2特定的SQL)。-->
            <!--<generatedKey column="ID" sqlStatement="" identity="true" />-->
            <!--列DATE_FIELD将映射到名为startDate的属性。这将覆盖默认属性(在本例中为DATE_FIELD)，如果将useActualColumnNames属性设置为false，则覆盖dateField。-->
            <!--<columnOverride column="DATE_FIELD" property="startDate" />-->
            <!--FRED列将被忽略。没有SQL会列出字段，也不会生成Java属性。-->
            <!--<ignoreColumn column="FRED" />-->
            <!--无论实际数据类型如何，列LONG_VARCHAR_FIELD都将被视为VARCHAR字段。-->
            <!--<columnOverride column="LONG_VARCHAR_FIELD" jdbcType="VARCHAR" />-->

        </table>

    </context>
</generatorConfiguration>
```

# 四、选择启动方式

启动方式有很多种，如插件形式，xml形式，java形式都是可以的，当然这里使用java代码的方式，代码的话官方文档直接copy就完事了。

```java
    @Test
    public void test() throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File("D:\\mybatis\\mybatis_generator\\src\\main\\resources\\mbg.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }
```

# 五、执行成功，工程生成

紧接着，就出现了以下画面，自动再dao下生成了mapper接口和mapper.xml，再domain中生成了一个实体类User和一封装条件的UserExample。

![image-20200424222037196](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200424222037196.png)



# 六、主配置文件的简单配置

当然主配置文件可不要忘记配置了，映射文件也记得对应起来。

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
        <mapper resource="com/smday/dao/UserMapper.xml"></mapper>
    </mappers>
</configuration>
```

接着就可以测试增删改查了。

# 六、测试基础的增删改查

方法太多，眼花缭乱，这部分各位可以自己去尝试。

```java
    @Test
    public void testMBG() throws IOException {
        String path = "mybatisConfig.xml";
        InputStream in = Resources.getResourceAsStream(path);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
        SqlSession sqlSession = factory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        //查询id为54的用户
        User user = mapper.selectByPrimaryKey(54);
        System.out.println("id = 54的user:"+user);
        user.setId(80);
        user.setUsername("hello");
        //添加一条记录,返回受影响行数
        int num = mapper.insert(user);
        System.out.println("新增一条id为80的记录");
        System.out.println("受影响行数:"+num);
        //提交事务
        sqlSession.commit();
        //再次查询
        User user1 = mapper.selectByPrimaryKey(80);
        System.out.println("id = 80的user:"+user1);
    }
```

![image-20200424221947173](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200424221947173.png)

# 七、测试多条件

我们可以发现除了User实体类以外，MBG还生成了一个Example可以封装条件的对象，里面的方法非常多，就不一一赘述了，说实话，看到的时候还是很惊讶的，因为很多sql语句还没有写过呢（捂脸）。

直接上手测试一把：

```java
    @Test
    public void testExample() throws IOException {
        String path = "mybatisConfig.xml";
        InputStream in = Resources.getResourceAsStream(path);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
        SqlSession sqlSession = factory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        //UserExample用于封装查询条件
        //查询所有
        //List<User> users = mapper.selectByExample(null);
        //for (User user : users) {
        //    System.out.println(user);
        //}

        //查询id是0-50之间,且名字中有王的user或者sex为男的user
        UserExample example = new UserExample();
        //Criteria为UserExample的静态内部类,用于拼接条件
        UserExample.Criteria criteria = example.createCriteria();

        criteria.andIdBetween(0,50);
        criteria.andUsernameLike("%王%");

        UserExample.Criteria criteria1 = example.createCriteria();
        criteria1.andSexEqualTo("男");
        //or连接两个条件
        example.or(criteria1);

        List<User> userList = mapper.selectByExample(example);
        for (User user : userList) {
            System.out.println(user);
        }
    }
```

![image-20200424223129308](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200424223129308.png)

![image-20200424223114455](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200424223114455.png)