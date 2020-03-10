# JDBC事务支持

JDBC连接的事务支持由Connection对象提供，且默认自动提交，即默认事务是关闭的状态。也就是说，SQL语句执行将会立即提交至数据库，永久生效。

JDBC中关于事务的操作：

```java
conn.setAutoCommit(false);	//关闭自动提交，开启事务

/*下面俩需要在开启事务之后才能使用*/
conn.commit();				//提交事务
conn.rollback();			//回滚事务
```

需要注意的是：当遇到一个未处理的`SQLException`的异常时，系统会非正常退出，事务会自动回滚。但如果显式捕捉该异常，则需要显式地回滚事务。

# try-with-resources

Java7增强的try语句的功能，保证那些实现Closeable接口的实现类能够自动关闭资源，不用显示的finally关闭资源。

细节可参考参考：[JDK7的try-with-resource方式的使用](https://blog.csdn.net/Petershusheng/article/details/53991157?utm_source=blogxgwz7)

```java
package cn.my.jdbcTransaction;

import cn.my.jdbcPra.util.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @auther Summerday
 * JDBC的事务支持
 */
public class JDBCDemo {
    public static void main(String[] args) {
        //获取连接
        try (Connection conn = JDBCUtils.getConnection()) {
            //关闭自动默认提交，开启事务
            conn.setAutoCommit(false);
            //定义sql
            //-500
            String sql1 = "update account set balance = balance - ? where id = ?";
            //+500
            String sql2 = "update account set balance = balance + ? where id = ?";

            //执行sql对象的prs
            try (PreparedStatement prs1 = conn.prepareStatement(sql1);
                 PreparedStatement prs2 = conn.prepareStatement(sql2)) {
                //设置参数
                prs1.setDouble(1, 500);
                prs1.setInt(2, 1);

                prs2.setDouble(1, 500);
                prs2.setInt(2, 2);

                //执行sql1语句
                prs1.executeUpdate();
                //执行sql2语句
                prs2.executeUpdate();
            } catch (Exception e) {
                System.out.println("roll back");
                conn.rollback();
            }
            //提交事务
            conn.commit();
            System.out.println("commit!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

