package model.repository;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserRepository {

    /**
     * Đăng ký người dùng mới
     * @param username Tên đăng nhập
     * @param email Email
     * @param password Mật khẩu
     * @param position Vai trò (admin/user)
     * @return Chuỗi kết quả: "Success:userId" nếu thành công, "Error:thông báo lỗi" nếu thất bại
     */
//    public static String registerUser(String username, String email, String password, String position) {
//        String sql = "INSERT INTO UserAccounts (UserName, Email, PasswordHash, Role, IsLocked) VALUES (?, ?, SHA2(?, 256), ?, ?)";
//        Connection conn = null;
//        try {
//            conn = DatabaseConnection.getConnection();
//            if (conn == null) {
//                return "Error: Could not connect to database";
//            }
//            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//                stmt.setString(1, username);
//                stmt.setString(2, email);
//                stmt.setString(3, password); // Mật khẩu được mã hóa bằng SHA2
//                stmt.setString(4, position.equalsIgnoreCase("admin") ? "Quản lí" : "Bệnh nhân"); // Vai trò: Quản lí hoặc Bệnh nhân
//                stmt.setBoolean(5, false); // Mặc định tài khoản không bị khóa
//
//                int rowsAffected = stmt.executeUpdate();
//                if (rowsAffected > 0) {
//                    ResultSet rs = stmt.getGeneratedKeys();
//                    if (rs.next()) {
//                        int userId = rs.getInt(1);
//                        return "Success:" + userId; // Trả về userId sau khi insert thành công
//                    }
//                }
//                return "Error: Failed to retrieve user ID";
//            }
//        } catch (SQLException e) {
//            System.err.println("Lỗi khi đăng ký người dùng: " + e.getMessage());
//            e.printStackTrace();
//            return "Error: " + e.getMessage();
//        } finally {
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    /**
     * Đăng ký người dùng với đầy đủ thông tin (dùng trong AdminRepository)
     * @param userId ID người dùng
     * @param username Tên đăng nhập
     * @param fullName Họ tên
     * @param role Vai trò
     * @param email Email
     * @param phoneNumber Số điện thoại
     * @param passwordHash Mật khẩu đã mã hóa
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean registerUser(String userId, String username, String fullName, String role, String email, String phoneNumber, String passwordHash) {
        String sql = "INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, PasswordHash, IsLocked) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                pstmt.setString(2, username);
                pstmt.setString(3, fullName);
                pstmt.setString(4, role);
                pstmt.setString(5, email);
                pstmt.setString(6, phoneNumber);
                pstmt.setString(7, passwordHash); // Mật khẩu đã mã hóa
                pstmt.setBoolean(8, false); // Mặc định tài khoản không bị khóa
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đăng ký người dùng: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Kiểm tra đăng nhập người dùng
     * @param username Tên đăng nhập (UserName trong bảng UserAccounts)
     * @param password Mật khẩu
     * @return true nếu đăng nhập thành công, false nếu thất bại
     */
    public static boolean loginUser(String username, String password) {
        String query = "SELECT Role, IsLocked FROM UserAccounts WHERE UserName = ? AND PasswordHash = SHA2(?, 256)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return false;
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                System.out.println("Executing login query for user: " + username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    boolean isLocked = rs.getBoolean("IsLocked");
                    if (isLocked) {
                        System.out.println("Tài khoản đã bị khóa: " + username);
                        return false;
                    }
                    System.out.println("Login success with role: " + rs.getString("Role"));
                    return true;
                } else {
                    System.out.println("Login failed for user: " + username);
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
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy vai trò của người dùng từ tên đăng nhập
     * @param username Tên đăng nhập
     * @return Vai trò của người dùng hoặc null nếu không tìm thấy
     */
    public static String getUserRole(String username) {
        String query = "SELECT Role FROM UserAccounts WHERE UserName = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("Role");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy vai trò người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Lấy ID bác sĩ từ tên đăng nhập
     * @param username Tên đăng nhập
     * @return DoctorID hoặc null nếu không tìm thấy
     */
    public static String getDoctorIdByUsername(String username) {
        String query = "SELECT d.DoctorID FROM Doctors d JOIN UserAccounts u ON d.UserID = u.UserID WHERE u.UserName = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("DoctorID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ID bác sĩ: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Lấy ID bệnh nhân từ tên đăng nhập
     * @param username Tên đăng nhập
     * @return PatientID hoặc null nếu không tìm thấy
     */
    public static String getPatientIdByUsername(String username) {
        String query = "SELECT p.PatientID FROM Patients p JOIN UserAccounts u ON p.UserID = u.UserID WHERE u.UserName = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("PatientID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ID bệnh nhân: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Xác nhận đặt lại mật khẩu
     * @param token Token đặt lại mật khẩu
     * @param newPassword Mật khẩu mới
     * @return true nếu thành công, false nếu thất bại
     */
    public static boolean confirmResetPassword(String token, String newPassword) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu");
                return false;
            }
            // Tìm người dùng bằng token và kiểm tra tính hợp lệ của token
            String findUserQuery = "SELECT UserID FROM PasswordResetTokens WHERE Token = ? AND ExpiryDate > NOW()";
            try (PreparedStatement findUserStmt = conn.prepareStatement(findUserQuery)) {
                findUserStmt.setString(1, token);
                ResultSet rs = findUserStmt.executeQuery();
                if (rs.next()) {
                    String userID = rs.getString("UserID");
                    // Cập nhật mật khẩu mới
                    String updatePasswordQuery = "UPDATE UserAccounts SET PasswordHash = SHA2(?, 256) WHERE UserID = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updatePasswordQuery)) {
                        updateStmt.setString(1, newPassword);
                        updateStmt.setString(2, userID);
                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            // Xóa token sau khi sử dụng
                            String deleteTokenQuery = "DELETE FROM PasswordResetTokens WHERE Token = ?";
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteTokenQuery)) {
                                deleteStmt.setString(1, token);
                                deleteStmt.executeUpdate();
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi khi đặt lại mật khẩu: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy UserID từ email
     * @param email Email người dùng
     * @return UserID hoặc null nếu không tìm thấy
     */
    public static String getUserIdByEmail(String email) {
        String query = "SELECT UserID FROM UserAccounts WHERE Email = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("UserID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng theo email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Lấy ID người dùng từ username hoặc email
     * @param usernameOrEmail Username hoặc email của người dùng
     * @return UserID nếu tìm thấy, null nếu không tìm thấy
     */
    public static String getUserIdByUsernameOrEmail(String usernameOrEmail) {
        String query = "SELECT UserID FROM UserAccounts WHERE UserName = ? OR Email = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, usernameOrEmail);
                stmt.setString(2, usernameOrEmail);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("UserID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng theo username/email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Thêm thông tin bệnh nhân
     * @param userId ID người dùng
     * @param name Tên bệnh nhân
     * @param birthdate Ngày sinh
     * @param gender Giới tính
     * @param address Địa chỉ
     * @return Chuỗi kết quả: "Success" nếu thành công, "Error:thông báo lỗi" nếu thất bại
     */
    public static String addPatient(int userId, String name, String birthdate, String gender, String address) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return "Error: Could not connect to database";
            }
            // Kiểm tra xem userID đã tồn tại trong bảng UserAccounts chưa
            String checkUserQuery = "SELECT UserID FROM UserAccounts WHERE UserID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery)) {
                checkStmt.setInt(1, userId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    return "Error: User ID not found";
                }
            }
            // Thêm thông tin bệnh nhân
            String insertQuery = "INSERT INTO Patients (UserID, PatientName, BirthDate, Gender, Address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
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
            }
        } catch (SQLException e) {
            System.err.println("Error adding patient: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Xóa tài khoản người dùng
     * @param userId ID người dùng
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM UserAccounts WHERE UserID = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return false;
            }
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
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy họ tên người dùng từ ID
     * @param userId ID người dùng
     * @return Họ tên hoặc null nếu không tìm thấy
     */
    public String getFullNameByUserId(String userId) {
        String sql = "SELECT FullName FROM UserAccounts WHERE UserID = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("FullName");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy họ tên: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Lấy email người dùng từ ID
     * @param userId ID người dùng
     * @return Email hoặc null nếu không tìm thấy
     */
    public String getEmailByUserId(String userId) {
        String sql = "SELECT Email FROM UserAccounts WHERE UserID = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("Email");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Lấy số điện thoại người dùng từ ID
     * @param userId ID người dùng
     * @return Số điện thoại hoặc null nếu không tìm thấy
     */
    public String getPhoneByUserId(String userId) {
        String sql = "SELECT PhoneNumber FROM UserAccounts WHERE UserID = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("PhoneNumber");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy số điện thoại: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Lấy ID admin từ tên đăng nhập
     * @param username Tên đăng nhập
     * @return AdminID hoặc null nếu không tìm thấy
     */
    public static String getAdminIdByUsername(String username) {
        String query = "SELECT a.AdminID FROM Admins a JOIN UserAccounts u ON a.UserID = u.UserID WHERE u.UserName = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("AdminID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy ID admin: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Lấy UserID từ tên đăng nhập
     * @param username Tên đăng nhập
     * @return UserID hoặc null nếu không tìm thấy
     */
    public static String getUserIdByUsername(String username) {
        String query = "SELECT UserID FROM UserAccounts WHERE UserName = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("UserID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy UserID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private static String generateUserId(String rolePrefix) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT MAX(CAST(SUBSTRING(UserID, 5) AS UNSIGNED)) AS maxId FROM UserAccounts WHERE UserID LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, rolePrefix + "-%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                return String.format("%s-%03d", rolePrefix, maxId + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rolePrefix + "-001"; // Giá trị mặc định nếu không có dữ liệu
    }

    public static String registerUser(String username, String email, String password, String role) {
        String userId = null;
        String rolePrefix = role.equals("Bệnh nhân") ? "PAT" : (role.equals("Bác sĩ") ? "DOC" : "ADM");
        userId = generateUserId(rolePrefix);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO useraccounts (UserID, UserName, FullName, Role, Email, PasswordHash, CreatedAt, PasswordChangeRequired, IsLocked) VALUES (?, ?, ?, ?, ?, SHA2(?, 256), NOW(), 1, 0)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, username); // Giả sử FullName = UserName, bạn có thể điều chỉnh
            stmt.setString(4, role);
            stmt.setString(5, email);
            stmt.setString(6, password);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Success: " + userId;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return "Error: Đăng ký thất bại";
    }

    // Các phương thức khác như isUsernameTaken, isEmailTaken, v.v.
    public static boolean isUsernameTaken(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM useraccounts WHERE UserName = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isEmailTaken(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM useraccounts WHERE Email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}