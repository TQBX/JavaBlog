今日疑惑：为啥视频里获取中文参数需要`request.setCharacterEncoding("utf-8");`设置utf-8编码，而我自己实现不需要。

为一探究竟，于是展开测验：**乱码的问题**产生原因所在，一定是因为**双方在编解码时使用的字符集不统一导致**。

浏览器向Tomcat服务器发送数据时，使用`utf-8`编码发送中文字符，而Tomcat8.0版本之前默认的编解码字符集为**ISO8859-1**。因此，如果想要解决编码问题，就需要通知Tomcat服务器，赶紧使用`utf-8`字符集解码。

# 针对Tomcat版本

针对于Tomcat8.0之前的版本的POST方法，如下：

```java
request.setCharacterEncoding("utf-8");
String nickname = request.getParameter("nickname");
```

针对于Tomcat8.0之前的版本的GET方法，如下：

```java
String nickname = request.getParameter("nickname");
nickname = new String(nickname.getBytes("iso8859-1"),"utf-8");
```

Tomcat8.0之后的版本的默认编码为`utf-8`编码，GET不需要进行处理，而POST的处理方法为：

```java
request.setCharacterEncoding("utf-8");
String nickname = request.getParameter("nickname");
```

ps:这里的POST指得是提交的是，并不指代码存在那个方法块中，因为有时候可能会在`DoPost`方法中调用`DoGet`方法。

## 为什么POST与GET不同

![image-20200316152934121](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200316152934121.png)

可以发现，不论是Tomcat的版本是7也好，是8也好。对于get和post请求方式，需要进行编解码指定字符集的方式都不相同，这是为什么呢？

原因在于，`request.setCharacterEncoding("utf-8");`这种设置能够指示服务器用什么字符集去解码。解的什么码呢，数据从何而来呢？其实数据存在于请求实体中，也就是POST方式提交的参数待的地方。这就是为什么，仅仅通过这句就可以解决POST乱码问题。

而GET方式请求的参数拼接在URI后面，这个方法也就没什么作用了。我们直到，浏览器通过`utf-8`这种编码方式，将每个字符编码成3个字节（二进制数据）传送到服务器，服务器如果以`ISO8859-1`的方式，将会把每个字节解码成一个字符，然后去对应的码表中寻找，找不到就会显示一个？。

因此，针对GET方法，新版Tomcat不需要进行处理，因为字符集统一都是`utf-8`。而以往的Tomcat需要进行以下操作：

- 将字符以`ISO8859-1`的形式重新编码成字节（二进制数据），`getBytes("iso8859-1")`。
- 将字节重新以`utf-8`的形式再次解码，转为中文字符，`new String(byte[]，"utf-8")`。

