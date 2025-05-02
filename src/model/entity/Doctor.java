package model.entity;

import model.enums.Gender;
import model.enums.Specialization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class Doctor extends Person {
    private static int id = 1;
    private String doctorId;
    private String userId;    // Liên kết với UserAccounts
    private Specialization specialization;  // Chuyên khoa
    private String email;
    private LocalDate createdAt; // Ngày tạo (tuyển dụng)

    // Thêm biến lưu thông tin đăng nhập tạm thời
    private String tempUsername;
    private String tempPassword;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public Doctor() {}

    // Constructor khi thêm bác sĩ mới (Tạo ID từ database, lấy ngày hiện tại)
    public Doctor(Connection conn, String userId, String fullName, LocalDate dateOfBirth, String address, Gender gender, String phoneNumber, Specialization specialization, String email) throws SQLException {
        super(fullName, dateOfBirth, address, gender, phoneNumber);
        this.userId = userId;
        this.doctorId = generateNewDoctorID(conn); // Lấy ID từ database
        this.specialization = specialization;
        setEmail(email);
        this.createdAt = LocalDate.now(); // Lấy ngày hiện tại
    }

    // Constructor khi lấy dữ liệu từ database (Giữ nguyên ngày tuyển dụng)
    public Doctor(String userId, String doctorId, String fullName, LocalDate dateOfBirth, String address, Gender gender, String phoneNumber, Specialization specialization, String email, LocalDate createdAt) {
        super(fullName, dateOfBirth, address, gender, phoneNumber);
        this.userId = userId;
        this.doctorId = doctorId;
        this.specialization = specialization;
        setEmail(email);
        this.createdAt = createdAt;
    }

    public String generateDoctorId() {
        return String.format("DOC-%03d", id++);
    }

     // Phương thức static để tạo DoctorID mới
    public static String generateNewDoctorID(Connection conn) throws SQLException {
        String query = "SELECT MAX(SUBSTRING(DoctorID, 5)) AS maxID FROM Doctors WHERE DoctorID LIKE 'DOC-%'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            int maxID = 0;
            if (rs.next()) {
                String maxIDStr = rs.getString("maxID");
                if (maxIDStr != null && !maxIDStr.isEmpty()) {
                    try {
                        maxID = Integer.parseInt(maxIDStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi chuyển đổi mã DoctorID: " + e.getMessage());
                    }
                }
            }
            return String.format("DOC-%03d", maxID + 1); // Định dạng DOC-XXX
        }
    }
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email không hợp lệ! Vui lòng nhập đúng định dạng.");
        }
        this.email = email;
    }

    public String getTempUsername() {
        return tempUsername;
    }
    
    public void setTempUsername(String tempUsername) {
        this.tempUsername = tempUsername;
    }
    
    public String getTempPassword() {
        return tempPassword;
    }
    
    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }
    
    // Thêm phương thức để lưu thông tin đăng nhập
    public void setLoginCredentials(String username, String password) {
        this.tempUsername = username;
        this.tempPassword = password;
    }
}
