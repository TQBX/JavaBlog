# Spring的优势之一：方便解耦，简化开发

注：以下内容是参考黑马的spring教程，加之个人的一些新见解整合而成的学习笔记。





# ！何谓耦合？

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

耦合永远都会存在，我们的目标是，尽可能削减耦合。

# 利用工厂模式+反射解耦合

bean.properties

```pro
UserDao = com.smday.dao.impl.UserDaoImpl
UserService = com.smday.service.impl.UserServiceImpl
```

BeanFactory

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

现在只需要这样：

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

# 两者有啥区别

```java
private UserDao userDao = new UserDaoImpl();//自己直接寻找并创建对象
private UserDao userDao = (UserDao)BeanFactory.getBean("UserDao");//交给工厂第三方去创建
```

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

如何保证单例，可以创建一个容器，在工厂类加载的时候，就将对应的key和object存入，保证只加载一次，且只有一份。

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

![spring-overview](E:\2Java_sources\黑马\57.spring\spring\spring_day01\资料\spring-framework-5.0.2.RELEASE-docs\spring-framework-reference\images\spring-overview.png)



![image-20200401112336903](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200401112336903.png)

配置pom.xml

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
    <!-- 解决1.5源值过时 -->
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>
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

当我们添加了org.springframework.spring-context依赖之后，就会发现，maven自动下载了相关的包，他们的依赖关系如下：

![image-20200401121417089](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200401121417089.png)

可以发现spring-expression、spring core、spring beans、spring context是小绿图中核心容器的四部分，而spring jcl实际上集成了org.apache.commons.logging的日志组件，spring aop



创建bean.xml配置文件（当然这里文件命名只是演示spring中类似于bean工厂的配置方式）

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



# ApplicationContext的三个常用实现类

`ClassPathXmlApplicationContext`：加载类路径下的配置文件。

`AnnotationConfigApplicationContext`：加载磁盘任意路径下的配置文件（需要有访问权限）

`FileSystemXmlApplicationContext`：读取注解创建容器。

# ApplicationContext与BeanFactory的区别

>  两者在构建核心容器时，创建对象所采取的策略有所区别。

`ApplicationContext`：采用立即加载的方式，只要一读取完配置文件，马上就创建配置文件中配置的对象。单例对象适用。

`BeanFactory`：采用延迟加载的方式，只有根据id获取对象的时候，才真正创建对象。多例对象适用。

# bean实例的三种创建方式

```xml

<!-- 一、使用默认构造函数创建，如果没有该默认构造函数，则创建失败。 -->
<bean id="userService" class="com.smday.service.impl.UserServiceImpl"></bean>

<!-- 二、使用普通公章中的方法创建对象(使用某个类中的方法创建对象,并存入spring容器 -->
<bean id="instanceFactory" class="com.smday.factory.InstanceFactory"></bean>
<bean id="userService" factory-bean="instanceFactory" factory-method="getUserService"></bean>
<!-- 三、使用工厂中的静态方法创建对象 -->
<bean id="userService" class="com.smday.factory.StaticFactory" factory-method="getUserService"></bean>

```

# bean实例的作用域

> 在默认情况下，spring应用上下文所有的bean都是单例的。

ps：有时bean的状态将会发生改变，重用一个bean实例将会造成安全问题，于是，可以通过bean标签的scope属性调整bean的作用域范围。

```xml
<!-- 默认单例 -->
<bean id="userService" class="com.smday.service.impl.UserServiceImpl" scope="singleton"></bean>
```

1. singleton ： 单例，不指定情况下则默认为单例。整个应用中只创建bean的一个实例。

2. protot ype：每次注入或通过spring应用上下文获取时，都会创建一个bean实例。

3. request：作用域web应用的请求范围
4. session：作用于web应用的会话范围
5. global-session：作用于集群环境的会话范围，若不是集群环境，则为session。

# bean实例的生命周期

