[toc]

# 一、REST风格是啥？

`REST(Representational State Transfer) `：表述性状态传递，它是一种针对网络应用的设计和开发方式，可以降低开发的复杂性，提高系统的可伸缩性。

简单来说，HTTP协议本身是无状态的协议，客户端想要操作服务器，可以通过请求资源的方式，将"状态"进行传递。

- GET请求表示获取资源。
- POST请求表示新建资源。
- PUT请求表示更新资源。
- DELETE请求表示删除资源。

首先我们需要有一个基本的共识就是，浏览器的form表单中，只能有两种请求的方式，GET和POST。那么问题来了，PUT和DELETE如何来表示呢？服务器怎么确认你发的是这俩请求呢？我们需要做些什么呢？

- 配置支持REST风格的过滤器：`HiddenHttpMethodFilter`。
- Controller控制器在注解上规定接收方法的方式。
- 前端通过form表单提交，且提交方式为post，再定义一个名为_method的参数，值为请求方式PUT或DELETE。

原理很简单，我们看看`HiddenHttpMethodFilter`的源码就知道了：

```java
	//HiddenHttpMethodFilter
	/** Default method parameter: _method */
	public static final String DEFAULT_METHOD_PARAM = "_method";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpServletRequest requestToUse = request;
		//前端必须是post提交的参数
		if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null) {
            //获取名为methodParam的值，其实就是_method的值
			String paramValue = request.getParameter(this.methodParam);
			if (StringUtils.hasLength(paramValue)) {
                //装饰器模式对request进行了增强，重写了getMethod方法：put--> PUT
				requestToUse = new HttpMethodRequestWrapper(request, paramValue);
			}
		}
		//最后放行的是增强后的request
		filterChain.doFilter(requestToUse, response);
	}
```

# 二、配置支持REST风格过滤器

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

    <filter>
        <!--Spring对Rest风格的支持: 将普通请求转化为规定形式的PUT DELETE-->
        <filter-name>hiddenHttpMethodFilter</filter-name>
        <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>hiddenHttpMethodFilter</filter-name>
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

# 三、配置spirngMVC

```xml
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
    </bean>

    <!-- 开启SpringMVC框架注解的支持 -->
    <mvc:annotation-driven/>

    <!--放行静态资源-->
    <mvc:default-servlet-handler/>

</beans>
```



# 四、定义控制器

```java
@Controller
@RequestMapping("/restful")
public class RestController {

    @RequestMapping(value = "/user/{id}",method = RequestMethod.GET)
    public String getUser(@PathVariable("id") Integer id){
        System.out.printf("==> 查询%d号用户",id);
        return "success";
    }

    @RequestMapping(value = "/user/{id}",method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable("id") Integer id){
        System.out.printf("==> 删除%d号用户",id);
        return "success";
    }

    @RequestMapping(value = "/user/{id}",method = RequestMethod.PUT)
    public String updateUser(@PathVariable("id") Integer id){
        System.out.printf("==> 更新%d号用户",id);
        return "success";
    }

    @RequestMapping(value = "/user",method = RequestMethod.POST)
    public String addUser(){
        System.out.print("==> 添加1号用户");
        return "success";
    }
}
```

# 五、前端定义表单

```html
<%--
  Date: 2020/5/2
  Time: 19:50
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Title</title>
    </head>
    <body>
        <%--增删改查 超链接是get请求 --%>
        <a href="restful/user/1">GET 查询1号用户</a><br>

        <form action="restful/user/1" method="post">
            <%--传递一个参数_method ,值为请求类型--%>
            <input name="_method" value="delete">
            <input type="submit" value="DELETE 删除1号用户">
        </form>

        <form action="restful/user/1" method="post">
            <input name="_method" value="put">
            <input type="submit" value="PUT 更新1号用户">
        </form>

        <form action="restful/user" method="post">
            <input type="submit" value="POST 添加1号用户">
        </form>

    </body>
</html>
```







