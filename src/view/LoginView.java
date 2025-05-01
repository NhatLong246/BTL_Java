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
            System.out.println("Current directory: " + new File(".").getAbsolutePath());
            getContentPane().setBackground(new Color(41, 128, 185));
        } else {
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
        }

        // Login panel
        int panelWidth = 900;
        int panelHeight = 700;

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(panelWidth, panelHeight);
        panel.setBackground(new Color(0, 0, 0, 150));

        panel.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - panelWidth) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - panelHeight) / 2,
                panelWidth, panelHeight);

        // Title
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

        userText = new JTextField("USERNAME");
        userText.setBounds(250, 200, 400, 50);
        userText.setFont(new Font("Arial", Font.PLAIN, 20));
        userText.setForeground(Color.GRAY);
        userText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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
                    if (!showPasswordCheckBox.isSelected()) {
                        passText.setEchoChar('●');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passText.getPassword().length == 0) {
                    passText.setText("PASSWORD");
                    passText.setForeground(Color.GRAY);
                    passText.setEchoChar((char) 0);
                } else if (!showPasswordCheckBox.isSelected()) {
                    passText.setEchoChar('●');
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
        class TransparentLabel extends JLabel {
            public TransparentLabel(String text, int alignment) {
                super(text, alignment);
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
                super.paintComponent(g2d);
                g2d.dispose();

                g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText().replaceAll("<html>|</html>|<u>|</u>", "");
                int x = getWidth() - fm.stringWidth(text) - 5;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, x, y);

                if (getText().contains("<u>")) {
                    g2d.drawLine(x, y + 2, x + fm.stringWidth(text), y + 2);
                }
                g2d.dispose();
            }
        }

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

        // Panel for LOGIN button and Sign Up link
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(0, 440, panelWidth, 100);

        // LOGIN Button
        JButton loginButton = new JButton(" LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 22));
        loginButton.setPreferredSize(new Dimension(200, 60));
        loginButton.setMaximumSize(new Dimension(200, 60));
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(Color.BLACK);
        loginButton.setIcon(resizeIcon("resources/img/user-profile.png", loginButton, 0.5));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if ("USERNAME".equals(username) || "PASSWORD".equals(password)) {
                showError("Vui lòng nhập tên đăng nhập và mật khẩu!");
                return;
            }

            loginButton.setEnabled(false);
            showError("Đang xử lý...");
            errorLabel.setForeground(Color.BLUE);

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return controller.login(username, password);
                }

                @Override
                protected void done() {
                    try {
                        boolean loginSuccess = get();
                        loginButton.setEnabled(true);

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

        // Sign Up Link
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signUpPanel.setOpaque(false);
        signUpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel noAccountLabel = new JLabel("Chưa có Tài khoản.");
        noAccountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        noAccountLabel.setForeground(Color.WHITE);

        JLabel createAccountLabel = new JLabel("Tạo tài khoản");
        createAccountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        createAccountLabel.setForeground(Color.WHITE);
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));
        createAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                controller.navigateToSignUp();
            }
        });

        signUpPanel.add(noAccountLabel);
        signUpPanel.add(createAccountLabel);

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(signUpPanel);

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

        // Add components to panel
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

        // Add to frame
        add(panel);
        setVisible(true);
    }

    private ImageIcon resizeIcon(String path, JButton button, double scale) {
        ImageIcon originalIcon = new ImageIcon(path);
        if (originalIcon.getIconWidth() == -1 || originalIcon.getIconHeight() == -1) {
            System.out.println("Failed to load image: " + path);
            return null;
        }

        int size = (int) (button.getPreferredSize().height * scale);
        Image resizedImage = originalIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
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
            // Thiết lập look and feel để có giao diện đẹp hơn
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginView());
    }
}