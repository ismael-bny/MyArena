package com.example.myarena.domain;

public class User {
    private Long id;
    private String name;
    private String email;
    private String pwdHash;
    private String phone;
    private UserRole role;
    private UserStatus status;

    public User() {}

    public User(String email, Long id, String name, String pwdHash, String phone, UserRole role, UserStatus userStatus) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.pwdHash = pwdHash;
        this.phone = phone;
        this.role = role;
        this.status = userStatus;
    }

    public Long getId() {
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

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPwdHash(String pwdHash) { this.pwdHash = pwdHash; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(UserRole role) { this.role = role; }
    public void setStatus(UserStatus status) {this.status = status; }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', role=" + role + ", status=" + status + "}";
    }
}
