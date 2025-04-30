package model.repository;

import java.sql.*;
import database.DatabaseConnection;

public class UserRepository {

	public static String registerUser(String username, String email, String password, String position) {
		String sql = "INSERT INTO UserAccounts (username, email, password, role) VALUES (?, ?, SHA2(?, 256), ?)";
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
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

    /**
     * Kiểm tra đăng nhập người dùng
     * @param username Tên đăng nhập (UserName trong bảng UserAccounts)
     * @param password Mật khẩu
     * @return true nếu đăng nhập thành công, false nếu thất bại
     */
    /*public static boolean loginUser(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Sửa lại truy vấn để khớp với cấu trúc bảng trong cơ sở dữ liệu
            String query = "SELECT Role FROM UserAccounts WHERE UserName = ? AND PasswordHash = SHA2(?, 256)";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            System.out.println("Executing login query for user: " + username);
        
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Trả về true nếu tìm thấy kết quả
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }*/

	public static boolean loginUser(String username, String password) {
	    String query = "SELECT Role, PasswordHash, IsLocked FROM UserAccounts WHERE UserName = ?";
	    Connection conn = null;
	    try {
	        conn = DatabaseConnection.getConnection();
	        if (conn == null) {
	            System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
	            return false;
	        }
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setString(1, username);
	            System.out.println("Executing login query for user: " + username);
	            ResultSet rs = stmt.executeQuery();
	            if (rs.next()) {
	                // Kiểm tra xem tài khoản có bị khóa không
	                boolean isLocked = rs.getBoolean("IsLocked");
	                if (isLocked) {
	                    System.out.println("Tài khoản đã bị khóa: " + username);
	                    return false;
	                }
	                // So sánh mật khẩu
	                String storedHash = rs.getString("PasswordHash");
	                String hashedPasswordQuery = "SELECT SHA2(?, 256) AS hashedPassword";
	                try (PreparedStatement hashStmt = conn.prepareStatement(hashedPasswordQuery)) {
	                    hashStmt.setString(1, password);
	                    ResultSet hashRs = hashStmt.executeQuery();
	                    if (hashRs.next()) {
	                        String hashedInputPassword = hashRs.getString("hashedPassword");
	                        if (hashedInputPassword.equals(storedHash)) {
	                            System.out.println("Login success with role: " + rs.getString("Role"));
	                            return true;
	                        }
	                    }
	                }
	                System.out.println("Login failed for user: " + username + " - Mật khẩu không khớp");
	                return false;
	            } else {
	                System.out.println("Login failed for user: " + username + " - Không tìm thấy tài khoản");
	                return false;
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Lỗi đăng nhập: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    } finally {
	        if (conn != null) {
	            try {
	                conn.close();
	                System.out.println("Đã đóng kết nối cơ sở dữ liệu");
	            } catch (SQLException e) {
	                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
	                e.printStackTrace();
	            }
	        }
	    }
	}

    /*public static boolean loginUser(String username, String password) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection is null");
                return false;
            }
            System.out.println("Database connected successfully");
            
            // In ra thông tin cơ sở dữ liệu
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("Database Product: " + metaData.getDatabaseProductName());
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
            
            // Kiểm tra bảng UserAccounts có tồn tại không
            ResultSet tables = metaData.getTables(null, null, "UserAccounts", null);
            if (!tables.next()) {
                System.err.println("Bảng UserAccounts không tồn tại!");
                return false;
            }
            System.out.println("Bảng UserAccounts tồn tại");
            
            // Thực hiện truy vấn thử
            String query = "SELECT Role FROM UserAccounts WHERE UserName = ? AND PasswordHash = SHA2(?, 256)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            System.out.println("Executing login query for user: " + username);
            System.out.println("SQL Query: " + stmt.toString());
            
            ResultSet rs = stmt.executeQuery();
            boolean hasResult = rs.next();
            System.out.println("Login result: " + (hasResult ? "Success" : "Failed"));
            
            return hasResult;
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    /**
     * Lấy vai trò của người dùng từ tên đăng nhập
     * @param username Tên đăng nhập
     * @return Vai trò của người dùng hoặc chuỗi rỗng nếu không tìm thấy
     */
    public static String getUserRole(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Sửa lại tên bảng và cột để phù hợp với cơ sở dữ liệu
            String query = "SELECT Role FROM UserAccounts WHERE UserName = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Role");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy vai trò người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Lấy ID bác sĩ từ tên đăng nhập
     * @param username Tên đăng nhập
     * @return ID bác sĩ hoặc null nếu không tìm thấy
     */
    public static String getDoctorIdByUsername(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT d.DoctorID FROM Doctors d JOIN UserAccounts u ON d.UserID = u.UserID WHERE u.UserName = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("DoctorID");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ID bác sĩ: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy ID bệnh nhân từ tên đăng nhập
     * @param username Tên đăng nhập
     * @return ID bệnh nhân hoặc null nếu không tìm thấy
     */
    public static String getPatientIdByUsername(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT p.PatientID FROM Patients p JOIN UserAccounts u ON p.UserID = u.UserID WHERE u.UserName = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("PatientID");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ID bệnh nhân: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Xác nhận đặt lại mật khẩu bằng token
     * @param token Token đặt lại mật khẩu
     * @param newPassword Mật khẩu mới
     * @return true nếu đổi mật khẩu thành công, false nếu thất bại
     */
    public static boolean confirmResetPassword(String token, String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu");
                return false;
            }
            
            // Tìm người dùng bằng token và kiểm tra tính hợp lệ của token
            String findUserQuery = "SELECT UserID FROM PasswordResetTokens WHERE Token = ? AND ExpiryDate > NOW()";
            PreparedStatement findUserStmt = conn.prepareStatement(findUserQuery);
            findUserStmt.setString(1, token);
            
            ResultSet rs = findUserStmt.executeQuery();
            if (rs.next()) {
                String userID = rs.getString("UserID");
                
                // Cập nhật mật khẩu mới
                String updatePasswordQuery = "UPDATE UserAccounts SET PasswordHash = SHA2(?, 256) WHERE UserID = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updatePasswordQuery);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, userID);
                
                int rowsAffected = updateStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Xóa token sau khi sử dụng
                    String deleteTokenQuery = "DELETE FROM PasswordResetTokens WHERE Token = ?";
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteTokenQuery);
                    deleteStmt.setString(1, token);
                    deleteStmt.executeUpdate();
                    
                    return true;
                }
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi khi đặt lại mật khẩu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy ID người dùng từ email
     * @param email Email của người dùng
     * @return UserID nếu tìm thấy, null nếu không tìm thấy
     */
    public static String getUserIdByEmail(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return null;
            }
            
            String query = "SELECT UserID FROM UserAccounts WHERE Email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("UserID");
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng theo email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy ID người dùng từ username hoặc email
     * @param usernameOrEmail Username hoặc email của người dùng
     * @return UserID nếu tìm thấy, null nếu không tìm thấy
     */
    public static String getUserIdByUsernameOrEmail(String usernameOrEmail) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return null;
            }
            
            String query = "SELECT UserID FROM UserAccounts WHERE UserName = ? OR Email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("UserID");
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng theo username/email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Thêm thông tin bệnh nhân mới vào hệ thống
     * @param userId ID người dùng
     * @param name Tên bệnh nhân
     * @param birthdate Ngày sinh (định dạng YYYY-MM-DD)
     * @param gender Giới tính
     * @param address Địa chỉ
     * @return "Success" nếu thêm thành công, thông báo lỗi nếu thất bại
     */
    public static String addPatient(int userId, String name, String birthdate, String gender, String address) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return "Error: Could not connect to database";
            }
            
            // Kiểm tra xem userID đã tồn tại trong bảng UserAccounts chưa
            String checkUserQuery = "SELECT UserID FROM UserAccounts WHERE UserID = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
            checkStmt.setInt(1, userId);
            
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                return "Error: User ID not found";
            }
            
