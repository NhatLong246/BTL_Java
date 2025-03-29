package entity;

// Lớp Medication (Thuốc)
public class Medication {
    private String medicationId; // Mã thuốc
    private String medicineName; // Tên thuốc
    private String Description; // Mô tả (thành phần, công dụng, đối tượng sử dụng)
    private String manufacturer; // Nhà sản xuất
    private String dosageForm; // Dạng bào chế (viên, nước, ...)
    private String sideEffects; // Tác dụng phụ

    private static int autoId = 1; // Biến đếm ID tự tăng của thuốc

    // Tạo mã thuốc tự động (MED-001, MED-002, ...)
    private String generateMedicationId() {
        return String.format("MED-%03d", autoId++);
    }

    public Medication() {}

    public Medication(String medicineName, String Description, String manufacturer, String dosageForm, String sideEffects) {
        this.medicationId = generateMedicationId();
        this.medicineName = medicineName;
        this.Description = Description;
        this.manufacturer = manufacturer;
        this.dosageForm = dosageForm;
        this.sideEffects = sideEffects;
    }

    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }
}
