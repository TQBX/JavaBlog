一、首先读取类路径下的配置文件，获取其字节输入流。

二、创建SqlSessionFactoryBuilder对象，调用内部的build方法。`factory = new SqlSessionFactoryBuilder().build(in);`

三、根据字节输入流创建XMLConfigBuilder即解析器对象parser。`XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);`

```java
  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
      //根据字节输入流创建XMLConfigBuilder即解析器对象parser
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      //返回的Configuration配置对象作为build的参数
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }
```



四、调用parser对象的parse方法，`parser.parse()`，该结果将返回一个Configuration配置对象，作为build方法的参数。

五、parse()方法中，调用parseConfiguration方法将Configuration元素下的所有配置信息封装进Parser对象的成员Configuration对象之中。

```java
  public Configuration parse() {
    if (parsed) {
      throw new BuilderException("Each XMLConfigBuilder can only be used once.");
    }
    parsed = true;
      //将configuration的配置信息一一封装到configuration中
    parseConfiguration(parser.evalNode("/configuration"));
    return configuration;
  }
```

六、其中进行解析xml元素的方式是将通过evalNode方法获取对应名称的节点信息。如：`parseConfiguration(parser.evalNode("/configuration"));`，此时`parser.evalNode("/configuration")`即为Configuration下的所有信息。

七、parseConfiguration方法相当于将里面每个元素的信息都单独封装到Configuration中。

![image-20200418095909926](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418095909926.png)

值得一提的是，我们之后要分析基于代理模式产生dao的代理对象涉及到mappers的封装，其实也在配置文件读取封装的时候就已经完成，也就是在parseConfiguration方法之中：`mapperElement(root.evalNode("mappers"));`。他的作用就是，读取我们主配置文件中`<mappers>`的元素内容，也就是我们配置的映射配置文件。

```xml
    <!-- 配置映射文件的位置 -->
    <mappers>
        <package name="com.smday.dao"></package>
    </mappers>
```

`private void mapperElement(XNode parent)`方法将mappers配置下的信息获取，此处获取我们resources包下的com.smday.dao包名。

![image-20200418123936750](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418123936750.png)

接着就调用了configuration的addMappers方法，其实还是调用的是mapperRegistry。

```java
  public void addMappers(String packageName) {
    mapperRegistry.addMappers(packageName);
  }
```

读到这里，我们就会渐渐了解MapperRegistry这个类的职责所在，接着来看，这个类中进行的一些工作，在每次添加mappers的时候，会利用ResolverUtil类查找类路径下的该包名路径下，是否有满足条件的类，如果有的话，就将Class对象添加进去，否则报错。

![image-20200418125313685](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418125313685.png)

![image-20200418125818344](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418125818344.png)

紧接着，就到了一步比较重要的部分，当然只是我个人觉得，因为第一遍看的时候，我没有想到，这步居然可以封装许许多多的重要信息，我们来看一看：

```java
  public <T> void addMapper(Class<T> type) {
    if (type.isInterface()) {
      //如果已经绑定，则抛出异常
      if (hasMapper(type)) {
        throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
      }
      boolean loadCompleted = false;
      try {
        //将接口类作为键，将MapperProxyFactory作为值存入
        knownMappers.put(type, new MapperProxyFactory<T>(type));
        // 在运行解析器之前添加类型十分重要，否则可能会自动尝试绑定映射器解析器
        // 如果类型已知，则不会尝试
        MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
        //解析mapper映射文件，封装信息
        parser.parse();
        loadCompleted = true;
      } finally {
        if (!loadCompleted) {
          knownMappers.remove(type);
        }
      }
    }
  }
```

映射配置文件的读取依靠namespace，我们可以通过查看源码发现读取映射配置文件的方法是loadXmlResouce()，所以namespace命名空间至关重要：

```java
  private void loadXmlResource() {
    // Spring may not know the real resource name so we check a flag
    // to prevent loading again a resource twice
    // this flag is set at XMLMapperBuilder#bindMapperForNamespace
    // 防止加载两次，可以发现这句 判断在许多加载资源文件的时候出现
    if (!configuration.isResourceLoaded("namespace:" + type.getName())) {
      String xmlResource = type.getName().replace('.', '/') + ".xml";
      InputStream inputStream = null;
      try {
        inputStream = Resources.getResourceAsStream(type.getClassLoader(), xmlResource);
      } catch (IOException e) {
        // ignore, resource is not required
      }
      if (inputStream != null) {
        XMLMapperBuilder xmlParser = new XMLMapperBuilder(inputStream, assistant.getConfiguration(), xmlResource, configuration.getSqlFragments(), type.getName());
        //最终解析
        xmlParser.parse();
      }
    }
  }
```

```java
  //xmlPaser.parse()
  public void parse() {
    if (!configuration.isResourceLoaded(resource)) {
      //读取映射配置文件信息的主要代码
      configurationElement(parser.evalNode("/mapper"));
      //加载完成将该路径设置进去，防止再次加载
      configuration.addLoadedResource(resource);
      bindMapperForNamespace();
    }

    parsePendingResultMaps();
    parsePendingCacheRefs();
    parsePendingStatements();
  }
```

![image-20200418135529157](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418135529157.png)

可以看到，对映射文件解析之后，mappedStatements对象中出现了以下内容：

![image-20200418131851000](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418131851000.png)

至此，主配置文件和映射配置文件的配置信息就已经读取完毕。

八、最后依据获得的Configuration对象，创建一个`new DefaultSqlSessionFactory(config)`。

```java
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }
```

总结：

- 解析配置文件的信息，并保存在Configuration对象中。

- 返回包含Configuration的DefaultSqlSession对象。