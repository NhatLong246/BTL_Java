package entity;

//Hồ sơ khám bệnh

import java.time.LocalDateTime;

public class MedicalRecords {
    private String recordId; // ID hồ sơ khám bệnh
    private Patient patient; // Bệnh nhân
    private Doctor doctor; // Bác sĩ
    private String diagnosis; // Chuẩn đoán bệnh
    private String treatmentPlan; // Kế hoạch điều trị
//    private List<Medication> medications; // Danh sách thuốc
    private LocalDateTime recordDate; // Ngày khám
    private LocalDateTime createdAt; // Ngày tạo hồ sơ (xem xét xóa trường này)

    private static int autoId = 1; // Biến đếm ID tự tăng của hồ sơ bệnh án

    // Tạo mã hồ sơ bệnh án tự động (REC-001, REC-002, ...)
    private String generateRecordId() {
        return String.format("REC-%03d", autoId++);
    }

    // Constructor
    public MedicalRecords() {}

    public MedicalRecords(String recordId, Patient patient, Doctor doctor, String diagnosis, String treatmentPlan, LocalDateTime recordDate, LocalDateTime createdAt) {
        this.recordId = generateRecordId();
        this.patient = patient;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
        this.recordDate = recordDate;
        this.createdAt = createdAt;
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

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}
