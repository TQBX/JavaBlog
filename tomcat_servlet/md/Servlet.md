# 什么是Servlet

---

“**Servlet** 是运行在**Web服务器**的Java小程序。Servlet可以获取并针对Web客户端的**请求作出响应**。一般情况下，通过**HTTP**，即超文本传输协议，进行传输通信。”

```
A servlet is a small Java program that runs within a Web server. Servlets receive and respond to requests from Web clients, usually across HTTP, the HyperText Transfer Protocol.
```

参考自：[Servlet必会必知](https://my.oschina.net/jeffli1993/blog/495336)

Servlet是Sun公司提供的动态资源开发的技术，其本质是一个java文件，也就是说需要编译运行的过程。

与普通的java文件不同的是，需要将编译后的.class文件放入Servlet容器之中，而tomcat就为之提供了存储并运行servlet的环境。

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

## 方式二

在创建项目的时候选择不勾选Create web.xml，利用注解的方式配置路径映射。

![version](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\version.png)

创建之后，会发现web目录中没有了web.xml，创建一个servlet，会发现类的左上方出现了注解，`@WebServlet(name = "SecondServlet")`,但是我们需要的是urlPatterns，我们需要改成如下模样：

![image-20200315192917295](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200315192917295.png)

原理还是和方式一样的，只不过注解的存在，不需要再一一地在web.xml中配置servlet-mapping了。

![image-20200315193218183](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200315193218183.png)

# Servlet接口方法及生命周期

```java
public interface Servlet {
    //在Servlet被创建时执行，且一个Servlet在内存中只有一个对象（单例）
    void init(ServletConfig var1) throws ServletException;
	//配置对象
    ServletConfig getServletConfig();
	//提供服务的方法，每一次Servlet被访问时执行，执行多次
    void service(ServletRequest var1, ServletResponse var2) throws ServletException, IOException;
	//获取Servlet的一些信息，版本，作者等等
    String getServletInfo();
	//在服务器正常关闭时执行，执行一次。
    void destroy();
}
```

Web服务器收到客户端的Servlet访问请求之后，调用Servlet程序。

>  Servlet是一个供其他Java程序（Servlet引擎）调用的Java类，它不能独立运行，它的运行完全由Servlet引擎来控制和调度。

【加载】Servlet第一次被访问的时候，将会创建出一个Servlet对象。创建出来的对象会一直保存在内存中，以便之后重复访问创建的Servlet。

【初始化】在创建Servlet对象之后立刻调用`init()`方法进行初始化操作。

【处理服务】每次对Servlet的访问都会调用Servlet的`service()`方法。

【销毁】在web应用被移除容器或者服务器关闭时，将会调用Servlet的`destroy()`方法，servlet对象随之销毁。

# Servlet继承结构

Servlet：定义了servlet都具有的方法，所有的Servlet都需要直接或间接实现这个接口。

GenericServlet：抽象类，对Servlet接口的大部分方法提供了实现，只有service()方法需要下一个勇士去定义。

HttpServlet：继承了GenericServlet类，是一个对HTTP协议进行了优化的Servlet，查看源码，可以发现它实现了service抽象方法，将request和response对象转化成HttpServletRequest和HttpServletResponse对象，并调用`protected void service(HttpServletRequest req, HttpServletResponse resp)`方法。该方法根据请求方式的不同，分别调用doXxx方法。

![image-20200315201045396](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200315201045396.png)

doXxx方法，主要用的还是DoGet()和DoPost()方法。

![do](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\do.png)

通常使用的HttpServlet比较多，包括IDEA自动创建Servlet的默认模板就是它。

> “HttpServlet 提供了一个能被继承后创建一个适应Web网站的**Http Servlet**的抽象类。”

# Servlet的线程安全问题

针对客户端的多次Servlet请求，通常情况下，服务器**只会创建一个Servlet实例对象**，也就是说Servlet实例对象一旦创建，它就会驻留在内存中，为后续的其它请求服务，直至web容器退出，servlet实例对象才会销毁。

当多个客户端并发访问同一个Servlet时，Web服务器会为每一个客户端的访问请求创建一个线程，并在这个线程上调用Servlet的service()方法，**如果service()方法内如果访问了统一资源，将会造成线程安全问题**。

