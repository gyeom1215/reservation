package com.deu.reservation.client.model;

public class User {
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_PROFESSOR = "PROFESSOR";
    public static final String ROLE_TA = "TA";

    private String id;
    private String password;
    private String name;
    private String department;
    private String role;

    public User(String id, String password, String name, String department, String role) {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isStudent() {
        return ROLE_STUDENT.equals(role);
    }

    public boolean isProfessor() {
        return ROLE_PROFESSOR.equals(role);
    }

    public boolean isTA() {
        return ROLE_TA.equals(role);
    }
} 