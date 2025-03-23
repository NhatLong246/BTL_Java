package entity;

public class Staff {
	    private String staffID;    // Mã nhân viên
	    private String fullName;   // Họ và tên
	    private String role;       // Vai trò (Y tá, Hộ lý, Nhân viên hành chính, ...)
	    private String phoneNumber;// Số điện thoại
	    private String email;      // Email
	    private String department; // Khoa/phòng ban làm việc

	    // Constructor
	    public Staff(String staffID, String fullName, String role, String phoneNumber, String email, String department) {
	        this.staffID = staffID;
	        this.fullName = fullName;
	        this.role = role;
	        this.phoneNumber = phoneNumber;
	        this.email = email;
	        this.department = department;
	    }

	    public String getStaffID() {
	        return staffID;
	    }

	    public void setStaffID(String staffID) {
	        this.staffID = staffID;
	    }

	    public String getFullName() {
	        return fullName;
	    }

	    public void setFullName(String fullName) {
	        this.fullName = fullName;
	    }

	    public String getRole() {
	        return role;
	    }

	    public void setRole(String role) {
	        this.role = role;
	    }

	    public String getPhoneNumber() {
	        return phoneNumber;
	    }

	    public void setPhoneNumber(String phoneNumber) {
	        this.phoneNumber = phoneNumber;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getDepartment() {
	        return department;
	    }

	    public void setDepartment(String department) {
	        this.department = department;
	    }

	    // Hiển thị thông tin nhân viên
	    public void displayStaffInfo() {
	        System.out.println("Staff ID: " + staffID);
	        System.out.println("Full Name: " + fullName);
	        System.out.println("Role: " + role);
	        System.out.println("Phone Number: " + phoneNumber);
	        System.out.println("Email: " + email);
	        System.out.println("Department: " + department);
	        System.out.println("------------------------------------");
	    }
}
