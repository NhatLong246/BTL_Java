package controller;

import model.entity.Patient;
import model.repository.PatientRepository;
import view.PatientView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utils.ReportExporter;

import database.DatabaseConnection;

public class PatientController {
    private final PatientView view;
    private final Patient patient;
    private final PatientRepository repository;

    public PatientController(PatientView view, Patient patient) throws SQLException {
        this.view = view;
        this.patient = patient;
        this.repository = new PatientRepository();
    }

    public void showHome() {
        view.setSelectedButton(view.getBtnHome());
        view.showHome();
    }

    
    public void showPatientInfo() {
        view.setSelectedButton(view.getBtnViewInfo());
        Map<String, Object> vitalSigns = getVitalSigns();
        view.showPatientInfo(vitalSigns); // Cập nhật để truyền dữ liệu chỉ số sức khỏe
    }

    public void showAppointments() {
        view.setSelectedButton(view.getBtnViewAppointments());
        view.showAppointments(repository.getAppointments(patient.getPatientID()));
    }

    public void showMedicalHistory() {
        view.setSelectedButton(view.getBtnViewMedicalHistory());
        view.showMedicalHistory(repository.getMedicalHistory(patient.getPatientID()));
    }

    public void showPayFees() {
        view.setSelectedButton(view.getBtnPayFees());
        view.showPayFees();
    }

    public void showPaymentHistory() {
        view.setSelectedButton(view.getBtnPaymentHistory());
        view.showPaymentHistory(repository.getPaymentHistory(patient.getPatientID()));
    }

    public List<Object[]> getBills() {
        return repository.getBills(patient.getPatientID());
    }

    public boolean payBill(String billID, String paymentMethod) {
        return repository.payBill(billID, paymentMethod);
    }

