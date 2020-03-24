在pom.xml中添加依赖

```xml
![sptingjunit](E:\1JavaBlog\frameworks\Spring\pic\sptingjunit.png)        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.2.4.RELEASE</version>
        </dependency>        
		<dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.2.4.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
```

创建测试类Appconfig，这里@Configuration注释相当于标识这是一个配置类。

```java
@Configuration
//@ComponentScan("com.my.demo")
@ComponentScan(basePackageClasses = {UserController.class, UserService.class, UserDao.class})
public class Appconfig {

}
```

在test中测试

![sptingjunit](E:\1JavaBlog\frameworks\Spring\pic\sptingjunit.png)

```java
@RunWith(SpringJUnit4ClassRunner.class)//利用log4j搭建测试环境
@ContextConfiguration(classes = Appconfig.class)//加载配置类（还可以利用xml的classpath开启）
public class UserServiceTest {
    @Resource(name = "userServiceFestival")
    private UserService userservice;

    @Test
    public void testMethod(){
        userservice.add();
    }
}
```

