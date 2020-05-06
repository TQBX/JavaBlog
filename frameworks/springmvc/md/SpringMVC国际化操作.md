[toc]

# 一、导入需要的jstl包

```xml
    <dependency>
      <groupId>jstl</groupId>
      <artifactId>jstl</artifactId>
      <version>1.2</version>
    </dependency>
```

# 二、配置web.xml

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
                      http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0">

    <!--配置中文乱码过滤器-->
    <filter>
        <filter-name>characterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <!--指定编码方式-->
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <!--解决请求和响应乱码setCharacterEncoding-->
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>characterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!--配置前端控制器-->
    <servlet>
        <servlet-name>dispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <!--加载类路径下的配置文件-->
            <param-value>classpath:springmvc.xml</param-value>
        </init-param>
        <!--服务器启动时创建对象,值越小,优先级越高,越先创建对象-->
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>dispatcherServlet</servlet-name>
        <!--注意不是/*,而是，因为/*还会拦截*.jsp等请求-->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>

```

# 三、配置springMVC.xml

```XML
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 开启注解扫描 -->
    <context:component-scan base-package="com.smday"/>

    <!-- 视图解析器对象 -->
    <bean id="internalResourceViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/pages/"/>
        <property name="suffix" value=".jsp"/>
        <!--导入jstl包-->
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
    </bean>
    <!--springMVC管理国际化资源文件,配置资源文件管理器-->
    <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
        <!--指定国际化文件的基础名-->
        <property name="basename" value="i18n"/>
        <!--properties文件是GBK,于是进行一下转换-->
        <property name="defaultEncoding" value="GBK"/>
    </bean>
    <!-- 开启SpringMVC框架注解的支持 -->
    <mvc:annotation-driven/>
    <mvc:default-servlet-handler/>

</beans>
```

# 四、定义配置文件

![image-20200506220003177](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200506220003177.png)

# 五、前端页面通过jstl的fmt标签获取

```html
<%--
  Date: 2020/5/6
  Time: 19:51
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <head>
        <title>Title</title>
    </head>
    <body>
        <fmt:message key="username"/>:<input/><br>
        <fmt:message key="password"/>:<input/><br>
        <input type="submit" value='"<fmt:message key="loginBtn"/>"'>
    </body>
</html>
```