    public List<Map<String, Object>> getPrescriptionsForPatient(String patientId) {
        List<Map<String, Object>> prescriptions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                // Sử dụng GROUP_CONCAT để gộp các chẩn đoán thành một chuỗi
                "SELECT p.PrescriptionID, p.PrescriptionDate, d.FullName as DoctorName, " +
                "GROUP_CONCAT(DISTINCT mr.Diagnosis SEPARATOR ', ') as Diagnosis " +
                "FROM Prescriptions p " +
                "JOIN Doctors d ON p.DoctorID = d.DoctorID " +
                "LEFT JOIN MedicalRecords mr ON p.PatientID = mr.PatientID AND p.DoctorID = mr.DoctorID " +
                "WHERE p.PatientID = ? " +
                "GROUP BY p.PrescriptionID, p.PrescriptionDate, d.FullName " + 
                "ORDER BY p.PrescriptionDate DESC")) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> prescription = new HashMap<>();
                prescription.put("prescriptionId", rs.getString("PrescriptionID"));
                prescription.put("date", rs.getDate("PrescriptionDate"));
                prescription.put("doctorName", rs.getString("DoctorName"));
                prescription.put("diagnosis", rs.getString("Diagnosis"));
                prescriptions.add(prescription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL error: " + e.getMessage());
        }
        
        return prescriptions;
    }

    public Map<String, Object> getPrescriptionDetails(String prescriptionId) {
        Map<String, Object> details = new HashMap<>();
        List<Map<String, Object>> medications = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Lấy thông tin chung về đơn thuốc
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT p.PrescriptionID, p.PrescriptionDate, d.FullName as DoctorName, " +
                "mr.Diagnosis, mr.TreatmentPlan " +
                "FROM Prescriptions p " +
                "JOIN Doctors d ON p.DoctorID = d.DoctorID " +
                "LEFT JOIN MedicalRecords mr ON p.PatientID = mr.PatientID AND p.DoctorID = mr.DoctorID " +
                "WHERE p.PrescriptionID = ?")) {
                
                stmt.setString(1, prescriptionId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    details.put("prescriptionId", rs.getString("PrescriptionID"));
                    details.put("date", rs.getDate("PrescriptionDate"));
                    details.put("doctorName", rs.getString("DoctorName"));
                    details.put("diagnosis", rs.getString("Diagnosis"));
                    details.put("notes", rs.getString("TreatmentPlan"));
                }
            }
            
            // Lấy danh sách thuốc trong đơn
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT pd.MedicationID, m.MedicineName, m.DosageForm, pd.Dosage, pd.Instructions, " +
                "m.Manufacturer, m.SideEffects " +
                "FROM PrescriptionDetails pd " +
                "JOIN Medications m ON pd.MedicationID = m.MedicationID " +
                "WHERE pd.PrescriptionID = ?")) {
                
                stmt.setString(1, prescriptionId);
                ResultSet rs = stmt.executeQuery();
                
                int index = 1;
                while (rs.next()) {
                    Map<String, Object> medication = new HashMap<>();
                    medication.put("index", index++);
                    medication.put("medicationId", rs.getString("MedicationID"));
                    medication.put("medicineName", rs.getString("MedicineName"));
                    medication.put("dosageForm", rs.getString("DosageForm"));
                    medication.put("dosage", rs.getString("Dosage"));
                    medication.put("instructions", rs.getString("Instructions"));
                    medication.put("manufacturer", rs.getString("Manufacturer"));
                    medication.put("sideEffects", rs.getString("SideEffects"));
                    medications.add(medication);
                }
            }
            
            details.put("medications", medications);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return details;
    }

    // Phương thức để lấy danh sách các chẩn đoán từ cơ sở dữ liệu
    public List<String> getDiagnosisList() {
        List<String> diagnoses = new ArrayList<>();
        diagnoses.add("Tất cả");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
            "SELECT DISTINCT Diagnosis FROM MedicalRecords WHERE PatientID = ? AND Diagnosis IS NOT NULL")) {
            
            stmt.setString(1, patient.getPatientID());        
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String diagnosis = rs.getString("Diagnosis");
                if (diagnosis != null && !diagnosis.isEmpty()) {
                    diagnoses.add(diagnosis);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return diagnoses;
    }

        public List<Map<String, Object>> getPrescriptionsByDiagnosis(String patientId, String diagnosis) {
        List<Map<String, Object>> prescriptions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt;
            
            if ("Tất cả".equals(diagnosis)) {
                stmt = conn.prepareStatement(
                    "SELECT p.PrescriptionID, p.PrescriptionDate, d.FullName as DoctorName, " +
                    "mr.Diagnosis " +
                    "FROM Prescriptions p " +
                    "JOIN Doctors d ON p.DoctorID = d.DoctorID " +
                    "LEFT JOIN MedicalRecords mr ON p.PatientID = mr.PatientID AND p.DoctorID = mr.DoctorID " +
                    "WHERE p.PatientID = ? " +
                    "ORDER BY p.PrescriptionDate DESC");
                stmt.setString(1, patientId);
            } else {
                stmt = conn.prepareStatement(
                    "SELECT p.PrescriptionID, p.PrescriptionDate, d.FullName as DoctorName, " +
                    "mr.Diagnosis " +
                    "FROM Prescriptions p " +
                    "JOIN Doctors d ON p.DoctorID = d.DoctorID " +
                    "JOIN MedicalRecords mr ON p.PatientID = mr.PatientID AND p.DoctorID = mr.DoctorID " +
                    "WHERE p.PatientID = ? AND mr.Diagnosis LIKE ? " +
                    "ORDER BY p.PrescriptionDate DESC");
                stmt.setString(1, patientId);
                stmt.setString(2, "%" + diagnosis + "%");
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> prescription = new HashMap<>();
                prescription.put("prescriptionId", rs.getString("PrescriptionID"));
                prescription.put("date", rs.getDate("PrescriptionDate"));
                prescription.put("doctorName", rs.getString("DoctorName"));
                prescription.put("diagnosis", rs.getString("Diagnosis"));
                prescriptions.add(prescription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL error: " + e.getMessage());
        }
        
        return prescriptions;
    }
    
    // Thêm các phương thức sau vào lớp PatientController
    public boolean exportMedicalHistoryToExcel(List<String[]> medicalHistory, String filePath, String patientName) {
        try {
            // Cần thêm phương thức này vào ReportExporter
            // return ReportExporter.exportMedicalHistoryToExcel(medicalHistory, filePath, patientName);
            JOptionPane.showMessageDialog(view, 
                "Chức năng xuất Excel đang được phát triển!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean exportMedicalHistoryToPdf(List<String[]> medicalHistory, String filePath, String patientName) {
        try {
            // Cần thêm phương thức này vào ReportExporter
            // return ReportExporter.exportMedicalHistoryToPdf(medicalHistory, filePath, patientName);
            JOptionPane.showMessageDialog(view, 
                "Chức năng xuất PDF đang được phát triển!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportAppointmentsToExcel(List<String[]> appointments, String filePath) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Lịch hẹn");
            
            // Tạo font cho tiêu đề
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            
            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Tạo header row
            String[] columns = {"ID Lịch hẹn", "Ngày hẹn", "Thời gian", "Bác sĩ", "Phòng", "Trạng thái"};
            Row headerRow = sheet.createRow(0);
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Điền dữ liệu
            int rowNum = 1;
            for (String[] appointment : appointments) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < appointment.length; i++) {
                    row.createCell(i).setCellValue(appointment[i] != null ? appointment[i] : "");
                }
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Ghi file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            
            workbook.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách hồ sơ bệnh án của bệnh nhân hiện tại
     * @return List<String[]> danh sách hồ sơ bệnh án
     */
    public List<String[]> getMedicalHistory() {
        return repository.getMedicalHistory(this.patient.getPatientID());
    }

    public List<String[]> getAppointments() {
        try {
            return this.repository.getAppointments(patient.getPatientID());
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public Map<String, Object> getVitalSigns() {
        return repository.getVitalSigns(patient.getPatientID());
    }

   
}