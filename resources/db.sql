Drop database if exists PatientManagement;
CREATE DATABASE PatientManagement;
USE PatientManagement;

-- Bảng Tài Khoản Người Dùng
CREATE TABLE UserAccounts (
    UserID VARCHAR(50) PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Role ENUM('Bác sĩ', 'Bệnh nhân', 'Quản lí') NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    PhoneNumber VARCHAR(15) UNIQUE,
    PasswordHash VARCHAR(255) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
    SpecialtyID VARCHAR(50),
    Address TEXT,
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (SpecialtyID) REFERENCES Specialties(SpecialtyID) 
        ON DELETE SET NULL ON UPDATE CASCADE
); 

-- Bảng Bệnh Nhân
CREATE TABLE Patients (
    PatientID VARCHAR(50) PRIMARY KEY,
    UserID VARCHAR(50) UNIQUE NOT NULL,
    DateOfBirth DATE NOT NULL,
    Gender ENUM('Nam', 'Nữ') NOT NULL,
    Address TEXT,
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bảng Cuộc Hẹn
CREATE TABLE Appointments (
    AppointmentID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    DoctorID VARCHAR(50),
    AppointmentDate DATETIME NOT NULL,
    Status ENUM('Chờ', 'Hoàn thành', 'Hủy') DEFAULT 'Chờ',
    Notes TEXT,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) 
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Bảng Dịch Vụ 
CREATE TABLE Services (
    ServiceID VARCHAR(50) PRIMARY KEY,
    ServiceName VARCHAR(255) NOT NULL,
    Cost DECIMAL(10,2) NOT NULL CHECK (Cost >= 0)
);

-- Bảng Hóa Đơn
CREATE TABLE Billing (
    BillID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    TotalAmount DECIMAL(10,2) NOT NULL CHECK (TotalAmount >= 0),
    PaymentMethod ENUM('Tiền mặt', 'Chuyển khoản') COLLATE utf8mb4_unicode_ci,
    Status ENUM('Chưa thanh toán', 'Đã thanh toán') DEFAULT 'Chưa thanh toán',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) ON DELETE CASCADE ON UPDATE CASCADE
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

-- Bảng Hồ Sơ Y Tế
CREATE TABLE MedicalRecords (
    RecordID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50), 
    DoctorID VARCHAR(50),
    Diagnosis TEXT NOT NULL,
    TreatmentPlan TEXT NOT NULL,
    RecordDate DATE NOT NULL,
    IsHistory BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) 
        ON DELETE SET NULL ON UPDATE CASCADE
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
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bảng Đơn thuốc
CREATE TABLE Prescriptions (
    PrescriptionID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    PrescriptionDate DATE NOT NULL,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) 
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Bảng Thuốc
CREATE TABLE Medications (
    MedicationID VARCHAR(50) PRIMARY KEY,
    MedicineName VARCHAR(255) NOT NULL,
    Description TEXT,
    Manufacturer VARCHAR(255),
    DosageForm VARCHAR(100),
    SideEffects TEXT
);

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
-- Bảng Xét Nghiệm
CREATE TABLE LabTests (
    LabTestID VARCHAR(50) PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description TEXT,
    Cost DECIMAL(10,2)
);

-- Bảng Kết Quả Xét Nghiệm
CREATE TABLE LabResults (
    ResultID VARCHAR(50),
    LabTestID VARCHAR(50),
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    Result TEXT,
    TestDate DATE NOT NULL,
    PRIMARY KEY (ResultID, LabTestID), 
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) ON DELETE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) ON DELETE SET NULL,
    FOREIGN KEY (LabTestID) REFERENCES LabTests(LabTestID) ON DELETE CASCADE
);

-- Bảng Insurance (Thông tin bảo hiểm y tế)
CREATE TABLE Insurance (
    InsuranceID VARCHAR(20) PRIMARY KEY,
    PatientID VARCHAR(20),
    Provider TEXT NOT NULL,
    PolicyNumber VARCHAR(100) UNIQUE NOT NULL,
    StartDate DATE NOT NULL,
    ExpirationDate DATE NOT NULL,
    CoverageDetails TEXT,
    Status ENUM('Hoạt Động', 'Hết Hạn', 'Không Xác Định') DEFAULT 'Hoạt Động',
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) ON DELETE SET NULL,
    INDEX (PatientID),
    CHECK (StartDate < ExpirationDate)
);

