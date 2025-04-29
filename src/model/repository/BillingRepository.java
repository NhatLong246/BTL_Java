package model.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingRepository {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userdb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2005";

    public String getTotalBills(String patientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT SUM(TotalAmount) FROM Billing WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("$%.2f", rs.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public String getPaidBills(String patientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT SUM(TotalAmount) FROM Billing WHERE PatientID = ? AND Status = 'Đã thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("$%.2f", rs.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public String getPendingBills(String patientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT SUM(TotalAmount) FROM Billing WHERE PatientID = ? AND Status = 'Chưa thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("$%.2f", rs.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public boolean processPayment(String patientId, double amount, String method) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String billID = "BILL" + System.currentTimeMillis();
            String query = "INSERT INTO Billing (BillID, PatientID, TotalAmount, PaymentMethod, Status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, billID);
            stmt.setString(2, patientId);
            stmt.setDouble(3, amount);
            stmt.setString(4, method);
            stmt.setString(5, "Đã thanh toán"); // Đổi thành "Đã thanh toán" ngay khi thanh toán

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                addBillingDetails(billID, amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addBillingDetails(String billID, double amount) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO BillingDetails (BillID, ServiceID, Amount) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, billID);
            stmt.setString(2, "SERVICE001");
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object[][] getPaymentHistory(String patientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT BillID, CreatedAt, TotalAmount, PaymentMethod, Status FROM Billing WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
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