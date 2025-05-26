package com.deu.reservation.server.model;

public class User {
    private String id;
    private String password;
    private String name;
    private String department;
    private Role role;

    public User(String id, String password, String name, String department, Role role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.department = department;
        this.role = role;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // 역할 기반 권한 체크 메서드
    public boolean canApproveReservations() {
        return role == Role.PROFESSOR || role == Role.TA;
    }

    public boolean canViewAllReservations() {
        return role == Role.PROFESSOR || role == Role.TA;
    }

    public boolean canModifyReservations() {
        return role == Role.PROFESSOR;
    }
} 