package entity;

public class MedicalHistory { //Tiền sử bệnh
    private String historyId; //Mã tiền sử bệnh
    private String patient; //Bệnh nhân
    private String diagnosis; //Chuẩn đoán trước đó
    private String treatment; //Điều trị trước đó
    private String recordDate; //Ngày cập nhật

    private static int autoId = 1; //Biến đếm ID tự tăng của tiền sử bệnh

    //tạo mã tiền sử bệnh tự động (HIS-001, HIS-002, ...)
    private String generateHistoryId() {
        return String.format("HIS-%03d", autoId++);
    }

    public MedicalHistory() {}

    public MedicalHistory(String patient, String diagnosis, String treatment, String recordDate) {
        this.historyId = generateHistoryId();
        this.patient = patient;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.recordDate = recordDate;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
}
