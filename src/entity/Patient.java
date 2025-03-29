package entity;

import enums.Gender;

import java.time.LocalDate;

public class Patient extends Person{
	private String patientID;
	private LocalDate createdAt; // Ngày tạo (ngày nhập viện)
	private Insurance insurance; // Bảo hiểm của bệnh nhân (nếu có)

	private static int autoId = 1; //Biến đếm ID tự tăng của bệnh nhân

	//Tạo mã bệnh nhân tự động (PAT-001, PAT-002, ...)
	private String generatePatientID() {
		return String.format("PAT-%03d", autoId++);
	}

	public Patient() {}

	public Patient(String fullName, LocalDate dateOfBirth, String address, Gender gender, String phoneNumber, LocalDate createdAt) {
		super(fullName, dateOfBirth, address, gender, phoneNumber);
		this.patientID = generatePatientID();
		this.createdAt = createdAt;
	}

	public String getPatientID() {
		return patientID;
	}

	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}
}