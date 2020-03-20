# Session是啥

session与cookie一样都是会话跟踪技术，但与cookie技术不同的是，session技术是一门服务器端的技术。

相当于在服务器端建立了用户的档案库，记录用户的信息，而这部分数据存储在服务器中，普通用户不可见，安全性较高。

但是如果数据量较大，会对服务器造成巨大压力，建议存储要求安全的少量数据，通常用于保存一些用户的登录状态信息。

# Session原理

在客户端访问服务器的时候，会在服务器内部创建一个与当前客户端相关的session对象（不同客户端浏览器会拥有各自的session对象，彼此间互不影响），自此会话建立。

如果客户端浏览器没有完全关闭，就会保持这次会话，会话期间如果用户多次请求服务器，都是操作的同一个session对象。

当然，如果浏览器完全关闭，session对象也就销毁了，代表会话结束。

![image-20200320162652422](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200320162652422.png)

# session与cookie配合使用

> cookie和session配合起来，能够保持用户与服务器的交互状态。

上面所说session的原理中，其实并没有说全。**HTTP协议本身是无状态的协议**，无法对之前发生过的请求和响应的状态进行管理：

> 在当前会话内，他怎么保证一个客户端浏览器对应一个session对象呢？

在当前会话内，服务器将会为每个用户都生成各自不同的一个**临时Cookie**，名为`JSSESIONID`，值作为**区别不同用户的ID**。接下来同一用户的请求中就会附带这个cookie，而服务器只需要检验请求中Cookie的JSSSIONID是不是同一个就完事，这样就确保浏览器于服务器的交互。

>  会话关闭，用户想继续使用上一次的session，可以实现么？

提到Cookie，我们联想之前删除Cookie的狸猫换太子的方式：

就是创建一个名称相同，MaxAge设置为0的一个cookie发送给浏览器，将无效cookie覆盖原有cookie完成cookie的删除。

同理，服务器一开始生成的JSESSIONID是临时的，意味着是**会话级别**，也就是浏览器关闭，会话关闭，这个cookie就会消失。

要想让用户关闭浏览器之后，下一次还保留这个cookie，对，和之前的思路一样，完全可以创建一个同样名为JSESSIONID的cookie，但是毕竟这次的id表示用户身份，不能随便乱取，我们一定还是需要最开始的那个id值，可以利用`session.getId()`获取。

接着就是设置存活时间，进而发送个浏览器，达到下一次会话，用户还是能够使用上一次的信息的效果。

```java
//创建一个名为JSESSIONID的cookie,将session保存在本地,使浏览器多次关闭打开都获取同一个session
Cookie cookie = new Cookie("JSESSIONID",session.getId());
//设置cookie存活时间
cookie.setMaxAge(60*60*24);
cookie.setPath(request.getContextPath()+"/");
response.addCookie(cookie);
```

# Session域对象

实现**当前会话范围**内的资源共享。

## 获取方式

```java
//如果服务器中存在当前request关联的对话HttpSession，则获取，如果没有则创建
request.getSession();
//create为true，效果和空参一样。
//create为false，如果服务器中存在当前request关联的对话HttpSession，则获取，如果则返回null
request.getSession(boolean create);
```

## 域对象对应API

既然是域对象，少不了的是关于Attribute的方法：

```java
//将验证码写入会话
session.setAttribute("code",code);
//获取会话中的验证码
String code = (String) session.getAttribute("code");
//将验证码从会话中移除
session.removeAttribute("code");
//获取所有的键
Enumeration<String> names = session.getAttributeNames();
```

## 生命周期

【创建】

第一次调用request.getSession()，Session对象被创建。

【销毁】

情况一：在默认情况下，session对象将会在服务器中存在30分钟，超过30分钟就销毁，与web.xml中的配置有关。

![image-20200320160248839](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200320160248839.png)

情况二：调用session.invalidate()方法，主动销毁session对象。

情况三：与服务器相关，若服务器正常关闭，session对象将被序列化【钝化】，服务器再次启动，将会读取该文件【活化】。若服务器非正常关闭，则session对象直接销毁。

# 三个域对象的作用范围

request作用于当前请求链范围，下一次请求失效，范围最小。

session作用于当前会话，浏览器关闭作用失效。

ServletContext作用于整个web应用范围内，web应用销毁作用失效，范围最大。

# Session案例之验证码

【Validate Servlet】

```java
/**
 * @auther Summerday
 *
 * 提供验证码响应
 */
@WebServlet("/validateServlet")
public class ValidateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        //禁止图像缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", -1);
        //创建ValidateCode对象
        ValidateCode vCode = new ValidateCode(80,30,4,10);
        //获取验证码字符串
        String code = vCode.getCode();

        HttpSession session = request.getSession();
        //写入会话
        session.setAttribute("code",code);
        //将图片写入流
        vCode.write(response.getOutputStream());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
```

【RegistServlet】

```java
        //获取session中的验证码
        HttpSession session = request.getSession();
        String code = (String) session.getAttribute("code");
        session.removeAttribute("code");

        if(!StringUtils.equalsIgnoreCase(code,valistr)){
            //注册页面提示，用户名称不能为空
            request.setAttribute("validate_msg", "验证码错误");
            //请求转发到注册页面，可以共享数据
            request.getRequestDispatcher("/regist.jsp").forward(request, response);

            //打断下方代码
            return;
        }
```

