package entity;

import enums.InsuranceProvider;

import java.time.LocalDate;

//lớp Insurance (Bảo hiểm)
public class Insurance {
    private String insuranceId; //Mã bảo hiểm
    private Patient patient; //Bệnh nhân
    private InsuranceProvider provider; //Nhà cung cấp bảo hiểm
    private String policyNumber; //Số hợp đồng
    private LocalDate startDate; //Ngày bắt đầu
    private LocalDate expirationDate; //Ngày hết hạn
    private String coverageDetails; //Chi tiết quyền lợi bảo hiểm


    public Insurance() {}


    public boolean isValidInsurance() {
        if (!insuranceId.matches("[A-Z]{2}-[1-3]-\\d{2}-\\d{10}")) {
            System.out.println("Mã bảo hiểm không hợp lệ!");
            return false;
        }
        if (startDate.isAfter(expirationDate)) {
            System.out.println("Ngày hết hạn phải lớn hơn ngày bắt đầu!");
            return false;
        }
        return true;
    }


}
