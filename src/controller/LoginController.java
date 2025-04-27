package controller;

import model.entity.Patient;
import model.repository.PatientRepository;
import model.repository.UserRepository;
import view.DoctorView;
import view.LoginView;
import view.PatientView;
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
                        try {
                            // Lấy thông tin bệnh nhân từ database
                            PatientRepository patientRepo = new PatientRepository();
                            Patient patient = patientRepo.getPatientByID(patientID);
                            
                            // Debug thông tin bệnh nhân trước khi tạo View
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
                                // Tạo và hiển thị giao diện bệnh nhân
                                PatientView patientView = new PatientView(patient);
                                patientView.setVisible(true);  // Đảm bảo gọi setVisible(true)
                                view.dispose();  // Đóng form đăng nhập
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