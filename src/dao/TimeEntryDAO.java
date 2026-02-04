package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.TimeEntry;

public class TimeEntryDAO {
    private final DatabaseManager dbManager;
    private final TaskDAO taskDAO;
    
    public TimeEntryDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.taskDAO = new TaskDAO();
    }
    
    public TimeEntry save(TimeEntry entry, Long userId) throws SQLException {
        String sql = """
            INSERT INTO time_entries (user_id, task_id, start_time, end_time, duration_minutes, 
                                    notes, billable, is_running, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, userId);
            // Handle null task for personal entries
            if (entry.getTask() != null) {
                pstmt.setLong(2, entry.getTask().getId());
            } else {
                pstmt.setNull(2, java.sql.Types.BIGINT);
            }
            pstmt.setString(3, entry.getStartTime().toString());
            pstmt.setString(4, entry.getEndTime() != null ? entry.getEndTime().toString() : null);
            pstmt.setObject(5, entry.getDurationMinutes());
            // Store description in notes field
            String notes = entry.getDescription() != null ? entry.getDescription() : entry.getNotes();
            pstmt.setString(6, notes);
            pstmt.setInt(7, entry.getBillable() ? 1 : 0);
            pstmt.setInt(8, entry.getIsRunning() ? 1 : 0);
            pstmt.setString(9, entry.getCreatedAt().toString());
            pstmt.setString(10, LocalDateTime.now().toString());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entry.setId(rs.getLong(1));
                }
            }
        }
        return entry;
    }
    
    public TimeEntry findById(Long id) throws SQLException {
        String sql = "SELECT * FROM time_entries WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTimeEntry(rs);
                }
            }
        }
        return null;
    }
    
    public List<TimeEntry> findAllByUser(Long userId) throws SQLException {
        List<TimeEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM time_entries WHERE user_id = ? ORDER BY start_time DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToTimeEntry(rs));
                }
            }
        }
        return entries;
    }
    
    public List<TimeEntry> findByTaskId(Long taskId) throws SQLException {
        List<TimeEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM time_entries WHERE task_id = ? ORDER BY start_time DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, taskId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToTimeEntry(rs));
                }
            }
        }
        return entries;
    }
    
    public TimeEntry findRunningEntry() throws SQLException {
        String sql = "SELECT * FROM time_entries WHERE is_running = 1 LIMIT 1";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return mapResultSetToTimeEntry(rs);
            }
        }
        return null;
    }
    
    public List<TimeEntry> findByDateRangeAndUser(LocalDateTime start, LocalDateTime end, Long userId) throws SQLException {
        List<TimeEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM time_entries WHERE user_id = ? AND start_time >= ? AND start_time <= ? ORDER BY start_time DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            pstmt.setString(2, start.toString());
            pstmt.setString(3, end.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToTimeEntry(rs));
                }
            }
        }
        return entries;
    }
    
    public void update(TimeEntry entry) throws SQLException {
        String sql = """
            UPDATE time_entries 
            SET task_id = ?, start_time = ?, end_time = ?, duration_minutes = ?,
                notes = ?, billable = ?, is_running = ?, updated_at = ?
            WHERE id = ?
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, entry.getTask().getId());
            pstmt.setString(2, entry.getStartTime().toString());
            pstmt.setString(3, entry.getEndTime() != null ? entry.getEndTime().toString() : null);
            pstmt.setObject(4, entry.getDurationMinutes());
            pstmt.setString(5, entry.getNotes());
            pstmt.setInt(6, entry.getBillable() ? 1 : 0);
            pstmt.setInt(7, entry.getIsRunning() ? 1 : 0);
            pstmt.setString(8, LocalDateTime.now().toString());
            pstmt.setLong(9, entry.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM time_entries WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    public int getTotalMinutesByTask(Long taskId) throws SQLException {
        String sql = "SELECT SUM(duration_minutes) as total FROM time_entries WHERE task_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, taskId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }
    
    public int getTotalMinutesByProject(Long projectId) throws SQLException {
        String sql = """
            SELECT SUM(te.duration_minutes) as total
            FROM time_entries te
            INNER JOIN tasks t ON te.task_id = t.id
            WHERE t.project_id = ?
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, projectId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }
    
    private TimeEntry mapResultSetToTimeEntry(ResultSet rs) throws SQLException {
        TimeEntry entry = new TimeEntry();
        entry.setId(rs.getLong("id"));
        
        Long taskId = rs.getLong("task_id");
        if (!rs.wasNull()) {
            entry.setTask(taskDAO.findById(taskId));
        }
        
        entry.setStartTime(LocalDateTime.parse(rs.getString("start_time").replace(" ", "T")));
        
        String endTime = rs.getString("end_time");
        if (endTime != null) {
            entry.setEndTime(LocalDateTime.parse(endTime.replace(" ", "T")));
        }
        
        Integer durationMinutes = rs.getObject("duration_minutes", Integer.class);
        entry.setDurationMinutes(durationMinutes);
        
        String notes = rs.getString("notes");
        entry.setNotes(notes);
        entry.setDescription(notes);  // Also set description from notes
        entry.setBillable(rs.getInt("billable") == 1);
        entry.setIsRunning(rs.getInt("is_running") == 1);
        
        return entry;
    }
}