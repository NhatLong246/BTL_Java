package controller;

import model.entity.Patient;
import model.enums.Gender;
import model.repository.DoctorRepository;
import view.DoctorView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DoctorController {
    private final DoctorView view;
    private final DoctorRepository repository;
    private final String doctorId;

    public DoctorController(DoctorView view, String doctorId) {
        this.view = view;
        this.repository = new DoctorRepository();
        this.doctorId = doctorId;
    }

    public void showHome() {
        view.setSelectedButton(view.getBtnHome());
        view.showHome();
    }

    public void showAddPatientForm() {
        view.setSelectedButton(view.getBtnAdd());
        view.showAddPatientForm();
    }

    public void showPatientList() {
        view.setSelectedButton(view.getBtnView());
        view.showPatientList(repository.getAllPatients());
    }

    public void showBookAppointment() {
        view.setSelectedButton(view.getBtnBook());
        view.showBookAppointment();
    }

    public void showDeletePatientForm() {
        view.setSelectedButton(view.getBtnDel());
        view.showDeletePatientForm();
    }

    public void addPatient(String name, String birthDateStr, String address, String phone, Gender gender, String medicalHistory) {
        if (name.isEmpty() || birthDateStr.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            String userId = "USER" + System.currentTimeMillis();

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/PatientManagement?useSSL=false&serverTimezone=UTC", "root", "2005")) {
                Patient patient = new Patient(userId, name, birthDate, address, gender, phone, conn);

                if (repository.addPatient(patient, medicalHistory)) {
                    JOptionPane.showMessageDialog(view, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    showHome();
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to add patient!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(view, "Database connection error!", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Invalid date format (YYYY-MM-DD)!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void bookAppointment(String patientId, String dateStr) {
        if (patientId.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter both Patient ID and Date!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate appointmentDate = LocalDate.parse(dateStr);
            if (repository.bookAppointment(patientId, appointmentDate, doctorId)) {
                JOptionPane.showMessageDialog(view, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showHome();
            } else {
                JOptionPane.showMessageDialog(view, "Failed to book appointment!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Invalid date format (YYYY-MM-DD)!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deletePatient(String patientId) {
        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter Patient ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to delete patient " + patientId + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (repository.deletePatient(patientId)) {
                JOptionPane.showMessageDialog(view, "Patient deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showHome();
            } else {
                JOptionPane.showMessageDialog(view, "Failed to delete patient!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}