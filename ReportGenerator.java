import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ReportGenerator {
    
    public void generateDailyReport(TimeTracker tracker, LocalDate date) {
        System.out.println("\n========== DAILY REPORT ==========");
        System.out.println("Date: " + date);
        System.out.println("==================================");
        
        List<TimeEntry> entries = tracker.getEntriesForDate(date);
        
        if (entries.isEmpty()) {
            System.out.println("No time entries for this date.");
            return;
        }
        
        Duration totalDuration = Duration.ZERO;
        Map<String, Duration> projectTime = new HashMap<>();
        
        for (TimeEntry entry : entries) {
            if (entry.getDuration() != null) {
                System.out.println(entry);
                totalDuration = totalDuration.plus(entry.getDuration());
                
                String project = entry.getProjectName();
                projectTime.put(project, 
                    projectTime.getOrDefault(project, Duration.ZERO).plus(entry.getDuration()));
            }
        }
        
        System.out.println("\n--- Summary by Project ---");
        for (Map.Entry<String, Duration> e : projectTime.entrySet()) {
            System.out.printf("%s: %s\n", e.getKey(), formatDuration(e.getValue()));
        }
        
        System.out.println("\nTotal Time: " + formatDuration(totalDuration));
        System.out.println("==================================\n");
    }
    
    public void generateWeeklyReport(TimeTracker tracker, LocalDate date) {
        System.out.println("\n========== WEEKLY REPORT ==========");
        LocalDate weekStart = date.minusDays(date.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        System.out.println("Week: " + weekStart + " to " + weekEnd);
        System.out.println("===================================");
        
        List<TimeEntry> entries = tracker.getEntriesForWeek(date);
        
        if (entries.isEmpty()) {
            System.out.println("No time entries for this week.");
            return;
        }
        
        Duration totalDuration = Duration.ZERO;
        Map<String, Duration> projectTime = new HashMap<>();
        Map<LocalDate, Duration> dailyTime = new HashMap<>();
        
        for (TimeEntry entry : entries) {
            if (entry.getDuration() != null) {
                totalDuration = totalDuration.plus(entry.getDuration());
                
                String project = entry.getProjectName();
                projectTime.put(project, 
                    projectTime.getOrDefault(project, Duration.ZERO).plus(entry.getDuration()));
                
                LocalDate entryDate = entry.getStartTime().toLocalDate();
                dailyTime.put(entryDate, 
                    dailyTime.getOrDefault(entryDate, Duration.ZERO).plus(entry.getDuration()));
            }
        }
        
        System.out.println("\n--- Daily Breakdown ---");
        for (Map.Entry<LocalDate, Duration> e : dailyTime.entrySet()) {
            System.out.printf("%s: %s\n", e.getKey(), formatDuration(e.getValue()));
        }
        
        System.out.println("\n--- Summary by Project ---");
        for (Map.Entry<String, Duration> e : projectTime.entrySet()) {
            System.out.printf("%s: %s\n", e.getKey(), formatDuration(e.getValue()));
        }
        
        System.out.println("\nTotal Time: " + formatDuration(totalDuration));
        System.out.println("Total Entries: " + entries.size());
        System.out.println("===================================\n");
    }
    
    public void generateMonthlyReport(TimeTracker tracker, int year, int month) {
        System.out.println("\n========== MONTHLY REPORT ==========");
        System.out.println("Month: " + year + "-" + String.format("%02d", month));
        System.out.println("====================================");
        
        List<TimeEntry> entries = tracker.getEntriesForMonth(year, month);
        
        if (entries.isEmpty()) {
            System.out.println("No time entries for this month.");
            return;
        }
        
        Duration totalDuration = Duration.ZERO;
        Map<String, Duration> projectTime = new HashMap<>();
        Map<String, Integer> taskCount = new HashMap<>();
        
        for (TimeEntry entry : entries) {
            if (entry.getDuration() != null) {
                totalDuration = totalDuration.plus(entry.getDuration());
                
                String project = entry.getProjectName();
                projectTime.put(project, 
                    projectTime.getOrDefault(project, Duration.ZERO).plus(entry.getDuration()));
                
                String task = entry.getTaskName();
                taskCount.put(task, taskCount.getOrDefault(task, 0) + 1);
            }
        }
        
        System.out.println("\n--- Summary by Project ---");
        for (Map.Entry<String, Duration> e : projectTime.entrySet()) {
            System.out.printf("%s: %s\n", e.getKey(), formatDuration(e.getValue()));
        }
        
        System.out.println("\n--- Most Frequent Tasks ---");
        taskCount.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .forEach(e -> System.out.printf("%s: %d times\n", e.getKey(), e.getValue()));
        
        System.out.println("\nTotal Time: " + formatDuration(totalDuration));
        System.out.println("Total Entries: " + entries.size());
        System.out.println("Average per Day: " + formatDuration(totalDuration.dividedBy(30)));
        System.out.println("====================================\n");
    }
    
    private String formatDuration(Duration d) {
        long hours = d.toHours();
        long minutes = d.toMinutesPart();
        long seconds = d.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}