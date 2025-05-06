package view;

import controller.ResetTokenController;

import javax.swing.*;
import java.awt.*;

public class ResetTokenDisplayView extends JFrame {
    private JTextField tokenField;
    private JPasswordField newPasswordField;
    private JButton confirmButton;
    private ResetTokenController controller;

    public ResetTokenDisplayView(String token) {
        this.controller = new ResetTokenController(this);
        setTitle("Reset Password");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Panel chính
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(200, 200, 200));
        panel.setBounds(0, 0, 400, 300);

        // Nhãn và trường Token
        JLabel tokenLabel = new JLabel("Token:");
        tokenLabel.setBounds(50, 50, 100, 30);
        tokenLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(tokenLabel);

        tokenField = new JTextField(token);
        tokenField.setBounds(150, 50, 200, 30);
        tokenField.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(tokenField);

        // Nhãn và trường New Password
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setBounds(50, 100, 150, 30);
        newPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(newPasswordLabel);

        newPasswordField = new JPasswordField();
        newPasswordField.setBounds(150, 100, 200, 30);
        newPasswordField.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(newPasswordField);

        // Nút Confirm
        confirmButton = new JButton("Confirm");
        confirmButton.setBounds(150, 160, 150, 40);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmButton.addActionListener(e -> controller.confirmReset(tokenField.getText(), new String(newPasswordField.getPassword())));
        panel.add(confirmButton);

        add(panel);
        setVisible(true);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}