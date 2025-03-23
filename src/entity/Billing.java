package entity;

import enums.PaymentMethod;
import enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;

// Lớp Billing (Hóa đơn)
public class Billing {
    private String billingId; // Mã hóa đơn
    private Patient patient; // Bệnh nhân
    private double totalAmount; // Tổng tiền
    private PaymentMethod paymentMethod; // Phương thức thanh toán
    private PaymentStatus paymentStatus; // Trạng thái thanh toán
    private LocalDate createdAt; // Ngày tạo hóa đơn
    private List<BillingDetail> billingDetails; // Danh sách chi tiết hóa đơn

    private static int autoId = 1; // Biến đếm ID tự tăng của hóa đơn

    // Tạo mã hóa đơn tự động (BIL-001, BIL-002, ...)
    private String generateBillingId() {
        return String.format("BIL-%03d", autoId++);
    }

    public Billing() {}

    public Billing(Patient patient, double totalAmount, PaymentMethod paymentMethod, PaymentStatus paymentStatus, LocalDate createdAt) {
        this.billingId = generateBillingId();
        this.patient = patient;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    public String getBillingId() {
        return billingId;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId;
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
