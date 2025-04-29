package model.entity;

import model.enums.Role;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class UserAccount {
    private String userId;
    private String fullName;
    private Role role;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private LocalDateTime createdAt;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public UserAccount() {}

    // Constructor khi tạo tài khoản mới
    public UserAccount(String userId, String fullName, Role role, String email, String phoneNumber, String passwordHash) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        setEmail(email);
        setPhoneNumber(phoneNumber);
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor khi lấy từ database
    public UserAccount(String userId, String fullName, String role, String email, String phoneNumber, String passwordHash, LocalDateTime createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = Role.fromString(role);  // Chuyển từ String thành Enum
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email không hợp lệ!");
        }
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        // Regex: Bắt đầu bằng "+84" hoặc "0", sau đó là đúng 9 chữ số
        if (phoneNumber != null && !phoneNumber.matches("(\\+84|0)\\d{9}")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ! Phải bắt đầu với +84 hoặc 0, và có đúng 9 chữ số.");
        }
        this.phoneNumber = phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
