还是以一个简单的查询作为切入，由于很多部分前面已经唠叨了一些篇幅，为了可读，这篇将会对源码做适当删减。

```java
//利用代理对象调用查询方法。
User user1 = userDao1.findById(41);
```

我们知道，此处userDao1已经是一个代理对象，因此基于动态代理模式，将会调用代理对象的invoke方法。

```java
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      //判断它是否为类
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, args);
          //判断是否是default方法
      } else if (isDefaultMethod(method)) {
        return invokeDefaultMethod(proxy, method, args);
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
    //通过cachedMapperMethod（对method的缓存）对MapperMethod对象进行初始化
    final MapperMethod mapperMethod = cachedMapperMethod(method);
    //执行execute方法，将sqlSession和当前运行的参数传入
    return mapperMethod.execute(sqlSession, args);
  }
```

