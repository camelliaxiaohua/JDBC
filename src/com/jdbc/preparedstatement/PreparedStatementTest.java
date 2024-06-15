package com.jdbc.preparedstatement;

import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ResourceBundle;

public class PreparedStatementTest {
    /**
     * @author Camellia.xiaohua
     * 使用PreparedStatement完成增、删、改、查。
     */
    @Test
    public void testPreparedStatement() {
        ResourceBundle rb = ResourceBundle.getBundle("jdbc");
        String driver = rb.getString("driver");
        String url = rb.getString("url");
        String password = rb.getString("password");
        String username = rb.getString("username");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
//        ResultSet resultSet = null;
        try{
            //1、注册驱动
            Class.forName(driver);
            //2、连接数据库
            connection= DriverManager.getConnection(url,username,password);
            //3、获取预编译的数据库操作对象
            String sql="insert into user(username,money) values(?,?)";
            preparedStatement=connection.prepareStatement(sql);
            for(int i=1;i<=10000;i++){
                preparedStatement.setString(1,"camellia"+i);
                preparedStatement.setDouble(2,i+1000);
                preparedStatement.executeUpdate();
            }
           /* preparedStatement.setString(1,"五哈");
//            preparedStatement.setInt(2,3);
            preparedStatement.setInt(2,400);
            preparedStatement.executeUpdate();
            //4、执行sql
            int count = preparedStatement.executeUpdate();
            System.out.println(count);*/
            //5、处理查询结果集
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
//            if(resultSet != null){try{resultSet.close();}catch(SQLException e){e.printStackTrace();}}
            if(preparedStatement != null){try{preparedStatement.close();}catch(SQLException e){e.printStackTrace();}}
            if(connection != null){try{connection.close();}catch(SQLException e){e.printStackTrace();}}
        }

    }
}
