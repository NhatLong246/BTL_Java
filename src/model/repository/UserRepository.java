package model.repository;

import java.sql.*;
<<<<<<< HEAD
=======
import java.time.LocalDate;
import java.util.UUID;

>>>>>>> 3f457736d1bb724311adaa4fc92302c9e9dc98cb
import database.DatabaseConnection;

public class UserRepository {

<<<<<<< HEAD
	public static String registerUser(String username, String email, String password, String position) {
		String sql = "INSERT INTO UserAccounts (username, email, password, role) VALUES (?, ?, SHA2(?, 256), ?)";
		try (Connection conn = DatabaseConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
=======
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
>>>>>>> 3f457736d1bb724311adaa4fc92302c9e9dc98cb

            return rs.next(); // Đăng nhập thành công nếu có kết quả
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false; // Lỗi kết nối cơ sở dữ liệu
        }
    }

<<<<<<< HEAD
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
            
=======
    // Phương thức lấy UserID từ username (FullName)
    public static String getPatientID(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT UserID FROM UserAccounts WHERE FullName = ?";
>>>>>>> 3f457736d1bb724311adaa4fc92302c9e9dc98cb
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            System.out.println("Executing login query for user: " + username);
        
            ResultSet rs = stmt.executeQuery();
<<<<<<< HEAD
            return rs.next(); // Trả về true nếu tìm thấy kết quả
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
=======
            if (rs.next()) {
                return rs.getString("UserID");
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
>>>>>>> 3f457736d1bb724311adaa4fc92302c9e9dc98cb
            e.printStackTrace();
            return false;
        }
    }*/

<<<<<<< HEAD
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
=======
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
>>>>>>> 3f457736d1bb724311adaa4fc92302c9e9dc98cb
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

<<<<<<< HEAD
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
=======
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
>>>>>>> 3f457736d1bb724311adaa4fc92302c9e9dc98cb
    }
}