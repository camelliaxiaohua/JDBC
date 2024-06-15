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
