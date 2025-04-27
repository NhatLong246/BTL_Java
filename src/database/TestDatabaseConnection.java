package database;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        testConnection();
    }
    
    public static void testConnection() {
        Connection conn = null;
        try {
            System.out.println("Attempting to connect to database...");
            conn = DatabaseConnection.getConnection();
            
            if (conn != null) {
                System.out.println("Connection established successfully!");
                
                // Hiển thị thông tin về database
                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("Database Name: " + metaData.getDatabaseProductName());
                System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
                System.out.println("Driver Name: " + metaData.getDriverName());
                System.out.println("Driver Version: " + metaData.getDriverVersion());
                System.out.println("URL: " + metaData.getURL());
                System.out.println("User: " + metaData.getUserName());
                
                // Kiểm tra bảng UserAccounts
                System.out.println("\nChecking UserAccounts table...");
                ResultSet tables = metaData.getTables(null, null, "UserAccounts", null);
                if (tables.next()) {
                    System.out.println("UserAccounts table exists!");
                    
                    // Kiểm tra cấu trúc bảng
                    ResultSet columns = metaData.getColumns(null, null, "UserAccounts", null);
                    System.out.println("\nColumns in UserAccounts table:");
                    while (columns.next()) {
                        System.out.println(columns.getString("COLUMN_NAME") + " - " + 
                                          columns.getString("TYPE_NAME") + " - " +
                                          columns.getString("IS_NULLABLE"));
                    }
                    
                    // Đếm số lượng record
                    Statement stmt = conn.createStatement();
                    ResultSet count = stmt.executeQuery("SELECT COUNT(*) FROM UserAccounts");
                    if (count.next()) {
                        System.out.println("\nTotal records in UserAccounts: " + count.getInt(1));
                    }
                    
                    // In ra một vài bản ghi đầu tiên
                    ResultSet users = stmt.executeQuery("SELECT UserID, UserName, PasswordHash FROM UserAccounts LIMIT 3");
                    System.out.println("\nSample records:");
                    while (users.next()) {
                        System.out.println("UserID: " + users.getString("UserID") + 
                                          ", UserName: " + users.getString("UserName") +
                                          ", PasswordHash: " + users.getString("PasswordHash").substring(0, 10) + "...");
                    }
                } else {
                    System.out.println("UserAccounts table DOES NOT exist!");
                }
            } else {
                System.out.println("Connection failed - connection object is null!");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Connection closed.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}