package entity;

import java.time.LocalDate;

public class Patient extends Person{
	
	private static int autoID =1;
	private String patientID;
	private String medicalHistory;
	public String getID() {
		return patientID;
	}
	public void setID(String iD) {
		this.patientID = iD;
	}
	public String getMedicalHistory() {
		return medicalHistory;
	}
	public void setMedicalHistory(String medicalHistory) {
		this.medicalHistory = medicalHistory;
	}
	
	private String generatepatientID() {
        return String.format("PAT-%03d", autoID++);
    }
	
	public Patient(String medicalHistory) {
		super();
		patientID=generatepatientID();
		this.medicalHistory = medicalHistory;
	}
	@Override
	public String toString() {
		return "Patient [ID=" + patientID  
				+ ", name=" + name 
				+ ", birthDate=" + birthDate 
				+ ", address=" + address 
				+ ", gender=" + gender 
				+ ", phoneNumber=" + phoneNumber 
				+ ", medicalHistory=" + medicalHistory+"]";
	}
	
	
	
}