-- Bảng HospitalRooms (Danh sách phòng bệnh viện)
CREATE TABLE HospitalRooms (
    RoomID VARCHAR(20) PRIMARY KEY,
    RoomNumber VARCHAR(50) UNIQUE NOT NULL,
    Type VARCHAR(100),
    Capacity INT,
    FloorNumber INT,
    Status TEXT
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
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (RoomID) REFERENCES HospitalRooms(RoomID) 
        ON DELETE SET NULL ON UPDATE CASCADE
);


-- Thêm dữ liệu vào bảng UserAccounts
INSERT INTO UserAccounts (UserID, FullName, Role, Email, PhoneNumber, PasswordHash) VALUES
('U001', 'Nguyễn Văn A', 'Bác sĩ', 'nguyenvana@example.com', '0987654321', 'hashed_password_1'),
('U002', 'Trần Thị B', 'Bệnh nhân', 'tranthib@example.com', '0976543210', 'hashed_password_2'),
('U003', 'Lê Văn C', 'Quản lí', 'levanc@example.com', '0965432109', 'hashed_password_3');

-- Thêm dữ liệu vào bảng Specialties
INSERT INTO Specialties (SpecialtyID, SpecialtyName) VALUES
('S001', 'Tim mạch'),
('S002', 'Nội tiết'),
('S003', 'Nhi khoa');

-- Thêm dữ liệu vào bảng Doctors
INSERT INTO Doctors (DoctorID, UserID, SpecialtyID, Address) VALUES
('D001', 'U001', 'S001', '123 Đường ABC, TP.HCM');

-- Thêm dữ liệu vào bảng Patients
INSERT INTO Patients (PatientID, UserID, DateOfBirth, Gender, Address) VALUES
('P001', 'U002', '1990-05-15', 'Nam', '456 Đường XYZ, Hà Nội');

-- Thêm dữ liệu vào bảng Appointments
INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Status, Notes) VALUES
('A001', 'P001', 'D001', '2025-04-10 10:00:00', 'Chờ', 'Khám tổng quát');

-- Thêm dữ liệu vào bảng Services
INSERT INTO Services (ServiceID, ServiceName, Cost) VALUES
('SV001', 'Khám sức khỏe tổng quát', 500000),
('SV002', 'Siêu âm tim', 700000);

-- Thêm dữ liệu vào bảng Billing
INSERT INTO Billing (BillID, PatientID, TotalAmount, PaymentMethod, Status) VALUES
('B001', 'P001', 500000, 'Tiền mặt', 'Chưa thanh toán'),
('B002', 'P001', 100000, 'Tiền mặt', 'Chưa thanh toán');

-- Thêm dữ liệu vào bảng BillingDetails
INSERT INTO BillingDetails (BillID, ServiceID, Amount) VALUES
('B001', 'SV001', 500000);

-- Thêm dữ liệu vào bảng MedicalRecords
INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, TreatmentPlan, RecordDate) VALUES
('MR001', 'P001', 'D001', 'Huyết áp cao', 'Theo dõi và điều trị bằng thuốc', '2025-04-10');

-- Thêm dữ liệu vào bảng VitalSigns
INSERT INTO VitalSigns (VitalSignID, PatientID, Temperature, BloodPressure, HeartRate, OxygenSaturation) VALUES
('VS001', 'P001', 36.8, '120/80', 75, 98.0);

-- Thêm dữ liệu vào bảng Prescriptions
INSERT INTO Prescriptions (PrescriptionID, PatientID, DoctorID, PrescriptionDate) VALUES
('PR001', 'P001', 'D001', '2025-04-10');

