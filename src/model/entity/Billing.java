package model.entity;

import model.enums.PaymentMethod;
import model.enums.PaymentStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Lớp Billing (Hóa đơn)
public class Billing {
    private String billId; // Mã hóa đơn
    private Patient patient; // Bệnh nhân
    private double totalAmount; // Tổng tiền
    private PaymentMethod paymentMethod; // Phương thức thanh toán
    private PaymentStatus paymentStatus; // Trạng thái thanh toán
    private LocalDateTime createdAt; // Ngày tạo hóa đơn
    private List<BillingDetail> billingDetails; // Danh sách chi tiết hóa đơn

    private static int autoId = 1; // Biến đếm ID tự tăng của hóa đơn

    // Tạo mã hóa đơn tự động (BIL-001, BIL-002, ...)
    private String generateNewBillID(Connection conn) {
        String newBillID = "BIL-001"; // ID mặc định nếu bảng rỗng
        String sql = "SELECT MAX(CAST(SUBSTRING(BillID, 5, LENGTH(BillID) - 4) AS UNSIGNED)) AS maxID FROM Billing";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next() && rs.getInt("maxID") > 0) {
                int maxID = rs.getInt("maxID") + 1; // Lấy số lớn nhất +1
                newBillID = String.format("BIL-%03d", maxID); // Định dạng BIL-XXX
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newBillID;
    }

    public Billing() {}

    // Constructor cho hóa đơn mới (Tạo mới ID) -- Dùng cho việc thêm mới hóa đơn
    public Billing(Connection conn, Patient patient, double totalAmount, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
        this.billId = generateNewBillID(conn);
        this.patient = patient;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor cho hóa đơn từ database (ID đã có, không tạo mới) -- Dùng cho việc sửa thông tin hóa đơn
    public Billing(String billId, Patient patient, double totalAmount, PaymentMethod paymentMethod, PaymentStatus paymentStatus, LocalDateTime createdAt) {
        this.billId = billId;
        this.patient = patient;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BillingDetail> getBillingDetails() {
        return billingDetails;
    }

    public void setBillingDetails(List<BillingDetail> billingDetails) {
        this.billingDetails = billingDetails;
    }
}
