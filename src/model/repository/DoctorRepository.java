package model.repository;

import model.entity.Doctor;
import model.entity.Patient;
import model.enums.Gender;
import model.enums.Specialization;
import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
     * Thêm bệnh nhân mới
     * @param patient Thông tin bệnh nhân
     * @param medicalHistory Lịch sử bệnh
     * @return true nếu thêm thành công
     */
    public boolean addPatient(Patient patient, String medicalHistory) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Tạo ID bệnh nhân mới
            String patientID = "PAT-" + System.currentTimeMillis() % 10000;
            patient.setPatientID(patientID);
            
            // Thêm vào bảng UserAccounts
            String userQuery = "INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, PasswordHash) " +
                    "VALUES (?, ?, ?, 'Bệnh nhân', ?, ?, SHA2(?, 256))";
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            userStmt.setString(1, patient.getUserID());
            userStmt.setString(2, generateUsername(patient.getFullName()));
            userStmt.setString(3, patient.getFullName());
            userStmt.setString(4, patient.getUserID() + "@example.com"); // Giả định email
            userStmt.setString(5, patient.getPhoneNumber());
            userStmt.setString(6, "password123"); // Mật khẩu mặc định
            userStmt.executeUpdate();

            // Thêm vào bảng Patients
            String patientQuery = "INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, Address, PhoneNumber, CreatedAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement patientStmt = conn.prepareStatement(patientQuery);
            patientStmt.setString(1, patientID);
            patientStmt.setString(2, patient.getUserID());
            patientStmt.setString(3, patient.getFullName());
            patientStmt.setDate(4, Date.valueOf(patient.getDateOfBirth()));
            patientStmt.setString(5, patient.getGender().toString());
            patientStmt.setString(6, patient.getAddress());
            patientStmt.setString(7, patient.getPhoneNumber());
            patientStmt.setDate(8, Date.valueOf(LocalDate.now()));
            patientStmt.executeUpdate();

            // Thêm vào bảng MedicalRecords (nếu có medicalHistory)
            if (medicalHistory != null && !medicalHistory.isEmpty()) {
                String recordQuery = "INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, TreatmentPlan, RecordDate) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement recordStmt = conn.prepareStatement(recordQuery);
                recordStmt.setString(1, "REC-" + System.currentTimeMillis() % 10000);
                recordStmt.setString(2, patientID);
                recordStmt.setString(3, "DOC-001"); // ID bác sĩ mặc định
                recordStmt.setString(4, medicalHistory);
                recordStmt.setString(5, "Chưa có kế hoạch điều trị");
                recordStmt.setDate(6, Date.valueOf(LocalDate.now()));
                recordStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
            String appId = "APP-" + System.currentTimeMillis() % 10000;
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
            String query = "SELECT DATE_FORMAT(AppointmentDate, '%d/%m/%Y %H:%i') as Time, " +
                    "AppointmentID, 'Chi tiết' as Action " +
                    "FROM Appointments " +
                    "WHERE DoctorID = ? AND AppointmentDate > NOW() " +
                    "ORDER BY AppointmentDate ASC LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] appointment = {
                    rs.getString("Time"),
                    rs.getString("AppointmentID"),
                    rs.getString("Action")
                };
                appointments.add(appointment);
            }
            
            // Nếu không có cuộc hẹn, thêm thông tin trống
            if (appointments.isEmpty()) {
                appointments.add(new Object[]{"--/--/---- --:--", "Không có cuộc hẹn", "--"});
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
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            // Kiểm tra xem bảng có tồn tại không
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "DoctorSchedule", null);
            if (!tables.next()) {
                System.out.println("CẢNH BÁO: Bảng DoctorSchedule không tồn tại!");
                return;
            }
            
            // Kiểm tra cấu trúc bảng
            ResultSet columns = meta.getColumns(null, null, "DoctorSchedule", null);
            System.out.println("Cấu trúc bảng DoctorSchedule:");
            while (columns.next()) {
                System.out.println(columns.getString("COLUMN_NAME") + " - " + columns.getString("TYPE_NAME"));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra bảng: " + e.getMessage());
        }
    }
}