一、调用SqlSessionFactory对象的openSession方法，其实是调用`private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit)`方法，通过参数就可以知道，分别是执行器的类型，事务隔离级别和设置是否自动提交，因此，我们就可以得知，我们在创建SqlSession的时候可以指定这些属性。

```java
  private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
      //获取Environment信息
      final Environment environment = configuration.getEnvironment();
      //获取TransactionFactory信息
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      //创建Transaction对象
      tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
      //创建执行器对象Executor
      final Executor executor = configuration.newExecutor(tx, execType);
      //创建DefaultSqlSession对象并返回
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      closeTransaction(tx); // may have fetched a connection so lets call close()
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
```



二、从configuration中获取environment、dataSource和transactionFactory信息，创建事务对象Transaction。

![image-20200418103818736](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200418103818736.png)

补充：后续看了一些博客，说是保证executor不为空，因为defaultExecutorType有可能为空。

三、根据配置信息，执行器信息和自动提交信息创建DefaultSqlSession。

