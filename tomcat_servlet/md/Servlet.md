# 什么是Servlet

Servlet是Sun公司提供的动态资源开发的技术，其本质是一个java文件，也就是说需要编译运行的过程。

与普通的java文件不同的是，需要将编译后的.class文件放入Servlet容器之中，而tomcat就为之提供了存储并运行servlet的环境。



servlet容器：存储并运行servlet的环境。

web容器：存储并运行web资源的环境。



# Servlet的作用

**处理浏览器带来HTTP请求，并返回一个响应给浏览器，从而实现浏览器和服务器的交互**。



# IDEA编写Servlet

## 方式一

创建项目或者模块，选择Java Enterprise，选择Web Application，方式一是勾选Create web.xml。

![image-20200314170258869](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200314170258869.png)

创建完毕之后，会发现项目已经生成了一个

![image-20200314170449485](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200314170449485.png)



在src目录下创建com.my servlet.FirstServlet.java，实现Servlet接口，重写抽象方法。可能会产生找不到javax.servlet：参照：

![image-20200314171954247](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200314171954247.png)

暂时不管其他的，先在service方法中，向页面输出一点东西：

```java
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.getWriter().write("this is my Servlet");
    }
```

然后再WEB-INF目录下的web.xml文件中配置如下信息，注意两个`<servlet-name></servlet-name>`标签体的内容需要相同，保证映射的关系。

```xml
    <servlet>
        <servlet-name>demo</servlet-name>
        <servlet-class>com.my.servlet.FirstServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>demo</servlet-name>
        <url-pattern>/first</url-pattern>
    </servlet-mapping>
```



启动tomcat服务器，在浏览器中输入`http://localhost/first`，即可访问FirstServlet。（当然，我已经把Tomcat的虚拟路径改为`/`，端口号改为80。

![image-20200314172841063](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200314172841063.png)

# Servlet细节补充

