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
    }

    public void signUp() {
        String username = view.getUsernameText().getText().trim();
        String email = view.getEmailText().getText().trim();
        String password = new String(view.getPassText().getPassword()).trim();

        if (username.isEmpty() || username.equals("USERNAME") ||
                email.isEmpty() || email.equals("EMAIL") ||
                password.isEmpty() || password.equals("PASSWORD")) {
            view.showError("Vui lòng điền đầy đủ các trường!");
            return;
        }

        if (password.length() < 6) {
            view.showError("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        if (!username.matches("^[A-Za-z0-9_]+$")) {
            view.showError("Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới!");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            view.showError("Định dạng email không hợp lệ!");
            return;
        }

        String result = UserRepository.registerUser(username, email, password, "Bệnh nhân");
        if (result != null && result.startsWith("Success")) {
            try {
                String userIdStr = result.split(":")[1];
                int userId = Integer.parseInt(userIdStr.trim());

                Patient patient = patientService.getPatientByUserId(userId);
                if (patient != null) {
                    JOptionPane.showMessageDialog(view, "Đăng ký thành công! Chuyển đến thông tin bệnh nhân.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    view.dispose();
                    new PatientView(patient).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(view, "Đăng ký thành công! Vui lòng đăng nhập để tiếp tục.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    view.dispose();
                    new LoginView().setVisible(true);
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi xử lý kết quả đăng ký: " + e.getMessage());
                e.printStackTrace();
                view.showError("Đăng ký thành công nhưng xảy ra lỗi khi xử lý dữ liệu người dùng!");
            }
        } else {
            view.showError("Đăng ký thất bại! " + (result != null ? result : "Lỗi không xác định"));
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