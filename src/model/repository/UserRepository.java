package model.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import database.DatabaseConnection;
import model.entity.Patient;
import model.enums.Gender;

public class UserRepository {

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

	// Phương thức đăng nhập và trả về vai trò
	public static int loginUser(String username, String password) {
		try (Connection conn = DatabaseConnection.getConnection()) {
			// Truy vấn để kiểm tra username và password
			String query = "SELECT role FROM users WHERE username = ? AND password = SHA2(?, 256)";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				// Đăng nhập thành công, lấy vai trò
				String role = rs.getString("role");
				switch (role) {
				case "admin":
					return 1; // Trả về 1 nếu là admin
				case "doctor":
					return 2; // Trả về 2 nếu là doctor
				case "patient":
					return 3; // Trả về 3 nếu là patient
				default:
					return -1; // Vai trò không hợp lệ
				}
			} else {
				return 0; // Đăng nhập thất bại
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return -1; // Lỗi kết nối cơ sở dữ liệu
		}
	}

	// Phương thức lấy patientID từ username
    public static String getPatientID(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.valueOf(rs.getInt("id"));
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Phương thức lấy thông tin bệnh nhân từ patientID
    public static Patient getPatientById(String patientID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM patients WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Patient patient = new Patient();
                patient.setPatientID(rs.getString("id"));
                patient.setFullName(rs.getString("name"));
                patient.setDateOfBirth(LocalDate.parse(rs.getString("birthdate")));
                patient.setAddress(rs.getString("address"));
                patient.setGender(Gender.valueOf(rs.getString("gender")));
                patient.setCreatedAt(LocalDate.parse(rs.getString("created_at")));
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
