# JDBC工具类&示例

## 一、JDBC工具类
```java
package com.jdbc.utils;

import java.sql.*;
import java.util.ResourceBundle;

/**
 * JDBC工具类，简化JDBC编程。
 * <p>
 * 该类提供了获取数据库连接和关闭资源的静态方法。
 * 配置信息通过配置文件读取，初始化时会自动加载驱动。
 * </p>
 *
 * @author Camellia.xiaohua
 * @version 1.0
 * @date 2024-5-21
 */
public class DBUtil {

    // 这里采用配置文件。
    private static final ResourceBundle rb = ResourceBundle.getBundle("jdbc");
    private static final String driver = rb.getString("driver");
    private static final String url = rb.getString("url");
    private static final String username = rb.getString("username");
    private static final String password = rb.getString("password");

    /**
     * 工具类中的构造方法都是私有的。
     * 因为工具类中的方法都是静态的，不需要new对象，直接采用类名调用。
     */
    private DBUtil() {}

    // 注册驱动加载一次即可。
    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load JDBC driver", e);
        }
    }

    /**
     * 获取数据库连接对象。
     *
     * @return 连接对象
     * @throws SQLException 当获取连接失败时抛出此异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 关闭资源。
     *
     * @param conn 连接对象，可以为null
     * @param stmt 数据库操作对象，可以为null
     * @param rs 查询结果集，可以为null
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();  // 建议替换为日志记录
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();  // 建议替换为日志记录
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();  // 建议替换为日志记录
            }
        }
    }
}

```

## 二、JDBC工具类示例

```java
package com.jdbc.fuzzyqueries;

import com.jdbc.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 示例类，演示如何使用JDBC进行模糊查询。
 * <p>
 * 该类包含一个主方法，通过模糊查询从数据库中检索用户名。
 * </p>
 * <p>
 * 配置信息和数据库连接由 {@link com.jdbc.utils.DBUtil} 提供。
 * </p>
 *
 * @version 1.0
 * @date 2024-5-21
 * @author Camellia.xiaohua
 */
public class FuzzyQueries {

    /**
     * 程序的主方法，演示如何使用JDBC进行模糊查询。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // 1、获取连接
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);    //注意开启事务
            // 2、获取预编译的数据库操作对象
            String sql = "SELECT username FROM user WHERE username LIKE ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "_a%");
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("username"));
            }
            conn.commit();  //提交事务
        } catch (SQLException e) {
            e.printStackTrace();  // 建议替换为日志记录
        } finally {
            // 释放资源
            DBUtil.close(conn, ps, rs);
        }
    }
}

```

>在使用封装工具类一定不要忘记：
> 1. 开启事务
> 2. 提交事务
> 3. 回滚事务