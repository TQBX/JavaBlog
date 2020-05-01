[toc]

```java
ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
```

父类抽象类AbstractApplicationContext的静态代码块：

```java
	static {
		// Eagerly load the ContextClosedEvent class to avoid weird classloader issues
		// on application shutdown in WebLogic 8.1. (Reported by Dustin Woods.)
		ContextClosedEvent.class.getName();
	}
```

构造器创建ClassPathXmlApplicationContext

# FactoryBean和BeanFactory

BeanFactory是一个接口类，定义了IoC容器的基本功能规范。

![BeanFactory](E:\1JavaBlog\frameworks\Spring\pic\BeanFactory.png)

## 使用转义符得到FactoryBean

我们在使用容器时，可以通过转义符`&`来得到FactoryBean本身。

具体的源码在BeanFatoryUtils中：

```java
	public static String transformedBeanName(String name) {
		Assert.notNull(name, "'name' must not be null");
        //判断是不是有一个&代表FactoryBean
		if (!name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
			return name;
		}
        //如果有的话，转化一下
		return transformedBeanNameCache.computeIfAbsent(name, beanName -> {
			do {
				beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
			}
			while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
			return beanName;
		});
	}
```

```java
    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        //得到的是beautyFactoryBean本身
        BeautyFactoryBean factoryBean = (BeautyFactoryBean) ac.getBean("&beautyFactoryBean");
        //调用本身的getObject方法获取对象
        Beauty object1 = factoryBean.getObject();
        
        //得到的是beautyFactoryBean生产的对象
        Beauty object2 = (Beauty) ac.getBean("beautyFactoryBean");
        //false
        System.out.println(object1 == object2);
    }
```

BeanFactory是Factory，也就是IoC容器或者对象工厂，spring中所有的bean都是由BeanFactory所管理的。

FactoryBean是bean，而且是一个可以产生或者修饰对象生成的工厂bean。

## BeanFactory包含的方法

![image-20200501161709533](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200501161709533.png)

我们可以通过这些方法，人为地进行一些判断，比如看看这个`user`这个bean在容器中存不存在啊，看看`user`这个bean是不是单例啊等等。

## FactoryBean包含的方法

![image-20200501162855747](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200501162855747.png)

我们可以自定义类实现该接口，以达到创建对象的目的。

# ApplicationContext的特点

高级形态的IoC容器，在BeanFactory的基础上添加了附加功能：

- 支持不同的信息源：扩展了MessageSource接口，支持国际化实现。
- 支持从不同IO途径获取bean的定义信息，扩展了ResourceLoader接口。
- 支持应用事件，扩展了ApplicationEventPublisher接口，可以在上下文引入事件机制。
- 支持其他附加服务，相比于BeanFactory，ApplicationContext功能更加丰富。

# IoC容器的初始化

- BeanDefinition的Resource定位

- BeanDefinition载入：将bean转化为IoC容器内部的数据结构，也就是BeanDefinition。

- BeanDefinition注册：

---

BeanDefinition的Resource定位

```java
//允许指定多个configLocation，还可以指定双亲IoC容器parent，从给定的xml文件中，加载definition。
public ClassPathXmlApplicationContext(
    String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
    throws BeansException {
	//最后通过ResourcePatternResolver读取Resource
    super(parent);
    setConfigLocations(configLocations);
    if (refresh) {
        //载入BeanDefinition
        refresh();
    }
}
```

AbstractApplicationContext的refresh()方法官方解释是这样的：

> Load or refresh the persistent representation of the configuration, which might an XML file, properties file, or relational database schema.
>
> As this is a startup method, it should destroy already created singletons if it fails, to avoid dangling resources. In other words, after invocation of that method, either all or no singletons at all should be instantiated.

大致翻译一下，

- 加载或刷新配置的持久表示，它可以是XML文件、properties文件或关系数据库模式。

- 由于这是一种启动方法，如果失败，它应该销毁已经创建的单例，以避免挂起资源。
- 换句话说，在调用该方法之后，要么成功实例化所有单例，要么失败，一个都不实例化。

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        // Prepare this context for refreshing.
        prepareRefresh();

        // 告知子类在这里启动refreshBeanFactory，也就是刷新内部的beanFactory
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // Prepare the bean factory for use in this context.
        prepareBeanFactory(beanFactory);

        try {
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);

            // Invoke factory processors registered as beans in the context.
            invokeBeanFactoryPostProcessors(beanFactory);

            // Register bean processors that intercept bean creation.
            registerBeanPostProcessors(beanFactory);

            // Initialize message source for this context.
            initMessageSource();

            // Initialize event multicaster for this context.
            initApplicationEventMulticaster();

            // Initialize other special beans in specific context subclasses.
            onRefresh();

            // Check for listener beans and register them.
            registerListeners();

            // Instantiate all remaining (non-lazy-init) singletons.
            finishBeanFactoryInitialization(beanFactory);

            // Last step: publish corresponding event.
            finishRefresh();
        }

        catch (BeansException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Exception encountered during context initialization - " +
                            "cancelling refresh attempt: " + ex);
            }

            // Destroy already created singletons to avoid dangling resources.
            destroyBeans();

            // Reset 'active' flag.
            cancelRefresh(ex);

            // Propagate exception to caller.
            throw ex;
        }

        finally {
            // Reset common introspection caches in Spring's core, since we
            // might not ever need metadata for singleton beans anymore...
            resetCommonCaches();
        }
    }
}

protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
    //刷新内部的beanFactory
    refreshBeanFactory();
    return getBeanFactory();
}
```

我们继续进入AbstractRefreshableApplicationContext.java的refreshBeanFactory方法，一探究竟：

```java
	@Override
	protected final void refreshBeanFactory() throws BeansException {
        //如果之前已经有容器存在
		if (hasBeanFactory()) {
            //就销毁
			destroyBeans();
            //关闭
			closeBeanFactory();
		}
		try {
            //如果之前不存在容器，就创建一个DefaultListableBeanFactory
			DefaultListableBeanFactory beanFactory = createBeanFactory();
            //为该工厂指定一个序列化id，如果需要的话，可以通过id反序列化得到工厂
			beanFactory.setSerializationId(getId());
            //定制内部工厂
			customizeBeanFactory(beanFactory);
            //这个部分就是关键部分：载入Beandefination，
			loadBeanDefinitions(beanFactory);
			synchronized (this.beanFactoryMonitor) {
				this.beanFactory = beanFactory;
			}
		}
		catch (IOException ex) {
			throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
		}
	}
```

AbstractXmlApplicationContext中的loadBeanDefinitions方法：

```java
    @Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		// 为beanFactory创建读取器
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

		// 使用此上下文的资源加载环境 配置beanDefinition的读取器。
		beanDefinitionReader.setEnvironment(this.getEnvironment());
		beanDefinitionReader.setResourceLoader(this);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

		//允许子类提供读取器的自定义初始化，接着加载bean definition
		initBeanDefinitionReader(beanDefinitionReader);
		loadBeanDefinitions(beanDefinitionReader);
	}
```

```java
//使用给定的XmlBeanDefinitionReader加载beanDefinition
protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
    Resource[] configResources = getConfigResources();
    if (configResources != null) {
        reader.loadBeanDefinitions(configResources);
    }
    String[] configLocations = getConfigLocations();
    if (configLocations != null) {
        //调用父类的loadBeanDefinitions
        reader.loadBeanDefinitions(configLocations);
    }
}
```

- 在初始化ApplicationContext的过程中，最主要关注的就是refresh()，它启动了BeanDefinition的载入。
- 实际使用的IoC容器就是DefaultListableBeanFactory，里面存储Bean definition的种种信息。
- Spring对应不同的BeanDefinition措施不同，主要提供了几个BeanDefinitionReader来帮助载入容器。

![BeanDefinitionReader](E:\1JavaBlog\frameworks\Spring\pic\BeanDefinitionReader.png)

```java
	//AbstractBeanDefinitionReader
	@Override
	public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
		Assert.notNull(locations, "Location array must not be null");
		int count = 0;
        //遍历所有路径，加载BeanDefinition信息
		for (String location : locations) {
			count += loadBeanDefinitions(location);
		}
		return count;
	}
```

虽然loadBeanDefinitions对location进行遍历，但其实点进去最终还是将location转为Resource对象，做出操作：`Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);`。

总之最后的最后都会调用XmlBeanDefinitionReader中的doLoadBeanDefinitions方法：

```java
	/**
	 * 这一步才是从指定的xml文件中加载bean 的定义。
	 */
	protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource)
			throws BeanDefinitionStoreException {

		try {
            //由DefaultDocumentLoader获取xml文件的Document对象
			Document doc = doLoadDocument(inputSource, resource);
            //BeanDefinition按照bean的语义进行解析并转化为容器的内部数据结构
			int count = registerBeanDefinitions(doc, resource);
			if (logger.isDebugEnabled()) {
				logger.debug("Loaded " + count + " bean definitions from " + resource);
			}
			return count;
		}//省略catch信息
	}
```

```java
	public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
        //获取BeanDefinitionDocumentReader对xml的beanDefinition进行解析
		BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
		int countBefore = getRegistry().getBeanDefinitionCount();
        //实际的解析过程
		documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
		return getRegistry().getBeanDefinitionCount() - countBefore;
	}
```

beandefinition载入过程：

1. 调用Xml的解析器得到Document对象，但是刚开始并不会按照bean的规则对其进行解析。
2. 按照bean的规则解析的过程则由DefaultBeanDefinitionDocumentReader来实现。

```
parseBeanDefinitions(root, this.delegate);
```

```java
/**
	 * Process the given bean element, parsing the bean definition
	 * and registering it with the registry.
	 */
protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
    BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
    if (bdHolder != null) {
        bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
        try {
            // Register the final decorated instance.
            BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
        }
        catch (BeanDefinitionStoreException ex) {
            getReaderContext().error("Failed to register bean definition with name '" +
                                     bdHolder.getBeanName() + "'", ele, ex);
        }
        // Send registration event.
        getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
    }
}
```

