CREATE DATABASE IF NOT EXISTS PatientManagement;
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
    PaymentMethod ENUM('Tiền mặt', 'Chuyển khoản'),
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
    RoomType ENUM('Tiêu chuẩn', 'VIP', 'ICU', 'Cấp cứu') NOT NULL,
    TotalBeds INT CHECK (TotalBeds > 0),  -- Tổng số giường trong phòng
    AvailableBeds INT CHECK (AvailableBeds >= 0),  -- Số giường còn trống
    FloorNumber INT CHECK (FloorNumber > 0),
    Status ENUM('Trống', 'Đang sử dụng', 'Đầy', 'Bảo trì') DEFAULT 'Trống'
);

-- Bảng Nhập Viện
CREATE TABLE Admissions (
    AdmissionID VARCHAR(50) PRIMARY KEY,
    PatientID VARCHAR(50),
    DoctorID VARCHAR(50),
    RoomID VARCHAR(50),
    AdmissionDate DATE NOT NULL,
    DischargeDate DATE,
    Notes TEXT,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (DoctorID) REFERENCES Doctors(DoctorID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (RoomID) REFERENCES HospitalRooms(RoomID)
        ON DELETE SET NULL ON UPDATE CASCADE
);