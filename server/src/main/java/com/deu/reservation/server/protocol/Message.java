package com.deu.reservation.server.protocol;

public class Message {
    public static final String TYPE_LOGIN = "LOGIN";
    public static final String TYPE_REGISTER = "REGISTER";
    public static final String TYPE_RESERVATION = "RESERVATION";
    public static final String TYPE_APPROVE = "APPROVE";
    public static final String TYPE_REJECT = "REJECT";
    public static final String TYPE_GET_RESERVATIONS = "GET_RESERVATIONS";
    public static final String TYPE_GET_ROOMS = "GET_ROOMS";

    private String type;
    private Object data;

    public Message(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
} 