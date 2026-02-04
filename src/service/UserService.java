package service;

import dao.UserDAO;
import model.User;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User register(String username, String password, String email, String fullName) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }

        // Check if username already exists
        if (userDAO.usernameExists(username.trim())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userDAO.emailExists(email.trim())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Create new user
        User user = new User(username.trim(), password, email.trim(), fullName != null ? fullName.trim() : "");
        return userDAO.create(user);
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        if (password == null || password.isEmpty()) {
            return null;
        }

        return userDAO.authenticate(username.trim(), password);
    }

    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }

    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public boolean updateUser(User user) {
        return userDAO.update(user);
    }

    public boolean deleteUser(Long userId) {
        return userDAO.delete(userId);
    }
}
