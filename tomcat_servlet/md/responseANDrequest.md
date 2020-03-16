回顾一下Servlet的调用过程，我们创建一个MyServlet类，并且定义了相关方法处理最初来自客户端浏览器的http请求，在方法中将"hello"写入response中，代码如下：

```java
@WebServlet("/myServlet")
public class MyServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("hello");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
```

最终我们打开浏览器，输入http://localhost:8080/demo/myServlet就可以在页面上看到可爱的hello。

![image-20200315214339931](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200315214339931.png)

事实上，当我们输入网址，到看到页面的hello，短短的时间内，浏览器，服务器以及Servlet三者之间的交互如下

![image-20200315214703875](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200315214703875.png)

这张图网上有，因为字体太小了，我就想把里面的字画大个一些，并结合这个例子分析一下。

我们之前分析过：服务器生成req和res对象，调用HttpServlet的service方法时，将会转化成HttpServletRequest和HttpServletResponse对象，处理数据之后，response对象返回给服务器，服务器将response对象作为响应返回给浏览器。

---

因此，request和response是啥，也就一目了然了，一个代表请求，一个代表响应，他们与HTTP协议都有着密不可分的协议。当然这两个对象也会有相关的方法，去设置响应的内容，或者获取请求的内容。

# request

## 继承结构

ServletRequest：request顶级接口，定义了request应该具有的基本方法。

HttpServletRequest：继承于ServletRequest的接口，增加了关于http协议相关的API。

## 常用方法

浏览器输入：`http://localhost/demo/requestDemo1?username=summerday&gender=male`

### 一、和客户端相关的信息

```java
/**
 * @auther Summerday
 *
 * 请求客户端参数API
 */
@WebServlet("/requestDemo1")
public class RequestDemo1 extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取客户端发出请求的完整URL:http://localhost/demo/requestDemo1
        StringBuffer requestURL = request.getRequestURL();
        System.out.println("请求完整URL："+requestURL.toString());

        //获取资源名URI :/demo/requestDemo1
        String requestURI = request.getRequestURI();
        System.out.println("请求资源URI："+requestURI);

        //返回请求行中的参数部分：username=summerday&gender=male
        String queryString = request.getQueryString();
        System.out.println("请求参数："+queryString);

        //返回发出请求的客户机IP地址：ipv6地址 0:0:0:0:0:0:0:1
        String remoteAddr = request.getRemoteAddr();
        System.out.println("客户机IP："+remoteAddr);

        //获取客户端的请求方式：GET
        String method = request.getMethod();
        System.out.println("请求方式："+method);

        //获取当前web应用虚拟目录的名称：/demo
        String contextPath = request.getContextPath();
        System.out.println("虚拟目录："+contextPath);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
```

### 二、获取请求头信息

```java
/**
 * @auther Summerday
 *
 * 获取请求头信息
 */
@WebServlet("/RequestDemo2")
public class RequestDemo2 extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //getHeader(name) 根据传入的name获取对应的值
        String header = request.getHeader("Host");
        System.out.println("Host的值为："+header);

        //getHerders(String name) 根据传入的name，获取所有的值
        Enumeration<String> hosts = request.getHeaders("Host");
        while (hosts.hasMoreElements()){
            String s = hosts.nextElement();
            System.out.println("host:"+s);
        }
        //getHeaderNames() --- Enumeration<String> 获取所有HeaderNames
        Enumeration<String> headerNames = request.getHeaderNames();

        while(headerNames.hasMoreElements()){
            String s = headerNames.nextElement();
            System.out.println("headerNames:"+s);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
```

### 三、获取请求参数







# response

