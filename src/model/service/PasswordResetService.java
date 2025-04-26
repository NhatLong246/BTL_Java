package model.service;

import model.repository.UserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.time.LocalDateTime;
import database.DatabaseConnection;

public class PasswordResetService {
    /**
     * Gửi yêu cầu đặt lại mật khẩu
     * @param usernameOrEmail Username hoặc email của người dùng
     * @return token hoặc null nếu thất bại
     */
    public String requestPasswordReset(String usernameOrEmail) {
        try {
            // Kiểm tra username hoặc email tồn tại
            String userID = UserRepository.getUserIdByUsernameOrEmail(usernameOrEmail);
            if (userID == null) {
                return null; // Username/Email không tồn tại
            }
            
            // Tạo token
            String token = generateUniqueToken();
            
            // Lưu token vào database
            if (saveResetToken(userID, token)) {
                // Trong thực tế, bạn sẽ gửi email với token này
                // cho người dùng tại đây
                System.out.println("Token đặt lại mật khẩu: " + token);
                
                return token;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi yêu cầu đặt lại mật khẩu: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Xác nhận đặt lại mật khẩu bằng token
     * @param token Token đặt lại mật khẩu
     * @param newPassword Mật khẩu mới
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean confirmResetPassword(String token, String newPassword) {
        return UserRepository.confirmResetPassword(token, newPassword);
    }
    
    /**
     * Tạo token duy nhất
     * @return Token ngẫu nhiên
     */
    private String generateUniqueToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * Lưu token đặt lại mật khẩu vào database
     * @param userID ID người dùng
     * @param token Token đặt lại
     * @return true nếu thành công, false nếu thất bại
     */
    private boolean saveResetToken(String userID, String token) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return false;
            }
            
            // Xóa token cũ nếu có
            String deleteQuery = "DELETE FROM PasswordResetTokens WHERE UserID = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setString(1, userID);
            deleteStmt.executeUpdate();
            
            // Thêm token mới với thời hạn 24 giờ
            String insertQuery = "INSERT INTO PasswordResetTokens (UserID, Token, ExpiryDate) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 24 HOUR))";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, userID);
            insertStmt.setString(2, token);
            
            return insertStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
