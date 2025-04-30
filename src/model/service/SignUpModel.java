package model.service;

import model.repository.UserRepository;

public class SignUpModel {
    public String registerUser(String username, String email, String password, String role) {
        // Kiểm tra định dạng email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Invalid email format!";
        }

        // Gọi UserRepository để đăng ký người dùng
        return UserRepository.registerUser(username, email, password, role);
    }
}