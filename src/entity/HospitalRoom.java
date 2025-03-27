package entity;

import enums.RoomStatus;
import enums.RoomType;

import java.util.ArrayList;
import java.util.List;

// Lớp HospitalRoom (Phòng bệnh)
public class HospitalRoom {
	private String roomId; // Mã phòng
	private RoomType type; // Loại phòng
	private int totalBeds; // Tổng số giường
	private int availableBeds; // Số giường trống
	private int floorNumber; // Số tầng
	private RoomStatus status; // Trạng thái
	private List<Patient> patients; // Danh sách bệnh nhân đang nằm

	public HospitalRoom() {}

	public HospitalRoom(String roomId, RoomType type, int totalBeds, int floorNumber) {
		this.roomId = roomId;
		this.type = type;
		this.totalBeds = totalBeds;
		this.availableBeds = totalBeds;
		this.floorNumber = floorNumber;
		this.status = RoomStatus.AVAILABLE; // Ban đầu phòng ở trạng thái trống
		this.patients = new ArrayList<>();
	}

	//Getter and Setter
	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public RoomStatus getStatus() {
		return status;
	}

	public void setStatus(RoomStatus status) {
		this.status = status;
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}

	public int getAvailableBeds() {
		return availableBeds;
	}

	public void setAvailableBeds(int availableBeds) {
		this.availableBeds = availableBeds;
	}

	public int getTotalBeds() {
		return totalBeds;
	}

	public void setTotalBeds(int totalBeds) {
		this.totalBeds = totalBeds;
	}

	public RoomType getType() {
		return type;
	}

	public void setType(RoomType type) {
		this.type = type;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	// Nhập viện bệnh nhân
	public boolean admitPatient(Patient patient) {
		if (availableBeds > 0 && (status == RoomStatus.AVAILABLE || status == RoomStatus.OCCUPIED)) {
			patients.add(patient);
			availableBeds--;

			// Cập nhật trạng thái phòng
			updateRoomStatus();

			System.out.println("Bệnh nhân " + patient.getFullName() + " nhập viện vào phòng " + roomId);
			return true;
		} else {
			System.out.println("Không thể nhập viện vào phòng " + roomId + " (Hết giường hoặc không khả dụng)");
			return false;
		}
	}

	// Xuất viện bệnh nhân
	public boolean dischargePatient(Patient patient) {
		if (patients.remove(patient)) {
			availableBeds++;

			// Cập nhật trạng thái phòng
			updateRoomStatus();

			System.out.println("Bệnh nhân " + patient.getFullName() + " xuất viện khỏi phòng " + roomId);
			return true;
		} else {
			System.out.println("Bệnh nhân không có trong phòng " + roomId);
			return false;
		}
	}

	private void updateRoomStatus() {
		if (availableBeds == 0) {
			status = RoomStatus.FULL;
		} else if (availableBeds == totalBeds) {
			status = RoomStatus.AVAILABLE;
		} else {
			status = RoomStatus.OCCUPIED;
		}
	}
}
