package model.room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RoomModel {
    private final String jsonPath;
    private JsonObject roomData;
    private final Gson gson;
    private static final Logger LOGGER = Logger.getLogger(RoomModel.class.getName());

    public RoomModel(String jsonPath) {
        this.jsonPath = jsonPath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadRoomData();
    }

    public String getJsonPath() {
        return jsonPath;
    }

    // 강의실 데이터 로드
    private void loadRoomData() {
        try {
            File file = new File(jsonPath);
            if (!file.exists()) {
                LOGGER.warning("Room data file not found: " + jsonPath);
                roomData = new JsonObject();
                return;
            }
            try (Reader reader = new FileReader(file)) {
                roomData = JsonParser.parseReader(reader).getAsJsonObject();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading room data: " + e.getMessage(), e);
            e.printStackTrace();
            roomData = new JsonObject();
        }
    }

    public Set<Integer> getRoomNumbers() {
        Set<Integer> roomNumbers = new HashSet<>();
        for (String key : roomData.keySet()) {
            try {
                JsonObject room = roomData.getAsJsonObject(key);
                if (room != null) {
                    roomNumbers.add(Integer.parseInt(key));
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Error parsing room number: " + e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return roomNumbers;
    }

    public Set<String> getDays(int roomNumber) {
        JsonObject room = roomData.getAsJsonObject(String.valueOf(roomNumber));
        if (room == null) return new HashSet<>();
        
        Set<String> days = new HashSet<>();
        for (String key : room.keySet()) {
            if (!key.equals("type")) {  // type 필드는 제외
                days.add(key);
            }
        }
        
        // 요일 순서 정의
        List<String> dayOrder = Arrays.asList("월", "화", "수", "목", "금");
        
        // 요일을 순서대로 정렬
        return days.stream()
            .sorted(Comparator.comparingInt(dayOrder::indexOf))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public List<String> getTimeSlots(int roomNumber, String day) {
        JsonObject room = roomData.getAsJsonObject(String.valueOf(roomNumber));
        if (room == null) return new ArrayList<>();
        
        JsonObject dayData = room.getAsJsonObject(day);
        if (dayData == null) return new ArrayList<>();
        
        List<String> timeSlots = new ArrayList<>();
        for (String key : dayData.keySet()) {
            if ("O".equals(dayData.get(key).getAsString())) {
                timeSlots.add(key);
            }
        }
        return timeSlots;
    }

    // 강의실 예약 가능 여부 확인
    public boolean isReservable(int roomNumber, String day, String timeSlot) {
        JsonObject room = roomData.getAsJsonObject(String.valueOf(roomNumber));
        if (room == null) return false;
        
        JsonObject dayData = room.getAsJsonObject(day);
        if (dayData == null) return false;
        
        JsonElement timeSlotData = dayData.get(timeSlot);
        return timeSlotData != null && "O".equals(timeSlotData.getAsString());
    }

    // 강의실 예약 상태 변경
    public void markReserved(int roomNumber, String day, String timeSlot, String jsonPath) {
        JsonObject room = roomData.getAsJsonObject(String.valueOf(roomNumber));
        if (room == null) {
            room = new JsonObject();
            roomData.add(String.valueOf(roomNumber), room);
        }
        
        JsonObject dayData = room.getAsJsonObject(day);
        if (dayData == null) {
            dayData = new JsonObject();
            room.add(day, dayData);
        }
        
        dayData.addProperty(timeSlot, "X");
        saveRoomData();
    }

    // 강의실 예약 취소 상태 변경
    public void markCancelled(int roomNumber, String day, String timeSlot, String jsonPath) {
        JsonObject room = roomData.getAsJsonObject(String.valueOf(roomNumber));
        if (room == null) return;
        
        JsonObject dayData = room.getAsJsonObject(day);
        if (dayData == null) return;
        
        dayData.addProperty(timeSlot, "O");
        saveRoomData();
    }

    // 강의실 데이터 저장
    private void saveRoomData() {
        try (Writer writer = new FileWriter(jsonPath)) {
            gson.toJson(roomData, writer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving room data: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public String getRoomType(int roomNumber) {
        JsonObject room = roomData.getAsJsonObject(String.valueOf(roomNumber));
        if (room == null) return null;
        
        JsonElement typeElement = room.get("type");
        return typeElement != null ? typeElement.getAsString() : null;
    }

    public Set<Integer> getRoomsByType(String type) {
        Set<Integer> rooms = new HashSet<>();
        for (String key : roomData.keySet()) {
            try {
                JsonObject room = roomData.getAsJsonObject(key);
                if (room != null) {
                    JsonElement typeElement = room.get("type");
                    if (typeElement != null && type.equals(typeElement.getAsString())) {
                        rooms.add(Integer.parseInt(key));
                    }
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Error parsing room number: " + e.getMessage(), e);
            }
        }
        return rooms;
    }
}