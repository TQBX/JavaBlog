[toc]

# SpringSecurity中的认证：Authentication

我们先来考虑一个最正常不过的用户信息验证场景：

1. 提示用户使用用户名和密码登录。
2. 系统（成功)验证用户名的密码是否正确。
3. 获取该用户的上下文信息（他们的角色列表等)。
4. 为用户建立安全上下文。
5. 用户继续操作，可能会执行一些操作，这些操作可能受到访问控制机制的保护，该机制根据当前安全上下文信息检查操作所需的权限。

前四项构成了身份验证过程，而这些在Spring Security中是如何发生的呢？

1. 用户名和密码被获取并组合到`UsernamePasswordAuthenticationToken`的实例中(Authentication接口的实例)。
2. token令牌被传递到`AuthenticationManager`的实例进行验证。
3. 身份验证成功时，`AuthenticationManager`将返回一个完全填充的身份验证实例。
4. 安全上下文是通过调用`SecurityContextHolder.getContext().setAuthentication(…)`，传入返回的身份验证对象。

## 简单案例

```java
/**
 * created by Summer-day
 */
public class AuthenticationExample  {
	private static AuthenticationManager am = new SampleAuthenticationManager();
	public static void main(String[] args) throws IOException {
		BufferedReader in  = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			
			System.out.println("please enter your username: ");
			String name = in.readLine();
			System.out.println("please enter your password: ");
			String password = in.readLine();
			
			try {
				//用户名和密码被组合到UsernamePasswordAuthenticationToken实例中
				Authentication request = new UsernamePasswordAuthenticationToken(name, password);
				//传递到AuthenticationManager实例中进行验证,身份验证成功后将返回一个完全填充的身份验证实例
				Authentication authentication = am.authenticate(request);
				//为安全上下文中传入身份验证对象,之后就可以对用户进行身份验证.
				SecurityContextHolder.getContext().setAuthentication(authentication);
				break;
			}catch (AuthenticationException e){
				System.out.println("authentication failed: "+e.getMessage());
			}
			
		}
		System.out.println("successfully authenticated security context contains: "+SecurityContextHolder.getContext().getAuthentication());
	}
	
}
class SampleAuthenticationManager implements AuthenticationManager{
	static final List<GrantedAuthority> AUTHORITIES = new ArrayList<>();
	static {
		AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
	}
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		//身份验证成功后将返回一个完全填充的身份验证实例
		if(auth.getName().equals(auth.getCredentials())){
			return new UsernamePasswordAuthenticationToken(auth.getName(),auth.getCredentials(),AUTHORITIES);
		}
		throw new BadCredentialsException("bad credentials");
	}
}
```

Spring Security并不介意如何将Authentication对象放入SecurityContextHolder中。唯一关键的需求是securitycontext在AbstractSecurityInterceptor需要授权用户操作之前包含一个代表主体的身份验证。

# 典型web应用验证授权过程

1. 访问主页，并单击链接。
2. 一个请求发送到服务器，服务器决定您请求的是一个受保护的资源。
3. 如果你**尚未通过身份验证**，服务器将返回一个响应，指示你必须进行身份验证。响应要么是HTTP响应代码，要么是对特定web页面的重定向。
4. 根据**身份验证机制**的不同，浏览器要么重定向到特定的web页面以便你填写表单，要么以某种方式检索你的身份（通过基本身份验证对话框、cookie、X.509证书等)。
5. 浏览器将向服务器返回一个响应。这要么是一个包含你填写的表单内容的POST请求，要么是一个包含你的身份验证细节的HTTP头。
6. 接下来，服务器将决定当前credentials 是否有效。如果它们是有效的，下一步将会发生。如果它们无效，通常你的浏览器会被要求再试一次（所以你返回到上面的第二步)。
7. 你发出的导致身份验证过程的原始请求将被重试。希望您已经通过身份验证获得了访问受保护资源的足够授权。如果您有足够的访问权限，请求将会成功。否则，您将收到一个HTTP错误代码403 forbidden。

Spring Security有专门的类负责上面描述的大多数步骤。主要是`ExceptionTranslationFilter`、`AuthenticationEntryPoint`和`authentication mechanism`身份验证机制，后者负责调用我们在前一节中看到的AuthenticationManager。

## ExceptionTranslationFilter

`ExceptionTranslationFilter`是一个Spring Security过滤器，负责检测抛出的任何Spring安全异常。这类异常通常由`AbstractSecurityInterceptor`抛出，它是授权服务的主要提供者。ExceptionTranslationFilter负责返回错误代码403(如果principal已经过身份验证的，因此只是缺乏足够的访问，开始第七步)，或启动一个`AuthenticationEntryPoint`（如果principal还没有经过身份验证的，开始第三步)。

## AuthenticationEntryPoint

**每个web应用程序都有一个默认的身份验证策略**，每个主要的身份验证系统都有自己的`AuthenticationEntryPoint`实现，它通常执行步骤3中描述的操作之一。

## Authentication Mechanism

一旦浏览器提交身份验证凭证（以HTTP表单post或HTTP头的形式)，服务器上就需要**收集这些身份验证细节**。到目前为止，我们已经进入了上述列表中的第六步。在Spring Security中，我们对从用户代理（通常是web浏览器)收集身份验证细节的功能有一个特殊的名称，称为`authentication mechanism`身份验证机制。

**身份验证机制接收回完整填充的身份验证对象后，它将认为请求有效，将身份验证放入securitycontext中，并导致重试原始请求（上面的第7步)**。另一方面，如果AuthenticationManager拒绝了请求，身份验证机制将要求用户代理重试（上面的第2步)。

## Storing the SecurityContext between requests

根据应用程序的类型，可能需要一种策略来存储用户操作之间的安全上下文。在典型的web应用程序中，用户登录一次，随后通过会话Id识别。服务器缓存持续会话的主体信息。**在Spring Security中，在请求之间存储SecurityContext的责任落在SecurityContextPersistenceFilter上，默认情况下，它将context作为HTTP请求之间的HttpSession属性存储。**它为每个请求将上下文恢复到securitycontext tholder，并且，至关重要的是，在请求完成时清除securitycontext tholder。出于安全考虑，不应该直接与HttpSession交互，而是始终使用securitycontexts tholder代替。

许多其他类型的应用程序（例如，无状态的RESTful web服务)不使用HTTP会话，并且将对每个请求重新进行身份验证。然而，仍然重要的是SecurityContextPersistenceFilter包含在链中，以确保securitycontext tholder在每个请求后被清除。

---

在一个在单个会话中接收并发请求的应用程序中，同一个SecurityContext实例将在线程之间共享。即使使用了ThreadLocal，它也是为每个线程从HttpSession检索的相同实例。如果您只是使用SecurityContext . getcontext()，并在返回的上下文对象上调用setAuthentication(anAuthentication)，那么在共享同一个SecurityContext实例的所有并发线程中，Authentication对象将会改变。您可以定制SecurityContextPersistenceFilter的行为，为每个请求创建一个全新的SecurityContext，防止一个线程中的更改影响另一个线程。或者，您可以在临时更改上下文时创建一个新实例。

方法`securitycontext.createemptycontext()`总是返回一个新的上下文实例。