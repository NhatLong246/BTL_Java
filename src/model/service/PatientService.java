package model.service;

import database.DatabaseConnection;
import model.entity.Patient;
import model.enums.Gender; // Thêm import cho enum Gender
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatientService {
    public Patient getPatientByUserId(String userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Patients WHERE UserID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String genderStr = rs.getString("Gender");
                Gender gender = (genderStr != null && !genderStr.isEmpty()) 
                    ? Gender.valueOf(genderStr) 
                    : Gender.MALE; // Giá trị mặc định nếu gender không hợp lệ
                return new Patient(
                    rs.getString("PatientID"),
                    rs.getString("UserID"),
                    rs.getString("FullName"),
                    rs.getDate("DateOfBirth") != null ? rs.getDate("DateOfBirth").toLocalDate() : null,
                    rs.getString("Address"),
                    gender,
                    rs.getString("PhoneNumber"),
                    rs.getDate("CreatedAt") != null ? rs.getDate("CreatedAt").toLocalDate() : null
                );
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getPatientByUserId: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Gender value in getPatientByUserId: " + e.getMessage());
            e.printStackTrace();
            // Trả về null hoặc xử lý lỗi theo cách khác nếu cần
        }
        return null;
    }
}