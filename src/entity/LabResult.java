package entity;

//lớp LabResult (Kết quả xét nghiệm)
public class LabResult {
    private String labResultId; //Mã kết quả xét nghiệm
    private Patient patient; //Bệnh nhân
    private Doctor doctor; //Bác sĩ
    private LabTest labTest; //Xét nghiệm
    private String result; //Kết quả xét nghiệm
    private String testDate; //Ngày xét nghiệm

    private static int autoId = 1; //Biến đếm ID tự tăng của kết quả xét nghiệm

    //tạo mã kết quả xét nghiệm tự động (RES-001, RES-002, ...)
    private String generateLabResultId() {
        return String.format("RES-%03d", autoId++);
    }

    public LabResult() {}

    public LabResult(Patient patient, Doctor doctor, LabTest labTest, String result, String testDate) {
        this.labResultId = generateLabResultId();
        this.patient = patient;
        this.doctor = doctor;
        this.labTest = labTest;
        this.result = result;
        this.testDate = testDate;
    }

    public String getLabResultId() {
        return labResultId;
    }

    public void setLabResultId(String labResultId) {
        this.labResultId = labResultId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LabTest getLabTest() {
        return labTest;
    }

    public void setLabTest(LabTest labTest) {
        this.labTest = labTest;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }
}
