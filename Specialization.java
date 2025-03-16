package entity;

public enum Specialization {
    GENERAL_MEDICINE("Nội khoa"),
    PEDIATRICS("Nhi khoa"),
    CARDIOLOGY("Tim mạch"),
    ORTHOPEDICS("Chấn thương chỉnh hình"),
    DERMATOLOGY("Da liễu"),
    NEUROLOGY("Thần kinh"),
    ONCOLOGY("Ung bướu"),
    PSYCHIATRY("Tâm thần"),
    RADIOLOGY("Chẩn đoán hình ảnh"),
    SURGERY("Ngoại khoa");

    private final String vietnameseName; // Tên tiếng Việt

    // Constructor
    Specialization(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    // Lấy tên tiếng Việt của chuyên khoa
    public String getVietnameseName() {
        return vietnameseName;
    }
}
