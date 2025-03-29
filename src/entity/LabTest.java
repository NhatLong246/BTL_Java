package entity;

//lớp LabTest (Xét nghiệm)
public class LabTest {
    private String labTestId; //Mã xét nghiệm
    private String labTestName; //Tên xét nghiệm
    private String Description; //Mô tả (mục đích, phương pháp thực hiện)
    private double cost; //Chi phí xét nghiệm

    private static int autoId = 1; //Biến đếm ID tự tăng của xét nghiệm

    //tạo mã xét nghiệm tự động (LAB-001, LAB-002, ...)
    private String generateLabTestId() {
        return String.format("LAB-%03d", autoId++);
    }

    public LabTest() {}

    public LabTest(String labTestName, String Description, double cost) {
        this.labTestId = generateLabTestId();
        this.labTestName = labTestName;
        this.Description = Description;
        this.cost = cost;
    }

    public String getLabTestId() {
        return labTestId;
    }

    public void setLabTestId(String labTestId) {
        this.labTestId = labTestId;
    }

    public String getLabTestName() {
        return labTestName;
    }

    public void setLabTestName(String labTestName) {
        this.labTestName = labTestName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
