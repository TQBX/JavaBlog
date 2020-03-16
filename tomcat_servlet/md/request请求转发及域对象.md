# 什么是请求转发

请求转发实现了**服务器内部的资源跳转**。

# 请求转发的特点

一次请求，一次响应。一个请求对象必然对应一个响应对象。

**Web应用内部（服务器内部）的资源跳转**，浏览器感受不到变化，**地址栏不会发生变化**。

# 请求转发实现服务器内部资源跳转

在demo4中

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取调度器,利用调度器实现转发
        request.getRequestDispatcher("/RequestDemo5").forward(request,response);
    }
```

在demo5中

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("here is RequestDemo5!");
    }
```

浏览器中输入：`http://localhost/demo/RequestDemo4`。

![转发](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\转发.png)

> 既然是服务器内部，那么path路径直接写`/资源名`即可。

# 请求转发与数据冲刷

>  ok，请求转发还是比较清晰的，现在有个新需求，如果我想现在demo4页面上显示一段数据，转发到demo5，再显示另外一段数据，该如何实现呢？

如果此时在demo4中先写入数据，再转发会发生什么现象呢？

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //再转发之前写入cache
        response.getWriter().write("cache");
        //获取调度器,利用调度器实现转发
        request.getRequestDispatcher("/RequestDemo5").forward(request,response);
    }
```

demo5不变，再次访问`http://localhost/demo/RequestDemo4`，会看到以下结果，并不能看到cache。

![huanchong](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\huanchong.png)

这是因为，如果在请求转发之前response缓冲区中还存在数据，请求转发时将会将缓冲区清空。

那如果我写入之后，直接调用`response.flushBuffer();`将数据先冲刷到浏览器上显示，而后再转发是不是就解决问题了呢？试试修改demo4，demo5不变。

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //再转发之前写入cache
        response.getWriter().write("cache");

        //调用flushBuffer方法冲刷缓冲区
        response.flushBuffer();
        //获取调度器,利用调度器实现转发
        request.getRequestDispatcher("/RequestDemo5").forward(request,response);
    }
```

再次访问，出现了以下状况：

![flush](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\flush.png)

调用flushBuffer()方法冲刷response缓冲区会造成一次响应操作，又因为请求转发模型中一次请求将会对应一次响应，导致请求转发的响应就无法实现。同理，如果再请求转发之后再冲刷，会导致冲刷操作无法执行。



# 请求转发语句前后正常执行

demo4:

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //再转发之前写入cache
        response.getWriter().write("cache");

        System.out.println("demo4 start");
        //获取调度器,利用调度器实现转发
        request.getRequestDispatcher("/RequestDemo5").forward(request,response);

        System.out.println("demo4 end");
        //调用flushBuffer方法冲刷缓冲区
        response.flushBuffer();
    }
```

demo5:

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("demo5 start");
        response.getWriter().write("here is RequestDemo5!");
        System.out.println("demo5 end");
    }
```

当访问demo4时，控制台将会输出以下结果：

```java
demo4 start
demo5 start
demo5 end
demo4 end
```

可见，转发前后语句正常执行，且按照一定顺序执行。

# 请求转发可以多重转发

在demo5中转发到demo6，前面代码不变：

```java
request.getRequestDispatcher("/RequestDemo6").forward(request,response);
```

demo6中：

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("demo6 start");
        response.getWriter().write("here is demo6");
        System.out.println("demo6 end");
    }
```

最后访问demo4，结果如下：

![多重转发](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\多重转发.png)

控制台输出：

```java
demo4 start
demo5 start
demo6 start
demo6 end
demo5 end
demo4 end
```

![image-20200316194930907](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200316194930907.png)

# 请求包含

突然想到刚才的需求，两个页面的内容都要显示，咋办呢？请求包含(include)！

demo4中

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //再转发之前写入demo4
        response.getWriter().write("demo4 ");
        //获取调度器,利用调度器实现转发
        request.getRequestDispatcher("/RequestDemo5").include(request,response);
    }
```

demo5中

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("demo5");
    }
```

此时需求即完成。

![请求包含](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\请求包含.png)



# 域对象是啥

在一个域内，实现数据共享的对象，如上request对象在服务器接收请求时创建，在服务器内部多次转发，**在同一条请求链上，多个Servlet资源共享request**，request就是一个域对象。

本质上，数据共享由对象map的属性名和属性值实现，因此我们可以看到需要关于Attribute的方法。

# 域对象相关方法

实现：在demo7中设置属性及属性值，在demo8中获取值。

```java
/**
 * @auther Summerday
 *
 * request域对象
 */
@WebServlet("/RequestDemo7")
public class RequestDemo7 extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //向域中添加数据
        request.setAttribute("message","hello,i'm demo7");
        request.setAttribute("name","summerday");
        request.setAttribute("age",18);
        //转发
        request.getRequestDispatcher("/index.jsp").forward(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
```

```java
/**
 * @auther Summerday
 */
@WebServlet("/RequestDemo8")
public class RequestDemo8 extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取全部域属性的名称
        Enumeration<String> names = request.getAttributeNames();
        while(names.hasMoreElements()){
            String s = names.nextElement();
            //获取指定名称的域属性
            String value = (String) request.getAttribute(s);
            System.out.println(s+">>"+value);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
```

![域属性](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\域属性.png)

# 域对象的生命周期

一次请求开始时，request对象将会被创建，在请求链（可能包含多次转发）结束的时候，request对象将销毁，而这个请求链的范围就是request域对象的作用范围。



