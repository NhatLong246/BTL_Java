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
            // Sửa từ bills sang Billing và Amount sang TotalAmount
            String query = "SELECT SUM(TotalAmount) as TotalAmount FROM Billing WHERE PatientID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double amount = rs.getDouble("TotalAmount");
                return String.format("%,.0f VND", amount > 0 ? amount : 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0 VND";
    }

    public String getPaidBills(String patientId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Sửa từ bills sang Billing và Amount sang TotalAmount
            String query = "SELECT SUM(TotalAmount) as TotalAmount FROM Billing WHERE PatientID = ? AND Status = 'Đã thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double amount = rs.getDouble("TotalAmount");
                return String.format("%,.0f VND", amount > 0 ? amount : 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0 VND";
    }

    public String getPendingBillsTotal(String patientId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Sửa từ bills sang Billing và Amount sang TotalAmount
            String query = "SELECT SUM(TotalAmount) as TotalAmount FROM Billing WHERE PatientID = ? AND Status = 'Chưa thanh toán'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double amount = rs.getDouble("TotalAmount");
                return String.format("%,.0f VND", amount > 0 ? amount : 0);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tổng hóa đơn chưa thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
        return "0 VND";
    }

    public boolean processPayment(String patientId, double amount, String method) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String billID = "BILL" + System.currentTimeMillis();
            // Sửa từ Bills sang Billing và Amount sang TotalAmount
            String query = "INSERT INTO Billing (BillID, PatientID, TotalAmount, PaymentMethod, Status, CreatedAt) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            
            stmt.setString(1, billID);
            stmt.setString(2, patientId);
            stmt.setDouble(3, amount);
            stmt.setString(4, method);
            stmt.setString(5, "Đã thanh toán");
            stmt.setTimestamp(6, now);
    
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
        // Sửa từ Bills sang Billing
        String query = "SELECT Status FROM Billing WHERE BillID = ?";
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
        // Sửa từ Bills sang Billing và loại bỏ UpdatedAt (vì bảng Billing không có)
        String query = "UPDATE Billing SET Status = ? WHERE BillID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setString(2, billId);
            
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
        // Sửa từ Bills sang Billing và Amount sang TotalAmount
        String query = "SELECT b.BillID, b.CreatedAt, s.ServiceName, b.TotalAmount, b.Status " +
                      "FROM Billing b " +
                      "LEFT JOIN BillingDetails bd ON b.BillID = bd.BillID " +
                      "LEFT JOIN Services s ON bd.ServiceID = s.ServiceID " +
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
                bill[2] = rs.getString("ServiceName") != null ? rs.getString("ServiceName") : "Dịch vụ khám bệnh";
                bill[3] = String.format("%,.0f VND", rs.getDouble("TotalAmount"));
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
        // Sửa lại truy vấn để phù hợp với bảng PaymentLogs (nếu tồn tại) hoặc Billing
        String query = "SELECT b.BillID, b.CreatedAt, s.ServiceName, b.TotalAmount, b.PaymentMethod " +
                      "FROM Billing b " +
                      "LEFT JOIN BillingDetails bd ON b.BillID = bd.BillID " +
                      "LEFT JOIN Services s ON bd.ServiceID = s.ServiceID " +
                      "WHERE b.PatientID = ? AND b.Status = 'Đã thanh toán' " +
                      "ORDER BY b.CreatedAt DESC";
                      
        List<Object[]> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] payment = new Object[5];
                payment[0] = rs.getString("BillID");
                payment[1] = rs.getTimestamp("CreatedAt");
                payment[2] = rs.getString("ServiceName") != null ? rs.getString("ServiceName") : "Dịch vụ khám bệnh";
                payment[3] = String.format("%,.0f VND", rs.getDouble("TotalAmount"));
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
        // Sửa từ Bills sang Billing và Amount sang TotalAmount
        String query = "SELECT TotalAmount FROM Billing WHERE BillID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, billId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("TotalAmount");
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
        // Sửa từ Bills sang Billing và chỉnh sửa JOIN
        String query = "SELECT s.ServiceName " +
                      "FROM Billing b " +
                      "LEFT JOIN BillingDetails bd ON b.BillID = bd.BillID " +
                      "LEFT JOIN Services s ON bd.ServiceID = s.ServiceID " +
                      "WHERE b.BillID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, billId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String serviceName = rs.getString("ServiceName"); 
                return serviceName != null ? serviceName : "Dịch vụ khám bệnh";
            }
            return "Dịch vụ khám bệnh";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Dịch vụ khám bệnh";
        }
    }
}