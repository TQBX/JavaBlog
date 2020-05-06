

# 一、前言

> 版本：
>
> springMVC 5.0.2RELEASE
>
> JDK1.8

前端控制器的配置：

web.xml

```xml
    <!--配置前端控制器-->
    <servlet>
        <servlet-name>dispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <!--加载类路径下的配置文件-->
            <param-value>classpath:springmvc.xml</param-value>
        </init-param>
        <!--服务器启动时创建对象,值越小,优先级越高,越先创建对象-->
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>dispatcherServlet</servlet-name>
        <!--注意不是/*,而是，因为/*还会拦截*.jsp等请求-->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
```

# 二、初始化

![DispatcherServlet](E:\1JavaBlog\frameworks\springmvc\pic\DispatcherServlet.png)

DispatcherServlet的启动与Servlet的启动过程紧密联系，我们通过以上继承图就可以发现。

## 1. 容器初始化

Servlet中定义的init()方法就是其生命周期的初始化方法，接着往下走，GenericServlet并没有给出具体实现，在HttpServletBean中的init()方法给出了具体的实现：

`HttpServletBean.init()`方法（忽略日志）

```java
	@Override
	public final void init() throws ServletException {
        //根据初始化参数设置bean属性(我们设置了contextConfigLocation，故可以获取)
		PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
		if (!pvs.isEmpty()) {
			try {
                //包装DispatcherServlet
				BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
                //获取资源加载器，用以加载springMVC的配置文件
				ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
                //注册一个ResourceEditor
				bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
                //该方法为空实现，可以重写，初始化BeanWrapper
				initBeanWrapper(bw);
                //最终将init-param读取的值spirng-mvc.xml存入contextConfigLocation中
				bw.setPropertyValues(pvs, true);
			}
		}

		// 让子类实现初始化
		initServletBean();

	}
```

那就来看看`FrameworfServlet.initServletBean()`干了啥（基本都是日志记录，还有计时，省略了这些部分）：

```java
	/**
	 * Overridden method of {@link HttpServletBean}, invoked after any bean properties
	 * have been set. Creates this servlet's WebApplicationContext.
	 */
	@Override
	protected final void initServletBean() throws ServletException {
        //WebApplicationContext的初始化
        this.webApplicationContext = initWebApplicationContext();
        //也是空实现，允许子类自定义
        initFrameworkServlet();
	}
```

所以重头戏就在initWebApplicationContext方法上，我们可以先来看看执行后的效果：

![image-20200504110000961](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200504110000961.png)

可以看到springMVC九大组件被赋值，除此之外webApplicationContext也已被赋值。

我们再来看看源码，看看其内部具体实现：`FrameworkServlet.initWebApplicationContext()`

```java
protected WebApplicationContext initWebApplicationContext() {
    //根容器查找
    WebApplicationContext rootContext =
        WebApplicationContextUtils.getWebApplicationContext(getServletContext());
    WebApplicationContext wac = null;
    if (this.webApplicationContext != null) {
        //在构建时注入了DispatcherServlet并且webApplicationContext已经存在->直接使用
        wac = this.webApplicationContext;
        if (wac instanceof ConfigurableWebApplicationContext) {
            ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) wac;
            if (!cwac.isActive()) {
                //如果context还没有refresh-->进行设置父级context以及application context的id等等操作
                if (cwac.getParent() == null) {
                    //在没有显式父级的情况下注入了context实例->将根应用程序上下文设置为父级
                    cwac.setParent(rootContext);
                }
                configureAndRefreshWebApplicationContext(cwac);
            }
        }
    }
    if (wac == null) {
		//在构造时未注入任何上下文实例-->从ServletContext中查询
        wac = findWebApplicationContext();
    }
    if (wac == null) {
        // ServletContext中没有-->就创建一个被本地的
        wac = createWebApplicationContext(rootContext);
    }
    if (!this.refreshEventReceived) {
        //如果context不支持refresh或者在初始化的时候已经refresh-->就手动触发onfresh
        onRefresh(wac);
    }
    //把当前建立的上下文存入ServletContext中，使用的属性名和当前Servlet名相关
    if (this.publishContext) {
        // 将上下文发布为servlet上下文属性
        String attrName = getServletContextAttributeName();
        getServletContext().setAttribute(attrName, wac);
    }
    return wac;
}
```

### 根容器查找的方法

```java
WebApplicationContext rootContext =
    WebApplicationContextUtils.getWebApplicationContext(getServletContext());
```
`WebApplicationContextUtils.getWebApplicationContext`

