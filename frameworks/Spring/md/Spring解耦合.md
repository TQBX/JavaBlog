> 注：以下内容是参考黑马的spring教程，加之个人的一些新见解整合而成的学习笔记。

# Spring的概述

先来一波醒目的spring模块图。

![spring-overview](E:\2Java_sources\黑马\57.spring\spring\spring_day01\资料\spring-framework-5.0.2.RELEASE-docs\spring-framework-reference\images\spring-overview.png)

Spring这个**轻量级的开源框架**的创建是用来代替更加重量级的企业级Java技术，以`控制反转（Inversion of Control ）`和`面向切面编程（Aspect-Oriented Programming）`为内核，提供了表现层Spring MVC和持久层Spring JDBC以及业务层事务管理等众多企业级的应用技术，还整合开源世界众多著名的第三方类库，成为使用最多的JavaEE企业应用开源框架。

# Spring采取的策略

Spring为了降低Java企业级开发的复杂性，采取的策略：

1. 基于POJO的轻量级和最小侵入性编程。
2. 通过依赖注入和面向接口实现松耦合。
3. 基于切面和惯例进行声明式编程。
4. 通过切面和模板减少样板代码。



# 何谓耦合？如何解耦？

我们常说的一句话叫做：**高内聚，低耦合**，这是软件设计中的圣经。

- 啥是耦合？耦合指的是对象之间的依赖性，对象间耦合度越高，维护成本越高。

- 啥是内聚？内聚指的是一个模块内部各个元素的紧密程度，之前学的封装的概念就是为了达到内聚的目标，保证信息隐蔽与局部化。

所以，一句话就是：我们希望同一个模块内的各个元素要高度紧密，但各个模块之间的相互依存度却不要那么紧密。

下面这段是简化版的三层：dao数据库访问层，service业务层，以及模拟的servlet层。

```java
//dao层
public class UserDaoImpl implements UserDao {
    @Override
    public void insertUser() {
        //省略数据库操作
        System.out.println("插入一条用户记录……");
    }
}
//service层
public class UserServiceImpl implements UserService {
    /**
     *  引入UserDao对象
     */
    private UserDao userDao = new UserDaoImpl();
    @Override
    public void insertUser() {
        userDao.insertUser();
    }
}
//模拟servlet
public class UserServlet {

    /**
     * 引入userService对象
     */
    static UserService userService = new UserServiceImpl();
    public static void main(String[] args) {
        userService.insertUser();
    }
}
```

可以明显地发现，Servlet层需要service层地对象干活，service层需要dao层对象干活，对象间的联系是十分密切的，或者说对象间的依赖关系并不松！而这样子的耦合是没有办法消除的，我们能做的只有尽可能地**削弱耦合**。

# 利用工厂模式+反射解耦合

如果让我们自己来解耦，我们也许会像下面这样来操作：

一、不希望在程序中写死某些数据，我们也许会创建一个配置文件，将必要的数据以键值对的形式存入。

【bean.properties】

```pro
UserDao = com.smday.dao.impl.UserDaoImpl
UserService = com.smday.service.impl.UserServiceImpl
```

二、然后呢，利用我们学过的工厂模式，在服务器启动应用加载的时候利用工厂，读取配置文件，接收参数创建对应全类名的对象。

【BeanFactory】

```java

public class BeanFactory {
    //从配置文件中读取
    private static Properties prop;
    static {
        try {
            InputStream in = BeanFactory.class.getClassLoader().getResourceAsStream("bean.properties");
            prop = new Properties();
            prop.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("初始化Properties失败!");
        }

    }
    public static Object getBean(String beanName) {
        Object bean = null;
        try {
            //获取全类名
            String beanPath = prop.getProperty(beanName);
            //调用对应类的默认构造函数
            bean = Class.forName(beanPath).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
}
```

三、ok，我们现在只需要这样，我们不需要显式地new一个dao对象了：

```java
public class UserServiceImpl implements UserService {
    /**
     *  引入UserDao对象
     */
    private UserDao userDao = (UserDao)BeanFactory.getBean("UserDao");

    @Override
    public void insertUser() {
        userDao.insertUser();
    }
}
```

【两者的区别】

```java
private UserDao userDao = new UserDaoImpl();//自己直接寻找并创建对象
private UserDao userDao = (UserDao)BeanFactory.getBean("UserDao");//交给工厂第三方去创建
```

我们可以直观地发现，我们不再需要直接主动new一个dao对象，而是利用我们定义的工厂给我们分配对象对吧，而这种从主动到被动方式的转变，其实就是**控制反转（Ioc）的思想**，这也是spring框架的核心之一，它**削减了程序间的耦合**。

> spring的概念渐渐引入，妙啊。

# 上述工厂模式不足之处

再创建实例的时候，每调用一次就会产生一次对象，明明可以只是用一个UserService，却多此一举地创建了许许多多的UserService对象，是相当影响性能的。

```java
public class UserServlet {
    /**
     * 引入userService对象
     */
    static UserService userService = (UserService) BeanFactory.getBean("UserService");
    static UserService userService1 = (UserService) BeanFactory.getBean("UserService");
    static UserService userService2 = (UserService) BeanFactory.getBean("UserService");

    public static void main(String[] args) {
        System.out.println(userService1 == userService2);//false，因为每次newInstance都会调用一次默认构造函数，多例
        userService.insertUser();
    }
}
```

再考虑bean很少出现线程安全问题的情况下，应该优先选择单例。

