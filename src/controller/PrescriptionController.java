package controller;

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
    
    
    public boolean savePrescription(String doctorId, Map<String, Object> prescriptionData, 
                                  List<Map<String, Object>> medicineList) {
        return repository.savePrescription(doctorId, prescriptionData, medicineList);
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