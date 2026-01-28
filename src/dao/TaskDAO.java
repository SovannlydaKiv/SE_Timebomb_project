package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class TaskDAO {
    private final DatabaseManager dbManager;
    private final ProjectDAO projectDAO;

    public TaskDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.projectDAO = new ProjectDAO();
    }

    public Task save(Task task) throws SQLException {
        String sql = """
            INSERT INTO tasks (name, description, project_id, priority, status, 
                             estimated_minutes, due_date, billable, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, task.getName());
            pstmt.setString(2, task.getDescription());
            pstmt.setObject(3, task.getProject() != null ? task.getProject().getId() : null);
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getStatus().name());
            pstmt.setObject(6, task.getEstimationMinutes());
            pstmt.setString(7, task.getDueDate() != null ? task.getDueDate().toString() : null);
            pstmt.setInt(8, task.getBillable() ? 1 : 0);
            pstmt.setString(9, task.getCreatedAt().toString());
            pstmt.setString(10, LocalDateTime.now().toString());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getLong(1));
                }
            }
            
            // Save tags
            saveTags(task);
        }
        return task;
    }

    public Task findById(Long id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Task task = mapResultSetToTask(rs);
                    loadTags(task);
                    return task;
                }
            }
        }
        return null;
    }

    public List<Task> findAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Task task = mapResultSetToTask(rs);
                loadTags(task);
                tasks.add(task);
            }
        }
        return tasks;
    }

    public List<Task> findByProjectId(Long projectId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE project_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, projectId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = mapResultSetToTask(rs);
                    loadTags(task);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public List<Task> findByStatus(TaskStatus status) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = mapResultSetToTask(rs);
                    loadTags(task);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public List<Task> findByPriority(Priority priority) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE priority = ? ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, priority.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = mapResultSetToTask(rs);
                    loadTags(task);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public List<Task> findByTag(String tag) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT DISTINCT t.* FROM tasks t
            INNER JOIN task_tags tt ON t.id = tt.task_id
            WHERE tt.tag = ?
            ORDER BY t.created_at DESC
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tag);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = mapResultSetToTask(rs);
                    loadTags(task);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public void update(Task task) throws SQLException {
        String sql = """
            UPDATE tasks 
            SET name = ?, description = ?, project_id = ?, priority = ?, status = ?,
                estimated_minutes = ?, due_date = ?, billable = ?, updated_at = ?
            WHERE id = ?
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, task.getName());
            pstmt.setString(2, task.getDescription());
            pstmt.setObject(3, task.getProject() != null ? task.getProject().getId() : null);
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getStatus().name());
            pstmt.setObject(6, task.getEstimationMinutes());
            pstmt.setString(7, task.getDueDate() != null ? task.getDueDate().toString() : null);
            pstmt.setInt(8, task.getBillable() ? 1 : 0);
            pstmt.setString(9, LocalDateTime.now().toString());
            pstmt.setLong(10, task.getId());
            
            pstmt.executeUpdate();
            
            // Update tags
            deleteTags(task.getId());
            saveTags(task);
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    private void saveTags(Task task) throws SQLException {
        if (task.getTags() == null || task.getTags().isEmpty()) return;
        
        String sql = "INSERT INTO task_tags (task_id, tag) VALUES (?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (String tag : task.getTags()) {
                pstmt.setLong(1, task.getId());
                pstmt.setString(2, tag);
                pstmt.executeUpdate();
            }
        }
    }

    private void loadTags(Task task) throws SQLException {
        String sql = "SELECT tag FROM task_tags WHERE task_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, task.getId());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<String> tags = new ArrayList<>();
                while (rs.next()) {
                    tags.add(rs.getString("tag"));
                }
                task.setTags(tags);
            }
        }
    }

    private void deleteTags(Long taskId) throws SQLException {
        String sql = "DELETE FROM task_tags WHERE task_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, taskId);
            pstmt.executeUpdate();
        }
    }

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setName(rs.getString("name"));
        task.setDescription(rs.getString("description"));
        
        Long projectId = rs.getObject("project_id", Long.class);
        if (projectId != null) {
            task.setProject(projectDAO.findById(projectId));
        }
        
        task.setPriority(Priority.valueOf(rs.getString("priority")));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        
        Integer estimatedMinutes = rs.getObject("estimated_minutes", Integer.class);
        task.setEstimationMinutes(estimatedMinutes);
        
        String dueDate = rs.getString("due_date");
        if (dueDate != null) {
            task.setDueDate(LocalDateTime.parse(dueDate));
        }
        
        task.setBillable(rs.getInt("billable") == 1);
        
        return task;
    }
}