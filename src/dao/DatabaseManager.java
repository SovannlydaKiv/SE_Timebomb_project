package dao;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "timetracker";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    private static final String DB_URL = String.format(
        "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", 
        DB_HOST, DB_PORT, DB_NAME
    );

    private static DatabaseManager instance;

    private DatabaseManager() {
        initializeDatabase();
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    private void initializeDatabase() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            
            // Create Projects table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS projects (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255) NOT NULL,
                    description TEXT,
                    color_code VARCHAR(50),
                    client VARCHAR(255),
                    status VARCHAR(50) DEFAULT 'ACTIVE',
                    hourly_rate DECIMAL(10, 2),
                    budget DECIMAL(10, 2),
                    deadline DATETIME,
                    created_at DATETIME NOT NULL,
                    updated_at DATETIME NOT NULL,
                    INDEX idx_status (status),
                    INDEX idx_client (client)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // Create Tasks table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255) NOT NULL,
                    description TEXT,
                    project_id BIGINT,
                    priority VARCHAR(50) DEFAULT 'MEDIUM',
                    status VARCHAR(50) DEFAULT 'TODO',
                    estimated_minutes INT,
                    due_date DATETIME,
                    billable TINYINT(1) DEFAULT 1,
                    created_at DATETIME NOT NULL,
                    updated_at DATETIME NOT NULL,
                    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                    INDEX idx_project (project_id),
                    INDEX idx_status (status),
                    INDEX idx_priority (priority)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // Create Tags table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS task_tags (
                    task_id BIGINT,
                    tag VARCHAR(100),
                    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
                    INDEX idx_task (task_id),
                    INDEX idx_tag (tag)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            // Create TimeEntries table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS time_entries (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    task_id BIGINT NOT NULL,
                    start_time DATETIME NOT NULL,
                    end_time DATETIME,
                    duration_minutes INT,
                    notes TEXT,
                    billable TINYINT(1) DEFAULT 1,
                    is_running TINYINT(1) DEFAULT 0,
                    created_at DATETIME NOT NULL,
                    updated_at DATETIME NOT NULL,
                    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
                    INDEX idx_task (task_id),
                    INDEX idx_start_time (start_time),
                    INDEX idx_is_running (is_running)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            
            System.out.println("MySQL Database initialized successfully!");
            System.out.println("Connected to: " + DB_URL);
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            System.err.println("Make sure MySQL is running and credentials are correct!");
            e.printStackTrace();
        }
    }
    
    public void close() {
        // Connection pooling would be managed here in production
        // For now, connections are closed in try-with-resources
    }
    
    // Configuration methods for easy customization
    public static void configure(String host, String port, String dbName, String user, String password) {
        // This would require refactoring to make fields non-final
        // For now, edit the constants above directly
        System.out.println("To configure database, edit DatabaseManager constants");
    }
}

