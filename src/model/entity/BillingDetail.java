package model.entity;

import java.math.BigDecimal;

// Lớp BillingDetail (Chi tiết hóa đơn)
public class BillingDetail {
    private String billId;  // ID hóa đơn
    private String serviceId; // ID của dịch vụ
    private BigDecimal amount; // Số tiền

    public BillingDetail() {}

    public BillingDetail(String billId, String serviceId, BigDecimal amount) {
        this.billId = billId;
        this.serviceId = serviceId;
        this.amount = amount;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
