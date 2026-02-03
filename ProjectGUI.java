import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ProjectGUI extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public ProjectGUI() {
        setTitle("Project Manager");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(createTopBar(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createBottomButtons(), BorderLayout.SOUTH);
    }

    /* ================= TOP BAR ================= */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("Project");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        topBar.add(title, BorderLayout.WEST);
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

        JComboBox<String> statusBox = new JComboBox<>(
                new String[] { "All", "NEW", "ACTIVE", "COMPLETED" });

        JTextField clientField = new JTextField(12);
        JButton filterBtn = createPrimaryButton("Filter");

        filterBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Filter clicked"));

        card.add(new JLabel("Status"));
        card.add(statusBox);
        card.add(new JLabel("Client"));
        card.add(clientField);
        card.add(filterBtn);

        return card;
    }

    /* ================= TABLE CARD ================= */
    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)));

        String[] cols = { "ID", "Name", "Client", "Status", "Tasks", "Deadline" };
        tableModel = new DefaultTableModel(cols, 0);

        tableModel.addRow(new Object[] { 1, "ISE", "Lyda", "ACTIVE", 5, "2026-02-10" });
        tableModel.addRow(new Object[] { 2, "Project & Seminar", "Vattey", "NEW", 2, "2026-03-01" });
        tableModel.addRow(new Object[] { 3, "UI & UX", "Monika", "COMPLETED", 12, "2025-12-20" });

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 242, 245));
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
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

        newBtn.addActionListener(e -> openProjectForm());
        editBtn.addActionListener(e -> showMessage("Edit"));
        deleteBtn.addActionListener(e -> showMessage("Delete"));
        refreshBtn.addActionListener(e -> showMessage("Refresh"));

        panel.add(refreshBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(newBtn);

        return panel;
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
