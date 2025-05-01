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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminRepository {

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT d.DoctorID, u.UserID, u.FullName, u.Email, u.PhoneNumber, d.DateOfBirth, d.Gender, d.Address, " +
                     "s.SpecialtyID, s.SpecialtyName, d.CreatedAt " +
                     "FROM Doctors d " +
                     "JOIN UserAccounts u ON d.UserID = u.UserID " +
                     "LEFT JOIN Specialties s ON d.SpecialtyID = s.SpecialtyID " +
                     "WHERE u.Role = 'Bác sĩ' AND u.IsLocked = 0";
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
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return false;
            }
            conn.setAutoCommit(false);

            String doctorId = Doctor.generateNewDoctorID(conn);

            String userSql = "INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, PasswordHash, IsLocked) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                String username = email.split("@")[0];
                pstmt.setString(1, userId);
                pstmt.setString(2, username);
                pstmt.setString(3, fullName);
                pstmt.setString(4, "Bác sĩ");
                pstmt.setString(5, email);
                pstmt.setString(6, phoneNumber);
                pstmt.setString(7, hashPassword("Doctor@123"));
                pstmt.setBoolean(8, false); // Mặc định không khóa
                pstmt.executeUpdate();
            }

            String doctorSql = "INSERT INTO Doctors (DoctorID, UserID, DateOfBirth, Gender, Address, SpecialtyID, CreatedAt) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(doctorSql)) {
                pstmt.setString(1, doctorId);
                pstmt.setString(2, userId);
                pstmt.setDate(3, java.sql.Date.valueOf(dateOfBirth));
                pstmt.setString(4, gender != null ? gender.getVietnamese() : null);
                pstmt.setString(5, address);
                pstmt.setString(6, specialtyId);
                pstmt.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
                pstmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Đã tạo bác sĩ mới với DoctorID: " + doctorId);
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
        String passwordHash = hashPassword(defaultPassword);
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

            String sql = "UPDATE UserAccounts SET IsLocked = ?, PasswordHash = ? WHERE UserID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBoolean(1, false);
                pstmt.setString(2, passwordHash);
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
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi mở khóa bác sĩ: " + e.getMessage());
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

    public boolean saveDoctorSchedule(String doctorId, boolean[][] schedule) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return false;
            }
            conn.setAutoCommit(false);

            // Kiểm tra bác sĩ có tồn tại không
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

            // Lưu lịch mới (chỉ lưu các ca làm việc)
            String insertSql = "INSERT INTO DoctorSchedule (DoctorID, DayOfWeek, ShiftType, Status) VALUES (?, ?, ?, ?)";
            String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
            String[] shifts = {"Sáng", "Chiều", "Tối"};
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (int shift = 0; shift < 3; shift++) {
                    for (int day = 0; day < 7; day++) {
                        if (schedule[shift][day]) {
                            pstmt.setString(1, doctorId);
                            pstmt.setString(2, days[day]);
                            pstmt.setString(3, shifts[shift]);
                            pstmt.setString(4, "Đang làm việc");
                            pstmt.addBatch();
                        }
                    }
                }
                pstmt.executeBatch();
            }

            conn.commit();
            System.out.println("Đã lưu lịch làm việc cho bác sĩ với DoctorID: " + doctorId);
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lưu lịch làm việc: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback giao dịch: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("Đã đóng kết nối cơ sở dữ liệu");
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    e.printStackTrace();
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
}