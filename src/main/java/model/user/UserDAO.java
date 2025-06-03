package model.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final String DEFAULT_USER_FILE = "src/main/resources/users.json";
    private final String userFile;
    private final Gson gson;
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private List<User> users;

    public UserDAO() {
        this(DEFAULT_USER_FILE);
    }

    public UserDAO(String userFile) {
        this.userFile = userFile;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.users = new ArrayList<>();
        initializeFile();
    }

    private void initializeFile() {
        File file = new File(userFile);
        if (!file.exists()) {
            try {
                File parentDir = file.getParentFile();
                if (parentDir != null) {
                    parentDir.mkdirs();
                }
                file.createNewFile();
                users = new ArrayList<>();
                users.add(new User("student", "1234", "홍길동", "컴퓨터공학과", "student"));
                users.add(new User("professor", "1234", "김교수", "전자공학과", "professor"));
                users.add(new User("ta", "1234", "이조교", "컴퓨터공학과", "ta"));
                saveUsers(users);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error initializing user file: " + e.getMessage(), e);
                throw new RuntimeException("Failed to initialize user file", e);
            }
        } else {
            users = loadUsers();
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean saveUser(User user) {
        if (user == null) {
            return false;
        }
        
        if (users.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            return false;
        }
        
        users.add(user);
        try {
            saveUsers(users);
            return true;
        } catch (Exception e) {
            users.remove(user);
            LOGGER.log(Level.SEVERE, "Error saving user: " + e.getMessage(), e);
            return false;
        }
    }

    public User findUserById(String id) {
        if (id == null) {
            return null;
        }
        return users.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public boolean updateUser(User updatedUser) {
        if (updatedUser == null) {
            return false;
        }
        
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(updatedUser.getId())) {
                users.set(i, updatedUser);
                try {
                    saveUsers(users);
                    return true;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error updating user: " + e.getMessage(), e);
                    users.set(i, users.get(i)); // 실패 시 원래 상태로 복구
                    return false;
                }
            }
        }
        return false;
    }

    public boolean deleteUser(String id) {
        if (id == null) {
            return false;
        }
        
        User removedUser = null;
        for (User user : users) {
            if (user.getId().equals(id)) {
                removedUser = user;
                break;
            }
        }
        
        if (removedUser != null) {
            users.remove(removedUser);
            try {
                saveUsers(users);
                return true;
            } catch (Exception e) {
                users.add(removedUser); // 실패 시 원래 상태로 복구
                LOGGER.log(Level.SEVERE, "Error deleting user: " + e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    private List<User> loadUsers() {
        try (FileReader reader = new FileReader(userFile)) {
            List<User> loadedUsers = gson.fromJson(reader, new TypeToken<List<User>>(){}.getType());
            return loadedUsers != null ? loadedUsers : new ArrayList<>();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading users: " + e.getMessage(), e);
            return new ArrayList<>();
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.SEVERE, "Error parsing user file: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private void saveUsers(List<User> users) {
        try (FileWriter writer = new FileWriter(userFile)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving users: " + e.getMessage(), e);
            throw new RuntimeException("Failed to save users", e);
        }
    }
}
