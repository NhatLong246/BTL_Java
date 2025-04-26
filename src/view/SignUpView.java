package view;

import controller.SignUpController;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class SignUpView extends JFrame {
    private JButton signUpButtonNav;
    private JButton signInButtonNav;
    private JTextField usernameText;
    private JTextField emailText;
    private JPasswordField passText;
    private SignUpController controller;

    public SignUpView() {
        this.controller = new SignUpController(this);
        setTitle("Sign Up");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Load background image
        try {
            // Kiểm tra file trực tiếp
            String imagePath = "resources/img/file_background.png";
            File imageFile = new File(imagePath);
            
            ImageIcon originalIcon = null;
            
            if (imageFile.exists()) {
                System.out.println("Tìm thấy ảnh tại: " + imageFile.getAbsolutePath());
                originalIcon = new ImageIcon(imagePath);
            } else {
                // Thử dùng ClassLoader
                URL imageUrl = getClass().getClassLoader().getResource("img/file_background.png");
                if (imageUrl != null) {
                    System.out.println("Tìm thấy ảnh qua ClassLoader: " + imageUrl);
                    originalIcon = new ImageIcon(imageUrl);
                } else {
                    // Thử đường dẫn tuyệt đối
                    String absolutePath = "d:/codejava/BTL_QuanLyBenhNhan/resources/img/file_background.png";
                    if (new File(absolutePath).exists()) {
                        System.out.println("Tìm thấy ảnh tại đường dẫn tuyệt đối");
                        originalIcon = new ImageIcon(absolutePath);
                    }
                }
            }
            
            if (originalIcon == null || originalIcon.getIconWidth() <= 0) {
                System.err.println("Failed to load image");
                getContentPane().setBackground(new Color(41, 128, 185));
            } else {
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        Toolkit.getDefaultToolkit().getScreenSize().width,
                        Toolkit.getDefaultToolkit().getScreenSize().height,
                        Image.SCALE_SMOOTH
                );
                ImageIcon bgImage = new ImageIcon(scaledImage);
                
                JLabel background = new JLabel(bgImage);
                background.setBounds(0, 0, 
                                  Toolkit.getDefaultToolkit().getScreenSize().width, 
                                  Toolkit.getDefaultToolkit().getScreenSize().height);
                
                setContentPane(background);
                setLayout(null);
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
            getContentPane().setBackground(new Color(41, 128, 185));
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
        JLabel titleLabel = new JLabel("SIGN UP", SwingConstants.CENTER);
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

        usernameText = new JTextField();
        usernameText.setBounds(250, 200, 400, 50);
        usernameText.setFont(new Font("Arial", Font.PLAIN, 20));

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(250, 260, 200, 40);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 20));

        emailText = new JTextField();
        emailText.setBounds(250, 300, 400, 50);
        emailText.setFont(new Font("Arial", Font.PLAIN, 20));

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(250, 360, 200, 40);
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 20));

        passText = new JPasswordField();
        passText.setBounds(250, 400, 400, 50);
        passText.setFont(new Font("Arial", Font.PLAIN, 20));

        // NEXT Button
        JButton nextButton = new JButton(" NEXT");
        int buttonWidth = 200;
        int buttonHeight = 60;
        nextButton.setBounds((panelWidth - buttonWidth) / 2, 480, buttonWidth, buttonHeight);
        nextButton.setFont(new Font("Arial", Font.BOLD, 22));
        nextButton.setBackground(Color.WHITE);
        nextButton.setForeground(Color.BLACK);
        nextButton.setIcon(resizeIcon("src/resources/img/add.png", nextButton, 0.5));
        nextButton.addActionListener(e -> controller.signUp(usernameText.getText(), emailText.getText(), new String(passText.getPassword())));

        // Sign In Navigation Button
        signInButtonNav.addActionListener(e -> {
            int totalButtonWidthActive = activeWidth + defaultWidth;
            int startXActive = (panelWidth - totalButtonWidthActive) / 2;

            signInButtonNav.setBounds(startXActive, buttonY, activeWidth, activeHeight);
            signInButtonNav.setBackground(Color.WHITE);
            signInButtonNav.setForeground(Color.BLACK);
            signInButtonNav.setFont(new Font("Arial", Font.BOLD, 24));

            signUpButtonNav.setBounds(startXActive + activeWidth, buttonY, defaultWidth, defaultHeight);
            signUpButtonNav.setBackground(Color.GRAY);
            signUpButtonNav.setForeground(Color.WHITE);
            signUpButtonNav.setFont(new Font("Arial", Font.BOLD, 20));

            controller.navigateToLogin();
        });

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
        panel.add(nextButton);

        // Thêm vào frame
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
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new SignUpView().setVisible(true));
    }
}