package model.enums;

public enum Gender {
    MALE("Nam"),
    FEMALE("Nữ");

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
            default:
                throw new IllegalArgumentException("Giới tính không hợp lệ! Vui lòng nhập Nam/Nữ.");
        }
    }

    public static Gender fromDatabase(String dbValue) {
        if (dbValue == null) return null;
        
        if (dbValue.equalsIgnoreCase("Nam") || dbValue.equalsIgnoreCase("MALE")) {
            return MALE;
        } else if (dbValue.equalsIgnoreCase("Nữ") || dbValue.equalsIgnoreCase("FEMALE")) {
            return FEMALE;
        }
        
        return null;  // Không xác định được
    }
}