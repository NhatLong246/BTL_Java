package model.repository;

import database.DatabaseConnection;
import model.entity.Patient;
import model.enums.Gender;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class PatientRepository {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2005";
    
    public Patient getPatientByUserId(String userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT p.*, ua.FullName, ua.PhoneNumber, ua.Email " +
                          "FROM Patients p " +
                          "JOIN UserAccounts ua ON p.UserID = ua.UserID " +
                          "WHERE p.UserID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId); // Sử dụng setString thay vì setInt
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Patient(
                    rs.getString("UserID"),
                    rs.getString("PatientID"),
                    rs.getString("FullName"),
                    rs.getDate("DateOfBirth").toLocalDate(),
                    rs.getString("Address"),
                    Gender.valueOf(rs.getString("Gender")),
                    rs.getString("PhoneNumber"),
                    rs.getDate("CreatedAt").toLocalDate()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Patient getPatientByID(String patientID) {
        System.out.println("Đang tìm bệnh nhân với ID: " + patientID);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM Patients WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            
            System.out.println("Executing query: " + query.replace("?", "'" + patientID + "'"));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Tạo và trả về đối tượng Patient
                Patient patient = new Patient(
                    rs.getString("UserID"),
                    patientID,
                    rs.getString("FullName"),
                    rs.getDate("DateOfBirth") != null ? rs.getDate("DateOfBirth").toLocalDate() : null,
                    rs.getString("Address"),
                    Gender.fromDatabase(rs.getString("Gender")),
                    rs.getString("PhoneNumber"),
                    rs.getDate("CreatedAt") != null ? rs.getDate("CreatedAt").toLocalDate() : null
                );
                
                System.out.println("Đã tìm thấy bệnh nhân: " + patient.getFullName());
                return patient;
            } else {
                System.out.println("Không tìm thấy bệnh nhân với ID: " + patientID);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm bệnh nhân: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<String[]> getMedicalHistory(String patientID) {
        List<String[]> medicalHistory = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT mr.RecordID, mr.RecordDate, ua.FullName as DoctorName, " +
                          "mr.Diagnosis, mr.TreatmentPlan, " +
                          "CASE WHEN mr.IsHistory = 1 THEN 'Lịch sử' ELSE 'Hiện tại' END as Status " +
                          "FROM MedicalRecords mr " +
                          "LEFT JOIN Doctors d ON mr.DoctorID = d.DoctorID " +
                          "LEFT JOIN UserAccounts ua ON d.UserID = ua.UserID " +
                          "WHERE mr.PatientID = ? " +
                          "ORDER BY mr.RecordDate DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, patientID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String[] record = new String[6];
                    record[0] = rs.getString("RecordID");                     // ID
                    record[1] = rs.getDate("RecordDate").toString();          // Ngày khám
                    record[2] = rs.getString("DoctorName");                   // Bác sĩ
                    record[3] = rs.getString("Diagnosis");                    // Chẩn đoán
                    record[4] = rs.getString("TreatmentPlan");                // Điều trị
                    record[5] = ""; // Ghi chú (có thể để trống)
                    medicalHistory.add(record);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử khám bệnh: " + e.getMessage());
            e.printStackTrace();
        }
        return medicalHistory;
    }

    public List<String[]> getAppointments(String patientID) {
        List<String[]> appointments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT a.AppointmentID, DATE_FORMAT(a.AppointmentDate, '%Y-%m-%d') as AppDate, " +
                           "TIME_FORMAT(a.AppointmentDate, '%H:%i:%s') as AppTime, " +
                           "d.FullName as DoctorName, " +
                           "CONCAT('P', FLOOR(RAND() * 100) + 100) as RoomNumber, " +  // Tạm thời tạo số phòng ngẫu nhiên
                           "a.Status " +
                           "FROM Appointments a " +
                           "LEFT JOIN Doctors d ON a.DoctorID = d.DoctorID " +
                           "WHERE a.PatientID = ? " +
                           "ORDER BY a.AppointmentDate DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, patientID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    appointments.add(new String[]{
                        rs.getString("AppointmentID"),               // ID Lịch hẹn
                        rs.getString("AppDate"),                     // Ngày hẹn
                        rs.getString("AppTime"),                     // Thời gian
                        rs.getString("DoctorName") != null ? 
                            rs.getString("DoctorName") : "N/A",      // Bác sĩ
                        rs.getString("RoomNumber"),                  // Phòng
                        rs.getString("Status")                       // Trạng thái
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    public List<Object[]> getBills(String patientID) {
        List<Object[]> bills = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Đang lấy danh sách hóa đơn cho bệnh nhân: " + patientID);
            
            String query = "SELECT b.BillID, b.CreatedAt, GROUP_CONCAT(s.ServiceName SEPARATOR ', ') as ServiceNames, " +
                           "b.TotalAmount, b.Status " +
                           "FROM Billing b " +
                           "LEFT JOIN BillingDetails bd ON b.BillID = bd.BillID " +
                           "LEFT JOIN Services s ON bd.ServiceID = s.ServiceID " +
                           "WHERE b.PatientID = ? AND b.Status = 'Chưa thanh toán' " +
                           "GROUP BY b.BillID, b.CreatedAt, b.TotalAmount, b.Status";
                           
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, patientID);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Object[] bill = new Object[5];
                    bill[0] = rs.getString("BillID");
                    bill[1] = rs.getTimestamp("CreatedAt");
                    bill[2] = rs.getString("ServiceNames") != null ? rs.getString("ServiceNames") : "Chưa xác định";
                    bill[3] = rs.getDouble("TotalAmount");
                    bill[4] = rs.getString("Status");
                    bills.add(bill);
                    System.out.println("Tìm thấy hóa đơn: " + bill[0] + ", dịch vụ: " + bill[2]);
                }
                
                System.out.println("Tổng số hóa đơn tìm thấy: " + bills.size());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bills;
    }

    public boolean payBill(String billID, String paymentMethod) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE Billing SET Status = 'Đã thanh toán', PaymentMethod = ? WHERE BillID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, paymentMethod);
            stmt.setString(2, billID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // public Object[][] getPaymentHistory(String patientID) {
    //     try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
    //         String query = "SELECT BillID, CreatedAt, TotalAmount, PaymentMethod, Status FROM Billing WHERE PatientID = ? AND Status = 'Đã thanh toán'";
    //         PreparedStatement stmt = conn.prepareStatement(query);
    //         stmt.setString(1, patientID);
    //         ResultSet rs = stmt.executeQuery();

    //         rs.last();
    //         int rowCount = rs.getRow();
    //         rs.beforeFirst();

    //         Object[][] data = new Object[rowCount][5];
    //         int rowIndex = 0;
    //         while (rs.next()) {
    //             data[rowIndex][0] = rs.getString("BillID");
    //             data[rowIndex][1] = rs.getTimestamp("CreatedAt");
    //             data[rowIndex][2] = rs.getDouble("TotalAmount");
    //             data[rowIndex][3] = rs.getString("PaymentMethod");
    //             data[rowIndex][4] = rs.getString("Status");
    //             rowIndex++;
    //         }
    //         return data;
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return new Object[0][0];
    // }

    public Object[][] getPaymentHistory(String patientID) {
        List<Object[]> paymentList = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT BillID, CreatedAt, TotalAmount, PaymentMethod, Status FROM Billing WHERE PatientID = ? AND Status = 'Đã thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("BillID");
                row[1] = rs.getTimestamp("CreatedAt");
                row[2] = rs.getDouble("TotalAmount");
                row[3] = rs.getString("PaymentMethod");
                row[4] = rs.getString("Status");
                paymentList.add(row);
            }
            
            // Chuyển từ ArrayList sang mảng 2 chiều
            Object[][] data = new Object[paymentList.size()][5];
            for (int i = 0; i < paymentList.size(); i++) {
                data[i] = paymentList.get(i);
            }
            return data;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
        return new Object[0][0];
    }

        /**
     * Thêm bệnh nhân mới vào database
     * @param patient Thông tin bệnh nhân cần thêm
     * @param medicalHistory Thông tin bệnh sử (có thể null nếu không có)
     * @param doctorId ID của bác sĩ thực hiện (có thể null)
     * @return true nếu thêm thành công
     */
    public boolean addPatient(Patient patient, String medicalHistory, String doctorId, String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            String checkPhoneQuery = "SELECT COUNT(*) FROM UserAccounts WHERE PhoneNumber = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkPhoneQuery)) {
                checkStmt.setString(1, patient.getPhoneNumber());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, 
                        "Số điện thoại " + patient.getPhoneNumber() + " đã được sử dụng.\n" +
                        "Vui lòng sử dụng số điện thoại khác.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            try {
                // Tạo ID bệnh nhân mới nếu chưa có
                if (patient.getPatientID() == null || patient.getPatientID().isEmpty()) {
                    String patientID = generateNewPatientID(conn);
                    patient.setPatientID(patientID);
                }
                
                // 1. Thêm vào bảng UserAccounts nếu chưa có
                String checkUserQuery = "SELECT COUNT(*) FROM UserAccounts WHERE UserID = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery)) {
                    checkStmt.setString(1, patient.getUserID());
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);
                    
                    if (count == 0) {
                        String password = generateSecurePassword();

                        String userQuery = "INSERT INTO UserAccounts (UserID, UserName, FullName, Role, " +
                                "Email, PhoneNumber, PasswordHash, CreatedAt, PasswordChangeRequired) " +
                                "VALUES (?, ?, ?, 'Bệnh nhân', ?, ?, SHA2(?, 256), NOW(), TRUE)";
                        try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                            userStmt.setString(1, patient.getUserID());
                            String username = generateUsername(patient.getFullName(), patient.getPatientID());
                            userStmt.setString(2, username);
                            userStmt.setString(3, patient.getFullName());
                            String emailValue = (email != null && !email.isEmpty()) 
                                ? email 
                                : patient.getUserID() + "@hospital.local";
                            userStmt.setString(4, emailValue);
                            userStmt.setString(5, patient.getPhoneNumber());
                            userStmt.setString(6, password);
                            userStmt.executeUpdate();
                            System.out.println("Đã thêm vào UserAccounts: " + patient.getUserID());

                            // Lưu thông tin đăng nhập để trả về cho người dùng
                            patient.setLoginCredentials(username, password);
                        }
                    }
                }
                
                // 2. Thêm vào bảng Patients
                String patientQuery = "INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, " +
                        "Gender, Address, PhoneNumber, CreatedAt) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
                try (PreparedStatement patientStmt = conn.prepareStatement(patientQuery)) {
                    patientStmt.setString(1, patient.getPatientID());
                    patientStmt.setString(2, patient.getUserID());
                    patientStmt.setString(3, patient.getFullName());
                    patientStmt.setDate(4, java.sql.Date.valueOf(patient.getDateOfBirth()));
                    patientStmt.setString(5, patient.getGender().toString());
                    patientStmt.setString(6, patient.getAddress());
                    patientStmt.setString(7, patient.getPhoneNumber());
                    patientStmt.executeUpdate();
                    System.out.println("Đã thêm vào Patients: " + patient.getPatientID());
                }
                
                // 3. Thêm vào bảng MedicalRecords nếu có thông tin
                if (medicalHistory != null && !medicalHistory.trim().isEmpty() && doctorId != null) {
                    String recordId = generateNewMedicalRecordID(conn);
                    String recordQuery = "INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, " +
                            "Diagnosis, RecordDate) VALUES (?, ?, ?, ?, NOW())";
                    try (PreparedStatement recordStmt = conn.prepareStatement(recordQuery)) {
                        recordStmt.setString(1, recordId);
                        recordStmt.setString(2, patient.getPatientID());
                        recordStmt.setString(3, doctorId);
                        recordStmt.setString(4, medicalHistory);
                        recordStmt.executeUpdate();
                        System.out.println("Đã thêm vào MedicalRecords: " + recordId);
                    }
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Lỗi thêm bệnh nhân: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addPatient(Patient patient, String medicalHistory, String doctorId) {
        return addPatient(patient, medicalHistory, doctorId, null);
    }

    // Thêm phương thức kiểm tra số điện thoại
    public boolean isPhoneNumberExists(String phoneNumber) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM UserAccounts WHERE PhoneNumber = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, phoneNumber);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra số điện thoại: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


        /**
     * Tạo mật khẩu ngẫu nhiên an toàn
     * @return Mật khẩu ngẫu nhiên
     */
    private String generateSecurePassword() {
        // Các ký tự được phép
        String uppercase = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // Loại bỏ I và O dễ nhầm lẫn
        String lowercase = "abcdefghijkmnopqrstuvwxyz"; // Loại bỏ l dễ nhầm lẫn
        String digits = "23456789"; // Loại bỏ 0 và 1 dễ nhầm lẫn
        String specialChars = "@#$%^&+=";
        
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();
        
        // Đảm bảo mật khẩu có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Thêm 4 ký tự ngẫu nhiên nữa để đủ 8 ký tự
        String allChars = uppercase + lowercase + digits + specialChars;
        for (int i = 0; i < 4; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Trộn ngẫu nhiên các ký tự
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = temp;
        }
        
        return new String(passwordArray);
    }
    
    /**
     * Tạo ID mới cho bản ghi y tế theo mẫu MR-001, MR-002, ...
     * @param conn Kết nối đến database
     * @return ID mới với định dạng MR-XXX
     */
    private String generateNewMedicalRecordID(Connection conn) throws SQLException {
        String query = "SELECT MAX(SUBSTRING(RecordID, 4)) as MaxID FROM MedicalRecords WHERE RecordID LIKE 'MR-%'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            int maxID = 0;
            if (rs.next()) {
                String maxIDStr = rs.getString("MaxID");
                if (maxIDStr != null && !maxIDStr.isEmpty()) {
                    try {
                        maxID = Integer.parseInt(maxIDStr);
                    } catch (NumberFormatException e) {
                        // Sử dụng giá trị mặc định 0 nếu có lỗi
                        System.err.println("Lỗi chuyển đổi ID bản ghi y tế: " + e.getMessage());
                    }
                }
            }
            return String.format("MR-%03d", maxID + 1);
        }
    }
    
    /**
     * Thêm bệnh nhân mới không kèm thông tin y tế
     * @param patient Đối tượng bệnh nhân cần thêm
     * @return true nếu thêm thành công
     */
    public boolean addPatient(Patient patient) {
        return addPatient(patient, null, null);
    }
    
    /**
     * Tạo ID mới cho bệnh nhân theo mẫu PAT-001, PAT-002, ...
     */
    private String generateNewPatientID(Connection conn) throws SQLException {
        String query = "SELECT MAX(SUBSTRING(PatientID, 5)) as MaxID FROM Patients WHERE PatientID LIKE 'PAT-%'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            int maxID = 0;
            if (rs.next()) {
                String maxIDStr = rs.getString("MaxID");
                if (maxIDStr != null && !maxIDStr.isEmpty()) {
                    try {
                        maxID = Integer.parseInt(maxIDStr);
                    } catch (NumberFormatException e) {
                        // Sử dụng giá trị mặc định 0 nếu có lỗi
                    }
                }
            }
            return String.format("PAT-%03d", maxID + 1);
        }
    }
    
    /**
     * Tạo tên đăng nhập từ họ tên
     */
    private String generateUsername(String fullName, String patientID) {
        if (fullName == null || fullName.isEmpty()) {
            return "user" + patientID.substring(4);
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
        
         // Lấy phần số từ PatientID (ví dụ: "001" từ "PAT-001")
        String idSuffix = "";
        if (patientID != null && patientID.length() > 4) {
            idSuffix = patientID.substring(4);
        }
        return normalized + idSuffix;
    }
}