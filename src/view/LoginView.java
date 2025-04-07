package view;

import controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

public class LoginView extends JFrame {
    private JButton signUpButtonNav;
    private JButton signInButtonNav;
    private JLabel errorLabel;
    private JPasswordField passText;
    private JCheckBox showPasswordCheckBox;
    private JTextField userText;
    private LoginController controller;

    public LoginView() {
        this.controller = new LoginController(this);
        setTitle("Patient Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        // Panel đăng nhập
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
        JLabel titleLabel = new JLabel("PATIENT MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, panelWidth, 50);

        // Navigation buttons
        signInButtonNav = new JButton("Sign In");
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

        userText = new JTextField("USERNAME");
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

        passText = new JPasswordField("PASSWORD");
        passText.setBounds(250, 300, 400, 50);
        passText.setFont(new Font("Arial", Font.PLAIN, 20));
        passText.setForeground(Color.GRAY);
        passText.setEchoChar((char) 0);
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
        forgotPasswordLabel.setBounds(450, 380, 200, 30);
        forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        forgotPasswordLabel.setForeground(Color.WHITE);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                forgotPasswordLabel.setText("<html><u>Forgot Password?</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                forgotPasswordLabel.setText("Forgot Password?");
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                controller.navigateToRequestReset();
            }
        });

        // LOGIN Button
        JButton loginButton = new JButton(" LOGIN");
        loginButton.setBounds(250, 440, 400, 60);
        loginButton.setFont(new Font("Arial", Font.BOLD, 22));
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(Color.BLACK);
        loginButton.setIcon(resizeIcon("src/resource/img/user-profile.png", loginButton, 0.5));
        loginButton.addActionListener(e -> controller.login(userText.getText(), new String(passText.getPassword())));

        // SIGN UP Button
        JButton signUpButton = new JButton(" SIGN UP");
        signUpButton.setBounds(250, 520, 400, 60);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 22));
        signUpButton.setBackground(Color.WHITE);
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setIcon(resizeIcon("src/resource/img/add.png", signUpButton, 0.5));
        signUpButton.addActionListener(e -> controller.navigateToSignUp());

        // Sign Up Navigation Button
        signUpButtonNav.addActionListener(e -> {
            signUpButtonNav.setBounds(signUpX, buttonY, activeWidth, activeHeight);
            signUpButtonNav.setBackground(Color.WHITE);
            signUpButtonNav.setForeground(Color.BLACK);
            signUpButtonNav.setFont(new Font("Arial", Font.BOLD, 24));

            signInButtonNav.setBounds(signInX, buttonY, defaultWidth, defaultHeight);
            signInButtonNav.setBackground(Color.GRAY);
            signInButtonNav.setForeground(Color.WHITE);
            signInButtonNav.setFont(new Font("Arial", Font.BOLD, 20));

            controller.navigateToSignUp();
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
        panel.add(loginButton);
        panel.add(signUpButton);

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

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void hideError() {
        errorLabel.setVisible(false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginView());
    }
}