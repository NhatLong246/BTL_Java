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
    PasswordChangeRequired BOOLEAN DEFAULT 1,
    IsLocked TINYINT(1) NOT NULL DEFAULT 0,
    INDEX idx_role (Role)
);

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
    FullName NVARCHAR(100) NOT NULL,
    PhoneNumber VARCHAR(20),
    Email VARCHAR(100),
    Address TEXT,
    SpecialtyID VARCHAR(50),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (SpecialtyID) REFERENCES Specialties(SpecialtyID) 
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Tạo bảng DoctorSchedule
CREATE TABLE DoctorSchedule (
    ScheduleID INT AUTO_INCREMENT PRIMARY KEY,
    DoctorID VARCHAR(10) NOT NULL,
    DayOfWeek NVARCHAR(10) NOT NULL,
    ShiftType NVARCHAR(10) NOT NULL,
    Status NVARCHAR(20) NOT NULL,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID),
    CHECK (DayOfWeek IN ('Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy', 'Chủ Nhật')),
    CHECK (ShiftType IN ('Sáng', 'Chiều', 'Tối')),
    CHECK (Status IN ('Đang làm việc', 'Hết ca làm việc'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE DoctorSchedule
MODIFY COLUMN Status VARCHAR(50) NOT NULL DEFAULT 'Đang làm việc';

-- Bảng Bệnh Nhân
CREATE TABLE Patients (
    PatientID VARCHAR(50) PRIMARY KEY,
    UserID VARCHAR(50) UNIQUE NOT NULL,
    FullName VARCHAR(255) NOT NULL,
    DateOfBirth DATE NOT NULL,
    Gender ENUM('Nam', 'Nữ') NOT NULL,
    PhoneNumber VARCHAR(20),
    Address TEXT,
	CreatedAt DATE, -- Ngày nhập viện
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
    BillID VARCHAR(50) NOT NULL,
    ServiceID VARCHAR(50) NOT NULL,
    Amount DECIMAL(10,2) NOT NULL CHECK (Amount >= 0),
    PRIMARY KEY (BillID, ServiceID),
    FOREIGN KEY (BillID) REFERENCES Billing(BillID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID) ON DELETE CASCADE ON UPDATE CASCADE
);


-- thêm bảng mới 
CREATE TABLE PaymentLogs (
    LogID INT AUTO_INCREMENT PRIMARY KEY,
    BillID VARCHAR(50) NOT NULL,
    PatientID VARCHAR(50) NOT NULL,
    PaymentAmount DECIMAL(10,2) NOT NULL CHECK (PaymentAmount >= 0),
    PaymentMethod ENUM('Tiền mặt', 'Chuyển khoản', 'Thẻ tín dụng', 'Ví điện tử') NOT NULL,
    PaymentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    TransactionID VARCHAR(100),
    Notes TEXT,
    FOREIGN KEY (BillID) REFERENCES Billing(BillID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_payment_date (PaymentDate)
);

-- Bảng Hồ Sơ Y Tế
CREATE TABLE MedicalRecords (
    RecordID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    Diagnosis TEXT NOT NULL,
--    TreatmentPlan TEXT NOT NULL,
    TreatmentPlan TEXT,
    RecordDate DATE NOT NULL,
    IsHistory BOOLEAN DEFAULT FALSE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_record_date (RecordDate)
);

ALTER TABLE MedicalRecords
MODIFY COLUMN TreatmentPlan TEXT NULL;

-- Bảng Chỉ Số Sức Khỏe
CREATE TABLE VitalSigns (
    VitalSignID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    Temperature DECIMAL(4,1) CHECK (Temperature BETWEEN 30 AND 45),
    SystolicPressure INT CHECK (SystolicPressure BETWEEN 50 AND 250),
    DiastolicPressure INT CHECK (DiastolicPressure BETWEEN 30 AND 150),
    HeartRate INT CHECK (HeartRate BETWEEN 30 AND 250),
    OxygenSaturation DECIMAL(5,2) CHECK (OxygenSaturation BETWEEN 50 AND 100),
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
ALTER TABLE Medications
MODIFY COLUMN MedicineName VARCHAR(100) NOT NULL DEFAULT 'Chưa xác định';

-- Bảng Chi tiết Đơn thuốc
CREATE TABLE PrescriptionDetails (
    PrescriptionID VARCHAR(50),
    MedicationID VARCHAR(50),
    Dosage VARCHAR(50),
    Instructions TEXT,
    PRIMARY KEY (PrescriptionID, MedicationID),
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

-- Thêm dữ liệu vào bảng Specialties (Chuyên khoa)
INSERT INTO Specialties (SpecialtyID, SpecialtyName) VALUES
('SPEC-001', 'Nội khoa'),
('SPEC-002', 'Ngoại khoa'),
('SPEC-003', 'Nhi khoa'),
('SPEC-004', 'Sản khoa'),
('SPEC-005', 'Tim mạch'),
('SPEC-006', 'Hô hấp'),
('SPEC-007', 'Thần kinh'),
('SPEC-008', 'Tiêu hóa'),
('SPEC-009', 'Da liễu'),
('SPEC-010', 'Xương khớp');

-- Thêm dữ liệu vào bảng UserAccounts
-- Tài khoản Admin (2 tài khoản)
INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, PasswordHash, CreatedAt, PasswordChangeRequired, IsLocked) VALUES
('ADM-001', 'admin1', 'Nguyễn Văn Hùng', 'Quản lí', 'admin1@hospital.com', '0912345678', SHA2('adminPass1', 256), '2025-05-02 10:00:00', 1, 0),
('ADM-002', 'admin2', 'Trần Thị Mai', 'Quản lí', 'admin2@hospital.com', '0912345679', SHA2('adminPass2', 256), '2025-05-02 10:00:00', 1, 0);

-- Tài khoản Bác sĩ (10 tài khoản)
INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, PasswordHash, CreatedAt, PasswordChangeRequired, IsLocked) VALUES
('DOC-001', 'doctor1', 'Nguyễn Thành Long', 'Bác sĩ', 'long.nguyen@hospital.com', '0987654321', SHA2('doctorPass1', 256), '2025-05-02 10:00:00', 1, 0),
('DOC-002', 'doctor2', 'Trần Thị Hồng Nhung', 'Bác sĩ', 'nhung.tran@hospital.com', '0971234567', SHA2('doctorPass2', 256), '2025-05-02 10:00:00', 1, 0),
('DOC-003', 'doctor3', 'Phạm Văn Hậu', 'Bác sĩ', 'hau.pham@hospital.com', '0939876543', SHA2('doctorPass3', 256), '2025-05-02 10:00:00', 1, 0),
('DOC-004', 'doctor4', 'Lê Thị Minh Thư', 'Bác sĩ', 'thu.le@hospital.com', '0908765432', SHA2('doctorPass4', 256), '2025-05-02 10:00:00', 1, 0),
('DOC-005', 'doctor5', 'Hoàng Văn Nam', 'Bác sĩ', 'nam.hoang@hospital.com', '0945678901', SHA2('doctorPass5', 256), '2025-05-02 10:00:00', 1, 0),
('DOC-006', 'doctor6', 'Nguyễn Thị Lan Anh', 'Bác sĩ', 'lananh.nguyen@hospital.com', '0923456789', SHA2('doctorPass6', 256), '2025-05-02 10:00:00', 1, 0),
('DOC-007', 'doctor7', 'Đỗ Văn Tuấn', 'Bác sĩ', 'tuan.do@hospital.com', '0967890123', SHA2('doctorPass7', 256), '2025-05-02 10:00:00', 1, 0),
('DOC-008', 'doctor8', 'Vũ Thị Ngọc Ánh', 'Bác sĩ', 'anh.vu@hospital.com', '0918765432', SHA2('doctorPass8', 256), '2025-05-02 10:00:00', 1, 0),
('DOC-009', 'doctor9', 'Bùi Văn Khánh', 'Bác sĩ', 'khanh.bui@hospital.com', '0954321098', SHA2('doctorPass9', 256), '2025-05-02 10:00:00', 1, 0),
('DOC-010', 'doctor10', 'Trương Thị Phương', 'Bác sĩ', 'phuong.truong@hospital.com', '0976543210', SHA2('doctorPass10', 256), '2025-05-02 10:00:00', 1, 0);

-- Tài khoản Bệnh nhân (20 tài khoản)
INSERT INTO UserAccounts (UserID, UserName, FullName, Role, Email, PhoneNumber, PasswordHash, CreatedAt, PasswordChangeRequired, IsLocked) VALUES
('PAT-001', 'patient1', 'Nguyễn Văn An', 'Bệnh nhân', 'an.nguyen@gmail.com', '0901234567', SHA2('patientPass1', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-002', 'patient2', 'Trần Thị Bích Ngọc', 'Bệnh nhân', 'ngoc.tran@gmail.com', '0912345670', SHA2('patientPass2', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-003', 'patient3', 'Lê Văn Cường', 'Bệnh nhân', 'cuong.le@gmail.com', '0933456789', SHA2('patientPass3', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-004', 'patient4', 'Phạm Thị Duyên', 'Bệnh nhân', 'duyen.pham@gmail.com', '0944567890', SHA2('patientPass4', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-005', 'patient5', 'Hoàng Văn Em', 'Bệnh nhân', 'em.hoang@gmail.com', '0955678901', SHA2('patientPass5', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-006', 'patient6', 'Nguyễn Thị Hồng', 'Bệnh nhân', 'hong.nguyen@gmail.com', '0966789012', SHA2('patientPass6', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-007', 'patient7', 'Trần Văn Khoa', 'Bệnh nhân', 'khoa.tran@gmail.com', '0977890123', SHA2('patientPass7', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-008', 'patient8', 'Lê Thị Lan', 'Bệnh nhân', 'lan.le@gmail.com', '0988901234', SHA2('patientPass8', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-009', 'patient9', 'Phạm Văn Minh', 'Bệnh nhân', 'minh.pham@gmail.com', '0999012345', SHA2('patientPass9', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-010', 'patient10', 'Hoàng Thị Nga', 'Bệnh nhân', 'nga.hoang@gmail.com', '0900123456', SHA2('patientPass10', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-011', 'patient11', 'Nguyễn Văn Phong', 'Bệnh nhân', 'phong.nguyen@gmail.com', '0911234567', SHA2('patientPass11', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-012', 'patient12', 'Trần Thị Quyên', 'Bệnh nhân', 'quyen.tran@gmail.com', '0922345678', SHA2('patientPass12', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-013', 'patient13', 'Lê Văn Sơn', 'Bệnh nhân', 'son.le@gmail.com', '0933456780', SHA2('patientPass13', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-014', 'patient14', 'Phạm Thị Thanh', 'Bệnh nhân', 'thanh.pham@gmail.com', '0944567891', SHA2('patientPass14', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-015', 'patient15', 'Hoàng Văn Tùng', 'Bệnh nhân', 'tung.hoang@gmail.com', '0955678902', SHA2('patientPass15', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-016', 'patient16', 'Nguyễn Thị Uyên', 'Bệnh nhân', 'uyen.nguyen@gmail.com', '0966789013', SHA2('patientPass16', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-017', 'patient17', 'Trần Văn Vỹ', 'Bệnh nhân', 'vy.tran@gmail.com', '0977890124', SHA2('patientPass17', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-018', 'patient18', 'Lê Thị Xuân', 'Bệnh nhân', 'xuan.le@gmail.com', '0988901235', SHA2('patientPass18', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-019', 'patient19', 'Phạm Văn Ý', 'Bệnh nhân', 'y.pham@gmail.com', '0999012346', SHA2('patientPass19', 256), '2025-05-02 10:00:00', 1, 0),
('PAT-020', 'patient20', 'Hoàng Thị Ánh', 'Bệnh nhân', 'anh.hoang@gmail.com', '0900123457', SHA2('patientPass20', 256), '2025-05-02 10:00:00', 1, 0);

-- Thêm dữ liệu vào bảng Doctors (10 bác sĩ)
INSERT INTO Doctors (DoctorID, UserID, DateOfBirth, Gender, FullName, PhoneNumber, Email, Address, SpecialtyID, CreatedAt) VALUES
('DOC-001', 'DOC-001', '1985-03-15', 'Nam', 'Nguyễn Thành Long', '0987654321', 'long.nguyen@hospital.com', '123 Đường Láng, Hà Nội', 'SPEC-001', '2025-05-02 10:00:00'),
('DOC-002', 'DOC-002', '1990-07-22', 'Nữ', 'Trần Thị Hồng Nhung', '0971234567', 'nhung.tran@hospital.com', '45 Nguyễn Huệ, Huế', 'SPEC-002', '2025-05-02 10:00:00'),
('DOC-003', 'DOC-003', '1982-11-30', 'Nam', 'Phạm Văn Hậu', '0939876543', 'hau.pham@hospital.com', '78 Trần Phú, Đà Nẵng', 'SPEC-003', '2025-05-02 10:00:00'),
('DOC-004', 'DOC-004', '1988-05-10', 'Nữ', 'Lê Thị Minh Thư', '0908765432', 'thu.le@hospital.com', '12 Lê Lợi, TP.HCM', 'SPEC-004', '2025-05-02 10:00:00'),
('DOC-005', 'DOC-005', '1979-09-18', 'Nam', 'Hoàng Văn Nam', '0945678901', 'nam.hoang@hospital.com', '56 Nguyễn Trãi, Hà Nội', 'SPEC-005', '2025-05-02 10:00:00'),
('DOC-006', 'DOC-006', '1992-02-25', 'Nữ', 'Nguyễn Thị Lan Anh', '0923456789', 'lananh.nguyen@hospital.com', '89 Phạm Văn Đồng, Đà Nẵng', 'SPEC-006', '2025-05-02 10:00:00'),
('DOC-007', 'DOC-007', '1980-12-05', 'Nam', 'Đỗ Văn Tuấn', '0967890123', 'tuan.do@hospital.com', '34 Nguyễn Văn Cừ, Hà Nội', 'SPEC-007', '2025-05-02 10:00:00'),
('DOC-008', 'DOC-008', '1987-06-14', 'Nữ', 'Vũ Thị Ngọc Ánh', '0918765432', 'anh.vu@hospital.com', '67 Lê Đại Hành, TP.HCM', 'SPEC-008', '2025-05-02 10:00:00'),
('DOC-009', 'DOC-009', '1983-08-20', 'Nam', 'Bùi Văn Khánh', '0954321098', 'khanh.bui@hospital.com', '90 Hùng Vương, Huế', 'SPEC-009', '2025-05-02 10:00:00'),
('DOC-010', 'DOC-010', '1991-04-12', 'Nữ', 'Trương Thị Phương', '0976543210', 'phuong.truong@hospital.com', '23 Trần Hưng Đạo, TP.HCM', 'SPEC-010', '2025-05-02 10:00:00');

-- Thêm dữ liệu vào bảng Patients (20 bệnh nhân)
INSERT INTO Patients (PatientID, UserID, FullName, DateOfBirth, Gender, PhoneNumber, Address, CreatedAt) VALUES
('PAT-001', 'PAT-001', 'Nguyễn Văn An', '1990-01-15', 'Nam', '0901234567', '123 Nguyễn Trãi, Hà Nội', '2025-05-01'),
('PAT-002', 'PAT-002', 'Trần Thị Bích Ngọc', '1985-06-22', 'Nữ', '0912345670', '45 Lê Lợi, TP.HCM', '2025-05-01'),
('PAT-003', 'PAT-003', 'Lê Văn Cường', '1995-09-10', 'Nam', '0933456789', '78 Trần Phú, Đà Nẵng', '2025-05-01'),
('PAT-004', 'PAT-004', 'Phạm Thị Duyên', '2000-03-25', 'Nữ', '0944567890', '12 Nguyễn Huệ, Huế', '2025-05-01'),
('PAT-005', 'PAT-005', 'Hoàng Văn Em', '1980-11-30', 'Nam', '0955678901', '56 Phạm Văn Đồng, Hà Nội', '2025-05-01'),
('PAT-006', 'PAT-006', 'Nguyễn Thị Hồng', '1992-07-18', 'Nữ', '0966789012', '89 Hùng Vương, TP.HCM', '2025-05-01'),
('PAT-007', 'PAT-007', 'Trần Văn Khoa', '1988-02-14', 'Nam', '0977890123', '34 Nguyễn Văn Cừ, Đà Nẵng', '2025-05-01'),
('PAT-008', 'PAT-008', 'Lê Thị Lan', '1997-05-20', 'Nữ', '0988901234', '67 Lê Đại Hành, Hà Nội', '2025-05-01'),
('PAT-009', 'PAT-009', 'Phạm Văn Minh', '1983-12-05', 'Nam', '0999012345', '90 Trần Hưng Đạo, Huế', '2025-05-01'),
('PAT-010', 'PAT-010', 'Hoàng Thị Nga', '1991-08-22', 'Nữ', '0900123456', '23 Nguyễn Trãi, TP.HCM', '2025-05-01'),
('PAT-011', 'PAT-011', 'Nguyễn Văn Phong', '1987-04-15', 'Nam', '0911234567', '123 Lê Lợi, Hà Nội', '2025-05-01'),
('PAT-012', 'PAT-012', 'Trần Thị Quyên', '1994-10-30', 'Nữ', '0922345678', '45 Trần Phú, Đà Nẵng', '2025-05-01'),
('PAT-013', 'PAT-013', 'Lê Văn Sơn', '1982-06-12', 'Nam', '0933456780', '78 Nguyễn Huệ, Huế', '2025-05-01'),
('PAT-014', 'PAT-014', 'Phạm Thị Thanh', '1999-01-25', 'Nữ', '0944567891', '12 Phạm Văn Đồng, TP.HCM', '2025-05-01'),
('PAT-015', 'PAT-015', 'Hoàng Văn Tùng', '1985-09-18', 'Nam', '0955678902', '56 Hùng Vương, Hà Nội', '2025-05-01'),
('PAT-016', 'PAT-016', 'Nguyễn Thị Uyên', '1993-03-20', 'Nữ', '0966789013', '89 Nguyễn Văn Cừ, Đà Nẵng', '2025-05-01'),
('PAT-017', 'PAT-017', 'Trần Văn Vỹ', '1980-07-14', 'Nam', '0977890124', '34 Lê Đại Hành, TP.HCM', '2025-05-01'),
('PAT-018', 'PAT-018', 'Lê Thị Xuân', '1996-11-05', 'Nữ', '0988901235', '67 Trần Hưng Đạo, Hà Nội', '2025-05-01'),
('PAT-019', 'PAT-019', 'Phạm Văn Ý', '1984-02-28', 'Nam', '0999012346', '90 Nguyễn Trãi, Huế', '2025-05-01'),
('PAT-020', 'PAT-020', 'Hoàng Thị Ánh', '1990-08-10', 'Nữ', '0900123457', '23 Lê Lợi, TP.HCM', '2025-05-01');

-- Chèn dữ liệu vào bảng DoctorSchedule (lịch làm việc đầy đủ cho tất cả 10 bác sĩ)
INSERT INTO DoctorSchedule (DoctorID, DayOfWeek, ShiftType, Status) VALUES
-- Lịch làm việc cho DOC-001
('DOC-001', 'Thứ Hai', 'Sáng', 'Đang làm việc'),
('DOC-001', 'Thứ Hai', 'Chiều', 'Đang làm việc'),
('DOC-001', 'Thứ Ba', 'Sáng', 'Đang làm việc'),
('DOC-001', 'Thứ Tư', 'Tối', 'Đang làm việc'),
('DOC-001', 'Thứ Năm', 'Sáng', 'Đang làm việc'),
('DOC-001', 'Thứ Sáu', 'Chiều', 'Đang làm việc'),
('DOC-001', 'Thứ Bảy', 'Tối', 'Đang làm việc'),
('DOC-001', 'Chủ Nhật', 'Sáng', 'Đang làm việc'),
-- Lịch làm việc cho DOC-002
('DOC-002', 'Thứ Hai', 'Tối', 'Đang làm việc'),
('DOC-002', 'Thứ Ba', 'Chiều', 'Đang làm việc'),
('DOC-002', 'Thứ Tư', 'Sáng', 'Đang làm việc'),
('DOC-002', 'Thứ Năm', 'Tối', 'Đang làm việc'),
('DOC-002', 'Thứ Sáu', 'Sáng', 'Đang làm việc'),
('DOC-002', 'Thứ Bảy', 'Chiều', 'Đang làm việc'),
('DOC-002', 'Chủ Nhật', 'Tối', 'Đang làm việc'),
-- Lịch làm việc cho DOC-003
('DOC-003', 'Thứ Hai', 'Sáng', 'Đang làm việc'),
('DOC-003', 'Thứ Ba', 'Tối', 'Đang làm việc'),
('DOC-003', 'Thứ Tư', 'Chiều', 'Đang làm việc'),
('DOC-003', 'Thứ Năm', 'Sáng', 'Đang làm việc'),
('DOC-003', 'Thứ Sáu', 'Tối', 'Đang làm việc'),
('DOC-003', 'Thứ Bảy', 'Sáng', 'Đang làm việc'),
('DOC-003', 'Chủ Nhật', 'Chiều', 'Đang làm việc'),
-- Lịch làm việc cho DOC-004
('DOC-004', 'Thứ Hai', 'Chiều', 'Đang làm việc'),
('DOC-004', 'Thứ Ba', 'Sáng', 'Đang làm việc'),
('DOC-004', 'Thứ Tư', 'Tối', 'Đang làm việc'),
('DOC-004', 'Thứ Năm', 'Chiều', 'Đang làm việc'),
('DOC-004', 'Thứ Sáu', 'Sáng', 'Đang làm việc'),
('DOC-004', 'Thứ Bảy', 'Tối', 'Đang làm việc'),
('DOC-004', 'Chủ Nhật', 'Sáng', 'Đang làm việc'),
-- Lịch làm việc cho DOC-005
('DOC-005', 'Thứ Hai', 'Tối', 'Đang làm việc'),
('DOC-005', 'Thứ Ba', 'Sáng', 'Đang làm việc'),
('DOC-005', 'Thứ Tư', 'Chiều', 'Đang làm việc'),
('DOC-005', 'Thứ Năm', 'Tối', 'Đang làm việc'),
('DOC-005', 'Thứ Sáu', 'Sáng', 'Đang làm việc'),
('DOC-005', 'Thứ Bảy', 'Chiều', 'Đang làm việc'),
('DOC-005', 'Chủ Nhật', 'Tối', 'Đang làm việc'),
-- Lịch làm việc cho DOC-006
('DOC-006', 'Thứ Hai', 'Sáng', 'Đang làm việc'),
('DOC-006', 'Thứ Ba', 'Chiều', 'Đang làm việc'),
('DOC-006', 'Thứ Tư', 'Tối', 'Đang làm việc'),
('DOC-006', 'Thứ Năm', 'Sáng', 'Đang làm việc'),
('DOC-006', 'Thứ Sáu', 'Chiều', 'Đang làm việc'),
('DOC-006', 'Thứ Bảy', 'Tối', 'Đang làm việc'),
('DOC-006', 'Chủ Nhật', 'Sáng', 'Đang làm việc'),
-- Lịch làm việc cho DOC-007
('DOC-007', 'Thứ Hai', 'Chiều', 'Đang làm việc'),
('DOC-007', 'Thứ Ba', 'Tối', 'Đang làm việc'),
('DOC-007', 'Thứ Tư', 'Sáng', 'Đang làm việc'),
('DOC-007', 'Thứ Năm', 'Chiều', 'Đang làm việc'),
('DOC-007', 'Thứ Sáu', 'Tối', 'Đang làm việc'),
('DOC-007', 'Thứ Bảy', 'Sáng', 'Đang làm việc'),
('DOC-007', 'Chủ Nhật', 'Chiều', 'Đang làm việc'),
-- Lịch làm việc cho DOC-008
('DOC-008', 'Thứ Hai', 'Tối', 'Đang làm việc'),
('DOC-008', 'Thứ Ba', 'Sáng', 'Đang làm việc'),
('DOC-008', 'Thứ Tư', 'Chiều', 'Đang làm việc'),
('DOC-008', 'Thứ Năm', 'Tối', 'Đang làm việc'),
('DOC-008', 'Thứ Sáu', 'Sáng', 'Đang làm việc'),
('DOC-008', 'Thứ Bảy', 'Chiều', 'Đang làm việc'),
('DOC-008', 'Chủ Nhật', 'Tối', 'Đang làm việc'),
-- Lịch làm việc cho DOC-009
('DOC-009', 'Thứ Hai', 'Sáng', 'Đang làm việc'),
('DOC-009', 'Thứ Ba', 'Tối', 'Đang làm việc'),
('DOC-009', 'Thứ Tư', 'Chiều', 'Đang làm việc'),
('DOC-009', 'Thứ Năm', 'Sáng', 'Đang làm việc'),
('DOC-009', 'Thứ Sáu', 'Tối', 'Đang làm việc'),
('DOC-009', 'Thứ Bảy', 'Sáng', 'Đang làm việc'),
('DOC-009', 'Chủ Nhật', 'Chiều', 'Đang làm việc'),
-- Lịch làm việc cho DOC-010
('DOC-010', 'Thứ Hai', 'Chiều', 'Đang làm việc'),
('DOC-010', 'Thứ Ba', 'Sáng', 'Đang làm việc'),
('DOC-010', 'Thứ Tư', 'Tối', 'Đang làm việc'),
('DOC-010', 'Thứ Năm', 'Chiều', 'Đang làm việc'),
('DOC-010', 'Thứ Sáu', 'Sáng', 'Đang làm việc'),
('DOC-010', 'Thứ Bảy', 'Tối', 'Đang làm việc'),
('DOC-010', 'Chủ Nhật', 'Sáng', 'Đang làm việc');

-- Thêm dữ liệu vào bảng Appointments (Cuộc hẹn)
INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Status, Notes, CreatedAt) VALUES
('APP-001', 'PAT-002', 'DOC-002', '2025-06-20 08:00:00', 'Hoàn thành', 'Kiểm tra tim mạch', '2025-06-18 10:00:00'),
('APP-002', 'PAT-003', 'DOC-003', '2025-06-21 14:00:00', 'Hoàn thành', 'Khám nhi khoa', '2025-06-19 10:30:00'),
('APP-003', 'PAT-018', 'DOC-001', '2025-06-23 07:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-21 14:30:00'),
('APP-004', 'PAT-004', 'DOC-005', '2025-06-23 15:00:00', 'Hoàn thành', 'Khám hô hấp', '2025-06-21 11:30:00'),
('APP-005', 'PAT-005', 'DOC-006', '2025-06-22 08:00:00', 'Hoàn thành', 'Khám thần kinh', '2025-06-20 12:00:00'),
('APP-006', 'PAT-012', 'DOC-001', '2025-06-23 15:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-21 14:30:00'),
('APP-007', 'PAT-007', 'DOC-008', '2025-06-20 09:00:00', 'Hoàn thành', 'Khám da liễu', '2025-06-18 13:00:00'),
('APP-008', 'PAT-008', 'DOC-009', '2025-06-21 18:00:00', 'Hoàn thành', 'Khám xương khớp', '2025-06-19 13:30:00'),
('APP-009', 'PAT-013', 'DOC-010', '2025-06-22 15:00:00', 'Hoàn thành', 'Phẫu thuật kiểm tra', '2025-06-20 14:00:00'),
('APP-010', 'PAT-014', 'DOC-001', '2025-06-23 08:00:00', 'Hoàn thành', 'Khám tổng quát', '2025-06-21 14:30:00'),
('APP-011', 'PAT-015', 'DOC-002', '2025-06-23 10:00:00', 'Hoàn thành', 'Kiểm tra tim mạch', '2025-06-21 15:00:00'),
('APP-012', 'PAT-016', 'DOC-003', '2025-06-22 14:00:00', 'Hoàn thành', 'Khám nhi khoa', '2025-06-20 15:30:00'),
('APP-013', 'PAT-017', 'DOC-004', '2025-06-21 15:00:00', 'Hoàn thành', 'Khám sản khoa', '2025-06-19 16:00:00'),
('APP-014', 'PAT-018', 'DOC-005', '2025-06-20 18:00:00', 'Hoàn thành', 'Khám hô hấp', '2025-06-18 16:30:00'),
('APP-015', 'PAT-019', 'DOC-006', '2025-06-23 08:00:00', 'Hoàn thành', 'Khám thần kinh', '2025-06-21 17:00:00'),
('APP-016', 'PAT-020', 'DOC-007', '2025-06-22 09:00:00', 'Hoàn thành', 'Khám tiêu hóa', '2025-06-20 17:30:00'),
('APP-017', 'PAT-001', 'DOC-008', '2025-06-21 14:00:00', 'Hoàn thành', 'Khám da liễu', '2025-06-19 18:00:00'),
('APP-018', 'PAT-002', 'DOC-009', '2025-06-20 15:00:00', 'Hoàn thành', 'Khám xương khớp', '2025-06-18 18:30:00'),
('APP-019', 'PAT-003', 'DOC-010', '2025-06-23 18:00:00', 'Hoàn thành', 'Phẫu thuật kiểm tra', '2025-06-21 19:00:00'),
('APP-020', 'PAT-004', 'DOC-001', '2025-06-22 08:00:00', 'Hoàn thành', 'Khám tổng quát', '2025-06-20 19:30:00'),
('APP-021', 'PAT-005', 'DOC-001', '2025-06-24 08:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 10:00:00'),
('APP-022', 'PAT-006', 'DOC-001', '2025-06-24 09:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 10:30:00'),
('APP-023', 'PAT-007', 'DOC-001', '2025-06-24 10:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 11:00:00'),
('APP-024', 'PAT-008', 'DOC-001', '2025-06-24 11:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 11:30:00'),
('APP-025', 'PAT-009', 'DOC-001', '2025-06-24 13:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 12:00:00'),
('APP-026', 'PAT-010', 'DOC-001', '2025-06-24 14:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 12:30:00'),
('APP-027', 'PAT-001', 'DOC-001', '2025-06-24 15:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 13:00:00'),
('APP-028', 'PAT-012', 'DOC-001', '2025-06-24 16:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 13:30:00'),
('APP-029', 'PAT-013', 'DOC-001', '2025-06-24 17:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 14:00:00'),
('APP-030', 'PAT-014', 'DOC-001', '2025-06-24 18:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 14:30:00'),
('APP-031', 'PAT-015', 'DOC-002', '2025-06-25 09:00:00', 'Chờ xác nhận', 'Kiểm tra tim mạch', '2025-06-23 15:00:00'),
('APP-032', 'PAT-016', 'DOC-003', '2025-06-25 14:00:00', 'Chờ xác nhận', 'Khám nhi khoa', '2025-06-23 15:30:00'),
('APP-033', 'PAT-017', 'DOC-004', '2025-06-26 10:00:00', 'Chờ xác nhận', 'Khám sản khoa', '2025-06-23 16:00:00'),
('APP-034', 'PAT-018', 'DOC-005', '2025-06-26 15:00:00', 'Chờ xác nhận', 'Khám hô hấp', '2025-06-23 16:30:00'),
('APP-035', 'PAT-019', 'DOC-006', '2025-06-27 09:00:00', 'Chờ xác nhận', 'Khám thần kinh', '2025-06-23 17:00:00'),
('APP-036', 'PAT-020', 'DOC-007', '2025-06-27 14:00:00', 'Chờ xác nhận', 'Khám tiêu hóa', '2025-06-23 17:30:00'),
('APP-037', 'PAT-001', 'DOC-008', '2025-06-28 10:00:00', 'Chờ xác nhận', 'Khám da liễu', '2025-06-23 18:00:00'),
('APP-038', 'PAT-002', 'DOC-009', '2025-06-28 15:00:00', 'Chờ xác nhận', 'Khám xương khớp', '2025-06-23 18:30:00'),
('APP-039', 'PAT-003', 'DOC-010', '2025-06-29 09:00:00', 'Chờ xác nhận', 'Phẫu thuật kiểm tra', '2025-06-23 19:00:00'),
('APP-040', 'PAT-004', 'DOC-001', '2025-06-29 14:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 19:30:00'),
('APP-041', 'PAT-005', 'DOC-001', '2025-06-30 10:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 20:00:00'),
('APP-042', 'PAT-006', 'DOC-001', '2025-07-01 09:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 20:30:00'),
('APP-043', 'PAT-007', 'DOC-001', '2025-07-02 14:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 21:00:00'),
('APP-044', 'PAT-008', 'DOC-001', '2025-07-03 10:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 21:30:00'),
('APP-045', 'PAT-009', 'DOC-001', '2025-07-04 15:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 22:00:00'),
('APP-046', 'PAT-010', 'DOC-001', '2025-07-05 09:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 22:30:00'),
('APP-047', 'PAT-001', 'DOC-001', '2025-07-06 14:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 23:00:00'),
('APP-048', 'PAT-012', 'DOC-001', '2025-07-07 10:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-23 23:30:00'),
('APP-049', 'PAT-013', 'DOC-001', '2025-07-08 15:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-24 00:00:00'),
('APP-050', 'PAT-014', 'DOC-001', '2025-07-09 09:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-24 00:30:00'),
('APP-051', 'PAT-015', 'DOC-001', '2025-07-10 14:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-24 01:00:00'),
('APP-052', 'PAT-016', 'DOC-001', '2025-07-11 10:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-24 01:30:00'),
('APP-053', 'PAT-017', 'DOC-001', '2025-07-12 15:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-24 02:00:00'),
('APP-054', 'PAT-018', 'DOC-001', '2025-07-13 09:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-24 02:30:00'),
('APP-055', 'PAT-019', 'DOC-001', '2025-07-14 14:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-24 03:00:00'),
('APP-056', 'PAT-020', 'DOC-001', '2025-07-15 10:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-24 03:30:00'),
('APP-057', 'PAT-001', 'DOC-002', '2025-07-16 09:00:00', 'Chờ xác nhận', 'Kiểm tra tim mạch', '2025-06-24 04:00:00'),
('APP-058', 'PAT-002', 'DOC-003', '2025-07-17 14:00:00', 'Chờ xác nhận', 'Khám nhi khoa', '2025-06-24 04:30:00'),
('APP-059', 'PAT-003', 'DOC-004', '2025-07-18 10:00:00', 'Chờ xác nhận', 'Khám sản khoa', '2025-06-24 05:00:00'),
('APP-060', 'PAT-004', 'DOC-005', '2025-07-19 15:00:00', 'Chờ xác nhận', 'Khám hô hấp', '2025-06-24 05:30:00'),
('APP-061', 'PAT-005', 'DOC-006', '2025-07-20 09:00:00', 'Chờ xác nhận', 'Khám thần kinh', '2025-06-24 06:00:00'),
('APP-062', 'PAT-006', 'DOC-007', '2025-07-21 14:00:00', 'Chờ xác nhận', 'Khám tiêu hóa', '2025-06-24 06:30:00'),
('APP-063', 'PAT-007', 'DOC-008', '2025-07-22 10:00:00', 'Chờ xác nhận', 'Khám da liễu', '2025-06-24 07:00:00'),
('APP-064', 'PAT-008', 'DOC-009', '2025-07-23 15:00:00', 'Chờ xác nhận', 'Khám xương khớp', '2025-06-24 07:30:00'),
('APP-065', 'PAT-009', 'DOC-010', '2025-07-24 09:00:00', 'Chờ xác nhận', 'Phẫu thuật kiểm tra', '2025-06-24 08:00:00'),
('APP-066', 'PAT-010', 'DOC-001', '2025-07-25 14:00:00', 'Chờ xác nhận', 'Khám tổng quát', '2025-06-24 08:30:00'),
('APP-067', 'PAT-003', 'DOC-002', '2025-07-26 10:00:00', 'Chờ xác nhận', 'Kiểm tra tim mạch', '2025-06-24 09:00:00'),
('APP-068', 'PAT-012', 'DOC-003', '2025-07-27 15:00:00', 'Chờ xác nhận', 'Khám nhi khoa', '2025-06-24 09:30:00'),
('APP-069', 'PAT-013', 'DOC-004', '2025-07-28 09:00:00', 'Chờ xác nhận', 'Khám sản khoa', '2025-06-24 10:00:00'),
('APP-070', 'PAT-014', 'DOC-005', '2025-07-29 14:00:00', 'Chờ xác nhận', 'Khám hô hấp', '2025-06-24 10:30:00');

-- Thêm dữ liệu vào bảng Services (Dịch vụ y tế)
INSERT INTO Services (ServiceID, ServiceName, Cost, CreatedAt) VALUES
('SVC-001', 'Khám tổng quát', 200000, '2025-05-02 10:00:00'),
('SVC-002', 'Chụp X-quang', 500000, '2025-05-02 10:00:00'),
('SVC-003', 'Xét nghiệm máu', 300000, '2025-05-02 10:00:00'),
('SVC-004', 'Siêu âm', 400000, '2025-05-02 10:00:00'),
('SVC-005', 'Khám chuyên khoa', 250000, '2025-05-02 10:00:00'),
('SVC-006', 'Nội soi dạ dày', 800000, '2025-05-02 10:00:00'),
('SVC-007', 'Đo điện tim', 350000, '2025-05-02 10:00:00'),
('SVC-008', 'Khám da liễu', 200000, '2025-05-02 10:00:00'),
('SVC-009', 'Phẫu thuật nhỏ', 1500000, '2025-05-02 10:00:00'),
('SVC-010', 'Tiêm vắc-xin', 500000, '2025-05-02 10:00:00');

-- Thêm dữ liệu vào bảng Billing (Hóa đơn)
-- Mỗi bệnh nhân có 1 hóa đơn
INSERT INTO Billing (BillID, PatientID, TotalAmount, PaymentMethod, Status, CreatedAt) VALUES
('BILL-001', 'PAT-001', 700000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-002', 'PAT-002', 1300000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00'),
('BILL-003', 'PAT-003', 500000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-004', 'PAT-004', 650000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00'),
('BILL-005', 'PAT-005', 750000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-006', 'PAT-006', 1200000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00'),
('BILL-007', 'PAT-007', 600000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-008', 'PAT-008', 450000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00'),
('BILL-009', 'PAT-009', 1000000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-010', 'PAT-010', 700000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00'),
('BILL-011', 'PAT-011', 500000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-012', 'PAT-012', 1800000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00'),
('BILL-013', 'PAT-013', 550000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-014', 'PAT-014', 650000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00'),
('BILL-015', 'PAT-015', 800000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-016', 'PAT-016', 900000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00'),
('BILL-017', 'PAT-017', 600000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-018', 'PAT-018', 750000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00'),
('BILL-019', 'PAT-019', 400000, 'Tiền mặt', 'Chưa thanh toán', '2025-05-02 10:00:00'),
('BILL-020', 'PAT-020', 950000, 'Chuyển khoản', 'Đã thanh toán', '2025-05-02 10:00:00');

-- Thêm dữ liệu vào bảng BillingDetails (Chi tiết hóa đơn)
-- Mỗi hóa đơn có 1-3 dịch vụ
INSERT INTO BillingDetails (BillDetailID, BillID, ServiceID, Amount) VALUES
('BD-001', 'BILL-001', 'SVC-001', 200000),
('BD-002', 'BILL-001', 'SVC-003', 300000),
('BD-003', 'BILL-001', 'SVC-007', 200000),
('BD-004', 'BILL-002', 'SVC-002', 500000),
('BD-005', 'BILL-002', 'SVC-009', 800000),
('BD-006', 'BILL-003', 'SVC-005', 250000),
('BD-007', 'BILL-003', 'SVC-008', 250000),
('BD-008', 'BILL-004', 'SVC-004', 400000),
('BD-009', 'BILL-004', 'SVC-005', 250000),
('BD-010', 'BILL-005', 'SVC-001', 200000),
('BD-011', 'BILL-005', 'SVC-007', 350000),
('BD-012', 'BILL-006', 'SVC-006', 800000),
('BD-013', 'BILL-006', 'SVC-003', 400000),
('BD-014', 'BILL-007', 'SVC-001', 200000),
('BD-015', 'BILL-007', 'SVC-005', 400000),
('BD-016', 'BILL-008', 'SVC-008', 200000),
('BD-017', 'BILL-008', 'SVC-003', 250000),
('BD-018', 'BILL-009', 'SVC-009', 800000),
('BD-019', 'BILL-009', 'SVC-001', 200000),
('BD-020', 'BILL-010', 'SVC-005', 250000),
('BD-021', 'BILL-010', 'SVC-004', 450000),
('BD-022', 'BILL-011', 'SVC-001', 200000),
('BD-023', 'BILL-011', 'SVC-003', 300000),
('BD-024', 'BILL-012', 'SVC-002', 500000),
('BD-025', 'BILL-012', 'SVC-009', 1300000),
('BD-026', 'BILL-013', 'SVC-005', 250000),
('BD-027', 'BILL-013', 'SVC-007', 300000),
('BD-028', 'BILL-014', 'SVC-004', 400000),
('BD-029', 'BILL-014', 'SVC-005', 250000),
('BD-030', 'BILL-015', 'SVC-001', 200000),
('BD-031', 'BILL-015', 'SVC-006', 600000),
('BD-032', 'BILL-016', 'SVC-003', 300000),
('BD-033', 'BILL-016', 'SVC-007', 600000),
('BD-034', 'BILL-017', 'SVC-001', 200000),
('BD-035', 'BILL-017', 'SVC-005', 400000),
('BD-036', 'BILL-018', 'SVC-004', 400000),
('BD-037', 'BILL-018', 'SVC-008', 350000),
('BD-038', 'BILL-019', 'SVC-003', 300000),
('BD-039', 'BILL-019', 'SVC-001', 100000),
('BD-040', 'BILL-020', 'SVC-005', 250000),
('BD-041', 'BILL-020', 'SVC-010', 700000);

-- Thêm dữ liệu vào bảng MedicalRecords (Hồ sơ y tế)
-- Mỗi bệnh nhân có 1 hồ sơ y tế
INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, TreatmentPlan, RecordDate, IsHistory, CreatedAt) VALUES
('MR-001', 'PAT-001', 'DOC-001', 'Cảm cúm', 'Nghỉ ngơi, uống nhiều nước, dùng thuốc hạ sốt', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-002', 'PAT-002', 'DOC-002', 'Viêm dạ dày', 'Kiêng đồ cay, uống thuốc bảo vệ dạ dày', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-003', 'PAT-003', 'DOC-003', 'Sốt xuất huyết', 'Theo dõi nhiệt độ, truyền dịch', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-004', 'PAT-004', 'DOC-004', 'Thai kỳ 12 tuần', 'Khám định kỳ, bổ sung vitamin', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-005', 'PAT-005', 'DOC-005', 'Tăng huyết áp', 'Dùng thuốc hạ huyết áp, giảm muối', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-006', 'PAT-006', 'DOC-006', 'Viêm phổi', 'Kháng sinh, nghỉ ngơi', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-007', 'PAT-007', 'DOC-007', 'Đau nửa đầu', 'Dùng thuốc giảm đau, tránh căng thẳng', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-008', 'PAT-008', 'DOC-008', 'Loét dạ dày', 'Dùng thuốc bảo vệ dạ dày, kiêng rượu bia', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-009', 'PAT-009', 'DOC-009', 'Viêm da dị ứng', 'Bôi kem chống dị ứng, tránh tiếp xúc chất gây dị ứng', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-010', 'PAT-010', 'DOC-010', 'Viêm khớp', 'Dùng thuốc giảm đau, vật lý trị liệu', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-011', 'PAT-011', 'DOC-001', 'Cảm cúm', 'Nghỉ ngơi, uống nhiều nước', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-012', 'PAT-012', 'DOC-002', 'Viêm họng', 'Súc miệng nước muối, dùng thuốc kháng viêm', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-013', 'PAT-013', 'DOC-003', 'Sốt cao', 'Hạ sốt, theo dõi nhiệt độ', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-014', 'PAT-014', 'DOC-004', 'Thai kỳ 16 tuần', 'Khám định kỳ, siêu âm', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-015', 'PAT-015', 'DOC-005', 'Rối loạn nhịp tim', 'Dùng thuốc điều hòa nhịp tim', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-016', 'PAT-016', 'DOC-006', 'Hen suyễn', 'Dùng thuốc giãn phế quản', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-017', 'PAT-017', 'DOC-007', 'Mất ngủ', 'Dùng thuốc an thần nhẹ, thư giãn', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-018', 'PAT-018', 'DOC-008', 'Rối loạn tiêu hóa', 'Dùng men vi sinh, ăn uống lành mạnh', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-019', 'PAT-019', 'DOC-009', 'Chàm', 'Bôi kem dưỡng ẩm, tránh gãi', '2025-05-02', FALSE, '2025-05-02 10:00:00'),
('MR-020', 'PAT-020', 'DOC-010', 'Thoái hóa khớp', 'Vật lý trị liệu, giảm cân', '2025-05-02', FALSE, '2025-05-02 10:00:00');

-- Thêm dữ liệu vào bảng VitalSigns (Chỉ số sức khỏe)
-- Mỗi bệnh nhân có 1 bản ghi chỉ số sức khỏe
INSERT INTO VitalSigns (VitalSignID, PatientID, Temperature, SystolicPressure, DiastolicPressure, HeartRate, OxygenSaturation, RecordedAt) VALUES
('VS-001', 'PAT-001', 37.5, 120, 80, 75, 98.5, '2025-05-02 10:00:00'),
('VS-002', 'PAT-002', 36.8, 130, 85, 80, 97.0, '2025-05-02 10:00:00'),
('VS-003', 'PAT-003', 38.2, 110, 70, 90, 96.5, '2025-05-02 10:00:00'),
('VS-004', 'PAT-004', 36.5, 115, 75, 70, 99.0, '2025-05-02 10:00:00'),
('VS-005', 'PAT-005', 37.0, 140, 90, 85, 98.0, '2025-05-02 10:00:00'),
('VS-006', 'PAT-006', 38.0, 125, 80, 88, 95.5, '2025-05-02 10:00:00'),
('VS-007', 'PAT-007', 36.9, 120, 78, 72, 97.5, '2025-05-02 10:00:00'),
('VS-008', 'PAT-008', 37.2, 118, 76, 78, 98.0, '2025-05-02 10:00:00'),
('VS-009', 'PAT-009', 37.8, 122, 82, 80, 96.0, '2025-05-02 10:00:00'),
('VS-010', 'PAT-010', 36.7, 124, 80, 74, 98.5, '2025-05-02 10:00:00'),
('VS-011', 'PAT-011', 37.4, 119, 79, 76, 97.5, '2025-05-02 10:00:00'),
('VS-012', 'PAT-012', 38.1, 121, 81, 82, 96.5, '2025-05-02 10:00:00'),
('VS-013', 'PAT-013', 38.5, 115, 75, 90, 95.0, '2025-05-02 10:00:00'),
('VS-014', 'PAT-014', 36.6, 117, 77, 68, 99.0, '2025-05-02 10:00:00'),
('VS-015', 'PAT-015', 37.1, 138, 88, 84, 97.0, '2025-05-02 10:00:00'),
('VS-016', 'PAT-016', 37.9, 123, 80, 86, 94.5, '2025-05-02 10:00:00'),
('VS-017', 'PAT-017', 36.8, 120, 78, 70, 98.0, '2025-05-02 10:00:00'),
('VS-018', 'PAT-018', 37.3, 116, 76, 77, 97.5, '2025-05-02 10:00:00'),
('VS-019', 'PAT-019', 37.7, 124, 82, 79, 96.0, '2025-05-02 10:00:00'),
('VS-020', 'PAT-020', 36.9, 122, 80, 73, 98.5, '2025-05-02 10:00:00');

-- Thêm dữ liệu vào bảng Medications (Danh sách thuốc)
INSERT INTO Medications (MedicationID, MedicineName, Description, Manufacturer, DosageForm, SideEffects, CreatedAt) VALUES
('MED-001', 'Paracetamol', 'Giảm đau, hạ sốt', 'Pharma VN', 'Viên nén', 'Buồn nôn, dị ứng', '2025-05-02 10:00:00'),
('MED-002', 'Amoxicillin', 'Kháng sinh', 'Pharma VN', 'Viên nang', 'Tiêu chảy, dị ứng', '2025-05-02 10:00:00'),
('MED-003', 'Omeprazole', 'Bảo vệ dạ dày', 'Pharma VN', 'Viên nang', 'Đau đầu, buồn nôn', '2025-05-02 10:00:00'),
('MED-004', 'Amlodipine', 'Hạ huyết áp', 'Pharma VN', 'Viên nén', 'Chóng mặt, mệt mỏi', '2025-05-02 10:00:00'),
('MED-005', 'Ventolin', 'Giãn phế quản', 'Pharma VN', 'Dung dịch hít', 'Run tay, tim đập nhanh', '2025-05-02 10:00:00'),
('MED-006', 'Ibuprofen', 'Giảm đau, chống viêm', 'Pharma VN', 'Viên nén', 'Đau dạ dày, buồn nôn', '2025-05-02 10:00:00'),
('MED-007', 'Loratadine', 'Chống dị ứng', 'Pharma VN', 'Viên nén', 'Buồn ngủ, khô miệng', '2025-05-02 10:00:00'),
('MED-008', 'Metformin', 'Điều trị tiểu đường', 'Pharma VN', 'Viên nén', 'Buồn nôn, tiêu chảy', '2025-05-02 10:00:00'),
('MED-009', 'Prednisolone', 'Chống viêm', 'Pharma VN', 'Viên nén', 'Tăng cân, mất ngủ', '2025-05-02 10:00:00'),
('MED-010', 'Atorvastatin', 'Hạ cholesterol', 'Pharma VN', 'Viên nén', 'Đau cơ, mệt mỏi', '2025-05-02 10:00:00');

-- Thêm dữ liệu vào bảng Prescriptions (Đơn thuốc)
-- Mỗi bệnh nhân có 1 đơn thuốc
INSERT INTO Prescriptions (PrescriptionID, PatientID, DoctorID, PrescriptionDate, CreatedAt) VALUES
('PR-001', 'PAT-001', 'DOC-001', '2025-05-02', '2025-05-02 10:00:00'),
('PR-002', 'PAT-002', 'DOC-002', '2025-05-02', '2025-05-02 10:00:00'),
('PR-003', 'PAT-003', 'DOC-003', '2025-05-02', '2025-05-02 10:00:00'),
('PR-004', 'PAT-004', 'DOC-004', '2025-05-02', '2025-05-02 10:00:00'),
('PR-005', 'PAT-005', 'DOC-005', '2025-05-02', '2025-05-02 10:00:00'),
('PR-006', 'PAT-006', 'DOC-006', '2025-05-02', '2025-05-02 10:00:00'),
('PR-007', 'PAT-007', 'DOC-007', '2025-05-02', '2025-05-02 10:00:00'),
('PR-008', 'PAT-008', 'DOC-008', '2025-05-02', '2025-05-02 10:00:00'),
('PR-009', 'PAT-009', 'DOC-009', '2025-05-02', '2025-05-02 10:00:00'),
('PR-010', 'PAT-010', 'DOC-010', '2025-05-02', '2025-05-02 10:00:00'),
('PR-011', 'PAT-011', 'DOC-001', '2025-05-02', '2025-05-02 10:00:00'),
('PR-012', 'PAT-012', 'DOC-002', '2025-05-02', '2025-05-02 10:00:00'),
('PR-013', 'PAT-013', 'DOC-003', '2025-05-02', '2025-05-02 10:00:00'),
('PR-014', 'PAT-014', 'DOC-004', '2025-05-02', '2025-05-02 10:00:00'),
('PR-015', 'PAT-015', 'DOC-005', '2025-05-02', '2025-05-02 10:00:00'),
('PR-016', 'PAT-016', 'DOC-006', '2025-05-02', '2025-05-02 10:00:00'),
('PR-017', 'PAT-017', 'DOC-007', '2025-05-02', '2025-05-02 10:00:00'),
('PR-018', 'PAT-018', 'DOC-008', '2025-05-02', '2025-05-02 10:00:00'),
('PR-019', 'PAT-019', 'DOC-009', '2025-05-02', '2025-05-02 10:00:00'),
('PR-020', 'PAT-020', 'DOC-010', '2025-05-02', '2025-05-02 10:00:00');

-- Thêm dữ liệu vào bảng PrescriptionDetails (Chi tiết đơn thuốc)
-- Mỗi đơn thuốc có 1-3 loại thuốc
INSERT INTO PrescriptionDetails (PrescriptionID, MedicationID, Dosage, Instructions) VALUES
('PR-001', 'MED-001', '500mg x 2', 'Uống sau ăn, 2 lần/ngày'),
('PR-001', 'MED-002', '500mg x 3', 'Uống trước ăn, 3 lần/ngày'),
('PR-002', 'MED-003', '20mg x 1', 'Uống trước ăn, 1 lần/ngày'),
('PR-002', 'MED-001', '500mg x 2', 'Uống sau ăn, 2 lần/ngày'),
('PR-003', 'MED-001', '500mg x 2', 'Uống sau ăn, 2 lần/ngày'),
('PR-004', 'MED-001', '500mg x 1', 'Uống khi cần, tối đa 2 lần/ngày'),
('PR-005', 'MED-004', '5mg x 1', 'Uống buổi sáng, 1 lần/ngày'),
('PR-005', 'MED-010', '20mg x 1', 'Uống buổi tối, 1 lần/ngày'),
('PR-006', 'MED-005', '100mcg x 2', 'Hít khi cần, tối đa 4 lần/ngày'),
('PR-006', 'MED-002', '500mg x 3', 'Uống trước ăn, 3 lần/ngày'),
('PR-007', 'MED-006', '400mg x 2', 'Uống sau ăn, 2 lần/ngày'),
('PR-008', 'MED-003', '20mg x 1', 'Uống trước ăn, 1 lần/ngày'),
('PR-009', 'MED-007', '10mg x 1', 'Uống buổi tối, 1 lần/ngày'),
('PR-009', 'MED-009', '5mg x 1', 'Uống buổi sáng, 1 lần/ngày'),
('PR-010', 'MED-006', '400mg x 2', 'Uống sau ăn, 2 lần/ngày'),
('PR-011', 'MED-001', '500mg x 2', 'Uống sau ăn, 2 lần/ngày'),
('PR-012', 'MED-002', '500mg x 3', 'Uống trước ăn, 3 lần/ngày'),
('PR-013', 'MED-001', '500mg x 2', 'Uống sau ăn, 2 lần/ngày'),
('PR-014', 'MED-001', '500mg x 1', 'Uống khi cần, tối đa 2 lần/ngày'),
('PR-015', 'MED-004', '5mg x 1', 'Uống buổi sáng, 1 lần/ngày'),
('PR-016', 'MED-005', '100mcg x 2', 'Hít khi cần, tối đa 4 lần/ngày'),
('PR-017', 'MED-006', '400mg x 2', 'Uống sau ăn, 2 lần/ngày'),
('PR-018', 'MED-003', '20mg x 1', 'Uống trước ăn, 1 lần/ngày'),
('PR-019', 'MED-007', '10mg x 1', 'Uống buổi tối, 1 lần/ngày'),
('PR-020', 'MED-006', '400mg x 2', 'Uống sau ăn, 2 lần/ngày');

-- Thêm dữ liệu vào bảng Insurance (Bảo hiểm y tế)
-- Mỗi bệnh nhân có thông tin bảo hiểm
INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, CoverageDetails, Status, CreatedAt) VALUES
('INS-001', 'PAT-001', 'Bảo Việt', 'BV001', '2025-01-01', '2025-12-31', 'Chi trả 80% chi phí khám chữa bệnh', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-002', 'PAT-002', 'Prudential', 'PRU002', '2025-01-01', '2025-12-31', 'Chi trả 70% chi phí nội trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-003', 'PAT-003', 'Bảo Minh', 'BM003', '2025-01-01', '2025-12-31', 'Chi trả 90% chi phí ngoại trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-004', 'PAT-004', 'Manulife', 'MAN004', '2025-01-01', '2025-12-31', 'Chi trả 60% chi phí sinh đẻ', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-005', 'PAT-005', 'Bảo Việt', 'BV005', '2025-01-01', '2025-12-31', 'Chi trả 80% chi phí khám chữa bệnh', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-006', 'PAT-006', 'Prudential', 'PRU006', '2025-01-01', '2025-12-31', 'Chi trả 70% chi phí nội trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-007', 'PAT-007', 'Bảo Minh', 'BM007', '2025-01-01', '2025-12-31', 'Chi trả 90% chi phí ngoại trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-008', 'PAT-008', 'Manulife', 'MAN008', '2025-01-01', '2025-12-31', 'Chi trả 60% chi phí khám chữa bệnh', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-009', 'PAT-009', 'Bảo Việt', 'BV009', '2025-01-01', '2025-12-31', 'Chi trả 80% chi phí khám chữa bệnh', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-010', 'PAT-010', 'Prudential', 'PRU010', '2025-01-01', '2025-12-31', 'Chi trả 70% chi phí nội trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-011', 'PAT-011', 'Bảo Minh', 'BM011', '2025-01-01', '2025-12-31', 'Chi trả 90% chi phí ngoại trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-012', 'PAT-012', 'Manulife', 'MAN012', '2025-01-01', '2025-12-31', 'Chi trả 60% chi phí khám chữa bệnh', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-013', 'PAT-013', 'Bảo Việt', 'BV013', '2025-01-01', '2025-12-31', 'Chi trả 80% chi phí khám chữa bệnh', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-014', 'PAT-014', 'Prudential', 'PRU014', '2025-01-01', '2025-12-31', 'Chi trả 70% chi phí nội trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-015', 'PAT-015', 'Bảo Minh', 'BM015', '2025-01-01', '2025-12-31', 'Chi trả 90% chi phí ngoại trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-016', 'PAT-016', 'Manulife', 'MAN016', '2025-01-01', '2025-12-31', 'Chi trả 60% chi phí khám chữa bệnh', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-017', 'PAT-017', 'Bảo Việt', 'BV017', '2025-01-01', '2025-12-31', 'Chi trả 80% chi phí khám chữa bệnh', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-018', 'PAT-018', 'Prudential', 'PRU018', '2025-01-01', '2025-12-31', 'Chi trả 70% chi phí nội trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-019', 'PAT-019', 'Bảo Minh', 'BM019', '2025-01-01', '2025-12-31', 'Chi trả 90% chi phí ngoại trú', 'Hoạt Động', '2025-05-02 10:00:00'),
('INS-020', 'PAT-020', 'Manulife', 'MAN020', '2025-01-01', '2025-12-31', 'Chi trả 60% chi phí khám chữa bệnh', 'Hoạt Động', '2025-05-02 10:00:00');

-- Thêm dữ liệu vào bảng HospitalRooms (Phòng bệnh viện)
INSERT INTO HospitalRooms (RoomID, RoomType, TotalBeds, AvailableBeds, FloorNumber, Status, CreatedAt) VALUES
('ROOM-001', 'Tiêu chuẩn', 4, 4, 1, 'Trống', '2025-05-02 10:00:00'),
('ROOM-002', 'Tiêu chuẩn', 4, 4, 1, 'Trống', '2025-05-02 10:00:00'),
('ROOM-003', 'VIP', 2, 2, 2, 'Trống', '2025-05-02 10:00:00'),
('ROOM-004', 'VIP', 2, 2, 2, 'Trống', '2025-05-02 10:00:00'),
('ROOM-005', 'ICU', 1, 1, 3, 'Trống', '2025-05-02 10:00:00'),
('ROOM-006', 'ICU', 1, 1, 3, 'Trống', '2025-05-02 10:00:00'),
('ROOM-007', 'Cấp cứu', 3, 3, 1, 'Trống', '2025-05-02 10:00:00'),
('ROOM-008', 'Cấp cứu', 3, 3, 1, 'Trống', '2025-05-02 10:00:00'),
('ROOM-009', 'Tiêu chuẩn', 4, 4, 4, 'Trống', '2025-05-02 10:00:00'),
('ROOM-010', 'VIP', 2, 2, 4, 'Trống', '2025-05-02 10:00:00');

-- Thêm dữ liệu vào bảng Admissions (Nhập viện)
-- 10 bệnh nhân được nhập viện
INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, DischargeDate, Notes, CreatedAt) VALUES
('ADM-001', 'PAT-001', 'DOC-001', 'ROOM-001', '2025-05-02', NULL, 'Nhập viện theo dõi cảm cúm', '2025-05-02 10:00:00'),
('ADM-002', 'PAT-002', 'DOC-002', 'ROOM-002', '2025-05-02', '2025-05-04', 'Nhập viện điều trị viêm dạ dày', '2025-05-02 10:00:00'),
('ADM-003', 'PAT-003', 'DOC-003', 'ROOM-005', '2025-05-02', NULL, 'Nhập viện ICU do sốt xuất huyết', '2025-05-02 10:00:00'),
('ADM-004', 'PAT-004', 'DOC-004', 'ROOM-003', '2025-05-02', NULL, 'Nhập viện theo dõi thai kỳ', '2025-05-02 10:00:00'),
('ADM-005', 'PAT-005', 'DOC-005', 'ROOM-001', '2025-05-02', '2025-05-03', 'Nhập viện điều trị tăng huyết áp', '2025-05-02 10:00:00'),
('ADM-006', 'PAT-006', 'DOC-006', 'ROOM-006', '2025-05-02', NULL, 'Nhập viện ICU do viêm phổi', '2025-05-02 10:00:00'),
('ADM-007', 'PAT-007', 'DOC-007', 'ROOM-002', '2025-05-02', NULL, 'Nhập viện theo dõi đau nửa đầu', '2025-05-02 10:00:00'),
('ADM-008', 'PAT-008', 'DOC-008', 'ROOM-004', '2025-05-02', '2025-05-05', 'Nhập viện điều trị loét dạ dày', '2025-05-02 10:00:00'),
('ADM-009', 'PAT-009', 'DOC-009', 'ROOM-007', '2025-05-02', NULL, 'Nhập viện cấp cứu do viêm da', '2025-05-02 10:00:00'),
('ADM-010', 'PAT-010', 'DOC-010', 'ROOM-009', '2025-05-02', NULL, 'Nhập viện điều trị viêm khớp', '2025-05-02 10:00:00');