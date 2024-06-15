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
            // 2、获取预编译的数据库操作对象
            String sql = "SELECT username FROM user WHERE username LIKE ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, "_a%");
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();  // 建议替换为日志记录
        } finally {
            // 释放资源
            DBUtil.close(conn, ps, rs);
        }
    }
}
