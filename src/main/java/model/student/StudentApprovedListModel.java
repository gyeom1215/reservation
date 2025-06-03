package model.student;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentApprovedListModel {
    private final List<StudentApprovedModel> schedules;
    private static final Logger LOGGER = Logger.getLogger(StudentApprovedListModel.class.getName());

    public StudentApprovedListModel(String jsonFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            schedules = mapper.readValue(
                new File(jsonFilePath),
                new TypeReference<>() {
                }
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading reservation data: " + e.getMessage(), e);
            System.err.println("예약 데이터 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 전체 예약 목록 반환
    public List<StudentApprovedModel> getList() {
        return schedules;
    }

    // 파일로 저장
    public void save(String jsonFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                new File(jsonFilePath), schedules
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving reservation data: " + e.getMessage(), e);
            System.err.println("예약 데이터 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 사용자의 이름, 역할로 필터링
    public List<StudentApprovedModel> getMySchedules(String name, String role) {
        return schedules.stream()
                .filter(s -> s.getName().equals(name) && s.getRole().equals(role))
                .collect(Collectors.toList());
    }
}