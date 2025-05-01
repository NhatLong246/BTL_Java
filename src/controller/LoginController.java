package controller;

import model.entity.Patient;
import model.repository.PatientRepository;
import model.repository.UserRepository;
import view.DoctorView;
import view.LoginView;
import view.PatientView;
import view.RequestResetView;
import view.SignUpView;
import view.AdminView;
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

    private String getDoctorID(String userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT DoctorID FROM Doctors WHERE UserID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("DoctorID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean login(String username, String password) {
        if (username.isEmpty() || username.equals("USERNAME") ||
                password.isEmpty() || password.equals("PASSWORD")) {
            view.showError("Vui lòng nhập tên đăng nhập và mật khẩu");
            return false;
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            view.showError("Mật khẩu phải có ít nhất 6 ký tự!");
            return false;
        }

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

        boolean loginSuccess = UserRepository.loginUser(username, password);
        if (loginSuccess) {
            view.hideError();
            JOptionPane.showMessageDialog(view, "Đăng nhập thành công!");

            String role = UserRepository.getUserRole(username);
            if (role == null) {
                view.showError("Không thể xác định vai trò người dùng!");
                return false;
            }

            switch (role) {
                case "Quản lí":
                    String adminId = UserRepository.getAdminIdByUsername(username);
                    if (adminId != null) {
                        new AdminView(adminId).setVisible(true);
                        view.dispose();
                        return true;
                    } else {
                        view.showError("Không tìm thấy thông tin quản lý!");
                        return false;
                    }
                case "Bác sĩ":
                    String userId = UserRepository.getUserIdByUsernameOrEmail(username);
                    String doctorId = getDoctorID(userId);
                    if (doctorId != null) {
                        new DoctorView(doctorId).setVisible(true);
                        view.dispose();
                        return true;
                    } else {
                        view.showError("Không tìm thấy thông tin bác sĩ!");
                        return false;
                    }
                case "Bệnh nhân":
                    String patientID = UserRepository.getPatientIdByUsername(username);
                    if (patientID != null) {
                        try {
                            PatientRepository patientRepo = new PatientRepository();
                            Patient patient = patientRepo.getPatientByID(patientID);

                            System.out.println("============ DEBUG PATIENT BEFORE VIEW ============");
                            System.out.println("PatientID: " + (patient != null ? patient.getPatientID() : "null"));
                            System.out.println("UserID: " + (patient != null ? patient.getUserID() : "null"));
                            System.out.println("FullName: " + (patient != null ? patient.getFullName() : "null"));
                            System.out.println("DateOfBirth: " + (patient != null ? patient.getDateOfBirth() : "null"));
                            System.out.println("Gender: " + (patient != null ? patient.getGender() : "null"));
                            System.out.println("Address: " + (patient != null ? patient.getAddress() : "null"));
                            System.out.println("PhoneNumber: " + (patient != null ? patient.getPhoneNumber() : "null"));
                            System.out.println("RegistrationDate: " + (patient != null ? patient.getRegistrationDate() : "null"));
                            System.out.println("=================================================");

                            if (patient != null) {
                                PatientView patientView = new PatientView(patient);
                                patientView.setVisible(true);
                                view.dispose();
                                return true;
                            } else {
                                view.showError("Không tìm thấy thông tin bệnh nhân");
                                return false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            view.showError("Lỗi khi tải thông tin bệnh nhân: " + e.getMessage());
                            return false;
                        }
                    } else {
                        view.showError("Không tìm thấy thông tin bệnh nhân");
                        return false;
                    }
                default:
                    view.showError("Vai trò không xác định: " + role);
                    return false;
            }
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