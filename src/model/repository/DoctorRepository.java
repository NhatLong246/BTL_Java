package model.repository;

import model.entity.Appointment;
import model.entity.Doctor;
import model.entity.MedicalRecord;
import model.entity.Patient;
import model.entity.Prescription;
import model.entity.PrescriptionDetail;
import model.entity.VitalSign;
import model.enums.AppointmentStatus;
import model.enums.Gender;
import model.enums.Specialization;
import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            String query = "SELECT d.*, u.email, u.phoneNumber, u.fullName FROM Doctors d " +
                    "JOIN UserAccounts u ON d.userID = u.userID WHERE d.doctorID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Doctor(
                        rs.getString("userID"),
                        rs.getString("doctorID"),
                        rs.getString("fullName"),
                        rs.getDate("dateOfBirth") != null ? rs.getDate("dateOfBirth").toLocalDate() : LocalDate.now(),
                        rs.getString("address"),
                        Gender.fromDatabase(rs.getString("gender")),
                        rs.getString("phoneNumber"),
                        rs.getString("specialtyID") != null ? Specialization.fromId(rs.getString("specialtyID")) : Specialization.GENERAL,
                        rs.getString("email"),
                        rs.getDate("createdAt") != null ? rs.getDate("createdAt").toLocalDate() : LocalDate.now()
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
                    "WHERE doctorID = ? AND DATE(appointmentDate) = CURDATE()";
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
                    "WHERE doctorID = ? AND DATE(appointmentDate) = CURDATE() " +
                    "AND status = 'PENDING'";
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
                    "WHERE doctorID = ? AND DATE(appointmentDate) = CURDATE() " +
                    "AND status = 'COMPLETED'";
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
            String query = "SELECT a.appointmentDate, a.patientID, p.fullName, a.notes " +
                    "FROM Appointments a " +
                    "JOIN Patients p ON a.patientID = p.patientID " +
                    "WHERE a.doctorID = ? AND a.appointmentDate >= NOW() " +
                    "ORDER BY a.appointmentDate ASC LIMIT 10";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] appointment = {
                        rs.getTimestamp("appointmentDate").toString(),
                        rs.getString("patientID"),
                        rs.getString("fullName"),
                        rs.getString("notes")
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
            String query = "SELECT p.patientID, p.fullName, p.createdAt, p.phoneNumber " +
                    "FROM Patients p " +
                    "ORDER BY p.createdAt DESC LIMIT ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] patient = {
                        rs.getString("patientID"),
                        rs.getString("fullName"),
                        rs.getDate("createdAt").toString(),
                        rs.getString("phoneNumber")
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
            String query = "SELECT p.*, u.fullName, u.phoneNumber FROM Patients p " +
                    "JOIN UserAccounts u ON p.userID = u.userID";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                try {
                    Patient patient = new Patient(
                            rs.getString("userID"),
                            rs.getString("patientID"),
                            rs.getString("fullName"),
                            rs.getDate("dateOfBirth") != null ? rs.getDate("dateOfBirth").toLocalDate() : null,
                            rs.getString("address"),
                            Gender.fromDatabase(rs.getString("gender")),
                            rs.getString("phoneNumber"),
                            rs.getDate("createdAt") != null ? rs.getDate("createdAt").toLocalDate() : null
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
            if (appointmentDate.isBefore(LocalDate.now())) {
                System.err.println("Lỗi: Ngày hẹn phải sau ngày hiện tại!");
                return false;
            }

            // Tạo đối tượng Appointment để tự động sinh appointmentID
            Appointment appointment = new Appointment(conn, patientId, doctorId, appointmentDate.atStartOfDay());

            String query = "INSERT INTO Appointments (appointmentID, patientID, doctorID, appointmentDate, status, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, appointment.getAppointmentId());
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.setTimestamp(4, Timestamp.valueOf(appointmentDate.atStartOfDay()));
            stmt.setString(5, AppointmentStatus.PENDING.toString()); // Trạng thái mặc định
            stmt.setString(6, "Cuộc hẹn mới");
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
        String query = "SELECT MAX(SUBSTRING(appointmentID, 5)) AS maxID FROM Appointments WHERE appointmentID LIKE 'APP-%'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            int maxID = 0;
            if (rs.next()) {
                String maxIDStr = rs.getString("maxID");
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

            // Lấy userID của bệnh nhân
            String userIdQuery = "SELECT userID FROM Patients WHERE patientID = ?";
            PreparedStatement userIdStmt = conn.prepareStatement(userIdQuery);
            userIdStmt.setString(1, patientId);
            ResultSet rs = userIdStmt.executeQuery();
            String userId = null;
            if (rs.next()) {
                userId = rs.getString("userID");
            }

            if (userId == null) {
                return false;
            }

            // Xóa các bản ghi liên quan
            // 1. Xóa từ MedicalRecords
            String deleteRecordsQuery = "DELETE FROM MedicalRecords WHERE patientID = ?";
            PreparedStatement deleteRecordsStmt = conn.prepareStatement(deleteRecordsQuery);
            deleteRecordsStmt.setString(1, patientId);
            deleteRecordsStmt.executeUpdate();

            // 2. Xóa từ Appointments
            String deleteAppointmentsQuery = "DELETE FROM Appointments WHERE patientID = ?";
            PreparedStatement deleteAppointmentsStmt = conn.prepareStatement(deleteAppointmentsQuery);
            deleteAppointmentsStmt.setString(1, patientId);
            deleteAppointmentsStmt.executeUpdate();

            // 3. Xóa từ Patients
            String deletePatientQuery = "DELETE FROM Patients WHERE patientID = ?";
            PreparedStatement deletePatientStmt = conn.prepareStatement(deletePatientQuery);
            deletePatientStmt.setString(1, patientId);
            deletePatientStmt.executeUpdate();

            // 4. Xóa từ UserAccounts
            String deleteUserQuery = "DELETE FROM UserAccounts WHERE userID = ?";
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
            String query = "SELECT TIME(appointmentDate) as time, 'Khám bệnh' as activity, " +
                    "status, CONCAT('Bệnh nhân: ', p.fullName) as notes " +
                    "FROM Appointments a " +
                    "JOIN Patients p ON a.patientID = p.patientID " +
                    "WHERE a.doctorID = ? AND DATE(appointmentDate) = CURDATE() " +
                    "ORDER BY appointmentDate ASC";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] schedule = {
                        rs.getString("time"),
                        rs.getString("activity"),
                        rs.getString("status"),
                        rs.getString("notes")
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
            String query = "SELECT a.appointmentDate, a.appointmentID, p.fullName " +
                    "FROM Appointments a " +
                    "JOIN Patients p ON a.patientID = p.patientID " +
                    "WHERE a.doctorID = ? " +
                    "AND a.appointmentDate > NOW() " +  // Chỉ lấy những cuộc hẹn trong tương lai
                    "AND a.status = 'PENDING' " +  // Chỉ lấy những cuộc hẹn có trạng thái "PENDING"
                    "ORDER BY a.appointmentDate ASC LIMIT ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, doctorId);
                stmt.setInt(2, limit);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // Format lại ngày giờ cho dễ đọc
                    Timestamp timestamp = rs.getTimestamp("appointmentDate");
                    String formattedDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp);

                    Object[] appointment = {
                            formattedDate,
                            rs.getString("appointmentID"),
                            rs.getString("fullName"),
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
                String query = "SELECT shiftType, dayOfWeek, status FROM DoctorSchedule WHERE doctorID = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, doctorId);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Object[] schedule = {
                            rs.getString("shiftType"),
                            rs.getString("dayOfWeek"),
                            rs.getString("status")
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
                        "doctorID VARCHAR(50) NOT NULL, " +
                        "dayOfWeek VARCHAR(20) NOT NULL, " +
                        "shiftType VARCHAR(20) NOT NULL, " +
                        "status VARCHAR(50) DEFAULT 'Không làm việc', " +
                        "PRIMARY KEY (doctorID, dayOfWeek, shiftType), " +
                        "FOREIGN KEY (doctorID) REFERENCES Doctors(doctorID) " +
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
                String query = "SELECT status FROM DoctorSchedule " +
                        "WHERE doctorID = ? AND dayOfWeek = ? AND shiftType = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, doctorId);
                stmt.setString(2, convertDayOfWeek(dayOfWeek)); // Chuyển đổi từ enum sang tên tiếng Việt
                stmt.setString(3, shift);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getString("status");
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
                String query = "INSERT INTO DoctorSchedule (doctorID, dayOfWeek, shiftType, status) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE status = ?";
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
                String query = "INSERT INTO DoctorSchedule (doctorID, dayOfWeek, shiftType, status) " +
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
        System.out.println("DEBUG SQL: SELECT status FROM DoctorSchedule WHERE doctorID = '" +
                doctorId + "' AND dayOfWeek = '" + dayOfWeek + "' AND shiftType = '" + shift + "'");

        String sql = "SELECT status FROM DoctorSchedule WHERE doctorID = ? AND dayOfWeek = ? AND shiftType = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctorId);
            stmt.setString(2, dayOfWeek);
            stmt.setString(3, shift);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
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
        String sql = "UPDATE DoctorSchedule SET status = ? WHERE doctorID = ? AND dayOfWeek = ? AND shiftType = ?";
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

//            if (hasScheduleIDColumn) {
//                System.out.println("CẢNH BÁO: Bảng DoctorSchedule có cột ScheduleID không được dùng trong code hiện tại!");
//            }
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
//            String sql = "SELECT p.*, u.Email, mr.RecordID, mr.Diagnosis, mr.TreatmentPlan, " +
//                         "a.AppointmentDate, " +
//                         "CASE WHEN DATE(a.AppointmentDate) = CURDATE() THEN 'Hôm nay' " +
//                         "ELSE CONCAT('Quá hạn: ', DATE_FORMAT(a.AppointmentDate, '%d/%m/%Y')) END as AppointmentStatus " +
//                         "FROM Patients p " +
//                         "JOIN Appointments a ON p.PatientID = a.PatientID " +
//                         "JOIN UserAccounts u ON p.UserID = u.UserID " +
//                         "LEFT JOIN MedicalRecords mr ON p.PatientID = mr.PatientID " +
//                         "AND mr.RecordDate = (SELECT MAX(RecordDate) FROM MedicalRecords " +
//                         "                     WHERE PatientID = p.PatientID) " +
//                         "WHERE a.DoctorID = ? AND a.Status = 'Chờ xác nhận' " +
//                         "ORDER BY a.AppointmentDate";

            String sql = "SELECT p.*, u.Email, mr.RecordID, mr.Diagnosis, mr.TreatmentPlan, " +
                    "a.AppointmentDate, " +
                    "CASE WHEN DATE(a.AppointmentDate) = CURDATE() THEN 'Hôm nay' " +
                    "WHEN DATE(a.AppointmentDate) < CURDATE() THEN CONCAT('Quá hạn: ', DATE_FORMAT(a.AppointmentDate, '%d/%m/%Y')) " +
                    "ELSE DATE_FORMAT(a.AppointmentDate, '%d/%m/%Y') END as AppointmentStatus " +
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

            String sql = "SELECT p.*, u.email, mr.recordID, mr.diagnosis, mr.treatmentPlan, " +
                    "a.appointmentID, a.appointmentDate, a.status " +
                    "FROM Patients p " +
                    "JOIN Appointments a ON p.patientID = a.patientID " +
                    "JOIN UserAccounts u ON p.userID = u.userID " +
                    "LEFT JOIN MedicalRecords mr ON p.patientID = mr.patientID " +
                    "AND mr.recordDate = (SELECT MAX(recordDate) FROM MedicalRecords " +
                    "                     WHERE patientID = p.patientID) " +
                    "WHERE a.doctorID = ? AND a.status = 'PENDING' " +
                    "AND (p.patientID LIKE ? OR p.fullName LIKE ? OR p.phoneNumber LIKE ?) " +
                    "ORDER BY a.appointmentDate";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, doctorId);
                String likePattern = "%" + keyword + "%";
                stmt.setString(2, likePattern);
                stmt.setString(3, likePattern);
                stmt.setString(4, likePattern);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Tạo đối tượng Patient từ kết quả truy vấn
                        Patient patient = new Patient();
                        patient.setPatientID(rs.getString("patientID"));
                        patient.setFullName(rs.getString("fullName"));

                        // Kiểm tra null cho ngày sinh
                        Date birthDate = rs.getDate("dateOfBirth");
                        if (birthDate != null) {
                            patient.setDateOfBirth(birthDate.toLocalDate());
                        }

                        patient.setAddress(rs.getString("address"));
                        patient.setPhoneNumber(rs.getString("phoneNumber"));

                        // Sửa lỗi chuyển đổi Gender
                        String genderStr = rs.getString("gender");
                        if (genderStr != null) {
                            patient.setGender(Gender.fromDatabase(genderStr));
                        }

                        // Tạo đối tượng MedicalRecord từ kết quả truy vấn
                        MedicalRecord medicalRecord = null;
                        String recordId = rs.getString("recordID");
                        if (recordId != null) {
                            medicalRecord = new MedicalRecord();
                            medicalRecord.setRecordId(recordId);
                            String diagnosis = rs.getString("diagnosis");
                            if (diagnosis != null) {
                                medicalRecord.setDiagnosis(diagnosis);
                            }
                            String treatmentPlan = rs.getString("treatmentPlan");
                            if (treatmentPlan != null) {
                                medicalRecord.setTreatmentPlan(treatmentPlan);
                            } else {
                                medicalRecord.setTreatmentPlan("");
                            }
                        }

                        // Tạo đối tượng Appointment
                        Appointment appointment = new Appointment(
                                conn,
                                rs.getString("patientID"),
                                rs.getString("doctorID"),
                                rs.getTimestamp("appointmentDate").toLocalDateTime()
                        );
                        appointment.setAppointmentId(rs.getString("appointmentID"));
                        appointment.setStatus(AppointmentStatus.valueOf(rs.getString("status")));

                        // Tạo mảng chứa thông tin bệnh nhân, hồ sơ y tế và lịch hẹn
                        Object[] record = {patient, medicalRecord, appointment};
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

    /**
     * Lấy chỉ số sức khỏe mới nhất của bệnh nhân
     * @param patientId ID của bệnh nhân
     * @return VitalSign hoặc null nếu không tìm thấy
     * @throws SQLException nếu có lỗi SQL
     */
    public VitalSign getLatestVitalSignByPatientId(String patientId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM VitalSigns WHERE patientID = ? ORDER BY recordedAt DESC LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                VitalSign vitalSign = new VitalSign();
                vitalSign.setVitalSignID(rs.getString("vitalSignID"));
                vitalSign.setPatientID(rs.getString("patientID"));
                vitalSign.setTemperature(rs.getDouble("temperature"));
                vitalSign.setSystolicPressure(rs.getInt("systolicPressure"));
                vitalSign.setDiastolicPressure(rs.getInt("diastolicPressure"));
                vitalSign.setHeartRate(rs.getInt("heartRate"));
                vitalSign.setOxygenSaturation(rs.getDouble("oxygenSaturation"));
                vitalSign.setRecordedAt(rs.getTimestamp("recordedAt").toLocalDateTime());
                return vitalSign;
            }
            return null;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Lưu chỉ số sức khỏe vào cơ sở dữ liệu
     * @param patientId ID của bệnh nhân
     * @param vitalSign Chỉ số sức khỏe
     * @return true nếu lưu thành công
     * @throws SQLException nếu có lỗi SQL
     */
    public boolean saveVitalSigns(String patientId, VitalSign vitalSign) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO VitalSigns (VitalSignID, PatientID, Temperature, SystolicPressure, DiastolicPressure, HeartRate, OxygenSaturation, RecordedAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, vitalSign.getVitalSignID() != null ? vitalSign.getVitalSignID() : generateVitalSignId(conn));
            stmt.setString(2, patientId);
            stmt.setDouble(3, vitalSign.getTemperature());
            stmt.setInt(4, vitalSign.getSystolicPressure());
            stmt.setInt(5, vitalSign.getDiastolicPressure());
            stmt.setInt(6, vitalSign.getHeartRate());
            stmt.setDouble(7, vitalSign.getOxygenSaturation());
            stmt.setTimestamp(8, Timestamp.valueOf(vitalSign.getRecordedAt()));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Tạo ID mới cho VitalSign theo mẫu VS-001, VS-002, ...
     * @param conn Kết nối database
     * @return ID mới với định dạng VS-XXX
     * @throws SQLException nếu có lỗi
     */
    private String generateVitalSignId(Connection conn) throws SQLException {
        String query = "SELECT MAX(SUBSTRING(VitalSignID, 4)) AS maxID FROM VitalSigns WHERE VitalSignID LIKE 'VS-%'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            int maxID = 0;
            if (rs.next()) {
                String maxIDStr = rs.getString("maxID");
                if (maxIDStr != null && !maxIDStr.isEmpty()) {
                    try {
                        maxID = Integer.parseInt(maxIDStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi chuyển đổi ID VitalSign: " + e.getMessage());
                    }
                }
            }
            return String.format("VS-%03d", maxID + 1);
        }
    }

    /**
     * Lưu đơn thuốc vào cơ sở dữ liệu
     * @param doctorId ID bác sĩ
     * @param prescriptionData Dữ liệu đơn thuốc
     * @param medicineList Danh sách thuốc
     * @return true nếu lưu thành công
     * @throws SQLException nếu có lỗi SQL
     */
    public boolean savePrescription(String doctorId, Map<String, Object> prescriptionData, List<Map<String, Object>> medicineList) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Lấy và kiểm tra thông tin đơn thuốc
            String prescriptionId = (String) prescriptionData.get("prescriptionId");
            String patientId = (String) prescriptionData.get("patientId");
            LocalDate prescriptionDate = LocalDate.now();

            if (prescriptionId == null || prescriptionId.trim().isEmpty()) {
                throw new IllegalArgumentException("prescriptionId không được để trống");
            }
            if (patientId == null || patientId.trim().isEmpty()) {
                throw new IllegalArgumentException("patientId không được để trống");
            }
            if (doctorId == null || doctorId.trim().isEmpty()) {
                throw new IllegalArgumentException("doctorId không được để trống");
            }

            // Kiểm tra trùng lặp prescriptionId và tạo ID mới nếu cần
            String checkSql = "SELECT COUNT(*) FROM Prescriptions WHERE prescriptionID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, prescriptionId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Tạo ID mới nếu đã tồn tại
                        prescriptionId = generateNewPrescriptionId(conn);
                        prescriptionData.put("prescriptionId", prescriptionId);
                    }
                }
            }

            // 2. Lưu thông tin đơn thuốc vào bảng Prescriptions
            String insertPrescriptionSql = "INSERT INTO Prescriptions (prescriptionID, patientID, doctorID, prescriptionDate) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmtPrescription = conn.prepareStatement(insertPrescriptionSql)) {
                stmtPrescription.setString(1, prescriptionId);
                stmtPrescription.setString(2, patientId);
                stmtPrescription.setString(3, doctorId);
                stmtPrescription.setDate(4, java.sql.Date.valueOf(prescriptionDate));
                stmtPrescription.executeUpdate();
            }

            // 3. Lưu chi tiết đơn thuốc vào bảng PrescriptionDetails
            if (medicineList == null || medicineList.isEmpty()) {
                throw new IllegalArgumentException("Danh sách thuốc không được để trống");
            }

            String insertDetailSql = "INSERT INTO PrescriptionDetails (prescriptionID, medicationID, dosage, instructions) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmtDetail = conn.prepareStatement(insertDetailSql)) {
                for (Map<String, Object> medicine : medicineList) {
                    String medicationId = (String) medicine.get("medicationId");
                    String dosage = (String) medicine.get("dosage");
                    String instructions = (String) medicine.get("instruction");
                    String medicineName = (String) medicine.get("name");

                    if (dosage == null || dosage.trim().isEmpty()) {
                        throw new IllegalArgumentException("Liều lượng không được để trống cho thuốc: " + medicineName);
                    }
                    if (instructions == null || instructions.trim().isEmpty()) {
                        throw new IllegalArgumentException("Hướng dẫn không được để trống cho thuốc: " + medicineName);
                    }

                    if (medicationId == null || medicationId.trim().isEmpty()) {
                        if (medicineName == null || medicineName.trim().isEmpty()) {
                            throw new IllegalArgumentException("Tên thuốc không được để trống khi medicationId không tồn tại");
                        }
                        medicationId = findOrCreateMedication(conn, medicineName);
                    }

                    stmtDetail.setString(1, prescriptionId);
                    stmtDetail.setString(2, medicationId);
                    stmtDetail.setString(3, dosage);
                    stmtDetail.setString(4, instructions);
                    stmtDetail.addBatch();
                }
                stmtDetail.executeBatch();
            }

            conn.commit();
            System.out.println("Lưu đơn thuốc thành công cho prescriptionID: " + prescriptionId);
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
            throw e;
        } catch (IllegalArgumentException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException("Lỗi dữ liệu đầu vào: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Sinh mã đơn thuốc duy nhất dựa trên connection hiện tại
     */
    private String generateNewPrescriptionId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(prescriptionID) FROM Prescriptions";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String maxId = rs.getString(1);
                if (maxId != null) {
                    int num = Integer.parseInt(maxId.replace("PRE-", "")) + 1;
                    return String.format("PRE-%03d", num);
                }
            }
            return "PRE-001"; // Giá trị mặc định nếu không có bản ghi nào
        }
    }

    /**
     * Cập nhật trạng thái hoàn thành khám cho bệnh nhân
     * @param patientId ID bệnh nhân
     * @param doctorId ID bác sĩ
     * @return true nếu cập nhật thành công
     * @throws SQLException nếu có lỗi SQL
     */
    public boolean completePatientExamination(String patientId, String doctorId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Cập nhật trạng thái cuộc hẹn
            String updateAppointmentSql = "UPDATE Appointments SET status = ? " +
                    "WHERE patientID = ? AND doctorID = ? AND DATE(appointmentDate) = CURDATE()";

            try (PreparedStatement stmt = conn.prepareStatement(updateAppointmentSql)) {
                stmt.setString(1, AppointmentStatus.COMPLETED.toString());
                stmt.setString(2, patientId);
                stmt.setString(3, doctorId);
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
            String sql = "SELECT MAX(prescriptionID) AS lastID FROM Prescriptions";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    String lastId = rs.getString("lastID");

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
     * Tìm hoặc tạo ID thuốc từ tên thuốc
     * @param conn Connection đến database
     * @param medicineName Tên thuốc
     * @return ID thuốc
     */
    private String findOrCreateMedication(Connection conn, String medicineName) throws SQLException {
        if (medicineName == null || medicineName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thuốc không được để trống hoặc null");
        }

        String sql = "SELECT medicationID FROM Medications WHERE medicineName = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicineName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("medicationID");
                }
            }
        }

        String medicationId = generateMedicationId(conn);

        sql = "INSERT INTO Medications (medicationID, medicineName) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medicationId);
            stmt.setString(2, medicineName);
            stmt.executeUpdate();
            System.out.println("Đã tạo mới thuốc với ID: " + medicationId + ", tên: " + medicineName);
        }

        return medicationId;
    }

    /**
     * Sinh ID mới cho thuốc
     * @param conn Connection đến database
     * @return ID thuốc mới
     */
    private String generateMedicationId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(medicationID) AS lastID FROM Medications";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("lastID");

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
        String selectSql = "SELECT diagnosis FROM MedicalRecords " +
                "WHERE patientID = ? AND isHistory = FALSE " +
                "ORDER BY recordDate DESC LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    latestDiagnosis = rs.getString("diagnosis");
                }
            }
        }

        // Nếu có chẩn đoán cũ và chẩn đoán mới khác với cũ
        if (latestDiagnosis != null && !latestDiagnosis.equals(newDiagnosis)) {
            // Cập nhật tất cả hồ sơ cũ thành tiền sử
            String updateSql = "UPDATE MedicalRecords SET isHistory = TRUE " +
                    "WHERE patientID = ? AND isHistory = FALSE";

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
            String sql = "SELECT MAX(recordID) as lastID FROM MedicalRecords";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    String lastId = rs.getString("lastID");

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
            String selectSql = "SELECT medicationID FROM Medications WHERE medicineName = ?";

            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setString(1, medicineName);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("medicationID");
                    }
                }
            }

            // Nếu không tìm thấy, tạo thuốc mới
            String medicationId = generateMedicationId();

            String insertSql = "INSERT INTO Medications (medicationID, medicineName) VALUES (?, ?)";

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
            String sql = "SELECT MAX(medicationID) as lastID FROM Medications";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    String lastId = rs.getString("lastID");

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
            String sql = "SELECT diagnosis FROM MedicalRecords " +
                    "WHERE patientID = ? AND isHistory = FALSE " +
                    "ORDER BY recordDate DESC LIMIT 1";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        diagnosis = rs.getString("diagnosis");
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
            String sql = "SELECT * FROM Medications ORDER BY medicineName";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> med = new HashMap<>();
                    med.put("medicationId", rs.getString("medicationID"));
                    med.put("medicineName", rs.getString("medicineName"));
                    med.put("description", rs.getString("description"));
                    med.put("manufacturer", rs.getString("manufacturer"));
                    med.put("dosageForm", rs.getString("dosageForm"));
                    med.put("sideEffects", rs.getString("sideEffects"));

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
            String sql = "SELECT * FROM Medications WHERE medicineName LIKE ? OR description LIKE ? ORDER BY medicineName";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String searchPattern = "%" + keyword + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> med = new HashMap<>();
                        med.put("medicationId", rs.getString("medicationID"));
                        med.put("medicineName", rs.getString("medicineName"));
                        med.put("description", rs.getString("description"));
                        med.put("manufacturer", rs.getString("manufacturer"));
                        med.put("dosageForm", rs.getString("dosageForm"));
                        med.put("sideEffects", rs.getString("sideEffects"));

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
            String sql = "SELECT COUNT(*) FROM Prescriptions WHERE prescriptionID = ?";

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
            String query = "UPDATE Appointments SET status = ? WHERE appointmentID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, AppointmentStatus.CANCELED.toString());
                stmt.setString(2, appointmentId);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }
}