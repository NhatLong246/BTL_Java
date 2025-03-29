package enums;

public enum PaymentMethod {
    CASH("Tiền mặt"),
    CREDIT_CARD("Thẻ tín dụng"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    INSURANCE("Bảo hiểm");

    private final String vietnameseName;

    PaymentMethod(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
