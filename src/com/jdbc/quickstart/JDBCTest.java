package com.jdbc.quickstart;

import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ResourceBundle;

public class JDBCTest {
    /**
     * JDBC快速开始
     * @throws Exception
     *  URL:统一资源定位符（网络中某个资源的绝对路径）<br>
     *  URL包括： 协议 IP PORT 资源名<br>
     *  http://182.61.200.7:80/index.html<br>
     *  http:// 通信协议。<br>
     *  182.61.200.7 服务器IP地址。<br>
     *  80 服务器上的软件端口。<br>
     *  index.html 服务器上某个资源名。<br>
     *  什么是通信协议，有什么用2？<br>
     *   通信协议是通信之前就提前定好的数据传输格式。<br>
     */
    @Test
    public void testQuickStart() {
        Connection connection=null;
        Statement statement=null;
        try {
            //1、注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            //2.获取连接
            String url = "jdbc:mysql://127.0.0.1:3306/testjdbc?useSSL=false";
            String username = "root";
            String password = "24211";
            connection = DriverManager.getConnection(url, username, password);
            //3.获取数据库操作对象Statement
            statement = connection.createStatement();
            //4.执行sql
            String sql = "update user set money=2000 where id=1";
            /*专门执行DML语句的（insert、delete、update）
             *返回值是“影响数据库中的记录条数”  */
            int count = statement.executeUpdate(sql);
            System.out.println(count);
            System.out.println(count==1?"数据更新成功":"数据更新失败");
        }catch (SQLException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            //5.释放资源
            //为了保证资源一定释放，在finally语句块中关闭资源。
            //并且要遵循从小到大依次关闭,分别try catch
            try{
                if(statement!=null)statement.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            try{
                if(connection!=null)connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用资源绑定器绑定属性配置文件
     */
    @Test
    public void testResourceBinding() {
        //使用资源绑定器绑定属性配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver=bundle.getString("driver");
        String url=bundle.getString("url");
        String username=bundle.getString("username");
        String password=bundle.getString("password");
        Connection connection=null;
        Statement statement=null;
        try {
            //1、注册驱动
            Class.forName(driver);
            //2.获取连接
            connection = DriverManager.getConnection(url, username, password);
            //3.获取数据库操作对象Statement
            statement = connection.createStatement();
            //4.执行sql
            String sql = "update user set money=2000 where id=1";
            int count = statement.executeUpdate(sql);
            System.out.println(count);
            System.out.println(count==1?"数据更新成功":"数据更新失败");
        }catch (SQLException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            //5.释放资源
            try{
                if(statement!=null)statement.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            try{
                if(connection!=null)connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理查询结果集
     */
    @Test
    public void testQueryResultSet(){
        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver=bundle.getString("driver");
        String url=bundle.getString("url");
        String username=bundle.getString("username");
        String password=bundle.getString("password");

        Connection connection=null;
        Statement statement=null;
        ResultSet resultSet=null;
        try{
            //1、注册驱动
            Class.forName(driver);
            //2、获取连接
            connection = DriverManager.getConnection(url, username, password);
            //3、获取数据库操作对象
            statement = connection.createStatement();
            //4、执行sql
            String sql = "select * from user";
            //专门执行DQL语句的方法。
            resultSet = statement.executeQuery(sql);
            //5、处理查询结果集
            while (resultSet.next()) { //光标指向行有数据
                //取数据
                //getString()方法特点：不管数据库中的数据是什么类型，都是以String形式取出。当然你也可以按数据类型取出。
                //JDBC中所有下标从1开始，不是从0开始。
                int id = resultSet.getInt("id");     //注意：这个填的是查询语句的列名称，如果起别名则要使用别名。
                String name = resultSet.getString("username");
                double money = resultSet.getDouble("money");
                System.out.println("id: "+id+"\tname: "+name+"\tmoney: "+money);

            }
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        finally {
            //6、释放资源
            if(resultSet!=null)try {resultSet.close();} catch (SQLException e) {e.printStackTrace();}
            if(statement!=null)try {statement.close();} catch (SQLException e) {e.printStackTrace();}
            if(connection!=null)try {connection.close();} catch (SQLException e) {e.printStackTrace();}
        }
    }

    /**
     * JDBC 事务
     * @throws Exception
     */
    @Test
    public void testTransaction() throws Exception {
        //Class.forName("com.mysql.cj.jdbc.Driver");
        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
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




}
