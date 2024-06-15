## 二、JDBC API详解

### 2.1 DriverManager
1. 注册驱动程序
2. 获取数据库连接
#### 1. 注册驱动程序
当你加载数据库驱动程序类时，它会自动向 `DriverManager` 注册自己。通常，通过 `Class.forName` 方法加载驱动程序类。
```java
//Driver源码
public class Driver extends NonRegisteringDriver implements java.sql.Driver {
    public Driver() throws SQLException {
    }

    static {
        try {
            DriverManager.registerDriver(new Driver()); //真正注册驱动的代码，在类加载时自动执行。
        } catch (SQLException var1) {
            throw new RuntimeException("Can't register driver!");
        }
    }
}
```
#### 2. 获取连接(`DriverManager.getConnection`)
`getConnection(String url, String user, String password)`
* 定义数据库连接参数
    * url：指定数据库的位置和数据库名称。语法：jdbc:mysql://ip地址(域名):端口号/数据库名称？参数键对值1&参数键对值1...
    * user：用户名
    * password：密码

>如果连接的是本地mysql并且端口是默认的3306，则可以简化书写：jdbc:mysql:///数据库名
> useSSL 是 JDBC URL 中的一个参数，用于指定是否在客户端和数据库服务器之间使用 SSL（安全套接字层）加密。useSSL=false不起用，useSSL=true启用。

### 2.2  Connection
1. 获取执行SQL的对象
    * Statement用于执行静态SQL语句。
    * PreparedStatement用于执行预编译的SQL语句。
    * CallableStatement用于执行存储过程。
2. 管理事务
    * MySQL 中的事务管理    
      开始事务：使用 `START TRANSACTION` 或 `BEGIN `命令开始一个新的事务。    
      提交事务：使用 `COMMIT `命令将事务中的操作永久保存到数据库中。    
      回滚事务：使用 `ROLLBACK` 命令取消事务中的所有操作，并恢复到事务开始之前的状态。
   >注意：MySql默认自动提交事务。

    * JDBC 中的事务管理    
      开启事务/关闭自动提交：使用 `setAutoCommit(false)` 方法关闭自动提交模式，从而启用事务。   
      提交事务：通过 `commit() `方法提交事务，将事务中的操作永久保存到数据库中。    
      回滚事务：如果发生错误或其他异常，可以使用 `rollback() `方法取消事务中的所有操作，并恢复到事务开始之前的状态。
```java
@Test
public void testTransaction() throws Exception {
Class.forName("com.mysql.cj.jdbc.Driver");
String url="jdbc:mysql://127.0.0.1:3306/testjdbc?useSSL=false";
String username="root";
String password="24211";
Connection connection = DriverManager.getConnection(url, username, password);
String sql1="update user set money=6000 where id=1";
String sql2="update user set money=6000 where id=2";
Statement statement=connection.createStatement();
try {
//开启事务
connection.setAutoCommit(false);

            int count = statement.executeUpdate(sql1);  //返回受影响的行数。
            //int i=3/0; 测试事务
            count =count + statement.executeUpdate(sql2);
            System.out.println(count);

            //提交事务
            connection.commit();
        } catch (SQLException e) {
            //回滚事务
            connection.rollback();
            throw new RuntimeException(e);
        }
        statement.close();
        connection.close();
    }
```