```java

//SpringMVC支持Spring容器与Web容易同时存在，并且Spring容器视作根容器，通常由ContextLoaderListener进行加载。
@Nullable
public static WebApplicationContext getWebApplicationContext(ServletContext sc) {
    //String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT"
    return getWebApplicationContext(sc, WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
}

@Nullable
public static WebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
	//根据ServletName.ROOT为键查找值
    Object attr = sc.getAttribute(attrName);
    if (attr == null) {
        return null;
    return (WebApplicationContext) attr;
}
```

Spring容器和Web容器如果同时存在，需要使用ContextLoaderListener加载Spring的配置，且它会以key为

`WebApplicationContext.class.getName() + ".ROOT`存到ServletContext中。

### 容器创建的方法

构建的时候没有任何Context实例注入，且ServletContext中也没有找到WebApplicationContext，此时就会创建一个local Context，这个方法允许显式传入父级容器作为参数。

```java
protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
    //默认：DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;可以在初始化参数中指定contextClass
    Class<?> contextClass = getContextClass();
    if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
        throw new ApplicationContextException(
            "Fatal initialization error in servlet with name '" + getServletName() +
            "': custom WebApplicationContext class [" + contextClass.getName() +
            "] is not of type ConfigurableWebApplicationContext");
    }
    //获取ConfigurableWebApplicationContext对象
    ConfigurableWebApplicationContext wac =
        (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

    wac.setEnvironment(getEnvironment());
    wac.setParent(parent);
    String configLocation = getContextConfigLocation();
    if (configLocation != null) {
        wac.setConfigLocation(configLocation);
    }
    configureAndRefreshWebApplicationContext(wac);

    return wac;
}
```

我们可以发现：在这个过程中，Web容器的IoC容器被建立，也就是XmlWebApplicationContext，，从而在web容器中建立起整个spring应用。 

`configureAndRefreshWebApplicationContext(wac);`

```java
protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {
    //省略给ConfigurableWebApplicationContext对象设置一些值...
    //每次context refresh，都会调用initPropertySources
    ConfigurableEnvironment env = wac.getEnvironment();
    if (env instanceof ConfigurableWebEnvironment) {
        ((ConfigurableWebEnvironment) env).initPropertySources(getServletContext(), getServletConfig());
    }
    postProcessWebApplicationContext(wac);
    applyInitializers(wac);
    //初始化webApplication容器，重启
    wac.refresh();
}
```

### 加载配置文件信息

其实也就是refresh()这个关键方法，之前了解过spring容器的初始化的过程，对这一步应该相当熟悉，还是分为三步：

- BeanDefinition的Resource的定位，我们这定位到了classpath:springmvc.xml。

![image-20200504145332063](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200504145332063.png)

- beanDefinition的载入过程，springMVC做了一些改变，比如定义了针对mvc的命名空间解析MvcNamespaceHandler。

![image-20200504133528656](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200504133528656.png)

- 接着是beanDefinition在IoC中的注册，也就是把beanName：beanDefinition以键值对的形式存入beandefinitionMap中。

## 2. MVC的初始化

MVC的初始化在DispatcherServlet的initStratefies方法中执行，通过方法名，我们就可以得出结论，就是在这进行了对九大组件的初始化，其实基本上都是从IoC容器中获取对象：

```java
	protected void initStrategies(ApplicationContext context) {
        //文件上传解析器
		initMultipartResolver(context);
        //区域信息解析器，与国际化相关
		initLocaleResolver(context);
        //主题解析器
		initThemeResolver(context);
        //handler映射信息解析
		initHandlerMappings(context);
        //handler的适配器
		initHandlerAdapters(context);
        //handler异常解析器
		initHandlerExceptionResolvers(context);
        //视图名转换器
		initRequestToViewNameTranslator(context);
        //视图解析器
		initViewResolvers(context);
        //flashMap管理器
		initFlashMapManager(context);
	}
```

### 文件上传解析器

```java
private void initMultipartResolver(ApplicationContext context) {
    try {
        this.multipartResolver = context.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
    }
    catch (NoSuchBeanDefinitionException ex) {
        // 默认是没有配置multipartResolver的.
        this.multipartResolver = null;
    }
}
```

配置文件上传解析器也很简单，只需要在容器中注册MultipartResolver即可开启文件上传功能。

### 区域信息解析器

```java
private void initLocaleResolver(ApplicationContext context) {
    try {
        this.localeResolver = context.getBean(LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);

    }
    catch (NoSuchBeanDefinitionException ex) {
        // 使用默认策略,利用反射创建对象
        this.localeResolver = getDefaultStrategy(context, LocaleResolver.class);
    }
}
```

