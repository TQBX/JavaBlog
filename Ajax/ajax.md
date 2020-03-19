# Ajax是啥

Asynchronous JavaScript and XML（异步的 JavaScript 和 XML）。

是一门在**不重新加载整个页面**的情况下，与服务器交换数据并**更新部分网页的艺术**，也就是通过在后台与服务器进行少量的数据交换，实现网页的异步更新。

# 同步交互与异步交互

![image-20200319101323076](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200319101323076.png)

【同步】：客户端必须等待服务器端的响应。在等待的期间客户端不能做其他操作。

【异步】：客户端不需要等待服务器端的响应。在服务器处理请求的过程中，客户端可以进行其他的操作。

【异步Ajax可能引发的问题】

如果在发送Ajax请求以后，还需要继续处理服务器的响应结果，这时可能会导致：异步请求的响应还没有到达，处理的函数就已经执行，导致处理结果不成功。

# Ajax中XMLHttpRequest对象属性与方法

XMLHttpRequest对象是Ajax的核心，它的属性、方法、事件是http的请求与响应控制的关键。

【属性】：

readyState：表示xhr对象正处于什么状态，可以通过判断状态，完成对应的操作。

> readyState有以下五种状态
>
> - 0: 请求未初始化：此时已经创建了一个xhr对象，但还没初始化。
> - 1: 准备发送：此时已经调用了xhr的open方法，并且xhr对象已经准备将一个请求发送到服务器。
> - 2: 已发送：此时已经通过send方法将请求发到服务器端，知识还没收到响应。
> - 3: 正在接收：此时已经接收到http响应头，但是消息体部分还没完全接收。
> - 4: 完成响应，且响应已就绪，此时已经**完成了HttpResponse响应的接收**。

responseText：客户端接收到的http响应的文本内容，当readyState==4，此时内容才完整。

responseXML：当readyState为4且响应头Content-Type的MIME类型指定为XML才会被解析成XML文档。

status和statusText：HTTP的状态码和文本，两者只有readyState>=3时才可以访问，否则会抛出异常。

【事件】：

onreadystatechange：每当readyState值发生改变，就会触发一次该事件，通常用于触发回传处理函数。

【方法】：

open（method，URI，async，【username】，【password】）：打开连接，获取可以send的xhr对象。

- method：必需，指定发送http请求的方法，如"GET"、"POST"等。
- URI：指定xhr对象将请求发送到服务器响应的URI，该地址将会被自动解析成绝对地址。
- async：异步请求为true，同步为false，默认为true。
- username和password：需要服务器验证访问用户的情况可使用。

send（【data】）：在open之后可以将open设定的参数作为请求发送。

- 异步请求在send调用后，将会立即返回，而同步请求则会中断，直到请求返回。
- 参数data可选，send()和send(null)相同。对于POST方法，data表示将要传给服务器的数据。

setRequestHeader()：只能在open之后才能调用，用以设置请求头信息。

getResponseHeader()：只有readyState>=3之后才能调用，用于检索响应的头部值。

# js实现Ajax【GET】

1、获取XMLHttpRequest对象，和浏览器版本相关。

2、打开与服务器的连接，`xmlhttp.open("GET", "<%=request.getContextPath()%>/AjaxServlet?username="+username, true);`，这里提交方式为GET，URI中拼接参数，多个参数以&连接。

3、发送请求，`xmlhttp.send();`

4、注册监听，基于`onreadystatechange`事件，当请求被发送到服务器时，我们需要执行一些响应的任务。每当readyState（存储XMLHttpRequest对象的状态信息）改变，就会触发`onreadystatechange`事件。



```js
<%--
  Date: 2020/3/15
  Time: 19:25
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>$Title$</title>
        <script>
            var xmlhttp;
            function check() {
                //获取XMLHttpRequest对象
                if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
                    xmlhttp = new XMLHttpRequest();
                } else {// code for IE6, IE5
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                //注册监听
                xmlhttp.onreadystatechange = response;
                var username = document.getElementById("username").value;
                //打开与服务器的连接
                xmlhttp.open("GET", "<%=request.getContextPath()%>/AjaxServlet?username="+username, true);
                //发送请求
                xmlhttp.send();

            }
            function response() {
                //判断请求状态码是否是4，并且状态码为200，该状态表示响应已就绪
                if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                    //一旦响应，就把服务端返回的数据写入span
                    document.getElementById("username_msg").innerText = xmlhttp.responseText;
                }
            }
        </script>
    </head>
    <body>
        <form action="<%=request.getContextPath()%>/AjaxServlet" method="get">
            姓名：<input type="text" id="username" name="username" placeholder="请输入姓名">
            <input type="button" onclick="check()" value="判断用户名是否存在">
            <span id="username_msg"></span>
        </form>
    </body>
</html>

```

```java
package com.my.Ajax;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther Summerday
 */
@WebServlet("/AjaxServlet")
public class AjaxServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //解决请求响应乱码问题
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        List<String> userNames = new ArrayList<>();
        userNames.add("天乔巴夏");
        userNames.add("summerday");
		//获取参数
        String username = request.getParameter("username");
        //创建响应对应的输出流
        PrintWriter writer = response.getWriter();
        if (userNames.contains(username)){
            writer.write("用户名已经存在");
        }else {
            writer.write("用户名可使用");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
```



# js实现Ajax【POST】

思路不变，区别于GET方式，有以下不同：

- open方法中的提交方式改为POST。

- 增加了`xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");`。(必须在open之后，否则会报错)。
- 发送请求： `xmlhttp.send("username="+username);`，携带参数，如果多个参数，用&分隔。

- 处理响应的方式与GET类似。

```js
            var xmlhttp;
            function check() {
                //获取XMLHttpRequest对象
                if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
                    xmlhttp = new XMLHttpRequest();
                } else {// code for IE6, IE5
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }

                //注册监听
                xmlhttp.onreadystatechange = response;
                var username = document.getElementById("username").value;
                //打开与服务器的连接
                xmlhttp.open("POST", "<%=request.getContextPath()%>/AjaxServlet?t="+new Date().getTime(), true);
                //必须在连接之后发送请求头
                xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                //发送请求
                xmlhttp.send("username="+username);

            }
```

可以在URI后增加时间戳，对付缓存。

