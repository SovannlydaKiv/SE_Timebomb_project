import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Time Tracker - Login");
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.45);
        int height = (int)(screenSize.height * 0.7);
        setSize(Math.max(550, width), Math.max(650, height));
        
        setMinimumSize(new Dimension(550, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(createLoginCard(), gbc);
        return wrapper;
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(24, 20, 24, 20)));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMinimumSize(new Dimension(360, 300));
        card.setPreferredSize(new Dimension(700, 640));
        card.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel(IconHelper.createClockIcon(48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Time Tracker");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        // Username
        usernameField = new JTextField();
        JPanel usernamePanel = createInputPanel("Username", usernameField);

        // Password
        passwordField = new JPasswordField();
        JPanel passwordPanel = createInputPanel("Password", passwordField);

        // Options
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        optionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JCheckBox rememberMe = new JCheckBox("Remember me");
        rememberMe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rememberMe.setOpaque(false);
        rememberMe.setFocusPainted(false);

        JLabel forgotPassword = new JLabel("Forgot password?");
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPassword.setForeground(new Color(59, 130, 246));
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));

        optionsPanel.add(rememberMe, BorderLayout.WEST);
        optionsPanel.add(forgotPassword, BorderLayout.EAST);

        // Login button
        JButton loginButton = new JButton("Sign In");
        loginButton.setBackground(new Color(59, 130, 246));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(260, 45));
        loginButton.setMaximumSize(new Dimension(320, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(37, 99, 235));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(59, 130, 246));
            }
        });

        loginButton.addActionListener(e -> handleLogin());

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

        // Sign up
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signupPanel.setOpaque(false);
        signupPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noAccount.setForeground(new Color(107, 114, 128));

        JLabel signUp = new JLabel("Sign up");
        signUp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signUp.setForeground(new Color(59, 130, 246));
        signUp.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> new SignUpPage().setVisible(true));
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                signUp.setText("<html><u>Sign up</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                signUp.setText("Sign up");
            }
        });

        signupPanel.add(noAccount);
        signupPanel.add(signUp);

        // Add all to card
        card.add(headerPanel);
        card.add(Box.createVerticalStrut(35));
        card.add(usernamePanel);
        card.add(Box.createVerticalStrut(20));
        card.add(passwordPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(optionsPanel);
        card.add(Box.createVerticalStrut(25));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(25));
        card.add(dividerPanel);
        card.add(Box.createVerticalStrut(25));
        card.add(signupPanel);

        return card;
    }

    private JPanel createInputPanel(String labelText, JTextField inputField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(660, 80));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        inputField.setBorder(new CompoundBorder(
            new LineBorder(new Color(209, 213, 219), 1, true),
            new EmptyBorder(12, 15, 12, 15)));
        inputField.setMaximumSize(new Dimension(640, 50));
        inputField.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                inputField.setBorder(new CompoundBorder(
                        new LineBorder(new Color(59, 130, 246), 2, true),
                        new EmptyBorder(11, 14, 11, 14)));
            }
            @Override
            public void focusLost(FocusEvent e) {
                inputField.setBorder(new CompoundBorder(
                        new LineBorder(new Color(209, 213, 219), 1, true),
                        new EmptyBorder(12, 15, 12, 15)));
            }
        });

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        panel.add(inputField);

        return panel;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open TrackerPage
        dispose();
        SwingUtilities.invokeLater(() -> new TrackerPage().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}