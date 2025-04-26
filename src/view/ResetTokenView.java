package view;

import view.UI.ResetTokenDisplayUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class ResetTokenView extends JFrame {
    private JTextField tokenField;
    private JButton copyButton;
    private JButton proceedButton;
    private JPanel panel;
    
    public ResetTokenView() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Reset Token - Patient Management System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(null);

        // Load background image
        String imagePath = "img/file_background.png";
        if (!new File(imagePath).exists()) {
            System.out.println("Image not found: " + imagePath);
        }

        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH);
        ImageIcon bgImage = new ImageIcon(scaledImage);

        JLabel background = new JLabel(bgImage);
        background.setBounds(0, 0, 500, 300);

        // Panel to display reset token
        panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(400, 200);
        panel.setBackground(new Color(0, 0, 0, 150));
        panel.setBounds(50, 50, 400, 200);

        // Title
        JLabel titleLabel = new JLabel("Your Reset Token", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, 400, 30);

        // Token display field (non-editable)
        tokenField = new JTextField();
        tokenField.setBounds(50, 70, 300, 40);
        tokenField.setFont(new Font("Arial", Font.PLAIN, 16));
        tokenField.setEditable(false);

        // Copy button
        copyButton = new JButton("Copy");
        copyButton.setBounds(50, 130, 120, 40);
        copyButton.setFont(new Font("Arial", Font.BOLD, 16));
        copyButton.setBackground(Color.WHITE);
        copyButton.setForeground(Color.BLACK);

        // Proceed button
        proceedButton = new JButton("Proceed");
        proceedButton.setBounds(230, 130, 120, 40);
        proceedButton.setFont(new Font("Arial", Font.BOLD, 16));
        proceedButton.setBackground(Color.WHITE);
        proceedButton.setForeground(Color.BLACK);

        // Add components to panel
        panel.add(titleLabel);
        panel.add(tokenField);
        panel.add(copyButton);
        panel.add(proceedButton);

        // Add to frame
        setContentPane(background);
        add(panel);
    }
    
    public void setResetToken(String token) {
        tokenField.setText(token);
    }
    
    public void addCopyButtonListener(ActionListener listener) {
        copyButton.addActionListener(listener);
    }
    
    public void addProceedButtonListener(ActionListener listener) {
        proceedButton.addActionListener(listener);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new ResetTokenDisplayUI("123e4567-e89b-12d3-a456-426614174000"));
    }
}