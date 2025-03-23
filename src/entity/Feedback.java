package entity;

import java.time.LocalDateTime;

public class Feedback {
	    private String feedbackID;  // Mã phản hồi
	    private String patientID;   // Mã bệnh nhân
	    private String doctorID;    // Mã bác sĩ
	    private int rating;         // Đánh giá (1-5 sao)
	    private String comments;    // Nội dung phản hồi
	    private LocalDateTime createdAt; // Thời gian gửi phản hồi

	    // Constructor
	    public Feedback(String feedbackID, String patientID, String doctorID, int rating, String comments, LocalDateTime createdAt) {
	        this.feedbackID = feedbackID;
	        this.patientID = patientID;
	        this.doctorID = doctorID;
	        setRating(rating);  // Sử dụng setter để kiểm tra hợp lệ
	        this.comments = comments;
	        this.createdAt = createdAt;
	    }

	    public String getFeedbackID() {
	        return feedbackID;
	    }

	    public void setFeedbackID(String feedbackID) {
	        this.feedbackID = feedbackID;
	    }

	    public String getPatientID() {
	        return patientID;
	    }

	    public void setPatientID(String patientID) {
	        this.patientID = patientID;
	    }

	    public String getDoctorID() {
	        return doctorID;
	    }

	    public void setDoctorID(String doctorID) {
	        this.doctorID = doctorID;
	    }

	    public int getRating() {
	        return rating;
	    }

	    public void setRating(int rating) {
	        if (rating < 1 || rating > 5) {
	            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
	        }
	        this.rating = rating;
	    }

	    public String getComments() {
	        return comments;
	    }

	    public void setComments(String comments) {
	        this.comments = comments;
	    }

	    public LocalDateTime getCreatedAt() {
	        return createdAt;
	    }

	    public void setCreatedAt(LocalDateTime createdAt) {
	        this.createdAt = createdAt;
	    }

	    // Hiển thị thông tin phản hồi
	    public void displayFeedbackInfo() {
	        System.out.println("Feedback ID: " + feedbackID);
	        System.out.println("Patient ID: " + patientID);
	        System.out.println("Doctor ID: " + doctorID);
	        System.out.println("Rating: " + rating + " stars");
	        System.out.println("Comments: " + comments);
	        System.out.println("Created At: " + createdAt);
	        System.out.println("------------------------------------");
	    }
	}
