# 会话是啥

举个例子：客户端和服务器之间可能会产生多次的请求和响应，从客户端开始向服务器之间发生请求，中间不管多少次请求响应，直到最后一次客户端访问服务器结束，客户端关闭。这一整个的过程就是**一次会话**。

也就是说，会话将两者之间发出的不同请求联系了起来。



# Cookie为什么出现？

我们知道，HTTP本身是**无状态协议**，不对之前发生过的请求和响应的状态进行管理。

无状态其实是有利有弊的：

- 利：不必保存状态可以减少服务器的CPU及内存资源的消耗。
- 弊：某些情况下，用户频繁发出请求，没有状态管理确实也很难受。

如何去解决呢？让服务器去管理状态属实不太现实，毕竟负担太大。但有时后真就出现用户频繁登录频繁访问的情况，这可如何是好？那只能把艰巨的任务交给客户端了，于是，Cookie应运而生。

# Cookie技术如何实现

>  Cookie 实际上是存储在**客户端**上的文本信息，保留了各种跟踪的信息，用户机制就是用户识别和状态管理。

Cookie技术通过在请求和响应报文中写入Cookie信息来控制客户端的状态：

1. 客户端向服务器发出请求，如果服务器需要记录用户的状态，就在响应中设置Set-Cookie的首部字段信息，通知客户端保存Cookie。
2. 客户端收到响应之后，如果Cookie有效，则将Cookie进行持久化，即写入对应的Cookie文件中存储。
3. 浏览器再次访问服务器时，将会自动在请求报文中加入Cookie，告诉服务器，这次我带来了上次你给我发的Cookie。
4. 服务器收到请求之后，会检查一下这个Cookie，获悉该请求从哪个客户端而来，对比服务器上的记录，得到之前的状态信息。

> - Cookie是一门客户端技术，每个客户端将各自保存各自的数据。
>
> - Cookie功能需要浏览器的支持，如果浏览器不支持Cookie或者Cookie禁用了，Cookie功能就会失效。

![image-20200319220318999](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200319220318999.png)



# Cookie相关的首部字段

Set-Cookie：是开始状态管理所使用的响应首部字段。

Cookie：服务器接收到的Cookie信息，也是发往服务器的请求首部字段。

## Set-Cookie字段的主要属性

NAME=VALUE：Cookie的名称和值，必需。

expires=Date：Cookie的有效期，默认为浏览器关闭为止。

path=PATH：将服务器上的该目录作为Cookie的适用对象，默认为文档所在文件目录。

domain=域名：Cookie对象的适用域名，默认为创建Cookie的服务器域名。

Secure：仅在HTTPS安全通信时才发送Cookie。

HttpOnly：使Cookie不能被Js脚本访问。

有时为了解决xxx等问题，可以利用会话技术解决。

# Cookie类的使用

SUN公司将Cookie操作相关的API封装成了Cookie类，我们可以灵活利用Cookie类完成会话跟踪。

## 1、创建cookie

```java
Cookie cookie = new Cookie("loginTime",s);
```

注意name和value的值都必须合理，否则需要进行编码。

> 使用初始名称/值对定义cookie。名称不能包含空格、逗号或分号，只能包含ASCII字母数字字符。

## 2、发送到浏览器

```java
response.addCookie(cookie);
```

可以在一次响应中添加多个Cookie。

## 3、设置最大生命时长

```java
//设置最大生命时长,以秒为单位，这里设置为一天
cookie.setMaxAge(60*60*24);
```

如果不设置最大生命时长，则**默认到浏览器关闭为止**。

- 如果值为正数，则浏览器将会对Cookie持久化，时间取决于值的秒数，时间到后自动清除cookie文件。
- 值为0，表示立即删除cookie。
- 值为负数，表示该cookie为临时cookie，并不会被持久化，窗口关闭即失效。

## 4、设置有效路径

```java
//将有效路径设置为web应用的虚拟路径
cookie.setPath(request.getContextPath()+"/");
```

与path属性相关，用于限制指定cookie的发送范围的文件目录。

如果不指定，默认的path就是发送Cookie的Servlet的所在路径。

## 5、获取cookie

```java
Cookie[] cookies = request.getCookies();
```

返回请求中所有Cookie对象组成的数组，如果没有Cookie，则返回null。

## 6、杀死cookie

```java
/*杀死cookie*/
//创建名称相同的cookie
Cookie cookie = new Cookie("time","aaa");
//设置相同的path
cookie.setPath(request.getContextPath()+"/");
//设置生命时长为0
cookie.setMaxAge(0);
//发送到浏览器
response.addCookie(cookie);
```

需要注意的是：一旦服务器将cookie发送到客户端，就没有显式删除cookie的办法。但是可以通过覆盖cookie实现实质性的删除操作：即服务器创建一个MaxAge为0，且除了value之外，其他都必须相同cookie，发往客户端。

> 但凡有一点不满足，都会创建一个新的同名Cookie，而不是删除原来的。



# domain与path

domain：规定哪些域名需要附带Cookie。

- 不指定domain默认为当前域名，此时子域名就不会附带Cookie。
- 如果setDomain(".mamll.com")，此时，该一级域名相同的资源都会附带Cookie。

path：指定哪些路径需要附带Cookie。

- 如果不指定，默认为只有当前资源的映射路径下有效。

- 如果指定path，则指定路径及其级别下都要附带Cookie。因此，我们可以将有效路径设置为web应用的虚拟路径：`cookie.setPath(request.getContextPath()+"/");`，这样web应用所有资源都会附带Cookie。

# Cookie技术的用途

保存个性化信息：用户的偏好，网页的背景图等。

对话管理：保存登录、购物车等信息。

追踪用户：记录和分析用户的行为。