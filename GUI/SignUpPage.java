import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import service.UserService;
import model.User;

public class SignUpPage extends JFrame {

    private UserService userService;

    private JTextField nameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public SignUpPage() {
        userService = new UserService();
        
        setTitle("Time Tracker - Sign Up");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.45);
        int height = (int)(screenSize.height * 0.7);
        setSize(Math.max(600, width), Math.max(700, height));

        setMinimumSize(new Dimension(600, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10)); // Small padding from edges
        wrapper.add(createCard(), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(24, 30, 24, 30))); // Increased horizontal padding inside card

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(IconHelper.createClockIcon(48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Create an account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Fill the form to create a new account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(12));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subtitleLabel);

        // Fields
        nameField = new JTextField();
        JPanel namePanel = createInputPanel("Full name", nameField);

        usernameField = new JTextField();
        JPanel userPanel = createInputPanel("Username", usernameField);

        emailField = new JTextField();
        JPanel emailPanel = createInputPanel("Email", emailField);

        passwordField = new JPasswordField();
        JPanel passPanel = createInputPanel("Password", passwordField);

        confirmPasswordField = new JPasswordField();
        JPanel confirmPanel = createInputPanel("Confirm password", confirmPasswordField);

        // Buttons
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        });

        JButton signupBtn = new JButton("Create account");
        signupBtn.setBackground(new Color(59, 130, 246));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFocusPainted(false);
        signupBtn.addActionListener(e -> handleSignUp());

        buttons.add(cancelBtn);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(signupBtn);

        // Layout
        card.add(headerPanel);
        card.add(Box.createVerticalStrut(18));
        card.add(namePanel);
        card.add(Box.createVerticalStrut(12));
        card.add(userPanel);
        card.add(Box.createVerticalStrut(12));
        card.add(emailPanel);
        card.add(Box.createVerticalStrut(12));
        card.add(passPanel);
        card.add(Box.createVerticalStrut(12));
        card.add(confirmPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(buttons);

        return card;
    }

    private JPanel createInputPanel(String labelText, JTextField inputField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        inputField.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                inputField.setBorder(new CompoundBorder(
                        new LineBorder(new Color(59, 130, 246), 2, true),
                        new EmptyBorder(9, 11, 9, 11)));
            }
            @Override
            public void focusLost(FocusEvent e) {
                inputField.setBorder(new CompoundBorder(
                        new LineBorder(new Color(209, 213, 219), 1, true),
                        new EmptyBorder(10, 12, 10, 12)));
            }
        });

        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(inputField);

        return panel;
    }

    private void handleSignUp() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.",
                    "Sign Up Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match.",
                    "Sign Up Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Register user using UserService (connects to database)
        try {
            User user = userService.register(username, password, email, name);
            if (user != null) {
                JOptionPane.showMessageDialog(this,
                        "Account created successfully. You can now sign in.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to create account. Please try again.",
                        "Sign Up Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Sign Up Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUpPage().setVisible(true));
    }
}

