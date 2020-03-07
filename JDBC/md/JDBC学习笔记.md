```java
public class JDBCDemo1 {
    public static void main(String[] args) throws Exception{

        /*1. 导入驱动jar包 mysql-connector-java-5.1.37-bin.jar

            - 复制对应数据库jar包到项目的libs目录下
            - 右键libs，add library
         */

        //2. 注册驱动
        Class.forName("com.mysql.jdbc.Driver");
        //3. 获取数据库连接对象
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb2", "root", "123456");
        //4. 定义sql语句
        String sql = "update orders set price = 500 where id = 1";
        //3. 获取执行sql的对象 Statement
        Statement stmt = conn.createStatement();
        //4. 执行sql
        int count = stmt.executeUpdate(sql);
        //5. 处理结果
        System.out.println(count);
        //6. 释放资源
        stmt.close();
        conn.close();

    }
}
```



详解对象

`DriverManager`：注册驱动对象



```java
public class Driver extends NonRegisteringDriver implements java.sql.Driver {
    //
    // Register ourselves with the DriverManager
    //
    static {
        try {
            java.sql.DriverManager.registerDriver(new Driver());
        } catch (SQLException E) {
            throw new RuntimeException("Can't register driver!");
        }
    }

    /**
     * Construct a new driver and register it with DriverManager
     * 
     * @throws SQLException
     *             if a database error occurs.
     */
    public Driver() throws SQLException {
        // Required for Class.forName().newInstance()
    }
}
```