如何保证单例呢？可以创建一个map容器，在工厂类加载的时候，就将对应的key和object存入，保证只加载一次，且只有一份。

```java
public class SingleTonBeanFactory {
    //定义一个Properties对象
    private static Properties props;

    //定义一个Map,用于存创建对象的容器
    private static Map<String,Object> beans;

    //使用静态代码块为Properties对象赋值
    static {
        try {
            //实例化对象
            props = new Properties();
            //获取properties文件的流对象
            InputStream in = BeanFactory.class.getClassLoader().getResourceAsStream("bean.properties");
            props.load(in);
            //实例化容器
            beans = new HashMap<String,Object>();
            //取出配置文件中所有的Key
            Enumeration keys = props.keys();
            //遍历枚举
            while (keys.hasMoreElements()){
                //取出每个Key
                String key = keys.nextElement().toString();
                //根据key获取value
                String beanPath = props.getProperty(key);
                //反射创建对象
                Object value = Class.forName(beanPath).newInstance();
                //把key和value存入容器中
                beans.put(key,value);
            }
        }catch(Exception e){
            throw new ExceptionInInitializerError("初始化properties失败！");
        }
    }

    /**
     * 根据bean的名称获取对象
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName){
        return beans.get(beanName);
    }
}
```

```java
public class UserServlet {
    /**
     * 引入userService对象
     */
    static UserService userService = (UserService) SingleTonBeanFactory.getBean("UserService");
    static UserService userService1 = (UserService) SingleTonBeanFactory.getBean("UserService");
    static UserService userService2 = (UserService) SingleTonBeanFactory.getBean("UserService");

    public static void main(String[] args) {
        System.out.println(userService1 == userService2);//true,同一个实例对象
        userService.insertUser();
    }
}
```

可以看到，`newInstance()`只有在BeanFactory加载的时候在静态代码块中调用一次，创建类名和对象存入Map并且只有一份，就能够保证单例。

# 构建spring环境

了解了上面的控制反转的理念，再进行spring内容的理解，将会容易许多。

首先，创建maven工程，并配置pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.smday</groupId>
    <artifactId>spring_learn</artifactId>
    <version>1.0-SNAPSHOT</version>
    <!-- 设置打包方式 -->
    <packaging>jar</packaging>
    <dependencies>
        <!-- springframework-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.2.4.RELEASE</version>
        </dependency>
    </dependencies>
</project>
```

当我们添加了`org.springframework.spring-context`依赖之后，就会发现，maven自动下载了相关的包，他们的依赖关系如下：

![image-20200401121417089](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200401121417089.png)

可以发现`spring-expression`、`spring core`、`spring beans`、`spring context`是小绿图中核心容器的四部分，而`spring jcl`实际上集成了`org.apache.commons.logging`的日志组件，`spring aop`提供了面向切面编程的相关功能。

创建bean.xml配置文件（当然这里文件命名只是演示spring中类似于bean工厂的配置方式），同样的id作为唯一标识，class表示全类名，应该不难理解。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--把对象的创建交给spring管理-->
    <bean id="userDao" class="com.smday.dao.impl.UserDaoImpl"></bean>
    <bean id="userService" class="com.smday.service.impl.UserServiceImpl"></bean>
</beans>
```

接着获取核心容器对象，通过核心容器再获取bean对象，这个过程也是十分熟悉，核心容器就是我们说的工厂，bean对象就是你要创建的对象，配置完成之后，我们再需要bean的时候，直接让spring狠心容器给我们创建就ok了。

```java
/**
 * @author Summerday
 *
 * 模拟表现层,调用业务层
 *
 */
public class UserServlet {

    /**
     * 获取spring的Ioc核心容器,并根据id获取对象
     * @param args
     */
    public static void main(String[] args) {
        //获取核心容器对象
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        //根据id获取bean对象
        UserService userService = (UserService) ac.getBean("userService");
        //根据id和类对象获取bean对象
        UserDao userDao = ac.getBean("userDao",UserDao.class);
    }
}
```

# Spring容器继承图

Spring自带了多个容器的实现，总的来说可以归为两种类型：bean工厂和应用上下文，也就是下图中BeanFactory接口和ApplicationContext接口分别定义。

![image-20200401112336903](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200401112336903.png)

> 两者在功能方面的差别。

`BeanFactory`：是最简单的容器，提供最基本的DI支持。

`ApplicationContext`：基于BeanFactory，并提供应用框架级别的服务。

>  两者在构建核心容器时，创建对象所采取的策略有所区别。

`BeanFactory`：采用延迟加载的方式，只有根据id获取对象的时候，才真正创建对象。多例对象适用。

`ApplicationContext`：采用立即加载的方式，只要一读取完配置文件，马上就创建配置文件中配置的对象。单例对象适用。

ps：`ApplicationContext`还是使用会更多一些，主要探讨这个。

# ApplicationContext的三个常用实现类

Spring的ApplicationContext负责bean的创建和组装，多种实现类的区别在于如何加载配置。

`ClassPathXmlApplicationContext`：加载类路径下的xml配置文件。

`AnnotationConfigApplicationContext`：读取Java配置类文件。

`FileSystemXmlApplicationContext`：加载磁盘任意路径下的配置文件（需要有访问权限）

`XmlWebApplicationContext`：读取web应用下的xml配置文件。

`AnnotationConfigWebApplicationContext`：从Java配置类中加载web应用上下文。



