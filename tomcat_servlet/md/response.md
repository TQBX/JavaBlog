# response继承结构

ServletResponse：response顶级接口，定义了response对象应有的功能。

HttpServletResponse：继承了ServletResponse接口，**增加了Http协议相关的API**，利于Http相关开发。

# HTTP响应组成

一个状态行、若干响应头、一个空行、响应实体

参考：[HTTP协议学习笔记](https://blog.csdn.net/Sky_QiaoBa_Sum/article/details/104861350)

# response相关API

## 1、设置状态码

> - void setStatus(int var1);

## 2、设置响应头

> - void setHeader(String var1, String var2);

## 3、设置响应内容

> - ServletOutputStream getOutputStream() throws IOException;
>
> - PrintWriter getWriter() throws IOException;

# response乱码处理

还是那句话，乱码就是双方编解码方式不一致造成的。

解决办法：

```java
//针对字节流
response.setContentType("text/html;charset=utf-8");
response.getOutputStream().write("你好也".getBytes("utf-8"));

//针对字符流
//response.setCharacterEncoding("utf-8");通知服务器，可省略
response.setContentType("text/html;charset=utf-8");
response.getWriter().write("你好");
```

综上所述，只要响应界面出现乱码，只需要通知浏览器使用对应的字符集就可以。

ps：

- `getOutputStream()`和`getWriter() `这两个方法，在一次请求当中调用了其中的一个, 就不能再调用另一个，否则你懂的。
- 浏览器会自动关闭流，不需要手动关闭。

- `response.setContentType("text/html;charset=utf-8");`需要写在前面。

# 请求重定向是啥

它也是一种**资源跳转**的方式，但是与转发不同的是，它可以在不同的服务器之间跳转。

# 请求重定向的特点

两次请求，两次响应，产生两次请求对象，对应两个响应对象。

服务器之间的跳转方式，但浏览器会感应到，**地址栏将会变化**。

## 重定向实现一

先来跳转一个demo3看看效果：

```java
//设置302状态码
response.setStatus(302);
//设置location响应头
response.setHeader("location",request.getContextPath()+"/ResponseDemo3");
```

> 这时不在服务器内部进行跳转了，因此需要加上**虚拟路径**。（没有写虚拟主机名的情况下，将会自动拼接上当前的虚拟主机。

当我们输入`http://localhost/demo/ResponseDemo2`时，页面发生了跳转，地址栏也发生了改变。

![重定向](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\重定向.png)

## 重定向实现二

利用sendRedirect方法，效果像当于既设置了302状态码，又设置了location的值：

```java
//设置302状态码+location
response.sendRedirect(request.getContextPath()+"/ResponseDemo3");
```

![image-20200316221856441](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200316221856441.png)

# response定时刷新

功能和特点类似于重定向，主要区别在于【定时】两个字，可以设置refresh响应头，完成定时刷新。

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write("3秒钟之后跳转到demo3");
        //设置响应头
        response.setHeader("refresh","3;url=http://localhost/demo/ResponseDemo3");
    }
```

3秒之后，从demo4跳转至demo3，地址栏将会发生改变。

# response控制浏览器缓存

```java
//告诉浏览器使用缓存 60s
response.setHeader("Cache-control","max-age=60");
response.setDateHeader("Expires", System.currentTimeMillis()+1000*60);
Date date = new Date();

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
String s = sdf.format(date);
response.getWriter().write(s);
```

缓存之后，60s之内，不断请求资源都将会从缓存中获取，看到的现象为时间不变。

```java
//告诉浏览器不要缓存
response.setDateHeader("Expires",-1);
response.setHeader("Cache-control", "no-cache");
response.setHeader("Pragma", "no-cache");
```



