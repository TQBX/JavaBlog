# 【解决】：Resource interpreted as Stylesheet but transferred with MIME type text/html

参考了许多博客，最终找到了问题，知道问题产生的原因也很简单，因为css的链接是可以正常访问的，于是判定链接方面是没有问题的。

那么剩下的，也就是可能是解析除了错误，果然，查看了图片，css等信息的响应，发现响应体的类型都是`text/html`，便很快定位了问题。

[前端踩坑Resource interpreted as Stylesheet but transferred with MIME type text/html](https://blog.csdn.net/sky_cui/article/details/86703706?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase)

[部署到Tomcat上CSS加载不出来的问题](https://blog.csdn.net/weixin_41894196/article/details/105451482?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2)

出错原因：

```java
@WebFilter("/*")
public class CharacterFilter implements Filter {
    /**
     * request请求编码
     */
    private static final String REQUEST_TYPE = "utf-8";

    /**
     * response响应乱码
     */
    private static final String RESPONSE_TYPE = "text/html;charset=utf-8";

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //获取请求方法
        String method = request.getMethod();

        //解决post请求中文数据乱码问题
        String post = "post";
        if (post.equalsIgnoreCase(method)) {
            request.setCharacterEncoding(REQUEST_TYPE);
        }
        //处理响应乱码
        response.setContentType(RESPONSE_TYPE);
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

}
```

之前项目中的全局过滤器将响应的格式都返回成了`"text/html;charset=utf-8"`，导致响应的解析失败。

我们只需要排除一下情况即可：

```java
String uri = request.getRequestURI();
if(uri.contains(".css")||uri.contains(".js")||uri.contains(".png")){
    
}else {
    //处理响应乱码
    response.setContentType(RESPONSE_TYPE);
}
chain.doFilter(request, response);
```



