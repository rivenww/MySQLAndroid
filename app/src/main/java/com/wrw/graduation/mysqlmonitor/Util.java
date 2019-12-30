package com.wrw.graduation.mysqlmonitor;

/**
 *
 */
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Util {
    final static String DRIVER_NAME = "com.mysql.jdbc.Driver";

    public static Connection openConnection(String url, String user,
                                            String password) {
        Connection conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            conn = null;
        } catch (SQLException e) {
            conn = null;
        }
        System.out.println(conn);
        return conn;
    }

    public static String query(Connection conn, String sql) {
        if (conn == null) {
            return null;
        }
        Statement statement = null;
        ResultSet result = null;
        String resultString = null;
        try {
            statement = conn.createStatement();
            result = statement.executeQuery(sql);
            System.out.println(result);
            if (result != null) {

                resultString = "";
                ResultSetMetaData result_MD = result.getMetaData();
                int colCount = result_MD.getColumnCount();
                for(int h = 1;h<=colCount;h++){//输出列标题
                    if(h > 1){
                        resultString = resultString + "\t";
                    }
                    String name = result_MD .getColumnName(h);
                    resultString = resultString + name;
                }
                resultString = resultString + "\n";
                while(result.next()){//按行输出
                    for(int i = 1;i<=colCount;i++){
                        if(i > 1){
                            resultString = resultString + " ";
                        }
                        String name = result_MD .getColumnName(i);
                        String value = result.getString(i);
                        resultString = resultString + value;
                    }
                    resultString = resultString + "\n";
                }
                return  resultString;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                    result = null;
                }
                if (statement != null) {
                    statement.close();
                    statement = null;
                }} catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return resultString;
    }

    public static boolean execSQL(Connection conn, String sql) {
        boolean execResult = false;
        if (conn == null) {
            return execResult;
        }
        Statement statement = null;
        try {
            statement = conn.createStatement();
            if (statement != null) {
                execResult = statement.execute(sql);
                Log.i("onInsert", "onInsert0");
                execResult = true;
            }
        } catch (SQLException e) {
            execResult = false;
        }
        return execResult;
    }
}