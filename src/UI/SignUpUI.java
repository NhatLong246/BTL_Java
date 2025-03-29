package UI;

import javax.swing.*;
import database.UserDAO;
import java.awt.*;
import java.io.File;

public class SignUpUI extends JFrame {
    private JButton signUpButtonNav;
    private JButton signInButtonNav;

    public SignUpUI() {
        setTitle("Sign Up");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Load background image
        String imagePath = "src/resource/img/file_background.png";
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

        // Panel đăng ký
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
        JLabel titleLabel = new JLabel("SIGN UP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, panelWidth, 50);

        // Navigation buttons
        signInButtonNav = new JButton("Sign In");
        signUpButtonNav = new JButton("Sign Up");

        // Kích thước mặc định (inactive)
        int defaultWidth = 200;
        int defaultHeight = 50;
        // Kích thước khi active
        int activeWidth = 240;
        int activeHeight = 60;

        // Tính toán vị trí để căn giữa 2 nút trong panel, không có khoảng cách
        int totalButtonWidth = defaultWidth + activeWidth;
        int startX = (panelWidth - totalButtonWidth) / 2;
        int signInX = startX;
        int signUpX = startX + defaultWidth;
        int buttonY = 80;

        // Đặt trạng thái ban đầu: Sign Up là active
        signUpButtonNav.setBounds(signUpX, buttonY, activeWidth, activeHeight);
        signUpButtonNav.setBackground(Color.WHITE);
        signUpButtonNav.setForeground(Color.BLACK);
        signUpButtonNav.setFont(new Font("Arial", Font.BOLD, 24));

        signInButtonNav.setBounds(signInX, buttonY, defaultWidth, defaultHeight);
        signInButtonNav.setBackground(Color.GRAY);
        signInButtonNav.setForeground(Color.WHITE);
        signInButtonNav.setFont(new Font("Arial", Font.BOLD, 20));

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(250, 160, 200, 40);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField userText = new JTextField();
        userText.setBounds(250, 200, 400, 50);
        userText.setFont(new Font("Arial", Font.PLAIN, 20));

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(250, 260, 200, 40);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField emailText = new JTextField();
        emailText.setBounds(250, 300, 400, 50);
        emailText.setFont(new Font("Arial", Font.PLAIN, 20));

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(250, 360, 200, 40);
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPasswordField passText = new JPasswordField();
        passText.setBounds(250, 400, 400, 50);
        passText.setFont(new Font("Arial", Font.PLAIN, 20));

        // Position
        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setBounds(250, 460, 200, 40);
        positionLabel.setForeground(Color.WHITE);
        positionLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField positionText = new JTextField();
        positionText.setBounds(250, 500, 400, 50);
        positionText.setFont(new Font("Arial", Font.PLAIN, 20));

        // SIGN UP Button
        JButton signUpButton = new JButton(" SIGN UP");
        int buttonWidth = 200;
        int buttonHeight = 60;
        signUpButton.setBounds((panelWidth - buttonWidth) / 2, 580, buttonWidth, buttonHeight);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 22));
        signUpButton.setBackground(Color.LIGHT_GRAY);
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setIcon(resizeIcon("src/resource/img/add.png", signUpButton, 0.5));

        signUpButton.addActionListener(e -> {
            String username = userText.getText();
            String email = emailText.getText();
            String password = new String(passText.getPassword());
            String position = positionText.getText();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || position.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = UserDAO.registerUser(username, email, password, position);
            if (success) {
                JOptionPane.showMessageDialog(null, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Sign up failed! Username or email might be taken.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý sự kiện khi nhấn nút Sign In
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

            dispose();
            new LoginUI().setVisible(true);
        });

        // Thêm thành phần vào panel
        panel.add(titleLabel);
        panel.add(signInButtonNav);
        panel.add(signUpButtonNav);
        panel.add(userLabel);
        panel.add(userText);
        panel.add(emailLabel);
        panel.add(emailText);
        panel.add(passLabel);
        panel.add(passText);
        panel.add(positionLabel);
        panel.add(positionText);
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
        SwingUtilities.invokeLater(() -> new SignUpUI().setVisible(true));
    }
}