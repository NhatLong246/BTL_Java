package model.entity;

// Lớp Medication (Thuốc)
public class Medication {
    private String medicationId; // Mã thuốc
    private String medicineName; // Tên thuốc
    private String description; // Mô tả (thành phần, công dụng, đối tượng sử dụng)
    private String manufacturer; // Nhà sản xuất
    private String dosageForm; // Dạng bào chế (viên, nước, bội,...)
    private String sideEffects; // Tác dụng phụ

    public Medication() {}

    public Medication(String medicationId, String medicineName, String description, String manufacturer, String dosageForm, String sideEffects) {
        this.medicationId = medicationId;
        this.medicineName = medicineName;
        this.description = description;
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
        return description;
    }

    public void setDescription(String description) {
        description = description;
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
