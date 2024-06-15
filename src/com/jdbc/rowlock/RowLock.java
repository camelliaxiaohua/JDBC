package com.jdbc.rowlock;

import com.jdbc.utils.DBUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RowLock {
    @Test
    public void testRowLock() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn= DBUtil.getConnection();
            conn.setAutoCommit(false);    //注意开启事务
            String sql = "SELECT id, username, money FROM user WHERE money < ?";//添加行级锁。
            ps= conn.prepareStatement(sql);
            ps.setDouble(1, 10000);
            rs=ps.executeQuery();
            while (rs.next()) {
                System.out.println("ID:\t"+rs.getInt("id")+"\tName:\t"+rs.getString("username")+"\tMoney:\t"+rs.getDouble("money"));
            }
            conn.commit();  //提交事务
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn,ps,rs);
        }

    }
}
