package model.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.DatabaseConnection;

import entity.Doctor;
import enums.PaymentStatus;
import model.entity.Billing;
import model.entity.Patient;
import model.enums.Gender;

public class UserRepository {

	// Phương thức đăng ký người dùng mới
    public static String registerUser(String email, String password) {
        if (isEmailTaken(email)) {
            return "Email already taken";
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password, role) VALUES (?, SHA2(?, 256), 'patient')";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email); // Sử dụng email làm username
            stmt.setString(2, password);
            stmt.executeUpdate();
            return "Success";
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }

    // Kiểm tra email đã tồn tại
    public static boolean isEmailTaken(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức đăng nhập
    public static boolean loginUser(String email, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT role FROM users WHERE username = ? AND password = SHA2(?, 256)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Trả về true nếu tìm thấy user, false nếu không
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy chức vụ
    public static String getUserRole(String username) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT role FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // reset password
    public static String resetPassword(String input) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            System.out.println("Database connection successful");

            String checkQuery = "SELECT id, email FROM users WHERE username = ? OR email = ?";
            stmt = conn.prepareStatement(checkQuery);
            stmt.setString(1, input);
            stmt.setString(2, input);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("id");
                String userEmail = rs.getString("email");
                System.out.println("User found: id=" + userId + ", email=" + userEmail);

                String resetToken = UUID.randomUUID().toString();
                Timestamp expiryDate = new Timestamp(System.currentTimeMillis() + 3600 * 1000);

                String insertQuery = "INSERT INTO reset_tokens (user_id, token, expiry_date) VALUES (?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE token = ?, expiry_date = ?";
                stmt = conn.prepareStatement(insertQuery);
                stmt.setString(1, userId);
                stmt.setString(2, resetToken);
                stmt.setTimestamp(3, expiryDate);
                stmt.setString(4, resetToken);
                stmt.setTimestamp(5, expiryDate);
                stmt.executeUpdate();

                return resetToken; // Trả về reset token
            } else {
                System.out.println("No user found for input: " + input);
                return null;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Phương thức confirmResetPassword (đã có)
    public static boolean confirmResetPassword(String token, String newPassword) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Kiểm tra token có tồn tại và chưa hết hạn
            String checkTokenQuery = "SELECT user_id FROM reset_tokens WHERE token = ? AND expiry_date > NOW()";
            stmt = conn.prepareStatement(checkTokenQuery);
            stmt.setString(1, token);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("user_id");

                // Cập nhật mật khẩu mới cho người dùng (mã hóa bằng SHA2)
                String updatePasswordQuery = "UPDATE users SET password = SHA2(?, 256) WHERE id = ?";
                stmt = conn.prepareStatement(updatePasswordQuery);
                stmt.setString(1, newPassword);
                stmt.setString(2, userId);
                stmt.executeUpdate();

                // Xóa token sau khi sử dụng
                String deleteTokenQuery = "DELETE FROM reset_tokens WHERE token = ?";
                stmt = conn.prepareStatement(deleteTokenQuery);
                stmt.setString(1, token);
                stmt.executeUpdate();

                return true; // Reset mật khẩu thành công
            } else {
                return false; // Token không hợp lệ hoặc đã hết hạn
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String registerUser(String username, String email, String password, String role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Chèn vào bảng users và lấy id vừa tạo
            stmt = conn.prepareStatement("INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); // Nên mã hóa password trong thực tế
            stmt.setString(4, role);
            stmt.executeUpdate();

            // Lấy id vừa tạo
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                return "Success:" + userId; // Trả về "Success" kèm theo userId
            } else {
                return "Error: Failed to retrieve user ID";
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String addPatient(int userId, String name, String birthdate, String gender, String address) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement("INSERT INTO patients (id, name, birthdate, address, gender) VALUES (?, ?, ?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, birthdate);
            stmt.setString(4, address);
            stmt.setString(5, gender);
            stmt.executeUpdate();
            return "Success";
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Phương thức lấy patientID từ email
    public static String getPatientID(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.valueOf(rs.getInt("id"));
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Phương thức lấy thông tin bệnh nhân từ patientID
    public static Patient getPatientById(String patientID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM patients WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Patient patient = new Patient();
                patient.setPatientID(rs.getString("id"));
                patient.setFullName(rs.getString("name"));
                patient.setDateOfBirth(LocalDate.parse(rs.getString("birthdate")));
                patient.setAddress(rs.getString("address"));
                patient.setGender(Gender.valueOf(rs.getString("gender")));
                patient.setCreatedAt(LocalDate.parse(rs.getString("created_at")));
                return patient;
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Phương thức lấy thông tin bác sĩ từ doctorID
    public static Doctor getDoctorById(String doctorID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE id = ? AND role = 'doctor'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, doctorID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getString("id"));
                return doctor;
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Phương thức đặt lịch hẹn
    public static boolean bookAppointment(String patientID, String doctor, LocalDate appointmentDate) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO appointments (patient_id, doctor_name, appointment_date, status) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            stmt.setString(2, doctor);
            stmt.setString(3, appointmentDate.toString());
            stmt.setString(4, "Scheduled");
            stmt.executeUpdate();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức lấy danh sách lịch hẹn
    public static List<String[]> getAppointmentsFromDatabase(String patientID) {
        List<String[]> appointments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT doctor_name, appointment_date, status FROM appointments WHERE patient_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new String[] { rs.getString("doctor_name"), rs.getString("appointment_date"),
                        rs.getString("status") });
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // Phương thức lấy danh sách hóa đơn chưa thanh toán
    public static List<Billing> getUnpaidBillings(String patientID) {
        List<Billing> billings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM bills WHERE patient_id = ? AND status = 'Unpaid'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Patient patient = getPatientById(rs.getString("patient_id"));
                Billing billing = new Billing(patient, rs.getDouble("amount"), null,
                        PaymentStatus.valueOf(rs.getString("status").toUpperCase()),
                        LocalDate.parse(rs.getString("created_at")));
                billing.setBillingId(rs.getString("id"));
                billings.add(billing);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return billings;
    }

    // Phương thức thanh toán hóa đơn
    public static boolean payBilling(String billingID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE bills SET status = 'Paid' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, billingID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}