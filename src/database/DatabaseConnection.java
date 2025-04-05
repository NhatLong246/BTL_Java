package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Properties properties = new Properties();

    // Load config từ file database.properties
    static {
        try {
            properties.load(new FileInputStream("resource\\database.properties"));
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
    }
}
