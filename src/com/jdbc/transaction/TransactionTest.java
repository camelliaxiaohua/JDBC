package com.jdbc.transaction;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * jdbc事务机制：
 *     1、JDBC中的事务是自动提交，只要执行任意一条DML语句，就自动提交一次。
 *     但是在实际业务中，通常是多条DML语句共同联合才能完成，必须保证这些DML语句在同一个事务中同时成功或者同时失败。
 */
public class TransactionTest {
    /**
     * sql脚本：
     *    drop table if exists t_act;
     *        drop table t_act{
     *            actno bigint,
     *            balance double(7,2) //注意：7表示有效数字的个数，2表示小数位的个数。
     *        };
     *
     */

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
            //1、注册驱动
            Class.forName(driver);
            //2、连接数据库
            connection= DriverManager.getConnection(url,username,password);
            //将自动提交代码机制修改为手动提交。
            connection.setAutoCommit(false);
            //3、获取预编译的数据库操作对象
            String sql="update t_act set balance=? where actno=? ";
            preparedStatement=connection.prepareStatement(sql);
            //4、执行sql
            preparedStatement.setDouble(1,10000);
            preparedStatement.setInt(2,1001);
            int count=preparedStatement.executeUpdate();
            preparedStatement.setDouble(1,10000);
            preparedStatement.setInt(2,1002);
            count+=preparedStatement.executeUpdate();
            //5、处理查询结果集
            System.out.println(count==2?"success":"fail");

            //程序能够走到这说明以上程序没有问题，事务结束，手动提交。
            connection.commit();
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
