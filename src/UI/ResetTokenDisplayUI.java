package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ResetTokenDisplayUI extends JFrame {
    public ResetTokenDisplayUI(String resetToken) {
        setTitle("Reset Token - Patient Management System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa màn hình
        setLayout(null);

        // Load background image
        String imagePath = "img/file_background.png";
        if (!new File(imagePath).exists()) {
            System.out.println("Image not found: " + imagePath);
        }

        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH);
        ImageIcon bgImage = new ImageIcon(scaledImage);

        JLabel background = new JLabel(bgImage);
        background.setBounds(0, 0, 500, 300);

        // Panel hiển thị reset token
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(400, 200);
        panel.setBackground(new Color(0, 0, 0, 150));
        panel.setBounds(50, 50, 400, 200);

        // Tiêu đề
        JLabel titleLabel = new JLabel("Your Reset Token", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, 400, 30);

        // Trường hiển thị reset token (không cho phép chỉnh sửa)
        JTextField tokenField = new JTextField(resetToken);
        tokenField.setBounds(50, 70, 300, 40);
        tokenField.setFont(new Font("Arial", Font.PLAIN, 16));
        tokenField.setEditable(false); // Không cho phép chỉnh sửa

        // Nút Copy
        JButton copyButton = new JButton("Copy");
        copyButton.setBounds(50, 130, 120, 40);
        copyButton.setFont(new Font("Arial", Font.BOLD, 16));
        copyButton.setBackground(Color.WHITE);
        copyButton.setForeground(Color.BLACK);

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Sao chép reset token vào clipboard
                StringSelection stringSelection = new StringSelection(resetToken);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                JOptionPane.showMessageDialog(null, "Reset token copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Nút Proceed
        JButton proceedButton = new JButton("Proceed");
        proceedButton.setBounds(230, 130, 120, 40);
        proceedButton.setFont(new Font("Arial", Font.BOLD, 16));
        proceedButton.setBackground(Color.WHITE);
        proceedButton.setForeground(Color.BLACK);

        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Đóng cửa sổ hiện tại
                new ForgotPasswordUI().setVisible(true); // Mở ForgotPasswordUI
            }
        });

        // Thêm thành phần vào panel
        panel.add(titleLabel);
        panel.add(tokenField);
        panel.add(copyButton);
        panel.add(proceedButton);

        // Thêm vào frame
        setContentPane(background);
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResetTokenDisplayUI("123e4567-e89b-12d3-a456-426614174000"));
    }
}