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

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public Doctor() {}

    // Constructor khi thêm bác sĩ mới (Tạo ID từ database, lấy ngày hiện tại)
    public Doctor(Connection conn, String userId, String fullName, LocalDate dateOfBirth, String address, Gender gender, String phoneNumber, Specialization specialization, String email) {
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

    private String generateDoctorId() {
        return String.format("DOC-%03d", id++);
    }

    // Tạo mã bác sĩ mới (Tìm số lớn nhất từ database rồi +1)
    public static String generateNewDoctorID(Connection conn) {
        String newDoctorID = "DOC-001";
        String sql = "SELECT MAX(CAST(REGEXP_SUBSTR(DoctorID, '[0-9]+') AS UNSIGNED)) AS maxID FROM Doctors";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next() && rs.getInt("maxID") > 0) {
                int maxID = rs.getInt("maxID") + 1;
                newDoctorID = String.format("DOC-%03d", maxID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newDoctorID;
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
}
