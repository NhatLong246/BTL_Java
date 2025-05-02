package view;

import controller.RequestResetController;

import javax.swing.*;
import java.awt.*;

public class RequestResetView extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JButton resetButton;
    private RequestResetController controller;

    public RequestResetView() {
        this.controller = new RequestResetController(this);
        setTitle("Forgot Password");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Panel chính
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(200, 200, 200));
        panel.setBounds(0, 0, 400, 300);

        // Nhãn và trường Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 50, 100, 30);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 200, 30);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(usernameField);

        // Nhãn và trường Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 100, 100, 30);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 100, 200, 30);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(emailField);

        // Nút Reset Password
        resetButton = new JButton("Reset Password");
        resetButton.setBounds(150, 160, 150, 40);
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.addActionListener(e -> controller.requestReset(usernameField.getText(), emailField.getText()));
        panel.add(resetButton);

        add(panel);
        setVisible(true);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}