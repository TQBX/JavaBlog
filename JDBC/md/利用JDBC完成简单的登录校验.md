以下参考自黑马培训视频的一个很基本的案例，通过JDBC连接数据库，完成简单的登录校验。

结合自己之前在网上看到的一些管理系统的步骤，感觉之后会用到类似的，特此整理一下。

一、将配置文件`jdbc.properties`放至src目录下，配置相关信息：url，user，password，driver等。

二、封装一个工具类`JDBCUtils`。

```java
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

/**
 * @auther Summerday
 * 封装JDBC工具类
 */
public class JDBCUtils {
    private static String url;          //数据库url
    private static String user;         //数据库用户名
    private static String password;     //数据库密码
    private static String driver;       //驱动类名
    /**
     * 文件的读取只需要一次，放在静态代码块中
     */
    static {
        try {
            //创建Properties对象
            Properties prop = new Properties();

            //获取src路径下的文件的方式 --> ClassLoader
            ClassLoader classLoader = JDBCUtils.class.getClassLoader();
            URL resource = classLoader.getResource("jdbc.properties");
            String path = resource.getPath();

            //加载文件
            prop.load(new FileReader(path));

            //通过Properties获取属性
            JDBCUtils.url = prop.getProperty("url");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
            driver = prop.getProperty("driver");

            //注册驱动
            Class.forName(driver);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取连接
     * @return 返回连接对象
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }

    /**
     * 释放资源
     * @param statement 想要释放的执行对象
     * @param conn      想要释放的连接对象
     */

    public static void closeConnection(Statement statement, Connection conn) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * 查询语句释放资源
     *
     * @param resultSet 想要释放的结果集
     * @param statement 想要释放的执行对象
     * @param conn      想要释放的连接对象
     */
    public static void closeConnection(ResultSet resultSet, Statement statement, Connection conn) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        closeConnection(statement, conn);
    }
}
```

三、封装一个String工具类`StringUtils`，之后有判空，判相等关于字符串的操作，可以继续在里面定义静态工具方法。

```java
/**
 * @auther Summerday
 * 
 * String工具类
 */
public class StringUtils {
    //判断数据是否为空的工具方法
    public static boolean isEmpty(String str){
        return (str == null||str.isEmpty());
    }

}
```

四、接下来是一个简易的校验登录的功能方法。

```java
    /**
     * 判断是否能够登录成功的方法
     * @param username  用户名
     * @param password  密码
     * @return  能成功登录，返回true
     */
    private static boolean loginAgain(String username,String password){
        //判断输入数据是否为空
        if(StringUtils.isEmpty(username)||StringUtils.isEmpty(password)){
            System.out.println("输入数据不能为空！");
            return false;
        }
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        //登录注册+获取连接
        try {
            conn = JDBCUtils.getConnection();

            //定义sql语句
            String sql = "select * from user where username = ? and password = ?";

            //获取preparedStatement对象，防止sql注入
            preparedStatement = conn.prepareStatement(sql);
            //填充占位符的值
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            //获取结果集
            resultSet = preparedStatement.executeQuery();

            //如果结果集有解，则登录成功。
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //关闭数据库连接，回收资源
            JDBCUtils.closeConnection(resultSet,preparedStatement,conn);
        }
        return false;
    }
```

