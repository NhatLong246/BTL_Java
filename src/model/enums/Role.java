package model.enums;

public enum Role {
    BAC_SI("Bác sĩ"),
    BENH_NHAN("Bệnh nhân"),
    QUAN_LY("Quản lí");

    private final String displayName;

    // Constructor
    Role(String displayName) {
        this.displayName = displayName;
    }

    // Getter -- Lấy tên hiển thị
    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.displayName.equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Vai trò không hợp lệ: " + text);
    }
}
