下面这句话意思非常明了，就是通过传入接口类型对象，获取接口代理对象。

`IUserDao userDao1 = sqlSession1.getMapper(IUserDao.class);`

具体的过程如下：

一、首先，调用SqlSession的实现类DefaultSqlSession的getMapper方法，其实是在该方法内调用configuration的getMapper方法，将接口类对象以及当前sqlsession对象传入。

```java
  //DefaultSqlSession.java
  @Override
  public <T> T getMapper(Class<T> type) {
    //调用configuration的getMapper
    return configuration.<T>getMapper(type, this);
  }

```

二、接着调用我们熟悉的mapperRegistry，因为我们知道，在读取配置文件，创建sqlSession的时候，接口类型信息就已经被存入到其内部维护的Map之中。

```java
  //Configuration.java
  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    
    return mapperRegistry.getMapper(type, sqlSession);
  }
```

![image-20200418112653674](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418112653674.png)

三、我们来看看getMapper方法具体的实现如何：

```java
  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    //根据传入的类型获取对应的键，也就是这个代理工厂
    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
    if (mapperProxyFactory == null) {
      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    }
    try {
      //最终返回的是代理工厂产生的一个实例对象
      return mapperProxyFactory.newInstance(sqlSession);
    } catch (Exception e) {
      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
  }
```

四、紧接着，我们进入MapperProxyFactory，真真实实地发现了创建代理对象的过程。

```java
  protected T newInstance(MapperProxy<T> mapperProxy) {
    //创建MapperProxy代理对象
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }

  public T newInstance(SqlSession sqlSession) {
    //MapperProxy是代理类，
    final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
    return newInstance(mapperProxy);
  }
```

![image-20200418143656732](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418143656732.png)

