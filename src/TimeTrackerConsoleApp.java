// TimeTrackerConsoleApp.java
import dao.TaskDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import model.*;
import service.*;

/**
 * Console-based Time Tracker Application with User Input
 */
public class TimeTrackerConsoleApp {
    
    private static Scanner scanner = new Scanner(System.in);
    private static ProjectService projectService = new ProjectService();
    private static TaskService taskService = new TaskService();
    private static TimeEntryService timeEntryService = new TimeEntryService();
    private static ReportService reportService = new ReportService();
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║     TIME TRACKER APPLICATION          ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1 -> manageProjects();
                    case 2 -> manageTasks();
                    case 3 -> manageTimeTracking();
                    case 4 -> viewReports();
                    case 5 -> {
                        System.out.println("\n👋 Thank you for using Time Tracker!");
                        running = false;
                    }
                    default -> System.out.println("❌ Invalid choice. Please try again.\n");
                }
            } catch (SQLException e) {
                System.err.println("❌ Database Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
    
    private static void displayMainMenu() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("           MAIN MENU");
        System.out.println("═══════════════════════════════════════");
        System.out.println("1. Manage Projects");
        System.out.println("2. Manage Tasks");
        System.out.println("3. Time Tracking");
        System.out.println("4. View Reports");
        System.out.println("5. Exit");
        System.out.println("═══════════════════════════════════════");
    }
    
    // ============================================================
    // PROJECT MANAGEMENT
    // ============================================================
    
    private static void manageProjects() throws SQLException {
        System.out.println("\n--- PROJECT MANAGEMENT ---");
        System.out.println("1. Create New Project");
        System.out.println("2. View All Projects");
        System.out.println("3. Update Project");
        System.out.println("4. Delete Project");
        System.out.println("5. Back to Main Menu");
        
        int choice = getIntInput("Choose an option: ");
        
        switch (choice) {
            case 1 -> createProject();
            case 2 -> viewAllProjects();
            case 3 -> updateProject();
            case 4 -> deleteProject();
            case 5 -> { /* return to main menu */ }
            default -> System.out.println("Invalid option.");
        }
    }
    
    private static void createProject() throws SQLException {
        System.out.println("\n--- CREATE NEW PROJECT ---");
        
        String name = getStringInput("Project Name: ");
        String description = getStringInput("Description: ");
        String client = getStringInput("Client Name: ");
        
        Project project = projectService.createProject(name, description, client);
        
        // Optional fields
        String addRate = getStringInput("Add hourly rate? (y/n): ");
        if (addRate.equalsIgnoreCase("y")) {
            double rate = getDoubleInput("Hourly Rate ($): ");
            project.setHourlyRate(rate);
        }
        
        String addBudget = getStringInput("Add budget? (y/n): ");
        if (addBudget.equalsIgnoreCase("y")) {
            double budget = getDoubleInput("Budget ($): ");
            project.setBudget(budget);
        }
        
        String colorCode = getStringInput("Color Code (e.g., #FF5733) [optional]: ");
        if (!colorCode.isEmpty()) {
            project.setColorCode(colorCode);
        }
        
        projectService.updateProject(project);
        
        System.out.println("\n✅ Project created successfully! ID: " + project.getId());
    }
    
    private static void viewAllProjects() throws SQLException {
        System.out.println("\n--- ALL PROJECTS ---");
        List<Project> projects = projectService.getAllProjects();
        
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-5s %-25s %-20s %-15s %-10s%n", 
            "ID", "Name", "Client", "Status", "Tasks");
        System.out.println("=".repeat(80));
        
        for (Project project : projects) {
            int taskCount = projectService.getTaskCount(project.getId());
            System.out.printf("%-5d %-25s %-20s %-15s %-10d%n",
                project.getId(),
                truncate(project.getName(), 25),
                truncate(project.getClient(), 20),
                project.getStatus().getDisplayName(),
                taskCount
            );
        }
        System.out.println("=".repeat(80) + "\n");
    }
    
    private static void updateProject() throws SQLException {
        viewAllProjects();
        long projectId = getLongInput("Enter Project ID to update: ");
        
        Project project = projectService.getProject(projectId);
        if (project == null) {
            System.out.println("❌ Project not found!");
            return;
        }
        
        System.out.println("\nCurrent: " + project.getName());
        String newName = getStringInput("New Name (press Enter to keep current): ");
        if (!newName.isEmpty()) {
            project.setName(newName);
        }
        
        String newClient = getStringInput("New Client (press Enter to keep current): ");
        if (!newClient.isEmpty()) {
            project.setClient(newClient);
        }
        
        System.out.println("Status: 1=Active, 2=Archived, 3=Completed");
        int statusChoice = getIntInput("New Status (0 to keep current): ");
        if (statusChoice > 0 && statusChoice <= 3) {
            ProjectStatus[] statuses = ProjectStatus.values();
            project.setStatus(statuses[statusChoice - 1]);
        }
        
        projectService.updateProject(project);
        System.out.println("✅ Project updated successfully!");
    }
    
    private static void deleteProject() throws SQLException {
        viewAllProjects();
        long projectId = getLongInput("Enter Project ID to delete: ");
        
        String confirm = getStringInput("Are you sure? This will delete all tasks! (yes/no): ");
        if (confirm.equalsIgnoreCase("yes")) {
            projectService.deleteProject(projectId);
            System.out.println("✅ Project deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    // ============================================================
    // TASK MANAGEMENT
    // ============================================================
    
    private static void manageTasks() throws SQLException {
        System.out.println("\n--- TASK MANAGEMENT ---");
        System.out.println("1. Create New Task");
        System.out.println("2. View All Tasks");
        System.out.println("3. Update Task Status");
        System.out.println("4. Delete Task");
        System.out.println("5. Back to Main Menu");
        
        int choice = getIntInput("Choose an option: ");
        
        switch (choice) {
            case 1 -> createTask();
            case 2 -> viewAllTasks();
            case 3 -> updateTaskStatus();
            case 4 -> deleteTask();
            case 5 -> { /* return to main menu */ }
            default -> System.out.println("Invalid option.");
        }
    }
    
    private static void createTask() throws SQLException {
    System.out.println("\n--- CREATE NEW TASK ---");
    
    // Show projects and select one
    List<Project> projects = projectService.getAllProjects();
    if (projects.isEmpty()) {
        System.out.println("❌ No projects available. Create a project first!");
        return;
    }
    
    System.out.println("\nAvailable Projects:");
    for (int i = 0; i < projects.size(); i++) {
        System.out.println((i + 1) + ". " + projects.get(i).getName());
    }
    
    int projectChoice = getIntInput("Select Project (number): ");
    if (projectChoice < 1 || projectChoice > projects.size()) {
        System.out.println("Invalid project selection.");
        return;
    }
    
    Project selectedProject = projects.get(projectChoice - 1);
    
    String taskName = getStringInput("Task Name: ");
    String description = getStringInput("Description: ");
    
    // ✅ OPTION 1: Create task manually then save
    Task task = new Task();
    task.setName(taskName);
    task.setProject(selectedProject);
    task.setDescription(description);
    
    // Set priority first
    System.out.println("Priority: 1=Low, 2=Medium, 3=High");
    int priorityChoice = getIntInput("Select Priority: ");
    Priority priority = Priority.MEDIUM; // default
    if (priorityChoice >= 1 && priorityChoice <= 3) {
        Priority[] priorities = Priority.values();
        priority = priorities[priorityChoice - 1];
    }
    task.setPriority(priority);
    
    // Set estimated time
    String addEstimate = getStringInput("Add estimated time? (y/n): ");
    if (addEstimate.equalsIgnoreCase("y")) {
        int estimatedMinutes = getIntInput("Estimated minutes: ");
        task.setEstimationMinutes(estimatedMinutes);
    }
    
    // Add tags
    String addTags = getStringInput("Add tags? (y/n): ");
    if (addTags.equalsIgnoreCase("y")) {
        String tags = getStringInput("Enter tags (comma-separated): ");
        for (String tag : tags.split(",")) {
            task.addTags(tag.trim());
        }
    }
    
    // Now save the task using TaskDAO directly or add to service
    TaskDAO taskDAO = new TaskDAO();
    task = taskDAO.save(task);
    
    System.out.println("\n✅ Task created successfully! ID: " + task.getId());
}
    
    private static void updateTaskStatus() throws SQLException {
        viewAllTasks();
        long taskId = getLongInput("Enter Task ID to update: ");
        
        Task task = taskService.getTask(taskId);
        if (task == null) {
            System.out.println("❌ Task not found!");
            return;
        }
        
        System.out.println("\nCurrent Status: " + task.getStatus().getDisplayName());
        System.out.println("1. To Do");
        System.out.println("2. In Progress");
        System.out.println("3. Completed");
        
        int statusChoice = getIntInput("Select New Status: ");
        
        switch (statusChoice) {
            case 1 -> task.setStatus(TaskStatus.TODO);
            case 2 -> taskService.markAsInProgress(taskId);
            case 3 -> taskService.markAsCompleted(taskId);
            default -> {
                System.out.println("Invalid status.");
                return;
            }
        }
        
        if (statusChoice == 1) {
            taskService.updateTask(task);
        }
        
        System.out.println("✅ Task status updated!");
    }
    
    private static void viewAllTasks() throws SQLException {
        System.out.println("\n--- ALL TASKS ---");
        List<Task> tasks = taskService.getAllTasks();
        
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(120));
        System.out.printf("%-5s %-30s %-20s %-15s %-15s %-20s%n", 
            "ID", "Name", "Project", "Status", "Priority", "Estimated");
        System.out.println("=".repeat(120));
        
        for (Task task : tasks) {
            String estimatedTime = task.getEstimationMinutes() > 0 
                ? formatMinutes(task.getEstimationMinutes()) 
                : "N/A";
            System.out.printf("%-5d %-30s %-20s %-15s %-15s %-20s%n",
                task.getId(),
                truncate(task.getName(), 30),
                truncate(task.getProject().getName(), 20),
                task.getStatus().getDisplayName(),
                task.getPriority().toString(),
                estimatedTime
            );
        }
        System.out.println("=".repeat(120) + "\n");
    }
    
    private static void deleteTask() throws SQLException {
        viewAllTasks();
        long taskId = getLongInput("Enter Task ID to delete: ");
        
        String confirm = getStringInput("Are you sure? (yes/no): ");
        if (confirm.equalsIgnoreCase("yes")) {
            taskService.deleteTask(taskId);
            System.out.println("✅ Task deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    // ============================================================
    // TIME TRACKING
    // ============================================================
    
    private static void manageTimeTracking() throws SQLException {
        System.out.println("\n--- TIME TRACKING ---");
        
        // Check if there's a running timer
        TimeEntry runningTimer = timeEntryService.getCurrentRunningTimer();
        if (runningTimer != null) {
            System.out.println("⏱️  Timer is currently running for: " + runningTimer.getTask().getName());
            long minutes = java.time.Duration.between(
                runningTimer.getStartTime(), 
                LocalDateTime.now()
            ).toMinutes();
            System.out.println("   Time elapsed: " + formatMinutes((int)minutes));
        }
        
        System.out.println("\n1. Start Timer");
        System.out.println("2. Stop Timer");
        System.out.println("3. Add Manual Time Entry");
        System.out.println("4. View Recent Time Entries");
        System.out.println("5. Back to Main Menu");
        
        int choice = getIntInput("Choose an option: ");
        
        switch (choice) {
            case 1 -> startTimer();
            case 2 -> stopTimer();
            case 3 -> addManualTimeEntry();
            case 4 -> viewRecentTimeEntries();
            case 5 -> { /* return to main menu */ }
            default -> System.out.println("Invalid option.");
        }
    }
    
    private static void startTimer() throws SQLException {
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("❌ No tasks available. Create a task first!");
            return;
        }
        
        System.out.println("\nSelect a task to track time:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i).getName() + 
                " [" + tasks.get(i).getStatus().getDisplayName() + "]");
        }
        
        int taskChoice = getIntInput("Select Task (number): ");
        if (taskChoice < 1 || taskChoice > tasks.size()) {
            System.out.println("Invalid task selection.");
            return;
        }
        
        Task selectedTask = tasks.get(taskChoice - 1);
        TimeEntry timer = timeEntryService.startTimer(selectedTask.getId());
        
        System.out.println("\n✅ Timer started for: " + selectedTask.getName());
        System.out.println("   Start time: " + timer.getStartTime().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    private static void stopTimer() throws SQLException {
        TimeEntry runningTimer = timeEntryService.getCurrentRunningTimer();
        
        if (runningTimer == null) {
            System.out.println("❌ No timer is currently running.");
            return;
        }
        
        String notes = getStringInput("Add notes (optional): ");
        if (!notes.isEmpty()) {
            runningTimer.setNotes(notes);
        }
        
        timeEntryService.stopTimer(runningTimer.getId());
        
        System.out.println("\n✅ Timer stopped!");
        System.out.println("   Task: " + runningTimer.getTask().getName());
        System.out.println("   Duration: " + runningTimer.getFormattedDuration());
    }
    
    private static void addManualTimeEntry() throws SQLException {
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("❌ No tasks available. Create a task first!");
            return;
        }
        
        System.out.println("\nSelect a task:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i).getName());
        }
        
        int taskChoice = getIntInput("Select Task (number): ");
        if (taskChoice < 1 || taskChoice > tasks.size()) {
            System.out.println("Invalid task selection.");
            return;
        }
        
        Task selectedTask = tasks.get(taskChoice - 1);
        
        int hours = getIntInput("Hours worked: ");
        int minutes = getIntInput("Minutes worked: ");
        int totalMinutes = (hours * 60) + minutes;
        
        String notes = getStringInput("Notes (optional): ");
        
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(totalMinutes);
        
        TimeEntry entry = timeEntryService.addManualEntry(
            selectedTask.getId(), 
            startTime, 
            endTime, 
            notes
        );
        
        System.out.println("\n✅ Time entry added successfully!");
        System.out.println("   Duration: " + entry.getFormattedDuration());
    }
    
    private static void viewRecentTimeEntries() throws SQLException {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<TimeEntry> entries = timeEntryService.getTimeEntryByDateRange(
            sevenDaysAgo, 
            LocalDateTime.now()
        );
        
        if (entries.isEmpty()) {
            System.out.println("No time entries in the last 7 days.");
            return;
        }
        
        System.out.println("\n--- RECENT TIME ENTRIES (Last 7 Days) ---");
        System.out.println("=".repeat(100));
        System.out.printf("%-5s %-30s %-20s %-15s %-30s%n", 
            "ID", "Task", "Duration", "Date", "Notes");
        System.out.println("=".repeat(100));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (TimeEntry entry : entries) {
            String notes = entry.getNotes() != null ? entry.getNotes() : "";
            System.out.printf("%-5d %-30s %-15s %-15s %-30s%n",
                entry.getId(),
                truncate(entry.getTask().getName(), 30),
                entry.getFormattedDuration(),
                entry.getStartTime().format(formatter),
                truncate(notes, 30)
            );
        }
        System.out.println("=".repeat(100) + "\n");
    }
    
    // ============================================================
    // REPORTS
    // ============================================================
    
    private static void viewReports() throws SQLException {
        System.out.println("\n--- REPORTS ---");
        System.out.println("1. Overall Time Report");
        System.out.println("2. Project Report");
        System.out.println("3. System Statistics");
        System.out.println("4. Back to Main Menu");
        
        int choice = getIntInput("Choose an option: ");
        
        switch (choice) {
            case 1 -> viewOverallReport();
            case 2 -> viewProjectReport();
            case 3 -> viewStatistics();
            case 4 -> { /* return to main menu */ }
            default -> System.out.println("Invalid option.");
        }
    }
    
    private static void viewOverallReport() throws SQLException {
        System.out.println("\nReport period:");
        System.out.println("1. Last 7 days");
        System.out.println("2. Last 30 days");
        System.out.println("3. All time");
        
        int period = getIntInput("Select period: ");
        
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();
        
        switch (period) {
            case 1 -> start = end.minusDays(7);
            case 2 -> start = end.minusDays(30);
            case 3 -> start = end.minusYears(10); // "all time"
            default -> {
                System.out.println("Invalid period.");
                return;
            }
        }
        
        String report = reportService.generateOverallReport(start, end);
        System.out.println("\n" + report);
    }
    
    private static void viewProjectReport() throws SQLException {
        viewAllProjects();
        
        long projectId = getLongInput("Enter Project ID for report: ");
        
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();
        
        String report = reportService.generateProjectReport(projectId, start, end);
        System.out.println("\n" + report);
    }
    
    private static void viewStatistics() throws SQLException {
        var stats = reportService.getStatistics();
        
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║       SYSTEM STATISTICS               ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("Total Projects:       " + stats.get("totalProjects"));
        System.out.println("Active Projects:      " + stats.get("activeProjects"));
        System.out.println("Total Tasks:          " + stats.get("totalTasks"));
        System.out.println("Completed Tasks:      " + stats.get("completedTasks"));
        System.out.println("Total Time Entries:   " + stats.get("totalTimeEntries"));
        System.out.println("Running Timer:        " + (Boolean.TRUE.equals(stats.get("hasRunningTimer")) ? "Yes" : "No"));
        System.out.println("═══════════════════════════════════════\n");
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }
    
    private static long getLongInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                long value = Long.parseLong(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }
    
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }
    
    private static String formatMinutes(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%dh %dm", hours, mins);
    }
    
    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}