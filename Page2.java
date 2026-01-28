import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

public class Page2 extends JFrame {

    public Page2() {
        setTitle("Time Tracker");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(5, 0, 5, 0));

        // Create icon label with clock icon
        JLabel iconLabel = new JLabel(createClockIcon());

        JLabel title = new JLabel("Time Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        header.add(iconLabel);
        header.add(title);
        
        return header;
    }

    private JPanel createMainPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(30, 50, 30, 50));

        container.add(createTodaysTotalCard());
        container.add(Box.createVerticalStrut(20));
        container.add(createTasksCompletedCard());
        container.add(Box.createVerticalStrut(20));
        container.add(createCategoriesUsedCard());
        container.add(Box.createVerticalStrut(20));
        container.add(createTimeByCategoryCard());

        return container;
    }

    private JPanel createTodaysTotalCard() {
        JPanel card = createCard();
        card.setPreferredSize(new Dimension(800, 130));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel(createCalendarIcon());
        
        JLabel label = new JLabel("Today's Total");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(107, 114, 128));

        headerPanel.add(icon);
        headerPanel.add(label);

        JLabel timeLabel = new JLabel("05:00:00");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(timeLabel);

        return card;
    }

    private JPanel createTasksCompletedCard() {
        JPanel card = createCard();
        card.setPreferredSize(new Dimension(800, 130));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel(createChartIcon());

        JLabel label = new JLabel("Tasks Completed");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(107, 114, 128));

        headerPanel.add(icon);
        headerPanel.add(label);

        JLabel countLabel = new JLabel("4");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(countLabel);

        return card;
    }

    private JPanel createCategoriesUsedCard() {
        JPanel card = createCard();
        card.setPreferredSize(new Dimension(800, 130));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel(createTagIcon());

        JLabel label = new JLabel("Categories Used");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(107, 114, 128));

        headerPanel.add(icon);
        headerPanel.add(label);

        JLabel countLabel = new JLabel("2");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(countLabel);

        return card;
    }

    private JPanel createTimeByCategoryCard() {
        JPanel card = createCard();
        card.setPreferredSize(new Dimension(800, 220));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

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
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Category name and time
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

        // Progress bar with rounded corners
        JPanel barContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = 10;
                
                // Calculate percentage (out of 5 hours total)
                int totalSeconds = hours * 3600 + minutes * 60 + seconds;
                int maxSeconds = 5 * 3600;
                double percentage = (double) totalSeconds / maxSeconds;
                int filledWidth = (int) (width * percentage);
                
                // Draw background (gray)
                g2.setColor(new Color(229, 231, 235));
                g2.fillRoundRect(0, 0, width, height, 5, 5);
                
                // Draw filled portion (colored)
                g2.setColor(color);
                g2.fillRoundRect(0, 0, filledWidth, height, 5, 5);
                
                g2.dispose();
            }
        };
        barContainer.setOpaque(false);
        barContainer.setPreferredSize(new Dimension(Integer.MAX_VALUE, 10));
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

    // ===== ICON CREATION METHODS =====
    
    private ImageIcon createClockIcon() {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Blue circle
        g2.setColor(new Color(59, 130, 246));
        g2.fillOval(2, 2, 20, 20);
        
        // White clock hands
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(12, 12, 12, 8);  // Hour hand
        g2.drawLine(12, 12, 15, 12); // Minute hand
        
        g2.dispose();
        return new ImageIcon(img);
    }
    
    private ImageIcon createCalendarIcon() {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Blue circle
        g2.setColor(new Color(59, 130, 246));
        g2.fillOval(2, 2, 20, 20);
        
        // White calendar
        g2.setColor(Color.WHITE);
        g2.fillRect(7, 9, 10, 8);
        g2.setColor(new Color(59, 130, 246));
        g2.drawLine(7, 11, 17, 11); // Horizontal line
        g2.drawLine(12, 11, 12, 17); // Vertical line
        
        g2.dispose();
        return new ImageIcon(img);
    }
    
    private ImageIcon createChartIcon() {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Purple circle
        g2.setColor(new Color(168, 85, 247));
        g2.fillOval(2, 2, 20, 20);
        
        // White bars (chart)
        g2.setColor(Color.WHITE);
        g2.fillRect(7, 14, 2, 4);  // Short bar
        g2.fillRect(11, 11, 2, 7); // Medium bar
        g2.fillRect(15, 9, 2, 9);  // Tall bar
        
        g2.dispose();
        return new ImageIcon(img);
    }
    
    private ImageIcon createTagIcon() {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Green circle
        g2.setColor(new Color(34, 197, 94));
        g2.fillOval(2, 2, 20, 20);
        
        // White tag/diamond shape
        g2.setColor(Color.WHITE);
        int[] xPoints = {8, 12, 16, 12};
        int[] yPoints = {12, 8, 12, 16};
        g2.fillPolygon(xPoints, yPoints, 4);
        
        // Small hole in tag
        g2.setColor(new Color(34, 197, 94));
        g2.fillOval(11, 10, 2, 2);
        
        g2.dispose();
        return new ImageIcon(img);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Page2().setVisible(true));
    }
}