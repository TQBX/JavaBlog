在Servlet3.0之前，需要编写web.xml进行servlet的相关配置，初始化参数，路径匹配啥的。

Servlet3.0之后。就可以对其进行简化，不再需要创建web.xml了，而是使用@WebServlet注解进行设置。

# @WebServlet官方介绍

关于该注解，官方文档解释的十分清楚，这里搬运一波：[Annotation Type WebServlet](http://tomcat.apache.org/tomcat-7.0-doc/servletapi/javax/servlet/annotation/WebServlet.html)

大概意思如下：

这是一个用来声明Servlet配置的注解。

如果name属性没有定义，那么将使用类的全类名，如：com.my.MyServlet。

<u>该注解中必须声明value或者urlPattern属性，必须有一个，但不能同时出现。</u>

如果URL pattern是唯一的，则使用value，否则就使用urlPattern属性。

```java
//官方示例
@WebServlet("/path")} //“value=”可以省略不写
public class TestServlet extends HttpServlet ... {
@WebServlet(name="TestServlet", urlPatterns={"/path", "/alt"})//多个pattern
public class TestServlet extends HttpServlet ... {
```

注解的类必须继承HttpServlet。

> ps:可以发现，javax.servlet.annotation包下还有其他的注解如：[WebFilter](http://tomcat.apache.org/tomcat-7.0-doc/servletapi/javax/servlet/annotation/WebFilter.html)、[WebListener](http://tomcat.apache.org/tomcat-7.0-doc/servletapi/javax/servlet/annotation/WebListener.html)等，以后遇到的时候，可以去搜寻使用方法，大差不差。

# @WebServlet具体细节

```JAVA
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServlet {
    String name() default "";//等价于<servlet-name>，没有显示指定则为全类名

    String[] value() default {};//等价于<url-pattern>，用于单一匹配，可以省略

    String[] urlPatterns() default {};//等价于<url-pattern>，用于多匹配

    int loadOnStartup() default -1;//等价于 <load-on-startup>，指定加载顺序

    WebInitParam[] initParams() default {};//等价于<init-param>，指定初始化参数

    boolean asyncSupported() default false;//等价于<async-supported>，异步支持

    String smallIcon() default "";//等价于<small-icon>，指向16px*16px小图标

    String largeIcon() default "";//等价于<large-icon>，指向32px*32px小图标

    String description() default "";//等价于<description>，描述信息

    String displayName() default "";//等价于<display-name>，显示名
}
```

# @WebInitParam注解使用

参考：[Annotation Type WebInitParam](http://tomcat.apache.org/tomcat-7.0-doc/servletapi/javax/servlet/annotation/WebInitParam.html)

这个注解是用来指定为Servlet或者Filter指定初始化参数的。

```java
WebServlet(
    name="TestServlet",
    urlPatterns={"/test"},
    initParams={WebInitParam(name="test", value="true")}
) 
public class TestServlet extends HttpServlet { ...
```

# @WebServlet具体使用

直接看示例，只需要在类上进行注解即可。

```java
package com.my.sconfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @auther Summerday
 */
@WebServlet(
        value = "/ServletConfigDemo1",
        initParams = {
            @WebInitParam(name = "username",value = "summerday"), @WebInitParam(name = "age",value = "18")
        },
        loadOnStartup = 1,
        description = "使用WebServlet注解"
)
public class ServletConfigDemo1 extends HttpServlet {
    private ServletConfig config = null;
    //通过init获取ServletConfig对象
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;

    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(config);
        //获取所有初始化参数名和值
        Enumeration<String> names = config.getInitParameterNames();
        while(names.hasMoreElements()){
            String s = names.nextElement();
            String param = config.getInitParameter(s);
            System.out.println(s+">>>"+param);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

//输出结果
org.apache.catalina.core.StandardWrapperFacade@13928a07
age>>>18
username>>>summerday
```









