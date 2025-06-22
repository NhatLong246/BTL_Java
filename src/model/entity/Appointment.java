package model.entity;

import model.enums.AppointmentStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

// Lịch hẹn
public class Appointment {
    private String appointmentId;       // Mã lịch hẹn (VD: "AP-001")
    private String patientId;           // Mã bệnh nhân
    private String doctorId;            // Mã bác sĩ
    private LocalDateTime appointmentDate; // Ngày giờ hẹn
    private AppointmentStatus status;   // Trạng thái lịch hẹn
    private String notes;               // Ghi chú

    // Constructor
    public Appointment(Connection conn, String patientId, String doctorId, LocalDateTime appointmentDate) {
        // Kiểm tra mã bệnh nhân không được để trống
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bệnh nhân không được để trống!");
        }
        // Kiểm tra ngày giờ hẹn không được để trống
        if (appointmentDate == null) {
            throw new IllegalArgumentException("Ngày giờ hẹn không được để trống!");
        }

        this.appointmentId = generateAppointmentId(conn); // Sinh mã lịch hẹn từ cơ sở dữ liệu
        this.patientId = patientId;
        this.doctorId = doctorId; // Có thể để trống
        this.appointmentDate = appointmentDate;
        this.status = AppointmentStatus.PENDING; // Mặc định là "Chờ"
        this.notes = null; // Mặc định ghi chú là rỗng
    }

    // Phương thức sinh mã lịch hẹn tự động (AP-001, AP-002,...)
    private String generateAppointmentId(Connection conn) {
        String newAppointmentID = "APP-001"; // Mã mặc định nếu bảng chưa có dữ liệu
        String sql = "SELECT MAX(CAST(REGEXP_SUBSTR(AppointmentID, '[0-9]+') AS UNSIGNED)) AS maxID FROM Appointments";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next() && rs.getInt("maxID") > 0) {
                int maxID = rs.getInt("maxID") + 1; // Lấy số lớn nhất và tăng lên 1
                newAppointmentID = String.format("APP-%03d", maxID); // Định dạng thành AP-XXX
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi sinh mã lịch hẹn: " + e.getMessage());
        }
        return newAppointmentID;
    }

    // Getters và Setters
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bệnh nhân không được để trống!");
        }
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId; // Có thể để trống
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        if (appointmentDate == null) {
            throw new IllegalArgumentException("Ngày giờ hẹn không được để trống!");
        }
        this.appointmentDate = appointmentDate;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes; // Có thể để trống
    }

    // Cập nhật trạng thái lịch hẹn
    public void updateStatus(AppointmentStatus newStatus) {
        this.status = newStatus;
    }
}