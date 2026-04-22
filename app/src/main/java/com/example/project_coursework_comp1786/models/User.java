package com.example.project_coursework_comp1786.models;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String email;
    private String fullName;
    private String role; // "admin" hoặc "staff"
    private boolean isActive; // Lưu ý kiểu boolean

    public User() {
        // Bắt buộc phải có cho Firebase
    }

    // Constructor này dùng cho lúc Register Admin (mặc định active)
    public User(String uid, String email, String fullName, String role) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isActive = true;
    }

    // Constructor ĐẦY ĐỦ - Dùng để load từ Database (Sẽ hết lỗi ở UserService)
    public User(String uid, String email, String fullName, String role, boolean isActive) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isActive = isActive;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Firebase dùng logic đặt tên is... cho boolean
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean active) { isActive = active; }

    // Hàm này bổ trợ cho code Java (Adapter/UserService)
    public boolean isActive() { return isActive; }
}