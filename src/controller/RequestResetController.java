package controller;

import model.repository.UserRepository;
import view.RequestResetView;
import view.UI.ResetTokenDisplayUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class RequestResetController extends JFrame {
    private RequestResetView view;

    public RequestResetController() {
        setTitle("Request Password Reset - Patient Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

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

            String resetToken = UserRepository.resetPassword(input);
            if (resetToken != null) {
                new ResetTokenDisplayUI(resetToken); // Show token
                dispose(); // Close this UI
            } else {
                JOptionPane.showMessageDialog(this, "Username or email not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JLabel setBackgroundImage() {
        String imagePath = "img/file_background.png";
        if (!new File(imagePath).exists()) {
            System.out.println("Image not found: " + imagePath);
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
