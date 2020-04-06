pom.xml需要的依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.smday</groupId>
    <artifactId>day04_tx</artifactId>
    <version>1.0-SNAPSHOT</version>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
    <packaging>jar</packaging>

    <dependencies>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.2.4.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.2.4.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.2.4.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.8.13</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.32</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.2.4.RELEASE</version>
        </dependency>

    </dependencies>

</project>
```

# 基于XML的配置声明式事务

1. 配置事务管理器。
2. 配置事务通知：使用`tx:advice`标签配置事务通知。
   - 属性id：给事务通知起一个唯一标识。
   - 属性transaction-manager：给事务通知提供一个事务管理器引用。
3. 配置aop：切入点表达式+与事务通知间的联系。
4. 配置事务的属性：在`tx:advice`标签内部配置。
   - isolation：指定事务的隔离级别,默认值尾DEFAULT 表示使用数据库的默认隔离级别
   - propagation：指定事务的传播行为,默认尾REQUIRED 表示一定会有事务,增删改的选择,查询可以选择SUPPORTS
   - read-only：指定事务是否只读,只有查询方法才可以设置为true,默认为false,表示读写
   - timeout：指定事务的超时时间,默认为-1.表示永不超时,如果指定了数值,以秒为单位
   - rollback-for：指定一个异常,当产生该异常时,事务回滚,产生其他异常时,事务不回滚,没有默认值,表示任何异常都回滚
   - no-rollback-for：指定一个异常,当产生该异常时,事务不回滚,产生其他异常时事务回滚,没有默认值,表示任何异常都回滚

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--配置业务层-->

    <bean id="accountService" class="com.smday.service.impl.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"></property>
    </bean>

    <!--配置数据源-->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
        <property name="url" value="jdbc:mysql://localhost:3306/spring"></property>
        <property name="username" value="root"></property>
        <property name="password" value="123456"></property>
    </bean>

    <!--配置持久层-->
    <bean id="accountDao" class="com.smday.dao.impl.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"></property>
    </bean>
    <!--1. 配置事务管理器-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>
    
    <!--2. 配置事务通知-->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <!--4. 配置事务的属性-->
        <tx:attributes>
            <tx:method name="*" propagation="REQUIRED" read-only="false"/>
            <tx:method name="find*" propagation="SUPPORTS" read-only="true"></tx:method>
        </tx:attributes>
    </tx:advice>
    
    <!--配置aop-->
    <aop:config>
        <!--3. 配置切入点表达式-->
        <aop:pointcut id="pt1" expression="execution(* com.smday.service.impl.*.*(..))"></aop:pointcut>
        <!--建立切入点表达式和事务通知间的对应关系-->
        <aop:advisor advice-ref="txAdvice" pointcut-ref="pt1"></aop:advisor>
    </aop:config>
</beans>

```

# 基于注解+xml形式配置声明式事务

1. 配置事务管理器。
2. 开启spring对注解事务的支持。
3. 在需要事务支持的地方使用`@Transactional`注解。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
    <!--配置spring创建容器时要扫描的包-->

    <context:component-scan base-package="com.smday"></context:component-scan>
    
    <!--配置jdbcTemplate-->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"></property>
    </bean>
    <!--配置数据源-->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
        <property name="url" value="jdbc:mysql://localhost:3306/spring"></property>
        <property name="username" value="root"></property>
        <property name="password" value="123456"></property>

    </bean>

    <!--1. 配置事务管理器-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--2. 开启spring对注解事务的支持-->
    <tx:annotation-driven transaction-manager="transactionManager"></tx:annotation-driven>
    
</beans>
```

```java
@Service("accountService")
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true)//只读型事务配置
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;
    @Override
    public Account findAccountById(Integer accountId) {
        return accountDao.findAccountById(accountId);
    }
    @Transactional(propagation = Propagation.REQUIRED,readOnly = false)//读写型事务配置
    @Override
    public void transfer(String sourceName, String targetName, Float money) {
        //执行操作
        //根据名称查询转出账户
        Account source = accountDao.findAccountByName(sourceName);
        //根据名称查询转入账户
        Account target = accountDao.findAccountByName(targetName);
        //转出账户减钱
        source.setMoney(source.getMoney()-money);
        //转入账户加钱
        target.setMoney(target.getMoney()+money);
        //更新转出账户
        accountDao.updateAccount(source);
        int i = 1/0;
        //更新转入账户
        accountDao.updateAccount(target);
    }
}
```

```java
/**
 * @author Summerday
 *
 * Spring整合Junit测试类
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:bean.xml")
public class AccountServiceTest {
    @Autowired
    private AccountService as;
    @Test
    public void testTransfer(){
        as.transfer("aaa","bbb",100f);
    }
}
```

# 基于纯注解的声明式事务实现

```properties
#jdbcConfig.properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/spring
jdbc.username=root
jdbc.password=123456
```

```java
/**
 * @author Summerday
 *
 * 连接数据库相关配置类
 */
public class JdbcConfig {

    @Value("${jdbc.driver}")//spel表达式
    private String driver;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;
    /**
     * 创建JdbcTemplate对象
     * @param dataSource
     * @return
     */
    @Bean(name = "jdbcTemplate")//bean注解让其进入容器
    public JdbcTemplate createJdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    /**
     * 创建数据源对象
     * @return
     */
    @Bean(name = "dataSource")
    public DataSource createDataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }
}
```

```java
/**
 * @author Summerday
 *
 * 事务相关的配置类
 */
public class TransactionConfig {

    /**
     * 用于创建事务管理器对象
     * @param dataSource
     * @return
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager createTransaction(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}
```

```java
/**
 * @author Summerday
 *
 * Spring 的配置类
 */

@Configuration
@ComponentScan("com.smday")
@Import({JdbcConfig.class,TransactionConfig.class})
@PropertySource("jdbcConfig.properties")
@EnableTransactionManagement
public class SpringConfiguration {
}
```

```java
/**
 * @author Summerday
 * 
 * Spring整合Junit测试类
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class AccountServiceTest {
    @Autowired
    private AccountService as;
    @Test
    public void testTransfer(){
        as.transfer("aaa","bbb",100f);
    }
}
```

