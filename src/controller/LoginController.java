package controller;

import model.repository.UserRepository;
import view.DoctorView;
import view.LoginView;
import view.RequestResetView;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import database.DatabaseConnection;
import view.SignUpView;

public class LoginController {
    private final LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
    }

    public boolean login(String username, String password) {
        if (username.isEmpty() || username.equals("USERNAME") ||
                password.isEmpty() || password.equals("PASSWORD")) {
            view.showError("Vui lòng nhập tên đăng nhập và mật khẩu");
            return false;
        }

        // Kiểm tra kết nối cơ sở dữ liệu trước
        try {
            Connection testConnection = DatabaseConnection.getConnection();
            if (testConnection == null) {
                view.showError("Không thể kết nối đến cơ sở dữ liệu");
                return false;
            }
            testConnection.close();
        } catch (SQLException e) {
            view.showError("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // Thử đăng nhập
        boolean loginSuccess = UserRepository.loginUser(username, password);
        if (loginSuccess) {
            view.hideError();
            JOptionPane.showMessageDialog(view, "Đăng nhập thành công!");

            String role = UserRepository.getUserRole(username);
            System.out.println("User role: " + role);

            switch (role) {
                case "Quản lí":
                    // new AdminView().setVisible(true);
                    JOptionPane.showMessageDialog(view, "Đăng nhập với vai trò Quản lí thành công!");
                    break;
                case "Bác sĩ":
                    String doctorID = UserRepository.getDoctorIdByUsername(username);
                    if (doctorID != null) {
                        new DoctorView(doctorID).setVisible(true);
                    } else {
                        view.showError("Không tìm thấy thông tin bác sĩ");
                        return false;
                    }
                    break;
                case "Bệnh nhân":
                    String patientID = UserRepository.getPatientIdByUsername(username);
                    if (patientID != null) {
                        // new PatientView(patientID).setVisible(true);
                        JOptionPane.showMessageDialog(view, "Đăng nhập với vai trò Bệnh nhân thành công! PatientID: " + patientID);
                    } else {
                        view.showError("Không tìm thấy thông tin bệnh nhân");
                        return false;
                    }
                    break;
                default:
                    view.showError("Vai trò không xác định: " + role);
                    return false;
            }
            view.dispose();
            return true;
        } else {
            view.showError("Tên đăng nhập hoặc mật khẩu không đúng");
            return false;
        }
    }

    public void navigateToSignUp() {
        view.dispose();
        new SignUpView().setVisible(true);
    }

    public void navigateToRequestReset() {
        new RequestResetView().setVisible(true);
    }
}