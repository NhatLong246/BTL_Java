package enums;

public enum StaffRole {
	NURSE("Nurse"),          // Y tá
    ORDERLY("Orderly"),      // Hộ lý
    ADMIN("Administrative Staff"), // Nhân viên hành chính
    DOCTOR("Doctor"),        // Bác sĩ
    TECHNICIAN("Technician");// Kỹ thuật viên

    private final String description;

    StaffRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
