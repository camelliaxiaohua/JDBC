package com.jdbc.quickstart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC快速入门
 */
public class JDBCDemo {
    public static void main(String[] args) throws Exception {
        //1、注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        //2.获取连接
        String url="jdbc:mysql://127.0.0.1:3306/testjdbc?useSSL=false";
        String username="root";
        String password="24211";
        Connection connection = DriverManager.getConnection(url, username, password);
        //3.定义SQL语句
        String sql="update user set money=2000 where id=1";
        //4.获取执行sql的对象Statement
        Statement statement=connection.createStatement();

        try {
            //开启事务
            connection.setAutoCommit(false);

            //5.执行sql
            int executed = statement.executeUpdate(sql);  //返回受影响的行数。
            System.out.println(executed);

            //提交事务
            connection.commit();
        } catch (SQLException e) {
            //回滚事务
            connection.rollback();
            throw new RuntimeException(e);
        }
        //6.释放资源
        statement.close();
        connection.close();
    }
}