`org.springframework.web.servlet.DispatcherServlet`同级目录下的`DispatcherServlet.properties`文件中规定了几大组件初始化的默认策略。

![image-20200504152507850](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200504152507850.png)

### handler映射信息解析

handlerMappings存在的意义在于为HTTP请求找到对应的控制器Controller。

```java
	private void initHandlerMappings(ApplicationContext context) {
		this.handlerMappings = null;
		//从所有的IoC容器中导入HandlerMappings，包括其双亲上下文
		if (this.detectAllHandlerMappings) {
			Map<String, HandlerMapping> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerMappings = new ArrayList<>(matchingBeans.values());
				// We keep HandlerMappings in sorted order.
				AnnotationAwareOrderComparator.sort(this.handlerMappings);
			}
		}
		else {
			try {
                //尝试从容器中获取
				HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
                
				this.handlerMappings = Collections.singletonList(hm);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerMapping later.
			}
		}
        //保证至少有一个handlerMapping
		if (this.handlerMappings == null) {
			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
		}
	}
```

接下来几个操作都差不多，就不赘述了。

总的来说，MVC初始化的过程建立在IoC容器初始化之后，毕竟要从容器中取出这些组件对象。

## 3. HandlerMapping的实现原理

### HandlerExecutionChain

HandlerMapping在SpringMVC扮演着相当重要的角色，我们说，它可以为HTTP请求找到 对应的Controller控制器，于是，我们来好好研究一下，这里面到底藏着什么玩意。

![image-20200504154801763](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200504154801763.png)

HandlerMapping是一个接口，其中包含一个getHandler方法，能够通过该方法获得与HTTP请求对应的handlerExecutionChain，而这个handlerExecutionChain对象中持有handler和interceptorList，以及和设置拦截器相关的方法。可以判断是同通过这些配置的拦截器对handler对象提供的功能进行了一波增强。

![image-20200504160311683](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200504160311683.png)

### RequestMappingHandlerMapping

我们以其中一个HandlerMapping作为例子解析一下，我们关注一下：



```java
protected void initHandlerMethods() {
	//获取所有上下文中的beanName
    String[] beanNames = (this.detectHandlerMethodsInAncestorContexts ?
                          BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), Object.class) :
                          obtainApplicationContext().getBeanNamesForType(Object.class));

    for (String beanName : beanNames) {
        if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
            Class<?> beanType = null;
            //得到对应beanName的Class
            beanType = obtainApplicationContext().getType(beanName);
            //判断是否为控制器类
            if (beanType != null && isHandler(beanType)) {
                //对控制器中的方法进行处理
                detectHandlerMethods(beanName);
            }
        }
    }
    handlerMethodsInitialized(getHandlerMethods());
}
```

isHandler方法：判断该类是否存在@Controller注解或者@RequestMapping注解

```java
	@Override
	protected boolean isHandler(Class<?> beanType) {
		return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
				AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class));
	}
```

detectHandlerMethods方法：

```java
protected void detectHandlerMethods(final Object handler) {
    //获取到控制器的类型
    Class<?> handlerType = (handler instanceof String ?
                            obtainApplicationContext().getType((String) handler) : handler.getClass());
    if (handlerType != null) {
        //对类型再次进行处理，主要是针对cglib
        final Class<?> userType = ClassUtils.getUserClass(handlerType);
        //遍历方法，对注解中的信息进行处理，得到RequestMappingInfo对象，得到methods数组
        Map<Method, T> methods = MethodIntrospector.selectMethods(userType,
                                                                  (MethodIntrospector.MetadataLookup<T>) method -> {
      return getMappingForMethod(method, userType);
         });
        //遍历methods[Method,{path}]
        for (Map.Entry<Method, T> entry : methods.entrySet()) {
            //对方法的可访问性进行校验，如private，static，SpringProxy
            Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
            //获取最终请求路径
            T mapping = entry.getValue();
            //注册
            registerHandlerMethod(handler, invocableMethod, mapping);
        }
    }
}
```

mapping对象的属性：

![image-20200504172052436](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200504172052436.png)

methods对象中存储的元素：

![image-20200504170639253](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200504170639253.png)

注册方法在AbstractHandlerMethodMapping中实现：

