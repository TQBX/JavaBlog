# 啥是批处理

将多条SQL添加到一个批中，一次性将批发送给数据库，数据库依次执行SQL语句，减少SQL语句发送的次数，提升程序运行的效率。

# Statement批处理

优点：

- **可以在一次批处理中处理不同语义的SQL语句**。

缺点：

- 没有预编译机制，不能防止sql注入攻击，且执行效率低。

- SQL语句无法预留在数据库服务器中，每次都是新发送一条SQL语句到达数据库，需要重新解读SQL语句。

```java
    //Statement实现批处理操作
    public static void main(String[] args) {
		//try-with-resource
        try (Connection conn = JDBCUtils.getConnection();
             Statement stat = conn.createStatement()) {
            //添加进批
            stat.addBatch("create table t1(id int,name varchar(20))");
            stat.addBatch("insert into t1 values(1,'a')");
            stat.addBatch("insert into t1 values(2,'b')");
            stat.addBatch("insert into t1 values(3,'c')");
            //执行批
            int[] counts = stat.executeBatch();
            //long[] longs = stat.executeLargeBatch();
            System.out.println(Arrays.toString(counts));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
```



> void addBatch( String sql )；添加进批，其实就是一个List中。
>
> int[] executeBatch()；执行批处理

需要注意的是，excuteBatch()不支持Select语句，不然会如下错误：

```java
java.sql.BatchUpdateException: Can not issue SELECT via executeUpdate() or executeLargeUpdate().
```

Java8对批处理的方法进行了增强：`long[] longs = stat.executeLargeBatch();`

`executeLargeBatch()`方法将返回一个long类型的数组，很好理解，因为配套的单个处理的`executeLargeUpdate()`返回的是单个的long型。如果SQL语句的返回结果大于`Integer.MAX_VALUE`的话可以用这个，当然，同样不支持select语句。

# PreparedStatement批处理

优点：

- 有**预编译机制**，可以**防止注入攻击，且执行效率较高**。
- 当发送多条结构相同的SQL时，SQL语句的骨架可以只发一次。

缺点：

- 不能在一次批处理中添加结构不同的SQL语句。

```java
    //PreparedStatement实现批处理操作
    public static void main(String[] args) {
        String sql = "insert into t1 values(?,?)";
        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement pstat = conn.prepareStatement(sql)) {
            for (int i = 0; i < 100; i++) {
                pstat.setInt(1, i);
                pstat.setString(2, i + "");
                pstat.addBatch();
                if (i % 10 == 0) {
                    //满10条 执行批处理
                    pstat.executeBatch();
                    //每10条执行完，释放
                    pstat.clearBatch();
                }
            }
            //保证所有都能执行一次
            pstat.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
```

> void clearBatch()；清空当前批中的statement。

