import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import service.ProjectService;
import service.TaskService;
import service.TimeEntryService;
import service.UserService;
import model.*;

public class TrackerPage extends JFrame {

    private JLabel timerLabel;
    private boolean running = false;
    private int seconds = 0;
    private final Timer timer;
    private JButton startPauseBtn;
    private JButton stopBtn;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    
    // Services
    private ProjectService projectService;
    private TaskService taskService;
    private TimeEntryService timeEntryService;
    private UserService userService;
    
    // Entries panel for refresh
    private JPanel entriesPanel;
    
    // Selected task for timing
    private Task selectedTask;
    private JLabel selectedTaskLabel;
    private LocalDateTime timerStartTime;
    
    // Manual entry components
    private JTextField descriptionField;
    private String selectedCategory = "Work";
    private JPanel categoryPanel;
    private JToggleButton projectTaskToggle;
    private JToggleButton personalToggle;
    private JPanel taskSelectionPanel;
    private JPanel manualEntryPanel;

    // Colors for modern UI
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246);
    private static final Color PRIMARY_GREEN = new Color(16, 185, 129);
    private static final Color PRIMARY_RED = new Color(239, 68, 68);
    private static final Color DARK_TEXT = new Color(31, 41, 55);
    private static final Color LIGHT_TEXT = new Color(107, 114, 128);
    private static final Color BG_GRAY = new Color(249, 250, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    public TrackerPage() {
        projectService = new ProjectService();
        taskService = new TaskService();
        timeEntryService = new TimeEntryService();
        userService = new UserService();
        
        setTitle("Time Tracker");
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.85);
        int height = (int)(screenSize.height * 0.85);
        setSize(width, height);
        
        setMinimumSize(new Dimension(900, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_GRAY);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        timer = new Timer(1000, e -> updateTimer());
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(12, 25, 12, 25)
        ));

        // Left side - Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(IconHelper.createClockIcon(28));
        JLabel title = new JLabel("Time Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(DARK_TEXT);

        leftPanel.add(iconLabel);
        leftPanel.add(title);

        // Center - Navigation tabs
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        navPanel.setOpaque(false);

        JButton trackerBtn = createNavButton("Tracker", true);
        JButton projectBtn = createNavButton("Projects", false);
        JButton summaryBtn = createNavButton("Summary", false);

        projectBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ProjectGUI().setVisible(true));
        });

        summaryBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new SummaryPage().setVisible(true));
        });

        navPanel.add(trackerBtn);
        navPanel.add(projectBtn);
        navPanel.add(summaryBtn);

        // Right side - User info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        JPanel userBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userBox.setOpaque(false);

        usernameLabel = new JLabel(UserSession.getUsername());
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(DARK_TEXT);
        
        emailLabel = new JLabel("(" + UserSession.getEmail() + ")");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailLabel.setForeground(LIGHT_TEXT);

        JButton editBtn = new JButton("Edit");
        styleSmallButton(editBtn, new Color(229, 231, 235), DARK_TEXT);
        editBtn.addActionListener(e -> openEditUserDialog());

        JButton logoutBtn = new JButton("Logout");
        styleSmallButton(logoutBtn, PRIMARY_RED, Color.WHITE);
        logoutBtn.addActionListener(e -> {
            UserSession.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        });

        userBox.add(usernameLabel);
        userBox.add(emailLabel);
        
        rightPanel.add(userBox);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(editBtn);
        rightPanel.add(logoutBtn);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(navPanel, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JButton createNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 38));
        
        if (active) {
            btn.setBackground(PRIMARY_BLUE);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(243, 244, 246));
            btn.setForeground(DARK_TEXT);
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(229, 231, 235));
                }
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(243, 244, 246));
                }
            });
        }
        return btn;
    }

    private void styleSmallButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(70, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void openEditUserDialog() {
        JDialog dlg = new JDialog(this, "Edit Profile", true);
        dlg.setSize(400, 250);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(CARD_BG);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 25, 15, 25));
        p.setBackground(CARD_BG);

        JTextField userField = new JTextField(UserSession.getUsername());
        JTextField emailFieldInput = new JTextField(UserSession.getEmail());
        
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        userField.setPreferredSize(new Dimension(300, 38));
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        emailFieldInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        emailFieldInput.setPreferredSize(new Dimension(300, 38));
        emailFieldInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailFieldInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel emailLabelText = new JLabel("Email");
        emailLabelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabelText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailFieldInput.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(userLabel);
        p.add(Box.createVerticalStrut(6));
        p.add(userField);
        p.add(Box.createVerticalStrut(15));
        p.add(emailLabelText);
        p.add(Box.createVerticalStrut(6));
        p.add(emailFieldInput);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(CARD_BG);
        
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        stylePrimaryButton(save);
        styleSecondaryButton(cancel);

        save.addActionListener(ae -> {
            String newUser = userField.getText().trim();
            String newEmail = emailFieldInput.getText().trim();
            if (!newUser.isEmpty() && !newEmail.isEmpty()) {
                // Update in database
                model.User currentUser = UserSession.getCurrentUser();
                if (currentUser != null) {
                    currentUser.setUsername(newUser);
                    currentUser.setEmail(newEmail);
                    boolean success = userService.updateUser(currentUser);
                    if (success) {
                        usernameLabel.setText(newUser);
                        emailLabel.setText("(" + newEmail + ")");
                        UserSession.set(newUser, newEmail);
                        JOptionPane.showMessageDialog(dlg, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(dlg, "Failed to update profile. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            dlg.dispose();
        });

        cancel.addActionListener(ae -> dlg.dispose());

        btns.add(cancel);
        btns.add(save);

        dlg.add(p, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(25, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 20, 0);

        // Timer card - compact
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.45;
        container.add(createTimerCard(), gbc);

        // Entries card - more space for recent entries
        gbc.gridy = 1;
        gbc.weighty = 0.55;
        gbc.insets = new Insets(0, 0, 0, 0);
        container.add(createEntriesCard(), gbc);

        return container;
    }

    private JPanel createTimerCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 10));

        // Top section - Timer display
        JPanel timerSection = new JPanel();
        timerSection.setLayout(new BoxLayout(timerSection, BoxLayout.Y_AXIS));
        timerSection.setOpaque(false);
        timerSection.setBorder(new EmptyBorder(15, 0, 5, 0));

        timerLabel = new JLabel("00:00:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 64));
        timerLabel.setForeground(DARK_TEXT);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("Ready to track");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(LIGHT_TEXT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timerSection.add(timerLabel);
        timerSection.add(Box.createVerticalStrut(5));
        timerSection.add(statusLabel);

        // Middle section - Mode toggle and input
        JPanel inputSection = new JPanel();
        inputSection.setLayout(new BoxLayout(inputSection, BoxLayout.Y_AXIS));
        inputSection.setOpaque(false);
        inputSection.setBorder(new EmptyBorder(10, 30, 10, 30));

        // Mode toggle buttons
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        modePanel.setOpaque(false);

        projectTaskToggle = new JToggleButton("Select from Project");
        personalToggle = new JToggleButton("Personal Entry");
        
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(projectTaskToggle);
        modeGroup.add(personalToggle);
        
        styleToggleButton(projectTaskToggle, true);
        styleToggleButton(personalToggle, false);
        
        projectTaskToggle.setSelected(true);

        projectTaskToggle.addActionListener(e -> {
            showProjectTaskMode();
            styleToggleButton(projectTaskToggle, true);
            styleToggleButton(personalToggle, false);
        });

        personalToggle.addActionListener(e -> {
            showPersonalMode();
            styleToggleButton(personalToggle, true);
            styleToggleButton(projectTaskToggle, false);
        });

        modePanel.add(projectTaskToggle);
        modePanel.add(personalToggle);

        // Task selection panel (for project mode)
        taskSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        taskSelectionPanel.setOpaque(false);

        selectedTaskLabel = new JLabel("No task selected");
        selectedTaskLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        selectedTaskLabel.setForeground(LIGHT_TEXT);

        JButton selectTaskBtn = new JButton("Select Task");
        selectTaskBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectTaskBtn.setBackground(PRIMARY_GREEN);
        selectTaskBtn.setForeground(Color.WHITE);
        selectTaskBtn.setFocusPainted(false);
        selectTaskBtn.setBorderPainted(false);
        selectTaskBtn.setPreferredSize(new Dimension(130, 36));
        selectTaskBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectTaskBtn.addActionListener(e -> openTaskSelectionDialog());

        taskSelectionPanel.add(selectedTaskLabel);
        taskSelectionPanel.add(selectTaskBtn);

        // Manual entry panel (for personal mode)
        manualEntryPanel = new JPanel(new BorderLayout());
        manualEntryPanel.setOpaque(false);
        manualEntryPanel.setBorder(new EmptyBorder(15, 50, 5, 50));
        manualEntryPanel.setVisible(false);

        JPanel manualContent = new JPanel();
        manualContent.setLayout(new BoxLayout(manualContent, BoxLayout.Y_AXIS));
        manualContent.setOpaque(false);

        JLabel descLabel = new JLabel("What are you working on?");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        descLabel.setForeground(DARK_TEXT);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        descriptionField = new JTextField();
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionField.setPreferredSize(new Dimension(500, 42));
        descriptionField.setMinimumSize(new Dimension(300, 42));
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        descriptionField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel catLabel = new JLabel("Category:");
        catLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        catLabel.setForeground(DARK_TEXT);
        catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        categoryPanel = createCategoryButtons();
        categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        manualContent.add(descLabel);
        manualContent.add(Box.createVerticalStrut(8));
        manualContent.add(descriptionField);
        manualContent.add(Box.createVerticalStrut(15));
        manualContent.add(catLabel);
        manualContent.add(Box.createVerticalStrut(8));
        manualContent.add(categoryPanel);
        
        manualEntryPanel.add(manualContent, BorderLayout.CENTER);

        inputSection.add(modePanel);
        inputSection.add(Box.createVerticalStrut(10));
        inputSection.add(taskSelectionPanel);
        inputSection.add(manualEntryPanel);

        // Bottom section - Buttons
        JPanel buttonSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonSection.setOpaque(false);
        buttonSection.setBorder(new EmptyBorder(5, 0, 10, 0));

        startPauseBtn = new JButton("Start Timer");
        startPauseBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        startPauseBtn.setBackground(PRIMARY_BLUE);
        startPauseBtn.setForeground(Color.WHITE);
        startPauseBtn.setFocusPainted(false);
        startPauseBtn.setBorderPainted(false);
        startPauseBtn.setPreferredSize(new Dimension(160, 45));
        startPauseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        stopBtn = new JButton("Stop & Save");
        stopBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        stopBtn.setBackground(new Color(229, 231, 235));
        stopBtn.setForeground(DARK_TEXT);
        stopBtn.setFocusPainted(false);
        stopBtn.setBorderPainted(false);
        stopBtn.setPreferredSize(new Dimension(160, 45));
        stopBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        stopBtn.setEnabled(false);

        startPauseBtn.addActionListener(e -> toggleTimer());
        stopBtn.addActionListener(e -> stopAndSaveTimer());

        buttonSection.add(startPauseBtn);
        buttonSection.add(stopBtn);

        card.add(timerSection, BorderLayout.NORTH);
        card.add(inputSection, BorderLayout.CENTER);
        card.add(buttonSection, BorderLayout.SOUTH);

        return card;
    }

    private void styleToggleButton(JToggleButton btn, boolean selected) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (selected) {
            btn.setBackground(PRIMARY_BLUE);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(243, 244, 246));
            btn.setForeground(DARK_TEXT);
        }
    }

    private void showProjectTaskMode() {
        taskSelectionPanel.setVisible(true);
        manualEntryPanel.setVisible(false);
        selectedTask = null;
        selectedTaskLabel.setText("No task selected");
        selectedTaskLabel.setForeground(LIGHT_TEXT);
    }

    private void showPersonalMode() {
        taskSelectionPanel.setVisible(false);
        manualEntryPanel.setVisible(true);
        selectedTask = null;
    }

    private JPanel createCategoryButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panel.setOpaque(false);

        String[] categories = {"Work", "Study", "Personal", "Exercise", "Creative", "Meeting"};
        Color[] colors = {
            new Color(59, 130, 246),
            new Color(139, 92, 246),
            new Color(16, 185, 129),
            new Color(245, 158, 11),
            new Color(236, 72, 153),
            new Color(99, 102, 241)
        };

        ButtonGroup catGroup = new ButtonGroup();
        
        for (int i = 0; i < categories.length; i++) {
            JToggleButton catBtn = new JToggleButton(categories[i]);
            final Color btnColor = colors[i];
            final String category = categories[i];
            
            catBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            catBtn.setFocusPainted(false);
            catBtn.setPreferredSize(new Dimension(90, 32));
            catBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (i == 0) {
                catBtn.setSelected(true);
                catBtn.setBackground(btnColor);
                catBtn.setForeground(Color.WHITE);
                catBtn.setBorderPainted(false);
            } else {
                catBtn.setBackground(new Color(243, 244, 246));
                catBtn.setForeground(DARK_TEXT);
                catBtn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            }
            
            final int colorIndex = i;
            catBtn.addActionListener(e -> {
                selectedCategory = category;
                // Update all buttons
                int idx = 0;
                for (Component c : panel.getComponents()) {
                    if (c instanceof JToggleButton) {
                        JToggleButton tb = (JToggleButton) c;
                        if (tb.isSelected()) {
                            tb.setBackground(colors[colorIndex]);
                            tb.setForeground(Color.WHITE);
                            tb.setBorderPainted(false);
                        } else {
                            tb.setBackground(new Color(243, 244, 246));
                            tb.setForeground(DARK_TEXT);
                            tb.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
                        }
                        idx++;
                    }
                }
            });
            
            catGroup.add(catBtn);
            panel.add(catBtn);
        }

        return panel;
    }

    private void openTaskSelectionDialog() {
        JDialog dlg = new JDialog(this, "Select Task from Project", true);
        dlg.setSize(520, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(CARD_BG);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(new EmptyBorder(18, 18, 10, 18));
        content.setBackground(CARD_BG);

        // Project selection
        JPanel projectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        projectPanel.setOpaque(false);
        
        JLabel projLabel = new JLabel("Project:");
        projLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        projectPanel.add(projLabel);
        
        JComboBox<Project> projectCombo = new JComboBox<>();
        projectCombo.setPreferredSize(new Dimension(320, 32));
        projectCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        try {
            List<Project> projects = projectService.getAllProjects(UserSession.getUserId());
            for (Project p : projects) {
                projectCombo.addItem(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        projectCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Project) {
                    Project p = (Project) value;
                    setText(p.getName() + (p.getClient() != null ? " (" + p.getClient() + ")" : ""));
                }
                return this;
            }
        });
        
        projectPanel.add(projectCombo);

        // Task list
        DefaultListModel<Task> taskListModel = new DefaultListModel<>();
        JList<Task> taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taskList.setFixedCellHeight(45);
        taskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel cell = new JPanel(new BorderLayout(10, 0));
                cell.setBorder(new EmptyBorder(8, 12, 8, 12));
                
                if (isSelected) {
                    cell.setBackground(new Color(239, 246, 255));
                } else {
                    cell.setBackground(index % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                }
                
                if (value instanceof Task) {
                    Task t = (Task) value;
                    
                    JLabel nameLabel = new JLabel(t.getName());
                    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    nameLabel.setForeground(DARK_TEXT);
                    
                    String estimate = t.getEstimationMinutes() != null ? 
                        String.format("%.1f hrs", t.getEstimationMinutes() / 60.0) : "";
                    
                    JLabel infoLabel = new JLabel(t.getStatus() + "  |  " + estimate);
                    infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    infoLabel.setForeground(LIGHT_TEXT);
                    
                    JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
                    textPanel.setOpaque(false);
                    textPanel.add(nameLabel);
                    textPanel.add(infoLabel);
                    
                    cell.add(textPanel, BorderLayout.CENTER);
                }
                
                return cell;
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        // Load tasks when project is selected
        projectCombo.addActionListener(e -> {
            taskListModel.clear();
            Project selectedProject = (Project) projectCombo.getSelectedItem();
            if (selectedProject != null) {
                try {
                    List<Task> tasks = taskService.getTaskByProject(selectedProject.getId());
                    for (Task t : tasks) {
                        taskListModel.addElement(t);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        if (projectCombo.getItemCount() > 0) {
            projectCombo.setSelectedIndex(0);
        }

        content.add(projectPanel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnPanel.setBackground(new Color(249, 250, 251));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        
        JButton cancelBtn = new JButton("Cancel");
        JButton selectBtn = new JButton("Select Task");
        styleSecondaryButton(cancelBtn);
        stylePrimaryButton(selectBtn);

        selectBtn.addActionListener(e -> {
            Task selected = taskList.getSelectedValue();
            if (selected != null) {
                selectedTask = selected;
                Project proj = (Project) projectCombo.getSelectedItem();
                selectedTaskLabel.setText(selected.getName() + " (" + proj.getName() + ")");
                selectedTaskLabel.setForeground(PRIMARY_GREEN);
                selectedTaskLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Please select a task", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dlg.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(selectBtn);

        dlg.add(content, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private JPanel createEntriesCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 12));

        JLabel title = new JLabel("Recent Time Entries");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(DARK_TEXT);
        title.setBorder(new EmptyBorder(0, 0, 8, 0));

        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        entriesPanel.setOpaque(false);

        loadEntriesIntoPanel();

        JScrollPane scrollPane = new JScrollPane(entriesPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }
    
    private void loadEntriesIntoPanel() {
        entriesPanel.removeAll();
        try {
            List<TimeEntry> entries = timeEntryService.getRecentEntries(5, UserSession.getUserId());
            if (entries.isEmpty()) {
                JLabel noEntries = new JLabel("No time entries yet. Start tracking your time!");
                noEntries.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                noEntries.setForeground(LIGHT_TEXT);
                noEntries.setBorder(new EmptyBorder(15, 8, 15, 8));
                entriesPanel.add(noEntries);
            } else {
                for (TimeEntry entry : entries) {
                    String taskName = entry.getTask() != null ? entry.getTask().getName() : "Personal";
                    String time = entry.getFormattedDurationHMS();
                    String desc = entry.getDescription() != null ? entry.getDescription() : "";
                    entriesPanel.add(createEntryRow(taskName, desc, time));
                }
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading entries");
            errorLabel.setForeground(PRIMARY_RED);
            entriesPanel.add(errorLabel);
        }
    }
    
    private void refreshEntriesPanel() {
        loadEntriesIntoPanel();
        entriesPanel.revalidate();
        entriesPanel.repaint();
    }

    private JPanel createEntryRow(String name, String description, String time) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(new Color(249, 250, 251));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(10, 12, 10, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        leftPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(DARK_TEXT);

        String descText = description != null && !description.isEmpty() ? description : "No description";
        JLabel descLabel = new JLabel(descText);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(LIGHT_TEXT);

        leftPanel.add(nameLabel);
        leftPanel.add(descLabel);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timeLabel.setForeground(PRIMARY_BLUE);

        row.add(leftPanel, BorderLayout.CENTER);
        row.add(timeLabel, BorderLayout.EAST);

        return row;
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(18, 22, 18, 22)
        ));
        return card;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setBackground(new Color(229, 231, 235));
        btn.setForeground(DARK_TEXT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(90, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void toggleTimer() {
        // Check if we have either a task selected OR a description for personal mode
        if (projectTaskToggle.isSelected() && selectedTask == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a task first", 
                "No Task Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (personalToggle.isSelected() && descriptionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a description for your time entry", 
                "No Description", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        running = !running;
        if (running) {
            if (timerStartTime == null) {
                timerStartTime = LocalDateTime.now();
            }
            timer.start();
            startPauseBtn.setText("Pause");
            startPauseBtn.setBackground(new Color(245, 158, 11));
            stopBtn.setEnabled(false);
            stopBtn.setBackground(new Color(229, 231, 235));
        } else {
            timer.stop();
            startPauseBtn.setText("Resume");
            startPauseBtn.setBackground(PRIMARY_GREEN);
            stopBtn.setEnabled(true);
            stopBtn.setBackground(PRIMARY_RED);
            stopBtn.setForeground(Color.WHITE);
        }
    }

    private void stopAndSaveTimer() {
        timer.stop();
        running = false;
        
        if (seconds > 0) {
            try {
                String description;
                String taskName;
                Long taskId = null;
                
                if (projectTaskToggle.isSelected() && selectedTask != null) {
                    taskId = selectedTask.getId();
                    taskName = selectedTask.getName();
                    description = "Timer entry";
                } else {
                    description = descriptionField.getText().trim() + " [" + selectedCategory + "]";
                    taskName = "Personal - " + selectedCategory;
                }
                
                // Use the new saveTimeEntry method that accepts null taskId and duration in seconds
                timeEntryService.saveTimeEntry(taskId, description, seconds, UserSession.getUserId());
                
                // Format the time display
                int h = seconds / 3600;
                int m = (seconds % 3600) / 60;
                int s = seconds % 60;
                String timeDisplay = String.format("%02d:%02d:%02d", h, m, s);
                
                JOptionPane.showMessageDialog(this, 
                    String.format("Time entry saved!\n\nTask: %s\nDuration: %s", taskName, timeDisplay),
                    "Entry Saved", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the entries panel
                refreshEntriesPanel();
                    
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving time entry: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // Reset everything
        seconds = 0;
        timerStartTime = null;
        timerLabel.setText("00:00:00");
        startPauseBtn.setText("Start Timer");
        startPauseBtn.setBackground(PRIMARY_BLUE);
        stopBtn.setEnabled(false);
        stopBtn.setBackground(new Color(229, 231, 235));
        stopBtn.setForeground(DARK_TEXT);
        
        descriptionField.setText("");
        selectedTask = null;
        selectedTaskLabel.setText("No task selected");
        selectedTaskLabel.setForeground(LIGHT_TEXT);
        selectedTaskLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
    }

    private void updateTimer() {
        seconds++;
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d:%02d", h, m, s));
    }
}
