package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class RequestResetView extends JPanel {
    public JTextField inputText;
    public JButton submitButton;

    public RequestResetView() {
        setLayout(null);
        setOpaque(false);
        setSize(900, 500);
        setBounds(
                (Toolkit.getDefaultToolkit().getScreenSize().width - 900) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - 500) / 2,
                900, 500);

        JLabel titleLabel = new JLabel("REQUEST PASSWORD RESET", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 80, 900, 50);
        add(titleLabel);

        JLabel inputLabel = new JLabel("Username or Email:");
        inputLabel.setBounds(250, 160, 200, 40);
        inputLabel.setForeground(Color.WHITE);
        inputLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(inputLabel);

        inputText = new JTextField("ENTER USERNAME OR EMAIL");
        inputText.setBounds(250, 200, 400, 50);
        inputText.setFont(new Font("Arial", Font.PLAIN, 20));
        inputText.setForeground(Color.GRAY);
        add(inputText);

        // Thêm FocusListener cho inputText
        inputText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputText.getText().equals("ENTER USERNAME OR EMAIL")) {
                    inputText.setText("");
                    inputText.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputText.getText().isEmpty()) {
                    inputText.setText("ENTER USERNAME OR EMAIL");
                    inputText.setForeground(Color.GRAY);
                }
            }
        });

        submitButton = new JButton("SUBMIT");
        submitButton.setBounds(250, 300, 400, 60);
        submitButton.setFont(new Font("Arial", Font.BOLD, 22));
        submitButton.setBackground(Color.WHITE);
        submitButton.setForeground(Color.BLACK);
        add(submitButton);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new RequestResetView().setVisible(true));
    }
}
