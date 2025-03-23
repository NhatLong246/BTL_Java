package enums;

public enum InsuranceProvider {
    BHYT("Bảo hiểm Y tế"),
    BHTN("Bảo hiểm Tự Nguyện"),
    PRIVATE("Bảo hiểm Tư Nhân");

    private final String displayName;

    InsuranceProvider(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
