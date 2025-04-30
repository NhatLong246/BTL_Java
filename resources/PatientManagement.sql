DROP DATABASE IF EXISTS PatientManagement;
CREATE DATABASE IF NOT EXISTS PatientManagement;
USE PatientManagement;

-- Bảng Tài Khoản Người Dùng
CREATE TABLE UserAccounts (
    UserID VARCHAR(50) PRIMARY KEY,
    UserName VARCHAR(50) UNIQUE NOT NULL,
    FullName VARCHAR(100) NOT NULL,
    Role ENUM('Bác sĩ', 'Bệnh nhân', 'Quản lí') NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    PhoneNumber VARCHAR(15) UNIQUE,
    PasswordHash VARCHAR(255) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role (Role)
);
-- Thêm cột IsLocked với giá trị mặc định là 0 (không khóa)
ALTER TABLE UserAccounts
ADD IsLocked TINYINT(1) NOT NULL DEFAULT 0;

-- Cập nhật dữ liệu hiện có: nếu PasswordHash là NULL, đặt IsLocked = 1
UPDATE UserAccounts
SET IsLocked = 1
WHERE PasswordHash IS NULL;

-- Đảm bảo PasswordHash không NULL cho các tài khoản hiện có
UPDATE UserAccounts
SET PasswordHash = SHA2('default_password', 256)
WHERE PasswordHash IS NULL;
-- Bảng Chuyên Khoa
CREATE TABLE Specialties (
    SpecialtyID VARCHAR(50) PRIMARY KEY,
    SpecialtyName VARCHAR(100) NOT NULL UNIQUE
);

-- Bảng Bác Sĩ
CREATE TABLE Doctors (
    DoctorID VARCHAR(50) PRIMARY KEY,
    UserID VARCHAR(50) UNIQUE NOT NULL,
    DateOfBirth DATE NOT NULL,
    Gender ENUM('Nam', 'Nữ') NOT NULL,
    Address TEXT,
    SpecialtyID VARCHAR(50),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (SpecialtyID) REFERENCES Specialties(SpecialtyID) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_specialty (SpecialtyID)
);

