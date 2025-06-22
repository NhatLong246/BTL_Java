package controller;

import model.service.PasswordResetService;
import view.RequestResetView;
import view.ResetTokenDisplayView;

public class RequestResetController {
    private RequestResetView view;
    private PasswordResetService service;

    public RequestResetController(RequestResetView view) {
        this.view = view;
        this.service = new PasswordResetService();
    }

    public void requestReset(String username, String email) {
        if (username.isEmpty() && email.isEmpty()) {
            view.showMessage("Please enter either a username or an email!");
            return;
        }

        String input = username.isEmpty() ? email : username;
        String resetMessage = service.requestPasswordReset(input);

        if (resetMessage != null) {
            view.showMessage("A reset token has been sent to your email!");
            view.dispose();
            new ResetTokenDisplayView(extractToken(resetMessage)).setVisible(true);
        } else {
            view.showMessage("Username or email not found!");
        }
    }

    private String extractToken(String resetMessage) {
        if (resetMessage != null && resetMessage.startsWith("Token: ")) {
            return resetMessage.split(" sent to ")[0].replace("Token: ", "");
        }
        return null;
    }
}