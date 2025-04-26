package controller;

import view.ResetTokenDisplayView;
import view.ForgotPasswordView;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.File;

public class ResetTokenController extends JFrame {
    private ResetTokenDisplayView view;
    private String token;

    public ResetTokenController(String token) {
        this.token = token;
        
        setTitle("Reset Token - Patient Management System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Thiết lập background
        setContentPane(createBackgroundPanel());
        setLayout(null);
        
        // Khởi tạo và thiết lập view
        view = new ResetTokenDisplayView();
        view.setToken(token);
        add(view);
        
        // Đăng ký các sự kiện
        registerEvents();
        
        setVisible(true);
    }
    
    private void registerEvents() {
        view.copyButton.addActionListener(e -> {
            StringSelection selection = new StringSelection(token);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            JOptionPane.showMessageDialog(this, "Token copied to clipboard!");
        });
        
        view.proceedButton.addActionListener(e -> {
            dispose();
            showForgotPasswordView();
        });
    }
    
    private void showForgotPasswordView() {
        JFrame frame = new JFrame("Reset Password");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Thiết lập background
        try {
            String imagePath = "resources/img/file_background.png";
            File imageFile = new File(imagePath);
            
            if (!imageFile.exists()) {
                imagePath = "src/resources/img/file_background.png";
                imageFile = new File(imagePath);
            }
            
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        Toolkit.getDefaultToolkit().getScreenSize().width,
                        Toolkit.getDefaultToolkit().getScreenSize().height,
                        Image.SCALE_SMOOTH);
                JLabel background = new JLabel(new ImageIcon(scaledImage));
                background.setBounds(0, 0, 
                                   Toolkit.getDefaultToolkit().getScreenSize().width, 
                                   Toolkit.getDefaultToolkit().getScreenSize().height);
                frame.setContentPane(background);
                frame.setLayout(null);
            } else {
                frame.getContentPane().setBackground(new Color(41, 128, 185));
            }
        } catch (Exception e) {
            e.printStackTrace();
            frame.getContentPane().setBackground(new Color(41, 128, 185));
        }
        
        ForgotPasswordView view = new ForgotPasswordView();
        view.tokenText.setText(token); // Pre-fill token
        frame.add(view);
        
        frame.setVisible(true);
    }
    
    private JPanel createBackgroundPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        
        try {
            String imagePath = "resources/img/file_background.png";
            File imageFile = new File(imagePath);
            
            if (!imageFile.exists()) {
                imagePath = "src/resources/img/file_background.png";
                imageFile = new File(imagePath);
            }
            
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        500, 300, Image.SCALE_SMOOTH);
                JLabel background = new JLabel(new ImageIcon(scaledImage));
                background.setBounds(0, 0, 500, 300);
                panel.add(background);
            } else {
                panel.setBackground(new Color(41, 128, 185));
            }
        } catch (Exception e) {
            e.printStackTrace();
            panel.setBackground(new Color(41, 128, 185));
        }
        
        return panel;
    }
}