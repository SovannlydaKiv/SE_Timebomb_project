import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeTracker {
    private List<TimeEntry> timeEntries;
    private TimeEntry activeEntry;
    private int nextId;
    
    public TimeTracker() {
        this.timeEntries = new ArrayList<>();
        this.activeEntry = null;
        this.nextId = 1;
    }
    
    public void startTask(String taskName, String projectName) {
        if (activeEntry != null) {
            System.out.println("Please stop the current timer first!");
            System.out.println("Active task: " + activeEntry.getTaskName());
            return;
        }
        
        TimeEntry entry = new TimeEntry(nextId++, taskName, projectName);
        entry.startTimer();
        activeEntry = entry;
        timeEntries.add(entry);
    }
    
    public void stopCurrentTask() {
        if (activeEntry == null) {
            System.out.println("No active timer running!");
            return;
        }
        
        activeEntry.stopTimer();
        activeEntry = null;
    }
    
    public void showActiveTask() {
        if (activeEntry == null) {
            System.out.println("No active timer running.");
        } else {
            System.out.println("Active: " + activeEntry);
        }
    }
    
    public List<TimeEntry> getEntriesForDate(LocalDate date) {
        List<TimeEntry> result = new ArrayList<>();
        for (TimeEntry entry : timeEntries) {
            if (entry.getStartTime() != null && 
                entry.getStartTime().toLocalDate().equals(date)) {
                result.add(entry);
            }
        }
        return result;
    }
    
    public List<TimeEntry> getEntriesForWeek(LocalDate date) {
        List<TimeEntry> result = new ArrayList<>();
        LocalDate weekStart = date.minusDays(date.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        for (TimeEntry entry : timeEntries) {
            if (entry.getStartTime() != null) {
                LocalDate entryDate = entry.getStartTime().toLocalDate();
                if (!entryDate.isBefore(weekStart) && !entryDate.isAfter(weekEnd)) {
                    result.add(entry);
                }
            }
        }
        return result;
    }
    
    public List<TimeEntry> getEntriesForMonth(int year, int month) {
        List<TimeEntry> result = new ArrayList<>();
        for (TimeEntry entry : timeEntries) {
            if (entry.getStartTime() != null) {
                LocalDateTime startTime = entry.getStartTime();
                if (startTime.getYear() == year && startTime.getMonthValue() == month) {
                    result.add(entry);
                }
            }
        }
        return result;
    }
    
    public List<TimeEntry> getAllEntries() {
        return new ArrayList<>(timeEntries);
    }
}