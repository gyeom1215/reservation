package controller;

import model.user.User;
import model.user.UserDAO;

public class AuthController {
    private final UserDAO userDAO;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    public User login(String id, String password) {
        User user = userDAO.findUserById(id);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean register(User user) {
        if (userDAO.findUserById(user.getId()) != null) {
            return false;
        }
        return userDAO.saveUser(user);
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(String id) {
        return userDAO.deleteUser(id);
    }
} 