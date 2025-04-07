package UI;

import javax.swing.*;

import UI.ResetTokenDisplayUI;
import database.UserDAO;

import java.awt.*;
import java.io.File;

public class RequestResetUI extends JFrame {
    public RequestResetUI() {
        setTitle("Request Password Reset - Patient Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Load background image
        String imagePath = "img/file_background.png";
        if (!new File(imagePath).exists()) {
            System.out.println("Image not found: " + imagePath);
        }

        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        );
        ImageIcon bgImage = new ImageIcon(scaledImage);

        JLabel background = new JLabel(bgImage);
        background.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);

        // Panel yêu cầu reset
        int panelWidth = 900;
        int panelHeight = 500;

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(panelWidth, panelHeight);
        panel.setBackground(new Color(0, 0, 0, 150));
        panel.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - panelWidth) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - panelHeight) / 2,
                panelWidth, panelHeight);

        // Tiêu đề
        JLabel titleLabel = new JLabel("REQUEST PASSWORD RESET", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 80, panelWidth, 50);

        // Username/Email
        JLabel inputLabel = new JLabel("Username or Email:");
        inputLabel.setBounds(250, 160, 200, 40);
        inputLabel.setForeground(Color.WHITE);
        inputLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField inputText = new JTextField("ENTER USERNAME OR EMAIL");
        inputText.setBounds(250, 200, 400, 50);
        inputText.setFont(new Font("Arial", Font.PLAIN, 20));
        inputText.setForeground(Color.GRAY);
        inputText.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (inputText.getText().equals("ENTER USERNAME OR EMAIL")) {
                    inputText.setText("");
                    inputText.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (inputText.getText().isEmpty()) {
                    inputText.setText("ENTER USERNAME OR EMAIL");
                    inputText.setForeground(Color.GRAY);
                }
            }
        });

        // SUBMIT Button
        JButton submitButton = new JButton(" SUBMIT");
        submitButton.setBounds(250, 300, 400, 60);
        submitButton.setFont(new Font("Arial", Font.BOLD, 22));
        submitButton.setBackground(Color.WHITE);
        submitButton.setForeground(Color.BLACK);

        submitButton.addActionListener(e -> {
            String input = inputText.getText().trim();

            if (input.isEmpty() || input.equals("ENTER USERNAME OR EMAIL")) {
                JOptionPane.showMessageDialog(null, "Please enter your username or email!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String resetToken = UserDAO.resetPassword(input);
            if (resetToken != null) {
                // Mở cửa sổ hiển thị reset token
                new ResetTokenDisplayUI(resetToken);
                dispose(); // Đóng RequestResetUI
            } else {
                JOptionPane.showMessageDialog(null, "Username or email not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Thêm thành phần vào panel
        panel.add(titleLabel);
        panel.add(inputLabel);
        panel.add(inputText);
        panel.add(submitButton);

        // Thêm vào frame
        setContentPane(background);
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RequestResetUI().setVisible(true));
    }
}