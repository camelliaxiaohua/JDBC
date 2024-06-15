---
title: JDBC介绍&编程六部
date: 2024-05-17 00:36:12
tags:
- JDBC
categories:
- JDBC
---

!!! note 目录
<!-- toc -->


# JDBC

## 一、JDBC介绍
1.  JDBC是Java语言连接数据库（Java DataBase Connectivity）
2. JDBC的本质是什么？
    * JDBC是SUN公司制定的一套接口（interface）
    * 接口都有调用者和实现者
    * 面向接口调用、面向接口写实现类，这都属于面向接口编程。
>为什么SUN制定一套JDBC接口呢？
>因为每个数据库的底层实现原理都不一样。

![](https://camelliaxiaohua-1313958787.cos.ap-shanghai.myqcloud.com/asserts_JavaSE/202405171637239.png)
3. JDBC开发前的准备工作，先从官网下载对应的驱动jar包，然后将其配置到环境变量classpath当中。
4. JDBC编程六部(important！！！)
    * 注册数据库（作用：告诉Java程序，即将要连接的是哪个品牌的数据库）
    * 获取连接（表示JVM的进程和数据库进程之间的通道打开了，属于进程之间的通信，重量级、使用完一定要关闭。）
    * 获取数据库操作对象（专门执行Sql语句的对象）
    * 执行SQL语句（DQL DML...）
    * 处理查询结果集（只有第四步执行的是select语句的时候，才会有第五步处理查询结果集。）
    * 释放资源（使用完资源之后一定要关闭资源。Java和数据库属于进程通信，开启之后一定要关闭。）

## 二、JDBC快速入门
1. 注册驱动
```java
//Way1
 DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
```
```java
//Way2  这个更常用。
Class.forName("com.mysql.cj.jdbc.Driver");
```
>自动注册驱动：在 JDBC 4.0 及更高版本中，驱动程序 jar 包中的 META-INF/services/java.sql.Driver 文件会自动注册驱动程序。
>所以在mysql 5之后的驱动包，可以省略Class.forName步骤。

2. 获取连接
```java
String url="jdbc:mysql://127.0.0.1:3306/testjdbc";
String username="root";
String password="24211";
Connection connection = DriverManager.getConnection(url, username, password);
```
>注意：使用配置文件绑定信息

3. 获取数据库操作对象Statement
```java
Statement statement=connection.createStatement();
```

4. 执行sql
```java
String sql = "update user set money=2000 where id=1";
/*专门执行DML语句的（insert、delete、update）
 *返回值是“影响数据库中的记录条数”  */
int count = statement.executeUpdate(sql);
System.out.println(count);
System.out.println(count==1?"数据更新成功":"数据更新失败");
```
>JDBC中的sql语句不要写;

5. 释放资源
```java
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
```
>注意，释放顺序不能打乱。

### 2.1、快速开始完整代码
```java
package com.jdbc.quickstart;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
}
```
### 2.2、使用资源绑定器绑定属性配置文件
```java
package com.jdbc.quickstart;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class JDBCTest {
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
}
```

### 2.3处理查询结果集
```java
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class JDBCTest {
    @Test
    public void testQueryResultSet() {
        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver = bundle.getString("driver");
        String url = bundle.getString("url");
        String username = bundle.getString("username");
        String password = bundle.getString("password");

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
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
                //getString()方法特点：不管数据库中的数据是什么类型，都是以                       String形式取出。当然你也可以按数据类型取出。
                //JDBC中所有下标从1开始，不是从0开始。
                int id = resultSet.getInt("id");     
                //注意：这个填的是查询语句的列名称，如果起别名则要使用别名。
                String name = resultSet.getString("username");
                double money = resultSet.getDouble("money");
                System.out.println("id: " + id + "\tname: " + name + "\tmoney: " + money);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            //6、释放资源
            if (resultSet != null) try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
```
>注意：这个填的是查询语句的列名称，如果起别名则要使用别名。


