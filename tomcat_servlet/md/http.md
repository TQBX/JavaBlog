# HTTP是啥

HTTP是`hypertext transfer protocol`，超文本传输协议，它是TCP/IP协议的一个**应用层协议**，用于定义WEB浏览器与WEB服务器之间交换数据的过程。

HTTP协议基于**请求响应模型**：浏览器向服务器发送**请求**数据，服务器对数据进行处理，根据请求做出响应。也就是说，先从客户端建立通信。

一次请求需要对应一次响应。

![image-20200314150608249](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200314150608249.png)

# HTTP1.0和HTTP1.1的区别

HTTP1.0协议中，客户端与web服务器建立TCP连接后，在请求得到一次响应后，获取web资源后连接断开。一次连接只能获得一个资源。随着HTTP的普及，文档中包含大量图片，无疑会导致大量无用的TCP连接与断开，增加通信量的开销。

为了解决以上问题，**持久连**接的策略也被提出：只要任意一段没有明确提出断开连接，则保持TCP的连接状态。

**HTTP1.1协议默认所有的连接都是持久连接**，允许客户端与web服务器建立连接后，在请求得到一次响应后，仍然会等待下一次请求，直到一段时间后，没有请求发送，连接才会断开。在一个连接上获取多个web资源。（持久连接）

> 持久连接减少了TCP连接的重复建立和断开造成的额外开销，减轻了服务器端的负载，使web页面的响应速度加快。
>
> 持久连接也让管线化方式发送请求成为可能：发送请求后，不需要等待响应就可以发送下一次请求。

HTTP本身是**无状态**的协议，它不对之前发生过的请求和响应的状态进行管理，而我们现在见到大多数的登录状态保存是基于Cookie技术实现的。

# HTTP报文是啥

**用于HTTP协议交互的信息被称为HTTP报文**，客户端的HTTP报文称为请求报文，服务器端的叫做响应报文。HTTP报文本身是由多行数据组成的字符串文本。

报文的格式：

报文首部：对应请求报文中的请求行、首部字段以及响应报文中的状态行、首部字段。

空行：用以划分报文首部和报文主体。

报文主体：作为请求或响应的有效载荷数据被传输，不一定需要。

# HTTP请求报文

客户端连上服务器后，向服务器请求某个web资源，称之为客户端向服务器发送了一个HTTP请求。

一个完整的HTTP请求需要包含以下内容：

- 一个请求行：描述客户端的请求方式、请求资源的名称以及使用的HTTP协议版本号。
- 若干请求头：用于描述客户端请求的Host，以及客户端的环境信息。
- 一个空行：分隔请求头和请求实体。
- 实体内容：作为请求或响应的有效载荷数据被传输，不一定需要。

![request](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\request.png)

## 请求行

> GET /s?ie=UTF-8&wd=baidu HTTP/1.1

GET：表示请求访问服务器的方法。

/s?ie=UTF-8&wd=baidu：知名了请求访问的资源对象，也称做**请求URI**。

HTTP/1.1 ：是客户端对应的HTTP版本。

### 访问服务器的方法

HTTP1.1支持的方法有：GET、POST、PUT、DELETE、OPTIONS、TRACE、CONNECT共七种。

HTTP1.0不支持的方法有OPTIONS、TRACE、CONNECT。

## 请求头

请求头种类众多，可以参看关于HTTP的书籍，强推《图解HTTP》。以下参考：[HTTP协议](https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247484755&idx=3&sn=109e71815ba6474a1e0f5120ab7e0d10&chksm=ebd74452dca0cd449e12c3bd6b8bb59b917f2c26c2e028c80e416e9eef2e9badf76a3f33e3ee###rd)

- Accept: text/html,image/* 【浏览器告诉服务器，它支持的数据类型】
- Accept-Charset: ISO-8859-1 【浏览器告诉服务器，它支持哪种**字符集**】
- Accept-Encoding: gzip,compress 【浏览器告诉服务器，它支持的**压缩格式**】
- Accept-Language: en-us,zh-cn 【浏览器告诉服务器，它的语言环境】
- Host: www.it315.org:80【浏览器告诉服务器，它的想访问哪台主机】
- If-Modified-Since: Tue, 11 Jul 2000 18:23:51 GMT【浏览器告诉服务器，缓存数据的时间】
- Referer: http://www.it315.org/index.jsp【浏览器告诉服务器，客户机是从那个页面来的---**防盗链**、统计工作】
- User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)【浏览器告诉服务器，浏览器的内核是什么】
- Cookie：【浏览器告诉服务器，**带来的Cookie是什么**】
- Connection: close/Keep-Alive： 【浏览器告诉服务器，请求完后是断开链接还是保持链接】
- Date: Tue, 11 Jul 2000 18:23:51 GMT：【浏览器告诉服务器，请求的时间】

# HTTP响应

服务器接收到客户端的请求，对其进行处理，作出响应。

一个完整的HTTP响应需要包含以下内容：

- 一个状态行：描述服务器对请求的处理结果。
- 若干响应头：用于描述服务器的基本信息，以及数据的描述，告诉客户端如何处理数据。
- 一个空行：分隔响应头和响应实体。
- 实体内容：服务器向客户端回送的数据，通常由html资源等。

![response](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\response.png)

## 状态行

> HTTP/1.1 200 OK

HTTP/1.1 代表服务器对应的HTTP版本。

200：表示请求的处理结果的状态码。

OK：表示状态码对应的原因短语。

## 常见状态码

![statuscode](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\statuscode.png)

## 响应头

- Location: http://www.it315.org/index.jsp 【服务器告诉浏览器**要跳转到哪个页面**】
- Server:apache tomcat【服务器告诉浏览器，服务器的型号是什么】
- Content-Encoding: gzip 【服务器告诉浏览器**数据压缩的格式**】
- Content-Length: 80 【服务器告诉浏览器回送数据的长度】
- Content-Language: zh-cn 【服务器告诉浏览器，服务器的语言环境】
- Content-Type: text/html; charset=GB2312 【服务器告诉浏览器，**回送数据的类型**】
- Last-Modified: Tue, 11 Jul 2000 18:23:51 GMT【服务器告诉浏览器该资源上次更新时间】
- Refresh: 1;url=http://www.it315.org【服务器告诉浏览器要**定时刷新**】
- Content-Disposition: attachment; filename=aaa.zip【服务器告诉浏览器**以下载方式打开数据**】
- Transfer-Encoding: chunked 【服务器告诉浏览器数据以分块方式回送】
- **Set-Cookie**:SS=Q0=5Lb_nQ; path=/search【服务器告诉浏览器要**保存Cookie**】
- Expires: -1【服务器告诉浏览器**不要设置缓存**】
- Cache-Control: no-cache 【服务器告诉浏览器**不要设置缓存**】
- Pragma: no-cache 【服务器告诉浏览器**不要设置缓存**】
- Connection: close/Keep-Alive 【服务器告诉浏览器连接方式】
- Date: Tue, 11 Jul 2000 18:23:51 GMT【服务器告诉浏览器回送数据的时间】