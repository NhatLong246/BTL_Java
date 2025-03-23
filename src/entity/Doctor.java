package entity;

import enums.Gender;
import enums.Specialization;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Doctor extends Person {
    private static int id = 1;
    private String doctorId;
    private Specialization specialization;  // Chuyên khoa
    private String email;
    private String createdAt; // Ngày tạo (tuyển dụng)

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public Doctor() {}

    public Doctor(String fullName, LocalDate dateOfBirth, String address, Gender gender, String phoneNumber, Specialization specialization, String email, String createdAt) {
        super(fullName, dateOfBirth, address, gender, phoneNumber);
        this.doctorId = generateDoctorId();
        this.specialization = specialization;
        setEmail(email);
        this.createdAt = createdAt;
    }

    private String generateDoctorId() {
        return String.format("DOC-%03d", id++);
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email không hợp lệ! Vui lòng nhập đúng định dạng.");
        }
        this.email = email;
    }
}
