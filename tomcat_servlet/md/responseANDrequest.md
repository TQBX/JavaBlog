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

因此，request和response是啥，也就一目了然了，一个代表请求，一个代表响应，他们与HTTP协议都有着密不可分的协议。

# request

## 继承结构

ServletRequest：request顶级接口，定义了request应该具有的基本方法。

HttpServletRequest：继承于ServletRequest的接口，增加了关于http协议相关的方法。

## 常用方法

```java

```











# response

