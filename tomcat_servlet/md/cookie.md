cookie-会话技术

# 会话是啥

浏览器与服务器建立连接之后，

http本身是无状态的，请求之间互不影响。

有时为了解决xxx等问题，可以利用会话技术解决。

# Cookie类的使用

## 创建cookie

```java
Cookie cookie = new Cookie("loginTime",s);
```

## 发送到浏览器

```java
response.addCookie(cookie);
```

## 设置最大生命时长

```java
//设置最大生命时长,以秒为单位
cookie.setMaxAge(60*60*24);
```

## 设置有效路径

```java
cookie.setPath(request.getContextPath()+"/");
```

## 获取cookie

```java
Cookie[] cookies = request.getCookies();
```

## 杀死cookie

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