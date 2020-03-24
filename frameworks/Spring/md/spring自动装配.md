# @Autowired实现自动装配

如果使用接口，`@Component`需要加载实现类身上。

并且多个实现类都存在时，可能会出现自动装配出现歧义。

【接口】

```java
public interface Userservice {
    void add();
}
```

【实现类一】

```java
@Component
public class UserServiceFestival implements Userservice {
    @Override
    public void add() {
        System.out.println("注册用户,并发送节日券");
    }
}
```

【实现类二】

```java
@Component
public class UserServiceNormal implements Userservice {
    @Override
    public void add() {
        System.out.println("添加用户");
    }
}
```

【进行测试】

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Appconfig.class)
public class UserServiceTest {
    @Autowired
    private Userservice userservice;

    @Test
    public void testMethod(){
        userservice.add();
    }
}
```

【报错】

![image-20200324211539356](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200324211539356.png)

# 设置首选bean的方法

【解决办法一】：设置首选bean，`@primary`(只能设置一个)

```java
@Component
@Primary
public class UserServiceFestival implements Userservice {
    @Override
    public void add() {
        System.out.println("注册用户,并发送节日券");
    }
}
```

【解决办法二】：使用限定符，`@Qualifier("festival")`，测试的时候可以指定。

```java
@Component
@Qualifier("festival")
public class UserServiceFestival implements Userservice {
    @Override
    public void add() {
        System.out.println("注册用户,并发送节日券");
    }
}
```

```java
    @Autowired
    @Qualifier("festival")
    private Userservice userservice;
```

【解决办法三】：还是限定符，在`@Component`中定义标识

```java
@Component("normal")
public class UserServiceNormal implements Userservice {

    @Override
    public void add() {
        System.out.println("添加用户");
    }
}
```

```java
    @Autowired
    @Qualifier("normal")
    private Userservice userservice;
```
【解决办法四】：spring默认给每个实现类分配id，值为类名首字母小写。

```java
    @Autowired
    @Qualifier("userServiceFestival")
    private Userservice userservice;
```

【解决办法五】：直接使用`@Resource`注解(这并不是Spring的标准，而是JDK的标准），`import javax.annotation.Resource;`，代替`@Autowired`+`@Qualifier`。

```java
    @Resource(name = "userServiceFestival")
    private Userservice userservice;
```

