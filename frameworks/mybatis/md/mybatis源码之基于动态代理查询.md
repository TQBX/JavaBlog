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
      //判断它是否为类
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

三、当然本例以findById为例，这里调用的是SelectOne方法，接收`com.smday.dao.IUserDao.findById`和`41`。

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
      //wrapCollection方法是对集合类型或者数组类型的参数做特殊处理
      //通过执行器调用query方法
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

六、默认执行CachingExecutor.query(ms,xxx,x)方法，获取boundsql，该对象包含sql的具体信息，创建缓存key。

![image-20200425124505206](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200425124505206.png)

七、先去二级缓存中查询数据，如果二级缓存中没有，则去一级缓存（localCache）中查询，接着数据库（queryFromDatabase）一条龙服务，这部分就不赘述了。最终调用的是Executor的doQuery方法，`list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);`。

八、创建StatementHandler对象，默认为PreparedStatementHandler，用以操作statement执行操作。

> ps:StatementHandler定义了一些主要的方法：预编译相关prepare、查询query、设置参数parameterize等等。

```java
  @Override
  public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
      //从mappedStatement中获取配置信息对象
      Configuration configuration = ms.getConfiguration();
      //创建StatementHandler对象，处理sql语句的对象，默认为PreparedStatementHandler
      StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
      //创建prepareStatement对象
      stmt = prepareStatement(handler, ms.getStatementLog());
      return handler.<E>query(stmt, resultHandler);
    } finally {
      closeStatement(stmt);
    }
  }
```

```java
  public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
      //RoutingStatementHandler并不是真实的服务对象，将会通过适配器模式找到对应的Statementhandler
    StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
      //拦截链对方法进行拦截
    statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
    return statementHandler;
  }
```

> Executor和Statement分为三种：Simple、Prepared、Callable。
>
> SqlSession四大对象在创建的时候都会被拦截器进行拦截，我们之后再做学习。

九、在创建StatementHandler的时候，我们会发现，它还初始化创建了另外两个重要的对象：

```java
//用于参数处理 
this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
//用于封装结果集
this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, rowBounds, parameterHandler, resultHandler, boundSql);
```

十、在创建prepareStatement对象的时候，其实还通过parameterHandler的prepare()对statement进行了参数的预编译：

```java
  private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    Statement stmt;
    Connection connection = getConnection(statementLog);
      //预编译（基础配置）
    stmt = handler.prepare(connection, transaction.getTimeout());
      //设置参数
    handler.parameterize(stmt);
    return stmt;
  }

//statementhandler的方法
public Statement prepare(Connection connection, Integer transactionTimeout)
    Statement statement = null;
	//预编译
    statement = instantiateStatement(connection);
	//设置超时
    setStatementTimeout(statement, transactionTimeout);
	//设置获取最大行数
    setFetchSize(statement);
    return statement;
```

还通过`handler.parameterize(stmt);`对参数进行设置，最终通过parameterHandler的setParameters的方法实现了该操作，其中还创建TypeHandler对象完成数据库类型和javaBean类型的映射。

```java
  @Override
  public void setParameters(PreparedStatement ps) {
	  //。。。省略对value值的操作
      //创建TypeHandler对象完成数据库类型和javaBean类型的映射
      TypeHandler typeHandler = parameterMapping.getTypeHandler();
      JdbcType jdbcType = parameterMapping.getJdbcType();
      if (value == null && jdbcType == null) {
          jdbcType = configuration.getJdbcTypeForNull();
      }
      //设置参数
      typeHandler.setParameter(ps, i + 1, value, jdbcType);
  }
```

十一、获取了ps参数之后，就可以执行statementHandler的query方法进行查询了

```java
  //PreparedStatementHandler.java  
  @Override
  public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
    //转为PreparedStatement对象
    PreparedStatement ps = (PreparedStatement) statement;
    ps.execute();
    //利用结果集处理对象对结果集进行处理：封装并返回。
    return resultSetHandler.<E> handleResultSets(ps);
  }
```

总结：

> 反射技术运用广泛，基于反射的动态代理模式使我们操作的不再是真实的服务，而是代理对象，正是基于动态代理，mybatis可以在真实对象的基础上，提供额外的服务，我们也可以利用这一特性去自定义一些类，满足我们的需求。

- 通过动态代理调用代理对象的方法。
- 通过sqlSession执行sql操作的方法：insert|delete|select|update

- 利用Executor对象对其他三大对象进行调度。
- PreparedStatementHandler对sql进行预编译，并进行了基础配置，接着设置参数，并执行sql语句。
- ParameterHandler负责对参数进行设置，其中TypeHandler负责数据库类型和javabean类型的映射。
- 最后查询结果由ResultHandler封装。







