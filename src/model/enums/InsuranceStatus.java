package model.enums;

public enum InsuranceStatus {
    HOAT_DONG("Hoạt Động"),
    HET_HAN("Hết Hạn"),
    KHONG_XAC_DINH("Không Xác Định");

    private final String statusText;

    InsuranceStatus(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusText() {
        return statusText;
    }

    public static InsuranceStatus fromString(String text) {
        for (InsuranceStatus s : InsuranceStatus.values()) {
            if (s.statusText.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + text);
    }
}
