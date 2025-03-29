package entity;

import java.time.LocalDate;
import java.util.List;

// Lớp Prescription (Đơn thuốc)
public class Prescription {
    private String prescriptionId; // Mã đơn thuốc
    private String patient; // Bệnh nhân
    private String doctor; // Bác sĩ kê đơn
    private LocalDate prescriptionDate; // Ngày kê đơn
    private List<PrescriptionDetail> prescriptionDetails; // Danh sách chi tiết đơn thuốc

    private static int autoId = 1; // Biến đếm ID tự tăng của đơn thuốc

    // Tạo mã đơn thuốc tự động (PRE-001, PRE-002, ...)
    private String generatePrescriptionId() {
        return String.format("PRE-%03d", autoId++);
    }

    public Prescription() {}

    public Prescription(String patient, String doctor, LocalDate prescriptionDate) {
        this.prescriptionId = generatePrescriptionId();
        this.patient = patient;
        this.doctor = doctor;
        this.prescriptionDate = prescriptionDate;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
}
