[toc]

# 一、配置支持Restful过滤器

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

# 二、定义控制器

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

# 三、前端定义表单

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

# 四、部分源码分析

```java
	//HiddenHttpMethodFilter
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



