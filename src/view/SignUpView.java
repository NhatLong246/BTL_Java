package view;

import controller.SignUpController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

public class SignUpView extends JFrame {
    private JLabel titleLabel;
    private JButton signUpButtonNav;
    private JButton signInButtonNav;
    private JTextField usernameText;
    private JTextField emailText;
    private JPasswordField passText;
    private JTextField phoneText; // Thêm ô nhập số điện thoại
    private JButton nextButton;
    private JLabel errorLabel;
    private SignUpController controller;

    public SignUpView() {
        setTitle("Sign Up");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Load background image
        String imagePath = "resources/img/file_background.png";
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.out.println("Image not found: " + imagePath);
            getContentPane().setBackground(Color.LIGHT_GRAY);
        } else {
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
            setContentPane(background);
        }

        // Panel đăng ký
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
        titleLabel = new JLabel("SIGN UP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, panelWidth, 50);

        // Navigation buttons
        signInButtonNav = new JButton("Login");
        signUpButtonNav = new JButton("Sign Up");

        int defaultWidth = 200;
        int defaultHeight = 50;
        int activeWidth = 240;
        int activeHeight = 60;

        int totalButtonWidth = defaultWidth + activeWidth;
        int startX = (panelWidth - totalButtonWidth) / 2;
        int signInX = startX;
        int signUpX = startX + defaultWidth;
        int buttonY = 80;

        signUpButtonNav.setBounds(signUpX, buttonY, activeWidth, activeHeight);
        signUpButtonNav.setBackground(Color.WHITE);
        signUpButtonNav.setForeground(Color.BLACK);
        signUpButtonNav.setFont(new Font("Arial", Font.BOLD, 24));

        signInButtonNav.setBounds(signInX, buttonY, defaultWidth, defaultHeight);
        signInButtonNav.setBackground(Color.GRAY);
        signInButtonNav.setForeground(Color.WHITE);
        signInButtonNav.setFont(new Font("Arial", Font.BOLD, 20));

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(250, 160, 200, 40);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 20));

        usernameText = new JTextField("USERNAME");
        usernameText.setBounds(250, 200, 400, 50);
        usernameText.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameText.setForeground(Color.GRAY);
        usernameText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(250, 260, 200, 40);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 20));

        emailText = new JTextField("EMAIL");
        emailText.setBounds(250, 300, 400, 50);
        emailText.setFont(new Font("Arial", Font.PLAIN, 20));
        emailText.setForeground(Color.GRAY);
        emailText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(250, 360, 200, 40);
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 20));

        passText = new JPasswordField("PASSWORD");
        passText.setBounds(250, 400, 400, 50);
        passText.setFont(new Font("Arial", Font.PLAIN, 20));
        passText.setForeground(Color.GRAY);
        passText.setEchoChar((char) 0);
        passText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Phone Number
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(250, 460, 200, 40);
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 20));

        phoneText = new JTextField("PHONENUMBER (+84/0)");
        phoneText.setBounds(250, 500, 400, 50);
        phoneText.setFont(new Font("Arial", Font.PLAIN, 20));
        phoneText.setForeground(Color.GRAY);
        phoneText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Error Label
        errorLabel = new JLabel("");
        errorLabel.setBounds(250, 560, 400, 30);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        errorLabel.setVisible(false);

        // NEXT Button
        nextButton = new JButton(" NEXT");
        int buttonWidth = 200;
        int buttonHeight = 60;
        nextButton.setBounds((panelWidth - buttonWidth) / 2, 600, buttonWidth, buttonHeight);
        nextButton.setFont(new Font("Arial", Font.BOLD, 22));
        nextButton.setBackground(Color.WHITE);
        nextButton.setForeground(Color.BLACK);
        nextButton.setIcon(resizeIcon("resources/img/next_icon.png", nextButton, 0.5));

        // Thêm thành phần vào panel
        panel.add(titleLabel);
        panel.add(signInButtonNav);
        panel.add(signUpButtonNav);
        panel.add(usernameLabel);
        panel.add(usernameText);
        panel.add(emailLabel);
        panel.add(emailText);
        panel.add(passLabel);
        panel.add(passText);
        panel.add(phoneLabel);
        panel.add(phoneText);
        panel.add(errorLabel);
        panel.add(nextButton);

        // Thêm panel vào frame
        add(panel);

        // Khởi tạo controller sau khi tất cả thành phần giao diện đã được tạo
        this.controller = new SignUpController(this);
    }

    // Getters cho các thành phần giao diện
    public JButton getSignInButtonNav() {
        return signInButtonNav;
    }

    public JButton getSignUpButtonNav() {
        return signUpButtonNav;
    }

    public JTextField getUsernameText() {
        return usernameText;
    }

    public JTextField getEmailText() {
        return emailText;
    }

    public JPasswordField getPassText() {
        return passText;
    }

    public JTextField getPhoneText() { // Thêm getter cho phoneText
        return phoneText;
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void hideError() {
        errorLabel.setVisible(false);
    }

    private ImageIcon resizeIcon(String path, JButton button, double scaleFactor) {
        File imageFile = new File(path);
        if (!imageFile.exists()) {
            System.out.println("Failed to load image: " + path);
            return null;
        }
        int iconSize = (int) (button.getHeight() * scaleFactor);
        ImageIcon originalIcon = new ImageIcon(path);
        if (originalIcon.getIconWidth() == -1 || originalIcon.getIconHeight() == -1) {
            System.out.println("Failed to load image: " + path);
            return null;
        }
        Image resizedImage = originalIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignUpView signUpView = new SignUpView();
            signUpView.setVisible(true);
        });
    }
}