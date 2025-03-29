package entity;

import enums.AppointmentStatus;

import java.time.LocalDateTime;

// Lịch hẹn
public class Appointment {
    private static int id = 1;   // ID tự động tăng
    private String appointmentId;       // ID lịch hẹn (VD: "AP-001")
    private Patient patient;            // Bệnh nhân
    private Doctor doctor;              // Bác sĩ
    private LocalDateTime appointmentTime; // Thời gian hẹn
    private AppointmentStatus status;   // Trạng thái lịch hẹn

    // Constructor
    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentTime) {
        this.appointmentId = generateAppointmentId();
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
        this.status = AppointmentStatus.PENDING; // Mặc định là "Chờ xác nhận"
    }

    // Tạo ID tự động (AP-001, AP-002,...)
    private String generateAppointmentId() {
        return String.format("AP-%03d", id++);
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    // Cập nhật trạng thái lịch hẹn
    public void updateStatus(AppointmentStatus newStatus) {
        this.status = newStatus;
    }

}
