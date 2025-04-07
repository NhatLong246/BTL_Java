package UI;

import javax.swing.*;

import UI.LoginUI;
import database.UserDAO;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

public class ForgotPasswordUI extends JFrame {
    public ForgotPasswordUI() {
        setTitle("Reset Password - Patient Management System");
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

        // Panel reset mật khẩu
        int panelWidth = 900;
        int panelHeight = 700;

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(panelWidth, panelHeight);
        panel.setBackground(new Color(0, 0, 0, 150));
        panel.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - panelWidth) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - panelHeight) / 2,
                panelWidth, panelHeight);

        // Tiêu đề
        JLabel titleLabel = new JLabel("RESET PASSWORD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 80, panelWidth, 50);

        // Token
        JLabel tokenLabel = new JLabel("Reset Token:");
        tokenLabel.setBounds(250, 160, 200, 40);
        tokenLabel.setForeground(Color.WHITE);
        tokenLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField tokenText = new JTextField("ENTER TOKEN");
        tokenText.setBounds(250, 200, 400, 50);
        tokenText.setFont(new Font("Arial", Font.PLAIN, 20));
        tokenText.setForeground(Color.GRAY);
        tokenText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tokenText.getText().equals("ENTER TOKEN")) {
                    tokenText.setText("");
                    tokenText.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (tokenText.getText().isEmpty()) {
                    tokenText.setText("ENTER TOKEN");
                    tokenText.setForeground(Color.GRAY);
                }
            }
        });

        // New Password
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setBounds(250, 260, 200, 40);
        newPassLabel.setForeground(Color.WHITE);
        newPassLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPasswordField newPassText = new JPasswordField("NEW PASSWORD");
        newPassText.setBounds(250, 300, 400, 50);
        newPassText.setFont(new Font("Arial", Font.PLAIN, 20));
        newPassText.setForeground(Color.GRAY);
        newPassText.setEchoChar((char) 0);
        newPassText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(newPassText.getPassword()).equals("NEW PASSWORD")) {
                    newPassText.setText("");
                    newPassText.setForeground(Color.BLACK);
                    newPassText.setEchoChar('●');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (newPassText.getPassword().length == 0) {
                    newPassText.setText("NEW PASSWORD");
                    newPassText.setForeground(Color.GRAY);
                    newPassText.setEchoChar((char) 0);
                }
            }
        });

        // SUBMIT Button
        JButton submitButton = new JButton(" SUBMIT");
        submitButton.setBounds(250, 400, 400, 60);
        submitButton.setFont(new Font("Arial", Font.BOLD, 22));
        submitButton.setBackground(Color.WHITE);
        submitButton.setForeground(Color.BLACK);
        submitButton.setIcon(resizeIcon("src/resource/img/user-profile.png", submitButton, 0.5));

        submitButton.addActionListener(e -> {
            String token = tokenText.getText().trim();
            String newPassword = new String(newPassText.getPassword()).trim();

            if (token.isEmpty() || token.equals("ENTER TOKEN")) {
                JOptionPane.showMessageDialog(null, "Please enter the reset token!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPassword.isEmpty() || newPassword.equals("NEW PASSWORD")) {
                JOptionPane.showMessageDialog(null, "Please enter a new password!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = UserDAO.confirmResetPassword(token, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(null, "Password reset successfully! Please log in with your new password.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginUI().setVisible(true); // Quay lại LoginUI
            } else {
                JOptionPane.showMessageDialog(null, "Invalid or expired token!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // BACK Button
        JButton backButton = new JButton(" BACK");
        backButton.setBounds(250, 480, 400, 60);
        backButton.setFont(new Font("Arial", Font.BOLD, 22));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> {
            dispose();
            new RequestResetUI().setVisible(true); // Quay lại giao diện yêu cầu reset
        });

        // Thêm thành phần vào panel
        panel.add(titleLabel);
        panel.add(tokenLabel);
        panel.add(tokenText);
        panel.add(newPassLabel);
        panel.add(newPassText);
        panel.add(submitButton);
        panel.add(backButton);

        // Thêm vào frame
        setContentPane(background);
        add(panel);
        setVisible(true);
    }

    private ImageIcon resizeIcon(String path, JButton button, double scaleFactor) {
        int iconSize = (int) (button.getHeight() * scaleFactor);
        ImageIcon originalIcon = new ImageIcon(path);
        Image resizedImage = originalIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ForgotPasswordUI().setVisible(true));
    }
}