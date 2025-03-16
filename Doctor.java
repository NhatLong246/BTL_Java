package entity;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Doctor extends Person{
    private static int id = 1;
    private String doctorId;
    private Specialization specialization;  // Chuyên khoa
    private int yearsOfExperience;  // Số năm kinh nghiệm
    private String email;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public Doctor() {}

    public Doctor(String name, LocalDate birthDate, String address, Gender gender, String phoneNumber, String email, int yearsOfExperience, Specialization specialization, String doctorId) {
        super(name, birthDate, address, gender, phoneNumber);
        setEmail(email);
        setYearsOfExperience(yearsOfExperience);
        this.specialization = specialization;
        this.doctorId = generateDoctorId();
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

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Số năm kinh nghiệm không thể âm!");
        }
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email không hợp lệ! Vui lòng nhập đúng định dạng.");
        }
        this.email = email;
    }
}
