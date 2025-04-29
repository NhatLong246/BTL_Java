package controller;

import model.service.PasswordResetService;
import view.ForgotPasswordView;
import view.LoginView;
import view.RequestResetView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ForgotPasswordController extends JFrame {
    private ForgotPasswordView view;
    private PasswordResetService service;

    public ForgotPasswordController() {
        setTitle("Reset Password - Patient Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        service = new PasswordResetService();
        view = new ForgotPasswordView();
        add(view);

        // Background
        JLabel background = new JLabel(new ImageIcon(new ImageIcon("img/file_background.png")
                .getImage().getScaledInstance(
                        Toolkit.getDefaultToolkit().getScreenSize().width,
                        Toolkit.getDefaultToolkit().getScreenSize().height,
                        Image.SCALE_SMOOTH)));
        background.setBounds(0, 0,
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
        setContentPane(background);
        background.setLayout(null);
        background.add(view);

        // Focus listeners
        view.tokenText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (view.tokenText.getText().equals("ENTER TOKEN")) {
                    view.tokenText.setText("");
                    view.tokenText.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (view.tokenText.getText().isEmpty()) {
                    view.tokenText.setText("ENTER TOKEN");
                    view.tokenText.setForeground(Color.GRAY);
                }
            }
        });

        view.newPassText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(view.newPassText.getPassword()).equals("NEW PASSWORD")) {
                    view.newPassText.setText("");
                    view.newPassText.setForeground(Color.BLACK);
                    view.newPassText.setEchoChar('●');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (view.newPassText.getPassword().length == 0) {
                    view.newPassText.setText("NEW PASSWORD");
                    view.newPassText.setForeground(Color.GRAY);
                    view.newPassText.setEchoChar((char) 0);
                }
            }
        });

        // Submit action
        view.submitButton.addActionListener(e -> {
            String token = view.tokenText.getText().trim();
            String newPassword = new String(view.newPassText.getPassword()).trim();

            if (token.isEmpty() || token.equals("ENTER TOKEN")) {
                JOptionPane.showMessageDialog(null, "Please enter the reset token!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPassword.isEmpty() || newPassword.equals("NEW PASSWORD")) {
                JOptionPane.showMessageDialog(null, "Please enter a new password!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = service.confirmResetPassword(token, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(null, "Password reset successfully! Please log in with your new password.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginView().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid or expired token!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Back button
        view.backButton.addActionListener(e -> {
            dispose();
            new RequestResetView().setVisible(true);
        });

        setVisible(true);
    }
}
