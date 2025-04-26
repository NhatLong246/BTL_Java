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

    public void showWelcomeMessage() {
        view.setSelectedButton(view.getBtnViewInfo());
        view.showWelcomeMessage();
    }

    public void showViewInfo() {
        view.setSelectedButton(view.getBtnViewInfo());
        view.showViewInfo(repository.getTotalBills(patient.getPatientID()),
                repository.getPaidBills(patient.getPatientID()),
                repository.getPendingBills(patient.getPatientID()));
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
}