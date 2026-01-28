package service;

import dao.TaskDAO;
import dao.TimeEntryDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import model.Task;
import model.TimeEntry;

public class TimeEntryService {
    private final TimeEntryDAO timeEntryDAO;
    private final TaskDAO taskDAO;

    public TimeEntryService() {
        this.timeEntryDAO = new TimeEntryDAO();
        this.taskDAO = new TaskDAO();
    }

    public TimeEntry startTimer(Long taskId) throws SQLException {
        TimeEntry runningEntry = timeEntryDAO.findRunningEntry();
        if (runningEntry != null) {
            stopTimer(runningEntry.getId());
        }

        Task task = taskDAO.findById(taskId);
        if(task == null) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }

        TimeEntry entry = new TimeEntry(task, LocalDateTime.now());
        entry.setBillable(task.getBillable());
        return timeEntryDAO.save(entry);
    }

    public TimeEntry stopTimer(Long entryId) throws SQLException {
        TimeEntry entry = timeEntryDAO.findById(entryId);
        if (entry == null) {
            throw new IllegalArgumentException("Time entry not found with id: " + entryId);
        }

        if (!entry.getIsRunning()) {
            throw new IllegalStateException("Time entry is not running: " + entryId);
        }

        entry.stopTimer();
        timeEntryDAO.update(entry);
        return entry;
    }

    public TimeEntry getCurrentRunningTimer() throws SQLException {
        return timeEntryDAO.findRunningEntry();
    }

    public TimeEntry addManualEntry(Long taskId, LocalDateTime start, LocalDateTime end, String notes) throws SQLException {
        Task task = taskDAO.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }

        TimeEntry entry = new TimeEntry();
        entry.setTask(task);
        entry.setStartTime(start);
        entry.setEndTime(end);
        entry.setNotes(notes);
        entry.setBillable(task.getBillable());
        entry.setIsRunning(false);

        return timeEntryDAO.save(entry);
    }

    public TimeEntry getTimeEntry(Long id) throws SQLException {
        return timeEntryDAO.findById(id);
    }

    public List<TimeEntry> getAllTimeEntry(Long id) throws SQLException {
        return timeEntryDAO.findAll();
    }

    public List<TimeEntry> getTimeEntryByTask (Long taskId) throws SQLException {
        return timeEntryDAO.findByTaskId(taskId);
    }

    public List<TimeEntry> getTimeEntryByDateRange(LocalDateTime start, LocalDateTime end) throws SQLException {
        return timeEntryDAO.findByDateRange(start, end);
    }

    public void updateTimeEntry(TimeEntry entry) throws SQLException {
        if(entry.getIsRunning()) {
            throw new IllegalStateException("Cannot update a running time entry. Stop the timer first.");
        }
        timeEntryDAO.update(entry);
    }

    public void deleteTimeEntry(Long id) throws SQLException {
        timeEntryDAO.delete(id);
    }

    public TimeSummary getTimeSummary(LocalDateTime start, LocalDateTime end) throws SQLException {
        List<TimeEntry> entries = timeEntryDAO.findByDateRange(start, end);

        int totalMinutes = 0;
        int billableMinutes = 0;

        for (TimeEntry entry : entries) {
            if (entry.getDurationMinutes() != null) {
                totalMinutes += entry.getDurationMinutes();
                if (entry.getBillable()) {
                    billableMinutes += entry.getDurationMinutes();
                }
            }
        }

        return new TimeSummary(entries.size(), totalMinutes, billableMinutes);
    }

    public static class TimeSummary {
        private final int entryCount;
        private final int totalMinutes;
        private final int billableMinutes;

        public TimeSummary(int entryCount, int totalMinutes, int billableMinutes) {
            this.entryCount = entryCount;
            this.totalMinutes = totalMinutes;
            this.billableMinutes = billableMinutes;
        }

        public int getEntryCount() { return entryCount; }
        public int getTotalMinutes() { return totalMinutes; }
        public int getBillableMinutes() { return billableMinutes; }
        public double getTotalHours() { return totalMinutes / 60.0; }
        public double getBillableHours() { return billableMinutes / 60.0; }

        @Override
        public String toString() {
            return String.format("TimeSummary{entries=%d, total=%dh %dm, billable=%dh %dm}",
                entryCount, totalMinutes / 60, totalMinutes % 60,
                billableMinutes / 60, billableMinutes % 60);
        }
    }
}