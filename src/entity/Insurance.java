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




    public String inputInsuranceId() {
        Scanner scanner = new Scanner(System.in);
        String insuranceId;
        String regex = "^[A-Z]{2}[1-5]\\d{2}\\d{10}$"; // Định dạng mã BHYT

        while (true) {
            System.out.print("Nhập mã bảo hiểm y tế (hoặc nhấn Enter nếu không có): ");
            insuranceId = scanner.nextLine().trim();

            if (insuranceId.isEmpty()) {
                System.out.println("Bạn đã chọn không có bảo hiểm.");
                return "Không có bảo hiểm"; // Hoặc có thể là null
            }

            if (insuranceId.matches(regex)) {
                return insuranceId; // Mã hợp lệ
            } else {
                System.out.println("⚠️ Mã bảo hiểm không hợp lệ! Hãy nhập lại theo định dạng DN-2-10-1234567890.");
            }
        }
    }


}