单例bean：容器创建时对象出生，容器销毁时对象消亡，单例对象的生命周期和容器相同。

多例bean：使用bean对象时创建，只要在使用过程中就一直活着，当对象长时间不用且没有别的对象引用时，由Java垃圾回收器回收。

# spring依赖注入

```xml
    <!--
    
    Ioc的作用:降低程序间的耦合(依赖关系)
    
    依赖关系的管理:交给spring维护
    
    当前类需要用到其他类的对象时,由spring为我们提供,只需要在配置文件中说明
    
    依赖关系的维护:依赖注入
    
    spring依赖注入:dependency injection
    可以注入的数据分为三类
    
    1. 基本类型和string
    2. 其他bean类，在配置文件中或者注释配置过的bean
    3. 复杂类型、集合类型
    
    注入的方式分为三种
    
    1. 使用构造函数提供
    2. 使用set方法提供
    3. 使用注解提供
    
    -->
```

当前类需要用到其他类的对象时,由spring提供,只需要在配置文件中说明。spring负责维护程序间的依赖关系，降低耦合，这种维护可以看作是依赖注入。

## 注入方式

构造函数

```xml
    <bean id="userService" class="com.smday.service.impl.UserServiceImpl">
        <constructor-arg name="name" value="天乔巴夏丶"></constructor-arg>
        <constructor-arg name="age" value="18"></constructor-arg>
        <constructor-arg name="birthday" ref="now"></constructor-arg>

    </bean>
    <!--配置一个日期对象-->
    <bean id="now" class="java.util.Date"></bean>
```



```
     使用的标签:constructor-arg
     标签位置:bean标签的内部
     标签中的属性
        type:指定构造函数中某个或某些参数的类型
        index:指定构造函数中参数的索引位置,从零开始
        name:构造函数中指定名称的参数(常用)

        value:提供基本类型和String类型的数据
        ref:提供其他的bean类型数据,在spring的ioc核心容器中出现过的bean对象
     优势:在获取bean对象时,注入数据是必须的操作,否则对象无法创建成功.
     弊端:改变了bean对象的实例化方式,使我们在创建对象时,如果用不到这些数据,也必须提供
```

set方法

```xml
    <bean id="userService" class="com.smday.service.impl.UserServiceImpl">
        <property name="name" value="summerday"></property>
        <property name="age" value="18"></property>
        <property name="birthday" ref="now"></property>
    </bean>
    <!--配置一个日期对象-->
    <bean id="now" class="java.util.Date"></bean>
```



```
     使用的标签:property
     标签位置:bean标签的内部

     标签中的属性
        name:指定注入时调用的set方法的名称
        value:提供基本类型和String类型的数据
        ref:提供其他的bean类型数据,在spring的ioc核心容器中出现过的bean对象
     优势:创建对象时,没有明确的限制,可以直接使用默认构造函数
     弊端:如果有某个成员必须有值,则获取对象时,如果没有set方法,就会失。
```

注解

xxxx

集合类型注入

```

    list结构集合注入的标签:list array set
    map结构集合注入的标签:map,props

    结构相同,标签可以互换
```

```xml
    <bean id="userService" class="com.smday.service.impl.UserServiceImpl">
        <property name="myStrs">
            <array>
                <value>AAA</value>
                <value>BBB</value>
                <value>BBB</value>
            </array>
        </property>
        <property name="myList">
            <list>
                <value>AAA</value>
                <value>BBB</value>
                <value>BBB</value>
            </list>
        </property>
        <property name="mySet">
            <set>
                <value>AAA</value>
                <value>BBB</value>
                <value>BBB</value>
            </set>
        </property>
        <property name="myMap">
            <map>
                <entry key="testA" value="AAA"></entry>
                <entry key="testB" >
                    <value>BBB</value>
                </entry>
            </map>
        </property>
        <property name="myProp">
            <props>
                <prop key="testC">CCC</prop>
                <prop key="testD">DDD</prop>
            </props>
        </property>
    </bean>
```

