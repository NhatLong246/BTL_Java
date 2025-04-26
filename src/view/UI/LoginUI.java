package view.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import view.UI.ForgotPasswordUI;
import view.UI.RequestResetUI;
import model.repository.UserRepository;

public class LoginUI extends JFrame {
    private JButton signUpButtonNav;
    private JButton signInButtonNav;
    private JLabel errorLabel;
    private JPasswordField passText;
    private JCheckBox showPasswordCheckBox;

    public LoginUI() {
        setTitle("Patient Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Load background image
        String imagePath = "resource/img/file_background.png";
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

        // Panel đăng nhập
        int panelWidth = 900;
        int panelHeight = 700;

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(panelWidth, panelHeight);
        panel.setBackground(new Color(0, 0, 0, 150));

        // Căn giữa panel
        panel.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - panelWidth) / 2,
                        (Toolkit.getDefaultToolkit().getScreenSize().height - panelHeight) / 2,
                        panelWidth, panelHeight);

        // Tiêu đề
        JLabel titleLabel = new JLabel("PATIENT MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, panelWidth, 50);

        // Navigation buttons
        signInButtonNav = new JButton("Login");
        signUpButtonNav = new JButton("Sign Up");

        int defaultWidth = 200;
        int defaultHeight = 50;
        int activeWidth = 240;
        int activeHeight = 60;

        int signInX = 250;
        int signUpX = 450;
        int buttonY = 80;

        signInButtonNav.setBounds(signInX - 20, buttonY, activeWidth, activeHeight);
        signInButtonNav.setBackground(Color.WHITE);
        signInButtonNav.setForeground(Color.BLACK);
        signInButtonNav.setFont(new Font("Arial", Font.BOLD, 24));

        signUpButtonNav.setBounds(signUpX + 20, buttonY, defaultWidth, defaultHeight);
        signUpButtonNav.setBackground(Color.GRAY);
        signUpButtonNav.setForeground(Color.WHITE);
        signUpButtonNav.setFont(new Font("Arial", Font.BOLD, 20));

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(250, 160, 200, 40);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField userText = new JTextField("USERNAME");
        userText.setBounds(250, 200, 400, 50);
        userText.setFont(new Font("Arial", Font.PLAIN, 20));
        userText.setForeground(Color.GRAY);
        userText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (userText.getText().equals("USERNAME")) {
                    userText.setText("");
                    userText.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (userText.getText().isEmpty()) {
                    userText.setText("USERNAME");
                    userText.setForeground(Color.GRAY);
                }
            }
        });

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(250, 260, 200, 40);
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 20));

        passText = new JPasswordField();
        passText.setBounds(250, 300, 400, 50);
        passText.setFont(new Font("Arial", Font.PLAIN, 20));
        passText.setForeground(Color.GRAY);
        passText.setText("PASSWORD");
        passText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(passText.getPassword()).equals("PASSWORD")) {
                    passText.setText("");
                    passText.setForeground(Color.BLACK);
                    passText.setEchoChar('●');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passText.getPassword().length == 0) {
                    passText.setText("PASSWORD");
                    passText.setForeground(Color.GRAY);
                    passText.setEchoChar((char) 0);
                }
            }
        });

        // Show Password CheckBox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBounds(250, 350, 150, 30);
        showPasswordCheckBox.setForeground(Color.WHITE);
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        showPasswordCheckBox.setOpaque(false);

        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passText.setEchoChar((char) 0);
            } else {
                if (passText.getPassword().length > 0 && !new String(passText.getPassword()).equals("PASSWORD")) {
                    passText.setEchoChar('●');
                }
            }
        });

        // Error Label
        errorLabel = new JLabel("");
        errorLabel.setBounds(250, 380, 400, 30);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        errorLabel.setVisible(false);

        // Forgot Password
        JLabel forgotPasswordLabel = new JLabel("Forgot Password?", SwingConstants.RIGHT);
        forgotPasswordLabel.setBounds(250, 380, 400, 30); // Căn phải trong khu vực 400px (từ x = 250 đến x = 650)
        forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        forgotPasswordLabel.setForeground(Color.WHITE);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new RequestResetUI().setVisible(true);
            }
        });

        // Panel chứa nút LOGIN và dòng chữ "Chưa có Tài khoản. Tạo tài khoản"
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(0, 440, panelWidth, 100);

        // LOGIN Button
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 22));
        loginButton.setPreferredSize(new Dimension(200, 60));
        loginButton.setMaximumSize(new Dimension(200, 60));
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(Color.BLACK);
        loginButton.setIcon(resizeIcon("src/resource/img/user-profile.png", 30));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel chứa dòng chữ "Chưa có Tài khoản. Tạo tài khoản"
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signUpPanel.setOpaque(false);
        signUpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Phần "Chưa có Tài khoản."
        JLabel noAccountLabel = new JLabel("Chưa có Tài khoản.");
        noAccountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        noAccountLabel.setForeground(Color.WHITE);

        // Phần "Tạo tài khoản" (liên kết)
        JLabel createAccountLabel = new JLabel("Tạo tài khoản");
        createAccountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        createAccountLabel.setForeground(Color.WHITE);
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));

        createAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new SignUpUI().setVisible(true);
            }
        });

        // Thêm hai JLabel vào signUpPanel
        signUpPanel.add(noAccountLabel);
        signUpPanel.add(createAccountLabel);

        // Thêm các thành phần vào buttonPanel
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(signUpPanel);

        // Action listener cho nút LOGIN
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.isEmpty() || username.equals("USERNAME") || 
                    password.isEmpty() || password.equals("PASSWORD")) {
                errorLabel.setText("Username or password is not correct");
                errorLabel.setVisible(true);
                return;
            }

            if (UserRepository.loginUser(username, password)) {
                errorLabel.setVisible(false);
                JOptionPane.showMessageDialog(null, "Login Successful!");
                
                String role = UserRepository.getUserRole(username);
                
                switch (role) {
                    case "admin":
                        // new AdminUI().setVisible(true);
                        break;
                    case "doctor":
                        new DoctorUI().setVisible(true);
                        break;
                    case "patient":
                        // new PatientUI().setVisible(true);
                        break;
                    default:
                        errorLabel.setText("Unknown role!");
                        errorLabel.setVisible(true);
                        return;
                }
                dispose();
            } else {
                errorLabel.setText("Username or password is not correct");
                errorLabel.setVisible(true);
            }
        });

        signUpButtonNav.addActionListener(e -> {
            signUpButtonNav.setBounds(signUpX, buttonY, activeWidth, activeHeight);
            signUpButtonNav.setBackground(Color.WHITE);
            signUpButtonNav.setForeground(Color.BLACK);
            signUpButtonNav.setFont(new Font("Arial", Font.BOLD, 24));

            signInButtonNav.setBounds(signInX, buttonY, defaultWidth, defaultHeight);
            signInButtonNav.setBackground(Color.GRAY);
            signInButtonNav.setForeground(Color.WHITE);
            signInButtonNav.setFont(new Font("Arial", Font.BOLD, 20));

            dispose();
            new SignUpUI().setVisible(true);
        });

        // Thêm thành phần vào panel
        panel.add(titleLabel);
        panel.add(signInButtonNav);
        panel.add(signUpButtonNav);
        panel.add(userLabel);
        panel.add(userText);
        panel.add(passLabel);
        panel.add(passText);
        panel.add(showPasswordCheckBox);
        panel.add(errorLabel);
        panel.add(forgotPasswordLabel);
        panel.add(buttonPanel);

        // Thêm vào frame
        setContentPane(background);
        add(panel);
        setVisible(true);
    }

    private ImageIcon resizeIcon(String path, int size) {
        ImageIcon originalIcon = new ImageIcon(path);
        if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.out.println("Failed to load image: " + path);
            return null;
        }
        Image resizedImage = originalIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}