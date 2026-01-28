import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.swing.SwingWorker;

import dao.TaskDAO;
import model.Task;
import service.TimeEntryService;

public class Page1 extends JFrame {

    private JLabel timerLabel;
    private boolean running = false;
    private int seconds = 0;
    private Timer timer;

    // store category buttons
    private JButton[] categoryButtons;

    // buttons
    private JButton startPauseBtn;
    private JButton stopBtn;
    
    // UI fields to persist
    private JTextField taskField;
    private String selectedCategory = "Work";

    // backend services/DAOs
    private TimeEntryService timeEntryService;
    private TaskDAO taskDAO;
    
    // UI components for dynamic updates
    private JPanel entriesContainer;
    private JLabel totalLabel;

    public Page1() {
        setTitle("Time Tracker");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        // initialize backend services
        timeEntryService = new TimeEntryService();
        taskDAO = new TaskDAO();

        timer = new Timer(1000, e -> updateTimer());
        
        // Load initial entries after services are ready
        SwingUtilities.invokeLater(this::refreshEntries);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Time Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(title);
        return header;
    }

    private JPanel createMainPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(20, 40, 20, 40));

        container.add(createTimerCard());
        container.add(Box.createVerticalStrut(25));
        container.add(createEntriesCard());

        return container;
    }

    private JPanel createTimerCard() {
        JPanel card = createCard();

        timerLabel = new JLabel("00:00:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel status = new JLabel("Ready");
        status.setForeground(Color.GRAY);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        taskField = new JTextField();
        taskField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        taskField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskField.setBorder(BorderFactory.createTitledBorder("Task Description"));

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        categoryPanel.setOpaque(false);

        String[] categories = { "Work", "Meetings", "Break", "Personal", "Learning" };
        categoryButtons = new JButton[categories.length];

        for (int i = 0; i < categories.length; i++) {
            JButton btn = createCategoryButton(categories[i]);
            categoryButtons[i] = btn;
            categoryPanel.add(btn);
        }

        setActiveCategory(categoryButtons[0]);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        startPauseBtn = new JButton("Start Timer");
        startPauseBtn.setBackground(new Color(37, 99, 235));
        startPauseBtn.setForeground(Color.WHITE);
        startPauseBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startPauseBtn.setFocusPainted(false);
        startPauseBtn.setPreferredSize(new Dimension(200, 45));

        stopBtn = new JButton("Stop Timer");
        stopBtn.setBackground(new Color(200, 200, 200));
        stopBtn.setForeground(Color.BLACK);
        stopBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stopBtn.setFocusPainted(false);
        stopBtn.setPreferredSize(new Dimension(200, 45));
        stopBtn.setEnabled(false);

        startPauseBtn.addActionListener(e -> toggleTimer());
        stopBtn.addActionListener(e -> stopTimer());

        buttonPanel.add(startPauseBtn);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(stopBtn);

        card.add(timerLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(status);
        card.add(Box.createVerticalStrut(20));
        card.add(taskField);
        card.add(Box.createVerticalStrut(15));
        card.add(categoryPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(buttonPanel);

        return card;
    }

    private JPanel createEntriesCard() {
        JPanel card = createCard();

        JLabel title = new JLabel("Today's Entries");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(51, 51, 51));

        totalLabel = new JLabel("Total: 00:00:00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(new Color(37, 99, 235));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        header.add(title, BorderLayout.WEST);
        header.add(totalLabel, BorderLayout.EAST);

        entriesContainer = new JPanel();
        entriesContainer.setLayout(new BoxLayout(entriesContainer, BoxLayout.Y_AXIS));
        entriesContainer.setOpaque(false);

        // Add scroll pane for when there are many entries or small window
        JScrollPane scrollPane = new JScrollPane(entriesContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(header);
        card.add(scrollPane);

        return card;
    }

    private JPanel createEntry(String name, String category, String time) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row.setPreferredSize(new Dimension(0, 35));

        // Left side - task info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel taskLabel = new JLabel(name);
        taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel categoryLabel = new JLabel(" • " + category);
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryLabel.setForeground(new Color(100, 100, 100));
        
        leftPanel.add(taskLabel);
        leftPanel.add(categoryLabel);

        // Right side - time with colored background
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        timeLabel.setOpaque(true);
        timeLabel.setBackground(new Color(240, 248, 255));
        timeLabel.setForeground(new Color(37, 99, 235));
        timeLabel.setBorder(new EmptyBorder(3, 10, 3, 10));

        row.add(leftPanel, BorderLayout.WEST);
        row.add(timeLabel, BorderLayout.EAST);

        return row;
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 25, 20, 25)));
        return card;
    }

    private JButton createCategoryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(248, 249, 250));
        btn.setForeground(new Color(55, 65, 81));
        btn.setBorder(new CompoundBorder(
            new LineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> setActiveCategory(btn));
        return btn;
    }

    private void setActiveCategory(JButton selected) {
        for (JButton btn : categoryButtons) {
            btn.setBackground(new Color(248, 249, 250));
            btn.setForeground(new Color(55, 65, 81));
            btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 16, 8, 16)
            ));
        }
        selected.setBackground(new Color(37, 99, 235));
        selected.setForeground(Color.WHITE);
        selected.setBorder(new CompoundBorder(
            new LineBorder(new Color(37, 99, 235), 1),
            new EmptyBorder(8, 16, 8, 16)
        ));
        // record selected category for backend use
        selectedCategory = selected.getText();
    }

    // ===== TIMER LOGIC =====
    private void toggleTimer() {
        running = !running;

        if (running) {
            timer.start();
            startPauseBtn.setText("Pause Timer");
            stopBtn.setEnabled(false);
        } else {
            timer.stop();
            startPauseBtn.setText("Resume Timer");
            stopBtn.setEnabled(true);
        }
    }

    private void stopTimer() {
        timer.stop();
        running = false;

        final int durationSeconds = seconds;
        final String description = taskField.getText() == null ? "" : taskField.getText().trim();

        final LocalDateTime endTime = LocalDateTime.now();
        final LocalDateTime startTime = endTime.minusSeconds(durationSeconds);

        // reset UI
        seconds = 0;
        timerLabel.setText("00:00:00");
        startPauseBtn.setText("Start Timer");
        stopBtn.setEnabled(false);

        // persist the manual entry in background to avoid blocking the EDT
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    // create a simple Task for this entry if needed
                    Task task = new Task(description, description);
                    Task saved = taskDAO.save(task);

                    // use service to add manual entry
                    timeEntryService.addManualEntry(saved.getId(), startTime, endTime, description);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    // refresh the entries display to show the new entry
                    refreshEntries();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Page1.this, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateTimer() {
        seconds++;
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d:%02d", h, m, s));
    }

    private void refreshEntries() {
        // Skip if services aren't initialized yet
        if (timeEntryService == null || entriesContainer == null) {
            return;
        }
        
        new SwingWorker<Void, Void>() {
            private java.util.List<model.TimeEntry> entries = new java.util.ArrayList<>();
            private int totalMinutes = 0;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Get today's entries
                    java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
                    java.time.LocalDateTime endOfDay = java.time.LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
                    entries = timeEntryService.getTimeEntryByDateRange(startOfDay, endOfDay);
                    
                    // Calculate total minutes
                    for (model.TimeEntry entry : entries) {
                        if (entry.getDurationMinutes() != null) {
                            totalMinutes += entry.getDurationMinutes();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading entries: " + e.getMessage());
                    // Keep empty list on error
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    // Clear existing entries
                    entriesContainer.removeAll();
                    
                    // Add new entries or show "No entries" message
                    if (entries.isEmpty()) {
                        JPanel noEntriesPanel = new JPanel();
                        noEntriesPanel.setLayout(new BorderLayout());
                        noEntriesPanel.setOpaque(false);
                        noEntriesPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
                        
                        JLabel noEntries = new JLabel("No entries for today - start your first timer!", JLabel.CENTER);
                        noEntries.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                        noEntries.setForeground(new Color(107, 114, 128));
                        noEntriesPanel.add(noEntries, BorderLayout.CENTER);
                        entriesContainer.add(noEntriesPanel);
                    } else {
                        for (model.TimeEntry entry : entries) {
                            String taskName = entry.getTask() != null ? entry.getTask().getName() : "Unnamed Task";
                            if (taskName.trim().isEmpty()) {
                                taskName = "Quick Timer";
                            }
                            String duration = entry.getFormattedDuration();
                            entriesContainer.add(createEntry(taskName, selectedCategory, duration));
                        }
                    }
                    
                    // Update total time
                    int hours = totalMinutes / 60;
                    int mins = totalMinutes % 60;
                    totalLabel.setText(String.format("Total: %02d:%02d:00", hours, mins));
                    
                    // Refresh the UI
                    entriesContainer.revalidate();
                    entriesContainer.repaint();
                } catch (Exception ex) {
                    System.err.println("Error updating UI: " + ex.getMessage());
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Page1().setVisible(true));
    }
}
