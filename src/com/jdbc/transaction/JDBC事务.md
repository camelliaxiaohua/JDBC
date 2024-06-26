---
title: JDBC事务（单机事务）
date: 2024-05-21 17:06:22
tags:
categories:
- JDBC
---


# JDBC事务（单机事务）
JDBC单机事务是指**在单个数据库连接**上执行一系列数据库操作，以确保这些操作要么全部成功，要么全部失败，从而保证数据的完整性和一致性。

## 一、JDBC事务机制

- 默认情况下，<u>JDBC 连接是自动提交的，每个独立的SQL语句都会被视为一个事务并立即提交</u>。
- 但是在实际业务中，通常是多条DML语句共同联合才能完成，必须保证这些DML语句在同一个事务中同时成功或者同时失败。
- 所以，要管理事务，首先需要<u>关闭自动提交模式</u>。

## 二、开启JDBC单机事务的三大步

1. 开启事务（关闭自动提交）   
   若要开启事务，一般在连接数据库的时候就关闭自动提交。
```java
connection= DriverManager.getConnection(url,username,password);
//将自动提交代码机制修改为手动提交。
connection.setAutoCommit(false);
```
2. 提交事务    
   在获取异常`catch`之前关闭事务，因为执行到这说明以上程序没有问题，事务结束，手动提交。
```java
 connection.commit();
```

3. 回滚事务    
   捕获异常之后，回滚事务。
```java
 e.printStackTrace();
```
### 示例代码
```java
 @Test
public void testTransaction() {
    ResourceBundle rb = ResourceBundle.getBundle("jdbc");
    String driver = rb.getString("driver");
    String url = rb.getString("url");
    String password = rb.getString("password");
    String username = rb.getString("username");
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    try{
        //注册驱动
        Class.forName(driver);
        //连接数据库
        connection= DriverManager.getConnection(url,username,password);
        /**1、将自动提交代码机制修改为手动提交。*/
        connection.setAutoCommit(false);
        //获取预编译的数据库操作对象
        String sql="update t_act set balance=? where actno=? ";
        preparedStatement=connection.prepareStatement(sql);
        //执行sql
        preparedStatement.setDouble(1,10000);
        preparedStatement.setInt(2,1001);
        int count=preparedStatement.executeUpdate();
        preparedStatement.setDouble(1,10000);
        preparedStatement.setInt(2,1002);
        count+=preparedStatement.executeUpdate();
        //处理
        System.out.println(count==2?"success":"fail");
        /**2.程序能够走到这说明以上程序没有问题，事务结束，手动提交。*/
        connection.commit();
    }catch (ClassNotFoundException e){
        /**3.回滚事务*/
        e.printStackTrace();
    }catch (SQLException e){
        /**3.回滚事务*/
        e.printStackTrace();
    } finally {
        if(preparedStatement != null){try{preparedStatement.close();}catch(SQLException e){e.printStackTrace();}}
        if(connection != null){try{connection.close();}catch(SQLException e){e.printStackTrace();}}
    }

}
```
>注意：单机事务这么写。