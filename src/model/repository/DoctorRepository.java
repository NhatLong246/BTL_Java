package model.repository;

import model.entity.Patient;
import model.enums.Gender;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DoctorRepository {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2005";

    public boolean addPatient(Patient patient, String medicalHistory) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Thêm vào bảng UserAccounts
            String userQuery = "INSERT INTO UserAccounts (UserID, FullName, Role, Email, PhoneNumber, PasswordHash) " +
                    "VALUES (?, ?, 'Bệnh nhân', ?, ?, ?)";
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            userStmt.setString(1, patient.getUserID());
            userStmt.setString(2, patient.getFullName());
            userStmt.setString(3, patient.getUserID() + "@example.com"); // Giả định email
            userStmt.setString(4, patient.getPhoneNumber());
            userStmt.setString(5, "defaultPassword"); // Mật khẩu mặc định
            userStmt.executeUpdate();

            // Thêm vào bảng Patients
            String patientQuery = "INSERT INTO Patients (PatientID, UserID, DateOfBirth, Gender, Address, CreatedAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement patientStmt = conn.prepareStatement(patientQuery);
            patientStmt.setString(1, patient.getPatientID());
            patientStmt.setString(2, patient.getUserID());
            patientStmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            patientStmt.setString(4, patient.getGender().toString());
            patientStmt.setString(5, patient.getAddress());
            patientStmt.setDate(6, Date.valueOf(patient.getCreatedAt()));
            patientStmt.executeUpdate();

            // Thêm vào bảng MedicalRecords (nếu có medicalHistory)
            if (medicalHistory != null && !medicalHistory.isEmpty()) {
                String recordQuery = "INSERT INTO MedicalRecords (RecordID, PatientID, Diagnosis, TreatmentPlan, RecordDate) " +
                        "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement recordStmt = conn.prepareStatement(recordQuery);
                recordStmt.setString(1, "REC" + System.currentTimeMillis());
                recordStmt.setString(2, patient.getPatientID());
                recordStmt.setString(3, medicalHistory);
                recordStmt.setString(4, "Chưa có kế hoạch điều trị");
                recordStmt.setDate(5, Date.valueOf(LocalDate.now()));
                recordStmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT p.*, u.FullName, u.PhoneNumber FROM Patients p " +
                    "JOIN UserAccounts u ON p.UserID = u.UserID";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getString("UserID"),
                        rs.getString("PatientID"),
                        rs.getString("FullName"),
                        rs.getDate("DateOfBirth").toLocalDate(),
                        rs.getString("Address"),
                        Gender.valueOf(rs.getString("Gender")),
                        rs.getString("PhoneNumber"),
                        rs.getDate("CreatedAt").toLocalDate()
                );
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    public boolean bookAppointment(String patientId, LocalDate appointmentDate, String doctorId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Status) " +
                    "VALUES (?, ?, ?, ?, 'Chờ xác nhận')";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "APP" + System.currentTimeMillis());
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

    public boolean deletePatient(String patientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM Patients WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}