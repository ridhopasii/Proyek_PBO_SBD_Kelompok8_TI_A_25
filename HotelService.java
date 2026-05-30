import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HotelService {

    public void tambahTamu(String nama, String tipeId, String noId, String noHp, String email, String alamat) {
        int id = tambahTamuReturnId(nama, tipeId, noId, noHp, email, alamat);
        if (id > 0) {
            System.out.println("[SUKSES] Tamu '" + nama + "' terdaftar dengan ID: " + id);
        }
    }

    public int tambahTamuReturnId(String nama, String tipeId, String noId, String noHp, String email, String alamat) {
        String sql = "INSERT INTO tamu (nama, identitas_tipe, identitas_no, no_hp, email, alamat) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = KoneksiDB.connect();
        if (conn == null) return -1;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nama);
            ps.setString(2, tipeId);
            ps.setString(3, noId);
            ps.setString(4, noHp);
            ps.setString(5, email);
            ps.setString(6, alamat);
            ps.executeUpdate();
            try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal tambah tamu: " + e.getMessage());
        }
        return -1;
    }

    public Karyawan authenticateKaryawan(String username, String password) {
        String sql = "SELECT k.id_karyawan, k.nama_karyawan, k.id_jabatan, j.nama_jabatan, " +
                     "k.username, k.password " +
                     "FROM karyawan k LEFT JOIN jabatan j ON k.id_jabatan = j.id_jabatan " +
                     "WHERE k.username = ? AND k.password = ? AND LOWER(j.nama_jabatan) = 'resepsionis'";
        Connection conn = KoneksiDB.connect();
        if (conn == null) return null;
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Karyawan(
                        rs.getInt("id_karyawan"),
                        rs.getString("nama_karyawan"),
                        rs.getInt("id_jabatan"),
                        rs.getString("nama_jabatan"),
                        rs.getString("username"),
                        rs.getString("password"),
                        "aktif"
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Login gagal: " + e.getMessage());
        }
        return null;
    }

    public void tambahKaryawan(String nama, int idJabatan, String username, String pass) {
        String sql = "INSERT INTO karyawan (nama_karyawan, id_jabatan, username, password) VALUES (?, ?, ?, ?)";
        Connection conn = KoneksiDB.connect();
        if (conn == null) { System.out.println("[ERROR] Koneksi database gagal!"); return; }
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setInt(2, idJabatan);
            ps.setString(3, username);
            ps.setString(4, pass);
            ps.executeUpdate();
            System.out.println("[SUKSES] Karyawan " + nama + " ditambahkan.");
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal tambah karyawan: " + e.getMessage());
        }
    }

    public void tambahSupplier(String nama, String kontak, String noHp, String email, String alamat) {
        String sql = "INSERT INTO supplier (nama_supplier, kontak_person, no_hp, email, alamat) VALUES (?, ?, ?, ?, ?)";
        Connection conn = KoneksiDB.connect();
        if (conn == null) { System.out.println("[ERROR] Koneksi database gagal!"); return; }
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, kontak);
            ps.setString(3, noHp);
            ps.setString(4, email);
            ps.setString(5, alamat);
            ps.executeUpdate();
            System.out.println("[SUKSES] Supplier " + nama + " ditambahkan.");
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal tambah supplier: " + e.getMessage());
        }
    }


    public void tambahKamar(int idTipe, String nomorKamar, int lantai) {
        String sql = "INSERT INTO kamar (id_tipe, nomor_kamar, lantai, status) VALUES (?, ?, ?, 'Tersedia')";
        Connection conn = KoneksiDB.connect();
        if (conn == null) { System.out.println("[ERROR] Koneksi database gagal!"); return; }
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTipe);
            ps.setString(2, nomorKamar);
            ps.setInt(3, lantai);
            ps.executeUpdate();
            System.out.println("[SUKSES] Kamar " + nomorKamar + " berhasil ditambahkan.");
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal tambah kamar: " + e.getMessage());
        }
    }

    
    public int hitungTotalKamar(int idKamar, String checkin, String checkout) throws Exception {
        try (Connection conn = KoneksiDB.connect()) {
            if (conn == null) throw new Exception("Gagal koneksi database");
            
            int hargaPerMalam = 0;
            String sqlHarga = "SELECT tk.harga_per_malam FROM kamar k JOIN tipe_kamar tk ON k.id_tipe = tk.id_tipe WHERE k.id_kamar = ?";
            try (PreparedStatement psHarga = conn.prepareStatement(sqlHarga)) {
                psHarga.setInt(1, idKamar);
                try (ResultSet rsHarga = psHarga.executeQuery()) {
                    if (rsHarga.next()) {
                        hargaPerMalam = rsHarga.getInt("harga_per_malam");
                    } else {
                        throw new Exception("Kamar tidak ditemukan.");
                    }
                }
            }
            java.time.LocalDate d1 = java.time.LocalDate.parse(checkin);
            java.time.LocalDate d2 = java.time.LocalDate.parse(checkout);
            long days = java.time.temporal.ChronoUnit.DAYS.between(d1, d2);
            if (days <= 0) days = 1; // Minimal 1 malam
            
            return (int) days * hargaPerMalam;
        }
    }

    public String buatReservasiBerbayar(Reservasi dataReservasi, String metodeBayar, int totalKamar) throws Exception {
        String sql = "INSERT INTO reservasi (id_tamu, id_kamar, id_karyawan, tanggal_checkin, tanggal_checkout) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = KoneksiDB.connect()) {
            if (conn == null) throw new Exception("Gagal koneksi database");

            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, dataReservasi.getTamu().getIdTamu());
                ps.setInt(2, dataReservasi.getKamar().getIdKamar());
                ps.setInt(3, dataReservasi.getKaryawan().getIdKaryawan());
                ps.setDate(4, dataReservasi.getTanggalCheckin());
                ps.setDate(5, dataReservasi.getTanggalCheckout());
                ps.executeUpdate();
                
                int idReservasi = -1;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idReservasi = rs.getInt(1);
                    }
                }
                
                if (idReservasi == -1) {
                    throw new Exception("Gagal mendapatkan ID Reservasi yang baru dibuat.");
                }
                
                String sqlInvoice = "INSERT INTO invoice (no_invoice, id_reservasi, total_kamar, total_restoran, total_layanan, total_tagihan, note) VALUES (?, ?, ?, 0, 0, ?, 'Generated at Check-in')";
                try (PreparedStatement psInv = conn.prepareStatement(sqlInvoice)) {
                    psInv.setString(1, "INV-" + (System.currentTimeMillis() / 1000));
                    psInv.setInt(2, idReservasi);
                    psInv.setInt(3, totalKamar);
                    psInv.setInt(4, totalKamar); // grand total awal
                    psInv.executeUpdate();
                }

                String sqlBayar = "INSERT INTO pembayaran (id_reservasi, jumlah_bayar, metode_bayar, status_pembayaran) VALUES (?, ?, ?, 'Lunas')";
                try (PreparedStatement psBayar = conn.prepareStatement(sqlBayar)) {
                    psBayar.setInt(1, idReservasi);
                    psBayar.setInt(2, totalKamar);
                    psBayar.setString(3, metodeBayar);
                    psBayar.executeUpdate();
                }
                
                // Return Invoice Sementara
                String invoiceText = cetakInvoice(conn, idReservasi, false);
                
                conn.commit();
                System.out.println("[SUKSES] Reservasi berbayar tercatat.");
                return invoiceText;
            } catch (Exception e) {
                conn.rollback();
                throw new Exception("Gagal membuat reservasi: " + e.getMessage(), e);
            }
        }
    }

    public void buatReservasi(Reservasi dataReservasi) throws Exception {
        String sql = "INSERT INTO reservasi (id_tamu, id_kamar, id_karyawan, tanggal_checkin, tanggal_checkout) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = KoneksiDB.connect()) {
            if (conn == null) throw new Exception("Gagal koneksi database");
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, dataReservasi.getTamu().getIdTamu());
                ps.setInt(2, dataReservasi.getKamar().getIdKamar());
                ps.setInt(3, dataReservasi.getKaryawan().getIdKaryawan());
                ps.setDate(4, dataReservasi.getTanggalCheckin());
                ps.setDate(5, dataReservasi.getTanggalCheckout());
                ps.executeUpdate();
                
                int idReservasi = -1;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idReservasi = rs.getInt(1);
                    }
                }
                
                if (idReservasi == -1) {
                    throw new Exception("Gagal mendapatkan ID Reservasi yang baru dibuat.");
                }
                
                // Ambil harga per malam kamar
                int hargaPerMalam = 0;
                String sqlHarga = "SELECT tk.harga_per_malam FROM kamar k JOIN tipe_kamar tk ON k.id_tipe = tk.id_tipe WHERE k.id_kamar = ?";
                try (PreparedStatement psHarga = conn.prepareStatement(sqlHarga)) {
                    psHarga.setInt(1, dataReservasi.getKamar().getIdKamar());
                    try (ResultSet rsHarga = psHarga.executeQuery()) {
                        if (rsHarga.next()) {
                            hargaPerMalam = rsHarga.getInt("harga_per_malam");
                        }
                    }
                }
                
                // Hitung durasi inap (jumlah malam)
                java.time.LocalDate d1 = dataReservasi.getTanggalCheckin().toLocalDate();
                java.time.LocalDate d2 = dataReservasi.getTanggalCheckout().toLocalDate();
                long days = java.time.temporal.ChronoUnit.DAYS.between(d1, d2);
                if (days <= 0) days = 1; // Minimal 1 malam
                
                int totalKamar = (int) days * hargaPerMalam;
                
                // Masukkan ke tabel invoice langsung pada saat check-in
                String sqlInvoice = "INSERT INTO invoice (no_invoice, id_reservasi, total_kamar, total_restoran, total_layanan, total_tagihan, note) VALUES (?, ?, ?, 0, 0, ?, 'Generated at Check-in')";
                try (PreparedStatement psInv = conn.prepareStatement(sqlInvoice)) {
                    psInv.setString(1, "INV-" + (System.currentTimeMillis() / 1000));
                    psInv.setInt(2, idReservasi);
                    psInv.setInt(3, totalKamar);
                    psInv.setInt(4, totalKamar); // grand total awal
                    psInv.executeUpdate();
                }
                
                conn.commit();
                System.out.println("[SUKSES] Reservasi tercatat dan Invoice Awal otomatis masuk ke Histori.");
            } catch (Exception e) {
                conn.rollback();
                throw new Exception("Gagal membuat reservasi: " + e.getMessage(), e);
            }
        }
    }

    
    public void pesanRestoran(int idReservasi, int idMeja, int idMenu, int jumlah) throws Exception {
        String sqlHargaMenu = "SELECT harga FROM menu_restoran WHERE id_menu = ?";
        String sqlPesanan = "INSERT INTO pesanan_restoran (id_reservasi, id_meja, total_harga) VALUES (?, ?, ?) RETURNING id_pesanan";
        String sqlDetail = "INSERT INTO detail_pesanan_restoran (id_pesanan, id_menu, jumlah, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = KoneksiDB.connect()) {
            if (conn == null) throw new Exception("Gagal koneksi database");
            conn.setAutoCommit(false);
            try (PreparedStatement psHarga = conn.prepareStatement(sqlHargaMenu)) {
                psHarga.setInt(1, idMenu);
                int hargaMenu;
                try (ResultSet rsHarga = psHarga.executeQuery()) {
                    if (!rsHarga.next()) {
                        throw new Exception("Menu restoran tidak ditemukan: " + idMenu);
                    }
                    hargaMenu = rsHarga.getInt("harga");
                }

                int subtotal = hargaMenu * jumlah;
                try (PreparedStatement psP = conn.prepareStatement(sqlPesanan)) {
                    psP.setInt(1, idReservasi); psP.setInt(2, idMeja); psP.setInt(3, subtotal);
                    try (ResultSet rs = psP.executeQuery()) {
                        if (rs.next()) {
                            int idPesanan = rs.getInt(1);
                            try (PreparedStatement psD = conn.prepareStatement(sqlDetail)) {
                                psD.setInt(1, idPesanan); psD.setInt(2, idMenu); psD.setInt(3, jumlah); psD.setInt(4, subtotal);
                                psD.executeUpdate();
                            }
                        } else {
                            throw new Exception("Gagal membuat pesanan restoran.");
                        }
                    }
                }
                conn.commit();
                System.out.println("[SUKSES] Pesanan restoran dicatat.");
            } catch (Exception e) { conn.rollback(); throw new Exception("Gagal mencatat pesanan restoran: " + e.getMessage(), e); }
        }
    }

    
    public void tambahPengadaan(int idBarang, int jumlah) throws Exception {
        tambahPengadaan(idBarang, jumlah, 1, 1);
    }

    public void tambahPengadaan(int idBarang, int jumlah, int idKaryawan) throws Exception {
        tambahPengadaan(idBarang, jumlah, 1, idKaryawan);
    }

    public void tambahPengadaan(int idBarang, int jumlah, int idSupplier, int idKaryawan) throws Exception {
        tambahPengadaan(idBarang, jumlah, idSupplier, idKaryawan, 0);
    }

    public void tambahPengadaan(int idBarang, int jumlah, int idSupplier, int idKaryawan, int hargaSatuan) throws Exception {
        String sqlPO = "INSERT INTO purchase_order (id_supplier, id_karyawan) VALUES (?, ?) RETURNING id_po";
        String sqlDetail = "INSERT INTO detail_po (id_po, id_barang, jumlah, harga_satuan) VALUES (?, ?, ?, ?)";
        String updateStok = "UPDATE logistik.inventaris SET stok_sekarang = stok_sekarang + ? WHERE id_barang = ?";
        Connection conn = KoneksiDB.connect();
        if (conn == null) throw new Exception("Gagal koneksi database");
        try (conn) {
            conn.setAutoCommit(false);
            try (PreparedStatement psPO = conn.prepareStatement(sqlPO);
                 PreparedStatement psUpdate = conn.prepareStatement(updateStok)) {
                psPO.setInt(1, idSupplier);
                psPO.setInt(2, idKaryawan);
                ResultSet rs = psPO.executeQuery();
                if (rs.next()) {
                    int idPO = rs.getInt(1);
                    try (PreparedStatement psD = conn.prepareStatement(sqlDetail)) {
                        psD.setInt(1, idPO); psD.setInt(2, idBarang); psD.setInt(3, jumlah); psD.setInt(4, hargaSatuan);
                        psD.executeUpdate();
                    }
                }
                psUpdate.setInt(1, jumlah); psUpdate.setInt(2, idBarang);
                psUpdate.executeUpdate();
                conn.commit();
                System.out.println("[SUKSES] Stok barang berhasil diperbarui.");
            } catch (Exception e) { conn.rollback(); throw new Exception("Gagal tambah pengadaan: " + e.getMessage(), e); }
        }
    }

    
    public void pesanLayanan(int idReservasi, int idLayanan, int jumlah) throws Exception {
        String sql = "INSERT INTO detail_layanan_reservasi (id_reservasi, id_layanan, jumlah) VALUES (?, ?, ?)";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReservasi); ps.setInt(2, idLayanan); ps.setInt(3, jumlah);
            ps.executeUpdate();
            System.out.println("[SUKSES] Layanan dipesan.");
        } catch (Exception e) {
            throw new Exception("Gagal memesan layanan: " + e.getMessage(), e);
        }
    }

    public void tambahLayanan(String namaLayanan, int harga) {
        String sql = "INSERT INTO layanan_hotel (nama_layanan, harga) VALUES (?, ?)";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaLayanan); ps.setInt(2, harga);
            ps.executeUpdate();
            System.out.println("[SUKSES] Layanan hotel baru ditambahkan.");
        } catch (Exception e) { System.out.println("[ERROR] " + e.getMessage()); }
    }

    public void tambahFasilitas(String nama, java.sql.Time buka, java.sql.Time tutup) {
        String sql = "INSERT INTO fasilitas_umum (nama_fasilitas, jam_buka, jam_tutup) VALUES (?, ?, ?)";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama); ps.setTime(2, buka); ps.setTime(3, tutup);
            ps.executeUpdate();
            System.out.println("[SUKSES] Fasilitas ditambahkan.");
        } catch (Exception e) { System.out.println("[ERROR] " + e.getMessage()); }
    }

    public void tambahMenuRestoran(int idKategori, String namaMenu, int harga) {
        String sql = "INSERT INTO menu_restoran (id_kategori, nama_menu, harga) VALUES (?, ?, ?)";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKategori); ps.setString(2, namaMenu); ps.setInt(3, harga);
            ps.executeUpdate();
            System.out.println("[SUKSES] Menu restoran ditambahkan.");
        } catch (Exception e) { System.out.println("[ERROR] " + e.getMessage()); }
    }

    public void updateMenuRestoran(int idMenu, int idKategori, String namaMenu, int harga) throws Exception {
        String sql = "UPDATE menu_restoran SET id_kategori = ?, nama_menu = ?, harga = ? WHERE id_menu = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKategori); ps.setString(2, namaMenu); ps.setInt(3, harga); ps.setInt(4, idMenu);
            ps.executeUpdate();
            System.out.println("[SUKSES] Menu restoran diperbarui.");
        } catch (Exception e) {
            throw new Exception("Gagal memperbarui menu: data tidak valid.", e);
        }
    }

    public void hapusMenuRestoran(int idMenu) throws Exception {
        String sql = "DELETE FROM menu_restoran WHERE id_menu = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMenu);
            ps.executeUpdate();
            System.out.println("[SUKSES] Menu restoran dihapus.");
        } catch (Exception e) {
            throw new Exception("Gagal menghapus menu: menu ini sudah pernah dipesan di transaksi restoran.", e);
        }
    }

    public java.util.Vector<String> getDropdownKategoriMenu() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT id_kategori, nama_kategori FROM kategori_menu ORDER BY id_kategori"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_kategori") + " - " + rs.getString("nama_kategori"));
        } catch (Exception e) {}
        return v;
    }

    public void gunakanFasilitas(int idReservasi, int idFasilitas) {
        String sql = "INSERT INTO akses_fasilitas (id_reservasi, id_fasilitas) VALUES (?, ?)";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReservasi); ps.setInt(2, idFasilitas);
            ps.executeUpdate();
            System.out.println("[SUKSES] Akses fasilitas dicatat.");
        } catch (Exception e) { System.out.println("[ERROR] " + e.getMessage()); }
    }

    public void lihatKamarLengkap() {
        String sql = "SELECT k.id_kamar, k.nomor_kamar, t.nama_tipe, t.harga_per_malam, k.lantai, k.status " +
                     "FROM kamar k JOIN tipe_kamar t ON k.id_tipe = t.id_tipe ORDER BY k.id_kamar ASC";
        try (Connection conn = KoneksiDB.connect(); 
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            System.out.println("\n=== STATUS KAMAR ===");
            while (rs.next()) {
                System.out.printf("ID: %-3d | No: %-4s | Tipe: %-20s | Harga: %-10d | Lantai: %-2d | Status: %s\n",
                    rs.getInt("id_kamar"), rs.getString("nomor_kamar"), rs.getString("nama_tipe"), 
                    rs.getInt("harga_per_malam"), rs.getInt("lantai"), rs.getString("status"));
            }
        } catch (Exception e) { System.out.println("[ERROR] " + e.getMessage()); }
    }

    public String prosesCheckOut(int idReservasi) throws Exception {
        try (Connection conn = KoneksiDB.connect()) {
            if (conn == null) throw new Exception("Gagal koneksi database");
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE reservasi SET status_reservasi = 'Selesai' WHERE id_reservasi = ?")) {
                    ps.setInt(1, idReservasi);
                    int updated = ps.executeUpdate();
                    if (updated == 0) throw new Exception("Reservasi tidak ditemukan: " + idReservasi);
                }

                String invoiceText = cetakInvoice(conn, idReservasi, true);
                conn.commit();
                System.out.println("[SUKSES] Check-out berhasil.");
                return invoiceText;
            } catch (Exception e) {
                conn.rollback();
                throw new Exception("Gagal proses check-out: " + e.getMessage(), e);
            }
        }
    }

    public String cetakInvoice(int idReservasi) throws Exception {
        try (Connection conn = KoneksiDB.connect()) {
            if (conn == null) throw new Exception("Gagal koneksi database");
            return cetakInvoice(conn, idReservasi, false);
        }
    }

    private String cetakInvoice(Connection conn, int idReservasi, boolean simpanInvoice) throws Exception {
        StringBuilder invoiceText = new StringBuilder();
        String sql =
            "SELECT t.nama, k.nomor_kamar, tk.nama_tipe, tk.harga_per_malam, " +
            "r.tanggal_checkin, r.tanggal_checkout, " +
            "(r.tanggal_checkout - r.tanggal_checkin) as durasi, " +
            "COALESCE((SELECT SUM(subtotal) FROM detail_pesanan_restoran d " +
            "  JOIN pesanan_restoran p ON d.id_pesanan = p.id_pesanan WHERE p.id_reservasi = ?), 0) as total_resto, " +
            "COALESCE((SELECT SUM(dl.jumlah * lh.harga) FROM detail_layanan_reservasi dl " +
            "  JOIN layanan_hotel lh ON dl.id_layanan = lh.id_layanan WHERE dl.id_reservasi = ?), 0) as total_layanan, " +
            "(SELECT STRING_AGG(" +
            "  m.nama_menu || '|' || d.jumlah || '|' || d.subtotal || '|' || " +
            "  COALESCE(to_char(p.waktu_pesan,'HH24:MI'),'') || '|' || " +
            "  GREATEST(1, DATE_PART('day', p.waktu_pesan - r.tanggal_checkin::timestamp) + 1)," +
            "  ';;' ORDER BY p.waktu_pesan) " +
            " FROM detail_pesanan_restoran d " +
            " JOIN pesanan_restoran p ON d.id_pesanan = p.id_pesanan " +
            " JOIN menu_restoran m ON d.id_menu = m.id_menu " +
            " WHERE p.id_reservasi = ?) AS resto_data, " +
            "(SELECT STRING_AGG(" +
            "  lh.nama_layanan || '|' || dl.jumlah || '|' || (dl.jumlah * lh.harga) || '|' || " +
            "  COALESCE(to_char(dl.waktu_pesan,'HH24:MI'),'') || '|' || " +
            "  GREATEST(1, DATE_PART('day', dl.waktu_pesan - r.tanggal_checkin::timestamp) + 1)," +
            "  ';;' ORDER BY dl.waktu_pesan) " +
            " FROM detail_layanan_reservasi dl " +
            " JOIN layanan_hotel lh ON dl.id_layanan = lh.id_layanan " +
            " WHERE dl.id_reservasi = ?) AS layanan_data, " +
            "(SELECT STRING_AGG(" +
            "  f.nama_fasilitas || '|' || COALESCE(to_char(a.waktu_masuk,'HH24:MI'),'') || '|' || " +
            "  GREATEST(1, DATE_PART('day', a.waktu_masuk - r.tanggal_checkin::timestamp) + 1)," +
            "  ';;' ORDER BY a.waktu_masuk) " +
            " FROM akses_fasilitas a " +
            " JOIN fasilitas_umum f ON a.id_fasilitas = f.id_fasilitas " +
            " WHERE a.id_reservasi = ?) AS fasilitas_data " +
            "FROM reservasi r " +
            "JOIN tamu t ON r.id_tamu = t.id_tamu " +
            "JOIN kamar k ON r.id_kamar = k.id_kamar " +
            "JOIN tipe_kamar tk ON k.id_tipe = tk.id_tipe " +
            "WHERE r.id_reservasi = ?";


        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Parameter: ?1=total_resto, ?2=total_layanan, ?3=resto_data, ?4=layanan_data, ?5=fasilitas_data, ?6=WHERE
            ps.setInt(1, idReservasi); // total_resto subquery
            ps.setInt(2, idReservasi); // total_layanan subquery
            ps.setInt(3, idReservasi); // resto_data STRING_AGG
            ps.setInt(4, idReservasi); // layanan_data STRING_AGG
            ps.setInt(5, idReservasi); // fasilitas_data STRING_AGG
            ps.setInt(6, idReservasi); // WHERE r.id_reservasi = ?
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("Reservasi tidak ditemukan: " + idReservasi);
                }

                int durasi = rs.getInt("durasi") == 0 ? 1 : rs.getInt("durasi");
                int totalKamar = durasi * rs.getInt("harga_per_malam");
                int totalResto = rs.getInt("total_resto");
                int totalLayanan = rs.getInt("total_layanan");
                int grandTotal = totalKamar + totalResto + totalLayanan;

                invoiceText.append("===========================================\n");
                invoiceText.append("               Kwitansi Hotel              \n");
                invoiceText.append("===========================================\n");
                invoiceText.append(String.format("Nama Tamu       : %s\n", rs.getString("nama")));
                invoiceText.append(String.format("No. Kamar       : %s (%s)\n", rs.getString("nomor_kamar"), rs.getString("nama_tipe")));
                invoiceText.append(String.format("Durasi Inap     : %d Malam\n", durasi));
                invoiceText.append(String.format("Waktu Cek-in    : %s 13:00\n", rs.getString("tanggal_checkin")));
                invoiceText.append(String.format("Waktu Cek-out   : %s 13:00\n", rs.getString("tanggal_checkout")));
                invoiceText.append("-------------------------------------------\n");
                invoiceText.append(String.format("Biaya Kamar     : Rp %,12d (Sudah Lunas)\n", totalKamar));

                invoiceText.append(String.format("Biaya Restoran  : Rp %,12d\n", totalResto));
                if (totalResto > 0) {
                    String restoData = rs.getString("resto_data");
                    if (restoData != null && !restoData.isEmpty()) {
                        String[] items = restoData.split(";;");
                        for (String item : items) {
                            String[] parts = item.split("\\|");
                            if (parts.length >= 5) {
                                String namaMenu = parts[0];
                                if (namaMenu.length() > 15) namaMenu = namaMenu.substring(0, 15);
                                int jumlah = Integer.parseInt(parts[1]);
                                int subtotal = (int) Double.parseDouble(parts[2]);
                                String waktu = parts[3];
                                int hariKe = (int) Double.parseDouble(parts[4]);
                                invoiceText.append(String.format("   ~ %2dx %-15s [Hari %d, %s] : Rp %,9d\n", jumlah, namaMenu, hariKe, waktu, subtotal));
                            }
                        }
                    }
                }

                invoiceText.append(String.format("Biaya Layanan   : Rp %,12d\n", totalLayanan));
                if (totalLayanan > 0) {
                    String layananData = rs.getString("layanan_data");
                    if (layananData != null && !layananData.isEmpty()) {
                        String[] items = layananData.split(";;");
                        for (String item : items) {
                            String[] parts = item.split("\\|");
                            if (parts.length >= 5) {
                                String namaLayanan = parts[0];
                                if (namaLayanan.length() > 15) namaLayanan = namaLayanan.substring(0, 15);
                                int jumlah = Integer.parseInt(parts[1]);
                                int subtotal = (int) Double.parseDouble(parts[2]);
                                String waktu = parts[3];
                                int hariKe = (int) Double.parseDouble(parts[4]);
                                invoiceText.append(String.format("   ~ %2dx %-15s [Hari %d, %s] : Rp %,9d\n", jumlah, namaLayanan, hariKe, waktu, subtotal));
                            }
                        }
                    }
                }

                String fasilitasData = rs.getString("fasilitas_data");
                if (fasilitasData != null && !fasilitasData.isEmpty()) {
                    invoiceText.append("Fasilitas Gratis Diakses:\n");
                    String[] items = fasilitasData.split(";;");
                    for (String item : items) {
                        String[] parts = item.split("\\|");
                        if (parts.length >= 3) {
                            String namaFasilitas = parts[0];
                            String waktu = parts[1];
                            int hariKe = (int) Double.parseDouble(parts[2]);
                            invoiceText.append(String.format("   ~ %-20s [Hari %d, %s]\n", namaFasilitas, hariKe, waktu));
                        }
                    }
                }

                invoiceText.append("-------------------------------------------\n");
                invoiceText.append(String.format("GRAND TOTAL REKAP : Rp %,12d\n", grandTotal));
                invoiceText.append(String.format("SISA TAGIHAN BLUM LUNAS (Layanan+Resto) : Rp %,12d\n", totalResto + totalLayanan));
                invoiceText.append("===========================================\n");

                if (simpanInvoice) {
                    // Coba update invoice terlebih dahulu
                    String updateInvoice = "UPDATE invoice SET total_kamar = ?, total_restoran = ?, total_layanan = ?, total_tagihan = ?, note = 'Finalized at Check-out' WHERE id_reservasi = ?";
                    int updatedRows = 0;
                    try (PreparedStatement psInv = conn.prepareStatement(updateInvoice)) {
                        psInv.setInt(1, totalKamar);
                        psInv.setInt(2, totalResto);
                        psInv.setInt(3, totalLayanan);
                        psInv.setInt(4, grandTotal);
                        psInv.setInt(5, idReservasi);
                        updatedRows = psInv.executeUpdate();
                    }
                    
                    // Jika data tagihan tidak ada (misal data legacy), lakukan insert baru
                    if (updatedRows == 0) {
                        String saveInvoice = "INSERT INTO invoice (no_invoice, id_reservasi, total_kamar, total_restoran, total_layanan, total_tagihan, note) VALUES (?, ?, ?, ?, ?, ?, 'Legacy Invoice')";
                        try (PreparedStatement psInv = conn.prepareStatement(saveInvoice)) {
                            psInv.setString(1, "INV-" + System.currentTimeMillis() / 1000);
                            psInv.setInt(2, idReservasi);
                            psInv.setInt(3, totalKamar);
                            psInv.setInt(4, totalResto);
                            psInv.setInt(5, totalLayanan);
                            psInv.setInt(6, grandTotal);
                            psInv.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Gagal mencetak invoice: " + e.getMessage(), e);
        }
        return invoiceText.toString();
    }

    
    public ResultSet getTabelKamar() throws Exception {
        String sql = "SELECT k.id_kamar, k.nomor_kamar, tk.nama_tipe, tk.harga_per_malam, k.lantai, k.status, COALESCE(t.nama, '-') as pengisi " +
                     "FROM kamar k JOIN tipe_kamar tk ON k.id_tipe = tk.id_tipe " +
                     "LEFT JOIN reservasi r ON k.id_kamar = r.id_kamar AND LOWER(r.status_reservasi) = 'check-in' " +
                     "LEFT JOIN tamu t ON r.id_tamu = t.id_tamu " +
                     "ORDER BY k.nomor_kamar ASC";
        Connection conn = KoneksiDB.connect();
        return conn.createStatement().executeQuery(sql);
    }

    public Kamar getDetailKamarObj(int idKamar) {
        String sql = "SELECT k.id_kamar, k.nomor_kamar, k.status, t.id_tipe, t.nama_tipe, t.harga_per_malam, t.kapasitas_orang " +
                     "FROM kamar k JOIN tipe_kamar t ON k.id_tipe = t.id_tipe WHERE k.id_kamar = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKamar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipeKamar tk = new TipeKamar(
                        rs.getInt("id_tipe"),
                        rs.getString("nama_tipe"),
                        rs.getInt("kapasitas_orang"),
                        rs.getInt("harga_per_malam")
                    );
                    Kamar k = new Kamar(
                        rs.getInt("id_kamar"),
                        tk,
                        rs.getString("nomor_kamar"),
                        rs.getString("status")
                    );
                    return k;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public ResultSet getTamu() throws Exception {
        return KoneksiDB.connect().createStatement().executeQuery("SELECT id_tamu, nama, identitas_tipe, identitas_no, no_hp, alamat FROM tamu");
    }

    public ResultSet getReservasiAktif() throws Exception {
        String sql = "SELECT r.id_reservasi, t.nama, k.nomor_kamar, r.tanggal_checkin, r.status_reservasi " +
                         "FROM reservasi r JOIN tamu t ON r.id_tamu = t.id_tamu JOIN kamar k ON r.id_kamar = k.id_kamar " +
                         "WHERE LOWER(r.status_reservasi) = 'check-in'";
        Connection conn = KoneksiDB.connect();
        return conn.createStatement().executeQuery(sql);
    }

    public void hapusKamar(int idKamar) throws Exception {
        String sql = "DELETE FROM kamar WHERE id_kamar = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKamar);
            ps.executeUpdate();
            System.out.println("[SUKSES] Kamar dihapus.");
        } catch (Exception e) { 
            System.out.println("[ERROR] " + e.getMessage());
            throw new Exception("Gagal menghapus kamar: Kamar ini sedang aktif digunakan dalam reservasi atau memiliki riwayat transaksi.", e);
        }
    }

    public void hapusTamu(int idTamu) throws Exception {
        String sql = "DELETE FROM tamu WHERE id_tamu = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTamu);
            ps.executeUpdate();
            System.out.println("[SUKSES] Data tamu dihapus.");
        } catch (Exception e) { 
            System.out.println("[ERROR] " + e.getMessage());
            throw new Exception("Gagal menghapus tamu: Tamu ini tidak bisa dihapus karena memiliki data reservasi, tagihan, atau histori transaksi aktif di sistem hotel.", e);
        }
    }

    public void updateTamu(int idTamu, String nama, String tipeId, String noId, String noHp, String email, String alamat) throws Exception {
        String sql = "UPDATE tamu SET nama = ?, identitas_tipe = ?, identitas_no = ?, no_hp = ?, email = ?, alamat = ? WHERE id_tamu = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama); ps.setString(2, tipeId); ps.setString(3, noId);
            ps.setString(4, noHp); ps.setString(5, email); ps.setString(6, alamat);
            ps.setInt(7, idTamu);
            ps.executeUpdate();
            System.out.println("[SUKSES] Data tamu diperbarui.");
        } catch (Exception e) { 
            System.out.println("[ERROR] " + e.getMessage());
            throw new Exception("Gagal memperbarui data tamu: Nomor identitas (KTP/Passport) '" + noId + "' sudah terdaftar pada tamu lain (Harus unik) atau data tidak valid.", e);
        }
    }

    public void updateKamar(int idKamar, int idTipe, String nomorKamar, int lantai, String status) throws Exception {
        String sql = "UPDATE kamar SET id_tipe = ?, nomor_kamar = ?, lantai = ?, status = ? WHERE id_kamar = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTipe); ps.setString(2, nomorKamar); ps.setInt(3, lantai); ps.setString(4, status); ps.setInt(5, idKamar);
            ps.executeUpdate();
            System.out.println("[SUKSES] Kamar diperbarui.");
        } catch (Exception e) { 
            System.out.println("[ERROR] " + e.getMessage());
            throw new Exception("Gagal memperbarui data kamar: Nomor kamar '" + nomorKamar + "' sudah digunakan atau data tidak valid.", e);
        }
    }

    
    public ResultSet getMenuRestoran() throws Exception {
        return KoneksiDB.connect().createStatement().executeQuery("SELECT m.id_menu, m.nama_menu, k.nama_kategori, m.harga FROM menu_restoran m JOIN kategori_menu k ON m.id_kategori = k.id_kategori");
    }

    public ResultSet getLayananHotel() throws Exception {
        return KoneksiDB.connect().createStatement().executeQuery("SELECT * FROM layanan_hotel");
    }

    public ResultSet getFasilitasUmum() throws Exception {
        return KoneksiDB.connect().createStatement().executeQuery("SELECT * FROM fasilitas_umum");
    }

    public ResultSet getInventaris() throws Exception {
        return KoneksiDB.connect().createStatement().executeQuery("SELECT * FROM logistik.inventaris");
    }

    public ResultSet cariTamu(String keyword) throws Exception {
        String sql = "SELECT * FROM tamu WHERE nama ILIKE ? OR email ILIKE ?";
        Connection conn = KoneksiDB.connect();
        if (conn == null) throw new Exception("Gagal koneksi database");
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");
        return ps.executeQuery();
    }

    public ResultSet getTagihan() throws Exception {
        return KoneksiDB.connect().createStatement().executeQuery(
            "SELECT i.id_invoice, i.no_invoice, i.id_reservasi, t.nama, i.total_tagihan, i.tanggal_buat, " +
            "COALESCE((SELECT status_pembayaran FROM pembayaran p WHERE p.id_reservasi = i.id_reservasi LIMIT 1), 'Belum Bayar') as status " +
            "FROM invoice i JOIN reservasi r ON i.id_reservasi = r.id_reservasi " +
            "JOIN tamu t ON r.id_tamu = t.id_tamu ORDER BY i.id_invoice DESC"
        );
    }

    public void hapusTagihan(int idInvoice) {
        String sql = "DELETE FROM invoice WHERE id_invoice = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInvoice);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal hapus tagihan: " + e.getMessage());
        }
    }

    public void konfirmasiPembayaran(int idReservasi, int totalTagihan, String metode) throws Exception {
        String sql = "INSERT INTO pembayaran (id_reservasi, jumlah_bayar, metode_bayar, status_pembayaran) VALUES (?, ?, ?, 'Lunas')";
        Connection conn = KoneksiDB.connect();
        if (conn == null) throw new Exception("Gagal koneksi database");
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReservasi);
            ps.setInt(2, totalTagihan);
            ps.setString(3, metode);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Gagal konfirmasi pembayaran: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // LAPORAN AGREGASI (GROUP BY + HAVING + COUNT/SUM/AVG)
    // ------------------------------------------------------------
    // Pendapatan & jumlah reservasi dikelompokkan per tipe kamar.
    // HAVING menyaring hanya tipe yang sudah menghasilkan pendapatan.
    // Dipakai di menu "Laporan & Dashboard" (CLI) dan tab GUI.
    // ============================================================
    public ResultSet getLaporanPendapatanPerTipe() throws Exception {
        String sql =
            "SELECT tk.nama_tipe, " +
            "       COUNT(r.id_reservasi) AS jumlah_reservasi, " +
            "       SUM(i.total_tagihan) AS total_pendapatan, " +
            "       ROUND(AVG(i.total_tagihan), 0) AS rata_rata " +
            "FROM reservasi r " +
            "JOIN kamar k ON r.id_kamar = k.id_kamar " +
            "JOIN tipe_kamar tk ON k.id_tipe = tk.id_tipe " +
            "JOIN invoice i ON i.id_reservasi = r.id_reservasi " +
            "GROUP BY tk.nama_tipe " +
            "HAVING SUM(i.total_tagihan) > 0 " +
            "ORDER BY total_pendapatan DESC";
        Connection conn = KoneksiDB.connect();
        if (conn == null) throw new Exception("Gagal koneksi database");
        return conn.createStatement().executeQuery(sql);
    }

    public int[] getStatusCounts() {
        int[] counts = new int[4]; 
        try (Connection conn = KoneksiDB.connect()) {
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM kamar");
            if (rs1.next()) counts[0] = rs1.getInt(1);
            
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM kamar WHERE LOWER(status) = 'tersedia'");
            if (rs2.next()) counts[1] = rs2.getInt(1);
            
            ResultSet rs3 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM kamar WHERE LOWER(status) = 'terisi'");
            if (rs3.next()) counts[2] = rs3.getInt(1);

            ResultSet rs4 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM reservasi WHERE LOWER(status_reservasi) = 'check-in'");
            if (rs4.next()) counts[3] = rs4.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return counts;
    }

    public java.util.Vector<String> getDropdownTamu() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT id_tamu, nama FROM tamu"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_tamu") + " - " + rs.getString("nama"));
        } catch (Exception e) {}
        return v;
    }

    public java.util.Vector<String> getDropdownKamarTersedia() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT k.id_kamar, k.nomor_kamar, t.nama_tipe FROM kamar k JOIN tipe_kamar t ON k.id_tipe = t.id_tipe WHERE LOWER(k.status) = 'tersedia'" ); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_kamar") + " - No." + rs.getString("nomor_kamar") + " (" + rs.getString("nama_tipe") + ")");
        } catch (Exception e) {}
        return v;
    }

    public java.util.Vector<String> getDropdownKaryawan() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT id_karyawan, nama_karyawan FROM karyawan"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_karyawan") + " - " + rs.getString("nama_karyawan"));
        } catch (Exception e) {}
        return v;
    }

    public java.util.Vector<String> getDropdownTipeKamar() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT id_tipe, nama_tipe FROM tipe_kamar"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_tipe") + " - " + rs.getString("nama_tipe"));
        } catch (Exception e) {}
        return v;
    }

    public java.util.Vector<String> getDropdownSupplier() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT id_supplier, nama_supplier FROM supplier ORDER BY id_supplier"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_supplier") + " - " + rs.getString("nama_supplier"));
        } catch (Exception e) {}
        return v;
    }

    public java.util.Vector<String> getDropdownTagihan() {
        java.util.Vector<String> v = new java.util.Vector<>();
        String sql = "SELECT i.id_invoice, i.no_invoice, i.id_reservasi, t.nama, i.total_tagihan " +
                     "FROM invoice i JOIN reservasi r ON i.id_reservasi = r.id_reservasi " +
                     "JOIN tamu t ON r.id_tamu = t.id_tamu ORDER BY i.id_invoice DESC";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                v.add(rs.getInt("id_invoice") + "|" + rs.getString("no_invoice") + "|" + rs.getInt("id_reservasi") + "|" + rs.getString("nama") + "|" + rs.getInt("total_tagihan"));
            }
        } catch (Exception e) {}
        return v;
    }

    public java.util.Vector<String> getDropdownReservasiAktif() {
        java.util.Vector<String> v = new java.util.Vector<>();
        String sql = "SELECT r.id_reservasi, t.nama, k.nomor_kamar FROM reservasi r " +
                 "JOIN tamu t ON r.id_tamu = t.id_tamu JOIN kamar k ON r.id_kamar = k.id_kamar " +
                 "WHERE LOWER(r.status_reservasi) = 'check-in'";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_reservasi") + " - " + rs.getString("nama") + " (Kamar " + rs.getString("nomor_kamar") + ")");
        } catch (Exception e) {}
        return v;
    }

    public java.util.Vector<String> getDropdownMejaRestoran() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT id_meja, nomor_meja FROM meja_restoran ORDER BY id_meja"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_meja") + " - " + rs.getString("nomor_meja"));
        } catch (Exception e) {}
        return v;
    }

    public java.util.Vector<String> getDropdownMenuRestoran() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT id_menu, nama_menu, harga FROM menu_restoran ORDER BY id_menu"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_menu") + " - " + rs.getString("nama_menu") + String.format(" (Rp %,d)", rs.getInt("harga")));
        } catch (Exception e) {}
        return v;
    }

    public java.util.Vector<String> getDropdownLayanan() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT id_layanan, nama_layanan, harga FROM layanan_hotel ORDER BY id_layanan"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_layanan") + " - " + rs.getString("nama_layanan") + String.format(" (Rp %,d)", rs.getInt("harga")));
        } catch (Exception e) {}
        return v;
    }

    public java.util.List<KamarInventaris> getInventarisKamar(int idKamar) {
        java.util.List<KamarInventaris> list = new java.util.ArrayList<>();
        String sql = "SELECT k.id_kamar, k.nomor_kamar, k.status, " +
                     "ki.jumlah_terpasang, " +
                     "i.id_barang, i.nama_barang, i.satuan " +
                     "FROM kamar k " +
                     "JOIN kamar_inventaris ki ON k.id_kamar = ki.id_kamar " +
                     "JOIN logistik.inventaris i ON ki.id_barang = i.id_barang " +
                     "WHERE k.id_kamar = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKamar);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Kamar k = new Kamar(rs.getInt("id_kamar"), null, rs.getString("nomor_kamar"), rs.getString("status"));
                    Inventaris inv = new Inventaris(rs.getInt("id_barang"), rs.getString("nama_barang"), 0, rs.getString("satuan"));
                    KamarInventaris ki = new KamarInventaris(k, inv, rs.getInt("jumlah_terpasang"));
                    list.add(ki);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void alokasikanInventarisKamar(int idKamar, int idBarang, int jumlah) throws Exception {
        String sql = "INSERT INTO kamar_inventaris (id_kamar, id_barang, jumlah_terpasang) VALUES (?, ?, ?) " +
                     "ON CONFLICT (id_kamar, id_barang) DO UPDATE SET jumlah_terpasang = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKamar);
            ps.setInt(2, idBarang);
            ps.setInt(3, jumlah);
            ps.setInt(4, jumlah);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Gagal mengalokasikan inventaris: " + e.getMessage(), e);
        }
    }

    public void hapusAlokasiInventarisKamar(int idKamar, int idBarang) throws Exception {
        String sql = "DELETE FROM kamar_inventaris WHERE id_kamar = ? AND id_barang = ?";
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKamar);
            ps.setInt(2, idBarang);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Gagal menghapus alokasi inventaris: " + e.getMessage(), e);
        }
    }

    public java.util.Vector<String> getDropdownInventaris() {
        java.util.Vector<String> v = new java.util.Vector<>();
        try (Connection conn = KoneksiDB.connect(); PreparedStatement ps = conn.prepareStatement("SELECT id_barang, nama_barang, satuan FROM logistik.inventaris ORDER BY id_barang"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) v.add(rs.getInt("id_barang") + " - " + rs.getString("nama_barang") + " (" + rs.getString("satuan") + ")");
        } catch (Exception e) {}
        return v;
    }

    public String getTagihanBerjalan(int idReservasi) throws Exception {
        try (Connection conn = KoneksiDB.connect()) {
            if (conn == null) throw new Exception("Gagal koneksi database");
            StringBuilder invoiceText = new StringBuilder();
            String sql = "SELECT t.nama, k.nomor_kamar, tk.nama_tipe, tk.harga_per_malam, " +
                         "r.tanggal_checkin, r.tanggal_checkout, " +
                         "(CURRENT_DATE - r.tanggal_checkin) as durasi_sekarang, " +
                         "(r.tanggal_checkout - r.tanggal_checkin) as durasi_total, " +
                         "COALESCE((SELECT SUM(subtotal) FROM detail_pesanan_restoran d JOIN pesanan_restoran p ON d.id_pesanan = p.id_pesanan WHERE p.id_reservasi = ?), 0) as total_resto, " +
                         "COALESCE((SELECT SUM(dl.jumlah * lh.harga) FROM detail_layanan_reservasi dl JOIN layanan_hotel lh ON dl.id_layanan = lh.id_layanan WHERE dl.id_reservasi = ?), 0) as total_layanan " +
                         "FROM reservasi r " +
                         "JOIN tamu t ON r.id_tamu = t.id_tamu " +
                         "JOIN kamar k ON r.id_kamar = k.id_kamar " +
                         "JOIN tipe_kamar tk ON k.id_tipe = tk.id_tipe " +
                         "WHERE r.id_reservasi = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idReservasi); ps.setInt(2, idReservasi); ps.setInt(3, idReservasi);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new Exception("Reservasi tidak ditemukan: " + idReservasi);

                    int durasiSekarang = rs.getInt("durasi_sekarang");
                    if (durasiSekarang <= 0) durasiSekarang = 1; // Minimal 1 hari berjalan
                    int durasiTotal = rs.getInt("durasi_total") == 0 ? 1 : rs.getInt("durasi_total");
                    
                    int totalKamarSekarang = durasiSekarang * rs.getInt("harga_per_malam");
                    int totalKamarTotal = durasiTotal * rs.getInt("harga_per_malam");
                    
                    int totalResto = rs.getInt("total_resto");
                    int totalLayanan = rs.getInt("total_layanan");
                    
                    int grandTotalSekarang = totalKamarSekarang + totalResto + totalLayanan;
                    int grandTotalTotal = totalKamarTotal + totalResto + totalLayanan;

                    invoiceText.append("===========================================\n");
                    invoiceText.append("     RILIS TAGIHAN BERJALAN (RUNNING BILL) \n");
                    invoiceText.append("          STATUS RESERVASI: AKTIF          \n");
                    invoiceText.append("===========================================\n");
                    invoiceText.append(String.format("Nama Tamu       : %s\n", rs.getString("nama")));
                    invoiceText.append(String.format("No. Kamar       : %s (%s)\n", rs.getString("nomor_kamar"), rs.getString("nama_tipe")));
                    invoiceText.append(String.format("Tgl Check-in    : %s\n", rs.getDate("tanggal_checkin").toString()));
                    invoiceText.append(String.format("Tgl Check-out   : %s\n", rs.getDate("tanggal_checkout").toString()));
                    invoiceText.append(String.format("Hari Berjalan   : %d Hari (dari total %d malam)\n", durasiSekarang, durasiTotal));
                    invoiceText.append("-------------------------------------------\n");
                    invoiceText.append(String.format("Biaya Kamar Inap  : Rp %,12d\n", totalKamarSekarang));
                    invoiceText.append(String.format("Biaya Restoran    : Rp %,12d\n", totalResto));
                    if (totalResto > 0) {
                        try (PreparedStatement psR = conn.prepareStatement("SELECT m.nama_menu, d.jumlah, d.subtotal FROM detail_pesanan_restoran d JOIN pesanan_restoran p ON d.id_pesanan = p.id_pesanan JOIN menu_restoran m ON d.id_menu = m.id_menu WHERE p.id_reservasi = ?")) {
                            psR.setInt(1, idReservasi);
                            try (ResultSet rsR = psR.executeQuery()) {
                                while (rsR.next()) {
                                    String namaMenu = rsR.getString("nama_menu");
                                    if (namaMenu.length() > 15) namaMenu = namaMenu.substring(0, 15);
                                    invoiceText.append(String.format("   ~ %2dx %-15s : Rp %,9d\n", rsR.getInt("jumlah"), namaMenu, rsR.getInt("subtotal")));
                                }
                            }
                        }
                    }
                    invoiceText.append(String.format("Biaya Layanan     : Rp %,12d\n", totalLayanan));
                    if (totalLayanan > 0) {
                        try (PreparedStatement psL = conn.prepareStatement("SELECT lh.nama_layanan, dl.jumlah, lh.harga FROM detail_layanan_reservasi dl JOIN layanan_hotel lh ON dl.id_layanan = lh.id_layanan WHERE dl.id_reservasi = ?")) {
                            psL.setInt(1, idReservasi);
                            try (ResultSet rsL = psL.executeQuery()) {
                                while (rsL.next()) {
                                    String namaLayanan = rsL.getString("nama_layanan");
                                    if (namaLayanan.length() > 15) namaLayanan = namaLayanan.substring(0, 15);
                                    invoiceText.append(String.format("   ~ %2dx %-15s : Rp %,9d\n", rsL.getInt("jumlah"), namaLayanan, rsL.getInt("jumlah") * rsL.getInt("harga")));
                                }
                            }
                        }
                    }
                    
                    invoiceText.append("-------------------------------------------\n");
                    invoiceText.append(String.format("GRAND TOTAL (SAAT INI) : Rp %,12d\n", grandTotalSekarang));
                    invoiceText.append(String.format("ESTIMASI TOTAL AKHIR   : Rp %,12d\n", grandTotalTotal));
                    invoiceText.append("===========================================\n");
                    invoiceText.append("  *Dokumen ini adalah rincian biaya sementara\n");
                    invoiceText.append("   selama tamu menginap dan bukan struk lunas.\n");
                    invoiceText.append("===========================================\n");
                }
            }
            return invoiceText.toString();
        }
    }
}
