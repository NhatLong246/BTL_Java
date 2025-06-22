package controller;

import model.repository.UserRepository;
import model.service.PatientService;
import model.entity.Patient;
import view.LoginView;
import view.PatientView;
import view.SignUpView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import model.repository.PatientRepository;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class SignUpController {
    private final SignUpView view;
    private final PatientService patientService;

    public SignUpController(SignUpView view) {
        this.view = view;
        this.patientService = new PatientService();

        // Thêm FocusListener cho các ô text
        addFocusListeners();

        // Thêm ActionListener cho các nút
        view.getSignInButtonNav().addActionListener(e -> navigateToLogin());
        view.getNextButton().addActionListener(e -> signUp());
    }

    private void addFocusListeners() {
        // FocusListener cho usernameText
        view.getUsernameText().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (view.getUsernameText().getText().equals("USERNAME")) {
                    view.getUsernameText().setText("");
                    view.getUsernameText().setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (view.getUsernameText().getText().isEmpty()) {
                    view.getUsernameText().setText("USERNAME");
                    view.getUsernameText().setForeground(Color.GRAY);
                }
            }
        });

        // FocusListener cho emailText
        view.getEmailText().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (view.getEmailText().getText().equals("EMAIL")) {
                    view.getEmailText().setText("");
                    view.getEmailText().setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (view.getEmailText().getText().isEmpty()) {
                    view.getEmailText().setText("EMAIL");
                    view.getEmailText().setForeground(Color.GRAY);
                }
            }
        });

        // FocusListener cho passText
        view.getPassText().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(view.getPassText().getPassword()).equals("PASSWORD")) {
                    view.getPassText().setText("");
                    view.getPassText().setForeground(Color.BLACK);
                    view.getPassText().setEchoChar('●');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (view.getPassText().getPassword().length == 0) {
                    view.getPassText().setText("PASSWORD");
                    view.getPassText().setForeground(Color.GRAY);
                    view.getPassText().setEchoChar((char) 0);
                }
            }
        });

        // FocusListener cho phoneText
        view.getPhoneText().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (view.getPhoneText().getText().equals("PHONE (+84/0)")) {
                    view.getPhoneText().setText("");
                    view.getPhoneText().setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (view.getPhoneText().getText().isEmpty()) {
                    view.getPhoneText().setText("PHONE (+84/0)");
                    view.getPhoneText().setForeground(Color.GRAY);
                }
            }
        });
    }

    // Hàm kiểm tra định dạng số điện thoại
    private boolean isValidPhoneNumber(String phone) {
        // Kiểm tra bắt đầu bằng +84 hoặc 0
        if (!phone.startsWith("+84") && !phone.startsWith("0")) {
            return false;
        }
        // Kiểm tra độ dài: +84 thì 11 ký tự, 0 thì 10 ký tự
        if (phone.startsWith("+84") && phone.length() != 11) {
            return false;
        }
        if (phone.startsWith("0") && phone.length() != 10) {
            return false;
        }
        // Kiểm tra phần số sau tiền tố có đúng 9 chữ số
        String digits = phone.startsWith("+84") ? phone.substring(3) : phone.substring(1);
        return digits.matches("\\d{9}");
    }

    public void signUp() {
        String username = view.getUsernameText().getText().trim();
        String email = view.getEmailText().getText().trim();
        String password = new String(view.getPassText().getPassword()).trim();
        String phone = view.getPhoneText().getText().trim();

        // Kiểm tra các trường rỗng hoặc placeholder
        if (username.isEmpty() || username.equals("USERNAME") ||
                email.isEmpty() || email.equals("EMAIL") ||
                password.isEmpty() || password.equals("PASSWORD") ||
                phone.isEmpty() || phone.equals("PHONE (+84/0)")) {
            view.showError("Vui lòng điền đầy đủ các trường!");
            return;
        }

        // Kiểm tra định dạng số điện thoại
        if (!isValidPhoneNumber(phone)) {
            view.showError("Số điện thoại không hợp lệ! Phải bắt đầu với +84 hoặc 0, và có đúng 9 chữ số.");
            return;
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            view.showError("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        // Kiểm tra định dạng username
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            view.showError("Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới!");
            return;
        }

        // Kiểm tra định dạng email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            view.showError("Định dạng email không hợp lệ!");
            return;
        }

        // Kiểm tra trùng lặp username hoặc email
        if (UserRepository.isUsernameTaken(username)) {
            view.showError("Tên đăng nhập đã tồn tại!");
            return;
        }
        if (UserRepository.isEmailTaken(email)) {
            view.showError("Email đã được sử dụng!");
            return;
        }

        // Thực hiện đăng ký
        String result = UserRepository.registerUser(username, email, password, "Bệnh nhân");
        if (result != null && result.startsWith("Success")) {
            try {
                String userIdStr = result.split(":")[1].trim();

                // Gọi getPatientByUserId với userId dạng String
                Patient patient = patientService.getPatientByUserId(userIdStr);
                if (patient != null) {
                    JOptionPane.showMessageDialog(view, "Đăng ký thành công! Chuyển đến thông tin bệnh nhân.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    view.dispose();
                    new PatientView(patient).setVisible(true);
                } else {
                    // Nếu không tìm thấy Patient, tạo mới
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        // Tạo Patient với patientID tạm thời (sẽ được xử lý trong PatientRepository)
                        Patient newPatient = new Patient(userIdStr, "TEMP-PAT", username, java.time.LocalDate.now(), "", null, phone, java.time.LocalDate.now());
                        PatientRepository patientRepository = new PatientRepository();
                        if (patientRepository.addPatient(newPatient, null, null, email)) {
                            patient = patientService.getPatientByUserId(userIdStr);
                            if (patient != null) {
                                JOptionPane.showMessageDialog(view, "Đăng ký thành công! Chuyển đến thông tin bệnh nhân.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                                view.dispose();
                                new PatientView(patient).setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(view, "Đăng ký thành công nhưng không tải được thông tin bệnh nhân!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                                view.dispose();
                                new LoginView().setVisible(true);
                            }
                        } else {
                            JOptionPane.showMessageDialog(view, "Đăng ký thành công nhưng không thể tạo hồ sơ bệnh nhân!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                            view.dispose();
                            new LoginView().setVisible(true);
                        }
                    } catch (SQLException e) {
                        System.err.println("Lỗi kết nối database: " + e.getMessage());
                        e.printStackTrace();
                        view.showError("Đăng ký thất bại do lỗi kết nối!");
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi xử lý kết quả đăng ký: " + e.getMessage());
                e.printStackTrace();
                view.showError("Đăng ký thành công nhưng xảy ra lỗi khi xử lý dữ liệu người dùng!");
            }
        } else {
            view.showError("Đăng ký thất bại! " + (result != null ? result : "Lỗi không xác định. Vui lòng kiểm tra kết nối cơ sở dữ liệu."));
            System.err.println("Register result: " + result);
        }
    }

    public void navigateToLogin() {
        int panelWidth = 900;
        int defaultWidth = 200;
        int activeWidth = 240;
        int buttonY = 80;

        int totalButtonWidthActive = activeWidth + defaultWidth;
        int startXActive = (panelWidth - totalButtonWidthActive) / 2;

        view.getSignInButtonNav().setBounds(startXActive, buttonY, activeWidth, 60);
        view.getSignInButtonNav().setBackground(Color.WHITE);
        view.getSignInButtonNav().setForeground(Color.BLACK);
        view.getSignInButtonNav().setFont(new Font("Arial", Font.BOLD, 24));

        view.getSignUpButtonNav().setBounds(startXActive + activeWidth, buttonY, defaultWidth, 50);
        view.getSignUpButtonNav().setBackground(Color.GRAY);
        view.getSignUpButtonNav().setForeground(Color.WHITE);
        view.getSignUpButtonNav().setFont(new Font("Arial", Font.BOLD, 20));

        view.dispose();
        new LoginView().setVisible(true);
    }
}