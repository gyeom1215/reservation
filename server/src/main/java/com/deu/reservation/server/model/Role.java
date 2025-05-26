package com.deu.reservation.server.model;

public enum Role {
    STUDENT,
    PROFESSOR,
    TA;

    public boolean canApproveReservations() {
        return this == PROFESSOR || this == TA;
    }

    public boolean canViewAllReservations() {
        return this == PROFESSOR || this == TA;
    }
} 