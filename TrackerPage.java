import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;


public class TrackerPage extends JFrame {

    private JLabel timerLabel;
    private boolean running = false;
    private int seconds = 0;
    private final Timer timer;
    private JButton[] categoryButtons;
    private JButton startPauseBtn;
    private JButton stopBtn;

    public TrackerPage() {
        setTitle("Time Tracker - Timer");
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.8);
        int height = (int)(screenSize.height * 0.8);
        setSize(width, height);
        
        setMinimumSize(new Dimension(600, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        timer = new Timer(1000, e -> updateTimer());
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Left side with icon and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(IconHelper.createClockIcon(24));
        JLabel title = new JLabel("Time Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        leftPanel.add(iconLabel);
        leftPanel.add(title);

        // Right side with navigation buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton trackerBtn = new JButton("Tracker");
        trackerBtn.setBackground(new Color(59, 130, 246));
        trackerBtn.setForeground(Color.WHITE);
        trackerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        trackerBtn.setFocusPainted(false);
        trackerBtn.setBorderPainted(false);
        trackerBtn.setPreferredSize(new Dimension(100, 35));
        trackerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        });

        rightPanel.add(trackerBtn);
        rightPanel.add(summaryBtn);
        rightPanel.add(logoutBtn);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

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

        JTextField taskField = new JTextField();
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

        // Button panel
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

        JLabel total = new JLabel("Total: 05:00:00");
        total.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(total, BorderLayout.EAST);

        card.add(header);
        card.add(Box.createVerticalStrut(15));
        card.add(createEntry("Project Planning", "Work", "02:00:00"));
        card.add(createEntry("Email Responses", "Work", "00:30:00"));
        card.add(createEntry("Team Meeting", "Meetings", "01:00:00"));

        return card;
    }

    private JPanel createEntry(String name, String category, String time) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel left = new JLabel("• " + name + " (" + category + ")");
        left.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel right = new JLabel(time);
        right.setFont(new Font("Segoe UI", Font.BOLD, 14));

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);

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
        btn.setBackground(new Color(230, 230, 230));
        btn.setForeground(Color.BLACK);
        btn.addActionListener(e -> setActiveCategory(btn));
        return btn;
    }

    private void setActiveCategory(JButton selected) {
        for (JButton btn : categoryButtons) {
            btn.setBackground(new Color(230, 230, 230));
            btn.setForeground(Color.BLACK);
        }
        selected.setBackground(new Color(37, 99, 235));
        selected.setForeground(Color.WHITE);
    }

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
        seconds = 0;
        timerLabel.setText("00:00:00");
        startPauseBtn.setText("Start Timer");
        stopBtn.setEnabled(false);
    }

    private void updateTimer() {
        seconds++;
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d:%02d", h, m, s));
    }
}