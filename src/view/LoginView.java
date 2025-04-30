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
        String imagePath = "resources/img/file_background.png";
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.err.println("ERROR: Image not found: " + imageFile.getAbsolutePath());
            // Thử tìm vị trí hiện tại để debug
            System.out.println("Current directory: " + new File(".").getAbsolutePath());
            // Thử đường dẫn thứ hai
            imagePath = "resources/img/file_background.png";
            imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.err.println("ERROR: Second path also failed: " + imageFile.getAbsolutePath());
                // Sử dụng một màu nền đơn giản thay vì hình ảnh
                getContentPane().setBackground(new Color(41, 128, 185));
                return; // Không tiếp tục xử lý hình ảnh
            }
        }

        // Tải hình ảnh nếu tìm thấy
        try {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            if (originalIcon.getIconWidth() <= 0) {
                System.err.println("ERROR: Failed to load image or invalid image");
                getContentPane().setBackground(new Color(41, 128, 185));
                return;
            }
            
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
        } catch (Exception e) {
            System.err.println("ERROR loading background image: " + e.getMessage());
            e.printStackTrace();
            getContentPane().setBackground(new Color(41, 128, 185));
        }

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
        showPasswordCheckBox.setContentAreaFilled(false);
        showPasswordCheckBox.setBorderPainted(false);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.setBackground(new Color(0, 0, 0, 0)); // Thêm nền trong suốt
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passText.setEchoChar((char) 0);
            } else {
                if (passText.getPassword().length > 0 && !new String(passText.getPassword()).equals("PASSWORD")) {
                    passText.setEchoChar('●');
                }
            }
        });

        // Tạo một custom JLabel cho Forgot Password để tránh hiệu ứng bôi đen
        class TransparentLabel extends JLabel {
            public TransparentLabel(String text, int alignment) {
                super(text, alignment);
                setOpaque(false);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                // Đảm bảo không vẽ nền
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
                super.paintComponent(g2d);
                g2d.dispose();
                
                // Chỉ vẽ văn bản
                g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText().replaceAll("<html>|</html>|<u>|</u>", ""); // Loại bỏ thẻ HTML khi vẽ
                int x = getWidth() - fm.stringWidth(text) - 5; // Căn lề phải
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, x, y);
                
                // Vẽ gạch chân nếu cần
                if (getText().contains("<u>")) {
                    g2d.drawLine(x, y + 2, x + fm.stringWidth(text), y + 2);
                }
                g2d.dispose();
            }
        }

        // Error Label
        errorLabel = new JLabel("");
        errorLabel.setBounds(250, 380, 400, 30);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        errorLabel.setVisible(false);

        // Forgot Password
        TransparentLabel forgotPasswordLabel = new TransparentLabel("Forgot Password?", SwingConstants.RIGHT);
        forgotPasswordLabel.setBounds(450, 380, 200, 30);
        forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        forgotPasswordLabel.setForeground(Color.WHITE);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean isHovered = false;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                forgotPasswordLabel.setText("<html><u>Forgot Password?</u></html>");
                forgotPasswordLabel.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                forgotPasswordLabel.setText("Forgot Password?");
                forgotPasswordLabel.repaint();
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
        // Đường dẫn khác với đường dẫn hình nền
        loginButton.setIcon(resizeIcon("resources/img/user-profile.png", loginButton, 0.5));
        loginButton.addActionListener(e -> {
            // Kiểm tra đầu vào
            String username = userText.getText();
            String password = new String(passText.getPassword());
            
            if ("USERNAME".equals(username) || "PASSWORD".equals(password)) {
                showError("Vui lòng nhập tên đăng nhập và mật khẩu!");
                return;
            }
            
            // Tắt nút đăng nhập và hiển thị thông báo đang xử lý
            loginButton.setEnabled(false);
            showError("Đang xử lý...");
            errorLabel.setForeground(Color.BLUE);
            
            // Sử dụng SwingWorker để thực hiện đăng nhập không đồng bộ
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    // Chỉ thực hiện đăng nhập, không xử lý giao diện ở đây
                    return controller.login(username, password);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean loginSuccess = get();
                        loginButton.setEnabled(true);
                        
                        // Nếu kết quả trả về false, hiển thị lỗi
                        // Nếu đăng nhập thành công, LoginController sẽ xử lý việc chuyển màn hình
                        if (!loginSuccess) {
                            errorLabel.setForeground(Color.RED);
                            showError("Tên đăng nhập hoặc mật khẩu không đúng!");
                        }
                    } catch (Exception e) {
                        loginButton.setEnabled(true);
                        errorLabel.setForeground(Color.RED);
                        showError("Lỗi đăng nhập: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        });

        // SIGN UP Button
        JButton signUpButton = new JButton(" SIGN UP");
        signUpButton.setBounds(250, 520, 400, 60);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 22));
        signUpButton.setBackground(Color.WHITE);
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setIcon(resizeIcon("resources/img/add.png", signUpButton, 0.5));
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