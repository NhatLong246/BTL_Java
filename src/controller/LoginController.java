package controller;

import model.repository.UserRepository;
import view.DoctorView;
import view.LoginView;
import view.UI.SignUpUI;
import view.RequestResetView;

import javax.swing.*;

public class LoginController {
    private final LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
    }

    public void login(String username, String password) {
        if (username.isEmpty() || username.equals("USERNAME") ||
                password.isEmpty() || password.equals("PASSWORD")) {
            view.showError("Username or password is not correct");
            return;
        }

        if (UserRepository.loginUser(username, password)) {
            view.hideError();
            JOptionPane.showMessageDialog(view, "Login Successful!");

            String role = UserRepository.getUserRole(username);

            switch (role) {
                case "quản lí":
                    // new AdminView().setVisible(true);
                    break;
                case "bác sĩ":
                    new DoctorView("DOC001").setVisible(true); // Giả định DoctorID
                    break;
                case "bệnh nhân":
                    // new PatientView().setVisible(true);
                    break;
                default:
                    view.showError("Unknown role!");
                    return;
            }
            view.dispose();
        } else {
            view.showError("Username or password is not correct");
        }
    }

    public void navigateToSignUp() {
        view.dispose();
        new SignUpUI().setVisible(true);
    }

    public void navigateToRequestReset() {
        new RequestResetView().setVisible(true);
    }
}