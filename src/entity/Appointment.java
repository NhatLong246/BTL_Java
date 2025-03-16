package entity;

import java.time.LocalDateTime;

public class Appointment {
    private static int id = 1;   // ID tự động tăng
    private String appointmentId;       // ID lịch hẹn (VD: "AP-001")
    private Patient patient;            // Bệnh nhân
    private Doctor doctor;              // Bác sĩ
    private LocalDateTime appointmentTime; // Thời gian hẹn
    private AppointmentStatus status;   // Trạng thái lịch hẹn

    private String generateAppointmentId() {
        return String.format("AP-%03d", id++);
    }

    public Appointment() {}

    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentTime) {
        this.appointmentId = generateAppointmentId();
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
        this.status = AppointmentStatus.PENDING;
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

    public void displayInfo() {
        System.out.println("Mã lịch hẹn: " + appointmentId);
        System.out.println("Bệnh nhân: " + patient.getName());
        System.out.println("Bác sĩ: " + doctor.getName());
        System.out.println("Thời gian hẹn: " + appointmentTime);
        System.out.println("Trạng thái: " + status.getVietnamese());
        System.out.println("------------------------------------");
    }
}
