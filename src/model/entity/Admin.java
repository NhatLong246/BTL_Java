package model.entity;

public class Admin {
    private String adminId; // ID quản trị viên
    private String fullName; // Họ và tên quản trị viên
    private String email; // Email quản trị viên
    private String phoneNumber; // Số điện thoại quản trị viên

    // Constructor mặc định
    public Admin() {
    }

    // Constructor đầy đủ
    public Admin(String adminId, String fullName, String email, String phoneNumber) {
        this.adminId = adminId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getter và Setter
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}