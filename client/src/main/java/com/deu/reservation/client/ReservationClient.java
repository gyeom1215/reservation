package com.deu.reservation.client;

import com.deu.reservation.client.model.User;
import com.deu.reservation.client.protocol.Message;
import com.deu.reservation.client.util.NetworkUtil;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationClient {
    private static final Logger LOGGER = Logger.getLogger(ReservationClient.class.getName());
    private Socket socket;
    private User currentUser;

    public ReservationClient() {
        try {
            socket = NetworkUtil.connectToServer();
            LOGGER.info("서버에 연결되었습니다.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "서버 연결 중 오류 발생: " + e.getMessage(), e);
            throw new RuntimeException("서버 연결 실패", e);
        }
    }

    public boolean login(String id, String password) {
        try {
            Message request = new Message(Message.TYPE_LOGIN, new User(id, password, null, null, null));
            NetworkUtil.sendMessage(socket, request);
            Message response = NetworkUtil.receiveMessage(socket);
            if (response.getData() instanceof User) {
                currentUser = (User) response.getData();
                return true;
            } else if (response.getData() instanceof Boolean) {
                return (Boolean) response.getData();
            } else if (response.getData() instanceof String) {
                return Boolean.parseBoolean((String) response.getData());
            } else {
                LOGGER.severe("서버로부터 잘못된 응답 형식: " + response.getData().getClass().getName());
                return false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "로그인 중 오류 발생: " + e.getMessage(), e);
            return false;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean register(User user) {
        try {
            Message request = new Message(Message.TYPE_REGISTER, user);
            NetworkUtil.sendMessage(socket, request);
            Message response = NetworkUtil.receiveMessage(socket);
            if (response.getData() instanceof Boolean) {
                return (Boolean) response.getData();
            } else if (response.getData() instanceof String) {
                return Boolean.parseBoolean((String) response.getData());
            } else {
                LOGGER.severe("서버로부터 잘못된 응답 형식: " + response.getData().getClass().getName());
                return false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "회원가입 중 오류 발생: " + e.getMessage(), e);
            return false;
        }
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "소켓 종료 중 오류 발생: " + e.getMessage(), e);
        }
    }
} 