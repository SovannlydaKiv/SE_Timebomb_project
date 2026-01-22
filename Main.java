import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        TimeTracker tracker = new TimeTracker();
        ReportGenerator reportGenerator = new ReportGenerator();
        Scanner scanner = new Scanner(System.in);
        
        // Demo: Add some sample data
        System.out.println("=== Time Tracker Demo ===\n");
        
        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Start Timer");
            System.out.println("2. Stop Timer");
            System.out.println("3. View Active Task");
            System.out.println("4. Daily Report");
            System.out.println("5. Weekly Report");
            System.out.println("6. Monthly Report");
            System.out.println("7. Add Sample Data");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    System.out.print("Enter task name: ");
                    String taskName = scanner.nextLine();
                    System.out.print("Enter project name: ");
                    String projectName = scanner.nextLine();
                    tracker.startTask(taskName, projectName);
                    break;
                    
                case 2:
                    tracker.stopCurrentTask();
                    break;
                    
                case 3:
                    tracker.showActiveTask();
                    break;
                    
                case 4:
                    System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
                    String dateStr = scanner.nextLine();
                    LocalDate dailyDate = dateStr.isEmpty() ? 
                        LocalDate.now() : LocalDate.parse(dateStr);
                    reportGenerator.generateDailyReport(tracker, dailyDate);
                    break;
                    
                case 5:
                    System.out.print("Enter date for week (YYYY-MM-DD) or press Enter for this week: ");
                    String weekStr = scanner.nextLine();
                    LocalDate weekDate = weekStr.isEmpty() ? 
                        LocalDate.now() : LocalDate.parse(weekStr);
                    reportGenerator.generateWeeklyReport(tracker, weekDate);
                    break;
                    
                case 6:
                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    System.out.print("Enter month (1-12): ");
                    int month = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    reportGenerator.generateMonthlyReport(tracker, year, month);
                    break;
                    
                case 7:
                    addSampleData(tracker);
                    System.out.println("Sample data added!");
                    break;
                    
                case 8:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                    
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
    
    private static void addSampleData(TimeTracker tracker) {
        // Simulate some completed tasks
        LocalDate today = LocalDate.now();
        
        // Today's tasks
        simulateTask(tracker, "Code Review", "Project Alpha", 3600000); // 1 hour
        simulateTask(tracker, "Bug Fixing", "Project Alpha", 7200000); // 2 hours
        simulateTask(tracker, "Meeting", "Project Beta", 1800000); // 30 min
        
        System.out.println("Sample data has been added for demonstration.");
    }
    
    private static void simulateTask(TimeTracker tracker, String task, 
                                     String project, long durationMillis) {
        tracker.startTask(task, project);
        try {
            Thread.sleep(100); // Small delay to simulate real timing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tracker.stopCurrentTask();
    }
}
