package model.service;

import model.repository.UserRepository;

public class PasswordResetService {
    public boolean confirmResetPassword(String token, String newPassword) {
        return UserRepository.confirmResetPassword(token, newPassword);
    }
}
