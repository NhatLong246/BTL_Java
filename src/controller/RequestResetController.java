package controller;

import model.service.PasswordResetService;
import view.RequestResetView;
import view.ResetTokenDisplayView;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class RequestResetController extends JFrame {
    private RequestResetView view;
    private PasswordResetService passwordResetService;

    public RequestResetController() {
        setTitle("Request Password Reset - Patient Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Khởi tạo service
        passwordResetService = new PasswordResetService();

        view = new RequestResetView();
        setContentPane(setBackgroundImage());
        add(view);
        registerEvents();
        setVisible(true);
    }

    private void registerEvents() {
        view.submitButton.addActionListener(e -> {
            String input = view.inputText.getText().trim();

            if (input.isEmpty() || input.equals("ENTER USERNAME OR EMAIL")) {
                JOptionPane.showMessageDialog(this, "Please enter your username or email!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Sử dụng PasswordResetService thay vì gọi trực tiếp UserRepository
            String resetToken = passwordResetService.requestPasswordReset(input);
            if (resetToken != null) {
                ResetTokenDisplayView tokenView = new ResetTokenDisplayView();
                tokenView.setToken(resetToken); // Set token vào view
                new ResetTokenController(resetToken); // Tạo controller với token
                dispose(); // Close this UI
            } else {
                JOptionPane.showMessageDialog(this, "Username or email not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JLabel setBackgroundImage() {
        // Sử dụng đường dẫn chính xác đến hình ảnh
        String imagePath = "resources/img/file_background.png";
        File imageFile = new File(imagePath);
        
        if (!imageFile.exists()) {
            System.out.println("Image not found: " + imageFile.getAbsolutePath());
            
            // Thử các đường dẫn khác
            imagePath = "src/resources/img/file_background.png";
            imageFile = new File(imagePath);
            
            if (!imageFile.exists()) {
                System.out.println("Image still not found at: " + imageFile.getAbsolutePath());
                return new JLabel(); // Return empty label if image not found
            }
        }

        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH);
        ImageIcon bgImage = new ImageIcon(scaledImage);

        JLabel background = new JLabel(bgImage);
        background.setBounds(0, 0,
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
        return background;
    }
}
