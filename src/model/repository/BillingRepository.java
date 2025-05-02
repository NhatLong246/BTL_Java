package model.repository;

import database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillingRepository {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userdb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2005";

    public String getTotalBills(String patientId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT SUM(Amount) FROM Bills WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("%,.0f VND", rs.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0 VND";
    }

    public String getPaidBills(String patientId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT SUM(Amount) FROM Bills WHERE PatientID = ? AND Status = 'Đã thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("%,.0f VND", rs.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0 VND";
    }

    public String getPendingBillsTotal(String patientId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT SUM(Amount) FROM Bills WHERE PatientID = ? AND Status = 'Chưa thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("%,.0f VND", rs.getDouble(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0 VND";
    }

    public boolean processPayment(String patientId, double amount, String method) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String billID = "BILL" + System.currentTimeMillis();
            String query = "INSERT INTO Bills (BillID, PatientID, Amount, PaymentMethod, Status, CreatedAt, UpdatedAt) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            
            stmt.setString(1, billID);
            stmt.setString(2, patientId);
            stmt.setDouble(3, amount);
            stmt.setString(4, method);
            stmt.setString(5, "Đã thanh toán");
            stmt.setTimestamp(6, now);
            stmt.setTimestamp(7, now);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                addBillingDetails(billID, amount);
                logPayment(billID, patientId, method);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addBillingDetails(String billID, double amount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
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

    /**
     * Kiểm tra xem hóa đơn có đang chờ thanh toán không
     */
    public boolean isBillPendingPayment(String billId) {
        String query = "SELECT Status FROM Bills WHERE BillID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, billId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return "Chưa thanh toán".equals(rs.getString("Status"));
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật trạng thái hóa đơn
     */
    public boolean updateBillStatus(String billId, String status) {
        String query = "UPDATE Bills SET Status = ?, UpdatedAt = ? WHERE BillID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, billId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ghi log thanh toán
     */
    public void logPayment(String billId, String patientId, String paymentMethod) {
        String query = "INSERT INTO PaymentLogs (BillID, PatientID, PaymentMethod, PaymentDate, Amount) " +
                      "SELECT ?, ?, ?, ?, Amount FROM Bills WHERE BillID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, billId);
            stmt.setString(2, patientId);
            stmt.setString(3, paymentMethod);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(5, billId);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy danh sách hóa đơn chưa thanh toán của bệnh nhân
     */
    public Object[][] getPendingBills(String patientId) {
        String query = "SELECT b.BillID, b.CreatedAt, s.ServiceName, b.Amount, b.Status " +
                      "FROM Bills b " +
                      "JOIN Services s ON b.ServiceID = s.ServiceID " +
                      "WHERE b.PatientID = ? AND b.Status = 'Chưa thanh toán' " +
                      "ORDER BY b.CreatedAt DESC";
                      
        List<Object[]> bills = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] bill = new Object[5];
                bill[0] = rs.getString("BillID");
                bill[1] = rs.getTimestamp("CreatedAt");
                bill[2] = rs.getString("ServiceName");
                bill[3] = String.format("%,.0f VND", rs.getDouble("Amount"));
                bill[4] = rs.getString("Status");
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bills.toArray(new Object[0][]);
    }

    /**
     * Lấy lịch sử thanh toán của bệnh nhân
     */
    public Object[][] getPaymentHistory(String patientId) {
        String query = "SELECT p.BillID, p.PaymentDate, s.ServiceName, b.Amount, p.PaymentMethod " +
                      "FROM PaymentLogs p " +
                      "JOIN Bills b ON p.BillID = b.BillID " +
                      "JOIN Services s ON b.ServiceID = s.ServiceID " +
                      "WHERE p.PatientID = ? " +
                      "ORDER BY p.PaymentDate DESC";
                      
        List<Object[]> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] payment = new Object[5];
                payment[0] = rs.getString("BillID");
                payment[1] = rs.getTimestamp("PaymentDate");
                payment[2] = rs.getString("ServiceName");
                payment[3] = String.format("%,.0f VND", rs.getDouble("Amount"));
                payment[4] = rs.getString("PaymentMethod");
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments.toArray(new Object[0][]);
    }

    /**
     * Lấy số tiền của hóa đơn
     */
    public double getBillAmount(String billId) {
        String query = "SELECT Amount FROM Bills WHERE BillID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, billId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("Amount");
            }
            return 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Lấy tên dịch vụ của hóa đơn
     */
    public String getBillService(String billId) {
        String query = "SELECT s.ServiceName " +
                      "FROM Bills b " +
                      "JOIN Services s ON b.ServiceID = s.ServiceID " +
                      "WHERE b.BillID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, billId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("ServiceName");
            }
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
}