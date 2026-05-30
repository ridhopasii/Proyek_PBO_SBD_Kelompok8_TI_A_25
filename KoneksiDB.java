import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.swing.*;
import java.awt.*;

// Utility class untuk mengelola koneksi ke database PostgreSQL
// Konfigurasi disimpan di memori (static field) — tidak butuh file eksternal
public class KoneksiDB {

    // ── Konfigurasi default (ubah sesuai setup lokal) ──────────────────────
    private static String dbUrl  = "jdbc:postgresql://localhost:5432/hotel";
    private static String dbUser = "postgres";
    private static String dbPass = "12345";
    // ───────────────────────────────────────────────────────────────────────

    /** Connect ke database menggunakan konfigurasi saat ini. */
    public static Connection connect() {
        return connectInternal(true);
    }

    private static Connection connectInternal(boolean allowPrompt) {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(dbUrl, dbUser, dbPass);

        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] Driver PostgreSQL tidak ditemukan!");
            System.err.println("Pastikan file postgresql-42.7.11.jar ada di folder project.");
            return null;

        } catch (Exception sqlError) {
            if (!allowPrompt) return null;

            boolean isHeadless = GraphicsEnvironment.isHeadless();
            if (!isHeadless) {
                int choice = JOptionPane.showConfirmDialog(
                    null,
                    "[KONEKSI DATABASE GAGAL]\n\n" +
                    "Error: " + sqlError.getMessage() + "\n\n" +
                    "Apakah Anda ingin mengonfigurasi koneksi database PostgreSQL secara manual?",
                    "Koneksi Database Bermasalah",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION && showConfigDialog()) {
                    return connectInternal(false);
                }
            } else {
                System.err.println("\n[KONEKSI DATABASE GAGAL]");
                System.err.println("Error: " + sqlError.getMessage());
                System.out.print("Konfigurasi koneksi secara manual? (y/n): ");
                java.util.Scanner sc = new java.util.Scanner(System.in);
                String ans = sc.nextLine().trim().toLowerCase();
                if ((ans.equals("y") || ans.equals("yes")) && promptCliConfig(sc)) {
                    return connectInternal(false);
                }
            }
            return null;
        }
    }

    /**
     * Simpan konfigurasi ke static fields (in-memory, berlaku selama session).
     * Tidak menulis file apapun.
     */
    public static boolean saveConfig(String host, String port, String dbName,
                                     String user, String password) {
        dbUrl  = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
        dbUser = user;
        dbPass = password;
        return true;
    }

    /** Dialog GUI untuk mengubah konfigurasi koneksi. */
    public static boolean showConfigDialog() {
        // Parse URL saat ini untuk isi form
        String curHost = "localhost", curPort = "5432", curDb = "hotel";
        try {
            String clean = dbUrl.substring("jdbc:postgresql://".length());
            int slash = clean.indexOf('/');
            if (slash != -1) {
                curDb = clean.substring(slash + 1);
                String hp = clean.substring(0, slash);
                int colon = hp.indexOf(':');
                if (colon != -1) { curHost = hp.substring(0, colon); curPort = hp.substring(colon + 1); }
                else curHost = hp;
            }
        } catch (Exception ignored) {}

        JTextField hostField = new JTextField(curHost);
        JTextField portField = new JTextField(curPort);
        JTextField dbField   = new JTextField(curDb);
        JTextField userField = new JTextField(dbUser);
        JPasswordField passField = new JPasswordField(dbPass);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Host:"));         panel.add(hostField);
        panel.add(new JLabel("Port:"));         panel.add(portField);
        panel.add(new JLabel("Nama Database:")); panel.add(dbField);
        panel.add(new JLabel("Username:"));     panel.add(userField);
        panel.add(new JLabel("Password:"));     panel.add(passField);

        int result = JOptionPane.showConfirmDialog(
            null, panel, "Konfigurasi Database PostgreSQL",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return false;

        String host = hostField.getText().trim();
        String port = portField.getText().trim();
        String db   = dbField.getText().trim();
        String usr  = userField.getText().trim();
        String pwd  = new String(passField.getPassword());

        // Uji koneksi terlebih dahulu
        String testUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        try {
            Class.forName("org.postgresql.Driver");
            try (Connection conn = DriverManager.getConnection(testUrl, usr, pwd)) {
                saveConfig(host, port, db, usr, pwd);
                JOptionPane.showMessageDialog(null,
                    "Koneksi Berhasil & Konfigurasi Disimpan!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                "Koneksi GAGAL! Kesalahan: " + ex.getMessage(),
                "Uji Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
            int saveAnyway = JOptionPane.showConfirmDialog(null,
                "Ingin tetap menyimpan konfigurasi ini?",
                "Koneksi Gagal", JOptionPane.YES_NO_OPTION);
            if (saveAnyway == JOptionPane.YES_OPTION) {
                saveConfig(host, port, db, usr, pwd);
                return true;
            }
        }
        return false;
    }

    /** Konfigurasi via terminal (CLI mode). */
    public static boolean promptCliConfig(java.util.Scanner sc) {
        // Parse URL saat ini untuk nilai default
        String defHost = "localhost", defPort = "5432", defDb = "hotel";
        try {
            String clean = dbUrl.substring("jdbc:postgresql://".length());
            int slash = clean.indexOf('/');
            if (slash != -1) {
                defDb = clean.substring(slash + 1);
                String hp = clean.substring(0, slash);
                int colon = hp.indexOf(':');
                if (colon != -1) { defHost = hp.substring(0, colon); defPort = hp.substring(colon + 1); }
                else defHost = hp;
            }
        } catch (Exception ignored) {}

        System.out.println("\n--- Konfigurasi Database CLI ---");
        System.out.print("Host [" + defHost + "]: ");
        String host = sc.nextLine().trim(); if (host.isEmpty()) host = defHost;

        System.out.print("Port [" + defPort + "]: ");
        String port = sc.nextLine().trim(); if (port.isEmpty()) port = defPort;

        System.out.print("Nama Database [" + defDb + "]: ");
        String db = sc.nextLine().trim(); if (db.isEmpty()) db = defDb;

        System.out.print("Username [" + dbUser + "]: ");
        String usr = sc.nextLine().trim(); if (usr.isEmpty()) usr = dbUser;

        System.out.print("Password: ");
        String pwd = sc.nextLine().trim(); if (pwd.isEmpty()) pwd = dbPass;

        String testUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        System.out.println("Menguji koneksi...");
        try {
            Class.forName("org.postgresql.Driver");
            try (Connection conn = DriverManager.getConnection(testUrl, usr, pwd)) {
                saveConfig(host, port, db, usr, pwd);
                System.out.println("[SUKSES] Koneksi berhasil! Konfigurasi disimpan (session ini).");
                return true;
            }
        } catch (Exception ex) {
            System.err.println("[GAGAL] Koneksi gagal: " + ex.getMessage());
            System.out.print("Ingin tetap menyimpan konfigurasi ini? (y/n): ");
            String ans = sc.nextLine().trim().toLowerCase();
            if (ans.equals("y") || ans.equals("yes")) {
                saveConfig(host, port, db, usr, pwd);
                return true;
            }
        }
        return false;
    }

    // Safely close ResultSet, Statement, dan Connection (urutan benar: rs → stmt → conn)
    public static void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                java.sql.Statement stmt = rs.getStatement();
                java.sql.Connection conn = (stmt != null) ? stmt.getConnection() : null;
                try { rs.close(); }   catch (Exception ignored) {}
                if (stmt != null) try { stmt.close(); } catch (Exception ignored) {}
                if (conn != null) try { conn.close(); } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        }
    }
}