-- Thêm dữ liệu vào bảng Medications
INSERT INTO Medications (MedicationID, MedicineName, Description, Manufacturer, DosageForm, SideEffects) VALUES
('M001', 'Paracetamol', 'Giảm đau hạ sốt', 'Dược phẩm Việt Nam', 'Viên nén', 'Buồn nôn, chóng mặt');

-- Thêm dữ liệu vào bảng PrescriptionDetails
INSERT INTO PrescriptionDetails (PrescriptionID, MedicationID, Dosage, Instructions) VALUES
('PR001', 'M001', '500mg', 'Uống sau khi ăn');

-- Thêm dữ liệu vào bảng LabTests
INSERT INTO LabTests (LabTestID, Name, Description, Cost) VALUES
('LT001', 'Xét nghiệm máu', 'Kiểm tra chỉ số máu', 300000);

-- Thêm dữ liệu vào bảng LabResults
INSERT INTO LabResults (ResultID, LabTestID, PatientID, DoctorID, Result, TestDate) VALUES
('LR001', 'LT001', 'P001', 'D001', 'Bình thường', '2025-04-10');

-- Thêm dữ liệu vào bảng Insurance
INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, CoverageDetails, Status) VALUES
('I001', 'P001', 'Bảo hiểm ABC', 'BH123456', '2024-01-01', '2026-01-01', 'Bảo hiểm y tế cơ bản', 'Hoạt Động');

-- Thêm dữ liệu vào bảng HospitalRooms
INSERT INTO HospitalRooms (RoomID, RoomNumber, Type, Capacity, FloorNumber, Status) VALUES
('R001', '101', 'Phòng đơn', 1, 1, 'Trống');

-- Thêm dữ liệu vào bảng Admissions
INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, Notes) VALUES
('AD001', 'P001', 'D001', 'R001', '2025-04-10', 'Nhập viện để theo dõi huyết áp cao');



Drop database if exists PatientManagement;
CREATE DATABASE PatientManagement;
USE PatientManagement;

-- Bảng Tài Khoản Người Dùng
CREATE TABLE UserAccounts (
    UserID VARCHAR(50) PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Role ENUM('Bác sĩ', 'Bệnh nhân', 'Quản lí') NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    PhoneNumber VARCHAR(15) UNIQUE,
    PasswordHash VARCHAR(255) NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
    SpecialtyID VARCHAR(50),
    Address TEXT,
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (SpecialtyID) REFERENCES Specialties(SpecialtyID) 
        ON DELETE SET NULL ON UPDATE CASCADE
); 

-- Bảng Bệnh Nhân
CREATE TABLE Patients (
    PatientID VARCHAR(50) PRIMARY KEY,
    UserID VARCHAR(50) UNIQUE NOT NULL,
    DateOfBirth DATE NOT NULL,
    Gender ENUM('Nam', 'Nữ') NOT NULL,
    Address TEXT,
    FOREIGN KEY (UserID) REFERENCES UserAccounts(UserID) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bảng Cuộc Hẹn
CREATE TABLE Appointments (
    AppointmentID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    DoctorID VARCHAR(50),
    AppointmentDate DATETIME NOT NULL,
    Status ENUM('Chờ', 'Hoàn thành', 'Hủy') DEFAULT 'Chờ',
    Notes TEXT,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) 
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Bảng Dịch Vụ 
CREATE TABLE Services (
    ServiceID VARCHAR(50) PRIMARY KEY,
    ServiceName VARCHAR(255) NOT NULL,
    Cost DECIMAL(10,2) NOT NULL CHECK (Cost >= 0)
);

-- Bảng Hóa Đơn
CREATE TABLE Billing (
    BillID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50) NOT NULL,
    TotalAmount DECIMAL(10,2) NOT NULL CHECK (TotalAmount >= 0),
    PaymentMethod ENUM('Tiền mặt', 'Chuyển khoản') COLLATE utf8mb4_unicode_ci,
    Status ENUM('Chưa thanh toán', 'Đã thanh toán') DEFAULT 'Chưa thanh toán',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) ON DELETE CASCADE ON UPDATE CASCADE
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

