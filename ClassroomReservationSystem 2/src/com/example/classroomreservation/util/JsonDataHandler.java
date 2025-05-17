package com.example.classroomreservation.util;

import com.example.classroomreservation.model.Reservation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonDataHandler {
    private final String filePath; // final로 변경
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonDataHandler(String filePath) {
        this.filePath = filePath;
    }

    public List<Reservation> loadReservations() {
        if (!Files.exists(Paths.get(filePath))) {
            return new ArrayList<>(); // 파일 없으면 빈 리스트
        }
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Reservation>>(){}.getType();
            List<Reservation> reservations = gson.fromJson(reader, listType);
            return reservations == null ? new ArrayList<>() : reservations;
        } catch (IOException e) {
            System.err.println("JSON 파일 로드 중 오류 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveReservations(List<Reservation> reservations) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(reservations, writer);
        } catch (IOException e) {
            System.err.println("JSON 파일 저장 중 오류 발생: " + e.getMessage());
        }
    }
}