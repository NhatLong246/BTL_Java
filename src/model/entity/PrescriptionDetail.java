package model.entity;

// Lớp PrescriptionDetail (Chi tiết đơn thuốc)
public class PrescriptionDetail {
    private Prescription prescription; // Đơn thuốc (khóa ngoại)
    private Medication medication; // Thuốc (khóa ngoại)
    private String dosage; // Liều lượng
    private String instructions; // Hướng dẫn sử dụng

    public PrescriptionDetail() {}

    public PrescriptionDetail(Prescription prescription, Medication medication, String dosage, String instructions) {
        this.prescription = prescription;
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
