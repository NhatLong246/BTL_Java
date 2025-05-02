package model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class MedicalRecord {
    private String recordId;        // Mã hồ sơ y tế (VD: "MR-001")
    private String patientId;       // Mã bệnh nhân
    private String doctorId;        // Mã bác sĩ
    private String diagnosis;       // Chẩn đoán
    private String treatmentPlan;   // Kế hoạch điều trị
    private LocalDate recordDate;   // Ngày ghi nhận
    private boolean isHistory;      // Cờ đánh dấu lịch sử

    private boolean validateFields = true;

    public MedicalRecord() {
        this.validateFields = false; // Không kiểm tra khi tạo từ database
    }

    // Constructor
    public MedicalRecord(Connection conn, String patientId, String doctorId, String diagnosis,
                         String treatmentPlan, LocalDate recordDate) {
        // Kiểm tra các trường không được để trống
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalArgumentException("Chẩn đoán không được để trống!");
        }
        if (treatmentPlan == null || treatmentPlan.trim().isEmpty()) {
            throw new IllegalArgumentException("Kế hoạch điều trị không được để trống!");
        }
        if (recordDate == null) {
            throw new IllegalArgumentException("Ngày ghi nhận không được để trống!");
        }

        this.recordId = generateRecordId(conn); // Sinh mã tự động
        this.patientId = patientId; // Có thể null
        this.doctorId = doctorId;   // Có thể null
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
        this.recordDate = recordDate;
        this.isHistory = false;     // Mặc định là FALSE
        this.validateFields = true;
    }

    // Phương thức sinh mã hồ sơ y tế tự động (MR-001, MR-002,...)
    private String generateRecordId(Connection conn) {
        String newRecordId = "MR-001"; // Giá trị mặc định nếu bảng rỗng
        String sql = "SELECT MAX(CAST(REGEXP_SUBSTR(RecordID, '[0-9]+') AS UNSIGNED)) AS maxID FROM MedicalRecords";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next() && rs.getInt("maxID") > 0) {
                int maxID = rs.getInt("maxID") + 1;
                newRecordId = String.format("MR-%03d", maxID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi sinh mã hồ sơ y tế: " + e.getMessage());
        }
        return newRecordId;
    }

    // Getters và Setters
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId; // Có thể null
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId; // Có thể null
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalArgumentException("Chẩn đoán không được để trống!");
        }
        this.diagnosis = diagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    
    public void setTreatmentPlan(String treatmentPlan) {
        // Chỉ kiểm tra khi đang tạo hồ sơ mới, không phải khi đọc từ database
        if (this.validateFields && (treatmentPlan == null || treatmentPlan.isEmpty())) {
            throw new IllegalArgumentException("Kế hoạch điều trị không được để trống!");
        }
        this.treatmentPlan = treatmentPlan;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        if (recordDate == null) {
            throw new IllegalArgumentException("Ngày ghi nhận không được để trống!");
        }
        this.recordDate = recordDate;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }
}