package com.lnxjuantous.qa.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SqlManager {
    private static Connection connection;
    private static Properties config;
    
    static {
        try {
            // Load configuration
            config = new Properties();
            config.load(SqlManager.class.getClassLoader().getResourceAsStream("config.properties"));
            
            // Initialize database connection
            String dbUrl = config.getProperty("db.url");
            String dbUser = config.getProperty("db.username");
            String dbPassword = config.getProperty("db.password");
            
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String dbUrl = config.getProperty("db.url");
                String dbUser = config.getProperty("db.username");
                String dbPassword = config.getProperty("db.password");
                
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            }
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get database connection", e);
        }
    }
    
    public static ResultSet executeQuery(String sql) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement(sql);
        return statement.executeQuery();
    }
    
    public static int executeUpdate(String sql) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement(sql);
        return statement.executeUpdate();
    }
    
    public static void executeTransfer(String fromAccount, String toAccount, double amount) throws SQLException {
        try {
            connection.setAutoCommit(false);
            
            // Check if from account has sufficient funds
            ResultSet rs = executeQuery("SELECT balance FROM accounts WHERE account_id = '" + fromAccount + "'");
            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (currentBalance < amount) {
                    throw new SQLException("Insufficient funds in account: " + fromAccount);
                }
            } else {
                throw new SQLException("Account not found: " + fromAccount);
            }
            
            // Deduct from source account
            executeUpdate("UPDATE accounts SET balance = balance - " + amount + " WHERE account_id = '" + fromAccount + "'");
            
            // Add to destination account
            executeUpdate("UPDATE accounts SET balance = balance + " + amount + " WHERE account_id = '" + toAccount + "'");
            
            // Record transaction
            executeUpdate("INSERT INTO transactions (from_account, to_account, amount, transaction_date) VALUES ('" + 
                         fromAccount + "', '" + toAccount + "', " + amount + ", NOW())");
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}