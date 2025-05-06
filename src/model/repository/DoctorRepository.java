package model.repository;

import model.entity.Doctor;
import model.entity.MedicalRecord;
import model.entity.Patient;
import model.enums.Gender;
import model.enums.Specialization;
import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorRepository {

    /**
     * Lấy thông tin bác sĩ theo ID
     * @param doctorId ID của bác sĩ
     * @return Doctor object hoặc null nếu không tìm thấy
     * @throws SQLException nếu có lỗi SQL
     */
    public Doctor getDoctorById(String doctorId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT d.*, u.Email, u.PhoneNumber, u.FullName FROM Doctors d " +
                    "JOIN UserAccounts u ON d.UserID = u.UserID WHERE d.DoctorID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Doctor(
                    rs.getString("UserID"),
                    rs.getString("DoctorID"),
                    rs.getString("FullName"),
                    rs.getDate("DateOfBirth") != null ? rs.getDate("DateOfBirth").toLocalDate() : LocalDate.now(),
                    rs.getString("Address"),
                    Gender.fromDatabase(rs.getString("Gender")),
                    rs.getString("PhoneNumber"),
                    rs.getString("SpecialtyID") != null ? Specialization.fromId(rs.getString("SpecialtyID")) : Specialization.GENERAL,
                    rs.getString("Email"),
                    rs.getDate("CreatedAt") != null ? rs.getDate("CreatedAt").toLocalDate() : LocalDate.now()
                );
            }
            return null;
        }
    }

    /**
     * Lấy tổng số bệnh nhân trong hệ thống
     * @return Số lượng bệnh nhân
     * @throws SQLException nếu có lỗi SQL
     */
    public int getTotalPatientCount() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS total FROM Patients";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    /**
     * Lấy số lượng cuộc hẹn trong ngày hôm nay của bác sĩ
     * @param doctorId ID của bác sĩ
     * @return Số lượng cuộc hẹn
     * @throws SQLException nếu có lỗi SQL
     */
    public int getTodayAppointmentCount(String doctorId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS total FROM Appointments " +
                    "WHERE DoctorID = ? AND DATE(AppointmentDate) = CURDATE()";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    /**
     * Lấy số lượng bệnh nhân đang chờ khám
     * @param doctorId ID của bác sĩ
     * @return Số lượng bệnh nhân đang chờ
     * @throws SQLException nếu có lỗi SQL
     */
    public int getWaitingPatientCount(String doctorId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS total FROM Appointments " +
                    "WHERE DoctorID = ? AND DATE(AppointmentDate) = CURDATE() " +
                    "AND Status = 'Chờ xác nhận'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    /**
     * Lấy số lượng cuộc hẹn đã hoàn thành hôm nay
     * @param doctorId ID của bác sĩ
     * @return Số lượng cuộc hẹn đã hoàn thành
     * @throws SQLException nếu có lỗi SQL
     */
    public int getCompletedTodayCount(String doctorId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS total FROM Appointments " +
                    "WHERE DoctorID = ? AND DATE(AppointmentDate) = CURDATE() " +
                    "AND Status = 'Hoàn thành'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    /**
     * Lấy danh sách các cuộc hẹn sắp tới
     * @param doctorId ID của bác sĩ
     * @return Danh sách các cuộc hẹn dưới dạng Object[]
     * @throws SQLException nếu có lỗi SQL
     */
    public List<Object[]> getUpcomingAppointments(String doctorId) throws SQLException {
        List<Object[]> appointments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT a.AppointmentDate, a.PatientID, p.FullName, a.Notes " +
                    "FROM Appointments a " +
                    "JOIN Patients p ON a.PatientID = p.PatientID " +
                    "WHERE a.DoctorID = ? AND a.AppointmentDate >= NOW() " +
                    "ORDER BY a.AppointmentDate ASC LIMIT 10";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] appointment = {
                    rs.getTimestamp("AppointmentDate").toString(),
                    rs.getString("PatientID"),
                    rs.getString("FullName"),
                    rs.getString("Notes")
                };
                appointments.add(appointment);
            }
        }
        return appointments;
    }

    /**
     * Lấy danh sách bệnh nhân mới nhất
     * @param limit Số lượng bệnh nhân cần lấy
     * @return Danh sách bệnh nhân dưới dạng Object[]
     * @throws SQLException nếu có lỗi SQL
     */
    public List<Object[]> getRecentPatients(int limit) throws SQLException {
        List<Object[]> patients = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT p.PatientID, p.FullName, p.CreatedAt, p.PhoneNumber " +
                    "FROM Patients p " +
                    "ORDER BY p.CreatedAt DESC LIMIT ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] patient = {
                    rs.getString("PatientID"),
                    rs.getString("FullName"),
                    rs.getDate("CreatedAt").toString(),
                    rs.getString("PhoneNumber")
                };
                patients.add(patient);
            }
        }
        return patients;
    }

    /**
     * Lấy tất cả bệnh nhân từ database
     * @return Danh sách tất cả bệnh nhân
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT p.*, u.FullName, u.PhoneNumber FROM Patients p " +
                    "JOIN UserAccounts u ON p.UserID = u.UserID";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                try {
                    Patient patient = new Patient(
                            rs.getString("UserID"),
                            rs.getString("PatientID"),
                            rs.getString("FullName"),
                            rs.getDate("DateOfBirth") != null ? rs.getDate("DateOfBirth").toLocalDate() : null,
                            rs.getString("Address"),
                            Gender.fromDatabase(rs.getString("Gender")),
                            rs.getString("PhoneNumber"),
                            rs.getDate("CreatedAt") != null ? rs.getDate("CreatedAt").toLocalDate() : null
                    );
                    patients.add(patient);
                } catch (Exception e) {
                    System.err.println("Lỗi khi tạo đối tượng Patient: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

        /**
     * Đặt lịch hẹn mới
     * @param patientId ID bệnh nhân
     * @param appointmentDate Ngày hẹn
     * @param doctorId ID bác sĩ
     * @return true nếu đặt thành công
     */
    public boolean bookAppointment(String patientId, LocalDate appointmentDate, String doctorId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Kiểm tra ngày hẹn có hợp lệ không (phải sau ngày hiện tại)
            if (appointmentDate.isBefore(LocalDate.now()) || appointmentDate.isEqual(LocalDate.now())) {
                System.err.println("Lỗi: Ngày hẹn phải sau ngày hiện tại!");
                return false;
            }
            
            // Tạo ID lịch hẹn mới theo định dạng APP-xxx
            String appId = generateNewAppointmentID(conn);
            
            String query = "INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Status, Notes) " +
                    "VALUES (?, ?, ?, ?, 'Chờ xác nhận', 'Cuộc hẹn mới')";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, appId);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.setTimestamp(4, Timestamp.valueOf(appointmentDate.atStartOfDay()));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tạo ID mới cho lịch hẹn theo mẫu APP-001, APP-002, ...
     * @param conn Kết nối database
     * @return ID mới với định dạng APP-XXX
     * @throws SQLException nếu có lỗi
     */
    private String generateNewAppointmentID(Connection conn) throws SQLException {
        String query = "SELECT MAX(SUBSTRING(AppointmentID, 5)) AS MaxID FROM Appointments WHERE AppointmentID LIKE 'APP-%'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            int maxID = 0;
            if (rs.next()) {
                String maxIDStr = rs.getString("MaxID");
                if (maxIDStr != null && !maxIDStr.isEmpty()) {
                    try {
                        maxID = Integer.parseInt(maxIDStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi chuyển đổi ID lịch hẹn: " + e.getMessage());
                    }
                }
            }
            return String.format("APP-%03d", maxID + 1);
        }
    }
 
    /**
     * Xóa bệnh nhân
     * @param patientId ID bệnh nhân cần xóa
     * @return true nếu xóa thành công
     */
    public boolean deletePatient(String patientId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Lấy UserID của bệnh nhân
            String userIdQuery = "SELECT UserID FROM Patients WHERE PatientID = ?";
            PreparedStatement userIdStmt = conn.prepareStatement(userIdQuery);
            userIdStmt.setString(1, patientId);
            ResultSet rs = userIdStmt.executeQuery();
            String userId = null;
            if (rs.next()) {
                userId = rs.getString("UserID");
            }
            
            if (userId == null) {
                return false;
            }
            
            // Xóa các bản ghi liên quan
            // 1. Xóa từ MedicalRecords
            String deleteRecordsQuery = "DELETE FROM MedicalRecords WHERE PatientID = ?";
            PreparedStatement deleteRecordsStmt = conn.prepareStatement(deleteRecordsQuery);
            deleteRecordsStmt.setString(1, patientId);
            deleteRecordsStmt.executeUpdate();
            
            // 2. Xóa từ Appointments
            String deleteAppointmentsQuery = "DELETE FROM Appointments WHERE PatientID = ?";
            PreparedStatement deleteAppointmentsStmt = conn.prepareStatement(deleteAppointmentsQuery);
            deleteAppointmentsStmt.setString(1, patientId);
            deleteAppointmentsStmt.executeUpdate();
            
            // 3. Xóa từ Patients
            String deletePatientQuery = "DELETE FROM Patients WHERE PatientID = ?";
            PreparedStatement deletePatientStmt = conn.prepareStatement(deletePatientQuery);
            deletePatientStmt.setString(1, patientId);
            deletePatientStmt.executeUpdate();
            
            // 4. Xóa từ UserAccounts
            String deleteUserQuery = "DELETE FROM UserAccounts WHERE UserID = ?";
            PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery);
            deleteUserStmt.setString(1, userId);
            deleteUserStmt.executeUpdate();
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tạo tên đăng nhập từ tên đầy đủ
     * @param fullName Tên đầy đủ
     * @return Tên đăng nhập
     */
    private String generateUsername(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "user" + System.currentTimeMillis() % 10000;
        }
        
        // Chuyển về chữ thường, bỏ dấu
        String normalized = fullName.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("\\s+", "");
        
        // Thêm số ngẫu nhiên để tránh trùng
        return normalized + (int)(Math.random() * 1000);
    }

    /**
     * Lấy lịch làm việc của bác sĩ trong ngày
     * @param doctorId ID của bác sĩ
     * @return Danh sách các hoạt động trong ngày
     * @throws SQLException nếu có lỗi SQL
     */
    public List<Object[]> getDoctorTodaySchedule(String doctorId) throws SQLException {
        List<Object[]> schedules = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Lấy các cuộc hẹn trong ngày
            String query = "SELECT TIME(AppointmentDate) as Time, 'Khám bệnh' as Activity, " +
                    "Status, CONCAT('Bệnh nhân: ', p.FullName) as Notes " +
                    "FROM Appointments a " +
                    "JOIN Patients p ON a.PatientID = p.PatientID " +
                    "WHERE a.DoctorID = ? AND DATE(AppointmentDate) = CURDATE() " +
                    "ORDER BY AppointmentDate ASC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] schedule = {
                    rs.getString("Time"),
                    rs.getString("Activity"),
                    rs.getString("Status"),
                    rs.getString("Notes")
                };
                schedules.add(schedule);
            }
            
            // Nếu không có lịch, thêm thông tin trống
            if (schedules.isEmpty()) {
                schedules.add(new Object[]{"--:--", "Không có lịch làm việc", "--", "Ngày trống"});
            }
        }
        return schedules;
    }

    /**
     * Lấy các cuộc hẹn sắp tới của bác sĩ
     * @param doctorId ID của bác sĩ
     * @param limit Số lượng cuộc hẹn cần lấy
     * @return Danh sách các cuộc hẹn sắp tới
     * @throws SQLException nếu có lỗi SQL
     */
    public List<Object[]> getNextAppointments(String doctorId, int limit) throws SQLException {
        List<Object[]> appointments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT a.AppointmentDate, a.AppointmentID, p.FullName " +
                    "FROM Appointments a " +
                    "JOIN Patients p ON a.PatientID = p.PatientID " +
                    "WHERE a.DoctorID = ? " + 
                    "AND a.AppointmentDate > NOW() " +  // Chỉ lấy những cuộc hẹn trong tương lai
                    "AND a.Status = 'Chờ xác nhận' " +  // Chỉ lấy những cuộc hẹn có trạng thái "Chờ xác nhận"
                    "ORDER BY a.AppointmentDate ASC LIMIT ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, doctorId);
                stmt.setInt(2, limit);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    // Format lại ngày giờ cho dễ đọc
                    Timestamp timestamp = rs.getTimestamp("AppointmentDate");
                    String formattedDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp);
                    
                    Object[] appointment = {
                        formattedDate,
                        rs.getString("AppointmentID"),
                        rs.getString("FullName"),
                        "Hủy"  // Thêm text cho nút thao tác
                    };
                    appointments.add(appointment);
                }
                
                // Nếu không có cuộc hẹn, thêm thông báo
                if (appointments.isEmpty()) {
                    appointments.add(new Object[]{"Không có cuộc hẹn", "---", "---", ""});
                }
            }
        }
        return appointments;
    }

    /**
     * Lấy lịch làm việc theo tuần của bác sĩ
     * @param doctorId ID của bác sĩ
     * @return Danh sách lịch làm việc trong tuần
     * @throws SQLException nếu có lỗi SQL
     */
    public List<Object[]> getDoctorWeeklySchedule(String doctorId) throws SQLException {
        List<Object[]> scheduleList = new ArrayList<>();
        
        // Tạo kết nối đến database
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Kiểm tra xem bảng DoctorSchedule đã tồn tại chưa
            try {
                // Kiểm tra bằng cách thử truy vấn
                String checkQuery = "SELECT 1 FROM DoctorSchedule LIMIT 1";
                conn.createStatement().executeQuery(checkQuery);
                
                // Nếu không có lỗi, bảng đã tồn tại và ta có thể truy vấn
                String query = "SELECT ShiftType, DayOfWeek, Status FROM DoctorSchedule WHERE DoctorID = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, doctorId);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Object[] schedule = {
                        rs.getString("ShiftType"),
                        rs.getString("DayOfWeek"),
                        rs.getString("Status")
                    };
                    scheduleList.add(schedule);
                }
            } catch (SQLException e) {
                // Bảng có thể không tồn tại, trả về danh sách trống
                // Hoặc tạo bảng nếu cần thiết
                System.out.println("Bảng DoctorSchedule có thể chưa tồn tại: " + e.getMessage());
                
                // Tạo bảng DoctorSchedule nếu cần
                createDoctorScheduleTable(conn);
            }
        }
        
        return scheduleList;
    }

    /**
     * Tạo bảng DoctorSchedule trong cơ sở dữ liệu nếu chưa tồn tại
     * @param conn Connection đến database
     * @throws SQLException nếu có lỗi SQL
     */
    private void createDoctorScheduleTable(Connection conn) throws SQLException {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS DoctorSchedule (" +
            "DoctorID VARCHAR(50) NOT NULL, " +
            "DayOfWeek VARCHAR(20) NOT NULL, " +
            "ShiftType VARCHAR(20) NOT NULL, " +
            "Status VARCHAR(50) DEFAULT 'Không làm việc', " +
            "PRIMARY KEY (DoctorID, DayOfWeek, ShiftType), " +
            "FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) " +
            "ON DELETE CASCADE ON UPDATE CASCADE)";
        
        conn.createStatement().executeUpdate(createTableSQL);
        System.out.println("Đã tạo bảng DoctorSchedule");
    }

    /**
     * Lấy trạng thái ca làm việc của bác sĩ
     * @param doctorId ID của bác sĩ
     * @param dayOfWeek Ngày trong tuần
     * @param shift Ca làm việc
     * @return Trạng thái ca làm việc
     * @throws SQLException nếu có lỗi SQL
     */
    public String getDoctorShiftStatus(String doctorId, String dayOfWeek, String shift) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Kiểm tra xem bảng DoctorSchedule đã tồn tại chưa
            try {
                String query = "SELECT Status FROM DoctorSchedule " +
                        "WHERE DoctorID = ? AND DayOfWeek = ? AND ShiftType = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, doctorId);
                stmt.setString(2, convertDayOfWeek(dayOfWeek)); // Chuyển đổi từ enum sang tên tiếng Việt
                stmt.setString(3, shift);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getString("Status");
                }
            } catch (SQLException e) {
                // Bảng có thể không tồn tại
                System.out.println("Bảng DoctorSchedule có thể chưa tồn tại: " + e.getMessage());
                // Tạo bảng nếu cần
                createDoctorScheduleTable(conn);
            }
            return "Không làm việc";
        }
    }

    /**
     * Cập nhật trạng thái lịch làm việc của bác sĩ
     * @param doctorId ID bác sĩ
     * @param dayOfWeek Ngày trong tuần
     * @param shift Ca làm việc
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công
     * @throws SQLException nếu có lỗi SQL
     */
    public boolean updateDoctorScheduleStatus(String doctorId, String dayOfWeek, String shift, String status) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Kiểm tra xem bảng DoctorSchedule đã tồn tại chưa
            try {
                // Thử INSERT ON DUPLICATE KEY UPDATE
                String query = "INSERT INTO DoctorSchedule (DoctorID, DayOfWeek, ShiftType, Status) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE Status = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, doctorId);
                stmt.setString(2, dayOfWeek);
                stmt.setString(3, shift);
                stmt.setString(4, status);
                stmt.setString(5, status);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                // Bảng có thể không tồn tại
                System.out.println("Bảng DoctorSchedule có thể chưa tồn tại: " + e.getMessage());
                // Tạo bảng
                createDoctorScheduleTable(conn);
                
                // Thử lại sau khi tạo bảng
                String query = "INSERT INTO DoctorSchedule (DoctorID, DayOfWeek, ShiftType, Status) " +
                        "VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, doctorId);
                stmt.setString(2, dayOfWeek);
                stmt.setString(3, shift);
                stmt.setString(4, status);
                
                return stmt.executeUpdate() > 0;
            }
        }
    }

    /**
     * Chuyển đổi từ DayOfWeek enum sang tên tiếng Việt
     * @param dayOfWeek Tên enum của ngày trong tuần
     * @return Tên tiếng Việt
     */
    private String convertDayOfWeek(String dayOfWeek) {
        switch (dayOfWeek) {
            case "MONDAY": return "Thứ Hai";
            case "TUESDAY": return "Thứ Ba";
            case "WEDNESDAY": return "Thứ Tư";
            case "THURSDAY": return "Thứ Năm";
            case "FRIDAY": return "Thứ Sáu";
            case "SATURDAY": return "Thứ Bảy";
            case "SUNDAY": return "Chủ Nhật";
            default: return dayOfWeek;
        }
    }

        /**
     * Lấy trạng thái ca làm việc của bác sĩ
     * @param doctorId ID của bác sĩ
     * @param dayOfWeek Ngày trong tuần (Thứ Hai, Thứ Ba...)
     * @param shift Ca làm việc (Sáng, Chiều, Tối)
     * @return Trạng thái ca làm việc
     * @throws SQLException nếu có lỗi SQL
     */
    public String getShiftStatus(String doctorId, String dayOfWeek, String shift) throws SQLException {
        // Debug để xem có vấn đề gì với SQL query
        System.out.println("DEBUG SQL: SELECT Status FROM DoctorSchedule WHERE DoctorID = '" + 
        doctorId + "' AND DayOfWeek = '" + dayOfWeek + "' AND ShiftTime = '" + shift + "'");
        
        String sql = "SELECT Status FROM DoctorSchedule WHERE DoctorID = ? AND DayOfWeek = ? AND ShiftType = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            stmt.setString(2, dayOfWeek);
            stmt.setString(3, shift);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Status");
                }
                return "Không làm việc"; // Mặc định nếu không tìm thấy
            }
        }
    }
    
    /**
     * Cập nhật trạng thái ca làm việc của bác sĩ
     * @param doctorId ID của bác sĩ
     * @param dayOfWeek Ngày trong tuần (Thứ Hai, Thứ Ba...)
     * @param shift Ca làm việc (Sáng, Chiều, Tối)
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công
     * @throws SQLException nếu có lỗi SQL
     */
    public boolean updateShiftStatus(String doctorId, String dayOfWeek, String shift, String status) throws SQLException {
        String sql = "UPDATE DoctorSchedule SET Status = ? WHERE DoctorID = ? AND DayOfWeek = ? AND ShiftType = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, doctorId);
            stmt.setString(3, dayOfWeek);
            stmt.setString(4, shift);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Thêm vào DoctorRepository
    public void checkDoctorScheduleTable() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Kiểm tra xem bảng có tồn tại không
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "DoctorSchedule", null);
            if (!tables.next()) {
                System.out.println("CẢNH BÁO: Bảng DoctorSchedule không tồn tại!");
                // Tạo bảng nếu không tồn tại
                createDoctorScheduleTable(conn);
                return;
            }
            
            // Kiểm tra cấu trúc bảng
            ResultSet columns = meta.getColumns(null, null, "DoctorSchedule", null);
            System.out.println("Cấu trúc bảng DoctorSchedule:");
            boolean hasScheduleIDColumn = false;
            
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                System.out.println(columnName + " - " + typeName);
                
                if ("ScheduleID".equalsIgnoreCase(columnName)) {
                    hasScheduleIDColumn = true;
                }
            }
            
            if (hasScheduleIDColumn) {
                System.out.println("CẢNH BÁO: Bảng DoctorSchedule có cột ScheduleID không được dùng trong code hiện tại!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra bảng: " + e.getMessage());
        }
    }

    public List<Object[]> getPatientsForExamination(String doctorId) {
        List<Object[]> patientRecords = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Sửa câu truy vấn để hiển thị cả cuộc hẹn cũ quá hạn
            String sql = "SELECT p.*, u.Email, mr.RecordID, mr.Diagnosis, mr.TreatmentPlan, " +
                         "a.AppointmentDate, " +
                         "CASE WHEN DATE(a.AppointmentDate) = CURDATE() THEN 'Hôm nay' " +
                         "ELSE CONCAT('Quá hạn: ', DATE_FORMAT(a.AppointmentDate, '%d/%m/%Y')) END as AppointmentStatus " +
                         "FROM Patients p " +
                         "JOIN Appointments a ON p.PatientID = a.PatientID " +
                         "JOIN UserAccounts u ON p.UserID = u.UserID " +
                         "LEFT JOIN MedicalRecords mr ON p.PatientID = mr.PatientID " +
                         "AND mr.RecordDate = (SELECT MAX(RecordDate) FROM MedicalRecords " +
                         "                     WHERE PatientID = p.PatientID) " +
                         "WHERE a.DoctorID = ? AND a.Status = 'Chờ xác nhận' " +
                         "ORDER BY a.AppointmentDate";
                         
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, doctorId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Tạo đối tượng Patient từ kết quả truy vấn
                        Patient patient = new Patient();
                        patient.setPatientID(rs.getString("PatientID"));
                        patient.setFullName(rs.getString("FullName"));
                        
                        // Kiểm tra null cho ngày sinh
                        Date birthDate = rs.getDate("DateOfBirth");
                        if (birthDate != null) {
                            patient.setDateOfBirth(birthDate.toLocalDate());
                        }
                        
                        patient.setAddress(rs.getString("Address"));
                        patient.setPhoneNumber(rs.getString("PhoneNumber"));
                        
                        // Sửa lỗi chuyển đổi Gender
                        String genderStr = rs.getString("Gender");
                        if (genderStr != null) {
                            patient.setGender(Gender.fromDatabase(genderStr));
                        }
                        
                        // Tạo đối tượng MedicalRecord từ kết quả truy vấn
                        MedicalRecord medicalRecord = null;
                        String recordId = rs.getString("RecordID");
                        if (recordId != null) {
                            medicalRecord = new MedicalRecord();
                            medicalRecord.setRecordId(recordId);
                            String diagnosis = rs.getString("Diagnosis");
                            if (diagnosis != null) {
                                medicalRecord.setDiagnosis(diagnosis);
                            }
                            String treatmentPlan = rs.getString("TreatmentPlan");
                            if (treatmentPlan != null) {
                                medicalRecord.setTreatmentPlan(treatmentPlan);
                            } else {
                                medicalRecord.setTreatmentPlan("");
                            }
                        }
                        
                        // Email và trạng thái lịch hẹn
                        String email = rs.getString("Email");
                        String appointmentStatus = rs.getString("AppointmentStatus");
                        
                        // Tạo mảng chứa thông tin bệnh nhân, hồ sơ y tế và trạng thái lịch hẹn
                        Object[] record = {patient, medicalRecord, email, appointmentStatus};
                        patientRecords.add(record);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return patientRecords;
    }

    public List<Object[]> searchPatientsForExamination(String doctorId, String keyword) {
        List<Object[]> patientRecords = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            System.out.println("Tìm kiếm với từ khóa: " + keyword);
            
            String sql = "SELECT p.*, u.Email, mr.RecordID, mr.Diagnosis, mr.TreatmentPlan, " +
                         "a.AppointmentDate, " +
                         "CASE WHEN DATE(a.AppointmentDate) = CURDATE() THEN 'Chờ khám' " +
                         "ELSE CONCAT('Quá hạn: ', DATE_FORMAT(a.AppointmentDate, '%d/%m/%Y')) END as AppointmentStatus " +
                         "FROM Patients p " +
                         "JOIN Appointments a ON p.PatientID = a.PatientID " +
                         "JOIN UserAccounts u ON p.UserID = u.UserID " +
                         "LEFT JOIN MedicalRecords mr ON p.PatientID = mr.PatientID " +
                         "AND mr.RecordDate = (SELECT MAX(RecordDate) FROM MedicalRecords " +
                         "                     WHERE PatientID = p.PatientID) " +
                         "WHERE a.DoctorID = ? AND a.Status = 'Chờ xác nhận' " +
                         "AND (p.PatientID LIKE ? OR p.FullName LIKE ? OR p.PhoneNumber LIKE ?) " +
                         "ORDER BY a.AppointmentDate";
                         
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, doctorId);
                String likePattern = "%" + keyword + "%";
                stmt.setString(2, likePattern);
                stmt.setString(3, likePattern);
                stmt.setString(4, likePattern);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Xử lý kết quả tương tự như phương thức getPatientsForExamination
                        // ...
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return patientRecords;
    }
    
    
    /**
     * Cập nhật trạng thái hoàn thành khám cho bệnh nhân
     */
    public boolean completePatientExamination(String patientId, String doctorId) {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Cập nhật trạng thái cuộc hẹn
            String updateAppointmentSql = "UPDATE Appointments SET Status = 'Đã khám' " +
                                         "WHERE PatientID = ? AND DoctorID = ? AND AppointmentDate = CURDATE()";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateAppointmentSql)) {
                stmt.setString(1, patientId);
                stmt.setString(2, doctorId);
                stmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Sinh mã đơn thuốc mới
     * @return Mã đơn thuốc mới
     */
    public String generateNewPrescriptionId() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT MAX(PrescriptionID) AS LastID FROM Prescriptions";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    String lastId = rs.getString("LastID");
                    
                    if (lastId != null) {
                        // Format: PRE-XXX
                        try {
                            int lastNumber = Integer.parseInt(lastId.substring(4));
                            int newNumber = lastNumber + 1;
                            return String.format("PRE-%03d", newNumber);
                        } catch (NumberFormatException | IndexOutOfBoundsException e) {
                            // Nếu không thể parse được số, trả về giá trị mặc định
                            System.out.println("Không thể parse được ID đơn thuốc cuối: " + lastId);
                            return "PRE-001";
                        }
                    }
                }
            }
            
            // Nếu không tìm thấy ID nào hoặc có lỗi xảy ra
            return "PRE-001";
        } catch (SQLException e) {
            e.printStackTrace();
            return "PRE-001";
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Lưu đơn thuốc mới vào database
     */
    public boolean savePrescription(String doctorId, Map<String, Object> prescriptionData, 
                                  List<Map<String, Object>> medicineList) {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            
            // Lấy ID đơn thuốc từ dữ liệu
            String prescriptionId = (String) prescriptionData.get("prescriptionId");
            boolean isExistingPrescription = false;
            
            // Kiểm tra xem đây có phải là đơn thuốc đã tồn tại hay không
            if (prescriptionId != null && !prescriptionId.trim().isEmpty() && 
                !prescriptionId.equals("[Tự động tạo]")) {
                
                // Kiểm tra xem ID này đã tồn tại trong DB chưa
                if (isPrescriptionIdExist(prescriptionId)) {
                    // Đây là trường hợp cập nhật đơn thuốc đã tồn tại
                    isExistingPrescription = true;
                }
            }
            
            // Nếu là đơn thuốc mới hoặc không có ID, tạo ID mới
            if (!isExistingPrescription) {
                prescriptionId = generateNewPrescriptionId();
            }
            
            String patientId = (String) prescriptionData.get("patientId");
            String diagnosis = (String) prescriptionData.get("diagnosis");
            String treatmentPlan = (String) prescriptionData.get("treatmentPlan"); // Lấy phác đồ điều trị
            String notes = (String) prescriptionData.get("notes");
            
            if (isExistingPrescription) {
                // Nếu đơn thuốc đã tồn tại, cập nhật thay vì thêm mới
                String updatePrescriptionSql = "UPDATE Prescriptions SET PatientID = ?, DoctorID = ?, " +
                                             "PrescriptionDate = ? WHERE PrescriptionID = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(updatePrescriptionSql)) {
                    stmt.setString(1, patientId);
                    stmt.setString(2, doctorId);
                    stmt.setString(3, (String) prescriptionData.get("date"));
                    stmt.setString(4, prescriptionId);
                    stmt.executeUpdate();
                }
            } else {
                // Thêm mới đơn thuốc
                String insertPrescriptionSql = "INSERT INTO Prescriptions (PrescriptionID, PatientID, DoctorID, " +
                                              "PrescriptionDate) VALUES (?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(insertPrescriptionSql)) {
                    stmt.setString(1, prescriptionId);
                    stmt.setString(2, patientId);
                    stmt.setString(3, doctorId);
                    stmt.setString(4, (String) prescriptionData.get("date"));
                    stmt.executeUpdate();
                }
            }
            
            // 2. Kiểm tra xem đã có MedicalRecord cho ngày hôm nay chưa
            String checkRecordSql = "SELECT RecordID FROM MedicalRecords WHERE PatientID = ? " +
                                   "AND DoctorID = ? AND DATE(RecordDate) = DATE(?) AND IsHistory = 0";
            
            String recordId = null;
            boolean recordExists = false;
            
            try (PreparedStatement stmt = conn.prepareStatement(checkRecordSql)) {
                stmt.setString(1, patientId);
                stmt.setString(2, doctorId);
                stmt.setString(3, (String) prescriptionData.get("date"));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        recordId = rs.getString("RecordID");
                        recordExists = true;
                    }
                }
            }
            
            // Nếu chưa có record, tạo mới
            if (!recordExists) {
                // Kiểm tra và đặt các hồ sơ y tế cũ thành tiền sử
                updateMedicalHistory(conn, patientId, diagnosis);
                
                // Tạo mới MedicalRecord
                String insertMedicalRecordSql = "INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, " +
                                               "Diagnosis, TreatmentPlan, RecordDate, IsHistory) VALUES (?, ?, ?, ?, ?, ?, ?)";
                
                recordId = generateMedicalRecordId();
                
                try (PreparedStatement stmt = conn.prepareStatement(insertMedicalRecordSql)) {
                    stmt.setString(1, recordId);
                    stmt.setString(2, patientId);
                    stmt.setString(3, doctorId);
                    stmt.setString(4, diagnosis);
                    stmt.setString(5, treatmentPlan); // Sử dụng phác đồ điều trị đã nhập
                    stmt.setString(6, (String) prescriptionData.get("date"));
                    stmt.setBoolean(7, false); // Không phải tiền sử
                    stmt.executeUpdate();
                }
            }
            // Nếu đã có record, chỉ cập nhật thông tin
            else {
                String updateRecordSql = "UPDATE MedicalRecords SET Diagnosis = ?, TreatmentPlan = ? " +
                                       "WHERE RecordID = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(updateRecordSql)) {
                    stmt.setString(1, diagnosis);
                    stmt.setString(2, treatmentPlan); // Sử dụng phác đồ điều trị đã nhập
                    stmt.setString(3, recordId);
                    stmt.executeUpdate();
                }
            }
            
            // 3. Xóa tất cả chi tiết đơn thuốc cũ
            String deleteDetailsSql = "DELETE FROM PrescriptionDetails WHERE PrescriptionID = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(deleteDetailsSql)) {
                stmt.setString(1, prescriptionId);
                stmt.executeUpdate();
            }
            
            // 4. Thêm chi tiết đơn thuốc mới
            String insertDetailsSql = "INSERT INTO PrescriptionDetails (PrescriptionID, MedicationID, " +
                                     "Dosage, Instructions) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(insertDetailsSql)) {
                for (Map<String, Object> medicine : medicineList) {
                    // Lấy hoặc tạo ID thuốc từ tên
                    String medicineName = (String) medicine.get("name");
                    String medicationId = findOrCreateMedication(conn, medicineName);
                    
                    stmt.setString(1, prescriptionId);
                    stmt.setString(2, medicationId);
                    stmt.setString(3, (String) medicine.get("dosage"));
                    stmt.setString(4, (String) medicine.get("instruction"));
                    stmt.executeUpdate();
                }
            }
            
            // 5. Cập nhật trạng thái cuộc hẹn thành "Hoàn thành"
            String updateAppointmentSql = "UPDATE Appointments SET Status = 'Hoàn thành' " +
                                         "WHERE PatientID = ? AND DoctorID = ? AND Status = 'Chờ xác nhận'";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateAppointmentSql)) {
                stmt.setString(1, patientId);
                stmt.setString(2, doctorId);
                stmt.executeUpdate();
            }
            
            conn.commit(); // Xác nhận transaction
            return true;
        } catch (SQLException e) {
            // Rollback nếu có lỗi
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Tìm hoặc tạo ID thuốc từ tên thuốc
     * @param conn Connection đến database
     * @param medicineName Tên thuốc
     * @return ID thuốc
     */
    private String findOrCreateMedication(Connection conn, String medicineName) throws SQLException {
        String sql = "SELECT MedicationID FROM Medications WHERE MedicineName = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicineName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MedicationID");
                }
            }
        }
        
        // Nếu không tìm thấy, tạo mới
        String medicationId = generateMedicationId(conn);
        
        sql = "INSERT INTO Medications (MedicationID, MedicineName) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicationId);
            stmt.setString(2, medicineName);
            stmt.executeUpdate();
        }
        
        return medicationId;
    }
    
    /**
     * Sinh ID mới cho thuốc
     * @param conn Connection đến database
     * @return ID thuốc mới
     */
    private String generateMedicationId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(MedicationID) AS LastID FROM Medications";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                String lastId = rs.getString("LastID");
                
                if (lastId != null) {
                    // Format: MED-XXXXX
                    int number = Integer.parseInt(lastId.substring(4)) + 1;
                    return String.format("MED-%03d", number);
                }
            }
        }
        
        // Default first ID
        return "MED-001";
    }
    
    /**
     * Cập nhật các hồ sơ y tế cũ thành tiền sử bệnh khi có chẩn đoán mới
     * @param conn Connection đến database
     * @param patientId ID của bệnh nhân
     * @param newDiagnosis Chẩn đoán mới
     */
    private void updateMedicalHistory(Connection conn, String patientId, String newDiagnosis) throws SQLException {
        String latestDiagnosis = null;
        
        // Lấy chẩn đoán gần nhất
        String selectSql = "SELECT Diagnosis FROM MedicalRecords " +
                           "WHERE PatientID = ? AND IsHistory = FALSE " +
                           "ORDER BY RecordDate DESC LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, patientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    latestDiagnosis = rs.getString("Diagnosis");
                }
            }
        }
        
        // Nếu có chẩn đoán cũ và chẩn đoán mới khác với cũ
        if (latestDiagnosis != null && !latestDiagnosis.equals(newDiagnosis)) {
            // Cập nhật tất cả hồ sơ cũ thành tiền sử
            String updateSql = "UPDATE MedicalRecords SET IsHistory = TRUE " +
                               "WHERE PatientID = ? AND IsHistory = FALSE";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, patientId);
                stmt.executeUpdate();
            }
        }
    }
    
    /**
     * Sinh ID mới cho hồ sơ y tế
     * @return ID hồ sơ y tế mới
     */
    private String generateMedicalRecordId() {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Lấy ID hồ sơ y tế cuối cùng
            String sql = "SELECT MAX(RecordID) as LastID FROM MedicalRecords";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    String lastId = rs.getString("LastID");
                    
                    if (lastId != null) {
                        // Format: MR-XXXXX
                        int number = Integer.parseInt(lastId.substring(3)) + 1;
                        return String.format("MR-%03d", number);
                    }
                }
                
                // Nếu chưa có hồ sơ y tế nào
                return "MR-001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Tạm thời trả về ID tạm thời nếu có lỗi
            return "MR-" + System.currentTimeMillis();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Lấy hoặc tạo mã thuốc từ tên thuốc
     * @param medicineName Tên thuốc
     * @return Mã thuốc
     */
    private String getMedicationId(String medicineName) throws SQLException {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Tìm thuốc theo tên
            String selectSql = "SELECT MedicationID FROM Medications WHERE MedicineName = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setString(1, medicineName);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("MedicationID");
                    }
                }
            }
            
            // Nếu không tìm thấy, tạo thuốc mới
            String medicationId = generateMedicationId();
            
            String insertSql = "INSERT INTO Medications (MedicationID, MedicineName) VALUES (?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, medicationId);
                stmt.setString(2, medicineName);
                stmt.executeUpdate();
            }
            
            return medicationId;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    /**
     * Sinh ID mới cho thuốc
     * @return ID thuốc mới
     */
    private String generateMedicationId() throws SQLException {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Lấy ID thuốc cuối cùng
            String sql = "SELECT MAX(MedicationID) as LastID FROM Medications";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    String lastId = rs.getString("LastID");
                    
                    if (lastId != null) {
                        // Format: MED-XXXXX
                        int number = Integer.parseInt(lastId.substring(4)) + 1;
                        return String.format("MED-%03d", number);
                    }
                }
                
                // Nếu chưa có thuốc nào
                return "MED-001";
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Lấy chẩn đoán gần nhất của bệnh nhân từ bảng MedicalRecords
     * @param patientId ID của bệnh nhân
     * @return Chẩn đoán gần nhất
     */
    public String getLatestDiagnosis(String patientId) {
        Connection conn = null;
        String diagnosis = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT Diagnosis FROM MedicalRecords " +
                         "WHERE PatientID = ? AND IsHistory = FALSE " +
                         "ORDER BY RecordDate DESC LIMIT 1";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        diagnosis = rs.getString("Diagnosis");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return diagnosis;
    }

        /**
     * Lấy tất cả thuốc từ cơ sở dữ liệu
     * @return Danh sách thuốc
     */
    public List<Map<String, Object>> getAllMedications() {
        List<Map<String, Object>> medications = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM Medications ORDER BY MedicineName";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Map<String, Object> med = new HashMap<>();
                    med.put("medicationId", rs.getString("MedicationID"));
                    med.put("medicineName", rs.getString("MedicineName"));
                    med.put("description", rs.getString("Description"));
                    med.put("manufacturer", rs.getString("Manufacturer"));
                    med.put("dosageForm", rs.getString("DosageForm"));
                    med.put("sideEffects", rs.getString("SideEffects"));
                    
                    medications.add(med);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return medications;
    }
    
    /**
     * Tìm kiếm thuốc theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách thuốc phù hợp
     */
    public List<Map<String, Object>> searchMedications(String keyword) {
        List<Map<String, Object>> medications = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM Medications WHERE MedicineName LIKE ? OR Description LIKE ? ORDER BY MedicineName";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String searchPattern = "%" + keyword + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> med = new HashMap<>();
                        med.put("medicationId", rs.getString("MedicationID"));
                        med.put("medicineName", rs.getString("MedicineName"));
                        med.put("description", rs.getString("Description"));
                        med.put("manufacturer", rs.getString("Manufacturer"));
                        med.put("dosageForm", rs.getString("DosageForm"));
                        med.put("sideEffects", rs.getString("SideEffects"));
                        
                        medications.add(med);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return medications;
    }

    /**
     * Kiểm tra xem ID đơn thuốc đã tồn tại trong cơ sở dữ liệu chưa
     * @param prescriptionId ID đơn thuốc cần kiểm tra
     * @return true nếu ID đã tồn tại, false nếu chưa
     */
    public boolean isPrescriptionIdExist(String prescriptionId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM Prescriptions WHERE PrescriptionID = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, prescriptionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Hủy cuộc hẹn theo ID
     * @param appointmentId ID cuộc hẹn cần hủy
     * @return true nếu thành công, false nếu thất bại
     * @throws SQLException khi có lỗi truy vấn SQL
     */
    public boolean cancelAppointment(String appointmentId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE Appointments SET Status = 'Hủy' WHERE AppointmentID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, appointmentId);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }

}