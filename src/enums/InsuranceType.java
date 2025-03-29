package enums;

public enum InsuranceType {
    DN("Doanh nghiệp"),
    HS("Học sinh"),
    XN("Công nhân"),
    CA("Công an"),
    SV("Sinh viên");

    private final String description;

    InsuranceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
