package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import database.ForgotPasswordUI;
import database.UserDAO;

public class LoginUI extends JFrame {
    private JButton signUpButtonNav;
    private JButton signInButtonNav;

    public LoginUI() {
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

        // Căn giữa panel
        panel.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - panelWidth) / 2,
                        (Toolkit.getDefaultToolkit().getScreenSize().height - panelHeight) / 2,
                        panelWidth, panelHeight);

        // Tiêu đề (Di chuyển lên trên cùng, trước các nút điều hướng)
        JLabel titleLabel = new JLabel("PATIENT MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, panelWidth, 50); // Đặt ở y=20, trên cùng của panel

        // Navigation buttons
        signInButtonNav = new JButton("Sign In");
        signUpButtonNav = new JButton("Sign Up");

        // Kích thước mặc định (inactive)
        int defaultWidth = 200;
        int defaultHeight = 50;
        // Kích thước khi active
        int activeWidth = 240;
        int activeHeight = 60;

        // Vị trí ban đầu: Sign In bên trái, Sign Up bên phải
        int signInX = 250; // Vị trí x của Sign In
        int signUpX = 450; // Vị trí x của Sign Up
        int buttonY = 80;  // Dịch xuống y=80 để nhường chỗ cho tiêu đề

        // Đặt trạng thái ban đầu: Sign In là active
        signInButtonNav.setBounds(signInX - 20, buttonY, activeWidth, activeHeight); // Dịch trái để đối xứng
        signInButtonNav.setBackground(Color.WHITE);
        signInButtonNav.setForeground(Color.BLACK);
        signInButtonNav.setFont(new Font("Arial", Font.BOLD, 24));

        signUpButtonNav.setBounds(signUpX + 20, buttonY, defaultWidth, defaultHeight); // Dịch phải để đối xứng
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

        JPasswordField passText = new JPasswordField("PASSWORD");
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

        // Quên mật khẩu (Căn phải)
        JLabel forgotPasswordLabel = new JLabel("Forgot Password?", SwingConstants.RIGHT);
        forgotPasswordLabel.setBounds(450, 360, 200, 30);
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
                new ForgotPasswordUI().setVisible(true);
            }
        });

        // LOGIN Button
        JButton loginButton = new JButton(" LOGIN");
        loginButton.setBounds(250, 420, 400, 60);
        loginButton.setFont(new Font("Arial", Font.BOLD, 22));
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(Color.BLACK);
        loginButton.setIcon(resizeIcon("src/resource/img/user-profile.png", loginButton, 0.5));

        // SIGN UP Button
        JButton signUpButton = new JButton(" SIGN UP");
        signUpButton.setBounds(250, 500, 400, 60);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 22));
        signUpButton.setBackground(Color.WHITE);
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setIcon(resizeIcon("src/resource/img/add.png", signUpButton, 0.5));

        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.isEmpty() || username.equals("USERNAME") || 
                    password.isEmpty() || password.equals("PASSWORD")) {
                    JOptionPane.showMessageDialog(null, "Please enter username and password!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (UserDAO.loginUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    
                    new DoctorUI().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
        });

        signUpButton.addActionListener(e -> {
            dispose();
            new SignUpUI().setVisible(true);
        });

        // Xử lý sự kiện khi nhấn nút Sign Up
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
        panel.add(titleLabel); // Thêm tiêu đề trước
        panel.add(signInButtonNav);
        panel.add(signUpButtonNav);
        panel.add(userLabel);
        panel.add(userText);
        panel.add(passLabel);
        panel.add(passText);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}