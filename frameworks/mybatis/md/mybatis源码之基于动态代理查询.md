```java
User user1 = userDao1.findById(41);
```

一、动态代理：执行代理对象的方法时拦截，进行方法增强。

```java
 /**
 * 作用:执行被代理对象的任何接口方法都会经过该方法
 * @param proxy : 代理对象的引用
 * @param method : 当前执行的方法
 * @param args : 当前执行方法所需的参数
 * @return : 和被代理对象有相同的返回值
 * @throws Throwable
 */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      //判断当前方法的类型是不是Object
      if (Object.class.equals(method.getDeclaringClass())) {
        //如果是的话，直接调用该方法并返回
        return method.invoke(this, args);
      } else if (isDefaultMethod(method)) {
        //判断该方法是不是default方法
        return invokeDefaultMethod(proxy, method, args);
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
    //对msqlcommand和method进行封装，并以method：mapperMethod的形式加入methodCache
    final MapperMethod mapperMethod = cachedMapperMethod(method);
    //返回mapperMethod的execute的返回结果
    return mapperMethod.execute(sqlSession, args);
  }
```

可以看看这个MapperMethod具体是个啥玩意儿：

```java
  //缓存思想的体现
  private MapperMethod cachedMapperMethod(Method method) {
    //从methodCache这个Map中取method对应的mapperMethod
    MapperMethod mapperMethod = methodCache.get(method);
    //如果里面没有，就创建一个
    if (mapperMethod == null) {
      mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
      //以method：mapperMethod的形式加入methodCache
      methodCache.put(method, mapperMethod);
    }
    //如果有就直接返回
    return mapperMethod;
  }
```

MapperMethod的构造器，sqlCommand和methodSignature是他的两个静态内部类：

```java
  public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
    this.command = new SqlCommand(config, mapperInterface, method);
    this.method = new MethodSignature(config, mapperInterface, method);
  }
```

![image-20200418150245513](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418150245513.png)

二、接着执行MapperMethod对象的execute方法，其实源码还是通俗易懂的，无非就是按照不同的sql语句的类别进行不同的数据结果的封装，值得注意的是，insert，update和delete其实底层都是调用了update方法，但为了语义清晰，所以区分类别。

之前command封装了sql语句的类别，我们这是`SELECT`对吧，

```java
  public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    switch (command.getType()) {
      case INSERT: {
    	Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.insert(command.getName(), param));
        break;
      }
      case UPDATE: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.update(command.getName(), param));
        break;
      }
      case DELETE: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.delete(command.getName(), param));
        break;
      }
      case SELECT:
        if (method.returnsVoid() && method.hasResultHandler()) {
          executeWithResultHandler(sqlSession, args);
          result = null;
        } else if (method.returnsMany()) {
          result = executeForMany(sqlSession, args);
        } else if (method.returnsMap()) {
          result = executeForMap(sqlSession, args);
        } else if (method.returnsCursor()) {
          result = executeForCursor(sqlSession, args);
        } else {
          //将Args转换为SqlCommand参数，简单理解就是获取了参数41，这里就不深入了
          Object param = method.convertArgsToSqlCommandParam(args);
          //调用selectOne方法，这部分可以发现，无论是使用代理dao还是定义sqlsession实现类，本质上都调用了这些方法，因为这里的command。getName就是具体定义的sql的namespace.id
          result = sqlSession.selectOne(command.getName(), param);
        }
        break;
      case FLUSH:
        result = sqlSession.flushStatements();
        break;
      default:
        throw new BindingException("Unknown execution method for: " + command.getName());
    }
    if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
      throw new BindingException("Mapper method '" + command.getName() 
          + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
    }
    return result;
  }
```

三、当然本例以findById为例，这里调用的是SelectOne方法，接收statement和parameter。

```java
  @Override
  public <T> T selectOne(String statement, Object parameter) {
   	//根据参数select List
    List<T> list = this.<T>selectList(statement, parameter);
    if (list.size() == 1) {
      //获取列表的一个元素
      return list.get(0);
    } else if (list.size() > 1) {
      //个数超过一抛出异常
      throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
    } else {
      //个数为0返回null
      return null;
    }
  }
```

四、调用selectList的方法，实现如下：

```java
  @Override
  public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
    try {
      //获取MappedStatement
      MappedStatement ms = configuration.getMappedStatement(statement);
      return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
```

五、获取MappedStatement对象，该对象代表一个增删改查标签的详细信息。

![image-20200418171239644](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418171239644.png)

六、执行executor.query(ms,xxx,x)方法，首先执行实现类中的方法，获取boundsql，该对象包含sql的具体信息。

十一、执行SimpleExecutor的doQuery方法，创建PreparedStatementHandler对象，通过该handler对象执行query方法









参考文章：关于一级和二级缓存，[你真的会用Mybatis的缓存么，不知道原理的话，容易踩坑哦](https://www.jianshu.com/p/c553169c5921)

