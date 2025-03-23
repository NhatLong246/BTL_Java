package entity;

import enums.RoomType;

public class HospitalRooms {
	
	    private String roomID;
	    private String roomNumber;
	    private RoomType type;  
	    private int capacity;
	    private int floorNumber;
	    private String status;
	    
	    public HospitalRooms() {}
	    // Constructor  
		public HospitalRooms(String roomID, String roomNumber, RoomType type, int capacity, int floorNumber,
				String status) {
			this.roomID = roomID;
			this.roomNumber = roomNumber;
			this.type = type;
			this.capacity = capacity;
			this.floorNumber = floorNumber;
			this.status = status;
		}
		public String getRoomID() {
			return roomID;
		}
		public void setRoomID(String roomID) {
			this.roomID = roomID;
		}
		public String getRoomNumber() {
			return roomNumber;
		}
		public void setRoomNumber(String roomNumber) {
			this.roomNumber = roomNumber;
		}
		public RoomType getType() {
			return type;
		}
		public void setType(RoomType type) {
			this.type = type;
		}
		public int getCapacity() {
			return capacity;
		}
		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}
		public int getFloorNumber() {
			return floorNumber;
		}
		public void setFloorNumber(int floorNumber) {
			this.floorNumber = floorNumber;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	   // Hàm xuất thông tin phòng
		public void displayRoomInfo() {
		    System.out.println("Room ID: " + roomID);
		    System.out.println("Room Number: " + roomNumber);
		    System.out.println("Room Type: " + type.getDescription());
		    System.out.println("Capacity: " + capacity + " beds");
		    System.out.println("Floor: " + floorNumber);
		    System.out.println("Status: " + status);
		    System.out.println("------------------------------------");
		}
}
