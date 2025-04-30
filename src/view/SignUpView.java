package view;

import controller.SignUpController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.net.URL;

public class SignUpView extends JFrame {
    private JLabel titleLabel;
    private JButton signUpButtonNav;
    private JButton signInButtonNav;
    private JTextField usernameText;
    private JTextField emailText;
    private JPasswordField passText;
    private JButton nextButton;
    private SignUpController controller;

    public SignUpView() {
        this.controller = new SignUpController(this);
        setTitle("Sign Up");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Load background image
        try {
            String imagePath = "resources/img/file_background.png";
            File imageFile = new File(imagePath);
            
            ImageIcon originalIcon = null;
            
            if (imageFile.exists()) {
                System.out.println("Tìm thấy ảnh tại: " + imageFile.getAbsolutePath());
                originalIcon = new ImageIcon(imagePath);
            } else {
                URL imageUrl = getClass().getClassLoader().getResource("img/file_background.png");
                if (imageUrl != null) {
                    System.out.println("Tìm thấy ảnh qua ClassLoader: " + imageUrl);
                    originalIcon = new ImageIcon(imageUrl);
                } else {
                    String absolutePath = "resources/img/file_background.png";
                    if (new File(absolutePath).exists()) {
                        System.out.println("Tìm thấy ảnh tại đường dẫn tuyệt đối");
                        originalIcon = new ImageIcon(absolutePath);
                    }
                }
            }
            
            if (originalIcon == null || originalIcon.getIconWidth() <= 0) {
                System.err.println("Không thể tải hình nền");
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
            System.err.println("Lỗi khi tải hình nền: " + e.getMessage());
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

        emailText = new JTextField("EMAIL");
        emailText.setBounds(250, 300, 400, 50);
        emailText.setFont(new Font("Arial", Font.PLAIN, 20));
        emailText.setForeground(Color.GRAY);
        emailText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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

        passText = new JPasswordField("PASSWORD");
        passText.setBounds(250, 400, 400, 50);
        passText.setFont(new Font("Arial", Font.PLAIN, 20));
        passText.setForeground(Color.GRAY);
        passText.setEchoChar((char) 0);
        passText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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

        // NEXT Button
        nextButton = new JButton(" NEXT");
        int buttonWidth = 200;
        int buttonHeight = 60;
        nextButton.setBounds((panelWidth - buttonWidth) / 2, 480, buttonWidth, buttonHeight);
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
        panel.add(nextButton);

        // Thêm vào frame
        add(panel);
        setVisible(true);
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

    public JButton getNextButton() {
        return nextButton;
    }

    private ImageIcon resizeIcon(String path, JButton button, double scaleFactor) {
        int iconSize = (int) (button.getHeight() * scaleFactor);
        ImageIcon originalIcon = new ImageIcon(path);
        if (originalIcon.getIconWidth() == -1 || originalIcon.getIconHeight() == -1) {
            System.out.println("Không thể tải hình ảnh: " + path);
            return null;
        }
        Image resizedImage = originalIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new SignUpView());
    }
}