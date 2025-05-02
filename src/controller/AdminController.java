package controller;

import model.enums.Gender;
import model.enums.Specialization;
import model.entity.Doctor;
import model.repository.AdminRepository;
import model.repository.UserRepository;
import view.AdminView;

import javax.swing.*;

import database.DatabaseConnection;
import view.LoginView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() ||
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

            // Kiểm tra tuổi của bác sĩ (từ 22 đến 70)
            int age = LocalDate.now().getYear() - dateOfBirth.getYear();
            if (dateOfBirth.plusYears(age).isAfter(LocalDate.now())) {
                age--; // Điều chỉnh nếu sinh nhật trong năm nay chưa đến
            }

            if (age < 22 || age > 70) {
                JOptionPane.showMessageDialog(view, 
                    "Tuổi của bác sĩ phải từ 22 đến 70!\nTuổi hiện tại: " + age, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }    
    
            // Kiểm tra định dạng email
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(view, "Định dạng email không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Kiểm tra số điện thoại
            if (!isValidVietnamesePhoneNumber(phone)) {
                JOptionPane.showMessageDialog(view, 
                    "Số điện thoại không hợp lệ! Số điện thoại Việt Nam phải:\n" +
                    "- Bắt đầu bằng 0 hoặc +84\n" +
                    "- Đầu số hợp lệ (03x, 05x, 07x, 08x, 09x)\n" +
                    "- Tổng 10 chữ số (không tính mã quốc gia)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            boolean doctorCreated = adminRepository.createDoctor(
                "", fullName, dateOfBirth, address, gender, phone, specialtyId, email
            );
    
            if (doctorCreated) {

                String generatedUsername = adminRepository.getLastCreatedUsername();
                String generatedPassword = adminRepository.getLastCreatedPassword();
                // Tạo thông báo thành công với thông tin đăng nhập
                StringBuilder message = new StringBuilder("Tạo tài khoản bác sĩ thành công!\n\n");
                message.append("Thông tin đăng nhập:\n");
                message.append("- Tên đăng nhập: ").append(generatedUsername).append("\n");
                message.append("- Mật khẩu: ").append(generatedPassword).append("\n\n");
                message.append("Vui lòng cung cấp thông tin đăng nhập này cho bác sĩ.");
                
                JOptionPane.showMessageDialog(view, message.toString(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Lưu tài khoản vào file
                saveDoctorCredentials(fullName, email, generatedUsername, generatedPassword);
                
                // Quay về màn hình chính
                showHome();
            } else {
                JOptionPane.showMessageDialog(view, "Không thể tạo bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Thêm phương thức lưu thông tin đăng nhập vào file
    private void saveDoctorCredentials(String fullName, String email, String username, String password) {
        // Tạo thư mục để lưu thông tin đăng nhập
        String directory = "doctor_credentials";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs(); // Tạo thư mục nếu chưa tồn tại
        }
        
        // Tạo tên file có định dạng rõ ràng
        String filename = directory + File.separator + 
                         "doctor_" + username + "_" + 
                         java.time.LocalDate.now().toString() + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("THÔNG TIN ĐĂNG NHẬP HỆ THỐNG QUẢN LÝ BỆNH NHÂN");
            writer.println("-----------------------------------------------");
            writer.println("Họ và tên: " + fullName);
            writer.println("Email: " + email);
            writer.println();
            writer.println("Tên đăng nhập: " + username);
            writer.println("Mật khẩu: " + password);
            writer.println();
            writer.println("Vui lòng đổi mật khẩu khi đăng nhập lần đầu tiên.");
            writer.println("-----------------------------------------------");
            writer.println("Ngày tạo: " + java.time.LocalDate.now());
            
            System.out.println("Đã lưu thông tin đăng nhập vào file: " + filename);
            
            // Hiển thị thông báo cho người dùng biết file được lưu ở đâu
            JOptionPane.showMessageDialog(
                view,
                "Thông tin đăng nhập đã được lưu tại:\n" + new File(filename).getAbsolutePath(),
                "Thông tin file",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            System.err.println("Không thể lưu thông tin đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Thêm phương thức kiểm tra email hợp lệ
    private boolean isValidEmail(String email) {
        // Loại bỏ khoảng trắng thừa
        String trimmedEmail = email.trim();
        
        // In ra log để debug
        System.out.println("Đang kiểm tra email: '" + trimmedEmail + "'");
        
        // Biểu thức chính quy kiểm tra định dạng email được cải tiến
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        boolean isValid = trimmedEmail.matches(emailRegex);
        
        System.out.println("Kết quả kiểm tra: " + isValid);
        
        return isValid;
    }
    
    // Thêm phương thức kiểm tra số điện thoại Việt Nam
    private boolean isValidVietnamesePhoneNumber(String phone) {
        // Loại bỏ khoảng trắng và dấu ngoặc nếu có
        String cleanPhone = phone.replaceAll("\\s+|-|\\(|\\)", "");
        
        // Chuyển đổi +84 thành 0
        if (cleanPhone.startsWith("+84")) {
            cleanPhone = "0" + cleanPhone.substring(3);
        }
        
        // Kiểm tra số điện thoại Việt Nam
        String regex = "^0[35789]\\d{8}$";
        return cleanPhone.matches(regex);
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

    /**
     * Lưu lịch làm việc của bác sĩ
     * @param doctorId ID của bác sĩ
     * @param schedule Mảng 2 chiều boolean chứa trạng thái làm việc theo ca và ngày
     * @return true nếu lưu thành công, false nếu có lỗi
     */
    public boolean saveDoctorSchedule(String doctorId, boolean[][] schedule) {
        try {
            // Kiểm tra bác sĩ có tồn tại
            Doctor doctor = adminRepository.getDoctorInfo(doctorId);
            if (doctor == null) {
                JOptionPane.showMessageDialog(view, 
                    "Không tìm thấy bác sĩ với ID: " + doctorId, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Gọi phương thức lưu lịch trong repository
            boolean result = adminRepository.saveDoctorSchedule(doctorId, schedule);
            
            if (result) {
                JOptionPane.showMessageDialog(view, 
                    "Đã lưu lịch làm việc cho bác sĩ " + doctor.getFullName() + " thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, 
                    "Không thể lưu lịch làm việc! Vui lòng thử lại sau.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
            return result;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, 
                "Lỗi khi lưu lịch làm việc: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
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
            new LoginView().setVisible(true);
        }
    }

    private String hashPassword(String password) {
        return password; // Thay bằng BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Lấy tóm tắt lịch làm việc của tất cả bác sĩ
     * @return Mảng 2 chiều chứa trạng thái làm việc theo ca và ngày
     */
    public String[][] getDoctorScheduleSummary() {
        return adminRepository.getDoctorScheduleSummary();
    }

        /**
     * Lấy lịch làm việc của bác sĩ theo ID
     * @param doctorId ID của bác sĩ
     * @return Mảng 2 chiều boolean chứa trạng thái làm việc theo ca và ngày trong tuần
     */
    public boolean[][] getDoctorSchedule(String doctorId) {
        try {
            // Kiểm tra xem bác sĩ có tồn tại không
            Doctor doctor = adminRepository.getDoctorInfo(doctorId);
            if (doctor == null) {
                JOptionPane.showMessageDialog(view, 
                    "Không tìm thấy bác sĩ với ID: " + doctorId, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            return adminRepository.getDoctorSchedule(doctorId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, 
                "Lỗi khi tải lịch làm việc: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
}