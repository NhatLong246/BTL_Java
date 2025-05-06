package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ForgotPasswordView extends JPanel {
    public JTextField tokenText;
    public JPasswordField newPassText;
    public JButton submitButton;
    public JButton backButton;
    private JLabel errorLabel;

    public ForgotPasswordView() {
        setLayout(null);
        setOpaque(false);
        setSize(900, 500);
        setBackground(new Color(0, 0, 0, 150));
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - 900) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - 500) / 2,
                900, 500);

        // Title
        JLabel titleLabel = new JLabel("RESET PASSWORD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 80, 900, 50);
        add(titleLabel);

        // Back Button
        backButton = new JButton(" BACK");
        backButton.setBounds(450, 80, 200, 60);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        add(backButton);

        // Token Label
        JLabel tokenLabel = new JLabel("Reset Token:");
        tokenLabel.setBounds(250, 160, 200, 40);
        tokenLabel.setForeground(Color.WHITE);
        tokenLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(tokenLabel);

        // Token Text
        tokenText = new JTextField("ENTER TOKEN");
        tokenText.setBounds(250, 200, 400, 50);
        tokenText.setFont(new Font("Arial", Font.PLAIN, 20));
        tokenText.setForeground(Color.GRAY);
        tokenText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        add(tokenText);
        tokenText.addFocusListener(new FocusAdapter() {
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

        // New Password Label
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setBounds(250, 260, 200, 40);
        newPassLabel.setForeground(Color.WHITE);
        newPassLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(newPassLabel);

        // New Password Text
        newPassText = new JPasswordField("NEW PASSWORD");
        newPassText.setBounds(250, 300, 400, 50);
        newPassText.setFont(new Font("Arial", Font.PLAIN, 20));
        newPassText.setForeground(Color.GRAY);
        newPassText.setEchoChar((char) 0);
        newPassText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        add(newPassText);
        newPassText.addFocusListener(new FocusAdapter() {
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

        // Submit Button
        submitButton = new JButton("SUBMIT");
        submitButton.setBounds(250, 400, 400, 60);
        submitButton.setFont(new Font("Arial", Font.BOLD, 22));
        submitButton.setBackground(Color.WHITE);
        submitButton.setForeground(Color.BLACK);
        add(submitButton);

        // Error Label
        errorLabel = new JLabel("");
        errorLabel.setBounds(250, 370, 400, 30);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        errorLabel.setVisible(false);
        add(errorLabel);
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void hideError() {
        errorLabel.setVisible(false);
    }
}