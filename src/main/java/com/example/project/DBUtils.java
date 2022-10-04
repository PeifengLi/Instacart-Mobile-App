package com.example.project;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.regex.*;


public class DBUtils {
    public static Connection getConn(String dbName, String DBM){
        try {
            if (DBM.equals("MySQL")){
                String driver = "com.mysql.jdbc.Driver";
                String user = "admin";
                String password = "12345678";
                Class.forName(driver);
                if (dbName.equals("ABC Retail")){
                    dbName = "ABC";
                }
                String ip = "jdbc:mysql://instacart.cdufsuaeos7a.us-east-1.rds.amazonaws.com:3306/"+dbName+"?useSSL=false";
                return DriverManager.getConnection(ip, user, password);
            }else {
                String driver = "com.amazon.redshift.jdbc42.Driver";
                String user = "awsuser";
                String password = "Instacart123";
                Class.forName(driver);
                Log.e("", "driver存在");
                String ip = "jdbc:redshift://instacart.ccssvmu5vydx.us-east-1.redshift.amazonaws.com:5439/dev/";
                Connection connection = DriverManager.getConnection(ip, user,password);
                if (connection == null) {
                    Log.e(" ", "连不上");
                }
                if (connection != null) {Log.e(" ", "连上了");}
                return null;
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Vector<Vector<String>> getInfo(String database, String DBM, String sql) {
        Vector<Vector<String>> res = new Vector<>();
        Connection connection = getConn(database, DBM);
        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
//                    Log.e("", " 执行");
                    if (Pattern.matches("select.*",sql)){
                        ResultSet rs = ps.executeQuery();
                        Log.e("", "select");
                        if (rs != null) {
                            int count = rs.getMetaData().getColumnCount();
                            Vector<String> head = new Vector<>(count);
                            for (int i = 1; i <= count; i++) {
                                String field = rs.getMetaData().getColumnName(i);
                                head.add(field);
                            }
                            res.add(head);
                            while (rs.next()) {
                                Vector<String> content = new Vector<>(count);
                                for (int i = 1; i <= count; i++) {
                                    String field = rs.getMetaData().getColumnName(i);
                                    content.add(rs.getString(field));
                                }
                                res.add(content);
                            }
                            connection.close();
                            ps.close();
                            return res;
                        }
                    }else if (Pattern.matches("show.*",sql)) {
                        ResultSet rs = ps.executeQuery();
                        if (rs !=null){
                            int count = rs.getMetaData().getColumnCount();
                            while (rs.next()) {
                                Vector<String> content = new Vector<>(count);
                                for (int i = 1; i <= count; i++) {
                                    Log.e("",rs.getString(i));
                                    if (rs.getString(i) != null);
                                    content.add(rs.getString(i));
                                }
                                res.add(content);
                            }
                        }
                        return res;

                    }else if(Pattern.matches("(?i)desc.*",sql)){
                        ResultSet rs = ps.executeQuery();
                        if (rs !=null){
                            int count = rs.getMetaData().getColumnCount();
                            while (rs.next()) {
                                Vector<String> content = new Vector<>(count);
                                for (int i = 1; i <= count-2; i++) {
                                    Log.e("",rs.getString(i));
                                    if (rs.getString(i) != null);
                                    content.add(rs.getString(i));
                                }
                                res.add(content);
                            }
                        }
                        return res;

                    }
                    else {
                        Log.e("", " 执行insert");
                        ps.executeUpdate();
                        Vector<String> out = new Vector<>();
                        out.add("Execute successfully");
                        res.add(out);
                        return res;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            String info = e.getMessage();
            Vector<String> in = new Vector<>();
            in.add(info);
            res.add(in);
            System.out.println(info);
            return res;
        }
    return null;}
}
