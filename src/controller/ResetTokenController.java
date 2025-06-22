package controller;

import model.service.PasswordResetService;
import view.ResetTokenDisplayView;

public class ResetTokenController {
    private ResetTokenDisplayView view;
    private PasswordResetService service;

    public ResetTokenController(ResetTokenDisplayView view) {
        this.view = view;
        this.service = new PasswordResetService();
    }

    public void confirmReset(String token, String newPassword) {
        if (token.isEmpty() || newPassword.isEmpty()) {
            view.showMessage("Vui lòng nhập đầy đủ mã lấy lại mật khẩu và mật khẩu mới!");
            return;
        }

        boolean success = service.confirmResetPassword(token, newPassword);
        if (success) {
            view.showMessage("Mật khẩu được đặt lại thành công. Bạn có thể đăng nhập với mật khẩu mới.");
            view.dispose();
        } else {
            view.showMessage("Mã lấy lại mật khẩu không hợp lệ hoặc không thể đặt lại mật khẩu!");
        }
    }
}