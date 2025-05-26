package com.deu.reservation.client.model;

public enum ReservationStatus {
    PENDING("대기 중"),
    APPROVED("승인됨"),
    REJECTED("거절됨"),
    CANCELLED("취소됨");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 