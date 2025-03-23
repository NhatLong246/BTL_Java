package entity;

// Lớp PrescriptionDetail (Chi tiết đơn thuốc)
public class PrescriptionDetail {
    private String prescriptionDetailId; // Mã chi tiết đơn thuốc
    private Prescription prescription; // Đơn thuốc
    private Medication medication; // Thuốc
    private String dosage; // Liều lượng
    private String iInstructions; // Hướng dẫn sử dụng

    private static int autoId = 1; // Biến đếm ID tự tăng của chi tiết đơn thuốc

    // Tạo mã chi tiết đơn thuốc tự động (PRD-001, PRD-002, ...)
    private String generatePrescriptionDetailId() {
        return String.format("PRD-%03d", autoId++);
    }

    public PrescriptionDetail() {}

    public PrescriptionDetail(Prescription prescription, Medication medication, String dosage, String iInstructions) {
        this.prescriptionDetailId = generatePrescriptionDetailId();
        this.prescription = prescription;
        this.medication = medication;
        this.dosage = dosage;
        this.iInstructions = iInstructions;
    }

    public String getPrescriptionDetailId() {
        return prescriptionDetailId;
    }

    public void setPrescriptionDetailId(String prescriptionDetailId) {
        this.prescriptionDetailId = prescriptionDetailId;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getiInstructions() {
        return iInstructions;
    }

    public void setiInstructions(String iInstructions) {
        this.iInstructions = iInstructions;
    }
}
