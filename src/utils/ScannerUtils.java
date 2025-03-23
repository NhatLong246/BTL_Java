package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ScannerUtils {

    public int NhapSoNguyen() {
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                int a = scanner.nextInt();
                return a;
            } catch (InputMismatchException e) {
                System.out.println("Yêu cầu nhập vào số nguyên");
                System.out.print("Mời nhập lại: ");

            }
        }
    }

    public double NhapSoThuc() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                double a = sc.nextDouble();
                return a;
            } catch (InputMismatchException e) {
                System.out.println("Yêu cầu nhập vào số!");
                System.out.print("Mời nhập lại: ");
                sc.nextLine();
            }
        }
    }

    public String NhapChuoi() {
        String str = "";
        while (true) {
            try {
                Scanner sc = new Scanner(System.in);
                str = sc.nextLine().trim();

                if (str.isEmpty()) {
                    throw new IllegalArgumentException("Chuỗi không được trống!");
                }
                // sc.close();
                return str;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public LocalDate NhapNgay(){
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                System.out.print("Nhập ngày sinh (dd/MM/yyyy): ");
                String dateInput = scanner.nextLine();
                LocalDate ngay = LocalDate.parse(dateInput, formatter);
                return ngay; // Nếu nhập đúng, thoát vòng lặp
            } catch (DateTimeParseException e) {
                System.out.println("Ngày không hợp lệ! Vui lòng nhập lại theo định dạng dd/MM/yyyy.");
            }
        }
    }

    public double NhapDiem() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                double gpa = sc.nextDouble();

                if (gpa < 0.0 || gpa > 10) {
                    System.out.println("Điểm phải nằm trong khoảng từ 0 -> 10");
                    continue;
                }
                sc.nextLine();
                return gpa;
            } catch (InputMismatchException e) {
                System.out.println("Điểm không hợp lệ!");
                sc.nextLine();
            }
        }
    }

    //hàm nhập tên chuỗi
    public String NhapTen() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                String name = sc.nextLine().trim();
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Tên không được trống!");
                }
                return name;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Kiểm tra định dạng mã bảo hiểm
    public static void validateInsuranceId(String insuranceId) throws IllegalArgumentException {
        if (!insuranceId.matches("[A-Z]{2}-[1-3]-\\d{2}-\\d{10}")) {
            throw new IllegalArgumentException("Mã bảo hiểm không hợp lệ! Định dạng phải là XX-X-XX-XXXXXXXXXX");
        }
    }

    // Kiểm tra ngày hết hạn phải sau ngày bắt đầu
    public static void validateDates(LocalDate startDate, LocalDate expirationDate) throws IllegalArgumentException {
        if (startDate.isAfter(expirationDate)) {
            throw new IllegalArgumentException("Ngày hết hạn phải lớn hơn ngày bắt đầu!");
        }
    }

    // Kiểm tra ngày sinh không thể là tương lai
    public static void validateDateOfBirth(LocalDate dateOfBirth) throws IllegalArgumentException {
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không thể là tương lai!");
        }
    }

    // Kiểm tra giới tính (chỉ chấp nhận Male, Female, Other)
    public static void validateGender(String gender) throws IllegalArgumentException {
        if (!gender.equalsIgnoreCase("Male") && 
            !gender.equalsIgnoreCase("Female") && 
            !gender.equalsIgnoreCase("Other")) {
            throw new IllegalArgumentException("Giới tính không hợp lệ! Chỉ chấp nhận: Male, Female hoặc Other.");
        }
    }

    // Kiểm tra chuỗi không được để trống (dùng cho địa chỉ và nơi đăng ký)
    public static void validateNonEmptyString(String value, String fieldName) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống!");
        }
    }


}

