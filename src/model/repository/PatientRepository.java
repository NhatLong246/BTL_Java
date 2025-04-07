package model.repository;

import model.entity.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PatientManagement?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2005";

    public List<String[]> getMedicalHistory(String patientID) {
        List<String[]> medicalHistory = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT mr.RecordDate, mr.Diagnosis, mr.TreatmentPlan, ua.FullName " +
                           "FROM MedicalRecords mr " +
                           "LEFT JOIN Doctors d ON mr.DoctorID = d.DoctorID " +
                           "LEFT JOIN UserAccounts ua ON d.UserID = ua.UserID " +
                           "WHERE mr.PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                medicalHistory.add(new String[]{
                    rs.getString("RecordDate"),
                    rs.getString("Diagnosis"),
                    rs.getString("TreatmentPlan"),
                    rs.getString("FullName") != null ? rs.getString("FullName") : "N/A"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicalHistory;
    }

    public List<String[]> getAppointments(String patientID) {
        List<String[]> appointments = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT ua.FullName, a.AppointmentDate, a.Status " +
                           "FROM Appointments a " +
                           "LEFT JOIN Doctors d ON a.DoctorID = d.DoctorID " +
                           "LEFT JOIN UserAccounts ua ON d.UserID = ua.UserID " +
                           "WHERE a.PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new String[]{
                    rs.getString("FullName") != null ? rs.getString("FullName") : "N/A",
                    rs.getString("AppointmentDate"),
                    rs.getString("Status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public List<Object[]> getBills(String patientID) {
        List<Object[]> bills = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT BillID, TotalAmount, Status FROM Billing WHERE PatientID = ? AND Status = 'Chưa thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bills.add(new Object[]{
                    rs.getString("BillID"),
                    rs.getDouble("TotalAmount"),
                    rs.getString("Status")
                });
            }
        } catch (SQLException e) {
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

    public Object[][] getPaymentHistory(String patientID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT BillID, CreatedAt, TotalAmount, PaymentMethod, Status FROM Billing WHERE PatientID = ? AND Status = 'Đã thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();

            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            Object[][] data = new Object[rowCount][5];
            int rowIndex = 0;
            while (rs.next()) {
                data[rowIndex][0] = rs.getString("BillID");
                data[rowIndex][1] = rs.getTimestamp("CreatedAt");
                data[rowIndex][2] = rs.getDouble("TotalAmount");
                data[rowIndex][3] = rs.getString("PaymentMethod");
                data[rowIndex][4] = rs.getString("Status");
                rowIndex++;
            }
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Object[0][0];
    }
}