            // Thêm thông tin bệnh nhân
            String insertQuery = "INSERT INTO Patients (UserID, PatientName, BirthDate, Gender, Address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, birthdate);
            stmt.setString(4, gender);
            stmt.setString(5, address);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Success";
            } else {
                return "Error: Failed to add patient information";
            }
        } catch (SQLException e) {
            System.err.println("Error adding patient: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    public boolean registerUser(String userId, String username, String fullName, String role, String email, String phoneNumber, String passwordHash) {
        String sql = "INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, PasswordHash) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, fullName);
            pstmt.setString(4, role);
            pstmt.setString(5, email);
            pstmt.setString(6, phoneNumber);
            pstmt.setString(7, passwordHash);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return false;
            }
            String sql = "DELETE FROM UserAccounts WHERE UserID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa tài khoản người dùng: " + e.getMessage());
            e.printStackTrace();
            return false;
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

    public String getFullNameByUserId(String userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return null;
            String sql = "SELECT FullName FROM UserAccounts WHERE UserID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("FullName");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String getEmailByUserId(String userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return null;
            String sql = "SELECT Email FROM UserAccounts WHERE UserID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("Email");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String getPhoneByUserId(String userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return null;
            String sql = "SELECT PhoneNumber FROM UserAccounts WHERE UserID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("PhoneNumber");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}