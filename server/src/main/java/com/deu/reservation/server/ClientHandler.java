package com.deu.reservation.server;

import com.deu.reservation.server.manager.ReservationManager;
import com.deu.reservation.server.model.*;
import com.deu.reservation.server.protocol.Message;
import com.deu.reservation.server.util.NetworkUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.JsonObject;

public class ClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    private final Socket socket;
    private final Gson gson = new Gson();
    // 회원 정보를 저장하는 static 맵 (id -> User)
    private static final Map<String, User> users = new HashMap<>();
    private static final String USER_FILE = "users.json";
    private static boolean loaded = false;

    // users.json에서 회원 정보 불러오기
    private static void loadUsers() {
        if (loaded) return;
        java.io.File file = new java.io.File(USER_FILE);
        if (file.exists()) {
            try (java.io.Reader reader = new java.io.FileReader(file)) {
                User[] userArr = new Gson().fromJson(reader, User[].class);
                if (userArr != null) {
                    for (User u : userArr) {
                        users.put(u.getId(), u);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "회원 정보 파일 로드 실패: " + e.getMessage(), e);
            }
        }
        loaded = true;
    }

    // users.json에 회원 정보 저장하기
    private static void saveUsers() {
        try (java.io.Writer writer = new java.io.FileWriter(USER_FILE)) {
            new Gson().toJson(users.values(), writer);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "회원 정보 파일 저장 실패: " + e.getMessage(), e);
        }
    }

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message = NetworkUtil.receiveMessage(socket);
                Message response = handleMessage(message);
                NetworkUtil.sendMessage(socket, response);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "클라이언트 처리 중 오류 발생: " + e.getMessage(), e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "소켓 종료 중 오류 발생: " + e.getMessage(), e);
            }
        }
    }

    private Message handleMessage(Message message) {
        try {
            loadUsers();
            ReservationManager.loadReservations();

            switch (message.getType()) {
                case Message.TYPE_LOGIN:
                    return handleLogin(message);
                case Message.TYPE_REGISTER:
                    return handleRegister(message);
                case Message.TYPE_RESERVATION:
                    return handleReservation(message);
                case Message.TYPE_APPROVE:
                    return handleApprove(message);
                case Message.TYPE_REJECT:
                    return handleReject(message);
                case Message.TYPE_GET_RESERVATIONS:
                    return handleGetReservations(message);
                case Message.TYPE_GET_ROOMS:
                    return handleGetRooms(message);
                default:
                    return createErrorResponse(message.getType(), "알 수 없는 요청 유형");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "메시지 처리 중 오류 발생: " + e.getMessage(), e);
            return createErrorResponse(message.getType(), "처리 중 오류 발생: " + e.getMessage());
        }
    }

    private Message handleLogin(Message message) {
        User loginUser = (User) message.getData();
        User storedUser = users.get(loginUser.getId());
        
        if (storedUser != null && storedUser.getPassword().equals(loginUser.getPassword())) {
            // 비밀번호는 보안을 위해 제외하고 사용자 정보 반환
            User responseUser = new User(
                storedUser.getId(),
                null, // 비밀번호는 제외
                storedUser.getName(),
                storedUser.getDepartment(),
                storedUser.getRole()
            );
            return new Message(Message.TYPE_LOGIN, responseUser);
        }
        return new Message(Message.TYPE_LOGIN, false);
    }

    private Message handleRegister(Message message) {
        User newUser = (User) message.getData();
        if (users.containsKey(newUser.getId())) {
            return createErrorResponse(Message.TYPE_REGISTER, "이미 존재하는 아이디입니다.");
        }

        try {
            Role role = Role.valueOf(newUser.getRole().toString());
            User user = new User(
                newUser.getId(),
                newUser.getPassword(),
                newUser.getName(),
                newUser.getDepartment(),
                role
            );
            users.put(user.getId(), user);
            saveUsers();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입 성공");
            return new Message(Message.TYPE_REGISTER, response);
        } catch (IllegalArgumentException e) {
            return createErrorResponse(Message.TYPE_REGISTER, "잘못된 역할입니다.");
        }
    }

    private Message handleReservation(Message message) {
        if (message.getData() instanceof Map) {
            Map<?,?> resData = (Map<?,?>) message.getData();
            String userId = (String) resData.get("userId");
            String roomId = (String) resData.get("roomId");
            String startTimeStr = (String) resData.get("startTime");
            String endTimeStr = (String) resData.get("endTime");
            String purpose = (String) resData.get("purpose");

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
                LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);

                User user = users.get(userId);
                if (user == null) {
                    return createErrorResponse(Message.TYPE_RESERVATION, "사용자를 찾을 수 없습니다.");
                }

                Reservation reservation = ReservationManager.createReservation(
                    userId, roomId, startTime, endTime, purpose, user);

                if (reservation == null) {
                    return createErrorResponse(Message.TYPE_RESERVATION, "해당 시간에 예약이 불가능합니다.");
                }

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("reservationId", reservation.getId());
                response.put("status", reservation.getStatus().toString());
                return new Message(Message.TYPE_RESERVATION, response);
            } catch (Exception e) {
                return createErrorResponse(Message.TYPE_RESERVATION, "잘못된 시간 형식입니다.");
            }
        }
        return createErrorResponse(Message.TYPE_RESERVATION, "잘못된 데이터 형식");
    }

    private Message handleApprove(Message message) {
        if (message.getData() instanceof Map) {
            Map<?,?> approveData = (Map<?,?>) message.getData();
            String reservationId = (String) approveData.get("reservationId");
            String approverId = (String) approveData.get("approverId");

            User approver = users.get(approverId);
            if (approver == null || !approver.canApproveReservations()) {
                return createErrorResponse(Message.TYPE_APPROVE, "승인 권한이 없습니다.");
            }

            boolean success = ReservationManager.approveReservation(reservationId, approverId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "예약이 승인되었습니다." : "예약 승인에 실패했습니다.");
            return new Message(Message.TYPE_APPROVE, response);
        }
        return createErrorResponse(Message.TYPE_APPROVE, "잘못된 데이터 형식");
    }

    private Message handleReject(Message message) {
        if (message.getData() instanceof Map) {
            Map<?,?> rejectData = (Map<?,?>) message.getData();
            String reservationId = (String) rejectData.get("reservationId");
            String rejecterId = (String) rejectData.get("rejecterId");

            User rejecter = users.get(rejecterId);
            if (rejecter == null || !rejecter.canApproveReservations()) {
                return createErrorResponse(Message.TYPE_REJECT, "거절 권한이 없습니다.");
            }

            boolean success = ReservationManager.rejectReservation(reservationId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "예약이 거절되었습니다." : "예약 거절에 실패했습니다.");
            return new Message(Message.TYPE_REJECT, response);
        }
        return createErrorResponse(Message.TYPE_REJECT, "잘못된 데이터 형식");
    }

    private Message handleGetReservations(Message message) {
        if (message.getData() instanceof Map) {
            Map<?,?> data = (Map<?,?>) message.getData();
            String userId = (String) data.get("userId");
            User user = users.get(userId);

            if (user == null) {
                return createErrorResponse(Message.TYPE_GET_RESERVATIONS, "사용자를 찾을 수 없습니다.");
            }

            List<Reservation> reservations;
            if (user.canViewAllReservations()) {
                reservations = ReservationManager.getAllReservations();
            } else {
                reservations = ReservationManager.getUserReservations(userId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reservations", reservations);
            return new Message(Message.TYPE_GET_RESERVATIONS, response);
        }
        return createErrorResponse(Message.TYPE_GET_RESERVATIONS, "잘못된 데이터 형식");
    }

    private Message handleGetRooms(Message message) {
        if (message.getData() instanceof Map) {
            Map<?,?> data = (Map<?,?>) message.getData();
            String type = (String) data.get("type");
            List<Room> rooms;
            
            if (type != null) {
                Room.RoomType roomType = Room.RoomType.valueOf(type);
                rooms = ReservationManager.getRoomsByType(roomType);
            } else {
                rooms = ReservationManager.getRooms();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("rooms", rooms);
            return new Message(Message.TYPE_GET_ROOMS, response);
        }
        return createErrorResponse(Message.TYPE_GET_ROOMS, "잘못된 데이터 형식");
    }

    private Message createErrorResponse(String type, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return new Message(type, response);
    }
} 