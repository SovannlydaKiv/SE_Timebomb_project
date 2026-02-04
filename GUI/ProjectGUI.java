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
                projects = projectService.getAllProjects();
            } else {
                projects = projectService.getAllProjects(); // Filter in memory
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
            List<Project> projects = projectService.getAllProjects();
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
            dlg.setSize(700, 500);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new BorderLayout());
            
            // Task table
            String[] taskCols = { "ID", "Name", "Status", "Priority", "Est. Hours", "Due Date" };
            DefaultTableModel taskModel = new DefaultTableModel(taskCols, 0);
            
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
            JScrollPane scrollPane = new JScrollPane(taskTable);
            
            // Add task button
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton addTaskBtn = new JButton("Add Task");
            addTaskBtn.setBackground(new Color(59, 130, 246));
            addTaskBtn.setForeground(Color.WHITE);
            addTaskBtn.addActionListener(e -> {
                openAddTaskDialog(project, taskModel);
            });
            
            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(e -> dlg.dispose());
            
            btnPanel.add(addTaskBtn);
            btnPanel.add(closeBtn);
            
            dlg.add(new JLabel("  Tasks (Double-click project row to view)", SwingConstants.LEFT), BorderLayout.NORTH);
            dlg.add(scrollPane, BorderLayout.CENTER);
            dlg.add(btnPanel, BorderLayout.SOUTH);
            dlg.setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openAddTaskDialog(Project project, DefaultTableModel taskModel) {
        JDialog dlg = new JDialog(this, "Add Task to " + project.getName(), true);
        dlg.setSize(400, 350);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 100.0, 0.5));
        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        JComboBox<TaskStatus> statusBox = new JComboBox<>(TaskStatus.values());
        
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
                
                taskService.creatTask(name, descField.getText().trim(), project, (Priority) priorityBox.getSelectedItem());
                
                // Add to table
                taskModel.addRow(new Object[] {
                    task.getId(),
                    task.getName(),
                    task.getStatus(),
                    task.getPriority(),
                    String.format("%.1f", hours),
                    "-"
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
                
                projectService.createProject(name, descField.getText().trim(), clientField.getText().trim());
                
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
            setBorder(new EmptyBorder(6, 14, 6, 14)); // 👈 smaller padding
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

    private void openProjectForm() {
        new ProjectForm(this).setVisible(true);
    }

    private void showMessage(String action) {
        JOptionPane.showMessageDialog(this, action + " clicked");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProjectGUI().setVisible(true));
    }
}