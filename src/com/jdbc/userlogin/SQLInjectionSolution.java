package com.jdbc.userlogin;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * 解决SQL注入问题
 */

public class SQLInjectionSolution {
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
        //使用PreparedStatement预编译的数据库操作对象。
        PreparedStatement pstmt = null;
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
            //3、获取预编译的数据库操作对像。
            //SQL语句框架中。?表示一个占位符，一个占位符将来接受一个值。注意：占位符不能用单引号括起来。
            String sql="select * from t_user where username = ? and password = ?";
            //程序执行到此处，会发送SQL语句框架给DBMS，然后DBMS进行sql语句的预编译。
            pstmt=conn.prepareStatement(sql);
            //给占位符？传值（第一个问号下标是1，后面依次2、3...。JDBC中所有下表从1开始。）
            pstmt.setString(1,username);  //这样即使有关键字也不参与编译。
            pstmt.setString(2,password);
            //4、执行SQL。在不需要再传sql了，因为在创建prepareStatement就已经传递过了。
            rs=pstmt.executeQuery();
            //5、处理查询结果集
            if(rs.next())loginSuccess=true; //只要结果集有数据就是成功,注意：直接return会导致资源没有释放。
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        finally {
            //6、关闭连接
            if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
            if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
            if(conn!=null)try {conn.close();} catch (SQLException e) {e.printStackTrace();}
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
