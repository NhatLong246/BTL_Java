package view; 

import view.UI.ForgotPasswordUI;

import javax.swing.*;
import java.awt.*;

public class ForgotPasswordView extends JPanel {
    public JTextField tokenText;
    public JPasswordField newPassText;
    public JButton submitButton;
    public JButton backButton;

    public ForgotPasswordView() {
        setLayout(null);
        setOpaque(false);

        int panelWidth = 900;
        int panelHeight = 700;
        setSize(panelWidth, panelHeight);
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - panelWidth) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - panelHeight) / 2,
                panelWidth, panelHeight);

        // Tiêu đề
        JLabel titleLabel = new JLabel("RESET PASSWORD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 80, panelWidth, 50);
        add(titleLabel);

        // Token input
        JLabel tokenLabel = new JLabel("Reset Token:");
        tokenLabel.setBounds(250, 160, 200, 40);
        tokenLabel.setForeground(Color.WHITE);
        tokenLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(tokenLabel);

        tokenText = new JTextField("ENTER TOKEN");
        tokenText.setBounds(250, 200, 400, 50);
        tokenText.setFont(new Font("Arial", Font.PLAIN, 20));
        tokenText.setForeground(Color.GRAY);
        add(tokenText);

        // Password input
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setBounds(250, 260, 200, 40);
        newPassLabel.setForeground(Color.WHITE);
        newPassLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(newPassLabel);

        newPassText = new JPasswordField("NEW PASSWORD");
        newPassText.setBounds(250, 300, 400, 50);
        newPassText.setFont(new Font("Arial", Font.PLAIN, 20));
        newPassText.setForeground(Color.GRAY);
        newPassText.setEchoChar((char) 0);
        add(newPassText);

        // Submit
        submitButton = new JButton(" SUBMIT");
        submitButton.setBounds(250, 400, 400, 60);
        submitButton.setFont(new Font("Arial", Font.BOLD, 22));
        submitButton.setBackground(Color.WHITE);
        submitButton.setForeground(Color.BLACK);
        add(submitButton);

        // Back
        backButton = new JButton(" BACK");
        backButton.setBounds(250, 480, 400, 60);
        backButton.setFont(new Font("Arial", Font.BOLD, 22));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        add(backButton);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new ForgotPasswordUI().setVisible(true));
    }
}
