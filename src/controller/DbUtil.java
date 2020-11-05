package controller;

import java.sql.*;

public class DbUtil {
    Connection conn = null;
    
    public void openConnection(){
        String dbClass = "com.mysql.jdbc.Driver";
        try {
            Class.forName(dbClass);
            String host = "localhost";
            String dbName = "tgroup";
            int port = 3306;
            String mysqlURL = "jdbc:mysql://"+host+":"+port+"/"+dbName;
            String username = "root";
            String password = "";
            conn = DriverManager.getConnection(mysqlURL, username, password);
            System.out.println("Connect successfully!");
        } catch (ClassNotFoundException | SQLException e){
            System.out.println(e);
            System.out.println("Connect failed!");
        }
    }
    
    public void closeConnection(){
        if (conn != null){
            try {
                conn.close();
                System.out.println("Connection closed!");
            } catch (Exception ex) {
                System.out.println(ex);
                System.out.println("Close connection failed!");
            }
        }
    }
    
    public boolean executeNonQuery(PreparedStatement statement){
        try {
            statement.executeUpdate();
            return true;
        } catch (Exception ex){
            System.out.println(ex);
            return false;
        }
    }
    
    public ResultSet executeQuery(PreparedStatement statement){
        ResultSet rs = null;
//        System.out.println("statement: " + statement);
        try {
            rs = statement.executeQuery();
        } catch (Exception e){
            System.out.println(e);
        }
        return rs;
    }
}