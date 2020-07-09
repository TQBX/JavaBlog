[toc]

# 一、Spring-Security前瞻

Spring Security是一个功能强大且高度可定制的身份验证和访问控制框架。它是保护基于spring的应用程序的事实标准。Spring Security是一个重点为Java应用程序提供身份验证和授权的框架。与所有Spring项目一样，Spring Security的真正强大之处在于它可以很容易地扩展以满足定制需求。

# 二、特点描述

- Comprehensive and extensible support for both Authentication and Authorization，对认证和授权保护的全面和可扩展的支持
- Protection against attacks like session fixation, clickjacking, cross site request forgery，防止会话固定，点击劫持，跨站点请求伪造等攻击。
- Servlet API integration，Servlet API集成。
- Optional integration with Spring Web MVC，可以与Spring Web MVC集成。

# 三、Springboot自动配置相关

- 启用Spring Security的默认配置，创建一个servlet Filter作为一个名为springSecurityFilterChain的bean，负责应用的安全，如保护应用程序url，验证提交的用户名和密码，重定向到登录表单。
- 使用用户名和随机生成的登录到控制台的密码创建名为UserDetailsService的bean。

- 为每个请求用一个名为springSecurityFilterChain的bean向Servlet容器注册过滤器。

Springboot虽然不用配置很多东西，但事实上他已经完成了许多的工作：

- 任何需要和应用交互的用户都需要身份验证。
- 为你生成一个默认的登录表单。
- 让用户基于名为`user`的用户名和生成的密码验证表单以通过身份验证。
- 通过BCrypt保护密码存储。
- 可以让用户注销。
- 防止CSRF攻击。
- 防御会话伪造session攻击。
  - [https://www.cnblogs.com/wenjieyatou/p/6118585.html](https://www.cnblogs.com/wenjieyatou/p/6118585.html)
  - [https://my.oschina.net/lemonzone2010/blog/266882](https://my.oschina.net/lemonzone2010/blog/266882)
- Header的集成。
  - HTTP严格传输安全的请求。
  - X-Content-Type-Options的集成。
  - Cache Control的集成。
  - X-XSS-Protection的集成
  - X-Frame-Options的集成，防止Clickjacking。

- 与以下ServletAPI的集成。
  - `HttpServletRequest#getRemoteUser()`
  - `HttpServletRequest.html#getUserPrincipal()`
  - `HttpServletRequest.html#isUserInRole(java.lang.String)`
  - `HttpServletRequest.html#login(java.lang.String, java.lang.String)`
  - `HttpServletRequest.html#logout()`

# 四、核心组件

## SecurityContextHolder、SecurityContextHolder, SecurityContext and Authentication Objects

> 最基本的对象，存储应用程序当前安全上下文的详细信息，其中包括当前使用应用的主体信息。

默认情况下，**SecurityContextHolder使用ThreadLocal来存储这些细节**，这意味着：即使SecurityContext没有显式地作为参数传递给这些方法，SecurityContext始终**对同一执行线程中的方法可用**，如果在当前主体的请求被处理后清除线程，那么以这种方式使用ThreadLocal是非常安全的。

有些应用程序并不完全适合使用ThreadLocal，他们可能需要通过指定的方式运行线程。例如，Swing客户机可能希望Java虚拟机中的所有线程使用相同的安全上下文。

为此，SecurityContextHolder可以**在启动时配置策略，以指定如何存储上下文**。

```java
public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";//默认
public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";//派生
public static final String MODE_GLOBAL = "MODE_GLOBAL";//独立应用
```

## Obtaining information about the current user

```java
Object principal = SecurityContextHolder
    .getContext() 
    .getAuthentication()
    .getPrincipal();
if (principal instanceof UserDetails) {
	String username = ((UserDetails)principal).getUsername();
} else {
	String username = principal.toString();
}
```

- getContext()方法返回的是一个SecurityContext 实例，存储在Thread-Local中。

- Spring Security中的大多数身份验证机制都返回一个UserDetails实例

## The UserDetailsService

Authentication中获取的Principal大多数情况下是UserDetails对象，Userdetails可以看作是用户数据库和SecurityContext之间的适配器。

也就是说，我们通常会将UserDetails转换为`the original object that your application provided`，这样您就可以调用特定于业务的方法，如`getEmail(),getEmployeeNumber`。

那么，我们该如何利用UserDetails建立起两者之间的桥梁呢？便是利用UserDetailsService接口。

```java
public interface UserDetailsService {
    //接收用户名字符串，返回UserDetails，SpringSecurity中加载用户信息的最常见的方式。
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

该接口上唯一的方法接受基于字符串的用户名参数并返回一个UserDetails。

在成功进行身份验证的时候，UserDetails将被用来构建存储在**SecurityContextHolder**中的Authentication对象。SpringSecurity提供了许多UserDetailsService的实现，包括基于内存的InMemoryDaoImpl和使用JDBC的JdbcDaoImpl。但是，我们大多数时候都会选择现有数据访问对象（dao）的实现。

> 注意：UserDetailsService是一个pure DAO，除了将数据提供给框架内的其他组件外，不执行其他的功能。特别是，它**没有对用户进行身份验证**，这是由AuthenticationManager完成的。在许多情况下，如果需要定制身份验证过程，那么直接实现AuthenticationProvider更有意义。

## GrantedAuthority

除了principal之外，身份验证提供的另一个重要方法是`getAuthorities()`。此方法提供了一个GrantedAuthority对象数组。

GrantedAuthority就是授予principal主体的权限，比如`"role"`权限，`ROLE_ADMINISTRATOR`和`ROLE_HR_SUPERVISOR`。这些roles之后将会被配置为web授权，方法授权和域对象授权，而SpringSecurity的其他组件可以解释这些权限。

**GrantedAuthority对象通常由UserDetailsService加载。**

通常，GrantedAuthority对象是`application-wide permissions`，并不是特定于给定域对象的。因此，你不可能去拥有一个GrantedAuthority去代表54号Employee的权限，因为如果这样，有成千上万个Employees，可能会耗尽内存，或者需要很长时间去验证一个用户。当然你可以使用SpringSecurity中的项目的域对象安全功能来实现该目的。

# 五、模块回顾

- `SecurityContextHolder`, to provide access to the `SecurityContext`，提供对Security上下文的访问。
- `SecurityContext`, to hold the `Authentication` and possibly request-specific security information，维护身份验证和可能特定于请求的安全信息。
- `Authentication`, to represent the principal in a Spring Security-specific manner，以Spring-Security‘特定的方式表示主体principal。
- `GrantedAuthority`, to reflect the application-wide permissions granted to a principal，反映授予主体的应用程序范围的权限。
- `UserDetails`, to provide the necessary information to build an Authentication object from your application’s DAOs or other source of security data，为从应用程序、dao或其他安全数据源构建身份验证对象提供必要的信息。
- `UserDetailsService`, to create a `UserDetails` when passed in a `String`-based username (or certificate ID or the like)，传入用户名字符串，构建用户详细信息。

