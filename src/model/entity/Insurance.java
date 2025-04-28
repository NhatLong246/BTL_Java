package model.entity;

import model.enums.InsuranceStatus;
import utils.ScannerUtils;
import java.time.LocalDate;
import java.util.Scanner;


//lớp Insurance (Bảo hiểm)
public class Insurance {
    private String insuranceId; // Mã bảo hiểm
    private String patientId; // Mã bệnh nhân (liên kết với Patient)
    private String provider; // Nhà cung cấp bảo hiểm
    private String policyNumber; // Số hợp đồng
    private LocalDate startDate; // Ngày bắt đầu
    private LocalDate expirationDate; // Ngày hết hạn
    private InsuranceStatus status; // Trạng thái bảo hiểm (Hoạt Động, Hết Hạn, Không Xác Định)


    public Insurance() {}

    public Insurance(String insuranceId, String patientId, String provider, String policyNumber, LocalDate startDate, LocalDate expirationDate, InsuranceStatus status) {
        this.insuranceId = insuranceId;
        this.patientId = patientId;
        this.provider = provider;
        this.policyNumber = policyNumber;
        this.startDate = startDate;
        this.expirationDate = expirationDate;
        this.status = status;
    }


    public void inputInsuranceData() {
        Scanner scanner = new Scanner(System.in);
        String regex = "^[A-Z]{2}[1-5]\\d{2}\\d{10}$"; // Định dạng mã bảo hiểm

        // Nhập mã bảo hiểm
        do {
            System.out.print("Nhập mã bảo hiểm y tế (hoặc nhấn Enter nếu không có): ");
            insuranceId = scanner.nextLine().trim();

            if (insuranceId.isEmpty()) {
                insuranceId = null;
                break;
            }

            if (!insuranceId.matches(regex)) {
                System.out.println("Mã bảo hiểm không hợp lệ! Hãy nhập lại theo định dạng DN-2-10-1234567890.");
            }
        } while (insuranceId != null && !insuranceId.matches(regex));

        // Nhập mã bệnh nhân (không để trống)
        do {
            System.out.print("Nhập mã bệnh nhân: ");
            patientId = scanner.nextLine().trim();
        } while (patientId.isEmpty());

        // Nhập nhà cung cấp bảo hiểm
        do {
            System.out.print("Nhập nhà cung cấp bảo hiểm: ");
            provider = scanner.nextLine().trim();
        } while (provider.isEmpty());

        // Nhập số hợp đồng bảo hiểm
        do {
            System.out.print("Nhập số hợp đồng bảo hiểm: ");
            policyNumber = scanner.nextLine().trim();
        } while (policyNumber.isEmpty());

        // Nhập ngày bắt đầu
        while (true) {
            try {
                System.out.print("Nhập ngày bắt đầu (yyyy-MM-dd): ");
                startDate = LocalDate.parse(scanner.nextLine().trim());
                break;
            } catch (Exception e) {
                System.out.println("Lỗi: Ngày không hợp lệ! Vui lòng nhập lại.");
            }
        }

        // Nhập ngày hết hạn (phải sau ngày bắt đầu)
        while (true) {
            try {
                System.out.print("Nhập ngày hết hạn (yyyy-MM-dd): ");
                expirationDate = LocalDate.parse(scanner.nextLine().trim());

                if (expirationDate.isBefore(startDate)) {
                    System.out.println("Lỗi: Ngày hết hạn phải lớn hơn ngày bắt đầu!");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Lỗi: Ngày không hợp lệ! Vui lòng nhập lại.");
            }
        }

        // Nhập trạng thái bảo hiểm (dùng Enum)
        while (true) {
            System.out.print("Nhập trạng thái bảo hiểm (Hoạt Động, Hết Hạn, Không Xác Định): ");
            String statusInput = scanner.nextLine().trim();
            try {
                status = InsuranceStatus.fromString(statusInput);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi: " + e.getMessage() + " Vui lòng nhập lại.");
            }
        }
    }


    public boolean isValidInsurance() {
        try {
            ScannerUtils.validateInsuranceId(insuranceId);
            ScannerUtils.validateDates(startDate, expirationDate);
            ScannerUtils.validateNonEmptyString(provider, "Nhà cung cấp bảo hiểm");
            ScannerUtils.validateNonEmptyString(policyNumber, "Số hợp đồng bảo hiểm");

            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
            return false;
        }
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public InsuranceStatus getStatus() {
        return status;
    }

    public void setStatus(InsuranceStatus status) {
        this.status = status;
    }
}

