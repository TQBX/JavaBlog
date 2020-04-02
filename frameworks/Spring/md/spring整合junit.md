# spring整合junit的配置流程

1. 导入spring整合junit的jar包

```xml
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.2.4.RELEASE</version>
        </dependency>
```

2. 使用junit提供的Runwith注解，将原有的main方法用spring提供的替换。
   - `@RunWith(SpringJUnit4ClassRunner.class)`
3. 通过ContextConfiguration注解告知spring的运行期，spring的ioc创建是基于xml还是注解，并说明其位置。
   - location属性：指定xml文件的位置，加上classpath关键字，表示在类路径下。
   - classes属性：指定注解类所在的位置。

【原先的测试案例】

```JAVA
public class AccountServiceTest {
    @Test
    public void testFindAll(){
        //基于xml：ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        ApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        AccountService as = ac.getBean("accountService", AccountService.class);
        List<Account> allAccount = as.findAllAccount();
        for (Account account : allAccount) {
            System.out.println(account);
        }
    }
}
```

# xml配置整合junit

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:bean.xml")
public class AccountServiceTest {
    @Autowired
    private AccountService as;
    @Test
    public void testFindAll(){
        List<Account> allAccount = as.findAllAccount();
        for (Account account : allAccount) {
            System.out.println(account);
        }
    }
}
```

# 注解配置整合junit

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class AccountServiceTest {
    @Autowired
    private AccountService as;
    @Test
    public void testFindAll(){
        List<Account> allAccount = as.findAllAccount();
        for (Account account : allAccount) {
            System.out.println(account);
        }
    }
}
```

