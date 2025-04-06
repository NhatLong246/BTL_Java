package model.repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;


import database.DatabaseConnection;
import model.entity.Patient;
import model.enums.Gender;

public class UserRepository {

	// Đăng ký người dùng (Gộp cả registerUser & SignUpUI)
	/*public static boolean registerUser(String username, String email, String password, String position) {
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
	}*/

	// Đăng ký người dùng và trả về kết quả dạng "Success:<userId>" hoặc "Error:<message>"
	public static String registerUser(String username, String email, String password, String position) {
		String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, SHA2(?, 256), ?)";
		try (Connection conn = DatabaseConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, username);
			stmt.setString(2, email);
			stmt.setString(3, password); // Mật khẩu mã hóa SHA2
			stmt.setString(4, position.equalsIgnoreCase("admin") ? "admin" : "user"); // Xác định role

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					int userId = rs.getInt(1);
					return "Success:" + userId; // Trả về userId sau khi insert thành công
				}
			}
			return "Error: Failed to retrieve user ID";
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

	// Phương thức đăng nhập và trả về vai trò
	/*public static int loginUser(String username, String password) {
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
	}*/

	// Phương thức đăng nhập và trả về giá trị boolean
	public static boolean loginUser(String username, String password) {
		try (Connection conn = DatabaseConnection.getConnection()) {
			// Truy vấn để kiểm tra username và password
			String query = "SELECT role FROM users WHERE username = ? AND password = SHA2(?, 256)";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				// Đăng nhập thành công, lấy vai trò
				return true;  // Đăng nhập thành công
			} else {
				return false;  // Đăng nhập thất bại
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;  // Lỗi kết nối cơ sở dữ liệu
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

	// Phương thức lấy vai trò của người dùng từ cơ sở dữ liệu
	public static String getUserRole(String username) {
		try (Connection conn = DatabaseConnection.getConnection()) {
			String query = "SELECT role FROM users WHERE username = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getString("role");
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;  // Nếu không tìm thấy người dùng hoặc có lỗi
	}

	// Method to confirm password reset using the token
	public static boolean confirmResetPassword(String token, String newPassword) {
		String sql = "UPDATE users SET password = SHA2(?, 256) WHERE reset_token = ? AND reset_token_expiry > NOW()";
		try (Connection conn = DatabaseConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, newPassword);
			stmt.setString(2, token);

			int rowsUpdated = stmt.executeUpdate();
			return rowsUpdated > 0;  // Nếu có ít nhất một dòng được cập nhật thì thành công
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String addPatient(int userId, String name, String birthdate, String gender, String address) {
		String sql = "INSERT INTO patients (user_id, name, birthdate, gender, address) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, userId);
			stmt.setString(2, name);
			stmt.setString(3, birthdate);
			stmt.setString(4, gender);
			stmt.setString(5, address);

			int rowsInserted = stmt.executeUpdate();
			return rowsInserted > 0 ? "Success" : "Failed to insert patient information";
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

	public static String resetPassword(String usernameOrEmail) {
		// Tạo token ngẫu nhiên
		String token = UUID.randomUUID().toString();

		// Câu lệnh SQL để cập nhật token reset mật khẩu và thời gian hết hạn
		String sql = "UPDATE users SET reset_token = ?, reset_token_expiry = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE username = ? OR email = ?";

		try (Connection conn = DatabaseConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			// Thiết lập giá trị cho câu lệnh SQL
			stmt.setString(1, token);  // Token mới
			stmt.setString(2, usernameOrEmail);  // username hoặc email
			stmt.setString(3, usernameOrEmail);  // username hoặc email

			// Thực thi câu lệnh cập nhật
			int rowsUpdated = stmt.executeUpdate();

			// Kiểm tra xem có bản ghi nào bị ảnh hưởng không
			if (rowsUpdated > 0) {
				return token; // Trả về token nếu cập nhật thành công
			} else {
				return null; // Không tìm thấy user với username/email
			}
		} catch (SQLException | ClassNotFoundException e) {
			// Log lỗi chi tiết, ví dụ sử dụng một logger thay vì e.printStackTrace()
			e.printStackTrace();
			return null; // Trả về null nếu có lỗi trong quá trình kết nối hoặc thực thi câu lệnh
		}
	}

}
