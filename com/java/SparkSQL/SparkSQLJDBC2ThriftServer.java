package com.java.SparkSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Administrator on 2016/4/12.
 */
public class SparkSQLJDBC2ThriftServer {

    public static void main(String [] args) {
        String sql = "select name from people where age = ?";
        Connection conn = null;
        ResultSet resultSet = null;
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            conn = DriverManager.getConnection("jdbc:hive2://master:10000/defaultDB","root","");
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,30);
            resultSet = preparedStatement.executeQuery();
            while ((resultSet.next())) {
                System.out.print(resultSet.getString(1));
                resultSet.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
