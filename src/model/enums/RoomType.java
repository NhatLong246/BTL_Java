package model.enums;

public enum RoomType {
    STANDARD("Tiêu chuẩn", "ST"),   // Phòng tiêu chuẩn
    VIP("VIP", "VIP"),            // Phòng VIP
    ICU("ICU", "ICU"),            // Phòng hồi sức cấp cứu
    EMERGENCY("Cấp cứu", "ER"); // Phòng cấp cứu

    private final String description; // Mô tả loại phòng
    private final String code;        // Mã loại phòng

    // Constructor
    RoomType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    // Lấy mô tả loại phòng
    public String getDescription() {
        return description;
    }

    // Lấy mã phòng
    public String getCode() {
        return code;
    }
}
