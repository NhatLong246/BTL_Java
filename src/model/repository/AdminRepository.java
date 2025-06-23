package model.repository;

import model.entity.Doctor;
import model.enums.Gender;
import model.enums.Specialization;
import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;

public class AdminRepository {

    private String lastCreatedUsername;
    private String lastCreatedPassword;


    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT d.DoctorID, u.UserID, u.FullName, u.Email, u.PhoneNumber, d.DateOfBirth, d.Gender, d.Address, " +
                     "s.SpecialtyID, s.SpecialtyName, d.CreatedAt " +
                     "FROM Doctors d " +
                     "JOIN UserAccounts u ON d.UserID = u.UserID " +
                     "LEFT JOIN Specialties s ON d.SpecialtyID = s.SpecialtyID " +
                     "WHERE u.Role = 'Bác sĩ' AND u.IsLocked = 0" +
                     " ORDER BY d.DoctorID ASC"; // Sắp xếp theo ngày tạo mới nhất
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            System.out.println("Đã kết nối đến cơ sở dữ liệu thành công");

            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "Doctors", null);
            if (!tables.next()) {
                System.err.println("Bảng Doctors không tồn tại!");
                return null;
            }
            tables = metaData.getTables(null, null, "UserAccounts", null);
            if (!tables.next()) {
                System.err.println("Bảng UserAccounts không tồn tại!");
                return null;
            }

            System.out.println("Thực hiện truy vấn: " + sql);
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    Doctor doctor = new Doctor(
                        rs.getString("UserID"),
                        rs.getString("DoctorID"),
                        rs.getString("FullName"),
                        rs.getDate("DateOfBirth").toLocalDate(),
                        rs.getString("Address"),
                        Gender.fromDatabase(rs.getString("Gender")),
                        rs.getString("PhoneNumber"),
                        Specialization.fromId(rs.getString("SpecialtyID")),
                        rs.getString("Email"),
                        rs.getDate("CreatedAt").toLocalDate()
                    );
                    doctors.add(doctor);
                    System.out.println("Bác sĩ " + count + ": DoctorID=" + doctor.getDoctorId() + ", FullName=" + doctor.getFullName());
                }
                System.out.println("Tổng số bác sĩ tìm thấy: " + count);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách bác sĩ: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy danh sách bác sĩ: " + e.getMessage());
            e.printStackTrace();
            return null;
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
        return doctors;
    }

    public boolean createDoctor(String userId, String fullName, LocalDate dateOfBirth, String address, Gender gender,
                               String phoneNumber, String specialtyId, String email) {
        Connection conn = null;
        String username = "";
        String defaultPassword = "";
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return false;
            }
            conn.setAutoCommit(false);
    
            String newUserId = generateNewUserID(conn);
            String doctorId = Doctor.generateNewDoctorID(conn);
    
            // Tạo username từ email hoặc fullName
            username = generateUsername(fullName, doctorId);
            // Tạo mật khẩu ngẫu nhiên an toàn
            defaultPassword = generateSecurePassword();
    
            this.lastCreatedUsername = username;
            this.lastCreatedPassword = defaultPassword;
    
            // Xử lý null hoặc rỗng cho phoneNumber và email
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                phoneNumber = "Chưa cập nhật"; // Giá trị mặc định thay vì rỗng
                System.out.println("Phone number trống, đã gán giá trị mặc định");
            }
            
            if (email == null || email.trim().isEmpty()) {
                email = username + "@example.com"; // Giá trị mặc định
                System.out.println("Email trống, đã gán giá trị mặc định");
            }
    
            // Debug để kiểm tra giá trị
            System.out.println("Phone number trước khi chèn vào DB: '" + phoneNumber + "'");
            System.out.println("Email trước khi chèn vào DB: '" + email + "'");
    
            // Không sử dụng hashPassword() nữa, sử dụng SHA2() trong SQL như trong PatientRepository
            String userSql = "INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, PasswordHash, IsLocked, PasswordChangeRequired) " +
                            "VALUES (?, ?, ?, ?, ?, ?, SHA2(?, 256), ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                pstmt.setString(1, newUserId);
                pstmt.setString(2, username);
                pstmt.setString(3, fullName);
                pstmt.setString(4, "Bác sĩ");
                pstmt.setString(5, email);
                pstmt.setString(6, phoneNumber);
                pstmt.setString(7, defaultPassword); // Mật khẩu gốc sẽ được băm bởi SHA2() trong SQL
                pstmt.setBoolean(8, false); // Mặc định không khóa
                pstmt.setBoolean(9, true);  // Yêu cầu đổi mật khẩu khi đăng nhập lần đầu
                pstmt.executeUpdate();
            }
    
            // Sửa câu lệnh SQL thêm trường PhoneNumber và Email vào danh sách cột
            String doctorSql = "INSERT INTO Doctors (DoctorID, UserID, DateOfBirth, Gender, Address, SpecialtyID, CreatedAt, FullName, PhoneNumber, Email) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(doctorSql)) {
                pstmt.setString(1, doctorId);
                pstmt.setString(2, newUserId);
                pstmt.setDate(3, java.sql.Date.valueOf(dateOfBirth));
                pstmt.setString(4, gender != null ? gender.getVietnamese() : "Nam"); // Giá trị mặc định nếu null
                pstmt.setString(5, address != null ? address : "Chưa cập nhật");
                pstmt.setString(6, specialtyId);
                pstmt.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
                pstmt.setString(8, fullName);
                pstmt.setString(9, phoneNumber); // Thêm số điện thoại vào bảng Doctors
                pstmt.setString(10, email);      // Thêm email vào bảng Doctors
                pstmt.executeUpdate();
            }
    
            conn.commit();
            System.out.println("Đã tạo bác sĩ mới với DoctorID: " + doctorId);
            
            // Kiểm tra dữ liệu sau khi thêm
            verifyDoctorData(conn, doctorId);
            
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback giao dịch: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            System.err.println("Lỗi SQL khi tạo bác sĩ: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback giao dịch: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            System.err.println("Lỗi không xác định khi tạo bác sĩ: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Kiểm tra dữ liệu bác sĩ sau khi thêm vào cơ sở dữ liệu
     * @param conn Connection đến cơ sở dữ liệu
     * @param doctorId ID của bác sĩ cần kiểm tra
     * @throws SQLException nếu có lỗi SQL
     */
    private void verifyDoctorData(Connection conn, String doctorId) throws SQLException {
        String sql = "SELECT d.DoctorID, d.FullName, d.PhoneNumber, d.Email, u.PhoneNumber as UserPhone, u.Email as UserEmail " +
                     "FROM Doctors d " +
                     "JOIN UserAccounts u ON d.UserID = u.UserID " +
                     "WHERE d.DoctorID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Kiểm tra dữ liệu sau khi thêm bác sĩ:");
                    System.out.println("DoctorID: " + rs.getString("DoctorID"));
                    System.out.println("FullName: " + rs.getString("FullName"));
                    System.out.println("PhoneNumber trong bảng Doctors: " + rs.getString("PhoneNumber"));
                    System.out.println("PhoneNumber trong bảng UserAccounts: " + rs.getString("UserPhone"));
                    System.out.println("Email trong bảng Doctors: " + rs.getString("Email"));
                    System.out.println("Email trong bảng UserAccounts: " + rs.getString("UserEmail"));
                } else {
                    System.out.println("Không tìm thấy bác sĩ với ID: " + doctorId);
                }
            }
        }
    }
    
    public String getLastCreatedUsername() {
        return lastCreatedUsername;
    }
    
    public String getLastCreatedPassword() {
        return lastCreatedPassword;
    }

    // Thêm phương thức này trong class AdminRepository
    private String generateNewUserID(Connection conn) throws SQLException {
        String query = "SELECT MAX(SUBSTRING(UserID, 5)) AS maxID FROM UserAccounts WHERE UserID LIKE 'USR-%'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            int maxID = 0;
            if (rs.next()) {
                String maxIDStr = rs.getString("maxID");
                if (maxIDStr != null && !maxIDStr.isEmpty()) {
                    try {
                        maxID = Integer.parseInt(maxIDStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi chuyển đổi mã UserID: " + e.getMessage());
                    }
                }
            }
            return String.format("USR-%03d", maxID + 1); // Định dạng USR-XXX
        }
    }

    // Thêm phương thức để tạo username
    private String generateUsername(String fullName, String doctorId) {
        if (fullName == null || fullName.isEmpty()) {
            return "doctor" + doctorId.substring(4);
        }
        
        // Chuyển về chữ thường, bỏ dấu
        String normalized = fullName.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("\\s+", "");
        
        // Lấy phần số từ DoctorID (ví dụ: "001" từ "DOC-001")
        String idSuffix = "";
        if (doctorId != null && doctorId.length() > 4) {
            idSuffix = doctorId.substring(4);
        }
        
        return normalized + idSuffix;
    }
    
    // Thêm phương thức để tạo mật khẩu ngẫu nhiên
    private String generateSecurePassword() {
        // Các ký tự được phép
        String uppercase = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // Loại bỏ I và O dễ nhầm lẫn
        String lowercase = "abcdefghijkmnopqrstuvwxyz"; // Loại bỏ l dễ nhầm lẫn
        String digits = "23456789"; // Loại bỏ 0 và 1 dễ nhầm lẫn
        String specialChars = "@#$%^&+=";
        
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();
        
        // Đảm bảo mật khẩu có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Thêm 4 ký tự ngẫu nhiên nữa để đủ 8 ký tự
        String allChars = uppercase + lowercase + digits + specialChars;
        for (int i = 0; i < 4; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Trộn ngẫu nhiên các ký tự
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = temp;
        }
        
        return new String(passwordArray);
    }

    public boolean lockDoctor(String doctorId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu!");
            }

            String checkDoctorSql = "SELECT UserID FROM Doctors WHERE DoctorID = ?";
            String userId = null;
            try (PreparedStatement pstmt = conn.prepareStatement(checkDoctorSql)) {
                pstmt.setString(1, doctorId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getString("UserID");
                        System.out.println("Tìm thấy bác sĩ với DoctorID: " + doctorId + ", UserID: " + userId);
                    } else {
                        System.out.println("Không tìm thấy bác sĩ với DoctorID: " + doctorId);
                        throw new SQLException("Không tìm thấy bác sĩ với DoctorID: " + doctorId);
                    }
                }
            }

            String checkUserSql = "SELECT IsLocked FROM UserAccounts WHERE UserID = ? AND Role = 'Bác sĩ'";
            boolean isAlreadyLocked = false;
            try (PreparedStatement pstmt = conn.prepareStatement(checkUserSql)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        isAlreadyLocked = rs.getBoolean("IsLocked");
                        if (isAlreadyLocked) {
                            System.out.println("Tài khoản với UserID: " + userId + " đã bị khóa trước đó");
                            return false;
                        }
                        System.out.println("Tìm thấy tài khoản người dùng với UserID: " + userId);
                    } else {
                        System.out.println("Không tìm thấy tài khoản người dùng với UserID: " + userId + " hoặc vai trò không phải 'Bác sĩ'");
                        throw new SQLException("Không tìm thấy tài khoản người dùng với UserID: " + userId + " hoặc vai trò không phải 'Bác sĩ'");
                    }
                }
            }

            String sql = "UPDATE UserAccounts SET IsLocked = ? WHERE UserID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBoolean(1, true);
                pstmt.setString(2, userId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Đã khóa tài khoản bác sĩ với DoctorID: " + doctorId);
                    return true;
                } else {
                    System.out.println("Không thể khóa bác sĩ với DoctorID: " + doctorId + " - Không có hàng nào được cập nhật");
                    throw new SQLException("Không thể khóa bác sĩ với DoctorID: " + doctorId + " - Không có hàng nào được cập nhật");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi khóa bác sĩ: " + e.getMessage());
            throw e;
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

    public boolean unlockDoctor(String doctorId) throws SQLException {
        String defaultPassword = "Doctor@123";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu!");
            }
    
            String checkDoctorSql = "SELECT UserID FROM Doctors WHERE DoctorID = ?";
            String userId = null;
            try (PreparedStatement pstmt = conn.prepareStatement(checkDoctorSql)) {
                pstmt.setString(1, doctorId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getString("UserID");
                        System.out.println("Tìm thấy bác sĩ với DoctorID: " + doctorId + ", UserID: " + userId);
                    } else {
                        System.out.println("Không tìm thấy bác sĩ với DoctorID: " + doctorId);
                        throw new SQLException("Không tìm thấy bác sĩ với DoctorID: " + doctorId);
                    }
                }
            }
    
            String checkUserSql = "SELECT IsLocked FROM UserAccounts WHERE UserID = ? AND Role = 'Bác sĩ'";
            boolean isLocked = false;
            try (PreparedStatement pstmt = conn.prepareStatement(checkUserSql)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        isLocked = rs.getBoolean("IsLocked");
                        if (!isLocked) {
                            System.out.println("Tài khoản với UserID: " + userId + " không bị khóa");
                            return false;
                        }
                        System.out.println("Tìm thấy tài khoản người dùng với UserID: " + userId);
                    } else {
                        System.out.println("Không tìm thấy tài khoản người dùng với UserID: " + userId + " hoặc vai trò không phải 'Bác sĩ'");
                        throw new SQLException("Không tìm thấy tài khoản người dùng với UserID: " + userId + " hoặc vai trò không phải 'Bác sĩ'");
                    }
                }
            }
    
            // Sửa câu SQL để sử dụng SHA2() trực tiếp trong database
            String sql = "UPDATE UserAccounts SET IsLocked = ?, PasswordHash = SHA2(?, 256), PasswordChangeRequired = TRUE WHERE UserID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBoolean(1, false);
                pstmt.setString(2, defaultPassword);
                pstmt.setString(3, userId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Đã mở khóa tài khoản bác sĩ với DoctorID: " + doctorId + " với mật khẩu mặc định: " + defaultPassword);
                    return true;
                } else {
                    System.out.println("Không thể mở khóa bác sĩ với DoctorID: " + doctorId + " - Không có hàng nào được cập nhật");
                    throw new SQLException("Không thể mở khóa bác sĩ với DoctorID: " + doctorId + " - Không có hàng nào được cập nhật");
                }
            }
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

    public List<Doctor> getLockedDoctors() {
        List<Doctor> lockedDoctors = new ArrayList<>();
        String sql = "SELECT d.DoctorID, u.UserID, u.FullName, u.Email, u.PhoneNumber, d.DateOfBirth, d.Gender, d.Address, " +
                     "s.SpecialtyID, s.SpecialtyName, d.CreatedAt " +
                     "FROM Doctors d " +
                     "JOIN UserAccounts u ON d.UserID = u.UserID " +
                     "LEFT JOIN Specialties s ON d.SpecialtyID = s.SpecialtyID " +
                     "WHERE u.IsLocked = 1 AND u.Role = 'Bác sĩ'";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            System.out.println("Thực hiện truy vấn: " + sql);
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    Doctor doctor = new Doctor();
                    doctor.setUserId(rs.getString("UserID"));
                    doctor.setDoctorId(rs.getString("DoctorID"));
                    doctor.setFullName(rs.getString("FullName"));
                    doctor.setDateOfBirth(rs.getDate("DateOfBirth").toLocalDate());
                    doctor.setAddress(rs.getString("Address"));
                    doctor.setGender(Gender.fromDatabase(rs.getString("Gender")));
                    doctor.setPhoneNumber(rs.getString("PhoneNumber"));
                    doctor.setSpecialization(rs.getString("SpecialtyID") != null ? Specialization.fromId(rs.getString("SpecialtyID")) : null);
                    doctor.setEmail(rs.getString("Email"));
                    doctor.setCreatedAt(rs.getDate("CreatedAt").toLocalDate());
                    lockedDoctors.add(doctor);
                    System.out.println("Bác sĩ bị khóa " + count + ": DoctorID=" + doctor.getDoctorId() + ", FullName=" + doctor.getFullName());
                }
                System.out.println("Tổng số bác sĩ bị khóa tìm thấy: " + count);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách bác sĩ bị khóa: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy danh sách bác sĩ bị khóa: " + e.getMessage());
            e.printStackTrace();
            return null;
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
        return lockedDoctors;
    }

    /**
     * Lưu lịch làm việc của bác sĩ
     * @param doctorId ID của bác sĩ
     * @param schedule Mảng 2 chiều boolean chứa trạng thái làm việc
     * @return true nếu lưu thành công, false nếu có lỗi
     */
    public boolean saveDoctorSchedule(String doctorId, boolean[][] schedule) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return false;
            }
            conn.setAutoCommit(false);
    
            // Kiểm tra bác sĩ tồn tại
            String checkSql = "SELECT COUNT(*) FROM Doctors WHERE DoctorID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, doctorId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("Bác sĩ với ID " + doctorId + " không tồn tại!");
                    return false;
                }
            }
    
            // Xóa lịch cũ
            String deleteSql = "DELETE FROM DoctorSchedule WHERE DoctorID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setString(1, doctorId);
                pstmt.executeUpdate();
            }
    
            // Kiểm tra cấu trúc bảng DoctorSchedule
            String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
            String[] shifts = {"Sáng", "Chiều", "Tối"};
            
            // Sử dụng câu lệnh SQL đúng với cấu trúc bảng
            String insertSql = "INSERT INTO DoctorSchedule (DoctorID, DayOfWeek, ShiftType, Status) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (int shift = 0; shift < shifts.length; shift++) {
                    for (int day = 0; day < days.length; day++) {
                        if (schedule[shift][day]) {
                            pstmt.setString(1, doctorId);
                            pstmt.setString(2, days[day]);
                            pstmt.setString(3, shifts[shift]);
                            pstmt.setString(4, "Đang làm việc");
                            pstmt.addBatch();
                        }
                    }
                }
                // Thực hiện batch update
                int[] result = pstmt.executeBatch();
                System.out.println("Đã cập nhật " + result.length + " ca làm việc cho bác sĩ ID: " + doctorId);
            }
    
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lưu lịch làm việc: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    public Doctor getDoctorInfo(String doctorId) {
        String sql = "SELECT d.DoctorID, u.UserID, u.FullName, u.Email, u.PhoneNumber, d.Address, d.DateOfBirth, d.Gender, " +
                     "s.SpecialtyID, s.SpecialtyName, d.CreatedAt " +
                     "FROM Doctors d " +
                     "JOIN UserAccounts u ON d.UserID = u.UserID " +
                     "LEFT JOIN Specialties s ON d.SpecialtyID = s.SpecialtyID " +
                     "WHERE d.DoctorID = ?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, doctorId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Doctor doctor = new Doctor();
                        doctor.setUserId(rs.getString("UserID"));
                        doctor.setDoctorId(rs.getString("DoctorID"));
                        doctor.setFullName(rs.getString("FullName"));
                        doctor.setDateOfBirth(rs.getDate("DateOfBirth").toLocalDate());
                        doctor.setAddress(rs.getString("Address"));
                        doctor.setGender(Gender.fromDatabase(rs.getString("Gender")));
                        doctor.setPhoneNumber(rs.getString("PhoneNumber"));
                        doctor.setSpecialization(rs.getString("SpecialtyID") != null ? Specialization.fromId(rs.getString("SpecialtyID")) : null);
                        doctor.setEmail(rs.getString("Email"));
                        doctor.setCreatedAt(rs.getDate("CreatedAt").toLocalDate());
                        return doctor;
                    } else {
                        System.out.println("Không tìm thấy bác sĩ với DoctorID: " + doctorId);
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy thông tin bác sĩ: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi lấy thông tin bác sĩ: " + e.getMessage());
            e.printStackTrace();
            return null;
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

    private String hashPassword(String password) {
        return password; // Mật khẩu sẽ được mã hóa bằng SHA2 trong truy vấn SQL
    }

        public List<Map<String, String>> getAllSpecialties() {
        List<Map<String, String>> specialties = new ArrayList<>();
        String sql = "SELECT SpecialtyID, SpecialtyName FROM Specialties ORDER BY SpecialtyName";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, String> specialty = new HashMap<>();
                specialty.put("id", rs.getString("SpecialtyID"));
                specialty.put("name", rs.getString("SpecialtyName"));
                specialties.add(specialty);
            }
            
            System.out.println("Đã lấy " + specialties.size() + " chuyên khoa từ database");
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách chuyên khoa: " + e.getMessage());
            e.printStackTrace();
        }
        
        return specialties;
    }

    /**
     * Lấy tóm tắt lịch làm việc của tất cả bác sĩ
     * @return Mảng 2 chiều chứa trạng thái làm việc theo ca và ngày trong tuần
     */
    public String[][] getDoctorScheduleSummary() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            
            String[][] scheduleData = new String[3][7]; // 3 ca x 7 ngày
            
            // Mặc định tất cả là "Không làm việc"
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 7; j++) {
                    scheduleData[i][j] = "Không làm việc";
                }
            }
            
            // Lấy danh sách các ca làm việc từ database
            String sql = "SELECT DayOfWeek, ShiftType, Status, COUNT(*) as DoctorCount " +
                         "FROM DoctorSchedule " +
                         "GROUP BY DayOfWeek, ShiftType, Status";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String dayOfWeek = rs.getString("DayOfWeek");
                    String shiftType = rs.getString("ShiftType");
                    String status = rs.getString("Status");
                    int doctorCount = rs.getInt("DoctorCount");
                    
                    // Chuyển đổi tên ngày sang index (0-6)
                    int dayIndex;
                    switch (dayOfWeek) {
                        case "Thứ Hai": dayIndex = 0; break;
                        case "Thứ Ba": dayIndex = 1; break;
                        case "Thứ Tư": dayIndex = 2; break;
                        case "Thứ Năm": dayIndex = 3; break;
                        case "Thứ Sáu": dayIndex = 4; break;
                        case "Thứ Bảy": dayIndex = 5; break;
                        case "Chủ Nhật": dayIndex = 6; break;
                        default: continue;
                    }
                    
                    // Chuyển đổi ca làm việc sang index (0-2)
                    int shiftIndex;
                    switch (shiftType) {
                        case "Sáng": shiftIndex = 0; break;
                        case "Chiều": shiftIndex = 1; break;
                        case "Tối": shiftIndex = 2; break;
                        default: continue;
                    }
                    
                    // Nếu có bác sĩ đang làm việc, đánh dấu ca đó là "Đang làm việc"
                    if (status.equals("Đang làm việc") && doctorCount > 0) {
                        scheduleData[shiftIndex][dayIndex] = "Đang làm việc";
                    }
                    // Nếu có ca "Hết ca làm việc" và không có ca "Đang làm việc"
                    else if (status.equals("Hết ca làm việc") && 
                            !scheduleData[shiftIndex][dayIndex].equals("Đang làm việc")) {
                        scheduleData[shiftIndex][dayIndex] = "Hết ca làm việc";
                    }
                }
                
                // Xử lý thêm: cập nhật trạng thái dựa trên thời gian hiện tại
                LocalDate today = LocalDate.now();
                LocalTime now = LocalTime.now();
                int todayIndex = today.getDayOfWeek().getValue() - 1; // 0 = Thứ Hai
                
                // Kiểm tra và cập nhật trạng thái các ca đã qua trong ngày hôm nay
                if (now.isAfter(LocalTime.of(11, 30)) && scheduleData[0][todayIndex].equals("Đang làm việc")) {
                    scheduleData[0][todayIndex] = "Hết ca làm việc";
                }
                if (now.isAfter(LocalTime.of(17, 0)) && scheduleData[1][todayIndex].equals("Đang làm việc")) {
                    scheduleData[1][todayIndex] = "Hết ca làm việc";
                }
                
                // Kiểm tra và cập nhật trạng thái các ngày đã qua trong tuần
                for (int day = 0; day < todayIndex; day++) {
                    for (int shift = 0; shift < 3; shift++) {
                        if (scheduleData[shift][day].equals("Đang làm việc")) {
                            scheduleData[shift][day] = "Hết ca làm việc";
                        }
                    }
                }
                
                return scheduleData;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy thông tin lịch làm việc: " + e.getMessage());
            e.printStackTrace();
            return null;
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
     * Lấy lịch làm việc của bác sĩ theo ID
     * @param doctorId ID của bác sĩ
     * @return Mảng 2 chiều boolean[3][7] chứa trạng thái làm việc (true = làm việc)
     */
    public boolean[][] getDoctorSchedule(String doctorId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return null;
            }
            
            boolean[][] schedule = new boolean[3][7]; // 3 ca x 7 ngày, mặc định là false (không làm việc)
            
            // Kiểm tra xem bảng DoctorSchedule có tồn tại không
            try {
                // Lấy thông tin ca làm việc từ database
                String sql = "SELECT DayOfWeek, ShiftType FROM DoctorSchedule WHERE DoctorID = ? AND Status = 'Đang làm việc'";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, doctorId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String dayOfWeek = rs.getString("DayOfWeek");
                            String shiftType = rs.getString("ShiftType");
                            
                            int dayIndex;
                            switch (dayOfWeek) {
                                case "Thứ Hai": dayIndex = 0; break;
                                case "Thứ Ba": dayIndex = 1; break;
                                case "Thứ Tư": dayIndex = 2; break;
                                case "Thứ Năm": dayIndex = 3; break;
                                case "Thứ Sáu": dayIndex = 4; break;
                                case "Thứ Bảy": dayIndex = 5; break;
                                case "Chủ Nhật": dayIndex = 6; break;
                                default: continue;
                            }
                            
                            int shiftIndex;
                            switch (shiftType) {
                                case "Sáng": shiftIndex = 0; break;
                                case "Chiều": shiftIndex = 1; break;
                                case "Tối": shiftIndex = 2; break;
                                default: continue;
                            }
                            
                            // Đánh dấu ca làm việc
                            schedule[shiftIndex][dayIndex] = true;
                        }
                    }
                    
                    return schedule;
                }
            } catch (SQLException e) {
                System.err.println("Lỗi SQL khi tải lịch làm việc: " + e.getMessage());
                // Nếu có lỗi, có thể là do bảng chưa được tạo hoặc cấu trúc không đúng
                System.err.println("Chi tiết lỗi: " + e.getMessage());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi tải lịch làm việc: " + e.getMessage());
            e.printStackTrace();
            return null;
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
     * Lưu phân công bác sĩ theo ca làm việc
     * @param day Ngày trong tuần
     * @param shift Tên ca
     * @param doctorIds Danh sách ID bác sĩ
     * @return true nếu lưu thành công
     */
    public boolean saveDoctorShiftAssignments(String day, String shift, List<String> doctorIds) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu!");
            }
            conn.setAutoCommit(false);
            
            // Xóa các phân công cũ cho ngày và ca này
            String deleteSql = "DELETE FROM DoctorSchedule WHERE DayOfWeek = ? AND ShiftType = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setString(1, day);
                stmt.setString(2, shift);
                stmt.executeUpdate();
            }
            
            // Thêm phân công mới
            if (!doctorIds.isEmpty()) {
                String insertSql = "INSERT INTO DoctorSchedule (DoctorID, DayOfWeek, ShiftType, Status) VALUES (?, ?, ?, 'Đang làm việc')";
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    for (String doctorId : doctorIds) {
                        stmt.setString(1, doctorId);
                        stmt.setString(2, day);
                        stmt.setString(3, shift);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi SQL khi lưu lịch làm việc: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Lấy tất cả bác sĩ được phân công cho một ca cụ thể
     * @param day Ngày trong tuần
     * @param shift Tên ca
     * @return Danh sách các bác sĩ
     */
    public List<Doctor> getDoctorsForShift(String day, String shift) {
        List<Doctor> doctors = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu!");
            }
            
            String sql = "SELECT d.DoctorID, d.UserID, u.FullName, d.DateOfBirth, d.Gender, d.Address, " +
                         "s.SpecialtyID, s.SpecialtyName, u.Email, u.PhoneNumber, d.CreatedAt " +
                         "FROM DoctorSchedule ds " +
                         "JOIN Doctors d ON ds.DoctorID = d.DoctorID " +
                         "JOIN UserAccounts u ON d.UserID = u.UserID " +
                         "LEFT JOIN Specialties s ON d.SpecialtyID = s.SpecialtyID " +
                         "WHERE ds.DayOfWeek = ? AND ds.ShiftType = ? AND ds.Status = 'Đang làm việc'" +
                         "ORDER BY d.DoctorID ASC";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, day);
                stmt.setString(2, shift);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Doctor doctor = new Doctor(
                            rs.getString("UserID"),
                            rs.getString("DoctorID"),
                            rs.getString("FullName"),
                            rs.getDate("DateOfBirth") != null ? rs.getDate("DateOfBirth").toLocalDate() : LocalDate.now(),
                            rs.getString("Address"),
                            Gender.fromDatabase(rs.getString("Gender")),
                            rs.getString("PhoneNumber"),
                            rs.getString("SpecialtyID") != null ? Specialization.fromId(rs.getString("SpecialtyID")) : null,
                            rs.getString("Email"),
                            rs.getDate("CreatedAt") != null ? rs.getDate("CreatedAt").toLocalDate() : LocalDate.now()
                        );
                        doctors.add(doctor);
                    }
                }
            }
            
            return doctors;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách bác sĩ trực: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Lấy lịch làm việc của một bác sĩ cụ thể
     * @param doctorId ID của bác sĩ
     * @return Map với cấu trúc: ngày -> ca -> danh sách bác sĩ
     */
    public Map<String, Map<String, List<Doctor>>> getDoctorShiftSchedule(String doctorId) {
        Map<String, Map<String, List<Doctor>>> schedule = new HashMap<>();
        String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
        String[] shifts = {"Sáng", "Chiều", "Tối"};
        
        // Khởi tạo cấu trúc Map
        for (String day : days) {
            schedule.put(day, new HashMap<>());
            for (String shift : shifts) {
                schedule.get(day).put(shift, new ArrayList<>());
            }
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu!");
            }
            
            // Lấy tất cả ca làm việc của bác sĩ cụ thể
            String doctorShiftsSql = "SELECT DayOfWeek, ShiftType FROM DoctorSchedule " +
                                  "WHERE DoctorID = ? AND Status = 'Đang làm việc'";
            
            List<String[]> doctorShifts = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(doctorShiftsSql)) {
                stmt.setString(1, doctorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        doctorShifts.add(new String[]{rs.getString("DayOfWeek"), rs.getString("ShiftType")});
                    }
                }
            }
            
            // Với mỗi ca làm việc của bác sĩ, lấy tất cả bác sĩ được phân công
            for (String[] shift : doctorShifts) {
                String day = shift[0];
                String shiftName = shift[1];
                
                List<Doctor> doctors = getDoctorsForShift(day, shiftName);
                schedule.get(day).put(shiftName, doctors);
            }
            
            return schedule;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lịch làm việc: " + e.getMessage());
            e.printStackTrace();
            return schedule;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Lấy lịch làm việc của một bác sĩ cụ thể
     * @param doctorId ID của bác sĩ
     * @return Map với cấu trúc: ngày -> ca -> danh sách bác sĩ (chỉ chứa bác sĩ được tìm)
     */
    public Map<String, Map<String, List<Doctor>>> getScheduleForDoctor(String doctorId) {
        Map<String, Map<String, List<Doctor>>> schedule = new HashMap<>();
        String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
        String[] shifts = {"Sáng", "Chiều", "Tối"};
        
        // Khởi tạo cấu trúc Map
        for (String day : days) {
            schedule.put(day, new HashMap<>());
            for (String shift : shifts) {
                schedule.get(day).put(shift, new ArrayList<>());
            }
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu!");
            }
            
            // Lấy thông tin bác sĩ
            Doctor doctor = getDoctorInfo(doctorId);
            if (doctor == null) {
                return schedule;
            }
            
            // Lấy tất cả ca làm việc của bác sĩ cụ thể
            String sql = "SELECT DayOfWeek, ShiftType FROM DoctorSchedule " +
                       "WHERE DoctorID = ? AND Status = 'Đang làm việc'";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, doctorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String day = rs.getString("DayOfWeek");
                        String shiftName = rs.getString("ShiftType");
                        
                        // Chỉ thêm bác sĩ hiện tại vào danh sách, không thêm bác sĩ khác
                        List<Doctor> doctorList = new ArrayList<>();
                        doctorList.add(doctor);
                        
                        schedule.get(day).put(shiftName, doctorList);
                    }
                }
            }
            
            return schedule;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lịch làm việc: " + e.getMessage());
            e.printStackTrace();
            return schedule;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Lấy tất cả lịch trực của bác sĩ
     * @return Map với cấu trúc: ngày -> ca -> danh sách bác sĩ
     */
    public Map<String, Map<String, List<Doctor>>> getAllDoctorSchedules() {
        Map<String, Map<String, List<Doctor>>> schedules = new HashMap<>();
        String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
        String[] shifts = {"Sáng", "Chiều", "Tối"};
        
        // Khởi tạo cấu trúc Map
        for (String day : days) {
            schedules.put(day, new HashMap<>());
            for (String shift : shifts) {
                schedules.get(day).put(shift, new ArrayList<>());
            }
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu!");
            }
            
            // Lấy tất cả lịch trực từ database
            String sql = "SELECT ds.DoctorID, ds.DayOfWeek, ds.ShiftType, " +
                         "d.UserID, u.FullName, d.DateOfBirth, d.Gender, d.Address, " +
                         "s.SpecialtyID, s.SpecialtyName, u.Email, u.PhoneNumber, d.CreatedAt " +
                         "FROM DoctorSchedule ds " +
                         "JOIN Doctors d ON ds.DoctorID = d.DoctorID " +
                         "JOIN UserAccounts u ON d.UserID = u.UserID " +
                         "LEFT JOIN Specialties s ON d.SpecialtyID = s.SpecialtyID " +
                         "WHERE ds.Status = 'Đang làm việc' " +
                         "ORDER BY ds.DayOfWeek, ds.ShiftType";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String day = rs.getString("DayOfWeek");
                    String shift = rs.getString("ShiftType");
                    
                    Doctor doctor = new Doctor(
                        rs.getString("UserID"),
                        rs.getString("DoctorID"),
                        rs.getString("FullName"),
                        rs.getDate("DateOfBirth") != null ? rs.getDate("DateOfBirth").toLocalDate() : LocalDate.now(),
                        rs.getString("Address"),
                        Gender.fromDatabase(rs.getString("Gender")),
                        rs.getString("PhoneNumber"),
                        rs.getString("SpecialtyID") != null ? Specialization.fromId(rs.getString("SpecialtyID")) : null,
                        rs.getString("Email"),
                        rs.getDate("CreatedAt") != null ? rs.getDate("CreatedAt").toLocalDate() : LocalDate.now()
                    );
                    
                    if (schedules.containsKey(day) && schedules.get(day).containsKey(shift)) {
                        schedules.get(day).get(shift).add(doctor);
                    }
                }
            }
            
            return schedules;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy tất cả lịch làm việc: " + e.getMessage());
            e.printStackTrace();
            return schedules;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }
}