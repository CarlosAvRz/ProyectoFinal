package com.example.chronos.finalproject.Models;

import android.graphics.Bitmap;

public class UserData {

    private String birthYear, birthMonth, birthDay, name, lastName, mLastName, email, role, userId, password;
    private Long warnings;
    private Bitmap profilePic;
    private static final UserData USER_DATA = new UserData();

    public static UserData getInstance() {
        return USER_DATA;
    }

    private UserData() {
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public void setBirthMonth(String birthMonth) {
        this.birthMonth = birthMonth;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWarnings(Long warnings) {
        this.warnings = warnings;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getBirthMonth() {
        return birthMonth;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getUserId() {
        return userId;
    }

    public Long getWarnings() {
        return warnings;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public String getPassword() {
        return password;
    }
}
