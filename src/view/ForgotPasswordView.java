package view; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ForgotPasswordView extends JPanel {
    public JTextField tokenText;
    public JPasswordField newPassText;
    public JButton submitButton;
    public JButton backButton;

    public ForgotPasswordView() {
        setLayout(null);
        setOpaque(false);

        int panelWidth = 900;
        int panelHeight = 700;
        setSize(panelWidth, panelHeight);
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - panelWidth) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - panelHeight) / 2,
                panelWidth, panelHeight);

        // Tiêu đề
        JLabel titleLabel = new JLabel("RESET PASSWORD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 80, panelWidth, 50);
        add(titleLabel);

        // Token input
        JLabel tokenLabel = new JLabel("Reset Token:");
        tokenLabel.setBounds(250, 160, 200, 40);
        tokenLabel.setForeground(Color.WHITE);
        tokenLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(tokenLabel);

        tokenText = new JTextField("ENTER TOKEN");
        tokenText.setBounds(250, 200, 400, 50);
        tokenText.setFont(new Font("Arial", Font.PLAIN, 20));
        tokenText.setForeground(Color.GRAY);
        add(tokenText);
        tokenText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tokenText.getText().equals("ENTER TOKEN")) {
                    tokenText.setText("");
                    tokenText.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (tokenText.getText().isEmpty()) {
                    tokenText.setText("ENTER TOKEN");
                    tokenText.setForeground(Color.GRAY);
                }
            }
        });

        // Password input
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setBounds(250, 260, 200, 40);
        newPassLabel.setForeground(Color.WHITE);
        newPassLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(newPassLabel);

        newPassText = new JPasswordField("NEW PASSWORD");
        newPassText.setBounds(250, 300, 400, 50);
        newPassText.setFont(new Font("Arial", Font.PLAIN, 20));
        newPassText.setForeground(Color.GRAY);
        newPassText.setEchoChar((char) 0);
        add(newPassText);
        newPassText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(newPassText.getPassword()).equals("NEW PASSWORD")) {
                    newPassText.setText("");
                    newPassText.setForeground(Color.BLACK);
                    newPassText.setEchoChar('●');
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (newPassText.getPassword().length == 0) {
                    newPassText.setText("NEW PASSWORD");
                    newPassText.setForeground(Color.GRAY);
                    newPassText.setEchoChar((char) 0);
                }
            }
        });

        // Submit
        submitButton = new JButton(" SUBMIT");
        submitButton.setBounds(250, 400, 400, 60);
        submitButton.setFont(new Font("Arial", Font.BOLD, 22));
        submitButton.setBackground(Color.WHITE);
        submitButton.setForeground(Color.BLACK);
        add(submitButton);

        // Back
        backButton = new JButton(" BACK");
        backButton.setBounds(250, 480, 400, 60);
        backButton.setFont(new Font("Arial", Font.BOLD, 22));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        add(backButton);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Reset Password");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Thiết lập nền
            try {
                String imagePath = "resources/img/file_background.png";
                ImageIcon bgIcon = new ImageIcon(imagePath);
                if (bgIcon.getIconWidth() > 0) {
                    Image scaledImage = bgIcon.getImage().getScaledInstance(
                            Toolkit.getDefaultToolkit().getScreenSize().width,
                            Toolkit.getDefaultToolkit().getScreenSize().height,
                            Image.SCALE_SMOOTH);
                    JLabel background = new JLabel(new ImageIcon(scaledImage));
                    background.setBounds(0, 0, 
                                       Toolkit.getDefaultToolkit().getScreenSize().width, 
                                       Toolkit.getDefaultToolkit().getScreenSize().height);
                    frame.setContentPane(background);
                } else {
                    frame.getContentPane().setBackground(new Color(41, 128, 185));
                }
            } catch (Exception e) {
                frame.getContentPane().setBackground(new Color(41, 128, 185));
            }
            
            frame.setLayout(null);
            frame.add(new ForgotPasswordView());
            frame.setVisible(true);
        });
    }
}
