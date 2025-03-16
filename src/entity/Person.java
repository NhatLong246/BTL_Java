package entity;

import java.time.LocalDate;

public class Person {
    protected String name; //tên
    protected LocalDate birthDate; //ngày sinh
    protected String address; //địa chỉ
    protected Gender gender; //giới tính
    protected String phoneNumber;

    public Person() {}

    public Person(String name, LocalDate birthDate, String address, Gender gender, String phoneNumber) {
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.gender = gender;
        setPhoneNumber(phoneNumber);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        // Regex: Bắt đầu bằng "+84" hoặc "0", sau đó là đúng 9 chữ số
        if (!phoneNumber.matches("(\\+84|0)\\d{9}")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ! Phải bắt đầu với +84 hoặc 0, và có đúng 9 chữ số.");
        }
        this.phoneNumber = phoneNumber;
    }
}
