package controller;

import model.service.PasswordResetService;

import java.util.Scanner;

public class ConsolePasswordReset {
    private PasswordResetService service;
    private Scanner scanner;

    public ConsolePasswordReset() {
        service = new PasswordResetService();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Password Reset System ===");
        System.out.print("Enter username or email: ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Error: Username or email cannot be empty!");
            return;
        }

        // Yêu cầu đặt lại mật khẩu
        String resetMessage = service.requestPasswordReset(input);
        if (resetMessage == null) {
            System.out.println("Error: Username or email not found!");
            return;
        }

        // Trích xuất token từ thông điệp
        String token = extractToken(resetMessage);
        System.out.println("A reset token has been sent to your email: " + resetMessage);
        System.out.print("Enter the reset token: ");
        String enteredToken = scanner.nextLine().trim();

        if (!enteredToken.equals(token)) {
            System.out.println("Error: Invalid token!");
            return;
        }

        // Nhập mật khẩu mới
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();

        if (newPassword.isEmpty()) {
            System.out.println("Error: New password cannot be empty!");
            return;
        }

        // Xác nhận đặt lại mật khẩu
        boolean success = service.confirmResetPassword(token, newPassword);
        if (success) {
            System.out.println("Password reset successfully! You can now log in with your new password.");
        } else {
            System.out.println("Error: Failed to reset password. Please try again.");
        }
    }

    private String extractToken(String resetMessage) {
        if (resetMessage != null && resetMessage.startsWith("Token: ")) {
            return resetMessage.split(" sent to ")[0].replace("Token: ", "");
        }
        return null;
    }

    public static void main(String[] args) {
        new ConsolePasswordReset().start();
    }
}