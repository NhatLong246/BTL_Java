package model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

// Lớp Prescription (Đơn thuốc)
public class Prescription {
    private String prescriptionId; // Mã đơn thuốc
    private String patientId; // Mã bệnh nhân (FK)
    private String doctorId; // Mã bác sĩ (FK)
    private LocalDate prescriptionDate; // Ngày kê đơn
    private List<PrescriptionDetail> prescriptionDetails; // Danh sách chi tiết đơn thuốc

    private static int autoId = 1; // Biến đếm ID tự tăng của đơn thuốc

    // Tạo mã đơn thuốc tự động (PRE-001, PRE-002, ...)
    public static String generateNewPrescriptionID(Connection conn) {
        String newPrescriptionID = "PRE-001"; // ID mặc định nếu bảng rỗng
        String sql = "SELECT MAX(CAST(REGEXP_SUBSTR(PrescriptionID, '[0-9]+') AS UNSIGNED)) AS maxID FROM Prescriptions";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next() && rs.getInt("maxID") > 0) {
                int maxID = rs.getInt("maxID") + 1; // Lấy số lớn nhất +1
                newPrescriptionID = String.format("PRE-%03d", maxID); // Định dạng PRE-XXX
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newPrescriptionID;
    }


    public Prescription() {}

    public Prescription(String patientId, String doctorId, LocalDate prescriptionDate, Connection conn) {
        this.prescriptionId = generateNewPrescriptionID(conn);
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.prescriptionDate = LocalDate.now();
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public List<PrescriptionDetail> getPrescriptionDetails() {
        return prescriptionDetails;
    }

    public void setPrescriptionDetails(List<PrescriptionDetail> prescriptionDetails) {
        this.prescriptionDetails = prescriptionDetails;
    }
}
