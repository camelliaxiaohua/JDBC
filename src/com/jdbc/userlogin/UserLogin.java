package com.jdbc.userlogin;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * 当前程序存在问题：
 * 用户名：fdsa
 * 密码：fdsa' or '1'='1
 * 登入成功
 * 这个就叫SQL注入（安全隐患）。
 *
 * 导致这个的根本原因是什么？
 * 用户输入的信息含有sql语句的关键字，并且这些关键字参与sql语句的编译过程。
 * 导致原SQL语句含义被扭曲了，进而达到sql注入。
 */
public class UserLogin {
    public static void main(String[] args) {
        //初始化一个界面UI
        Map<String,String> userLoginInfo= initUI();
        //验证用户信息
        boolean loginSuccess=login(userLoginInfo);
        System.out.println(loginSuccess?"登入成功":"登入失败");
    }

    /**
     * 用户登入
     * @param userLoginInfo 用户登入信息
     * @return false表示失败，true表示成功。
     */
    private static boolean login(Map<String, String> userLoginInfo) {
        boolean loginSuccess=false;
        ResourceBundle rb = ResourceBundle.getBundle("userlogin");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String driver = rb.getString("driver");
        String url = rb.getString("url");
        String username = rb.getString("username");
        String password = rb.getString("password");
        try{
            //1、注册驱动
            Class.forName(driver);
            //2、连接数据库.
            conn= DriverManager.getConnection(url, username, password);
            //3、获取数据库对像。
            stmt = conn.createStatement();
            //4、执行SQL语句
            String sql="select * from t_user where username='"+userLoginInfo.get("username")+"' and password='"+userLoginInfo.get("password")+"'";
            //5、处理查询结果集
            rs=stmt.executeQuery(sql);
            if(rs.next())loginSuccess=true; //只要结果集有数据就是成功,注意：直接return会导致资源没有释放。
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        finally {
            //6、关闭连接
            try{if(rs!=null)rs.close();}catch(SQLException e){e.printStackTrace();}
            try{if(stmt!=null)stmt.close();}catch(SQLException e){e.printStackTrace();}
            try{if(conn!=null)conn.close();}catch(SQLException e){e.printStackTrace();}
        }
        return loginSuccess;
    }

    /**
     * 初始化用户界面
     * @return 用户输入的用户名和密码。
     */
    private static Map<String, String> initUI() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("用户名:");
        String username = scanner.nextLine();
        System.out.println("密码:");
        String password = scanner.nextLine();
        Map<String, String> userLoginInfo = new HashMap<String, String>();
        userLoginInfo.put("username", username);
        userLoginInfo.put("password", password);
        return userLoginInfo;
    }
}
