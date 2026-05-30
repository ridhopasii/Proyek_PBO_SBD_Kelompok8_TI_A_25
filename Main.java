import java.awt.*;
import javax.swing.*;

// Login screen untuk aplikasi hotel
public class Main extends JFrame {
    private HotelService service = new HotelService();

    public Main() {
        setTitle("Sistem Informasi Hotel - Portal Login");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Setup UI theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore jika gagal, pake default aja
        }

        // Main panel
        JPanel mainWrapper = new JPanel(new BorderLayout(15, 15));
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainWrapper.setBackground(new Color(245, 246, 250));

        // Judul
        JLabel titleLabel = new JLabel("Login Portal Resepsionis", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Panel untuk input field
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        inputPanel.add(userLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passLabel);
        inputPanel.add(passwordField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton loginButton = new JButton("Login ke Aplikasi");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(180, 40));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Action listener
        loginButton.addActionListener(event -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            processLogin(username, password);
        });

        getRootPane().setDefaultButton(loginButton);
        buttonPanel.add(loginButton);

        // Add components
        mainWrapper.add(titleLabel, BorderLayout.NORTH);
        mainWrapper.add(inputPanel, BorderLayout.CENTER);
        mainWrapper.add(buttonPanel, BorderLayout.SOUTH);

        add(mainWrapper);
    }

    private void processLogin(String username, String password) {
        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Username dan password tidak boleh kosong!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Authenticate
        Karyawan loginUser = service.authenticateKaryawan(username, password);
        if (loginUser == null) {
            JOptionPane.showMessageDialog(
                this,
                "Login GAGAL! Username/password salah atau bukan resepsionis.",
                "Akses Ditolak",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Success - print user info
        System.out.println("[LOGIN SUCCESS]");
        loginUser.tampilkanProfil();

        // Buka main GUI
        SwingUtilities.invokeLater(() -> {
            this.dispose();
            HotelGUI mainApp = new HotelGUI();
            mainApp.setCurrentUser(loginUser);
            mainApp.setLocationRelativeTo(null);
            mainApp.setVisible(true);
        });
    }

    public static void main(String[] args) {
        // Launch UI di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Main loginFrame = new Main();
            loginFrame.setVisible(true);
        });
    }
}
