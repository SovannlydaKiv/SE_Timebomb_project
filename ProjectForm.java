import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ProjectForm extends JDialog {

    public ProjectForm(JFrame parent) {
        super(parent, "New Project", true);
        setSize(600, 750);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createFormArea(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    /* ================= HEADER ================= */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Create Project");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);
        return header;
    }

    /* ================= FORM AREA ================= */
    private JPanel createFormArea() {
        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(new Color(245, 247, 250));
        background.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 220, 220)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JTextField nameField = createInput();
        JTextField clientField = createInput();
        JTextField colorField = createInput();
        JTextField rateField = createInput();
        JTextField budgetField = createInput();

        // ✅ Status dropdown like your screenshot
        JComboBox<String> statusBox = new JComboBox<>(
                new String[] { "NEW", "ACTIVE", "COMPLETED" });
        statusBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // ✅ Description input box
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(new LineBorder(new Color(210, 210, 210)));

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(new LineBorder(new Color(210, 210, 210)));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        card.add(createField("Project Name", nameField));
        card.add(createField("Client", clientField));
        card.add(createField("Color Code", colorField));
        card.add(createField("Hourly Rate", rateField));
        card.add(createField("Budget", budgetField));
        card.add(createField("Status", statusBox));
        card.add(createField("Description", descScroll));

        background.add(card, BorderLayout.CENTER);
        return background;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new LineBorder(new Color(220, 220, 220)));

        JButton cancelBtn = new RoundedButton(
                "Cancel", new Color(230, 230, 230), Color.BLACK);

        JButton saveBtn = new RoundedButton(
                "Save Project", new Color(66, 133, 244), Color.WHITE);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "The project is Saved");
            dispose();
        });

        footer.add(cancelBtn);
        footer.add(saveBtn);
        return footer;
    }

    /* ================= HELPERS ================= */
    private JPanel createField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createInput() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setBorder(new LineBorder(new Color(210, 210, 210)));
        return field;
    }

    /* ================= ROUNDED BUTTON ================= */
    static class RoundedButton extends JButton {

        private final Color bg;
        private final int radius = 22;

        public RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            setForeground(fg);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 22, 8, 22));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), radius, radius));

            super.paintComponent(g);
            g2.dispose();
        }
    }
}
