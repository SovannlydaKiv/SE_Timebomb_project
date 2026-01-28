package dao;

import model.Project;
import model.ProjectStatus;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private final DatabaseManager dbManager;

    public ProjectDAO()
    {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Project save(Project project) throws SQLException {
        String sql = """
            INSERT INTO projects (name, description, color_code, client, status, 
                                hourly_rate, budget, deadline, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setString(3, project.getColorCode());
            pstmt.setString(4, project.getClient());
            pstmt.setString(5, project.getStatus().name());
            pstmt.setObject(6, project.getHourlyRate());
            pstmt.setObject(7, project.getBudget());
            pstmt.setString(8, project.getDeadLine() != null ? project.getDeadLine().toString() : null);
            pstmt.setString(9, project.getCreatedAt().toString());
            pstmt.setString(10, LocalDateTime.now().toString());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    project.setId(rs.getLong(1));
                }
            }
        }
        return project;
    }
    
    public Project findById(Long id) throws SQLException {
        String sql = "SELECT * FROM projects WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProject(rs);
                }
            }
        }
        return null;
    }
    
    public List<Project> findAll() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        }
        return projects;
    }
    
    public List<Project> findByStatus(ProjectStatus status) throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs));
                }
            }
        }
        return projects;
    }
    
    public List<Project> findByClient(String client) throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE client = ? ORDER BY created_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, client);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs));
                }
            }
        }
        return projects;
    }
    
    public void update(Project project) throws SQLException {
        String sql = """
            UPDATE projects 
            SET name = ?, description = ?, color_code = ?, client = ?, status = ?,
                hourly_rate = ?, budget = ?, deadline = ?, updated_at = ?
            WHERE id = ?
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setString(3, project.getColorCode());
            pstmt.setString(4, project.getClient());
            pstmt.setString(5, project.getStatus().name());
            pstmt.setObject(6, project.getHourlyRate());
            pstmt.setObject(7, project.getBudget());
            pstmt.setString(8, project.getDeadLine() != null ? project.getDeadLine().toString() : null);
            pstmt.setString(9, LocalDateTime.now().toString());
            pstmt.setLong(10, project.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    public int countTasks(Long projectId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM tasks WHERE project_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, projectId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
    
    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getLong("id"));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        project.setColorCode(rs.getString("color_code"));
        project.setClient(rs.getString("client"));
        project.setStatus(ProjectStatus.valueOf(rs.getString("status")));
        
        Double hourlyRate = rs.getObject("hourly_rate", Double.class);
        project.setHourlyRate(hourlyRate);
        
        Double budget = rs.getObject("budget", Double.class);
        project.setBudget(budget);
        
        String deadline = rs.getString("deadline");
        if (deadline != null) {
            project.setDeadLine(LocalDateTime.parse(deadline));
        }
        
        return project;
    }
}