-- Bảng lịch làm việc cho bác sĩ 
CREATE TABLE DoctorSchedule (
    ScheduleID VARCHAR(50) PRIMARY KEY,
    DoctorID VARCHAR(50) NOT NULL,
    DayOfWeek ENUM('Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy', 'Chủ Nhật') NOT NULL,
    ShiftType ENUM('Sáng', 'Chiều', 'Tối') NOT NULL,
    Status ENUM('Đang làm việc', 'Hết ca làm việc') DEFAULT 'Đang làm việc',
    UNIQUE KEY unique_doctor_schedule (DoctorID, DayOfWeek, ShiftType),
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bảng Bệnh Nhân
CREATE TABLE Patients (
    PatientID VARCHAR(50) PRIMARY KEY,
    UserID VARCHAR(50) UNIQUE NOT NULL,
    FullName VARCHAR(255) NOT NULL,
    DateOfBirth DATE NOT NULL,
    Gender ENUM('Nam', 'Nữ') NOT NULL,
    PhoneNumber VARCHAR(20),
    Address TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bảng Cuộc Hẹn
CREATE TABLE Appointments (
    AppointmentID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    DoctorID VARCHAR(50),
    AppointmentDate DATETIME NOT NULL,
    Status ENUM('Chờ xác nhận', 'Hoàn thành', 'Hủy') DEFAULT 'Chờ xác nhận',
    Notes TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_appointment_date (AppointmentDate),
    INDEX idx_appointment_status (Status)
);

-- Bảng Dịch Vụ
CREATE TABLE Services (
    ServiceID VARCHAR(50) PRIMARY KEY,
    ServiceName VARCHAR(255) NOT NULL,
    Cost DECIMAL(10,2) NOT NULL CHECK (Cost >= 0),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Hóa Đơn
CREATE TABLE Billing (
    BillID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    TotalAmount DECIMAL(10,2) NOT NULL CHECK (TotalAmount >= 0),
    PaymentMethod ENUM('Tiền mặt', 'Chuyển khoản'),
    Status ENUM('Chưa thanh toán', 'Đã thanh toán') DEFAULT 'Chưa thanh toán',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_billing_status (Status)
);

-- Bảng Chi Tiết Hóa Đơn
CREATE TABLE BillingDetails (
    BillDetailID VARCHAR(50) PRIMARY KEY,
    BillID VARCHAR(50) NOT NULL,
    ServiceID VARCHAR(50) NOT NULL,
    Amount DECIMAL(10,2) NOT NULL CHECK (Amount >= 0),
    UNIQUE KEY unique_bill_service (BillID, ServiceID),
    FOREIGN KEY (BillID) REFERENCES Billing(BillID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bảng Hồ Sơ Y Tế
CREATE TABLE MedicalRecords (
    RecordID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    Diagnosis TEXT NOT NULL,
    TreatmentPlan TEXT NOT NULL,
    RecordDate DATE NOT NULL,
    IsHistory BOOLEAN DEFAULT FALSE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_record_date (RecordDate)
);

-- Bảng Chỉ Số Sức Khỏe
CREATE TABLE VitalSigns (
    VitalSignID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    Temperature DECIMAL(4,1) CHECK (Temperature BETWEEN 30 AND 45),
    BloodPressure VARCHAR(20),
    HeartRate INT CHECK (HeartRate > 0),
    OxygenSaturation DECIMAL(5,2) CHECK (OxygenSaturation BETWEEN 0 AND 100),
    RecordedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_recorded_at (RecordedAt)
);

-- Bảng Đơn thuốc
CREATE TABLE Prescriptions (
    PrescriptionID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    PrescriptionDate DATE NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_prescription_date (PrescriptionDate)
);

-- Bảng Thuốc
CREATE TABLE Medications (
    MedicationID VARCHAR(50) PRIMARY KEY,
    MedicineName VARCHAR(255) NOT NULL,
    Description TEXT,
    Manufacturer VARCHAR(255),
    DosageForm VARCHAR(100),
    SideEffects TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_medicine_name (MedicineName)
);

-- Bảng Chi tiết Đơn thuốc
CREATE TABLE PrescriptionDetails (
    PrescriptionDetailID VARCHAR(50) PRIMARY KEY,
    PrescriptionID VARCHAR(50) NOT NULL,
    MedicationID VARCHAR(50) NOT NULL,
    Dosage VARCHAR(50) NOT NULL,
    Instructions TEXT,
    UNIQUE KEY unique_prescription_medication (PrescriptionID, MedicationID),
    FOREIGN KEY (PrescriptionID) REFERENCES Prescriptions(PrescriptionID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (MedicationID) REFERENCES Medications(MedicationID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bảng Insurance (Thông tin bảo hiểm y tế)
CREATE TABLE Insurance (
    InsuranceID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    Provider VARCHAR(255) NOT NULL,
    PolicyNumber VARCHAR(100) UNIQUE NOT NULL,
    StartDate DATE NOT NULL,
    ExpirationDate DATE NOT NULL,
    CoverageDetails TEXT,
    Status ENUM('Hoạt Động', 'Hết Hạn', 'Không Xác Định') DEFAULT 'Hoạt Động',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_insurance_status (Status),
    CHECK (StartDate < ExpirationDate)
);

-- Bảng HospitalRooms (Danh sách phòng bệnh viện)
CREATE TABLE HospitalRooms (
    RoomID VARCHAR(50) PRIMARY KEY,
    RoomType ENUM('Tiêu chuẩn', 'VIP', 'ICU', 'Cấp cứu') NOT NULL,
    TotalBeds INT CHECK (TotalBeds > 0),
    AvailableBeds INT CHECK (AvailableBeds >= 0),
    FloorNumber INT CHECK (FloorNumber > 0),
    Status ENUM('Trống', 'Đang sử dụng', 'Đầy', 'Bảo trì') DEFAULT 'Trống',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (AvailableBeds <= TotalBeds),
    INDEX idx_room_status (Status),
    INDEX idx_room_type (RoomType)
);

-- Bảng Nhập Viện
CREATE TABLE Admissions (
    AdmissionID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    DoctorID VARCHAR(50),
    RoomID VARCHAR(50),
    AdmissionDate DATE NOT NULL,
    DischargeDate DATE,
    Notes TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (RoomID) REFERENCES HospitalRooms(RoomID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_admission_date (AdmissionDate),
    INDEX idx_discharge_date (DischargeDate),
    CHECK (DischargeDate IS NULL OR AdmissionDate <= DischargeDate)
);

-- Trigger to update room availability when a patient is admitted
DELIMITER //
CREATE TRIGGER after_admission_insert
AFTER INSERT ON Admissions
FOR EACH ROW
BEGIN
    IF NEW.RoomID IS NOT NULL THEN
        UPDATE HospitalRooms
        SET AvailableBeds = AvailableBeds - 1,
            Status = CASE 
                WHEN AvailableBeds - 1 = 0 THEN 'Đầy'
                ELSE 'Đang sử dụng' 
            END
        WHERE RoomID = NEW.RoomID AND Status != 'Bảo trì';
    END IF;
END//

-- Trigger to update room availability when a patient is discharged
CREATE TRIGGER after_admission_update
AFTER UPDATE ON Admissions
FOR EACH ROW
BEGIN
    IF NEW.DischargeDate IS NOT NULL AND OLD.DischargeDate IS NULL AND NEW.RoomID IS NOT NULL THEN
        UPDATE HospitalRooms
        SET AvailableBeds = AvailableBeds + 1,
            Status = CASE 
                WHEN AvailableBeds + 1 = TotalBeds THEN 'Trống'
                ELSE 'Đang sử dụng' 
            END
        WHERE RoomID = NEW.RoomID AND Status != 'Bảo trì';
    END IF;
END//
DELIMITER ;


-- ================================
-- 1. Insert dữ liệu vào bảng UserAccounts
-- ================================
INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, PasswordHash)
VALUES
('U001', 'bs_hoang', 'Hoàng Văn A', 'Bác sĩ', 'hoang.a@example.com', '0909123456', 'hash1'),
('U002', 'bn_linh', 'Nguyễn Thị Linh', 'Bệnh nhân', 'linh.b@example.com', '0911222333', 'hash2'),
('U003', 'admin123', 'Trần Văn Quản', 'Quản lí', 'admin@example.com', '0988999777', 'hash3'),
('U004', 'bs_hung', 'Lê Minh Hùng', 'Bác sĩ', 'hung.lm@example.com', '0933444555', 'hash4'),
('U005', 'bn_khanh', 'Phạm Thị Khánh', 'Bệnh nhân', 'khanh.pt@example.com', '0922334455', 'hash5'),
('U006', 'bs_lan', 'Lê Thị Lan', 'Bác sĩ', 'lan.lt@example.com', '0909887766', 'hash6'),
('U007', 'bn_nam', 'Trịnh Minh Nam', 'Bệnh nhân', 'nam.tm@example.com', '0909223344', 'hash7');

-- ================================
-- 2. Chuyên khoa
-- ================================
INSERT INTO Specialties (SpecialtyID, SpecialtyName)
VALUES
('S001', 'Nội tổng quát'),
('S002', 'Nhi khoa'),
('S003', 'Tim mạch'),
('S004', 'Ngoại thần kinh'),
('S005', 'Da liễu');

-- ================================
-- 3. Bác sĩ
-- ================================
INSERT INTO Doctors (DoctorID, UserID, DateOfBirth, Gender, Address, SpecialtyID)
VALUES
('D001', 'U001', '1980-05-10', 'Nam', '123 Lê Lợi, Quận 1', 'S001'),
('D002', 'U004', '1985-08-20', 'Nam', '234 Nguyễn Trãi, Quận 5', 'S003'),
('D003', 'U006', '1978-02-14', 'Nữ', '345 Cách Mạng Tháng 8, Quận 3', 'S005');

-- ================================
-- 4. Lịch bác sĩ
-- ================================
INSERT INTO DoctorSchedule (ScheduleID, DoctorID, DayOfWeek, ShiftType)
VALUES
('SC001', 'D001', 'Thứ Hai', 'Sáng'),
('SC002', 'D001', 'Thứ Tư', 'Chiều'),
('SC003', 'D002', 'Thứ Sáu', 'Sáng'),
('SC004', 'D003', 'Thứ Ba', 'Chiều');

-- ================================
-- 5. Bệnh nhân
-- ================================
INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address)
VALUES
('P001', 'U002', 'Nguyễn Thị Linh', '1995-03-22', 'Nữ', '0911222333', '456 Trần Hưng Đạo, Quận 5'),
('P002', 'U005', 'Phạm Thị Khánh', '2000-11-15', 'Nữ', '0922334455', '78 Nguyễn Văn Cừ, Quận 10'),
('P003', 'U007', 'Trịnh Minh Nam', '1988-07-30', 'Nam', '0909223344', '99 Hai Bà Trưng, Quận 1');

-- ================================
-- 6. Lịch hẹn
-- ================================
INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Notes)
VALUES
('A001', 'P001', 'D001', '2025-05-01 09:00:00', 'Khám đau đầu'),
('A002', 'P002', 'D002', '2025-05-02 14:30:00', 'Khám tim mạch định kỳ'),
('A003', 'P003', 'D003', '2025-05-03 10:00:00', 'Viêm da cơ địa');

-- ================================
-- 7. Dịch vụ y tế
-- ================================
INSERT INTO Services (ServiceID, ServiceName, Cost)
VALUES
('SV001', 'Khám tổng quát', 300000),
('SV002', 'Xét nghiệm máu', 200000),
('SV003', 'Siêu âm tim', 400000),
('SV004', 'Chụp MRI', 1500000),
('SV005', 'Khám da liễu', 250000);

-- ================================
-- 8. Hóa đơn
-- ================================
INSERT INTO Billing (BillID, PatientID, TotalAmount, PaymentMethod, Status)
VALUES
('B001', 'P001', 500000, 'Tiền mặt', 'Đã thanh toán'),
('B002', 'P002', 600000, 'Chuyển khoản', 'Chưa thanh toán'),
('B003', 'P003', 750000, 'Tiền mặt', 'Đã thanh toán');

-- ================================
-- 9. Chi tiết hóa đơn
-- ================================
INSERT INTO BillingDetails (BillDetailID, BillID, ServiceID, Amount)
VALUES
('BD001', 'B001', 'SV001', 300000),
('BD002', 'B001', 'SV002', 200000),
('BD003', 'B002', 'SV003', 600000),
('BD004', 'B003', 'SV001', 300000),
('BD005', 'B003', 'SV005', 450000);

-- ================================
-- 10. Hồ sơ bệnh án
-- ================================
INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, TreatmentPlan, RecordDate)
VALUES
('MR001', 'P001', 'D001', 'Đau đầu kéo dài', 'Thuốc giảm đau, nghỉ ngơi', '2025-04-28'),
('MR002', 'P002', 'D002', 'Rối loạn nhịp tim', 'Theo dõi ECG và uống thuốc hàng ngày', '2025-04-29'),
('MR003', 'P003', 'D003', 'Viêm da tiếp xúc', 'Bôi thuốc mỡ, tránh tiếp xúc hóa chất', '2025-04-30');

-- ================================
-- 11. Chỉ số sinh hiệu
-- ================================
INSERT INTO VitalSigns (VitalSignID, PatientID, Temperature, BloodPressure, HeartRate, OxygenSaturation)
VALUES
('VS001', 'P001', 37.0, '120/80', 75, 98.0),
('VS002', 'P002', 36.8, '125/85', 80, 97.5),
('VS003', 'P003', 37.5, '110/70', 72, 96.0);

-- ================================
-- 12. Đơn thuốc
-- ================================
INSERT INTO Prescriptions (PrescriptionID, PatientID, DoctorID, PrescriptionDate)
VALUES
('PR001', 'P001', 'D001', '2025-04-28'),
('PR002', 'P002', 'D002', '2025-04-29'),
('PR003', 'P003', 'D003', '2025-04-30');

-- ================================
-- 13. Thuốc
-- ================================
INSERT INTO Medications (MedicationID, MedicineName, Description, Manufacturer, DosageForm, SideEffects)
VALUES
('M001', 'Paracetamol', 'Thuốc giảm đau, hạ sốt', 'DHG Pharma', 'Viên nén', 'Buồn nôn, chóng mặt'),
('M002', 'Bisoprolol', 'Thuốc tim mạch', 'Stada', 'Viên nén', 'Mệt mỏi, chóng mặt'),
('M003', 'Betadine Cream', 'Thuốc bôi ngoài da sát khuẩn', 'Medipharco', 'Kem bôi', 'Ngứa, rát');

-- ================================
-- 14. Chi tiết đơn thuốc
-- ================================
INSERT INTO PrescriptionDetails (PrescriptionDetailID, PrescriptionID, MedicationID, Dosage, Instructions)
VALUES
('PD001', 'PR001', 'M001', '2 viên/ngày', 'Sau ăn sáng và tối'),
('PD002', 'PR002', 'M002', '1 viên/ngày', 'Uống sáng sớm'),
('PD003', 'PR003', 'M003', 'Bôi 2 lần/ngày', 'Sáng và tối sau tắm');

-- ================================
-- 15. Bảo hiểm
-- ================================
INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, CoverageDetails)
VALUES
('INS001', 'P001', 'Bảo hiểm PVI', 'BH12345', '2024-01-01', '2025-12-31', 'Chi trả 80% viện phí'),
('INS002', 'P002', 'Bảo hiểm Bảo Việt', 'BH67890', '2024-06-01', '2026-05-31', 'Chi trả 90% viện phí'),
('INS003', 'P003', 'Bảo hiểm PTI', 'BH11223', '2023-03-01', '2025-03-01', 'Chi trả 70% viện phí');

-- ================================
-- 16. Phòng bệnh
-- ================================
INSERT INTO HospitalRooms (RoomID, RoomType, TotalBeds, AvailableBeds, FloorNumber)
VALUES
('R001', 'Tiêu chuẩn', 4, 2, 2),
('R002', 'VIP', 2, 1, 3),
('R003', 'ICU', 1, 1, 1);

-- ================================
-- 17. Nhập viện
-- ================================
INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, Notes)
VALUES
('AD001', 'P001', 'D001', 'R001', '2025-04-20', 'Theo dõi 24h'),
('AD002', 'P002', 'D002', 'R002', '2025-04-25', 'Điều trị tim mạch'),
('AD003', 'P003', 'D003', 'R003', '2025-04-29', 'Chuẩn đoán');

