package controller;

import model.enums.Gender;
import model.enums.Specialization;
import model.entity.Doctor;
import model.repository.AdminRepository;
import model.repository.UserRepository;
import view.AdminView;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminController {
    private AdminView view;
    private AdminRepository adminRepository;
    private UserRepository userRepository;
    private String adminId;

    public AdminController(AdminView view, String adminId) {
        this.view = view;
        this.adminRepository = new AdminRepository();
        this.userRepository = new UserRepository();
        this.adminId = adminId;
    }

    public void showHome() {
        view.setSelectedButton(view.getBtnHome());
        view.showHome();
    }

    public void showAdminInfo() {
        view.showAdminInfo();
    }

    public String getAdminName() {
        return userRepository.getFullNameByUserId(adminId);
    }

    public String getAdminEmail() {
        return userRepository.getEmailByUserId(adminId);
    }

    public String getAdminPhone() {
        return userRepository.getPhoneByUserId(adminId);
    }

    public void showCreateDoctorForm() {
        view.setSelectedButton(view.getBtnCreateDoctor());
        view.showCreateDoctorForm();
    }

    public List<Map<String, String>> getAllSpecialties() {
        return adminRepository.getAllSpecialties();
    }

        public void createDoctor(String username, String fullName, String email, String phone, String address,
                             String birthDate, Gender gender, String specialtyId) {
        try {
            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                address.isEmpty() || birthDate.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            LocalDate dateOfBirth;
            try {
                dateOfBirth = LocalDate.parse(birthDate);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Ngày sinh không hợp lệ! Vui lòng nhập định dạng yyyy-MM-dd.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            String userId = "USR-" + UUID.randomUUID().toString().substring(0, 8);
            String defaultPassword = "Doctor@123";
            String passwordHash = hashPassword(defaultPassword);
    
            boolean userCreated = userRepository.registerUser(
                userId, username, fullName, "Bác sĩ", email, phone, passwordHash
            );
    
            if (!userCreated) {
                JOptionPane.showMessageDialog(view, "Không thể tạo tài khoản người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            boolean doctorCreated = adminRepository.createDoctor(
                userId, fullName, dateOfBirth, address, gender, phone, specialtyId, email
            );
    
            if (doctorCreated) {
                JOptionPane.showMessageDialog(view, "Tạo tài khoản bác sĩ thành công!\nMật khẩu mặc định: " + defaultPassword,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                userRepository.deleteUser(userId);
                JOptionPane.showMessageDialog(view, "Không thể tạo bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void showManageDoctorForm() {
        view.setSelectedButton(view.getBtnManageDoctor());
        List<Doctor> doctors = adminRepository.getAllDoctors();
        view.showManageDoctorForm(doctors);
    }

    public void lockDoctor(String doctorId) {
        try {
            if (doctorId.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập ID bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = adminRepository.lockDoctor(doctorId);
            if (success) {
                JOptionPane.showMessageDialog(view, "Khóa tài khoản bác sĩ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Không tìm thấy bác sĩ hoặc lỗi khi khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void showLockedDoctors() {
        view.setSelectedButton(view.getBtnViewLockedDoctors());
        List<Doctor> lockedDoctors = adminRepository.getLockedDoctors();
        view.showLockedDoctors(lockedDoctors);
    }

    public void unlockDoctor(String doctorId) {
        try {
            boolean success = adminRepository.unlockDoctor(doctorId);
            if (success) {
                JOptionPane.showMessageDialog(view, "Mở khóa tài khoản bác sĩ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                showLockedDoctors();
            } else {
                JOptionPane.showMessageDialog(view, "Không tìm thấy bác sĩ hoặc lỗi khi mở khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void showScheduleDoctorForm() {
        view.setSelectedButton(view.getBtnScheduleDoctor());
        view.showScheduleDoctorForm();
    }

    public void saveDoctorSchedule(String doctorId, boolean[][] schedule) {
        try {
            if (doctorId.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập ID bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = adminRepository.saveDoctorSchedule(doctorId, schedule);
            if (success) {
                JOptionPane.showMessageDialog(view, "Lưu lịch làm việc thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Không tìm thấy bác sĩ hoặc lỗi khi lưu lịch!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void showViewDoctorInfoForm() {
        view.setSelectedButton(view.getBtnViewDoctorInfo());
        List<Doctor> doctors = adminRepository.getAllDoctors();
        view.showViewDoctorInfoForm(doctors);
    }

    public void viewDoctorInfo(String doctorId) {
        try {
            if (doctorId.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập ID bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Doctor doctor = adminRepository.getDoctorInfo(doctorId);
            if (doctor != null) {
                view.showDoctorInfo(doctor);
            } else {
                JOptionPane.showMessageDialog(view, "Không tìm thấy bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            view,
            "Bạn có chắc chắn muốn đăng xuất không?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            view.dispose();
        }
    }

    private String hashPassword(String password) {
        return password; // Thay bằng BCrypt.hashpw(password, BCrypt.gensalt());
    }
}