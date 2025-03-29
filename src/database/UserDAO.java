package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Đăng ký người dùng (Gộp cả registerUser & SignUpUI)
    public static boolean registerUser(String username, String email, String password, String position) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, SHA2(?, 256), ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); // Mật khẩu mã hóa SHA2
            stmt.setString(4, position.equalsIgnoreCase("admin") ? "admin" : "user"); // Xác định role

            return stmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Đăng nhập người dùng
    public static boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = SHA2(?, 256)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // Mã hóa SHA2 trước khi so sánh

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Nếu có dữ liệu trả về, tức là đăng nhập thành công
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra email hợp lệ
    public static boolean checkUserEmail(String username, String email) {
        String sql = "SELECT * FROM users WHERE username = ? AND email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Nếu có dữ liệu trả về, tức là username và email hợp lệ
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
