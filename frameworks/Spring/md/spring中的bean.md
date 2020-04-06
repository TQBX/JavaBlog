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

依赖的概念我们已经清楚，就是对象间的耦合关系嘛，现在这种依赖关系由spring来维护，当对象相互需要的时候，也仅需要我们在配置文件中告知spring即可，spring这种依赖关系维护的方式，就是所谓的依赖注入。（dependency injection ）

所以通过DI，对象的依赖关系将有系统中负责协调各对象的第三方组件在创建对象的时候进行设定，对象无需自行创建或惯例他们的依赖关系，因为这种关系将会被自动注入到需要他们的对象之中。

```xml
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

## 三种注入方式

【构造函数】：`constructor-arg`

```xml
    <bean id="userService" class="com.smday.service.impl.UserServiceImpl">
        <!-- name:构造函数中指定名称的参数(常用) -->
        <constructor-arg name="name" value="天乔巴夏丶"></constructor-arg>
        <constructor-arg name="age" value="18"></constructor-arg>
        <constructor-arg name="birthday" ref="now"></constructor-arg>

    </bean>
    <!--配置一个日期对象-->
    <bean id="now" class="java.util.Date"></bean>
```



```
     使用的标签:
     标签位置:bean标签的内部
     标签中的属性
        type:指定构造函数中某个或某些参数的类型
        index:指定构造函数中参数的索引位置,从零开始
        

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

