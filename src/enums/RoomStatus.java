package enums;

public enum RoomStatus {
    AVAILABLE("Trống"),
    OCCUPIED("Đang sử dụng"),
    FULL("Đầy"),
    MAINTENANCE("Bảo trì");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}