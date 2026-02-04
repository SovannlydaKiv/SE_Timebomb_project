import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;
import java.util.Calendar;
import java.time.LocalDateTime;
import service.ProjectService;
import service.TaskService;
import model.*;

public class ProjectGUI extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private ProjectService projectService;
    private TaskService taskService;
    private JComboBox<String> statusBox;
    private JTextField clientField;

    public ProjectGUI() {
        projectService = new ProjectService();
        taskService = new TaskService();
        
        setTitle("Project Manager");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(createTopBar(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createBottomButtons(), BorderLayout.SOUTH);
        
        // Load projects from database
        loadProjects();
    }

    /* ================= TOP BAR ================= */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("Projects");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navPanel.setOpaque(false);

        JButton trackerBtn = new JButton("Tracker");
        trackerBtn.setBackground(new Color(229, 231, 235));
        trackerBtn.setForeground(Color.BLACK);
        trackerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        trackerBtn.setFocusPainted(false);
        trackerBtn.setBorderPainted(false);
        trackerBtn.setPreferredSize(new Dimension(100, 35));
        trackerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        trackerBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new TrackerPage().setVisible(true));
        });

        JButton projectBtn = new JButton("Projects");
        projectBtn.setBackground(new Color(59, 130, 246));
        projectBtn.setForeground(Color.WHITE);
        projectBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        projectBtn.setFocusPainted(false);
        projectBtn.setBorderPainted(false);
        projectBtn.setPreferredSize(new Dimension(100, 35));
        projectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton summaryBtn = new JButton("Summary");
        summaryBtn.setBackground(new Color(229, 231, 235));
        summaryBtn.setForeground(Color.BLACK);
        summaryBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryBtn.setFocusPainted(false);
        summaryBtn.setBorderPainted(false);
        summaryBtn.setPreferredSize(new Dimension(100, 35));
        summaryBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        summaryBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new SummaryPage().setVisible(true));
        });

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(239, 68, 68));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setPreferredSize(new Dimension(100, 35));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            UserSession.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        });

        navPanel.add(trackerBtn);
        navPanel.add(projectBtn);
        navPanel.add(summaryBtn);
        navPanel.add(logoutBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(navPanel, BorderLayout.EAST);
        return topBar;
    }

    /* ================= MAIN CONTENT ================= */
    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 250));
        main.setBorder(new EmptyBorder(20, 30, 20, 30));

        main.add(createFilterCard(), BorderLayout.NORTH);
        main.add(createTableCard(), BorderLayout.CENTER);

        return main;
    }

    /* ================= FILTER CARD ================= */
    private JPanel createFilterCard() {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 15, 10, 15)));

        statusBox = new JComboBox<>(
                new String[] { "All", "ACTIVE", "COMPLETED", "ON_HOLD", "ARCHIVED" });

        clientField = new JTextField(12);
        JButton filterBtn = createPrimaryButton("Filter");

        filterBtn.addActionListener(e -> filterProjects());

        card.add(new JLabel("Status"));
        card.add(statusBox);
        card.add(new JLabel("Client"));
        card.add(clientField);
        card.add(filterBtn);

        return card;
    }
    
    private void filterProjects() {
        String selectedStatus = (String) statusBox.getSelectedItem();
        String clientFilter = clientField.getText().trim();
        
        tableModel.setRowCount(0);
        
        try {
            List<Project> projects;
            if ("All".equals(selectedStatus)) {
                projects = projectService.getAllProjects(UserSession.getUserId());
            } else {
                projects = projectService.getAllProjects(UserSession.getUserId()); // Filter in memory
            }
            
            for (Project p : projects) {
                // Apply filters
                boolean statusMatch = "All".equals(selectedStatus) || 
                    (p.getStatus() != null && p.getStatus().name().equals(selectedStatus));
                boolean clientMatch = clientFilter.isEmpty() || 
                    (p.getClient() != null && p.getClient().toLowerCase().contains(clientFilter.toLowerCase()));
                
                if (statusMatch && clientMatch) {
                    int taskCount = projectService.getTaskCount(p.getId());
                    String deadline = p.getDeadLine() != null ? p.getDeadLine().toLocalDate().toString() : "No deadline";
                    tableModel.addRow(new Object[] { 
                        p.getId(), 
                        p.getName(), 
                        p.getClient() != null ? p.getClient() : "-", 
                        p.getStatus(), 
                        taskCount, 
                        deadline 
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering projects: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadProjects() {
        tableModel.setRowCount(0);
        
        try {
            List<Project> projects = projectService.getAllProjects(UserSession.getUserId());
            for (Project p : projects) {
                int taskCount = projectService.getTaskCount(p.getId());
                String deadline = p.getDeadLine() != null ? p.getDeadLine().toLocalDate().toString() : "No deadline";
                tableModel.addRow(new Object[] { 
                    p.getId(), 
                    p.getName(), 
                    p.getClient() != null ? p.getClient() : "-", 
                    p.getStatus(), 
                    taskCount, 
                    deadline 
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading projects: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ================= TABLE CARD ================= */
    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)));

        String[] cols = { "ID", "Name", "Client", "Status", "Tasks", "Deadline" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 242, 245));
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Double-click to view/edit project tasks
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        Long projectId = (Long) tableModel.getValueAt(row, 0);
                        openProjectTasksDialog(projectId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }
    
    private void openProjectTasksDialog(Long projectId) {
        try {
            Project project = projectService.getProject(projectId);
            if (project == null) return;
            
            JDialog dlg = new JDialog(this, "Tasks for: " + project.getName(), true);
            dlg.setSize(750, 500);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());
            
            // Task table
            String[] taskCols = { "ID", "Name", "Status", "Priority", "Est. Hours", "Due Date" };
            DefaultTableModel taskModel = new DefaultTableModel(taskCols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            List<Task> tasks = taskService.getTaskByProject(projectId);
            for (Task t : tasks) {
                String estHours = t.getEstimationMinutes() != null ? 
                    String.format("%.1f", t.getEstimationMinutes() / 60.0) : "-";
                String dueDate = t.getDueDate() != null ? t.getDueDate().toLocalDate().toString() : "-";
                taskModel.addRow(new Object[] {
                    t.getId(),
                    t.getName(),
                    t.getStatus(),
                    t.getPriority(),
                    estHours,
                    dueDate
                });
            }
            
            JTable taskTable = new JTable(taskModel);
            taskTable.setRowHeight(28);
            taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(taskTable);
            
            // Button panel
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            JButton editTaskBtn = new JButton("Edit Task");
            editTaskBtn.setBackground(new Color(251, 191, 36));
            editTaskBtn.setForeground(Color.BLACK);
            editTaskBtn.addActionListener(e -> {
                int selectedRow = taskTable.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(dlg, "Please select a task to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Long taskId = (Long) taskModel.getValueAt(selectedRow, 0);
                openEditTaskDialog(taskId, taskModel, selectedRow);
            });
            
            JButton deleteTaskBtn = new JButton("Delete Task");
            deleteTaskBtn.setBackground(new Color(234, 67, 53));
            deleteTaskBtn.setForeground(Color.WHITE);
            deleteTaskBtn.addActionListener(e -> {
                int selectedRow = taskTable.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(dlg, "Please select a task to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Long taskId = (Long) taskModel.getValueAt(selectedRow, 0);
                String taskName = (String) taskModel.getValueAt(selectedRow, 1);
                int confirm = JOptionPane.showConfirmDialog(dlg, 
                    "Are you sure you want to delete task '" + taskName + "'?", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        taskService.deleteTask(taskId);
                        taskModel.removeRow(selectedRow);
                        loadProjects();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dlg, "Error deleting task: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            JButton addTaskBtn = new JButton("Add Task");
            addTaskBtn.setBackground(new Color(59, 130, 246));
            addTaskBtn.setForeground(Color.WHITE);
            addTaskBtn.addActionListener(e -> {
                openAddTaskDialog(project, taskModel);
            });
            
            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(e -> dlg.dispose());
            
            btnPanel.add(editTaskBtn);
            btnPanel.add(deleteTaskBtn);
            btnPanel.add(addTaskBtn);
            btnPanel.add(closeBtn);
            
            dlg.add(new JLabel("  Tasks (Select a task and click Edit to modify)", SwingConstants.LEFT), BorderLayout.NORTH);
            dlg.add(scrollPane, BorderLayout.CENTER);
            dlg.add(btnPanel, BorderLayout.SOUTH);
            dlg.setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openEditTaskDialog(Long taskId, DefaultTableModel taskModel, int rowIndex) {
        try {
            Task task = taskService.getTask(taskId);
            if (task == null) return;
            
            JDialog dlg = new JDialog(this, "Edit Task: " + task.getName(), true);
            dlg.setSize(450, 450);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());
            
            JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
            form.setBorder(new EmptyBorder(15, 15, 15, 15));
            
            JTextField nameField = new JTextField(task.getName());
            JTextField descField = new JTextField(task.getDescription() != null ? task.getDescription() : "");
            
            double currentHours = task.getEstimationMinutes() != null ? task.getEstimationMinutes() / 60.0 : 1.0;
            JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(currentHours, 0.5, 500.0, 0.5));
            
            JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
            priorityBox.setSelectedItem(task.getPriority());
            
            JComboBox<TaskStatus> statusBox = new JComboBox<>(TaskStatus.values());
            statusBox.setSelectedItem(task.getStatus());
            
            // Calendar date picker panel
            JPanel datePanel = createDatePickerPanel(task.getDueDate());
            
            JCheckBox billableBox = new JCheckBox("", task.getBillable());
            
            form.add(new JLabel("Task Name:"));
            form.add(nameField);
            form.add(new JLabel("Description:"));
            form.add(descField);
            form.add(new JLabel("Estimated Hours:"));
            form.add(hoursSpinner);
            form.add(new JLabel("Priority:"));
            form.add(priorityBox);
            form.add(new JLabel("Status:"));
            form.add(statusBox);
            form.add(new JLabel("Due Date:"));
            form.add(datePanel);
            form.add(new JLabel("Billable:"));
            form.add(billableBox);
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveBtn = new JButton("Save Changes");
            saveBtn.setBackground(new Color(59, 130, 246));
            saveBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("Cancel");
            
            saveBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Task name is required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    task.setName(name);
                    task.setDescription(descField.getText().trim());
                    double hours = (Double) hoursSpinner.getValue();
                    task.setEstimationMinutes((int) (hours * 60));
                    task.setPriority((Priority) priorityBox.getSelectedItem());
                    task.setStatus((TaskStatus) statusBox.getSelectedItem());
                    task.setBillable(billableBox.isSelected());
                    
                    // Get date from the date picker panel
                    LocalDateTime dueDate = getDateFromPanel(datePanel);
                    task.setDueDate(dueDate);
                    
                    taskService.updateTask(task);
                    
                    // Update table row
                    taskModel.setValueAt(task.getName(), rowIndex, 1);
                    taskModel.setValueAt(task.getStatus(), rowIndex, 2);
                    taskModel.setValueAt(task.getPriority(), rowIndex, 3);
                    taskModel.setValueAt(String.format("%.1f", hours), rowIndex, 4);
                    taskModel.setValueAt(dueDate != null ? dueDate.toLocalDate().toString() : "-", rowIndex, 5);
                    
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, "Task updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dlg, "Error updating task: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            cancelBtn.addActionListener(e -> dlg.dispose());
            
            btnPanel.add(cancelBtn);
            btnPanel.add(saveBtn);
            
            dlg.add(form, BorderLayout.CENTER);
            dlg.add(btnPanel, BorderLayout.SOUTH);
            dlg.setVisible(true);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading task: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createDatePickerPanel(LocalDateTime currentDate) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        // Year spinner
        int currentYear = currentDate != null ? currentDate.getYear() : java.time.LocalDate.now().getYear();
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 2020, 2100, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "####"));
        yearSpinner.setName("year");
        
        // Month combo
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setName("month");
        if (currentDate != null) {
            monthCombo.setSelectedIndex(currentDate.getMonthValue() - 1);
        } else {
            monthCombo.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);
        }
        
        // Day spinner
        int currentDay = currentDate != null ? currentDate.getDayOfMonth() : java.time.LocalDate.now().getDayOfMonth();
        JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(currentDay, 1, 31, 1));
        daySpinner.setName("day");
        
        // Calendar button to open a calendar popup
        JButton calendarBtn = new JButton("📅");
        calendarBtn.setToolTipText("Open Calendar");
        calendarBtn.addActionListener(e -> {
            showCalendarPopup(yearSpinner, monthCombo, daySpinner, calendarBtn);
        });
        
        // Clear date button
        JButton clearBtn = new JButton("✕");
        clearBtn.setToolTipText("Clear Date");
        clearBtn.setMargin(new Insets(2, 5, 2, 5));
        clearBtn.addActionListener(e -> {
            yearSpinner.setValue(java.time.LocalDate.now().getYear());
            monthCombo.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);
            daySpinner.setValue(java.time.LocalDate.now().getDayOfMonth());
        });
        
        panel.add(yearSpinner);
        panel.add(monthCombo);
        panel.add(daySpinner);
        panel.add(calendarBtn);
        panel.add(clearBtn);
        
        return panel;
    }
    
    private void showCalendarPopup(JSpinner yearSpinner, JComboBox<String> monthCombo, JSpinner daySpinner, JButton source) {
        JDialog calendarDialog = new JDialog(this, "Select Date", true);
        calendarDialog.setSize(300, 280);
        calendarDialog.setLocationRelativeTo(source);
        calendarDialog.setLayout(new BorderLayout());
        
        // Month/Year navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevMonth = new JButton("<");
        JButton nextMonth = new JButton(">");
        JLabel monthYearLabel = new JLabel();
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, (Integer) yearSpinner.getValue());
        cal.set(Calendar.MONTH, monthCombo.getSelectedIndex());
        cal.set(Calendar.DAY_OF_MONTH, (Integer) daySpinner.getValue());
        
        // Calendar grid
        JPanel calendarGrid = new JPanel(new GridLayout(7, 7, 2, 2));
        calendarGrid.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        Runnable updateCalendar = () -> {
            calendarGrid.removeAll();
            
            String[] dayNames = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (String dayName : dayNames) {
                JLabel lbl = new JLabel(dayName, SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(Color.GRAY);
                calendarGrid.add(lbl);
            }
            
            Calendar temp = (Calendar) cal.clone();
            temp.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfWeek = temp.get(Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = temp.getActualMaximum(Calendar.DAY_OF_MONTH);
            
            // Empty cells before first day
            for (int i = 0; i < firstDayOfWeek; i++) {
                calendarGrid.add(new JLabel(""));
            }
            
            // Day buttons
            for (int day = 1; day <= daysInMonth; day++) {
                JButton dayBtn = new JButton(String.valueOf(day));
                dayBtn.setMargin(new Insets(2, 2, 2, 2));
                dayBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                
                final int selectedDay = day;
                dayBtn.addActionListener(ev -> {
                    yearSpinner.setValue(cal.get(Calendar.YEAR));
                    monthCombo.setSelectedIndex(cal.get(Calendar.MONTH));
                    daySpinner.setValue(selectedDay);
                    calendarDialog.dispose();
                });
                
                // Highlight current selection
                if (day == (Integer) daySpinner.getValue() && 
                    cal.get(Calendar.MONTH) == monthCombo.getSelectedIndex() &&
                    cal.get(Calendar.YEAR) == (Integer) yearSpinner.getValue()) {
                    dayBtn.setBackground(new Color(59, 130, 246));
                    dayBtn.setForeground(Color.WHITE);
                }
                
                calendarGrid.add(dayBtn);
            }
            
            // Fill remaining cells
            int totalCells = 42; // 6 rows * 7 days
            int usedCells = firstDayOfWeek + daysInMonth;
            for (int i = usedCells; i < totalCells; i++) {
                calendarGrid.add(new JLabel(""));
            }
            
            monthYearLabel.setText(new java.text.SimpleDateFormat("MMMM yyyy").format(cal.getTime()));
            calendarGrid.revalidate();
            calendarGrid.repaint();
        };
        
        prevMonth.addActionListener(ev -> {
            cal.add(Calendar.MONTH, -1);
            updateCalendar.run();
        });
        
        nextMonth.addActionListener(ev -> {
            cal.add(Calendar.MONTH, 1);
            updateCalendar.run();
        });
        
        navPanel.add(prevMonth);
        navPanel.add(monthYearLabel);
        navPanel.add(nextMonth);
        
        updateCalendar.run();
        
        calendarDialog.add(navPanel, BorderLayout.NORTH);
        calendarDialog.add(calendarGrid, BorderLayout.CENTER);
        calendarDialog.setVisible(true);
    }
    
    private LocalDateTime getDateFromPanel(JPanel datePanel) {
        int year = 0, month = 0, day = 0;
        
        for (java.awt.Component comp : datePanel.getComponents()) {
            if (comp instanceof JSpinner) {
                JSpinner spinner = (JSpinner) comp;
                if ("year".equals(spinner.getName())) {
                    year = (Integer) spinner.getValue();
                } else if ("day".equals(spinner.getName())) {
                    day = (Integer) spinner.getValue();
                }
            } else if (comp instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) comp;
                if ("month".equals(combo.getName())) {
                    month = combo.getSelectedIndex() + 1;
                }
            }
        }
        
        if (year > 0 && month > 0 && day > 0) {
            return LocalDateTime.of(year, month, day, 23, 59);
        }
        return null;
    }
    
    private void openAddTaskDialog(Project project, DefaultTableModel taskModel) {
        JDialog dlg = new JDialog(this, "Add Task to " + project.getName(), true);
        dlg.setSize(450, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 500.0, 0.5));
        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        JComboBox<TaskStatus> statusBox = new JComboBox<>(TaskStatus.values());
        
        // Calendar date picker for due date
        JPanel dueDatePanel = createDatePickerPanel(null);
        
        form.add(new JLabel("Task Name:"));
        form.add(nameField);
        form.add(new JLabel("Description:"));
        form.add(descField);
        form.add(new JLabel("Estimated Hours:"));
        form.add(hoursSpinner);
        form.add(new JLabel("Priority:"));
        form.add(priorityBox);
        form.add(new JLabel("Status:"));
        form.add(statusBox);
        form.add(new JLabel("Due Date:"));
        form.add(dueDatePanel);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(59, 130, 246));
        saveBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Task name is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Task task = new Task();
                task.setName(name);
                task.setDescription(descField.getText().trim());
                task.setProject(project);
                // Convert hours to minutes
                double hours = (Double) hoursSpinner.getValue();
                task.setEstimationMinutes((int) (hours * 60));
                task.setPriority((Priority) priorityBox.getSelectedItem());
                task.setStatus((TaskStatus) statusBox.getSelectedItem());
                
                // Get due date from calendar picker
                LocalDateTime dueDate = getDateFromPanel(dueDatePanel);
                task.setDueDate(dueDate);
                
                // Create task via service
                Task createdTask = taskService.creatTask(name, descField.getText().trim(), project, (Priority) priorityBox.getSelectedItem());
                // Update the created task with additional fields
                createdTask.setEstimationMinutes((int) (hours * 60));
                createdTask.setStatus((TaskStatus) statusBox.getSelectedItem());
                createdTask.setDueDate(dueDate);
                taskService.updateTask(createdTask);
                
                // Add to table
                String dueDateStr = dueDate != null ? dueDate.toLocalDate().toString() : "-";
                taskModel.addRow(new Object[] {
                    createdTask.getId(),
                    createdTask.getName(),
                    createdTask.getStatus(),
                    createdTask.getPriority(),
                    String.format("%.1f", hours),
                    dueDateStr
                });
                
                dlg.dispose();
                loadProjects(); // Refresh project list
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Error creating task: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dlg.dispose());
        
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    /* ================= BOTTOM BUTTONS ================= */
    private JPanel createBottomButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(220, 220, 220)));

        JButton newBtn = createPrimaryButton("New Project");
        JButton editBtn = createSecondaryButton("Edit");
        JButton deleteBtn = createDangerButton("Delete");
        JButton refreshBtn = createSecondaryButton("Refresh");

        newBtn.addActionListener(e -> openNewProjectDialog());
        editBtn.addActionListener(e -> editSelectedProject());
        deleteBtn.addActionListener(e -> deleteSelectedProject());
        refreshBtn.addActionListener(e -> loadProjects());

        panel.add(refreshBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(newBtn);

        return panel;
    }
    
    private void openNewProjectDialog() {
        JDialog dlg = new JDialog(this, "Create New Project", true);
        dlg.setSize(450, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField clientField = new JTextField();
        JComboBox<ProjectStatus> statusBox = new JComboBox<>(ProjectStatus.values());
        JSpinner budgetSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 100.0));
        JSpinner rateSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000.0, 5.0));
        
        form.add(new JLabel("Project Name:"));
        form.add(nameField);
        form.add(new JLabel("Description:"));
        form.add(descField);
        form.add(new JLabel("Client:"));
        form.add(clientField);
        form.add(new JLabel("Status:"));
        form.add(statusBox);
        form.add(new JLabel("Budget ($):"));
        form.add(budgetSpinner);
        form.add(new JLabel("Hourly Rate ($):"));
        form.add(rateSpinner);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Create");
        saveBtn.setBackground(new Color(59, 130, 246));
        saveBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Project name is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Project project = new Project();
                project.setName(name);
                project.setDescription(descField.getText().trim());
                project.setClient(clientField.getText().trim());
                project.setStatus((ProjectStatus) statusBox.getSelectedItem());
                project.setBudget((Double) budgetSpinner.getValue());
                project.setHourlyRate((Double) rateSpinner.getValue());
                
                projectService.createProject(name, descField.getText().trim(), clientField.getText().trim(), UserSession.getUserId());
                
                dlg.dispose();
                loadProjects();
                JOptionPane.showMessageDialog(this, "Project created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Error creating project: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dlg.dispose());
        
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
    
    private void editSelectedProject() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a project to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long projectId = (Long) tableModel.getValueAt(row, 0);
        openProjectTasksDialog(projectId);
    }
    
    private void deleteSelectedProject() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a project to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long projectId = (Long) tableModel.getValueAt(row, 0);
        String projectName = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete project '" + projectName + "'?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                projectService.deleteProject(projectId);
                loadProjects();
                JOptionPane.showMessageDialog(this, "Project deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting project: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* ================= BUTTON FACTORY ================= */
    private JButton createPrimaryButton(String text) {
        return new RoundedButton(text, new Color(66, 133, 244), Color.WHITE);
    }

    private JButton createSecondaryButton(String text) {
        return new RoundedButton(text, new Color(230, 230, 230), Color.BLACK);
    }

    private JButton createDangerButton(String text) {
        return new RoundedButton(text, new Color(234, 67, 53), Color.WHITE);
    }

    /* ================= ROUNDED BUTTON ================= */
    static class RoundedButton extends JButton {

        private final Color bgColor;
        private final int radius = 20;

        public RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bgColor = bg;

            setForeground(fg);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(6, 14, 6, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape round = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), radius, radius);

            g2.setColor(bgColor);
            g2.fill(round);

            super.paintComponent(g);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProjectGUI().setVisible(true));
    }
}