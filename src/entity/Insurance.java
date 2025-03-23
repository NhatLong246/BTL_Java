package entity;

import utils.ScannerUtils;
import java.time.LocalDate;


//lớp Insurance (Bảo hiểm)
public class Insurance {
    private String insuranceId; //Mã bảo hiểm
    private Patient patient; //Bệnh nhân
    private String policyNumber; //Số hợp đồng
    private LocalDate startDate; //Ngày bắt đầu
    private LocalDate expirationDate; //Ngày hết hạn
    private LocalDate dateOfBirth;
    private String address;
    private String gender;
    private String registrationLocation;


    public Insurance() {}

    public boolean isValidInsurance() {
        try {
            ScannerUtils.validateInsuranceId(insuranceId);
            ScannerUtils.validateDates(startDate, expirationDate);
            ScannerUtils.validateDateOfBirth(dateOfBirth);
            ScannerUtils.validateGender(gender);
            ScannerUtils.validateNonEmptyString(address, "Địa chỉ");
            ScannerUtils.validateNonEmptyString(registrationLocation, "Nơi đăng ký khám chữa bệnh");

            return true; // Không có lỗi, trả về true
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
            return false;
        }
    }


}