-- Bảng Hồ Sơ Y Tế
CREATE TABLE MedicalRecords (
    RecordID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50), 
    DoctorID VARCHAR(50),
    Diagnosis TEXT NOT NULL,
    TreatmentPlan TEXT NOT NULL,
    RecordDate DATE NOT NULL,
    IsHistory BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) 
        ON DELETE SET NULL ON UPDATE CASCADE
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
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bảng Đơn thuốc
CREATE TABLE Prescriptions (
    PrescriptionID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    PrescriptionDate DATE NOT NULL,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) 
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Bảng Thuốc
CREATE TABLE Medications (
    MedicationID VARCHAR(50) PRIMARY KEY,
    MedicineName VARCHAR(255) NOT NULL,
    Description TEXT,
    Manufacturer VARCHAR(255),
    DosageForm VARCHAR(100),
    SideEffects TEXT
);

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
-- Bảng Xét Nghiệm
CREATE TABLE LabTests (
    LabTestID VARCHAR(50) PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description TEXT,
    Cost DECIMAL(10,2)
);

-- Bảng Kết Quả Xét Nghiệm
CREATE TABLE LabResults (
    ResultID VARCHAR(50),
    LabTestID VARCHAR(50),
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    Result TEXT,
    TestDate DATE NOT NULL,
    PRIMARY KEY (ResultID, LabTestID), 
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) ON DELETE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) ON DELETE SET NULL,
    FOREIGN KEY (LabTestID) REFERENCES LabTests(LabTestID) ON DELETE CASCADE
);

-- Bảng Insurance (Thông tin bảo hiểm y tế)
CREATE TABLE Insurance (
    InsuranceID VARCHAR(20) PRIMARY KEY,
    PatientID VARCHAR(20),
    Provider TEXT NOT NULL,
    PolicyNumber VARCHAR(100) UNIQUE NOT NULL,
    StartDate DATE NOT NULL,
    ExpirationDate DATE NOT NULL,
    CoverageDetails TEXT,
    Status ENUM('Hoạt Động', 'Hết Hạn', 'Không Xác Định') DEFAULT 'Hoạt Động',
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) ON DELETE SET NULL,
    INDEX (PatientID),
    CHECK (StartDate < ExpirationDate)
);

-- Bảng HospitalRooms (Danh sách phòng bệnh viện)
CREATE TABLE HospitalRooms (
    RoomID VARCHAR(20) PRIMARY KEY,
    RoomNumber VARCHAR(50) UNIQUE NOT NULL,
    Type VARCHAR(100),
    Capacity INT,
    FloorNumber INT,
    Status TEXT
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
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (RoomID) REFERENCES HospitalRooms(RoomID) 
        ON DELETE SET NULL ON UPDATE CASCADE
);


-- Thêm dữ liệu vào bảng UserAccounts
INSERT INTO UserAccounts (UserID, FullName, Role, Email, PhoneNumber, PasswordHash) VALUES
('U001', 'Nguyễn Văn A', 'Bác sĩ', 'nguyenvana@example.com', '0987654321', 'hashed_password_1'),
('U002', 'Trần Thị B', 'Bệnh nhân', 'tranthib@example.com', '0976543210', 'hashed_password_2'),
('U003', 'Lê Văn C', 'Quản lí', 'levanc@example.com', '0965432109', 'hashed_password_3');

-- Thêm dữ liệu vào bảng Specialties
INSERT INTO Specialties (SpecialtyID, SpecialtyName) VALUES
('S001', 'Tim mạch'),
('S002', 'Nội tiết'),
('S003', 'Nhi khoa');

-- Thêm dữ liệu vào bảng Doctors
INSERT INTO Doctors (DoctorID, UserID, SpecialtyID, Address) VALUES
('D001', 'U001', 'S001', '123 Đường ABC, TP.HCM');

-- Thêm dữ liệu vào bảng Patients
INSERT INTO Patients (PatientID, UserID, DateOfBirth, Gender, Address) VALUES
('P001', 'U002', '1990-05-15', 'Nam', '456 Đường XYZ, Hà Nội');

