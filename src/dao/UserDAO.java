package dao;

import model.User;
import java.sql.*;
import java.time.LocalDateTime;

public class UserDAO {
    private final DatabaseManager dbManager;

    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public User create(User user) {
        String sql = "INSERT INTO users (username, password, email, full_name, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setTimestamp(5, Timestamp.valueOf(now));
            stmt.setTimestamp(6, Timestamp.valueOf(now));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getLong(1));
                        user.setCreatedAt(now);
                        user.setUpdatedAt(now);
                    }
                }
            }
            return user;
            
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return null;
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }
        return null;
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, full_name = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setTimestamp(5, Timestamp.valueOf(now));
            stmt.setLong(6, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                user.setUpdatedAt(now);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
}
