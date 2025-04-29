package model.enums;

public enum PaymentStatus {
    PENDING("Chưa thanh toán"),
    COMPLETED("Đã thanh toán"),
    CANCELLED("Đã hủy"),
    REFUNDED("Đã hoàn tiền");

    private final String vietnameseName;

    PaymentStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
