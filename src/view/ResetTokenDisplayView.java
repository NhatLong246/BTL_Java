package view;

import controller.ResetTokenController;

import javax.swing.*;
import java.awt.*;

public class ResetTokenDisplayView extends JPanel {
    public JTextField tokenField;
    public JButton copyButton;
    public JButton proceedButton;

    public ResetTokenDisplayView() {
        setLayout(null);
        setOpaque(false);
        setSize(400, 200);
        setBounds(50, 50, 400, 200);

        JLabel titleLabel = new JLabel("Your Reset Token", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, 400, 30);
        add(titleLabel);

        tokenField = new JTextField();
        tokenField.setBounds(50, 70, 300, 40);
        tokenField.setFont(new Font("Arial", Font.PLAIN, 16));
        tokenField.setEditable(false);
        add(tokenField);

        copyButton = new JButton("Copy");
        copyButton.setBounds(50, 130, 120, 40);
        copyButton.setFont(new Font("Arial", Font.BOLD, 16));
        copyButton.setBackground(Color.WHITE);
        copyButton.setForeground(Color.BLACK);
        add(copyButton);

        proceedButton = new JButton("Proceed");
        proceedButton.setBounds(230, 130, 120, 40);
        proceedButton.setFont(new Font("Arial", Font.BOLD, 16));
        proceedButton.setBackground(Color.WHITE);
        proceedButton.setForeground(Color.BLACK);
        add(proceedButton);
    }

    public void setToken(String token) {
        tokenField.setText(token);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResetTokenController("123e4567-e89b-12d3-a456-426614174000"));
    }
}


