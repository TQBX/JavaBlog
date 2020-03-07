# JDBC概述

Java Database Connectivity：数据库连接，是一种可执行SQL语句的API。

JDBC是**面向接口编程**的典型应用：关系型数据库系统类型多样（诸如MySQL，DB2，Oracle），为了实现统一简化开发，Sun公司制定这套标准的API（接口），不同的厂商提供实现，使得依据JDBC开发的数据库程序**可以跨平台执行**，使用时只需要不同数据库的驱动程序即可。

![image-20200307195250769](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200307195250769.png)

>  大部分的数据库系统都有相应的JDBC驱动程序，当连接某个特定的数据库时，必须有相应的数据库驱动程序。

# JDBC编程步骤（以MySQL为例）

## 1、导入jar包

IDEA中，导入相应的JDBC驱动jar包到项目中，并通过`add library`添加到工作空间。

## 2、加载数据库驱动

加载数据库驱动，通常使用Class类的`forName(驱动类全类名)`加载驱动，例如MySQL中加载驱动的方法为：

```java
Class.forName("com.mysql.jdbc.Driver");
```

> 需要注意的是，最新的JDBC驱动已经可以通过SPI自动注册驱动类，这种情况下jar中的META-INF/services目录下会包含java.sql.Driver的文件，已经指定了JDBC驱动类，所以该情况下，这步其实可以省略。

![jdbc驱动](E:\1myblog\JavaBlog\JavaBlog\JDBC\pic\jdbc驱动.png)

## 3、通过DriverManager获得Connection对象

利用`DriverManager`类的静态方法`public static Connection getConnection(String url,String user, String password)`获取连接对象Connection。

传递参数：数据库URL，用户名user，密码password。

- 数据库URL的书写方式：`jdbc:mysql://服务器名或IP地址:端口号/数据库名[?参数名=参数值]`

  ```java
  Connection conn = DriverManager.getConnection(        "jdbc:mysql://localhost:3306/mydb2", "root", "123456");
  ```

- 如果服务器为本地，在MySQL中可以简写为：

  ```java
  Connection conn = DriverManager.getConnection(        "jdbc:mysql://localhost:3306/mydb2", "root", "123456");
  ```

另外getConnection还有个重载方法：`public static Connection getConnection(String url,java.util.Properties info)`，将user和password存储到Properties对象中，传入方法即可。

```java
Properties prop = new Properties();
prop.setProperty("user","root");
prop.setProperty("password","123456")

DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb2",prop);
```

## 3、定义SQL语句

```java
String sql = "update orders set price = 500 where id = 1";//普通的sql语句

//如果需要执行某些相似的sql语句多次，只有某个地方有改动，可以使用带占位符的sql语句
String sql = "select * from user where username = ? and password = ?";//带占位符的sql语句
```

##  4、利用Connection创建Statement对象

```java
//创建最基本的Statement对象
Statement statement = conn.createStatement();

//创建一个预编译sql语句的PreparedStatement对象，后者是前者的子接口。使用时需要将sql语句占位符部分进行填充。
PreparedStatement prs = conn.prepareStatement(sql);
```

### Statement与PreparedStatement：

- 在执行多次相似的sql语句时，PreparedStatement预编译SQL语句性能更好。

- PreparedStatement无需凭借SQL语句，编程更加简单（ps：确实简单）。

- PreparedStatement可以防止SQL注入。

  ```java
  //SQL注入的例子：
  String sql = "select * from user where username = '"+username+"'and password = '"+password+"'";
  resultSet = statement.executeQuery(sql);
  return resultSet.next();
  ```

  当我用Statement对象执行拼接完成之后的SQL语句，目的是为了判断是否传入正确的用户名和密码。这时，如果随便输入不存在的用户名`dede`，密码输入：`' or true or '`，这时SQL注入之后就会变成下面这个鬼样子：

  ```sql
  select * from user where username = 'dede'and password = ''or true or '';
  ```

  相当于直接输入了true，非常不合理，而PreparedStatement可以避免SQL注入：

  ```java
  //PreparedStatement防止SQL注入
  String sql = "select * from user where username = ? and password = ?";
  preparedStatement.setString(1,username);
  preparedStatement.setString(2,password);
  resultSet = preparedStatement.executeQuery();
  ```

  

  

## 5、利用Statement执行SQL语句

无论是Statement还是它的子接口对象，都拥有执行SQL语句的方法，下面是比较重要的几个：

- boolean excute()：可以执行任何的SQL语句，但是比较麻烦，通常不用。（这个方法在子接口里是没有的嗷），如果部清楚SQL语句的类型，可以使用该方法。
- int executeUpdate()：用于执行DML和DDL语句，返回结果是受影响的行数。

```java
//4. 定义sql语句
String sql = "update orders set price = 500 where id = 1";
//3. 获取执行sql的对象 Statement
Statement stmt = conn.createStatement();
//4. 执行sql
int count = stmt.executeUpdate(sql);
```

- ResultSet executeQuery()：只能执行查询语句，返回结果是一个ResultSet结果集。

```java
PreparedStatement preparedStatement = null;
//定义sql语句
String sql = "select * from user where username = ? and password = ?";
//获取PreparedStatement对象
preparedStatement = conn.prepareStatement(sql);
//给对应的占位符赋值,如果不清楚参数类型可以使用setObject()传入参数
preparedStatement.setString(1,username);
preparedStatement.setString(2,password);
//执行查询语句，获取结果集
ResultSet resultSet = preparedStatement.executeQuery();
```

## 6、操作ResultSet对象

明确一点，ResultSet对象是通过`excuteQuery`执行查询语句的结果。原理类似于迭代器，通过指针的移动来获取值。

典型的方法：

- next()：指针一开始指向第一行之前，指针向下移动，返回一个boolean值，代表后面是否有记录，有就返回true，没有就返回false。所以判断是否存在记录可以直接用`ResultSet.next();`判断。

- getXxx()：用以获取指定行，特定列的值。有许多的重载方法：包括传递指定列数（从1开始，如`getInt(1)`代表获取该行第一列的INT值），指定参数（传递列名，如`getInt("id")`，获取改行id字段的值）。

```java
//打印所有记录
while(resultSet.next()) {
    //获取数据
    int id = resultSet.getInt(1);
    String product = resultSet.getString("product");
    int price = resultSet.getInt("price");

    System.out.println(id + "--" + product + "--" + price);
}
```

## 7、回收数据库资源

回收数据库资源有两种方式：

Connection、Statement、ResultSet都继承了AutoCloseable接口，都可以使用Java7的`try-with-resources`的**自动关闭资源**的try语句来关闭：

```java
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb2", "root", "123456");
                Statement stmt = conn.createStatement())
        {
            String sql = "update orders set price = 500 where id = 1";
            int count = stmt.executeUpdate(sql);
            System.out.println(count);
        }
    }
```

还有就是传统的try……catch语句：

```java
    public static void main(String[] args){
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb2", "root", "123456");
            stmt = conn.createStatement();
            String sql = "update orders set price = 500 where id = 1";
            int count = stmt.executeUpdate(sql);
            System.out.println(count);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //避免空指针异常
            if(stmt!=null){
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
```

