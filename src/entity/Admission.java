package entity;

import java.time.LocalDate;

// Lớp Admission (Quản lý nhập viện bệnh nhân)
public class Admission {
    private String patientId; // Mã bệnh nhân (PK, FK)
    private LocalDate admissionDate; // Ngày nhập viện (PK)
    private String doctorId; // Mã bác sĩ (FK)
    private String roomId; // Mã phòng bệnh (FK)
    private LocalDate dischargeDate; // Ngày xuất viện
    private String notes; // Ghi chú

    public Admission() {}

    //constructor với ngày xuất viện null
    public Admission(String patientId, LocalDate admissionDate, String doctorId, String roomId, String notes) {
        this.patientId = patientId;
        this.admissionDate = admissionDate;
        this.doctorId = doctorId;
        this.roomId = roomId;
        this.dischargeDate = null;
        this.notes = notes;
    }

    //constructor với ngày xuất viện khác null
    public Admission(String patientId, LocalDate admissionDate, String doctorId, String roomId, LocalDate dischargeDate, String notes) {
        this.patientId = patientId;
        this.admissionDate = admissionDate;
        this.doctorId = doctorId;
        this.roomId = roomId;
        this.dischargeDate = dischargeDate;
        this.notes = notes;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDate getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDate dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
