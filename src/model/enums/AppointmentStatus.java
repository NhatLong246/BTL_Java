package model.enums;

public enum AppointmentStatus {
    PENDING("Chờ xác nhận"),
    COMPLETED("Đã hoàn thành"),
    CANCELED("Đã hủy");

    private final String vietnamese;

    AppointmentStatus(String vietnamese) {
        this.vietnamese = vietnamese;
    }

    public String getVietnamese() {
        return vietnamese;
    }
}
