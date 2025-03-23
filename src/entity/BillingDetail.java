package entity;

// Lớp BillingDetail (Chi tiết hóa đơn)
public class BillingDetail {
    private String billingDetailId; // Mã chi tiết hóa đơn
    private Billing billing; // Hóa đơn
    private String serviceType; // Loại dịch vụ (Appointment, Prescription, LabTest)
    private String serviceId; // Mã dịch vụ của dịch vụ tương ứng (AppointmentId, PrescriptionId, LabTestId)
    private double amount; // Số tiền của dịch vụ

    private static int autoId = 1; // Biến đếm ID tự tăng của chi tiết hóa đơn

    // Tạo mã chi tiết hóa đơn tự động (BID-001, BID-002, ...)
    private String generateBillingDetailId() {
        return String.format("BID-%03d", autoId++);
    }

    public BillingDetail() {}

    public BillingDetail(Billing billing, String serviceType, String serviceId, double amount) {
        this.billingDetailId = generateBillingDetailId();
        this.billing = billing;
        this.serviceType = serviceType;
        this.serviceId = serviceId;
        this.amount = amount;
    }

    public String getBillingDetailId() {
        return billingDetailId;
    }

    public void setBillingDetailId(String billingDetailId) {
        this.billingDetailId = billingDetailId;
    }

    public Billing getBilling() {
        return billing;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
