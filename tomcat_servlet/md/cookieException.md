【已解决】：java.lang.IllegalArgumentException: An invalid character [32] was present in the Cookie value

# 报错如下：

![8rxqv8.png](https://s1.ax1x.com/2020/03/19/8rxqv8.png)

# 报错代码

```java
        //解决乱码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        //产生时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        String s = sdf.format(date);


        //创建cookie
        Cookie cookie = new Cookie("loginTime",s);
        //向浏览器发送cookie
        response.addCookie(cookie);
```

# 解决办法

当然看到这个报错还是比较明显的，是因为在产生时间字符串的时候**出现了空格**（whitespace），在创建cookie对象的时候是不允许的。

在给客户端发送cookie的时候，将value进行"utf-8"编码，在获取客户端请求中的cookie时进行响应的"utf-8"解码，即可。

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //解决乱码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        //产生时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        String s = sdf.format(date);
        //使用utf-8编码
        s = URLEncoder.encode(s, "utf-8");

        //创建cookie
        Cookie cookie = new Cookie("loginTime",s);
        //向浏览器发送cookie
        response.addCookie(cookie);

        //将全部cookie转化为Cookie对象存入数组中，每个cookie存name:value键值对
        Cookie[] cookies = request.getCookies();
        Cookie loginTime = null;
        if(cookies!=null) {
            for (Cookie c : cookies) {
                if ("loginTime".equals(c.getName())) {
                    String value = c.getValue();
                    //使用utf-8解码
                    value = URLDecoder.decode(value,"utf-8");
                    //设置解码后的值
                    c.setValue(value);
                    loginTime = c;
                }
            }
        }
        if (loginTime!=null){
            response.getWriter().write("上次访问的时间："+loginTime.getValue());
        }else {
            response.getWriter().write("首次登录");
        }
    }
```

参考：[javax.servlet.http.Cookie](https://docs.oracle.com/cd/E17802_01/products/products/servlet/2.1/api/javax.servlet.http.Cookie.html)