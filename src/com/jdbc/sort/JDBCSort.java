package com.jdbc.sort;

import java.sql.*;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 *
 */
public class JDBCSort {
    public static void main(String[] args) {
        //用户在控制台输入desc就是升序、输入asc就是降序。
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入desc或者asc:");
        String keyWords = sc.nextLine();

        //执行sql
        ResourceBundle rb = ResourceBundle.getBundle("jdbc");
        String driver = rb.getString("driver");
        String url = rb.getString("url");
        String username = rb.getString("username");
        String password = rb.getString("password");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            conn= DriverManager.getConnection(url,username,password);
            stmt = conn.createStatement();
            String sql="select id from user order by id "+keyWords;
            rs=stmt.executeQuery(sql);
            while(rs.next()){
                System.out.println("id:"+rs.getInt(1));
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
            finally {
            if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
            if(stmt!=null)try {stmt.close();} catch (SQLException e) {e.printStackTrace();}
            if(conn!=null)try {conn.close();} catch (SQLException e) {e.printStackTrace();}
        }

    }
}
