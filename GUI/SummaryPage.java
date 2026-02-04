import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class SummaryPage extends JFrame {

    public SummaryPage() {
        setTitle("Time Tracker - Summary");
        
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

        JButton createProjectBtn = new JButton("Create Project");
        createProjectBtn.setBackground(new Color(59, 130, 246));
        createProjectBtn.setForeground(Color.WHITE);
        createProjectBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createProjectBtn.setFocusPainted(false);
        createProjectBtn.setBorderPainted(false);
        createProjectBtn.setPreferredSize(new Dimension(140, 35));
        createProjectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        createProjectBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ProjectGUI().setVisible(true));
        });

        JButton summaryBtn = new JButton("Summary");
        summaryBtn.setBackground(new Color(59, 130, 246));
        summaryBtn.setForeground(Color.WHITE);
        summaryBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryBtn.setFocusPainted(false);
        summaryBtn.setBorderPainted(false);
        summaryBtn.setPreferredSize(new Dimension(100, 35));
        summaryBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        rightPanel.add(createProjectBtn);
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
        container.setBorder(new EmptyBorder(30, 50, 30, 50));

        container.add(createStatCard("Today's Total", "05:00:00", IconHelper.createCalendarIcon()));
        container.add(Box.createVerticalStrut(20));
        container.add(createStatCard("Tasks Completed", "4", IconHelper.createChartIcon()));
        container.add(Box.createVerticalStrut(20));
        container.add(createStatCard("Categories Used", "2", IconHelper.createTagIcon()));
        container.add(Box.createVerticalStrut(20));
        container.add(createCategoryCard());
        container.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        
        return wrapper;
    }

    private JPanel createStatCard(String title, String value, ImageIcon icon) {
        JPanel card = createCard();
        card.setPreferredSize(new Dimension(800, 120));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(107, 114, 128));

        headerPanel.add(iconLabel);
        headerPanel.add(label);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    private JPanel createCategoryCard() {
        JPanel card = createCard();
        card.setPreferredSize(new Dimension(800, 200));

        JLabel title = new JLabel("Time by Category");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(20));
        card.add(createCategoryBar("Work", new Color(59, 130, 246), 4, 0, 0));
        card.add(Box.createVerticalStrut(20));
        card.add(createCategoryBar("Meetings", new Color(168, 85, 247), 1, 0, 0));

        return card;
    }

    private JPanel createCategoryBar(String category, Color color, int hours, int minutes, int seconds) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftPanel.setOpaque(false);

        JLabel colorDot = new JLabel("●");
        colorDot.setForeground(color);
        colorDot.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        leftPanel.add(colorDot);
        leftPanel.add(categoryLabel);

        JLabel timeLabel = new JLabel(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        infoPanel.add(leftPanel, BorderLayout.WEST);
        infoPanel.add(timeLabel, BorderLayout.EAST);

        JPanel barContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = 10;
                
                int totalSeconds = hours * 3600 + minutes * 60 + seconds;
                int maxSeconds = 5 * 3600;
                double percentage = (double) totalSeconds / maxSeconds;
                int filledWidth = (int) (width * percentage);
                
                g2.setColor(new Color(229, 231, 235));
                g2.fillRoundRect(0, 0, width, height, 5, 5);
                
                g2.setColor(color);
                g2.fillRoundRect(0, 0, filledWidth, height, 5, 5);
                
                g2.dispose();
            }
        };
        barContainer.setOpaque(false);
        barContainer.setPreferredSize(new Dimension(100, 10));
        barContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));

        container.add(infoPanel);
        container.add(Box.createVerticalStrut(8));
        container.add(barContainer);

        return container;
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(20, 25, 20, 25)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }
}