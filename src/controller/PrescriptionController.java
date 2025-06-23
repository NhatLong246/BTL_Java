package controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import model.repository.DoctorRepository;
import view.PrescriptionDetailsDoctorView;

public class PrescriptionController {
    private PrescriptionDetailsDoctorView view;
    private DoctorRepository repository;
    
    public PrescriptionController(PrescriptionDetailsDoctorView view) {
        this.view = view;
        this.repository = new DoctorRepository();
    }

    
    /**
     * Sinh mã đơn thuốc tự động
     */
    public String generatePrescriptionId() {
        return repository.generateNewPrescriptionId();
    }
    
    
    public boolean savePrescription(String doctorId, Map<String, Object> prescriptionData, List<Map<String, Object>> medicineList) {
        try {
            // Log data for debugging
            System.out.println("Prescription data:");
            for (Map.Entry<String, Object> entry : prescriptionData.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            
            System.out.println("Medicine list (size: " + medicineList.size() + "):");
            for (int i = 0; i < medicineList.size(); i++) {
                Map<String, Object> medicine = medicineList.get(i);
                System.out.println("Medicine #" + (i+1) + ":");
                for (Map.Entry<String, Object> entry : medicine.entrySet()) {
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                }
            }
            
            return repository.savePrescription(doctorId, prescriptionData, medicineList);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lưu đơn thuốc: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu đơn thuốc: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy chẩn đoán gần nhất của bệnh nhân
     * @param patientId ID của bệnh nhân
     * @return Chẩn đoán gần nhất
     */
    public String getLatestDiagnosis(String patientId) {
        return repository.getLatestDiagnosis(patientId);
    }

    /**
     * Lấy danh sách tất cả thuốc
     * @return Danh sách thuốc
     */
    public List<Map<String, Object>> getAllMedications() {
        return repository.getAllMedications();
    }
    
    /**
     * Tìm kiếm thuốc theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách thuốc phù hợp
     */
    public List<Map<String, Object>> searchMedications(String keyword) {
        return repository.searchMedications(keyword);
    }
}