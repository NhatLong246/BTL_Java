package model.repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

import database.DatabaseConnection;
import model.entity.Patient;
import model.enums.Gender;

public class UserRepository {

    // Đăng ký người dùng và trả về kết quả dạng "Success:<userId>" hoặc "Error:<message>"
    public static String registerUser(String username, String email, String password, String position) {
        String userId = UUID.randomUUID().toString(); // Tạo UserID ngẫu nhiên
        String sql = "INSERT INTO UserAccounts (UserID, FullName, Role, Email, PasswordHash) VALUES (?, ?, ?, ?, SHA2(?, 256))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, username); // FullName được sử dụng làm username
            stmt.setString(3, position.equalsIgnoreCase("Quản lí") ? "Quản lí" : 
                             position.equalsIgnoreCase("Bác sĩ") ? "Bác sĩ" : "Bệnh nhân"); // Xác định Role
            stmt.setString(4, email);
            stmt.setString(5, password); // Mật khẩu mã hóa SHA2

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Success:" + userId; // Trả về userId sau khi insert thành công
            }
            return "Error: Failed to insert user";
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Phương thức đăng nhập và trả về giá trị boolean
    public static boolean loginUser(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Truy vấn để kiểm tra username (FullName) và password
            String query = "SELECT UserID FROM UserAccounts WHERE FullName = ? AND PasswordHash = SHA2(?, 256)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Đăng nhập thành công nếu có kết quả
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false; // Lỗi kết nối cơ sở dữ liệu
        }
    }

    // Phương thức lấy UserID từ username (FullName)
    public static String getPatientID(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT UserID FROM UserAccounts WHERE FullName = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("UserID");
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Phương thức lấy thông tin bệnh nhân từ UserID
    public static Patient getPatientById(String userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM Patients WHERE UserID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Patient patient = new Patient();
                patient.setPatientID(rs.getString("PatientID"));
                patient.setFullName(rs.getString("UserID")); // Có thể cần lấy FullName từ UserAccounts
                patient.setDateOfBirth(LocalDate.parse(rs.getString("DateOfBirth")));
                patient.setAddress(rs.getString("Address"));
                patient.setGender(Gender.valueOf(rs.getString("Gender")));
                patient.setCreatedAt(LocalDate.parse(rs.getString("CreatedAt")));
                return patient;
            }
            return null; // Không tìm thấy bệnh nhân
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Kiểm tra email hợp lệ
    public static boolean checkUserEmail(String username, String email) {
        String sql = "SELECT * FROM UserAccounts WHERE FullName = ? AND Email = ?";
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

    // Phương thức lấy vai trò của người dùng từ cơ sở dữ liệu
    public static String getUserRole(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT Role FROM UserAccounts WHERE FullName = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("Role");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null; // Nếu không tìm thấy người dùng hoặc có lỗi
    }

    // Method to confirm password reset using the token
    public static boolean confirmResetPassword(String token, String newPassword) {
        String sql = "UPDATE UserAccounts SET PasswordHash = SHA2(?, 256), reset_token = NULL, reset_token_expiry = NULL WHERE reset_token = ? AND reset_token_expiry > NOW()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, token);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Nếu có ít nhất một dòng được cập nhật thì thành công
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Thêm thông tin bệnh nhân vào bảng Patients
    public static String addPatient(String userId, String name, String birthdate, String gender, String address) {
        String patientId = UUID.randomUUID().toString(); // Tạo PatientID ngẫu nhiên
        String sql = "INSERT INTO Patients (PatientID, UserID, DateOfBirth, Gender, Address, CreatedAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patientId);
            stmt.setString(2, userId);
            stmt.setString(3, birthdate);
            stmt.setString(4, gender);
            stmt.setString(5, address);
            stmt.setString(6, LocalDate.now().toString()); // Ngày nhập viện (CreatedAt)

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0 ? "Success" : "Failed to insert patient information";
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Yêu cầu reset mật khẩu
    public static String resetPassword(String usernameOrEmail) {
        // Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();

        // Câu lệnh SQL để cập nhật token reset mật khẩu và thời gian hết hạn
        String sql = "UPDATE UserAccounts SET reset_token = ?, reset_token_expiry = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE FullName = ? OR Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Thiết lập giá trị cho câu lệnh SQL
            stmt.setString(1, token); // Token mới
            stmt.setString(2, usernameOrEmail); // FullName hoặc Email
            stmt.setString(3, usernameOrEmail); // FullName hoặc Email

            // Thực thi câu lệnh cập nhật
            int rowsUpdated = stmt.executeUpdate();

            // Kiểm tra xem có bản ghi nào bị ảnh hưởng không
            if (rowsUpdated > 0) {
                return token; // Trả về token nếu cập nhật thành công
            } else {
                return null; // Không tìm thấy user với username/email
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi
        }
    }
}