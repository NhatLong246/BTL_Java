package model.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PasswordResetService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement";
    private static final String DB_USER = "root"; // Thay bằng username MySQL của bạn
    private static final String DB_PASSWORD = "2005"; // Thay bằng password MySQL của bạn
    private Map<String, String> resetTokens; // Lưu token và username

    public PasswordResetService() {
        resetTokens = new HashMap<>();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public String requestPasswordReset(String input) {
        String username = null;
        String email = null;

        try (Connection conn = getConnection()) {
            String sql = "SELECT UserName, Email FROM useraccounts WHERE UserName = ? OR Email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, input);
            stmt.setString(2, input);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Query executed: " + sql.replace("?", "'" + input + "'"));

            if (rs.next()) {
                username = rs.getString("UserName");
                email = rs.getString("Email");
                System.out.println("Found record - Username: " + username + ", Email: " + email);
            } else {
                System.out.println("No record found for input: " + input);
            }

            if (username != null && email != null) {
                String token = UUID.randomUUID().toString();
                resetTokens.put(token, username);
                System.out.println("Reset token generated: " + token + " for email: " + email);
                return "Token: " + token + " sent to " + email;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean confirmResetPassword(String token, String newPassword) {
        if (resetTokens.containsKey(token)) {
            String username = resetTokens.get(token);
            try (Connection conn = getConnection()) {
                String sql = "UPDATE useraccounts SET PasswordHash = SHA2(?, 256), PasswordChangeRequired = 0 WHERE UserName = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, newPassword);
                stmt.setString(2, username);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Password for " + username + " reset successfully.");
                    resetTokens.remove(token);
                    return true;
                }
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid token: " + token);
        }
        return false;
    }
}