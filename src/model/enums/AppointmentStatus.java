package model.enums;

public enum AppointmentStatus {
    PENDING("Chờ xác nhận"),
    COMPLETED("Hoàn thành"),
    CANCELED("Hủy");
    
    private final String value;
    
    AppointmentStatus(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    public static AppointmentStatus fromString(String text) {
        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No constant with value " + text);
    }
}