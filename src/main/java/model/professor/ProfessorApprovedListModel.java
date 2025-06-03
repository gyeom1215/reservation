package model.professor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfessorApprovedListModel {
    private final List<ProfessorApprovedModel> schedules;
    private static final Logger LOGGER = Logger.getLogger(ProfessorApprovedListModel.class.getName());

    public ProfessorApprovedListModel(String jsonFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        schedules = mapper.readValue(
            new File(jsonFilePath),
                new TypeReference<>() {
                });
    }

    // 전체 예약 목록 반환
    public List<ProfessorApprovedModel> getList() {
        return schedules;
    }

    // 파일로 저장
    public void save(String jsonFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(
            new File(jsonFilePath), schedules
        );
    }

    // 사용자의 이름, 역할로 필터링
    public List<ProfessorApprovedModel> getMySchedules(String name, String role) {
        return schedules.stream()
                .filter(s -> s.getName().equals(name) && s.getRole().equals(role))
                .collect(Collectors.toList());
    }
}