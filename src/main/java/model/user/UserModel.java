package model.user;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserModel {
    private final String userFile;
    private static final Logger LOGGER = Logger.getLogger(UserModel.class.getName());

    public UserModel(String userFile) {
        this.userFile = userFile;
        LOGGER.info("UserModel initialized with file: " + userFile);
        ensureUserFileExists();
    }

    private void ensureUserFileExists() {
        try {
            File file = new File(userFile);
            if (!file.exists()) {
                LOGGER.info("Creating new users.json file");
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error ensuring user file exists: " + e.getMessage(), e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            LOGGER.info("Reading users from file: " + userFile);
            File file = new File(userFile);
            if (!file.exists()) {
                LOGGER.warning("User file does not exist: " + userFile);
                return users;
            }

            String content = "";
            try (FileReader reader = new FileReader(file)) {
                StringBuilder sb = new StringBuilder();
                char[] buffer = new char[1024];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    sb.append(buffer, 0, read);
                }
                content = sb.toString();
            }

            if (content.trim().isEmpty()) {
                LOGGER.warning("User file is empty");
                return users;
            }

            JSONArray arr = new JSONArray(new JSONTokener(content));
            LOGGER.info("Found " + arr.length() + " users in file");
            
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.getString("id");
                String name = obj.getString("name");
                String password = obj.getString("password");
                String role = obj.getString("role");
                String department = obj.getString("department");
                
                // 역할과 학과 데이터 검증
                if (role == null || role.isEmpty()) {
                    role = "student"; // 기본값 설정
                }
                if (department == null || department.isEmpty()) {
                    department = "컴퓨터공학과"; // 기본값 설정
                }
                
                LOGGER.info("Loading user: id=" + id + ", name=" + name + ", role=" + role + ", department=" + department);
                
                User user = new User(id, password, name, department, role);
                users.add(user);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reading users: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return users;
    }

    private void createDefaultUsers() {
        try {
            LOGGER.info("Creating default users");
            JSONArray defaultUsers = new JSONArray();
            
            JSONObject admin = new JSONObject();
            admin.put("id", "admin");
            admin.put("name", "관리자");
            admin.put("password", "admin123");
            admin.put("role", "ta");
            admin.put("department", "컴퓨터공학과");
            defaultUsers.put(admin);
            
            JSONObject professor = new JSONObject();
            professor.put("id", "prof1");
            professor.put("name", "교수1");
            professor.put("password", "prof123");
            professor.put("role", "professor");
            professor.put("department", "컴퓨터공학과");
            defaultUsers.put(professor);
            
            JSONObject student = new JSONObject();
            student.put("id", "student1");
            student.put("name", "학생1");
            student.put("password", "student123");
            student.put("role", "student");
            student.put("department", "컴퓨터공학과");
            defaultUsers.put(student);
            
            try (FileWriter writer = new FileWriter(userFile)) {
                writer.write(defaultUsers.toString(4));
            }
            LOGGER.info("Default users created successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating default users: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public boolean registerUser(User user) {
        try {
            LOGGER.info("Registering new user: " + user.getId());
            File file = new File(userFile);
            JSONArray arr;
            
            if (!file.exists() || file.length() == 0) {
                arr = new JSONArray();
            } else {
                try (FileReader reader = new FileReader(file)) {
                    arr = new JSONArray(new JSONTokener(reader));
                }
            }
            
            // 중복 체크
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getString("id").equals(user.getId())) {
                    LOGGER.warning("User already exists: " + user.getId());
                    return false;
                }
            }

            JSONObject newUser = new JSONObject();
            newUser.put("id", user.getId());
            newUser.put("name", user.getName());
            newUser.put("password", user.getPassword());
            newUser.put("role", user.getRole());
            newUser.put("department", user.getDepartment());
            arr.put(newUser);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(arr.toString(4));
            }
            LOGGER.info("Successfully registered user: " + user.getId());
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error registering user: " + e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        try {
            LOGGER.info("Updating user: " + user.getId());
            File file = new File(userFile);
            if (!file.exists()) {
                LOGGER.warning("User file does not exist: " + userFile);
                return false;
            }

            JSONArray arr;
            try (FileReader reader = new FileReader(file)) {
                arr = new JSONArray(new JSONTokener(reader));
            }

            boolean found = false;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getString("id").equals(user.getId())) {
                    obj.put("name", user.getName());
                    if (user.getPassword() != null) {
                        obj.put("password", user.getPassword());
                    }
                    obj.put("role", user.getRole());
                    obj.put("department", user.getDepartment());
                    found = true;
                    break;
                }
            }

            if (!found) {
                LOGGER.warning("User not found for update: " + user.getId());
                return false;
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(arr.toString(4));
            }
            LOGGER.info("Successfully updated user: " + user.getId());
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating user: " + e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        try {
            LOGGER.info("Deleting user: " + userId);
            File file = new File(userFile);
            if (!file.exists()) {
                LOGGER.warning("User file does not exist: " + userFile);
                return false;
            }

            JSONArray arr;
            try (FileReader reader = new FileReader(file)) {
                arr = new JSONArray(new JSONTokener(reader));
            }

            boolean found = false;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getString("id").equals(userId)) {
                    arr.remove(i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                LOGGER.warning("User not found for deletion: " + userId);
                return false;
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(arr.toString(4));
            }
            LOGGER.info("Successfully deleted user: " + userId);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting user: " + e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }
} 