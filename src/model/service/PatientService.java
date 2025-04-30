package model.service;

import model.entity.Patient;
import model.repository.PatientRepository;

public class PatientService {
    private PatientRepository patientRepository;

    public PatientService() {
        this.patientRepository = new PatientRepository();
    }

    public Patient getPatientByUserId(int userId) {
        // Gọi PatientRepository để lấy thông tin Patient dựa trên userId
        return patientRepository.getPatientByUserId(userId);
    }
}