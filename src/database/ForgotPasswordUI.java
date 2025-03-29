package database;
import javax.swing.*;
import java.awt.*;

public class ForgotPasswordUI extends JFrame {
    public ForgotPasswordUI() {
        setTitle("Forgot Password");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        add(userLabel);

        JTextField userText = new JTextField();
        userText.setBounds(150, 50, 200, 30);
        add(userText);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 100, 100, 30);
        add(emailLabel);

        JTextField emailText = new JTextField();
        emailText.setBounds(150, 100, 200, 30);
        add(emailText);

        JButton resetButton = new JButton("Reset Password");
        resetButton.setBounds(120, 170, 150, 40);
        add(resetButton);

        resetButton.addActionListener(e -> {
            String username = userText.getText();
            String email = emailText.getText();

            if (UserDAO.checkUserEmail(username, email)) {
                JOptionPane.showMessageDialog(this, "Password reset link sent!");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or email!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setLocationRelativeTo(null);
    }
}
