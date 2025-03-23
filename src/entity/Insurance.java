package entity;

import enums.InsuranceProvider;

import java.time.LocalDate;
import java.util.Scanner;

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


}
