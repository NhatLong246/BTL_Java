package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/PatientManagement";
            String user = "root";
            String password = "2005";
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC không được tìm thấy: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
        return null; // Sẽ trả về null nếu có lỗi
    }

    private static Properties properties = new Properties();

    // Load config từ file database.properties
    /*static {
        try {
            properties.load(new FileInputStream("resources\\database.properties"));
        } catch (IOException e) {
            System.err.println("Lỗi đọc file cấu hình database.properties: " + e.getMessage());
        }
    }

    // Kết nối tới database
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String driver = properties.getProperty("driver");

        // Kiểm tra nếu properties bị thiếu
        if (url == null || username == null || password == null || driver == null) {
            throw new SQLException("Thiếu thông tin cấu hình database.properties!");
        }

        // Load driver
        Class.forName(driver);

        // Trả về kết nối
        return DriverManager.getConnection(url, username, password);
    }*/

    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null) {
                System.out.println("Database connection successful!");
                return true;
            } else {
                System.out.println("Failed to make connection!");
                return false;
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
