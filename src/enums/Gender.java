package enums;

public enum Gender {
    MALE("Nam"),
    FEMALE("Nữ"),
    OTHER("Khác");

    private final String vietnamese;

    Gender(String vietnamese) {
        this.vietnamese = vietnamese;
    }

    public String getVietnamese() {
        return vietnamese;
    }

    // Chuyển từ String sang Enum
    public static Gender fromString(String text) {
        if (text == null) return null;
        switch (text.trim().toLowerCase()) {
            case "male":
            case "nam":
            case "m":
                return MALE;
            case "female":
            case "nữ":
            case "f":
                return FEMALE;
            case "other":
            case "khác":
            case "o":
                return OTHER;
            default:
                throw new IllegalArgumentException("Giới tính không hợp lệ! Vui lòng nhập Nam/Nữ/Khác.");
        }
    }
}

