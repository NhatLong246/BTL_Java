package model.entity;

import model.enums.Gender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Patient extends Person{
	private String patientID;
	private String userID;    // Liên kết với tài khoản người dùng
	private LocalDate createdAt; // Ngày tạo (ngày nhập viện)
	private Insurance insurance; // Bảo hiểm của bệnh nhân (nếu có)

	private static int autoId = 1; //Biến đếm ID tự tăng của bệnh nhân

	//Tạo mã bệnh nhân tự động (PAT-001, PAT-002, ...)
	private String generatePatientID() {
		return String.format("PAT-%03d", autoId++);
	}

	public static String generateNewPatientID(Connection conn) {
		String newPatientID = "PAT-001"; // ID mặc định nếu bảng rỗng
		String sql = "SELECT MAX(CAST(REGEXP_SUBSTR(PatientID, '[0-9]+') AS UNSIGNED)) AS maxID FROM Patients";

		try (PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			if (rs.next() && rs.getInt("maxID") > 0) {
				int maxID = rs.getInt("maxID") + 1; // Lấy số lớn nhất +1
				newPatientID = String.format("PAT-%03d", maxID); // Định dạng PAT-XXX
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return newPatientID;
	}

	public Patient() {}

	// Constructor cho bệnh nhân mới (Tạo mới ID) -- Dùng cho việc thêm mới bệnh nhân
	public Patient(String userId, String fullName, LocalDate dateOfBirth, String address, Gender gender, String phoneNumber, Connection conn) {
		super(fullName, dateOfBirth, address, gender, phoneNumber);
		this.userID = userId;
		this.patientID = generateNewPatientID(conn); // Tạo ID khi thêm mới
		this.createdAt = LocalDate.now(); // Ngày nhập viện là ngày hiện tại
	}

	// Constructor cho bệnh nhân từ database (ID đã có, không tạo mới) -- Dùng cho việc sửa thông tin bệnh nhân
	public Patient(String userId, String patientId, String fullName, LocalDate dateOfBirth, String address, Gender gender, String phoneNumber, LocalDate createdAt) {
		super(fullName, dateOfBirth, address, gender, phoneNumber);
		this.userID = userId;
		this.patientID = patientId; // Giữ nguyên ID từ database
		this.createdAt = createdAt;
	}

	public String getPatientID() {
		return patientID;
	}

	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public Insurance getInsurance() {
		return insurance;
	}

	public void setInsurance(Insurance insurance) {
		this.insurance = insurance;
	}
}


/*
public static void insertNewPatient(Connection conn, String fullName, LocalDate dob, Gender gender, String address, String passwordHash) {
	String patientID = generateNewPatientID(conn); // Tạo mã bệnh nhân mới
	String userID = patientID; // Dùng luôn mã bệnh nhân làm UserID
	String role = "Bệnh nhân"; // Vai trò mặc định

	// Chèn vào bảng UserAccounts trước
	String userSql = "INSERT INTO UserAccounts (UserID, FullName, Role, Email, PasswordHash) VALUES (?, ?, ?, ?, ?)";
	try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
		userStmt.setString(1, userID);
		userStmt.setString(2, fullName);
		userStmt.setString(3, role);
		userStmt.setString(4, userID + "@benhvien.com"); // Tạo email giả định
		userStmt.setString(5, passwordHash);
		userStmt.executeUpdate();
	} catch (SQLException e) {
		e.printStackTrace();
		return; // Nếu lỗi thì dừng lại
	}

	// Chèn vào bảng Patients
	String patientSql = "INSERT INTO Patients (PatientID, UserID, DateOfBirth, Gender, Address) VALUES (?, ?, ?, ?, ?)";
	try (PreparedStatement patientStmt = conn.prepareStatement(patientSql)) {
		patientStmt.setString(1, patientID);
		patientStmt.setString(2, userID);
		patientStmt.setDate(3, Date.valueOf(dob));
		patientStmt.setString(4, gender.toString());
		patientStmt.setString(5, address);
		patientStmt.executeUpdate();
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
*/
