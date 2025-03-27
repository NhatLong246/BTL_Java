package enums;

public enum RoomType {
    STANDARD("Standard Room", "ST"),   // Phòng tiêu chuẩn
    VIP("VIP Room", "VIP"),            // Phòng VIP
    ICU("ICU Room", "ICU"),            // Phòng hồi sức cấp cứu
    EMERGENCY("Emergency Room", "ER"); // Phòng cấp cứu

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
