package controller;

import model.entity.Patient;
import model.entity.Doctor;
import model.enums.Gender;
import model.repository.DoctorRepository;
import view.DoctorView;
import view.LoginView;
import database.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorController {
    private final DoctorView view;
    private final DoctorRepository repository;
    private final String doctorId;
    private Doctor doctorInfo; // Thêm thông tin bác sĩ

    public DoctorController(DoctorView view, String doctorId) {
        this.view = view;
        this.repository = new DoctorRepository();
        this.doctorId = doctorId;
        
        // Tải thông tin bác sĩ khi khởi tạo controller
        loadDoctorInfo();
        repository.checkDoctorScheduleTable();
    }

    /**
     * Tải thông tin bác sĩ từ database
     */
    private void loadDoctorInfo() {
        try {
            this.doctorInfo = repository.getDoctorById(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải thông tin bác sĩ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lấy tên của bác sĩ
     * @return Tên bác sĩ hoặc "Không xác định" nếu không tìm thấy
     */
    public String getDoctorName() {
        if (doctorInfo != null) {
            return doctorInfo.getFullName();
        }
        return "Không xác định";
    }

    /**
     * Lấy tổng số bệnh nhân của bác sĩ
     * @return Số lượng bệnh nhân
     */
    public int getTotalPatients() {
        try {
            return repository.getTotalPatientCount();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tổng số bệnh nhân: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Lấy số lượng cuộc hẹn trong ngày hôm nay
     * @return Số cuộc hẹn hôm nay
     */
    public int getTodayAppointments() {
        try {
            return repository.getTodayAppointmentCount(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy số cuộc hẹn hôm nay: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Lấy số bệnh nhân đang chờ khám
     * @return Số bệnh nhân đang chờ
     */
    public int getWaitingPatients() {
        try {
            return repository.getWaitingPatientCount(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy số bệnh nhân đang chờ: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Lấy số cuộc hẹn đã hoàn thành trong ngày
     * @return Số cuộc hẹn hoàn thành
     */
    public int getCompletedAppointments() {
        try {
            return repository.getCompletedTodayCount(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy số cuộc hẹn hoàn thành: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Lấy danh sách các cuộc hẹn sắp tới
     * @return Danh sách các cuộc hẹn dưới dạng mảng Object[]
     */
    public List<Object[]> getUpcomingAppointments() {
        try {
            return repository.getUpcomingAppointments(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách cuộc hẹn: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách bệnh nhân mới đăng ký gần đây
     * @return Danh sách bệnh nhân dưới dạng mảng Object[]
     */
    public List<Object[]> getRecentPatients() {
        try {
            return repository.getRecentPatients(5); // Lấy 5 bệnh nhân mới nhất
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
            // Hiển thị danh sách trống nếu có lỗi
            view.showPatientList(new ArrayList<>());
        }
    }

    public void showBookAppointment() {
        view.setSelectedButton(view.getBtnBook());
        view.showBookAppointment();
    }

    public void showDeletePatientForm() {
        view.setSelectedButton(view.getBtnDel());
        view.showDeletePatientForm();
    }

    public void addPatient(String name, String birthDateStr, String address, String phone, Gender gender, String medicalHistory) {
        if (name.isEmpty() || birthDateStr.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            // Tạo UserID có định dạng đúng với schema
            String userId = "USR-" + System.currentTimeMillis() % 10000;

            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(view, "Không thể kết nối đến cơ sở dữ liệu!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Tạo đối tượng Patient với các tham số phù hợp
                Patient patient = new Patient(
                    userId,          // userID
                    null,            // patientID - sẽ được tạo trong repository
                    name,            // fullName
                    birthDate,       // dateOfBirth
                    address,         // address
                    gender,          // gender
                    phone,           // phoneNumber
                    LocalDate.now()  // registrationDate
                );

                if (repository.addPatient(patient, medicalHistory)) {
                    JOptionPane.showMessageDialog(view, "Thêm bệnh nhân thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    showHome();
                } else {
                    JOptionPane.showMessageDialog(view, "Không thể thêm bệnh nhân!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(view, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Định dạng ngày không hợp lệ (YYYY-MM-DD)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void bookAppointment(String patientId, String dateStr) {
        if (patientId.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập ID bệnh nhân và ngày hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate appointmentDate = LocalDate.parse(dateStr);
            if (repository.bookAppointment(patientId, appointmentDate, doctorId)) {
                JOptionPane.showMessageDialog(view, "Đặt lịch hẹn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                showHome();
            } else {
                JOptionPane.showMessageDialog(view, "Không thể đặt lịch hẹn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Định dạng ngày không hợp lệ (YYYY-MM-DD)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deletePatient(String patientId) {
        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập ID bệnh nhân!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (repository.deletePatient(patientId)) {
            JOptionPane.showMessageDialog(view, "Xóa bệnh nhân thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            showHome();
        } else {
            JOptionPane.showMessageDialog(view, "Không thể xóa bệnh nhân!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Đăng xuất khỏi hệ thống
     */
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

    /**
     * Lấy chuyên khoa của bác sĩ
     * @return Tên chuyên khoa
     */
    public String getDoctorSpecialty() {
        if (doctorInfo != null && doctorInfo.getSpecialization() != null) {
            return doctorInfo.getSpecialization().getName();
        }
        return "Chưa cập nhật";
    }

    /**
     * Lấy email của bác sĩ
     * @return Địa chỉ email
     */
    public String getDoctorEmail() {
        if (doctorInfo != null) {
            return doctorInfo.getEmail();
        }
        return "Chưa cập nhật";
    }

    /**
     * Lấy số điện thoại của bác sĩ
     * @return Số điện thoại
     */
    public String getDoctorPhone() {
        if (doctorInfo != null) {
            return doctorInfo.getPhoneNumber();
        }
        return "Chưa cập nhật";
    }

    /**
     * Lấy địa chỉ của bác sĩ
     * @return Địa chỉ
     */
    public String getDoctorAddress() {
        if (doctorInfo != null) {
            return doctorInfo.getAddress();
        }
        return "Chưa cập nhật";
    }

    /**
     * Lấy lịch làm việc của bác sĩ trong ngày
     * @return Danh sách các hoạt động trong ngày
     */
    public List<Object[]> getTodaySchedule() {
        try {
            return repository.getDoctorTodaySchedule(doctorId);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch làm việc: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy cuộc hẹn tiếp theo
     * @return Danh sách cuộc hẹn sắp tới
     */
    public List<Object[]> getNextAppointments() {
        try {
            return repository.getNextAppointments(doctorId, 5); // Lấy 5 cuộc hẹn tiếp theo
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy cuộc hẹn sắp tới: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Cập nhật trạng thái ca làm việc hiện tại
     * @param dayOfWeek Ngày trong tuần
     * @param shift Ca làm việc
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công
     */
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

    /**
     * Lấy thông tin ca làm việc hiện tại
     * @return Thông tin ca làm việc
     */
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
        
        // Kiểm tra xem bác sĩ có làm việc trong ca hiện tại không
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

    /**
     * Lấy lịch làm việc theo tuần
     * @return Mảng 2 chiều chứa trạng thái lịch [ca][ngày]
     */
    public String[][] getWeeklySchedule() {
        try {
            // Mặc định tạo mảng với trạng thái "Không làm việc"
            String[][] scheduleData = new String[3][7];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 7; j++) {
                    scheduleData[i][j] = "Không làm việc";
                }
            }
            
            // Lấy dữ liệu từ repository nếu có
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
            
            // Trả về mảng mặc định nếu có lỗi
            String[][] defaultSchedule = new String[3][7];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 7; j++) {
                    defaultSchedule[i][j] = "Không làm việc";
                }
            }
            return defaultSchedule;
        }
    }

    /**
     * Chuyển đổi tên ca làm việc thành index
     * @param shift Tên ca làm việc
     * @return Index của ca (0-2)
     */
    private int getShiftIndex(String shift) {
        switch (shift) {
            case "Sáng": return 0;
            case "Chiều": return 1;
            case "Tối": return 2;
            default: return -1;
        }
    }

    /**
     * Chuyển đổi tên thứ trong tuần thành index
     * @param dayOfWeek Tên thứ trong tuần
     * @return Index của thứ (0-6)
     */
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

    // Thêm phương thức này vào class DoctorController
    /*public boolean updateAllPassedShifts() {
        try {
            // Lấy ngày và thời gian hiện tại
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            
            // Xác định ca hiện tại
            String currentShift = getCurrentShift(now);
            
            // Xác định thứ trong tuần
            int currentDayOfWeek = today.getDayOfWeek().getValue();
            
            // Xác định các ca đã qua để cập nhật thành "Hết ca làm việc"
            // 1. Tất cả các ca của các ngày trong tuần đã qua
            // 2. Các ca của ngày hiện tại, nhưng đã qua giờ làm việc
            
            // Định nghĩa ca làm việc
            String[] shifts = {"Sáng", "Chiều", "Tối"};
            int currentShiftIndex = -1;
            
            for (int i = 0; i < shifts.length; i++) {
                if (shifts[i].equals(currentShift)) {
                    currentShiftIndex = i;
                    break;
                }
            }
            
            // Không có ca nào phù hợp cho cập nhật
            if (currentShiftIndex == -1) {
                return false;
            }
            
            boolean anyUpdates = false;
            
            // Cập nhật tất cả ca trước ngày hiện tại nếu đang là "Đang làm việc"
            for (int day = 1; day <= 7; day++) {
                if (day < currentDayOfWeek) {
                    // Ngày đã qua
                    for (String shift : shifts) {
                        String currentStatus = repository.getShiftStatus(
                                doctorId, getDayOfWeekInVietnamese(day), shift);
                        
                        if ("Đang làm việc".equals(currentStatus)) {
                            repository.updateShiftStatus(
                                    doctorId, getDayOfWeekInVietnamese(day), shift, "Hết ca làm việc");
                            anyUpdates = true;
                        }
                    }
                } 
                else if (day == currentDayOfWeek) {
                    // Ngày hiện tại, chỉ cập nhật ca đã qua
                    for (int i = 0; i < currentShiftIndex; i++) {
                        String currentStatus = repository.getShiftStatus(
                                doctorId, getDayOfWeekInVietnamese(day), shifts[i]);
                        
                        if ("Đang làm việc".equals(currentStatus)) {
                            repository.updateShiftStatus(
                                    doctorId, getDayOfWeekInVietnamese(day), shifts[i], "Hết ca làm việc");
                            anyUpdates = true;
                        }
                    }
                }
            }
            
            return anyUpdates;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/

    /**
     * Chỉ cập nhật trạng thái các ca làm việc đã hết trong ngày hiện tại
     */
    public boolean updateAllPassedShifts() {
        try {
            // Lấy ngày và thời gian hiện tại
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            
            // Xác định ca hiện tại
            String currentShift = getCurrentShift(now);
            
            // Xác định thứ trong tuần hiện tại
            int currentDayOfWeek = today.getDayOfWeek().getValue();
            String currentDayOfWeekVietnamese = getDayOfWeekInVietnamese(currentDayOfWeek);
            
            // Debug
            System.out.println("Ngày hiện tại: " + currentDayOfWeekVietnamese);
            System.out.println("Ca hiện tại: " + currentShift);
            
            // Định nghĩa ca làm việc
            String[] shifts = {"Sáng", "Chiều", "Tối"};
            int currentShiftIndex = -1;
            
            for (int i = 0; i < shifts.length; i++) {
                if (shifts[i].equals(currentShift)) {
                    currentShiftIndex = i;
                    break;
                }
            }
            
            // Debug
            System.out.println("Index ca hiện tại: " + currentShiftIndex);
            
            // Không có ca nào phù hợp cho cập nhật
            if (currentShiftIndex == -1) {
                System.out.println("Không tìm thấy ca hiện tại phù hợp.");
                return false;
            }
            
            boolean anyUpdates = false;
            
            // Chỉ cập nhật các ca trong ngày hiện tại đã qua
            try {
                // In ra SQL query để debug
                System.out.println("SQL debug: Lấy trạng thái ca làm việc cho " + doctorId + 
                                   " vào " + currentDayOfWeekVietnamese + " ca " + shifts[0]);
                
                // CHỈ cập nhật các ca trước ca hiện tại trong ngày hôm nay
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

    // Helper method
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

    // Helper method
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
}