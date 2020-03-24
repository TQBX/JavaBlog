不使用自动装配（自动扫描）

```java

public class UserServiceNormal implements UserService {


    private UserDao userdao;


    public UserServiceNormal() {
        super();
    }

    public void setUserdao(UserDao userdao) {
        this.userdao = userdao;
    }

    public UserServiceNormal(UserDao userdao) {
        this.userdao = userdao;
    }

    @Override
    public void add() {
        userdao.add();
    }
}

```



# 构造函数装配

```java

@Configuration
public class AppConfig {

    //在启动时自动读取
    @Bean
    public UserDao userDaoNormal(){
        System.out.println("userDao 创建");
        return new UserDaoNormal();
    }

    @Bean
    public UserService userServiceNormal(UserDao userDao){
        System.out.println("userDaoNormal 创建");
        return new UserServiceNormal(userDao);
    }
    
}
```



# 利用setter方法

```java
@Configuration
public class AppConfig {

    //在启动时自动读取
    @Bean
    public UserDao userDaoNormal(){
        System.out.println("userDao 创建");
        return new UserDaoNormal();
    }

    @Bean
    public UserService userServiceNormal(UserDao userDao){
        System.out.println("userDaoNormal 创建");
        UserServiceNormal userService = new UserServiceNormal();
        userService.setUserdao(userDao);
        return userService;
    }

}
```

