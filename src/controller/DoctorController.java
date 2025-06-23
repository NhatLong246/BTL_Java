package controller;

import model.entity.Appointment;
import model.entity.Doctor;
import model.entity.MedicalRecord;
import model.entity.Medication;
import model.entity.Patient;
import model.entity.Prescription;
import model.entity.PrescriptionDetail;
import model.entity.VitalSign;
import model.enums.Gender;
import model.repository.DoctorRepository;
import model.repository.PatientRepository;
import view.DoctorView;
import view.LoginView;
import database.DatabaseConnection;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import utils.ReportExporter;

public class DoctorController {
    private final DoctorView view;
    private final DoctorRepository repository;
    private final String doctorId;
    private Doctor doctorInfo;
    private final PatientRepository patientRepository;

    public DoctorController(DoctorView view, String doctorId) {
        this.view = view;
        this.repository = new DoctorRepository();
        this.patientRepository = new PatientRepository();
        this.doctorId = doctorId;
        
        loadDoctorInfo();
        repository.checkDoctorScheduleTable();
    }

    private void loadDoctorInfo() {
        try {
            this.doctorInfo = repository.getDoctorById(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải thông tin bác sĩ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getDoctorName() {
        if (doctorInfo != null) {
            return doctorInfo.getFullName();
        }
        return "Không xác định";
    }

    public int getTotalPatients() {
        try {
            return repository.getTotalPatientCount();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tổng số bệnh nhân: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int getTodayAppointments() {
        try {
            return repository.getTodayAppointmentCount(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy số cuộc hẹn hôm nay: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int getWaitingPatients() {
        try {
            return repository.getWaitingPatientCount(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy số bệnh nhân đang chờ: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int getCompletedAppointments() {
        try {
            return repository.getCompletedTodayCount(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy số cuộc hẹn hoàn thành: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public List<Object[]> getUpcomingAppointments() {
        try {
            return repository.getUpcomingAppointments(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách cuộc hẹn: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Object[]> getRecentPatients() {
        try {
            return repository.getRecentPatients(5);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách bệnh nhân mới: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void showHome() {
        view.setSelectedButton(view.getBtnHome());
        view.showHome();
    }

    public void showAddPatientForm() {
        view.setSelectedButton(view.getBtnAdd());
        view.showAddPatientForm();
    }

    public void showPatientList() {
        try {
            view.setSelectedButton(view.getBtnView());
            view.showPatientList(repository.getAllPatients());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Lỗi khi tải danh sách bệnh nhân: " + e.getMessage(),
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            view.showPatientList(new ArrayList<>());
        }
    }

    public void showBookAppointment() {
        view.setSelectedButton(view.getBtnBook());
        view.showBookAppointment();
    }

    public void showExamination() {
        view.setSelectedButton(view.getBtnExamination());
        view.showExamination();
    }

    public void showDeletePatientForm() {
        view.setSelectedButton(view.getBtnDel());
        view.showDeletePatientForm();
    }

    public void addPatient(String name, String birthDateStr, String address, String phone, Gender gender, String medicalHistory, String email) {
        if (name.isEmpty() || birthDateStr.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            
            if (birthDate.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(view, "Ngày sinh không thể ở tương lai!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (birthDate.isBefore(LocalDate.now().minusYears(120))) {
                JOptionPane.showMessageDialog(view, "Ngày sinh không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!isValidVietnamesePhoneNumber(phone)) {
                JOptionPane.showMessageDialog(view, 
                    "Số điện thoại không hợp lệ! Số điện thoại Việt Nam phải:\n" +
                    "- Bắt đầu bằng 0 hoặc +84\n" +
                    "- Đầu số hợp lệ (03x, 05x, 07x, 08x, 09x)\n" +
                    "- Tổng 10 chữ số (không tính mã quốc gia)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (patientRepository.isPhoneNumberExists(phone)) {
                JOptionPane.showMessageDialog(view, 
                    "Số điện thoại " + phone + " đã được sử dụng.\n" +
                    "Vui lòng sử dụng số điện thoại khác.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.isEmpty() && !isValidEmail(email)) {
                JOptionPane.showMessageDialog(view, "Định dạng email không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            String userId = generateNewUserID();
            
            Patient patient = new Patient(
                userId,
                null,
                name,
                birthDate,
                address,
                gender,
                phone,
                LocalDate.now()
            );
    
            if (patientRepository.addPatient(patient, medicalHistory, doctorId, email)) {
                if (patient.getTempUsername() != null && patient.getTempPassword() != null) {
                    JOptionPane.showMessageDialog(
                        view, 
                        "Đã thêm bệnh nhân thành công!\n\n" +
                        "ID Bệnh nhân: " + patient.getPatientID() + "\n" +
                        "Thông tin đăng nhập:\n" +
                        "- Tên đăng nhập: " + patient.getTempUsername() + "\n" +
                        "- Mật khẩu: " + patient.getTempPassword() + "\n\n" +
                        "Vui lòng cung cấp thông tin đăng nhập này cho bệnh nhân.",
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    savePrintableCredentials(patient);
                } else {
                    JOptionPane.showMessageDialog(
                        view, 
                        "Đã thêm bệnh nhân thành công!\nID Bệnh nhân: " + patient.getPatientID(), 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
                showHome();
            } else {
                JOptionPane.showMessageDialog(
                    view, 
                    "Không thể thêm bệnh nhân! Vui lòng kiểm tra lại thông tin.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                view, 
                "Định dạng ngày không hợp lệ! Vui lòng nhập theo định dạng YYYY-MM-DD.", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Lỗi khi kiểm tra dữ liệu: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void savePrintableCredentials(Patient patient) {
        String directory = "credentials";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String filename = directory + File.separator + 
                         "credentials_" + patient.getPatientID() + "_" + 
                         java.time.LocalDate.now().toString() + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("THÔNG TIN ĐĂNG NHẬP HỆ THỐNG QUẢN LÝ BỆNH NHÂN");
            writer.println("-----------------------------------------------");
            writer.println("Họ và tên: " + patient.getFullName());
            writer.println("ID Bệnh nhân: " + patient.getPatientID());
            writer.println();
            writer.println("Tên đăng nhập: " + patient.getTempUsername());
            writer.println("Mật khẩu: " + patient.getTempPassword());
            writer.println();
            writer.println("Vui lòng đổi mật khẩu khi đăng nhập lần đầu tiên.");
            writer.println("-----------------------------------------------");
            writer.println("Ngày tạo: " + java.time.LocalDate.now());
            
            System.out.println("Đã lưu thông tin đăng nhập vào file: " + filename);
            
            JOptionPane.showMessageDialog(
                view,
                "Thông tin đăng nhập đã được lưu tại:\n" + new File(filename).getAbsolutePath(),
                "Thông tin file",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            System.err.println("Không thể lưu thông tin đăng nhập: " + e.getMessage());
            JOptionPane.showMessageDialog(
                view,
                "Không thể lưu thông tin đăng nhập: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String generateNewUserID() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT MAX(SUBSTRING(UserID, 5)) AS maxID FROM UserAccounts WHERE UserID LIKE 'USR-%'";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                int maxID = 0;
                if (rs.next()) {
                    String maxIDStr = rs.getString("maxID");
                    if (maxIDStr != null && !maxIDStr.isEmpty()) {
                        try {
                            maxID = Integer.parseInt(maxIDStr);
                        } catch (NumberFormatException e) {
                            System.err.println("Lỗi chuyển đổi mã UserID: " + e.getMessage());
                        }
                    }
                }
                return String.format("USR-%03d", maxID + 1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo UserID mới: " + e.getMessage());
            e.printStackTrace();
            return "USR-" + (int)(Math.random() * 900 + 100);
        }
    }

    private boolean isValidVietnamesePhoneNumber(String phone) {
        String cleanPhone = phone.replaceAll("\\s+|-|\\(|\\)", "");
        
        if (cleanPhone.startsWith("+84")) {
            cleanPhone = "0" + cleanPhone.substring(3);
        }
        
        String regex = "^0[35789]\\d{8}$";
        
        return cleanPhone.matches(regex);
    }

    private boolean isValidEmail(String email) {
        String trimmedEmail = email.trim();
        
        System.out.println("Đang kiểm tra email: '" + trimmedEmail + "'");
        
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        boolean isValid = trimmedEmail.matches(emailRegex);
        
        System.out.println("Kết quả kiểm tra: " + isValid);
        
        return isValid;
    }

    public void bookAppointment(String patientId, String dateStr) {
        if (patientId.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập ID bệnh nhân và ngày hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try {
            LocalDateTime appointmentDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            if (appointmentDateTime.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(view, "Thời gian hẹn phải sau thời gian hiện tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Patient patient = patientRepository.getPatientByID(patientId);
            if (patient == null) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy bệnh nhân với ID: " + patientId, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (repository.bookAppointment(patientId, appointmentDateTime.toLocalDate(), doctorId)) {
                JOptionPane.showMessageDialog(view, "Đặt lịch hẹn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                showHome();
            } else {
                JOptionPane.showMessageDialog(view, "Không thể đặt lịch hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Định dạng ngày không hợp lệ (YYYY-MM-DD HH:mm:ss)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean deletePatient(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "ID bệnh nhân không hợp lệ!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            Patient patient = patientRepository.getPatientById(patientId);
            if (patient == null) {
                JOptionPane.showMessageDialog(view, 
                    "Không tìm thấy bệnh nhân có ID: " + patientId, 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            boolean hasPendingAppointments = patientRepository.hasActiveAppointments(patientId);
            if (hasPendingAppointments) {
                int confirm = JOptionPane.showConfirmDialog(view,
                    "Bệnh nhân này có các cuộc hẹn đang chờ. Việc xóa sẽ hủy tất cả các cuộc hẹn này.\n" +
                    "Bạn có chắc chắn muốn tiếp tục không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            
            return patientRepository.deletePatient(patientId);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa bệnh nhân: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Đã xảy ra lỗi khi xóa bệnh nhân: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void logout() {
        int option = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            view.dispose();
            new LoginView().setVisible(true);
        }
    }

    public String getDoctorSpecialty() {
        if (doctorInfo != null && doctorInfo.getSpecialization() != null) {
            return doctorInfo.getSpecialization().getName();
        }
        return "Chưa cập nhật";
    }

    public String getDoctorEmail() {
        if (doctorInfo != null) {
            return doctorInfo.getEmail();
        }
        return "Chưa cập nhật";
    }

    public String getDoctorPhone() {
        if (doctorInfo != null) {
            return doctorInfo.getPhoneNumber();
        }
        return "Chưa cập nhật";
    }

    public String getDoctorAddress() {
        if (doctorInfo != null) {
            return doctorInfo.getAddress();
        }
        return "Chưa cập nhật";
    }

    public String getDoctorId() {
        return doctorId;
    }

    public List<Object[]> getTodaySchedule() {
        try {
            return repository.getDoctorTodaySchedule(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch làm việc: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Object[]> getNextAppointments() {
        try {
            return repository.getNextAppointments(doctorId, 5);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy cuộc hẹn sắp tới: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean updateCurrentShiftStatus(String dayOfWeek, String shift, String status) {
        try {
            return repository.updateDoctorScheduleStatus(doctorId, dayOfWeek, shift, status);
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái ca làm việc: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Lỗi khi cập nhật trạng thái: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public String getCurrentShiftInfo() {
        LocalTime now = LocalTime.now();
        String shift = "";
        String timeRange = "";
        
        if ((now.isAfter(LocalTime.of(7, 0)) || now.equals(LocalTime.of(7, 0))) && 
            now.isBefore(LocalTime.of(11, 30))) {
            shift = "Sáng";
            timeRange = "7:00-11:30";
        } else if ((now.isAfter(LocalTime.of(13, 30)) || now.equals(LocalTime.of(13, 30))) && 
                   now.isBefore(LocalTime.of(17, 0))) {
            shift = "Chiều";
            timeRange = "13:30-17:00";
        } else if ((now.isAfter(LocalTime.of(17, 0)) || now.equals(LocalTime.of(17, 0))) || 
                   now.isBefore(LocalTime.of(7, 0))) {
            shift = "Tối";
            timeRange = "17:00-7:00 (hôm sau)";
        } else {
            return "Đang nghỉ giữa ca";
        }
        
        try {
            String status = repository.getDoctorShiftStatus(doctorId, LocalDate.now().getDayOfWeek().toString(), shift);
            if (status.equals("Đang làm việc")) {
                return shift + " (" + timeRange + ") - Đang làm việc";
            } else if (status.equals("Hết ca làm việc")) {
                return shift + " (" + timeRange + ") - Đã kết thúc";
            } else {
                return shift + " (" + timeRange + ") - Không lịch làm việc";
            }
        } catch (SQLException e) {
            return shift + " (" + timeRange + ") - Chưa xác định trạng thái";
        }
    }

    public String[][] getWeeklySchedule() {
        try {
            String[][] scheduleData = new String[3][7];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 7; j++) {
                    scheduleData[i][j] = "Không làm việc";
                }
            }
            
            List<Object[]> scheduleList = repository.getDoctorWeeklySchedule(doctorId);
            for (Object[] schedule : scheduleList) {
                int shiftIndex = getShiftIndex((String)schedule[0]);
                int dayIndex = getDayIndex((String)schedule[1]);
                String status = (String)schedule[2];
                
                if (shiftIndex >= 0 && dayIndex >= 0) {
                    scheduleData[shiftIndex][dayIndex] = status;
                }
            }
            
            return scheduleData;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch làm việc: " + e.getMessage());
            e.printStackTrace();
            
            String[][] defaultSchedule = new String[3][7];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 7; j++) {
                    defaultSchedule[i][j] = "Không làm việc";
                }
            }
            return defaultSchedule;
        }
    }

    private int getShiftIndex(String shift) {
        switch (shift) {
            case "Sáng": return 0;
            case "Chiều": return 1;
            case "Tối": return 2;
            default: return -1;
        }
    }

    private int getDayIndex(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Thứ Hai": return 0;
            case "Thứ Ba": return 1;
            case "Thứ Tư": return 2;
            case "Thứ Năm": return 3;
            case "Thứ Sáu": return 4;
            case "Thứ Bảy": return 5;
            case "Chủ Nhật": return 6;
            default: return -1;
        }
    }

    public boolean updateAllPassedShifts() {
        try {
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            
            String currentShift = getCurrentShift(now);
            
            int currentDayOfWeek = today.getDayOfWeek().getValue();
            String currentDayOfWeekVietnamese = getDayOfWeekInVietnamese(currentDayOfWeek);
            
            System.out.println("Ngày hiện tại: " + currentDayOfWeekVietnamese);
            System.out.println("Ca hiện tại: " + currentShift);
            
            String[] shifts = {"Sáng", "Chiều", "Tối"};
            int currentShiftIndex = -1;
            
            for (int i = 0; i < shifts.length; i++) {
                if (shifts[i].equals(currentShift)) {
                    currentShiftIndex = i;
                    break;
                }
            }
            
            System.out.println("Index ca hiện tại: " + currentShiftIndex);
            
            if (currentShiftIndex == -1) {
                System.out.println("Không tìm thấy ca hiện tại phù hợp.");
                return false;
            }
            
            boolean anyUpdates = false;
            
            try {
                System.out.println("SQL debug: Lấy trạng thái ca làm việc cho " + doctorId + 
                                   " vào " + currentDayOfWeekVietnamese + " ca " + shifts[0]);
                
                for (int i = 0; i < currentShiftIndex; i++) {
                    String status = repository.getShiftStatus(doctorId, currentDayOfWeekVietnamese, shifts[i]);
                    System.out.println("Ca " + shifts[i] + ": " + status);
                    
                    if ("Đang làm việc".equals(status)) {
                        System.out.println("Cập nhật ca " + shifts[i] + " sang Hết ca làm việc");
                        repository.updateShiftStatus(doctorId, currentDayOfWeekVietnamese, 
                                                    shifts[i], "Hết ca làm việc");
                        anyUpdates = true;
                    }
                }
            } catch (Exception e) {
                System.out.println("Lỗi khi cập nhật ca làm việc: " + e.getMessage());
                e.printStackTrace();
            }
            
            return anyUpdates;
        } catch (Exception e) {
            System.out.println("Lỗi tổng thể: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String getDayOfWeekInVietnamese(int day) {
        switch (day) {
            case 1: return "Thứ Hai";
            case 2: return "Thứ Ba";
            case 3: return "Thứ Tư";
            case 4: return "Thứ Năm";
            case 5: return "Thứ Sáu";
            case 6: return "Thứ Bảy";
            case 7: return "Chủ Nhật";
            default: return "";
        }
    }

    private String getCurrentShift(LocalTime time) {
        if ((time.isAfter(LocalTime.of(7, 0)) || time.equals(LocalTime.of(7, 0))) &&
            time.isBefore(LocalTime.of(11, 30))) {
            return "Sáng";
        } else if ((time.isAfter(LocalTime.of(13, 30)) || time.equals(LocalTime.of(13, 30))) &&
                   time.isBefore(LocalTime.of(17, 0))) {
            return "Chiều";
        } else if ((time.isAfter(LocalTime.of(17, 0)) || time.equals(LocalTime.of(17, 0))) ||
                   time.isBefore(LocalTime.of(7, 0))) {
            return "Tối";
        } else {
            return "Ngoài giờ làm việc";
        }
    }

    /**
     * Lấy danh sách bệnh nhân chờ khám
     * @return Danh sách bệnh nhân dưới dạng Object[]
     */
    public List<Object[]> getPatientsForExamination() {
        // Gọi repository để lấy danh sách bệnh nhân chờ khám
        return repository.getPatientsForExamination(doctorId);
    }

    /**
     * Tìm kiếm bệnh nhân chờ khám theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách bệnh nhân phù hợp dưới dạng Object[]
     */
    public List<Object[]> searchPatientsForExamination(String keyword) {
        try {
            List<Object[]> patientRecords = repository.searchPatientsForExamination(doctorId, keyword);
            List<Object[]> result = new ArrayList<>();

            for (Object[] record : patientRecords) {
                Patient patient = (Patient) record[0];
                MedicalRecord medicalRecord = (MedicalRecord) record[1];
                Appointment appointment = (Appointment) record[2];

                String diagnosis = medicalRecord != null ? medicalRecord.getDiagnosis() : "";
                String birthDateStr = patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "";
                String status = appointment != null ? appointment.getStatus().toString() : "Chờ khám";

                // Lấy email từ UserAccounts qua userID
                String email = getEmailFromUserId(patient.getUserID());

                result.add(new Object[]{
                    patient,
                    medicalRecord,
                    email,
                    status
                });
            }

            return result;
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm bệnh nhân chờ khám: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy chỉ số sức khỏe mới nhất của bệnh nhân
     * @param patientId ID của bệnh nhân
     * @return VitalSign hoặc null nếu không tìm thấy
     */
    public VitalSign getVitalSigns(String patientId) {
        try {
            return repository.getLatestVitalSignByPatientId(patientId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chỉ số sức khỏe: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lưu chỉ số sức khỏe vào cơ sở dữ liệu
     * @param patientId ID của bệnh nhân
     * @param vitalSign Chỉ số sức khỏe
     * @return true nếu lưu thành công
     */
    public boolean saveVitalSigns(String patientId, VitalSign vitalSign) {
        try {
            return repository.saveVitalSigns(patientId, vitalSign);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu chỉ số sức khỏe: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lưu đơn thuốc vào cơ sở dữ liệu
     * @param patientId ID bệnh nhân
     * @param medicine Tên thuốc
     * @param dosage Liều lượng
     * @param frequency Tần suất
     * @param duration Thời gian sử dụng
     * @return true nếu lưu thành công
     */
    public boolean savePrescription(String patientId, String medicine, String dosage, String frequency, String duration) {
        try {
            // Sinh ID mới cho đơn thuốc
            String prescriptionId = repository.generateNewPrescriptionId();

            // Chuẩn bị dữ liệu cho prescriptionData
            Map<String, Object> prescriptionData = new HashMap<>();
            prescriptionData.put("prescriptionId", prescriptionId);
            prescriptionData.put("patientId", patientId);

            // Chuẩn bị danh sách thuốc
            List<Map<String, Object>> medicineList = new ArrayList<>();
            Map<String, Object> medicineMap = new HashMap<>();
            medicineMap.put("medicineName", medicine);
            medicineMap.put("dosage", dosage);
            medicineMap.put("instructions", frequency + " - " + duration);
            medicineList.add(medicineMap);

            // Gọi phương thức savePrescription từ repository
            return repository.savePrescription(doctorId, prescriptionData, medicineList);
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu đơn thuốc: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hoàn thành khám bệnh cho bệnh nhân
     * @param patientId ID bệnh nhân
     * @return true nếu hoàn thành thành công
     */
    public boolean completeExamination(String patientId) {
        try {
            return repository.completePatientExamination(patientId, doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi hoàn thành khám bệnh: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sinh mã đơn thuốc mới
     * @return Mã đơn thuốc mới
     */
    public String generateNewPrescriptionId() {
        try {
            return repository.generateNewPrescriptionId();
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo ID đơn thuốc: " + e.getMessage());
            e.printStackTrace();
            return "PRES-" + (int)(Math.random() * 900 + 100);
        }
    }

    /**
     * Lấy email từ userID thông qua UserAccounts
     * @param userId ID người dùng
     * @return Email hoặc chuỗi rỗng nếu không tìm thấy
     */
    private String getEmailFromUserId(String userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT email FROM UserAccounts WHERE userID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("email");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy email: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
    public boolean exportPatientsToExcel(List<Patient> patients, String filePath) {
        try {
            return ReportExporter.exportPatientsToExcel(patients, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean exportPatientsToPdf(List<Patient> patients, String filePath) {
        try {
            JOptionPane.showMessageDialog(view, 
                "Chức năng xuất PDF đang được phát triển!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean exportScheduleToExcel(String doctorId, String doctorName, boolean[][] schedule, String filePath) {
        try {
            return ReportExporter.exportScheduleToExcel(doctorId, doctorName, schedule, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean exportScheduleToPdf(String doctorId, String doctorName, boolean[][] schedule, String filePath) {
        try {
            return ReportExporter.exportScheduleToPdf(doctorId, doctorName, schedule, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean exportAppointmentsToExcel(List<Appointment> appointments, String filePath) {
        try {
            return ReportExporter.exportAppointmentsToExcel(appointments, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
        
    public boolean cancelAppointment(String appointmentId) {
        try {
            return repository.cancelAppointment(appointmentId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy lịch hẹn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Patient getPatientById(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            return null;
        }
        
        try {
            return patientRepository.getPatientById(patientId);
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm bệnh nhân: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Patient> searchPatients(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            return patientRepository.searchPatients(keyword);
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm bệnh nhân: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Patient> getAllPatients() {
        try {
            return patientRepository.getAllPatients();
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách bệnh nhân: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}