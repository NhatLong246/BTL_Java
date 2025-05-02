package utils;

import model.entity.Doctor;
import model.entity.Patient;
import model.entity.Appointment;

// Import POI với tên cụ thể
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

// Thay vì java.awt.Color cho PDF
// Thay đổi import cho phiên bản iText mới
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;



public class ReportExporter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static boolean exportDoctorsToExcel(List<Doctor> doctors, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách bác sĩ");
            
            // Tạo font cho tiêu đề
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            
            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Tạo header row
            String[] columns = {"ID Bác sĩ", "Họ và tên", "Email", "Số điện thoại", "Địa chỉ", 
                                "Ngày sinh", "Giới tính", "Chuyên khoa", "Ngày tuyển dụng", "Trạng thái"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Điền dữ liệu
            int rowNum = 1;
            for (Doctor doctor : doctors) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(doctor.getDoctorId());
                row.createCell(1).setCellValue(doctor.getFullName());
                row.createCell(2).setCellValue(doctor.getEmail());
                row.createCell(3).setCellValue(doctor.getPhoneNumber());
                row.createCell(4).setCellValue(doctor.getAddress());
                row.createCell(5).setCellValue(doctor.getDateOfBirth().format(DATE_FORMATTER));
                row.createCell(6).setCellValue(doctor.getGender().getVietnamese());
                row.createCell(7).setCellValue(doctor.getSpecialization().getName());
                row.createCell(8).setCellValue(doctor.getCreatedAt().format(DATE_FORMATTER));
                row.createCell(9).setCellValue("Đang hoạt động"); // Thêm logic kiểm tra trạng thái khi cần
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Ghi file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean exportDoctorsToPdf(List<Doctor> doctors, String filePath) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Thêm tiêu đề - chỉ rõ com.itextpdf.text.Font
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("DANH SÁCH BÁC SĨ", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Space
            
            // Thêm ngày xuất báo cáo
            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
            Paragraph date = new Paragraph("Ngày xuất báo cáo: " + LocalDate.now().format(DATE_FORMATTER), normalFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph(" ")); // Space
            
            // Tạo bảng
            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            
            // Thiết lập header
            String[] headers = {"ID Bác sĩ", "Họ và tên", "Email", "Số điện thoại", 
                              "Địa chỉ", "Ngày sinh", "Giới tính", "Chuyên khoa", "Ngày tuyển dụng"};
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, com.itextpdf.text.Font.BOLD);
            
            // Dùng BaseColor thay cho java.awt.Color
            BaseColor headerBgColor = new BaseColor(66, 133, 244);
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(headerBgColor);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Điền dữ liệu
            com.itextpdf.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
            for (Doctor doctor : doctors) {
                addCellToPdfTable(table, doctor.getDoctorId(), cellFont);
                addCellToPdfTable(table, doctor.getFullName(), cellFont);
                addCellToPdfTable(table, doctor.getEmail(), cellFont);
                addCellToPdfTable(table, doctor.getPhoneNumber(), cellFont);
                addCellToPdfTable(table, doctor.getAddress(), cellFont);
                addCellToPdfTable(table, doctor.getDateOfBirth().format(DATE_FORMATTER), cellFont);
                addCellToPdfTable(table, doctor.getGender().getVietnamese(), cellFont);
                addCellToPdfTable(table, doctor.getSpecialization().getName(), cellFont);
                addCellToPdfTable(table, doctor.getCreatedAt().format(DATE_FORMATTER), cellFont);
            }
            
            document.add(table);
            document.close();
            return true;
            
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    private static void addCellToPdfTable(PdfPTable table, String text, com.itextpdf.text.Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }


    public static boolean exportScheduleToExcel(String doctorId, String doctorName, boolean[][] schedule, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Lịch làm việc");
            
            // Tạo font cho tiêu đề
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            
            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Tạo style cho ô làm việc
            CellStyle workingStyle = workbook.createCellStyle();
            workingStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            workingStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Thông tin bác sĩ
            Row doctorInfoRow = sheet.createRow(0);
            Cell infoCell = doctorInfoRow.createCell(0);
            infoCell.setCellValue("Bác sĩ: " + doctorName + " (ID: " + doctorId + ")");
            CellStyle infoStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font infoFont = workbook.createFont();
            infoFont.setBold(true);
            infoStyle.setFont(infoFont);
            infoCell.setCellStyle(infoStyle);
            
            // Ngày xuất báo cáo
            Row dateRow = sheet.createRow(1);
            dateRow.createCell(0).setCellValue("Ngày xuất: " + LocalDate.now().format(DATE_FORMATTER));
            
            // Header cho lịch
            Row headerRow = sheet.createRow(3);
            headerRow.createCell(0).setCellValue("Ca / Ngày");
            headerRow.getCell(0).setCellStyle(headerStyle);
            
            String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
            for (int i = 0; i < days.length; i++) {
                Cell cell = headerRow.createCell(i + 1);
                cell.setCellValue(days[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Dữ liệu lịch làm việc
            String[] shifts = {"Sáng (7:00-11:30)", "Chiều (13:30-17:00)", "Tối (17:00-7:00)"};
            for (int i = 0; i < shifts.length; i++) {
                Row row = sheet.createRow(4 + i);
                Cell shiftCell = row.createCell(0);
                shiftCell.setCellValue(shifts[i]);
                shiftCell.setCellStyle(headerStyle);
                
                for (int j = 0; j < 7; j++) {
                    Cell cell = row.createCell(j + 1);
                    if (schedule[i][j]) {
                        cell.setCellValue("Làm việc");
                        cell.setCellStyle(workingStyle);
                    } else {
                        cell.setCellValue("Nghỉ");
                    }
                }
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i <= days.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Ghi file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean exportScheduleToPdf(String doctorId, String doctorName, boolean[][] schedule, String filePath) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Thêm tiêu đề
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("LỊCH LÀM VIỆC BÁC SĨ", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Space
            
            // Thông tin bác sĩ
            com.itextpdf.text.Font doctorFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, com.itextpdf.text.Font.BOLD);
            Paragraph doctorInfo = new Paragraph("Bác sĩ: " + doctorName + " (ID: " + doctorId + ")", doctorFont);
            doctorInfo.setAlignment(Element.ALIGN_LEFT);
            document.add(doctorInfo);
            document.add(new Paragraph(" ")); // Space
            
            // Thêm ngày xuất báo cáo
            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
            Paragraph date = new Paragraph("Ngày xuất báo cáo: " + LocalDate.now().format(DATE_FORMATTER), normalFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph(" ")); // Space
            
            // Tạo bảng lịch - THÊM DÒNG NÀY ĐỂ KHỞI TẠO TABLE
            PdfPTable table = new PdfPTable(8); // 1 cột cho tên ca + 7 ngày trong tuần
            table.setWidthPercentage(100);
            
            // Thiết lập header
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, com.itextpdf.text.Font.BOLD);
            // Sử dụng BaseColor thay cho java.awt.Color
            BaseColor headerBgColor = new BaseColor(66, 133, 244);
            BaseColor grayColor = new BaseColor(200, 200, 200);
            BaseColor workingColor = new BaseColor(144, 238, 144); // Light green
            
            PdfPCell headerCell = new PdfPCell(new Phrase("Ca / Ngày", headerFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setBackgroundColor(headerBgColor);
            headerCell.setPadding(5);
            table.addCell(headerCell);
            
            String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
            for (String day : days) {
                headerCell = new PdfPCell(new Phrase(day, headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(headerBgColor);
                headerCell.setPadding(5);
                table.addCell(headerCell);
            }
            
            // Dữ liệu lịch làm việc trong exportScheduleToPdf
            com.itextpdf.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
            String[] shifts = {"Sáng (7:00-11:30)", "Chiều (13:30-17:00)", "Tối (17:00-7:00)"};

            for (int i = 0; i < shifts.length; i++) {
                PdfPCell shiftCell = new PdfPCell(new Phrase(shifts[i], headerFont));
                shiftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                shiftCell.setBackgroundColor(grayColor);
                shiftCell.setPadding(5);
                table.addCell(shiftCell);
                
                for (int j = 0; j < 7; j++) {
                    PdfPCell cell = new PdfPCell(new Phrase(schedule[i][j] ? "Làm việc" : "Nghỉ", cellFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    if (schedule[i][j]) {
                        cell.setBackgroundColor(workingColor);
                    }
                    cell.setPadding(5);
                    table.addCell(cell);
                }
            }
            
            document.add(table);
            document.close();
            return true;
            
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportPatientsToExcel(List<Patient> patients, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách bệnh nhân");
            
            // Tạo font cho tiêu đề
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            
            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Tạo header row
            String[] columns = {"ID Bệnh nhân", "Họ và tên", "Số điện thoại", "Địa chỉ", 
                                "Ngày sinh", "Giới tính"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Điền dữ liệu trong exportPatientsToExcel
            int rowNum = 1;
            for (Patient patient : patients) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(patient.getPatientID());
                row.createCell(1).setCellValue(patient.getFullName());
                row.createCell(2).setCellValue(patient.getPhoneNumber());  // Di chuyển lên vị trí 2 (thay vì 3)
                row.createCell(3).setCellValue(patient.getAddress());      // Di chuyển lên vị trí 3 (thay vì 4)
                if (patient.getDateOfBirth() != null) {
                    row.createCell(4).setCellValue(patient.getDateOfBirth().format(DATE_FORMATTER));  // Vị trí 4
                } else {
                    row.createCell(4).setCellValue("");
                }
                if (patient.getGender() != null) {
                    row.createCell(5).setCellValue(patient.getGender().getVietnamese());  // Vị trí 5
                } else {
                    row.createCell(5).setCellValue("");
                }
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Ghi file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean exportAppointmentsToExcel(List<Appointment> appointments, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách cuộc hẹn");
            
            // Tạo font cho tiêu đề
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            
            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Tạo header row
            String[] columns = {"ID Cuộc hẹn", "ID Bệnh nhân", "Tên bệnh nhân", "ID Bác sĩ", 
                               "Tên bác sĩ", "Ngày hẹn", "Giờ hẹn", "Trạng thái"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            int rowNum = 1; 
            for (Appointment appointment : appointments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(appointment.getAppointmentId());
                row.createCell(1).setCellValue(appointment.getPatientId());
                row.createCell(2).setCellValue(""); 
                row.createCell(3).setCellValue(appointment.getDoctorId());
                row.createCell(4).setCellValue("");
                row.createCell(5).setCellValue(appointment.getAppointmentDate().format(DATE_FORMATTER));
                row.createCell(6).setCellValue(appointment.getAppointmentDate().toLocalTime().toString());
                row.createCell(7).setCellValue(appointment.getStatus().toString());
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Ghi file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Thêm phương thức xuất báo cáo bệnh án 
    public static boolean exportMedicalHistoryToExcel(List<String[]> medicalHistory, String filePath, String patientName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hồ sơ bệnh án - " + patientName);
            
            // Tạo font cho tiêu đề
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            
            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Tạo header row
            String[] columns = {"ID", "Ngày khám", "Bác sĩ", "Chẩn đoán", "Điều trị", "Ghi chú"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Điền dữ liệu
            int rowNum = 1;
            for (String[] record : medicalHistory) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < record.length; i++) {
                    row.createCell(i).setCellValue(record[i] != null ? record[i] : "");
                }
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Thêm thông tin bệnh nhân ở phía trên
            sheet.createRow(rowNum + 1).createCell(0).setCellValue("Thông tin bệnh nhân:");
            Row patientNameRow = sheet.createRow(rowNum + 2);
            patientNameRow.createCell(0).setCellValue("Họ và tên:");
            patientNameRow.createCell(1).setCellValue(patientName);
            
            // Ghi file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Thêm phương thức xuất bệnh án ra PDF
    public static boolean exportMedicalHistoryToPdf(List<String[]> medicalHistory, String filePath, String patientName) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Thêm tiêu đề
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("HỒ SƠ BỆNH ÁN", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Space
            
            // Thêm thông tin bệnh nhân
            com.itextpdf.text.Font patientFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, com.itextpdf.text.Font.BOLD);
            Paragraph patientInfo = new Paragraph("Bệnh nhân: " + patientName, patientFont);
            patientInfo.setAlignment(Element.ALIGN_LEFT);
            document.add(patientInfo);
            document.add(new Paragraph(" ")); // Space
            
            // Thêm ngày xuất báo cáo
            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
            Paragraph date = new Paragraph("Ngày xuất báo cáo: " + LocalDate.now().format(DATE_FORMATTER), normalFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph(" ")); // Space
            
            // Tạo bảng
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            
            // Thiết lập header
            String[] headers = {"ID", "Ngày khám", "Bác sĩ", "Chẩn đoán", "Điều trị", "Ghi chú"};
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, com.itextpdf.text.Font.BOLD);
            
            // Dùng BaseColor thay cho java.awt.Color
            BaseColor headerBgColor = new BaseColor(66, 133, 244);
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(headerBgColor);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Điền dữ liệu
            com.itextpdf.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
            for (String[] record : medicalHistory) {
                for (String value : record) {
                    addCellToPdfTable(table, value != null ? value : "", cellFont);
                }
            }
            
            document.add(table);
            document.close();
            return true;
            
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Thêm phương thức xuất bệnh nhân ra PDF nếu cần
    public static boolean exportPatientsToPdf(List<Patient> patients, String filePath) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Thêm tiêu đề
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("DANH SÁCH BỆNH NHÂN", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Space
            
            // Thêm ngày xuất báo cáo
            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
            Paragraph date = new Paragraph("Ngày xuất báo cáo: " + LocalDate.now().format(DATE_FORMATTER), normalFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph(" ")); // Space
            
            // Tạo bảng
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            
            // Thiết lập header
            String[] headers = {"ID Bệnh nhân", "Họ và tên", "Số điện thoại", "Địa chỉ", "Ngày sinh", "Giới tính"};
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, com.itextpdf.text.Font.BOLD);
            
            // Dùng BaseColor thay cho java.awt.Color
            BaseColor headerBgColor = new BaseColor(66, 133, 244);
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(headerBgColor);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Điền dữ liệu
            com.itextpdf.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
            for (Patient patient : patients) {
                addCellToPdfTable(table, patient.getPatientID(), cellFont);
                addCellToPdfTable(table, patient.getFullName(), cellFont);
                addCellToPdfTable(table, patient.getPhoneNumber(), cellFont);
                addCellToPdfTable(table, patient.getAddress(), cellFont);
                if (patient.getDateOfBirth() != null) {
                    addCellToPdfTable(table, patient.getDateOfBirth().format(DATE_FORMATTER), cellFont);
                } else {
                    addCellToPdfTable(table, "", cellFont);
                }
                if (patient.getGender() != null) {
                    addCellToPdfTable(table, patient.getGender().getVietnamese(), cellFont);
                } else {
                    addCellToPdfTable(table, "", cellFont);
                }
            }
            
            document.add(table);
            document.close();
            return true;
            
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
