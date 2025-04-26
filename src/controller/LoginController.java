package controller;

import model.repository.UserRepository;
import view.DoctorView;
import view.LoginView;
import view.UI.SignUpUI;
import view.RequestResetView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseConnection;

public class LoginController {
    private final LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
    }

    // Lấy DoctorID từ UserID
    private String getDoctorID(String userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT DoctorID FROM Doctors WHERE UserID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("DoctorID");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void login(String username, String password) {
        if (username.isEmpty() || username.equals("USERNAME") ||
                password.isEmpty() || password.equals("PASSWORD")) {
            view.showError("Username or password is not correct");
            return;
        }

        if (UserRepository.loginUser(username, password)) {
            view.hideError();
            JOptionPane.showMessageDialog(view, "Login Successful!");

            String role = UserRepository.getUserRole(username);
            String userId = UserRepository.getPatientID(username); // Lấy UserID để sử dụng nếu cần

            if (role == null) {
                view.showError("Cannot determine user role!");
                return;
            }

            // So sánh vai trò với giá trị chính xác từ cơ sở dữ liệu
            switch (role) {
                case "Quản lí":
                    // new AdminView().setVisible(true); // Bỏ comment khi có AdminView
                    JOptionPane.showMessageDialog(view, "Admin View not implemented yet!");
                    break;
                case "Bác sĩ":
                    String doctorId = getDoctorID(userId);
                    if (doctorId != null) {
                        new DoctorView(doctorId).setVisible(true);
                    } else {
                        view.showError("Cannot find DoctorID for this user!");
                        return;
                    }
                    break;
                case "Bệnh nhân":
                    // new PatientView().setVisible(true); // Bỏ comment khi có PatientView
                    JOptionPane.showMessageDialog(view, "Patient View not implemented yet!");
                    break;
                default:
                    view.showError("Unknown role: " + role);
                    return;
            }
            view.dispose();
        } else {
            view.showError("Username or password is not correct");
        }
    }

    public void navigateToSignUp() {
        view.dispose();
        new SignUpUI().setVisible(true);
    }

    public void navigateToRequestReset() {
        new RequestResetView().setVisible(true);
    }
}