-- Thêm dữ liệu vào bảng Appointments
INSERT INTO Appointments (AppointmentID, PatientID, DoctorID, AppointmentDate, Status, Notes) VALUES
('A001', 'P001', 'D001', '2025-04-10 10:00:00', 'Chờ', 'Khám tổng quát');

-- Thêm dữ liệu vào bảng Services
INSERT INTO Services (ServiceID, ServiceName, Cost) VALUES
('SV001', 'Khám sức khỏe tổng quát', 500000),
('SV002', 'Siêu âm tim', 700000);

-- Thêm dữ liệu vào bảng Billing
INSERT INTO Billing (BillID, PatientID, TotalAmount, PaymentMethod, Status) VALUES
('B001', 'P001', 500000, 'Tiền mặt', 'Chưa thanh toán'),
('B002', 'P001', 100000, 'Tiền mặt', 'Chưa thanh toán');

-- Thêm dữ liệu vào bảng BillingDetails
INSERT INTO BillingDetails (BillID, ServiceID, Amount) VALUES
('B001', 'SV001', 500000);

-- Thêm dữ liệu vào bảng MedicalRecords
INSERT INTO MedicalRecords (RecordID, PatientID, DoctorID, Diagnosis, TreatmentPlan, RecordDate) VALUES
('MR001', 'P001', 'D001', 'Huyết áp cao', 'Theo dõi và điều trị bằng thuốc', '2025-04-10');

-- Thêm dữ liệu vào bảng VitalSigns
INSERT INTO VitalSigns (VitalSignID, PatientID, Temperature, BloodPressure, HeartRate, OxygenSaturation) VALUES
('VS001', 'P001', 36.8, '120/80', 75, 98.0);

-- Thêm dữ liệu vào bảng Prescriptions
INSERT INTO Prescriptions (PrescriptionID, PatientID, DoctorID, PrescriptionDate) VALUES
('PR001', 'P001', 'D001', '2025-04-10');

-- Thêm dữ liệu vào bảng Medications
INSERT INTO Medications (MedicationID, MedicineName, Description, Manufacturer, DosageForm, SideEffects) VALUES
('M001', 'Paracetamol', 'Giảm đau hạ sốt', 'Dược phẩm Việt Nam', 'Viên nén', 'Buồn nôn, chóng mặt');

-- Thêm dữ liệu vào bảng PrescriptionDetails
INSERT INTO PrescriptionDetails (PrescriptionID, MedicationID, Dosage, Instructions) VALUES
('PR001', 'M001', '500mg', 'Uống sau khi ăn');

-- Thêm dữ liệu vào bảng LabTests
INSERT INTO LabTests (LabTestID, Name, Description, Cost) VALUES
('LT001', 'Xét nghiệm máu', 'Kiểm tra chỉ số máu', 300000);

-- Thêm dữ liệu vào bảng LabResults
INSERT INTO LabResults (ResultID, LabTestID, PatientID, DoctorID, Result, TestDate) VALUES
('LR001', 'LT001', 'P001', 'D001', 'Bình thường', '2025-04-10');

-- Thêm dữ liệu vào bảng Insurance
INSERT INTO Insurance (InsuranceID, PatientID, Provider, PolicyNumber, StartDate, ExpirationDate, CoverageDetails, Status) VALUES
('I001', 'P001', 'Bảo hiểm ABC', 'BH123456', '2024-01-01', '2026-01-01', 'Bảo hiểm y tế cơ bản', 'Hoạt Động');

-- Thêm dữ liệu vào bảng HospitalRooms
INSERT INTO HospitalRooms (RoomID, RoomNumber, Type, Capacity, FloorNumber, Status) VALUES
('R001', '101', 'Phòng đơn', 1, 1, 'Trống');

-- Thêm dữ liệu vào bảng Admissions
INSERT INTO Admissions (AdmissionID, PatientID, DoctorID, RoomID, AdmissionDate, Notes) VALUES
('AD001', 'P001', 'D001', 'R001', '2025-04-10', 'Nhập viện để theo dõi huyết áp cao');



