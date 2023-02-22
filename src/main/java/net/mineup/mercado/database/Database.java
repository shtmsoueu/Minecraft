package net.mineup.mercado.database;

import java.io.*;
import java.sql.*;

public class Database
{
    private Connection connection;
    private File file;
    private Statement stmt;
    private String urlconn;
    private String user;
    private String password;
    
    public Database(File f) {
        this.file = f;
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.file);
            this.stmt = this.connection.createStatement();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Database(String urlconn, String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(urlconn, user, password);
            this.stmt = this.connection.createStatement();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.urlconn = urlconn;
        this.user = user;
        this.password = password;
    }
    
    public Database(String host, String database, String user, String pass) {
        this("jdbc:mysql://" + host + "/" + database, user, pass);
    }
    
    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.urlconn, this.user, this.password);
            this.stmt = this.connection.createStatement();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public void update(String sql) {
        try {
            this.stmt.executeUpdate(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao Executar SQL");
        }
    }
    
    public void update(PreparedStatement sql) {
        try {
            sql.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao Executar SQL");
        }
    }
    
    public PreparedStatement prepareStatement(String sql) {
        try {
            return this.connection.prepareStatement(sql);
        }
        catch (SQLException e) {
            System.out.print("SQLException: " + e.getCause());
            return null;
        }
    }
}
