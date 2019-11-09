package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {
    private  Connection conn ;

    public  boolean connect() {

        try {
            // db parameters
            String url = "jdbc:sqlite:storage/banco.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

//            System.out.println("Connection to SQLite has been established.");
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public  boolean desconect(){
        try {
            if (conn != null) {
                conn.close();
            }
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public  Connection getConn() {
        return conn;
    }

    public  Statement criarStatement (){

        try {
            return  conn.createStatement();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        return null;
        }
    }
}
