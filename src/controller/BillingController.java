package controller;

import model.entity.Patient;
import model.repository.BillingRepository;
import view.BillingView;
import view.PatientView;

import javax.swing.*;
import java.awt.*;

public class BillingController {
    private final BillingView view;
    private final Patient patient;
    private final BillingRepository repository;

    public BillingController(BillingView view, Patient patient) {
        this.view = view;
        this.patient = patient;
        this.repository = new BillingRepository();
    }

    /**
     * Lấy tổng số tiền của tất cả hóa đơn
     * @param patientId ID của bệnh nhân
     * @return Chuỗi định dạng số tiền (VND)
     */
    public String getTotalBills(String patientId) {
        return repository.getTotalBills(patientId);
    }

    /**
     * Lấy tổng số tiền của các hóa đơn đã thanh toán
     * @param patientId ID của bệnh nhân
     * @return Chuỗi định dạng số tiền (VND)
     */
    public String getPaidBills(String patientId) {
        return repository.getPaidBills(patientId);
    }

    /**
     * Lấy tổng số tiền của các hóa đơn chưa thanh toán
     * @param patientId ID của bệnh nhân
     * @return Chuỗi định dạng số tiền (VND)
     */
    public String getPendingBillsTotal(String patientId) {
        return repository.getPendingBillsTotal(patientId);
    }

    public void showWelcomeMessage() {
        view.setSelectedButton(view.getBtnViewInfo());
        view.showWelcomeMessage();
    }

    public void showViewInfo() {
        view.setSelectedButton(view.getBtnViewInfo());
        view.showViewInfo(repository.getTotalBills(patient.getPatientID()),
                repository.getPaidBills(patient.getPatientID()),
                repository.getPendingBillsTotal(patient.getPatientID()));
    }

    public void showPaymentForm() {
        view.setSelectedButton(view.getBtnPayment());
        view.showPaymentForm();
    }

    public void showPaymentHistory() {
        view.setSelectedButton(view.getBtnHistory());
        view.showPaymentHistory(repository.getPaymentHistory(patient.getPatientID()));
    }

    public void backToPatientUI() {
        new PatientView(patient).setVisible(true);
        view.dispose();
    }

    public boolean processPayment(double amount, String method) {
        return repository.processPayment(patient.getPatientID(), amount, method);
    }

    /**
     * Xử lý thanh toán hóa đơn
     * @param billId ID của hóa đơn
     * @param paymentMethod Phương thức thanh toán
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    public boolean payBill(String billId, String paymentMethod) {
        try {
            // Kiểm tra trạng thái hóa đơn
            if (!repository.isBillPendingPayment(billId)) {
                return false;
            }

            // Cập nhật trạng thái hóa đơn
            boolean success = repository.updateBillStatus(billId, "Đã thanh toán");
            
            if (success) {
                // Ghi log thanh toán
                repository.logPayment(billId, patient.getPatientID(), paymentMethod);
            }
            
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách hóa đơn chưa thanh toán của bệnh nhân
     * @return Mảng 2 chiều chứa thông tin hóa đơn
     */
    public Object[][] getPendingBills() {
        return repository.getPendingBills(patient.getPatientID());
    }

    /**
     * Lấy lịch sử thanh toán của bệnh nhân
     * @return Mảng 2 chiều chứa thông tin thanh toán
     */
    public Object[][] getPaymentHistory() {
        return repository.getPaymentHistory(patient.getPatientID());
    }

    public double getBillAmount(String billId) {
        return repository.getBillAmount(billId);
    }

    public String getBillService(String billId) {
        return repository.getBillService(billId);
    }
}