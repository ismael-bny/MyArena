package com.example.myarena.domain;

public class User {
    private String id;
    private String name;
    private String email;
    private String pwdHash;
    private String phone;
    private UserRole role;
    private UserStatus status;

    public User(String email, String id, String name, String pwdHash, String phone, UserRole role, UserStatus userStatus) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.pwdHash = pwdHash;
        this.phone = phone;
        this.role = role;
        this.status = userStatus;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPwdHash() {
        return pwdHash;
    }

    public String getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getUserStatus() {
        return status;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', role=" + role + ", status=" + status + "}";
    }
}
