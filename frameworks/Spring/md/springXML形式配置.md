创建applicationContext.xml。

![spring。xml](E:\1JavaBlog\frameworks\Spring\pic\spring。xml.png)

默认是这样子的，相当于增加了@Configuration注释

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

</beans>
```

配置扫描

```xml
        <!--配置扫描,相当于@ComponentScan("com.my.demo")-->
        <context:component-scan base-package="com.my.demo"/>
```

此时读取配置文件

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserControllerTest {
    @Autowired
    private UserController userController;

    @Test
    public void testAdd(){
        userController.add();
    }
}
```

