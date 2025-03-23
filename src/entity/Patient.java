package entity;

import java.time.LocalDate;

public class Patient extends Person{
	
	private static int autoID =1;
	private String patientID;
	private String medicalHistory; //Tiền sử bệnh

	public Patient() {}

	public Patient(String medicalHistory) {
		super();
		patientID=generatepatientID();
		this.medicalHistory = medicalHistory;
	}

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

}
