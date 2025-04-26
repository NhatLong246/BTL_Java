package controller;

import model.service.PatientService; // Import PatientService
import model.service.SignUpModel;
import model.entity.Patient; // Import Patient
import view.LoginView;
import view.PatientView; // Đổi từ PatientInfoView thành PatientView
import view.SignUpView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class SignUpController {
    private SignUpView view;
    private SignUpModel model;
    private PatientService patientService; // Thêm PatientService

    public SignUpController(SignUpView view, SignUpModel model) {
        this.view = view;
        this.model = model;
        this.patientService = new PatientService(); // Khởi tạo PatientService

        // Thêm FocusListener cho các ô text
        addFocusListeners();

        // Thêm ActionListener cho các nút
        view.getSignInButtonNav().addActionListener(e -> handleSignIn());
        view.getNextButton().addActionListener(e -> handleSignUp());
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

    private void handleSignIn() {
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

    private void handleSignUp() {
        String username = view.getUsernameText().getText();
        String email = view.getEmailText().getText();
        String password = new String(view.getPassText().getPassword());

        // Kiểm tra placeholder
        if (username.isEmpty() || username.equals("USERNAME") ||
                email.isEmpty() || email.equals("EMAIL") ||
                password.isEmpty() || password.equals("PASSWORD")) {
            JOptionPane.showMessageDialog(view, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Gọi model để đăng ký
        String result = model.registerUser(username, email, password, "patient");
        if (result.startsWith("Success")) {
            int userId = Integer.parseInt(result.split(":")[1]);
            // Lấy thông tin Patient từ userId
            Patient patient = patientService.getPatientByUserId(userId);
            if (patient != null) {
                view.dispose();
                new PatientView(patient).setVisible(true); // Truyền đối tượng Patient
            } else {
                JOptionPane.showMessageDialog(view, "Failed to load patient information!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(view, "Sign up failed! " + result, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}