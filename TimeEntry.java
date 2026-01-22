// TimeEntry.java
import java.time.LocalDateTime;
import java.time.Duration;

public class TimeEntry {
    private int id;
    private String taskName;
    private String projectName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    
    public TimeEntry(int id, String taskName, String projectName) {
        this.id = id;
        this.taskName = taskName;
        this.projectName = projectName;
    }
    
    public void startTimer() {
        this.startTime = LocalDateTime.now();
        System.out.println("Timer started for: " + taskName);
    }
    
    public void stopTimer() {
        if (startTime == null) {
            System.out.println("Timer hasn't been started yet!");
            return;
        }
        this.endTime = LocalDateTime.now();
        this.duration = Duration.between(startTime, endTime);
        System.out.println("Timer stopped. Duration: " + formatDuration(duration));
    }
    
    public String formatDuration(Duration d) {
        long hours = d.toHours();
        long minutes = d.toMinutesPart();
        long seconds = d.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    // Getters
    public int getId() { return id; }
    public String getTaskName() { return taskName; }
    public String getProjectName() { return projectName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Duration getDuration() { return duration; }
    
    public boolean isActive() {
        return startTime != null && endTime == null;
    }
    
    @Override
    public String toString() {
        String status = isActive() ? "RUNNING" : "COMPLETED";
        String durationStr = duration != null ? formatDuration(duration) : "N/A";
        return String.format("[%s] %s - %s | Duration: %s", 
            status, taskName, projectName, durationStr);
    }
}
