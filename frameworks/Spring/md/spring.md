![image-20200324165320737](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200324165320737.png)

IoC Inversion of Control ：控制反转

DI   Dependency Injection ：依赖注入

AOP ： Aspect Oriented Programming：面向切面编程

# 创建applicationContext.xml

![spring。xml](E:\1JavaBlog\frameworks\Spring\pic\spring。xml.png)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--
        bean元素:描述当前的对象,需要由spring管理
        id:属性:标识对象,微来在应用程序中可以根据id获取对象
        class:被管理对象的全类名
    -->
    <bean id="service" class="hello.MessageService"></bean>

    <bean id="printer" class="hello.MessagePrinter">
        <!--将service对象注入printer-->
        <property name="service" ref="service"></property>
    </bean>
</beans>
```



