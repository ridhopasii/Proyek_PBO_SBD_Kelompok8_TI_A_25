import java.sql.Connection;

public class TestKoneksi {
    public static void main(String[] args) {
        System.out.println("Mencoba koneksi ke database...");
        
        Connection conn = KoneksiDB.connect();
        
        if (conn != null) {
            System.out.println("Koneksi BERHASIL!");
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Koneksi GAGAL! Silakan cek kembali URL, user, dan password.");
        }
    }
}
