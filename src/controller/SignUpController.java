package controller;

import model.repository.UserRepository;
import view.LoginView;
import view.PatientInfoView;
import view.SignUpView;

import javax.swing.*;

public class SignUpController {
    private final SignUpView view;

    public SignUpController(SignUpView view) {
        this.view = view;
    }

    public void signUp(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.showError("Please fill all fields!");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            view.showError("Invalid email format!");
            return;
        }

        // Sửa vai trò thành "Bệnh nhân" với chữ B viết hoa
        String result = UserRepository.registerUser(username, email, password, "Bệnh nhân");
        
        // Xử lý kết quả phù hợp với định dạng trả về từ UserRepository
        if (result != null && result.startsWith("Success")) {
            try {
                // Tách lấy userId từ kết quả
                String userIdStr = result.split(":")[1];
                String userId = userIdStr.trim(); // Đảm bảo không có khoảng trắng
                
                view.showSuccess("Sign up successful! Proceeding to patient details.");
                view.dispose();
                new PatientInfoView(Integer.parseInt(userId)).setVisible(true);
            } catch (Exception e) {
                // Xử lý lỗi nếu định dạng kết quả không như mong đợi
                System.err.println("Error processing registration result: " + e.getMessage());
                e.printStackTrace();
                view.showError("Registration successful but error occurred processing user data.");
            }
        } else {
            view.showError("Sign up failed! " + (result != null ? result : "Unknown error"));
        }
    }

    public void navigateToLogin() {
        view.dispose();
        new LoginView().setVisible(true);
    }
}