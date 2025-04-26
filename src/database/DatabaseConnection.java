package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Bạn có thể có đoạn code tương tự trong DatabaseConnection.java
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/PatientManagement";
            String user = "root";
            String password = "yourpassword"; // Mật khẩu có thể không đúng
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC không được tìm thấy: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
        return null; // Sẽ trả về null nếu có lỗi
    }
}
