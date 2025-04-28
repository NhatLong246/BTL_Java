package controller;

import model.entity.Patient;
import model.repository.PatientRepository;
import view.PatientView;

import java.sql.SQLException;
import java.util.List;

public class PatientController {
    private final PatientView view;
    private final Patient patient;
    private final PatientRepository repository;

    public PatientController(PatientView view, Patient patient) throws SQLException {
        this.view = view;
        this.patient = patient;
        this.repository = new PatientRepository();
    }

    public void showHome() {
        view.setSelectedButton(view.getBtnHome());
        view.showHome();
    }

    public void showPatientInfo() {
        view.setSelectedButton(view.getBtnViewInfo());
        view.showPatientInfo();
    }

    public void showAppointments() {
        view.setSelectedButton(view.getBtnViewAppointments());
        view.showAppointments(repository.getAppointments(patient.getPatientID()));
    }

    public void showMedicalHistory() {
        view.setSelectedButton(view.getBtnViewMedicalHistory());
        view.showMedicalHistory(repository.getMedicalHistory(patient.getPatientID()));
    }

    public void showPayFees() {
        view.setSelectedButton(view.getBtnPayFees());
        view.showPayFees();
    }

    public void showPaymentHistory() {
        view.setSelectedButton(view.getBtnPaymentHistory());
        view.showPaymentHistory(repository.getPaymentHistory(patient.getPatientID()));
    }

    public List<Object[]> getBills() {
        return repository.getBills(patient.getPatientID());
    }

    public boolean payBill(String billID, String paymentMethod) {
        return repository.payBill(billID, paymentMethod);
    }
}