import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import service.UserService;
import model.User;

public class SignUpPage extends JFrame {

    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private UserService userService;

    public SignUpPage() {
        userService = new UserService();
        
        setTitle("Time Tracker - Sign Up");
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.45);
        int height = (int)(screenSize.height * 0.85);
        setSize(Math.max(550, width), Math.max(750, height));
        
        setMinimumSize(new Dimension(550, 750));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(30, 40, 30, 40));
        wrapper.add(createSignUpCard(), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createSignUpCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(40, 40, 40, 40)));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel(IconHelper.createClockIcon(48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Join Time Tracker today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        // Full Name
        fullNameField = new JTextField();
        JPanel fullNamePanel = createInputPanel("Full Name", fullNameField);

        // Username
        usernameField = new JTextField();
        JPanel usernamePanel = createInputPanel("Username", usernameField);

        // Email
        emailField = new JTextField();
        JPanel emailPanel = createInputPanel("Email", emailField);

        // Password
        passwordField = new JPasswordField();
        JPanel passwordPanel = createInputPanel("Password", passwordField);

        // Confirm Password
        confirmPasswordField = new JPasswordField();
        JPanel confirmPasswordPanel = createInputPanel("Confirm Password", confirmPasswordField);

        // Sign Up button
        JButton signUpButton = new JButton("Create Account");
        signUpButton.setBackground(new Color(59, 130, 246));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        signUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signUpButton.setBackground(new Color(37, 99, 235));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                signUpButton.setBackground(new Color(59, 130, 246));
            }
        });

        signUpButton.addActionListener(e -> handleSignUp());

        // Divider with OR
        JPanel dividerPanel = new JPanel();
        dividerPanel.setLayout(new BoxLayout(dividerPanel, BoxLayout.X_AXIS));
        dividerPanel.setOpaque(false);
        dividerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        dividerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator leftLine = new JSeparator(SwingConstants.HORIZONTAL);
        leftLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        leftLine.setForeground(new Color(209, 213, 219));
        
        JSeparator rightLine = new JSeparator(SwingConstants.HORIZONTAL);
        rightLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        rightLine.setForeground(new Color(209, 213, 219));
        
        JLabel orLabel = new JLabel("OR");
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        orLabel.setForeground(new Color(107, 114, 128));
        orLabel.setBorder(new EmptyBorder(0, 15, 0, 15));

        dividerPanel.add(leftLine);
        dividerPanel.add(orLabel);
        dividerPanel.add(rightLine);

        // Sign in link
        JPanel signinPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signinPanel.setOpaque(false);
        signinPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel haveAccount = new JLabel("Already have an account?");
        haveAccount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        haveAccount.setForeground(new Color(107, 114, 128));

        JLabel signIn = new JLabel("Sign in");
        signIn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signIn.setForeground(new Color(59, 130, 246));
        signIn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                signIn.setText("<html><u>Sign in</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                signIn.setText("Sign in");
            }
        });

        signinPanel.add(haveAccount);
        signinPanel.add(signIn);

        // Add all to card
        card.add(headerPanel);
        card.add(Box.createVerticalStrut(25));
        card.add(fullNamePanel);
        card.add(Box.createVerticalStrut(15));
        card.add(usernamePanel);
        card.add(Box.createVerticalStrut(15));
        card.add(emailPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(passwordPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(confirmPasswordPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(signUpButton);
        card.add(Box.createVerticalStrut(20));
        card.add(dividerPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(signinPanel);

        return card;
    }

        private JPanel createInputPanel(String labelText, JTextField inputField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
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
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (fullName.isEmpty()) {
            showError("Please enter your full name.");
            return;
        }

        if (username.isEmpty()) {
            showError("Please enter a username.");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }

        if (email.isEmpty()) {
            showError("Please enter your email address.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter a password.");
            return;
        }

        if (password.length() < 4) {
            showError("Password must be at least 4 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            User newUser = userService.register(username, password, email, fullName);
            
            if (newUser != null) {
                JOptionPane.showMessageDialog(this,
                        "Account created successfully!\nYou can now sign in with your credentials.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Navigate to login page
                dispose();
                SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
            } else {
                showError("Failed to create account. Please try again.");
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUpPage().setVisible(true));
    }
}
