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

    /**
     * Lưu đơn thuốc vào cơ sở dữ liệu
     * @param doctorId ID bác sĩ
     * @param prescriptionData Dữ liệu đơn thuốc
     * @param medicineList Danh sách thuốc
     * @return true nếu lưu thành công
     * @throws SQLException nếu có lỗi SQL
     */
    public boolean savePrescription(String doctorId, Map<String, Object> prescriptionData, 
                                  List<Map<String, Object>> medicineList) throws SQLException {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("doctorId không được để trống");
        }
        if (prescriptionData == null || prescriptionData.get("prescriptionId") == null || 
            prescriptionData.get("patientId") == null) {
            throw new IllegalArgumentException("prescriptionData không hợp lệ");
        }
        if (medicineList == null || medicineList.isEmpty()) {
            throw new IllegalArgumentException("Danh sách thuốc không được để trống");
        }
        for (Map<String, Object> medicine : medicineList) {
            String name = (String) medicine.get("name");
            String dosage = (String) medicine.get("dosage");
            String instruction = (String) medicine.get("instruction");
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Tên thuốc không được để trống");
            }
            if (dosage == null || dosage.trim().isEmpty()) {
                throw new IllegalArgumentException("Liều dùng không được để trống");
            }
            if (instruction == null || instruction.trim().isEmpty()) {
                throw new IllegalArgumentException("Cách dùng không được để trống");
            }
        }
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

    /**
     * Lưu đơn thuốc và cập nhật trạng thái cuộc hẹn thành hoàn thành
     * @param doctorId ID bác sĩ
     * @param prescriptionData Dữ liệu đơn thuốc
     * @param medicineList Danh sách thuốc
     * @param patientId ID bệnh nhân
     * @return true nếu lưu thành công
     * @throws SQLException nếu có lỗi SQL
     */
    public boolean savePrescriptionAndCompleteAppointment(String doctorId, Map<String, Object> prescriptionData, 
                                  List<Map<String, Object>> medicineList, String patientId) throws SQLException {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("doctorId không được để trống");
        }
        if (prescriptionData == null || prescriptionData.get("prescriptionId") == null || 
            prescriptionData.get("patientId") == null) {
            throw new IllegalArgumentException("prescriptionData không hợp lệ");
        }
        if (medicineList == null || medicineList.isEmpty()) {
            throw new IllegalArgumentException("Danh sách thuốc không được để trống");
        }
        
        // Gọi hai hàm trong một transaction
        boolean result = repository.savePrescriptionAndCompleteAppointment(doctorId, prescriptionData, medicineList, patientId);
        
        return result;
    }
}