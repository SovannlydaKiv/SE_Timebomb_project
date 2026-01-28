package service;

import dao.ProjectDAO;
import dao.TaskDAO;
import dao.TimeEntryDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import model.*;

public class ReportService {
    private final TimeEntryDAO timeEntryDAO;
    private final TaskDAO taskDAO;
    private final ProjectDAO projectDAO;

    public ReportService() {
        this.timeEntryDAO = new TimeEntryDAO();
        this.taskDAO = new TaskDAO();
        this.projectDAO = new ProjectDAO();
    }

    public String generateOverallReport(LocalDateTime start, LocalDateTime end) throws SQLException {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        report.append("═══════════════════════════════════════════════\n");
        report.append("        TIME TRACKING REPORT\n");
        report.append("═══════════════════════════════════════════════\n\n");
        report.append("Period: ").append(start.format(formatter))
              .append(" to ").append(end.format(formatter)).append("\n\n");

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

        report.append("Summary:\n");
        report.append("  Total Entries: ").append(entries.size()).append("\n");
        report.append("  Total Time: ").append(formatMinutes(totalMinutes)).append("\n");
        report.append("  Billable Time: ").append(formatMinutes(billableMinutes)).append("\n");
        report.append("  Non-billable Time: ").append(formatMinutes(totalMinutes - billableMinutes)).append("\n\n");

        Map<String, Integer> projectTime = new HashMap<>();
        for (TimeEntry entry : entries) {
            if (entry.getDurationMinutes() != null && entry.getTask().getProject() != null) {
                String projectName = entry.getTask().getProject().getName();
                projectTime.merge(projectName, entry.getDurationMinutes(), Integer::sum);
            }
        }

        if (!projectTime.isEmpty()) {
            report.append("Time by Project:\n");
            projectTime.entrySet().stream()
                       .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                       .forEach(e -> report.append("   ").append(e.getKey())
                                           .append(": ").append(formatMinutes(e.getValue())).append("\n"));
        }

        report.append("\n═══════════════════════════════════════════════\n");

        return report.toString();
    }

    public String generateProjectReport(Long projectId, LocalDateTime start, LocalDateTime end) throws SQLException {
        Project project = projectDAO.findById(projectId);
        if (project == null) {
            return "Project not Found.";
        }

        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        report.append("═══════════════════════════════════════════════\n");
        report.append("     PROJECT REPORT: ").append(project.getName()).append("\n");
        report.append("═══════════════════════════════════════════════\n\n");

        if (project.getClient() != null) {
            report.append("Client: ").append(project.getClient()).append("\n");
        }
        report.append("Status: ").append(project.getStatus().getDisplayName()).append("\n");
        report.append("Period: ").append(start.format(formatter))
              .append(" to ").append(end.format(formatter)).append("\n\n");

        List<Task> task = taskDAO.findByProjectId(projectId);
        report.append("Total Tasks: ").append(task.size()).append("\n\n");

        int totalProjectMinutes = 0;

        report.append("Tasks;\n");
        for (Task currentTask : task) {
            List<TimeEntry> taskEntries = timeEntryDAO.findByTaskId(currentTask.getId());
            int taskMinutes = taskEntries.stream()
                .filter(e -> e.getDurationMinutes() != null)  // Filter out null values
                .mapToInt(TimeEntry::getDurationMinutes)       // Convert to int stream
            .sum();                                         // Sum all minutes
    
            totalProjectMinutes += taskMinutes;
            report.append("  • ").append(currentTask.getName())
                  .append(" [").append(currentTask.getStatus().getDisplayName()).append("]")
                  .append(" - ").append(formatMinutes(taskMinutes)).append("\n");
        }

        report.append("\nTotal Time: ").append(formatMinutes(totalProjectMinutes)).append("\n");
        
        if (project.getHourlyRate() != null) {
            double earnings = (totalProjectMinutes / 60.0) * project.getHourlyRate();
            report.append("Estimated Earnings: $").append(String.format("%.2f", earnings)).append("\n");
        }
        
        report.append("\n═══════════════════════════════════════════════\n");
        
        return report.toString();
    }

    public Map<String, Object> getStatistics() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalProjects", projectDAO.findAll().size());
        stats.put("activeProjects", projectDAO.findByStatus(ProjectStatus.ACTIVE).size());
        stats.put("totalTasks", taskDAO.findAll().size());
        stats.put("completedTasks", taskDAO.findByStatus(TaskStatus.COMPLETED).size());
        stats.put("totalTimeEntries", timeEntryDAO.findAll().size());
        
        TimeEntry runningTimer = timeEntryDAO.findRunningEntry();
        stats.put("hasRunningTimer", runningTimer != null);
        
        return stats;
    }
    
    private String formatMinutes(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%dh %dm", hours, mins);
    }
}
