/*
package controller;

import model.ResetTokenModel;
import view.ForgotPasswordView;
import view.ResetTokenDisplayView;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;

public class ResetTokenController extends JFrame {
    private ResetTokenDisplayView view;
    private ResetTokenModel model;

    public ResetTokenController(String resetToken) {
        setTitle("Reset Token - Patient Management System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        model = new ResetTokenModel(resetToken);
        view = new ResetTokenDisplayView();
        view.setToken(model.getResetToken());

        setContentPane(setBackgroundImage());
        add(view);
        registerEvents();

        setVisible(true);
    }

    private JLabel setBackgroundImage() {
        String imagePath = "img/file_background.png";
        if (!new File(imagePath).exists()) {
            System.out.println("Image not found: " + imagePath);
        }

        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH);
        ImageIcon bgImage = new ImageIcon(scaledImage);

        JLabel background = new JLabel(bgImage);
        background.setBounds(0, 0, 500, 300);
        return background;
    }

    private void registerEvents() {
        view.copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(model.getResetToken());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            JOptionPane.showMessageDialog(this, "Reset token copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        view.proceedButton.addActionListener(e -> {
            dispose();
            new ForgotPasswordView().setVisible(true); // Thay ForgotPasswordUI bằng class bạn muốn mở
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResetTokenController("123e4567-e89b-12d3-a456-426614174000"));
    }
}
*/

package controller;

import model.ResetTokenModel;
import view.ResetTokenView;
import view.ForgotPasswordView;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.JOptionPane;

public class ResetTokenController {
    private ResetTokenModel model;
    private ResetTokenView view;

    public ResetTokenController(ResetTokenModel model, ResetTokenView view) {
        this.model = model;
        this.view = view;

        // Initialize view with data from model
        this.view.setResetToken(model.getResetToken());

        // Add event listeners
        this.view.addCopyButtonListener(e -> copyToClipboard());
        this.view.addProceedButtonListener(e -> proceedToForgotPassword());
    }

    private void copyToClipboard() {
        StringSelection stringSelection = new StringSelection(model.getResetToken());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        JOptionPane.showMessageDialog(null, "Reset token copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void proceedToForgotPassword() {
        view.dispose();
        ForgotPasswordView forgotPasswordView = new ForgotPasswordView();
        // Here you would create the corresponding model and controller for the ForgotPasswordView
        // ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel();
        // ForgotPasswordController forgotPasswordController = new ForgotPasswordController(forgotPasswordModel, forgotPasswordView);
        forgotPasswordView.setVisible(true);
    }
}