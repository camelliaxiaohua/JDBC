# SQL注入
SQL注入是一种网络攻击，利用应用程序未正确处理用户输入数据的漏洞，通过在输入字段插入恶意的SQL代码来执行数据库操作。
这种攻击可能导致数据泄露、数据篡改或者完全控制数据库。

## 一、SQL注入现象
假设有一个登录页面，用户需要输入用户名和密码。应用程序接收到用户输入后，构建了如下的SQL查询语句
```sql
SELECT * FROM users WHERE username='输入的用户名' AND password='输入的密码';
```

攻击者可以在用户名和密码字段中插入恶意的SQL代码，比如输入 `' OR '1'='1`，那么构建出来的查询语句就变成了：
```sql
SELECT * FROM users WHERE username='' OR '1'='1' AND password='' OR '1'='1';
```
这样，无论输入的用户名和密码是什么，条件 `'1'='1'` 都为真，因此查询将返回所有用户的记录，从而绕过了身份验证，攻击者就可以获取所有用户的信息。

### 示例代码
```java
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
```

## 二、解决SQL注入问题
* 只要用户提供的信息不参与SQL语句的编译过程，问题就解决了。
即使用户提供的信息中含有SQL语句的关键字，但是没有参与编译，不起作用。
而为了让用户信息不参与编译，可以使用java.sql.PreparedStatement。    
* PreparedStatement接口继承了java.sql.Statement。    
* PreparedStatement是属于预编译的数据库操作对象。     
* PreparedStatement的原理：预先对SQL语句的框架进行编译，然后再给SQL语句传**值**。     

# 2.1 PreparedStatement实现
>使用PreparedStatement的代码步骤顺序有所不同。
1. 注册驱动
```java
 Class.forName(driver);
```
2. 获取连接
```java
conn= DriverManager.getConnection(url, username, password);
```
3. 获取预编译的数据库操作对象
```java
String sql="select * from t_user where username = ? and password = ?";
//程序执行到此处，会发送SQL语句框架给DBMS，然后DBMS进行sql语句的预编译。
pstmt=conn.prepareStatement(sql);
//给占位符？传值（第一个问号下标是1，后面依次2、3...。JDBC中所有下表从1开始。）
pstmt.setString(1,username);  //这样即使有关键字也不参与编译。
pstmt.setString(2,password);
```
>sql语句的位置有所调动，需在prepareStatement创建之前。
>SQL语句框架中。?表示一个占位符，一个占位符将来接受一个值。
> 注意：占位符不能用单引号括起来。

4. 执行SQL
```java
 rs=pstmt.executeQuery();
```
> 在不需要再传sql了，因为在创建prepareStatement就已经传递过了。


5. 处理结果集
```java
//写自己的需求即可。
if(rs.next())loginSuccess=true;
```
6. 释放资源
```java
if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
if(conn!=null)try {conn.close();} catch (SQLException e) {e.printStackTrace();}
```

### 示例代码
```java
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
            //4、执行SQL。
            rs=pstmt.executeQuery(); //在不需要再传sql了，因为在创建prepareStatement就已经传递过了。
            //5、处理查询结果集
            if(rs.next())loginSuccess=true; //只要结果集有数据就是成功,注意：直接return会导致资源没有释放。
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        finally {
            //6、关闭连接
            try{if(rs!=null)rs.close();}catch(SQLException e){e.printStackTrace();}
            try{if(pstmt!=null)pstmt.close();}catch(SQLException e){e.printStackTrace();}
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
```

## 三、prepareStatement和Statement对比

### 3.1 效率方面

1. **`PreparedStatement`预编译和缓存：**
    - `PreparedStatement` 预编译SQL语句，并将其缓存起来。在需要多次执行相同的SQL语句时，数据库可以重复使用预编译的查询计划，而无需重新编译。这节省了编译时间，提高了性能。   

2. **`Statement` 每次执行都要编译：**
    - 每次执行 `Statement` 时，SQL语句都会被重新解析、编译和优化，这会增加数据库的开销，尤其是当相同的SQL语句被多次执行时。


### 3.2 类型安全检测

#### 1. **PreparedStatement**

- **参数化查询**：`PreparedStatement` 允许使用占位符（`?`）来代表参数，并使用特定的方法（如 `setString()`、`setInt()` 等）来设置这些参数的值。
- **自动类型检查**：由于每个参数都通过特定的方法设置，因此 JDBC 驱动程序会自动进行类型检查，确保传递的值符合SQL查询的要求。

示例代码：

```java
String sql = "INSERT INTO t_user (username, password, age) VALUES (?, ?, ?)";
PreparedStatement pstmt = connection.prepareStatement(sql);

// 设置参数时，JDBC驱动程序会自动进行类型检查
pstmt.setString(1, username); // 设置字符串类型
pstmt.setString(2, password); // 设置字符串类型
pstmt.setInt(3, age);            // 设置整数类型

pstmt.executeUpdate();
```
在上面的代码中：
- 第一个参数被设置为字符串类型（`setString`），第二个参数也被设置为字符串类型，第三个参数被设置为整数类型（`setInt`）。
- JDBC 驱动程序会确保这些参数的类型与SQL语句中的预期类型一致。如果类型不匹配，会在编译时或运行时抛出 `SQLException`。

#### 2. **Statement**

- **直接拼接SQL字符串**：`Statement` 通过拼接字符串来构建SQL语句。
- **缺乏类型检查**：由于SQL语句是动态构建的，JDBC驱动程序无法在构建SQL语句时进行类型检查，这可能导致SQL注入风险和运行时错误。

示例代码：

```java
String username = "username";
String password = "password";
int age = 30;

String sql = "INSERT INTO t_user (username, password, age) VALUES ('" + username + "', '" + password + "', " + age + ")";
Statement stmt = connection.createStatement();
stmt.executeUpdate(sql);
```
在上面的代码中：
- 所有的SQL查询部分都是通过字符串拼接完成的。
- 缺乏类型检查，容易导致SQL注入问题，也容易因为字符串拼接错误导致语法错误。

>总结：
> 1. PreparedStatement没有sql注入问题。   
> 2. PreparedStatement有预编译和缓存，效率更高。   
> 3. PreparedStatement支持类型安全检测。   

## 3.3 何时用Statement？
虽然Statement有很多缺点，会导致SQL注入。但是有的程序功能必须使用Statement，需要进行sql语句的拼接，利用SQL注入传入sql关键字。
例如、商城的按价格排序。    
```java
package com.jdbc.sort;

import java.sql.*;
import java.util.ResourceBundle;
import java.util.Scanner;

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

```