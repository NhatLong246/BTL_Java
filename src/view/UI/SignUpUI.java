package view.UI;

import javax.swing.*;
import model.repository.UserRepository;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
        signInButtonNav = new JButton("Login");
        signUpButtonNav = new JButton("Sign Up");

        // Kích thước mặc định (inactive)
        int defaultWidth = 200;
        int defaultHeight = 50;
        // Kích thước khi active
        int activeWidth = 240;
        int activeHeight = 60;

        // Tính toán vị trí để căn giữa 2 nút trong panel
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
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(250, 160, 200, 40);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField usernameText = new JTextField("USERNAME");
        usernameText.setBounds(250, 200, 400, 50);
        usernameText.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameText.setForeground(Color.GRAY);
        usernameText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameText.getText().equals("USERNAME")) {
                    usernameText.setText("");
                    usernameText.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (usernameText.getText().isEmpty()) {
                    usernameText.setText("USERNAME");
                    usernameText.setForeground(Color.GRAY);
                }
            }
        });

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(250, 260, 200, 40);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField emailText = new JTextField("EMAIL");
        emailText.setBounds(250, 300, 400, 50);
        emailText.setFont(new Font("Arial", Font.PLAIN, 20));
        emailText.setForeground(Color.GRAY);
        emailText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (emailText.getText().equals("EMAIL")) {
                    emailText.setText("");
                    emailText.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (emailText.getText().isEmpty()) {
                    emailText.setText("EMAIL");
                    emailText.setForeground(Color.GRAY);
                }
            }
        });

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(250, 360, 200, 40);
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPasswordField passText = new JPasswordField("PASSWORD");
        passText.setBounds(250, 400, 400, 50);
        passText.setFont(new Font("Arial", Font.PLAIN, 20));
        passText.setForeground(Color.GRAY);
        passText.setEchoChar((char) 0); // Hiển thị placeholder ban đầu
        passText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(passText.getPassword()).equals("PASSWORD")) {
                    passText.setText("");
                    passText.setForeground(Color.BLACK);
                    passText.setEchoChar('●'); // Hiển thị ký tự mật khẩu khi người dùng nhập
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passText.getPassword().length == 0) {
                    passText.setText("PASSWORD");
                    passText.setForeground(Color.GRAY);
                    passText.setEchoChar((char) 0); // Hiển thị placeholder
                }
            }
        });

        // NEXT Button
        JButton nextButton = new JButton(" NEXT");
        int buttonWidth = 200;
        int buttonHeight = 60;
        nextButton.setBounds((panelWidth - buttonWidth) / 2, 480, buttonWidth, buttonHeight);
        nextButton.setFont(new Font("Arial", Font.BOLD, 22));
        nextButton.setBackground(Color.WHITE);
        nextButton.setForeground(Color.BLACK);
        nextButton.setIcon(resizeIcon("resource/img/next-icon.png", nextButton, 0.5)); // Thêm icon "Next"

        nextButton.addActionListener(e -> {
            String username = usernameText.getText();
            String email = emailText.getText();
            String password = new String(passText.getPassword());

            // Kiểm tra placeholder
            if (username.isEmpty() || username.equals("USERNAME") || 
                email.isEmpty() || email.equals("EMAIL") || 
                password.isEmpty() || password.equals("PASSWORD")) {
                JOptionPane.showMessageDialog(null, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra định dạng email
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(null, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Đăng ký với role mặc định là "patient"
            String result = UserRepository.registerUser(username, email, password, "patient");
            if (result.startsWith("Success")) {
                int userId = Integer.parseInt(result.split(":")[1]);
                dispose();
                new PatientInfoUI(userId).setVisible(true); // Truyền userId
            } else {
                JOptionPane.showMessageDialog(null, "Sign up failed! " + result, "Error", JOptionPane.ERROR_MESSAGE);
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
        panel.add(usernameLabel);
        panel.add(usernameText);
        panel.add(emailLabel);
        panel.add(emailText);
        panel.add(passLabel);
        panel.add(passText);
        panel.add(nextButton);

        // Thêm vào frame
        setContentPane(background);
        add(panel);
        setVisible(true);
    }

    private ImageIcon resizeIcon(String path, JButton button, double scaleFactor) {
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
        SwingUtilities.invokeLater(() -> new SignUpUI().setVisible(true));
    }
}