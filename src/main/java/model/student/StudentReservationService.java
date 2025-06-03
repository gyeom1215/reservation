package model.student;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.ta.Reservation;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentReservationService {
    private final String reservationFile;
    private final Gson gson;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final Logger LOGGER = Logger.getLogger(StudentReservationService.class.getName());

    public StudentReservationService() {
        this("student_reservations.json");
    }

    public StudentReservationService(String reservationFile) {
        this.reservationFile = reservationFile;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        
        // LocalDateTime 직렬화/역직렬화 어댑터 추가
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(formatter.format(src));
            }
        });
        
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
                    throws JsonParseException {
                return LocalDateTime.parse(json.getAsString(), formatter);
            }
        });
        
        this.gson = gsonBuilder.create();
    }

    // 예약 생성
    public boolean makeReservation(StudentReservation reservation) {
        try {
            List<StudentReservation> reservations = loadReservations();
            reservations.add(reservation);
            return saveReservations(reservations);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing reservation: " + ex.getMessage(), ex);
            System.err.println("예약 처리 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    // 학생 ID로 예약 목록 조회
    public List<Reservation> getReservationsByStudentId(String studentId) {
        List<StudentReservation> reservations = loadReservations();
        return reservations.stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .map(this::convertToReservation)
                .collect(Collectors.toList());
    }

    // 예약 데이터 로드
    private List<StudentReservation> loadReservations() {
        File file = new File(reservationFile);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<StudentReservation>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading reservation data: " + e.getMessage(), e);
            System.err.println("예약 데이터 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean saveReservations(List<StudentReservation> reservations) {
        try (Writer writer = new FileWriter(reservationFile)) {
            gson.toJson(reservations, writer);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving reservation data: " + e.getMessage(), e);
            System.err.println("예약 데이터 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Reservation convertToReservation(StudentReservation studentReservation) {
        return new Reservation(
            studentReservation.getStudentId(),
            "STUDENT",
            studentReservation.getRoomType(),
            Integer.parseInt(studentReservation.getRoomNumber()),
            studentReservation.getStartTime().getDayOfWeek().toString(),
            List.of(studentReservation.getStartTime().toString(), studentReservation.getEndTime().toString()),
            "대기"
        );
    }
} 