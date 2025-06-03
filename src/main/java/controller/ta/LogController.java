/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import model.ta.Reservation;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class LogController {

    // ✅ GitHub 공유 폴더에 있는 로그 파일 경로로 변경
    private static final String LOG_PATH = "ta_log.json";  // 같은 디렉토리에 있는 경우

    // 로그 저장 메서드
    public void saveTaLog(String transition, Reservation reservation) {
    JSONObject log = new JSONObject();
    log.put("transition", transition);
    log.put("targetUser", reservation.getName());
    log.put("room", reservation.getType() + " " + reservation.getRoomNumber());
    log.put("time", reservation.getDay() + " " + reservation.getTimeSlots().get(0));
    log.put("timestamp", java.time.LocalDateTime.now().toString());

    File file = new File("ta_log.json");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
        writer.write(log.toString());
        writer.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    // 로그 불러와서 JTextArea에 출력
    public void loadTaLog(JTextArea textArea) {
        File file = new File(LOG_PATH);
        StringBuilder logText = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                JSONObject obj = new JSONObject(line);

                String entry = String.format(
                    "%s | %s | %s | %s | %s\n",
                    obj.optString("timestamp"),
                    obj.optString("transition"),
                    obj.optString("targetUser"),
                    obj.optString("room"),
                    obj.optString("time")
                );

                logText.append(entry);
            }

            textArea.setText(logText.toString());

        } catch (IOException e) {
            textArea.setText("❌ 로그 불러오는 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void saveLog(String role, String message) {
    String logFile = "ta_log.json";

    JSONObject obj = new JSONObject();
    obj.put("timestamp", java.time.LocalDateTime.now().toString());
    obj.put("targetUser", role);
    obj.put("transition", message);
    obj.put("time", "테스트용");

    try (FileWriter writer = new FileWriter(logFile, true)) {
        writer.write(obj.toString() + System.lineSeparator()); // NDJSON 형식
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
