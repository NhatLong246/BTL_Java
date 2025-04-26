package model.entity;

import model.enums.Gender;

import java.time.LocalDate;

public class Person {
    protected String fullName; //tên
    protected LocalDate dateOfBirth; //ngày sinh
    protected String address; //địa chỉ
    protected Gender gender; //giới tính
    protected String phoneNumber;

    public Person() {}

    public Person(String fullName, LocalDate dateOfBirth, String address, Gender gender, String phoneNumber) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.gender = gender;
        setPhoneNumber(phoneNumber);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
