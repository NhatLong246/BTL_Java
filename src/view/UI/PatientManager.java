package view.UI;

import model.entity.Patient;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PatientManager {
    private List<Patient> patients;
    private static final String FILE_NAME = "patients.dat";

    public PatientManager() {
        this.patients = loadFromFile();
    }

    // Thêm bệnh nhân mới
    public void addPatient(Patient patient) {
        patients.add(patient);
        saveToFile();
    }

    // Lấy danh sách bệnh nhân
    public List<Patient> getAllPatients() {
        return patients;
    }

    // Lưu danh sách bệnh nhân vào file
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(patients);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Đọc danh sách bệnh nhân từ file
    private List<Patient> loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Patient>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private List<Patient> patientList = new ArrayList<>();

    public List<Patient> getPatientList() {
        return patientList;
    }

    public boolean removePatientByID(String id) {
        Iterator<Patient> iterator = patientList.iterator();
        while (iterator.hasNext()) {
            Patient patient = iterator.next();
            if (patient.getPatientID().equalsIgnoreCase(id)) {
                iterator.remove();
                return true; // Xóa thành công
            }
        }
        return false; // Không tìm thấy bệnh nhân để xóa
    }
}
