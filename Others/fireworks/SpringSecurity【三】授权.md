# Access-Control (Authorization) in Spring Security

Spring Security中两个非常重要且相似的名词：Authentication和Authorization，认证与授权，所谓认证涉及到身份验证机制，所谓授权涉及到访问控制。

## AccessDecisionManager

在Spring Security中，负责做出**访问控制决策**的主要接口是`AccessDecisionManager`。它有一个decision方法，该方法接受表示请求访问的主体的身份验证对象、一个“安全对象”(见下文)和一列应用于该对象的安全元数据属性(例如一列访问被授予所需的角色)。

```java
/**
 * 做出最终的访问控制决策
 */
public interface AccessDecisionManager {
	/**
	 * 为传递的参数处理访问控制请求
	 *
	 * @param authentication 代表需要访问主体的Authentication对象
	 * @param object the secured object
	 * @param configAttributes 应用于该对象的安全元数据属性列表
	 */
	void decide(Authentication authentication, Object object,
			Collection<ConfigAttribute> configAttributes) throws AccessDeniedException,
			InsufficientAuthenticationException;
	boolean supports(ConfigAttribute attribute);
	boolean supports(Class<?> clazz);
}

```

可以选择使用`AspectJ`或`Spring AOP`执行方法授权，也可以选择使用**过滤器执行web请求授权**，当然可以同时使用以上多种方法。主流的使用模式是执行一些web请求授权，以及在服务层上执行一些Spring AOP方法调用授权。

## secured objects

什么是secured objects？Spring给出的定义是：`Spring Security uses the term to refer to any object that can have security (such as an authorization decision) applied to it. The most common examples are method invocations and web requests.`

每种受支持的安全对象类型都有自己的拦截器类，这些拦截器类应该是`AbstractSecurityInterceptor`的子类。并且，在调用`AbstractSecurityInterceptor`时，如果主体已经通过身份验证，则`securitycontextsHolder`将包含有效的身份验证。

## AbstractSecurityInterceptor

AbstractSecurityInterceptor为处理安全对象请求提供了一致的工作流：

1. 查找与当前请求相关联的配置属性。
2. 将安全对象、当前身份验证和配置属性提交给AccessDecisionManager以进行授权决策。
3. 可选地更改调用所依据的Authentication。
4. 允许安全对象调用继续进行(假设已授予访问权限)。
5. AbstractSecurityInterceptor调用返回之后执行调用AfterInvocationManager，如果抛出异常则不向后执行。

## Configuration Attributes

AbstractSecurityInterceptor配置了SecurityMetadataSource，用于查找安全对象的属性。通常这个配置对用户是隐藏的。配置属性将作为安全方法上的注释或安全url上的访问属性输入。