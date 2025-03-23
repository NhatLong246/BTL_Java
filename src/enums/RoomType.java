package enums;

public enum RoomType {
	STANDARD("Standard Room"),
    VIP("VIP Room"),
    ICU("ICU Room"),
    EMERGENCY("Emergency Room");

    private final String description;

    // Constructor 
    RoomType(String description) {
        this.description = description;
    }

    // Lấy mô tả của loại phòng
    public String getDescription() {
        return description;
    }
}
