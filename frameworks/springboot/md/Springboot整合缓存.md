# 一、快速体验整合缓存

Springboot2.x的cache组件在I/O中，为`Spring cache abstraction`。

![](src/2.png)

主要的依赖组件依赖如下：

```xml
    <dependencies>
        <!-- 整合cache -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <!-- 整合mybatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.2</version>
        </dependency>
        <!-- mysql驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- 整合web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
</dependencies>
```

编辑application.yml配置。

```yml
# 配置数据源
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/spring_cache?serverTimezone=UTC
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
mybatis:
  configuration:
    map-underscore-to-camel-case: true
server:
  port: 8088
```

定义一个与数据库交互的mapper。

```java
@Mapper
public interface EmployeeMapper {

    @Select("select * from employee where id = #{id}")
    Employee getEmpById(Integer id);

    @Update("update employee set lastName=#{lastName},email=#{email},gender=#{gender},d_id=#{dId} where id=#{id}")
    void updateEmp(Employee employee);

    @Delete("delete from employee where id = #{id}")
    void deleteEmpById(Integer id);

    @Insert("insert into employee(lastName,email,gender,d_id) values(#{lastName},#{email},#{gender},#{dId}")
    void insertEmp(Employee employee);
}
```

在主配置类上使用`@MapperScan`注解扫描mapper接口所在的包，使用`@EnableCaching`开启基于注解的缓存。

```java
@MapperScan("com.smday.cache.mapper")
@SpringBootApplication
@EnableCaching
public class Springboot01CacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(Springboot01CacheApplication.class, args);
    }
}
```

在需要缓存的方法上加上适当的注解，如`@Cacheable`。

```java
@Service
public class EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    /**
     * 将方法的运行结果进行缓存,以后再需要相同的数据,直接从缓存中获取,不用调用方法
     */
    @Cacheable(cacheNames = "emp",condition = "#id>0")
    public Employee getEmp(Integer id){
        System.out.println("查询"+id+"号员工!");
        return employeeMapper.getEmpById(id);
    }
}
```



# 二、整合Redis

pom.xml到入依赖：

```xml
<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

配置application.yml：

```yml
spring:
  redis:
    host: 121.199.16.31
    password: 123456
```

测试Redis：

```java
@SpringBootTest
class Springboot01CacheApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;  //操作的k-v是字符串
    @Autowired
    RedisTemplate<Object,Employee> redisTemplate;  //操作的k-v是对象
    @Test
    public void test1(){
        String name = stringRedisTemplate.opsForValue().get("name");
        Employee employee = new Employee();
        employee.setdId(1);
        employee.setGender(2);
        employee.setLastName("hyh");
        redisTemplate.opsForValue().set("emp",employee);
    }
}
```

自定义redis序列化规则：

```java
@Configuration
public class MyRedisConfig {

    @Bean
    public RedisTemplate<Object, Employee> redisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<Object, Employee> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(Employee.class));
        return template;
    }
}
```

Springboot2.x自定义ReidsCacheManager的改变：[https://www.jianshu.com/p/20366ecf12ce](https://www.jianshu.com/p/20366ecf12ce)

```java
/**
     * 缓存管理器
     */
@Bean
public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    //初始化一个RedisCacheWriter
    RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
    //设置CacheManager的值序列化方式为json序列化
    RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
    RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
        .fromSerializer(jsonSerializer);
    RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(pair);
    //设置默认超过期时间是30秒
    defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
    //初始化RedisCacheManager
    return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
}
```

