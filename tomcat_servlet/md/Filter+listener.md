# Filter过滤器

>  与Servlet一样，Filter也是一门web开发中的三大核心技术之一。

见名知义，过滤器的功能其实很好理解：我允许你通过，你就能通过，不允许你通过，想通过门都没有，当然想通过还是有办法的，但是决定权在过滤器，说不定过滤器把你加工一下，你就能达到通过的标准了。

欸，差不多就这个意思。我们之前在编写Servlet的时候，一遇到中文字符的请求或者响应都忘不了在一开始就加上下面两句话保证在处理请求，发出响应的过程中编解码统一，对吧：

```java
//请求乱码处理
request.setCharacterEncoding("utf-8");
//响应乱码处理
response.setContentType("text/html;charset=utf-8");
```

试想，如果有好多好多的Servlet都需要这样处理，是不是会比较麻烦呢，既然都需要，我完全可以在他们进入Servlet之前对请求进行一波加工，出来之后对响应也进行一波加工，会方便许多。

```java
    //可提取为初始化参数，这里默认utf-8了
	private String encode = "utf-8";
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        //全局响应乱码解决
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        request.setCharacterEncoding(encode);
        response.setContentType("text/html;charset="+encode);
        chain.doFilter(request, response);
    }
```

这时候，就可以引出web中的过滤器了：一旦对方访问的资源路径正好和url-pattern配置的拦截路径匹配，就**将其request对象拦截**，拦截之后可以选择放行，也可以选择增加一些小操作，通过过滤器，我们就能完成许多通用的操作，如登录验证、统一编码处理等。

![image-20200322204648281](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200322204648281.png)

# Filter接口

所有的自定义都必须实现`javax.servlet.Filter`接口，下面是接口中定义的三个方法。

```java
public interface Filter {
	//web应用加载进容器，Filter对象创建之后，执行init方法初始化，用于加载资源，只执行一次。
    void init(FilterConfig var1) throws ServletException;
    //每次请求或响应被拦截时执行，可执行多次。
    void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException;
    //web应用移除容器，服务器被正常关闭，则执行destroy方法，用于释放资源，只执行一次。
    void destroy();
}
```

## 参数详解

`FilterConfig var1`：代表当前Filter在web.xml中的配置信息对象。

`ServletRequest var1`：拦截的请求对象。

`ServletResponse var2`：拦截的响应对象。

`FilterChain var3`：过滤器链，提供`doFilter()`方法，放行过滤器。

# 定义Filter

【方式一】web.xml配置

1. 创建一个Filter类，实现`javax.servlet.Filter`接口。

```java
public class FilterDemo1 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("FilterDemo1.init");
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("FilterDemo1.doFilter");
    }
    @Override
    public void destroy() {
        System.out.println("FilterDemo1.destroy");
    }
}
```

2. 在web.xml中配置。

```xml
    <filter>
        <filter-name>Filter1</filter-name>
        <filter-class>com.my.filter.FilterDemo1</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>Filter1</filter-name>
        <url-pattern>/*</url-pattern><!--拦截路径/*表示拦截所有资源-->
    </filter-mapping>
```

【方式二】注解配置

```java
@WebFilter("/*")
public class FilterDemo1 implements Filter
```

# 生命周期

web应用被加载到容器中时，**过滤器对象被创建，并执行init方法初始化**。

过滤器对象创建之后，一直存在于内存中，**每拦截一次请求或响应时都会执行doFilter方法**。

执行doFilter方法之后，可以选择对处理结果放行，处理逻辑依据具体情况。

直到web应用被移除容器，F**ilter对象才会销毁，在销毁之前会执行destroy方法**。

# 配置细节

【拦截路径配置】：

1. 具体资源路径：`/index.jsp`  只拦截`index.jsp`。
2. 拦截目录： `/demo/*`    拦截`/demo`目录下的所有资源。
3. 后缀名拦截：`*.jsp`  拦截所有后缀名为`.jsp`的资源。
4. 拦截所有资源：`/*`  拦截所有资源。

【拦截方式配置】：

注解设置`dispatcherTypes`属性，如：`@WebFilter(value = "/FilterDemo4",dispatcherTypes = DispatcherType.REQUEST)`。

- REQUEST：浏览器直接请求资源（默认）
- FORWARD：转发访问资源
- INCLUDE：包含访问资源
- ERROR：错误跳转资源
- ASYNC：异步访问资源

web.xml配置

- 设置`<dispatcher></dispatcher>`标签，如：`<dispatcher>REQUEST</dispatcher>`。

# 过滤器链

## 执行流程

![image-20200322205658890](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200322205658890.png)

## 执行顺序

【注解配置】

过滤器先后顺序，按照类名的字符串比较规则比较，值小的先执行，Filter1在Filter2之前执行。

【web.xml】配置

过滤器先后顺序由`<filter-mapping>`的配置顺序决定，先配置的先拦截。