```java
public void register(T mapping, Object handler, Method method) {
    this.readWriteLock.writeLock().lock();
    try {
        //处理方法的对象
        HandlerMethod handlerMethod = createHandlerMethod(handler, method);
        //判断映射的唯一性
        assertUniqueMethodMapping(handlerMethod, mapping);
		//将mapping信息和控制器方法对应
        this.mappingLookup.put(mapping, handlerMethod);
		//将path与处理器映射（一个方法可能可以处理多个url）
        List<String> directUrls = getDirectUrls(mapping);
        for (String url : directUrls) {
            this.urlLookup.add(url, mapping);
        }
		//控制器名的大写英文缩写#方法名
        String name = null;
        if (getNamingStrategy() != null) {
            name = getNamingStrategy().getName(handlerMethod, mapping);
            addMappingName(name, handlerMethod);
        }
		//跨域请求相关配置
        CorsConfiguration corsConfig = initCorsConfiguration(handler, method, mapping);
        if (corsConfig != null) {
            this.corsLookup.put(handlerMethod, corsConfig);
        }
		//将所有配置统一注册到registry中
        this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directUrls, name));
    }
    finally {
        this.readWriteLock.writeLock().unlock();
    }
}
```

至此，所有的Controller，以及其中标注了@RequestMapping注解的方法，都被一一解析，注册进HashMap中，于是，对应请求路径与处理方法就一一匹配，此时HandlerMapping也初始化完成。

# 三、请求响应处理

## 1. 请求分发

我们需要明确的一个点是，请求过来的时候，最先执行的地方在哪，是Servlet的service方法，我们只需要看看该方法在子类中的一个实现即可：

FrameworkServlet重写的service方法：

```java
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//获取请求方法
		HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
        //拦截PATCH请求
		if (HttpMethod.PATCH == httpMethod || httpMethod == null) {
			processRequest(request, response);
		}
		else {
			super.service(request, response);
		}
	}
```

其实最后都是调用了processRequest方法，该方法中又调用了真正的doService()方法，其中细节先不探讨，我们直奔，看看DispatcherServlet的这个doService干了哪些事情（DispatcherServlet这个类确实是核心中的核心，既建立了IoC容器，又负责请求分发）：

```java
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
	//忽略一大串前期准备，使其能够处理view 对象
    //接着进入真正的分发
	doDispatch(request, response);
	}
```

doService：

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request;
    HandlerExecutionChain mappedHandler = null;
    boolean multipartRequestParsed = false;

    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

    try {
        ModelAndView mv = null;
        Exception dispatchException = null;

        try {
            //如果是文件上传请求，对request进行包装，如果不是就原样返回
            processedRequest = checkMultipart(request);
            //文件上传请求标识符
            multipartRequestParsed = (processedRequest != request);

            //为当前的request请求寻找合适的handler
            mappedHandler = getHandler(processedRequest);
            //如果没有handler可以处理该请求，就跳转到错误页面
            if (mappedHandler == null) {
                noHandlerFound(processedRequest, response);
                return;
            }
            //为当前的request请求寻找合适的adaptor
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

            String method = request.getMethod();
            boolean isGet = "GET".equals(method);
            //判断是否支持getLastModified，如果不支持，返回-1
            if (isGet || "HEAD".equals(method)) {
                long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                if (logger.isDebugEnabled()) {
                    logger.debug("Last-Modified value for [" + getRequestUri(request) + "] is: " + lastModified);
                }
                if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
                    return;
                }
            }
            if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                return;
            }
            // 真正处理请求的方法
            mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

            if (asyncManager.isConcurrentHandlingStarted()) {
                return;
            }

            applyDefaultViewName(processedRequest, mv);
            mappedHandler.applyPostHandle(processedRequest, response, mv);
        }
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
    
}
```



## 2. 参数解析

## 3. 返回值解析

## 4. 视图解析

Map、String、ModelMap --> bindMap

最后返回的都是ModelAndView对象，包含了逻辑名和模型对象的视图。

>  视图解析器 viewResolver --实例化 --> view（无状态的，不会有线程安全问题）

### 视图解析器

视图解析器（实现ViewResolver接口）：将逻辑视图解析为具体的视图对象。

![image-20200506192115248](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200506192115248.png)

每个视图解析器都实现了Ordered接口，并开放order属性，order越小优先级越高。

按照视图解析器的优先顺序对逻辑视图名进行解析，直到解析成功并返回视图对象，否则抛出异常。

### 视图

视图（实现View接口）：渲染模型数据，将模型数据以某种形式展现给用户。

![image-20200506192330201](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200506192330201.png)

最终采取的视图对象对模型数据进行渲染render，处理器并不关心，处理器关心生产模型的数据，实现解耦。