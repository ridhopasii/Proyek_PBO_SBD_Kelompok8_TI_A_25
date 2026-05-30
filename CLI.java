import java.util.Scanner;
import java.util.Vector;

public class CLI {
    private static HotelService service = new HotelService();
    private static Karyawan currentUser = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== APLIKASI MANAJEMEN HOTEL ===");
        System.out.println("Mode: Terminal CLI");
        System.out.println();

        // Login dulu sebelum bisa akses aplikasi
        boolean loginSuccess = false;
        while (!loginSuccess) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            
            currentUser = service.authenticateKaryawan(username, password);
            if (currentUser == null) {
                System.out.println("[GAGAL] Login tidak berhasil!");
                System.out.println();
            } else {
                loginSuccess = true;
            }
        }

        System.out.println();
        System.out.println("[BERHASIL LOGIN]");
        System.out.println("Selamat datang, " + currentUser.getNamaKaryawan() + "!");
        System.out.print("Posisi: ");
        currentUser.tampilkanProfil();
        System.out.println();

        // Main menu loop
        int menuChoice = 0;
        while (menuChoice != 99) {
            try {
                displayMainMenu();
                menuChoice = readIntSafe(scanner, "Masukkan pilihan [1-7 atau 99]: ");
                System.out.println();

                switch (menuChoice) {
                    case 1:
                        manageRooms(scanner);
                        break;
                    case 2:
                        manageGuests(scanner);
                        break;
                    case 3:
                        manageReservations(scanner);
                        break;
                    case 4:
                        processCheckout(scanner);
                        break;
                    case 5:
                        confirmPayment(scanner);
                        break;
                    case 6:
                        manageLogistics(scanner);
                        break;
                    case 7:
                        showReports();
                        break;
                    case 99:
                        System.out.println("Terima kasih! Aplikasi ditutup...");
                        break;
                    default:
                        System.out.println("[ERROR] Pilihan tidak valid!");
                }
            } catch (Exception ex) {
                System.out.println("[ERROR] Ada masalah: " + ex.getMessage());
            }
            System.out.println();
        }

        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("┌───────────────────────────────────────────┐");
        System.out.println("│          🏨 MENU UTAMA APLIKASI           │");
        System.out.println("├───────────────────────────────────────────┤");
        System.out.println("│  [1]  Manajemen Kamar & Inventaris        │");
        System.out.println("│  [2]  Manajemen Tamu                      │");
        System.out.println("│  [3]  Manajemen Reservasi & Transaksi     │");
        System.out.println("│  [4]  Check-out & Cetak Invoice           │");
        System.out.println("│  [5]  Konfirmasi Pembayaran               │");
        System.out.println("│  [6]  Manajemen Logistik & PO             │");
        System.out.println("│  [7]  Laporan & Dashboard                 │");
        System.out.println("│  [99] Keluar                              │");
        System.out.println("└───────────────────────────────────────────┘");
    }

    private static void manageRooms(Scanner scanner) {
        System.out.println("\n┌───────────────────────────────────────────┐");
        System.out.println("│      -- MANAJEMEN KAMAR & INVENTARIS --   │");
        System.out.println("├───────────────────────────────────────────┤");
        System.out.println("│  [1] Lihat Semua Kamar                    │");
        System.out.println("│  [2] Tambah Kamar                         │");
        System.out.println("│  [3] Hapus Kamar                          │");
        System.out.println("│  [4] Lihat Inventaris Kamar               │");
        System.out.println("│  [5] Alokasikan Inventaris ke Kamar       │");
        System.out.println("│  [6] Hapus Alokasi Inventaris Kamar       │");
        System.out.println("│  [0] Batal / Kembali                      │");
        System.out.println("└───────────────────────────────────────────┘");
        
        try {
            int ch = readIntSafe(scanner, "Pilih [0-6]: ", 0, 6);
            if (ch == 0) return;
            switch (ch) {
                case 1:
                    service.lihatKamarLengkap();
                    break;
                case 2:
                    Vector<String> types = service.getDropdownTipeKamar();
                    int idTipe = chooseFromList(scanner, "Pilih tipe kamar:", types);
                    if (idTipe == -1) return;
                    String no = readStringSafe(scanner, "Nomor Kamar: ");
                    int lt = readIntSafe(scanner, "Lantai: ");
                    service.tambahKamar(idTipe, no, lt);
                    break;
                case 3:
                    int id = readIntSafe(scanner, "ID Kamar yang akan dihapus: ");
                    service.hapusKamar(id); 
                    break;
                case 4:
                    int idK = readIntSafe(scanner, "Masukkan ID Kamar: ");
                    java.util.List<KamarInventaris> list = service.getInventarisKamar(idK);
                    if (list.isEmpty()) {
                        System.out.println("Tidak ada barang logistik terpasang di kamar ini.");
                    } else {
                        System.out.println("=== INVENTARIS KAMAR ===");
                        for (KamarInventaris ki : list) {
                            System.out.printf("Barang: %-15s | Jumlah: %2d %s\n", 
                                ki.getInventaris().getNamaBarang(), ki.getJumlahTerpasang(), ki.getInventaris().getSatuan());
                        }
                    }
                    break;
                case 5:
                    int idKam = readIntSafe(scanner, "Masukkan ID Kamar: ");
                    Vector<String> invList = service.getDropdownInventaris();
                    int idBar = chooseFromList(scanner, "Pilih barang logistik:", invList);
                    if (idBar == -1) return;
                    int jml = readIntSafe(scanner, "Jumlah Terpasang: ");
                    service.alokasikanInventarisKamar(idKam, idBar, jml);
                    System.out.println("Barang berhasil dialokasikan ke kamar.");
                    break;
                case 6:
                    int idKamH = readIntSafe(scanner, "Masukkan ID Kamar: ");
                    int idBarH = readIntSafe(scanner, "Masukkan ID Barang: ");
                    service.hapusAlokasiInventarisKamar(idKamH, idBarH);
                    System.out.println("Alokasi barang berhasil dihapus dari kamar.");
                    break;
            }
        } catch (Exception e) { System.out.println("Gagal melakukan aksi kamar: " + e.getMessage()); }
    }

    private static void manageGuests(Scanner scanner) {
        System.out.println("\n┌───────────────────────────────────────────┐");
        System.out.println("│           -- MANAJEMEN TAMU --            │");
        System.out.println("├───────────────────────────────────────────┤");
        System.out.println("│  [1] Lihat Semua Tamu                     │");
        System.out.println("│  [2] Tambah Tamu Baru                     │");
        System.out.println("│  [3] Edit Tamu                            │");
        System.out.println("│  [4] Hapus Tamu                           │");
        System.out.println("│  [5] Cari Tamu (LIKE/ILIKE)               │");
        System.out.println("│  [0] Batal / Kembali                      │");
        System.out.println("└───────────────────────────────────────────┘");
        
        try {
            int ch = readIntSafe(scanner, "Pilih [0-5]: ", 0, 5);
            if (ch == 0) return;
            switch (ch) {
                case 1:
                    Vector<String> vt = service.getDropdownTamu();
                    if (vt.isEmpty()) {
                        System.out.println("Tidak ada data tamu.");
                    } else {
                        System.out.println("=== DAFTAR TAMU ===");
                        for (String t : vt) System.out.println(t);
                    }
                    break;
                case 2:
                    String nama = readStringSafe(scanner, "Nama: ");
                    String ti = readStringSafe(scanner, "Tipe Identitas (KTP/Passport): ");
                    String ni = readStringSafe(scanner, "No Identitas: ");
                    String hp = readStringSafe(scanner, "No HP: ");
                    String em = readStringSafe(scanner, "Email: ");
                    String al = readStringSafe(scanner, "Alamat: ");
                    service.tambahTamu(nama, ti, ni, hp, em, al);
                    break;
                case 3:
                    Vector<String> vtEdit = service.getDropdownTamu();
                    int idT = chooseFromList(scanner, "Pilih tamu yang akan diedit:", vtEdit);
                    if (idT == -1) return;
                    String nNama = readStringSafe(scanner, "Nama Baru: ");
                    String nTi = readStringSafe(scanner, "Tipe Identitas Baru: ");
                    String nNi = readStringSafe(scanner, "No Identitas Baru: ");
                    String nHp = readStringSafe(scanner, "No HP Baru: ");
                    String nEm = readStringSafe(scanner, "Email Baru: ");
                    String nAl = readStringSafe(scanner, "Alamat Baru: ");
                    service.updateTamu(idT, nNama, nTi, nNi, nHp, nEm, nAl);
                    break;
                case 4:
                    Vector<String> vtDel = service.getDropdownTamu();
                    int idTDel = chooseFromList(scanner, "Pilih tamu yang akan dihapus:", vtDel);
                    if (idTDel == -1) return;
                    service.hapusTamu(idTDel);
                    break;
                case 5:
                    String keyword = readStringSafe(scanner, "Ketik Nama / Email Tamu untuk dicari: ");
                    java.sql.ResultSet rs = null;
                    try {
                        rs = service.cariTamu(keyword);
                        System.out.println("=== HASIL PENCARIAN ===");
                        boolean found = false;
                        while (rs.next()) {
                            found = true;
                            System.out.printf("ID: %d | Nama: %s | ID No: %s | HP: %s | Email: %s | Alamat: %s\n",
                                rs.getInt("id_tamu"), rs.getString("nama"), rs.getString("identitas_tipe"),
                                rs.getString("identitas_no"), rs.getString("no_hp"), rs.getString("email"), rs.getString("alamat"));
                        }
                        if (!found) System.out.println("Tamu tidak ditemukan.");
                    } finally {
                        KoneksiDB.closeQuietly(rs);
                    }
                    break;
            }
        } catch (Exception e) { System.out.println("Gagal melakukan aksi tamu: " + e.getMessage()); }
    }

    private static void manageReservations(Scanner scanner) {
        System.out.println("\n┌───────────────────────────────────────────┐");
        System.out.println("│    -- MANAJEMEN RESERVASI & TRANSAKSI --  │");
        System.out.println("├───────────────────────────────────────────┤");
        System.out.println("│  [1] Buat Reservasi Baru (Check-in)       │");
        System.out.println("│  [2] Rilis Tagihan Berjalan (Running Bill)│");
        System.out.println("│  [3] Pesan Restoran                       │");
        System.out.println("│  [4] Pesan Layanan Hotel                  │");
        System.out.println("│  [5] Catat Akses Fasilitas Umum           │");
        System.out.println("│  [6] Kelola Menu Restoran (CRUD)          │");
        System.out.println("│  [0] Batal / Kembali                      │");
        System.out.println("└───────────────────────────────────────────┘");

        try {
            int ch = readIntSafe(scanner, "Pilih [0-6]: ", 0, 6);
            if (ch == 0) return;
            switch (ch) {
                case 1:
                    createReservation(scanner); break;
                case 2:
                    Vector<String> aktifBill = service.getDropdownReservasiAktif();
                    if (aktifBill.isEmpty()) { System.out.println("Tidak ada reservasi aktif."); return; }
                    int idRBill = chooseFromList(scanner, "Pilih reservasi untuk Running Bill:", aktifBill);
                    if (idRBill == -1) return;
                    String billText = service.getTagihanBerjalan(idRBill);
                    System.out.println("\n" + billText);
                    break;
                case 3:
                    Vector<String> aktifResto = service.getDropdownReservasiAktif();
                    if (aktifResto.isEmpty()) { System.out.println("Tidak ada tamu aktif untuk memesan restoran."); return; }
                    int idRResto = chooseFromList(scanner, "Pilih reservasi tamu:", aktifResto);
                    if (idRResto == -1) return;
                    
                    Vector<String> mejaList = service.getDropdownMejaRestoran();
                    int idMeja = chooseFromList(scanner, "Pilih meja restoran:", mejaList);
                    if (idMeja == -1) return;
                    
                    Vector<String> menuList = service.getDropdownMenuRestoran();
                    int idMenu = chooseFromList(scanner, "Pilih menu makanan/minuman:", menuList);
                    if (idMenu == -1) return;
                    
                    int porsi = readIntSafe(scanner, "Jumlah Porsi: ");
                    service.pesanRestoran(idRResto, idMeja, idMenu, porsi);
                    System.out.println("Pesanan restoran berhasil ditambahkan ke tagihan kamar.");
                    break;
                case 4:
                    Vector<String> aktifLay = service.getDropdownReservasiAktif();
                    if (aktifLay.isEmpty()) { System.out.println("Tidak ada tamu aktif untuk memesan layanan."); return; }
                    int idRLay = chooseFromList(scanner, "Pilih reservasi tamu:", aktifLay);
                    if (idRLay == -1) return;
                    
                    Vector<String> layList = service.getDropdownLayanan();
                    int idLay = chooseFromList(scanner, "Pilih layanan hotel:", layList);
                    if (idLay == -1) return;
                    
                    int jmlLay = readIntSafe(scanner, "Jumlah (kali): ");
                    service.pesanLayanan(idRLay, idLay, jmlLay);
                    System.out.println("Layanan hotel berhasil ditambahkan ke tagihan kamar.");
                    break;
                case 5:
                    Vector<String> aktifFas = service.getDropdownReservasiAktif();
                    if (aktifFas.isEmpty()) { System.out.println("Tidak ada tamu aktif untuk menggunakan fasilitas."); return; }
                    int idRFas = chooseFromList(scanner, "Pilih reservasi tamu:", aktifFas);
                    if (idRFas == -1) return;
                    
                    Vector<String> fasItems = new Vector<>();
                    try (java.sql.Connection conn = KoneksiDB.connect(); 
                         java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id_fasilitas, nama_fasilitas FROM fasilitas_umum ORDER BY id_fasilitas");
                         java.sql.ResultSet rsFas = ps.executeQuery()) {
                        while (rsFas.next()) {
                            fasItems.add(rsFas.getInt("id_fasilitas") + " - " + rsFas.getString("nama_fasilitas"));
                        }
                    }
                    if (fasItems.isEmpty()) { System.out.println("Fasilitas umum tidak ditemukan."); return; }
                    int idFas = chooseFromList(scanner, "Pilih fasilitas umum:", fasItems);
                    if (idFas == -1) return;
                    
                    service.gunakanFasilitas(idRFas, idFas);
                    System.out.println("Penggunaan fasilitas berhasil dicatat.");
                    break;
                case 6:
                    manageMenu(scanner);
                    break;
            }
        } catch (Exception e) { System.out.println("Gagal transaksi: " + e.getMessage()); }
    }

    private static void manageMenu(Scanner scanner) {
        System.out.println("\n┌───────────────────────────────────────────┐");
        System.out.println("│     -- KELOLA MENU RESTORAN (CRUD) --     │");
        System.out.println("├───────────────────────────────────────────┤");
        System.out.println("│  [1] Lihat Semua Menu                     │");
        System.out.println("│  [2] Tambah Menu                          │");
        System.out.println("│  [3] Edit Menu                            │");
        System.out.println("│  [4] Hapus Menu                           │");
        System.out.println("│  [0] Batal / Kembali                      │");
        System.out.println("└───────────────────────────────────────────┘");

        try {
            int ch = readIntSafe(scanner, "Pilih [0-4]: ", 0, 4);
            if (ch == 0) return;
            switch (ch) {
                case 1: {
                    Vector<String> vm = service.getDropdownMenuRestoran();
                    if (vm.isEmpty()) { System.out.println("Belum ada menu."); break; }
                    System.out.println("=== DAFTAR MENU ===");
                    for (String m : vm) System.out.println(m);
                    break;
                }
                case 2: {
                    Vector<String> kat = service.getDropdownKategoriMenu();
                    int idKat = chooseFromList(scanner, "Pilih kategori menu:", kat);
                    if (idKat == -1) return;
                    String nama = readStringSafe(scanner, "Nama Menu: ");
                    int harga = readIntSafe(scanner, "Harga (Rp): ");
                    service.tambahMenuRestoran(idKat, nama, harga);
                    break;
                }
                case 3: {
                    Vector<String> vm = service.getDropdownMenuRestoran();
                    int idMenu = chooseFromList(scanner, "Pilih menu yang akan diedit:", vm);
                    if (idMenu == -1) return;
                    Vector<String> kat = service.getDropdownKategoriMenu();
                    int idKat = chooseFromList(scanner, "Pilih kategori baru:", kat);
                    if (idKat == -1) return;
                    String nama = readStringSafe(scanner, "Nama Menu Baru: ");
                    int harga = readIntSafe(scanner, "Harga Baru (Rp): ");
                    service.updateMenuRestoran(idMenu, idKat, nama, harga);
                    break;
                }
                case 4: {
                    Vector<String> vm = service.getDropdownMenuRestoran();
                    int idMenu = chooseFromList(scanner, "Pilih menu yang akan dihapus:", vm);
                    if (idMenu == -1) return;
                    service.hapusMenuRestoran(idMenu);
                    break;
                }
            }
        } catch (Exception e) { System.out.println("Gagal kelola menu: " + e.getMessage()); }
    }

    private static void manageLogistics(Scanner scanner) {
        System.out.println("\n┌───────────────────────────────────────────┐");
        System.out.println("│         -- MANAJEMEN LOGISTIK & PO --     │");
        System.out.println("├───────────────────────────────────────────┤");
        System.out.println("│  [1] Lihat Stok Inventaris Gudang         │");
        System.out.println("│  [2] Tambah Stok Logistik (Pengadaan PO)  │");
        System.out.println("│  [3] Lihat Daftar Supplier                │");
        System.out.println("│  [0] Batal / Kembali                      │");
        System.out.println("└───────────────────────────────────────────┘");
        
        try {
            int ch = readIntSafe(scanner, "Pilih [0-3]: ", 0, 3);
            if (ch == 0) return;
            switch (ch) {
                case 1:
                    java.sql.ResultSet rsLog = null;
                    try {
                        rsLog = service.getInventaris();
                        System.out.println("=== STOK LOGISTIK GUDANG ===");
                        while (rsLog.next()) {
                            System.out.printf("ID: %-3d | Barang: %-20s | Stok Saat Ini: %-4d | Satuan: %s\n",
                                rsLog.getInt("id_barang"), rsLog.getString("nama_barang"), rsLog.getInt("stok_sekarang"), rsLog.getString("satuan"));
                        }
                    } finally {
                        KoneksiDB.closeQuietly(rsLog);
                    }
                    break;
                case 2:
                    java.sql.ResultSet rsItems = null;
                    Vector<String> invItems = new Vector<>();
                    try {
                        rsItems = service.getInventaris();
                        while (rsItems.next()) {
                            invItems.add(rsItems.getInt("id_barang") + " - " + rsItems.getString("nama_barang"));
                        }
                    } finally {
                        KoneksiDB.closeQuietly(rsItems);
                    }
                    int idBarang = chooseFromList(scanner, "Pilih barang logistik yang akan ditambah stoknya:", invItems);
                    if (idBarang == -1) return;
                    
                    Vector<String> supplierList = service.getDropdownSupplier();
                    int idSupplier = chooseFromList(scanner, "Pilih supplier:", supplierList);
                    if (idSupplier == -1) return;
                    
                    int jml = readIntSafe(scanner, "Berapa jumlah barang yang dibeli/masuk?: ");
                    int hargaSatuan = readIntSafe(scanner, "Harga Satuan (Rp) [Boleh 0 jika gratis/hibah]: ");
                    
                    service.tambahPengadaan(idBarang, jml, idSupplier, currentUser.getIdKaryawan(), hargaSatuan);
                    System.out.println("Stok barang berhasil ditambah dan purchase order tercatat!");
                    break;
                case 3:
                    try (java.sql.Connection conn = KoneksiDB.connect(); 
                         java.sql.PreparedStatement ps = conn.prepareStatement("SELECT * FROM supplier ORDER BY id_supplier");
                         java.sql.ResultSet rsSup = ps.executeQuery()) {
                        System.out.println("=== DAFTAR SUPPLIER ===");
                        while (rsSup.next()) {
                            System.out.printf("ID: %-2d | Nama: %-20s | Kontak: %-15s | HP: %s\n",
                                rsSup.getInt("id_supplier"), rsSup.getString("nama_supplier"), rsSup.getString("kontak_person"), rsSup.getString("no_hp"));
                        }
                    }
                    break;
            }
        } catch (Exception e) { System.out.println("Gagal pengadaan: " + e.getMessage()); }
    }

    private static void createReservation(Scanner scanner) {
        try {
            System.out.println("\n-- FORM RESERVASI / CHECK-IN --");
            System.out.print("Tambah tamu baru? (y/n): ");
            String yn = scanner.nextLine().trim().toLowerCase();
            int idTamu = -1;
            if (yn.equals("y") || yn.equals("yes")) {
                String nama = readStringSafe(scanner, "Nama: ");
                String ti = readStringSafe(scanner, "Tipe Identitas (KTP/Passport): ");
                String ni = readStringSafe(scanner, "No Identitas: ");
                String hp = readStringSafe(scanner, "No HP: ");
                String em = readStringSafe(scanner, "Email: ");
                String al = readStringSafe(scanner, "Alamat: ");
                idTamu = service.tambahTamuReturnId(nama, ti, ni, hp, em, al);
                if (idTamu <= 0) { System.out.println("Gagal tambah tamu. Batal reservasi."); return; }
            } else {
                Vector<String> vt = service.getDropdownTamu();
                if (vt.isEmpty()) { System.out.println("Tidak ada data tamu. Silakan tambah tamu baru."); return; }
                idTamu = chooseFromList(scanner, "Pilih tamu:", vt);
                if (idTamu == -1) return;
            }

            Vector<String> vk = service.getDropdownKamarTersedia();
            if (vk.isEmpty()) { System.out.println("Tidak ada kamar tersedia saat ini."); return; }
            int idKamar = chooseFromList(scanner, "Pilih kamar tersedia:", vk);
            if (idKamar == -1) return;

            java.sql.Date cinDate = readDateSafe(scanner, "Tanggal Check-in (YYYY-MM-DD): ");
            java.sql.Date coutDate = readDateSafe(scanner, "Tanggal Check-out (YYYY-MM-DD): ");

            Tamu tamuObj = new Tamu();
            tamuObj.setIdTamu(idTamu);
            Kamar kamarObj = new Kamar();
            kamarObj.setIdKamar(idKamar);
            Karyawan karObj = new Karyawan();
            karObj.setIdKaryawan(currentUser.getIdKaryawan());
            
            Reservasi reservasi = new Reservasi();
            reservasi.setTamu(tamuObj);
            reservasi.setKamar(kamarObj);
            reservasi.setKaryawan(karObj);
            reservasi.setTanggalCheckin(cinDate);
            reservasi.setTanggalCheckout(coutDate);

            service.buatReservasi(reservasi);
            System.out.println("Reservasi berhasil dibuat.");
        } catch (Exception e) { System.out.println("Gagal membuat reservasi: " + e.getMessage()); }
    }

    private static void processCheckout(Scanner scanner) {
        try {
            Vector<String> aktif = service.getDropdownReservasiAktif();
            if (aktif.isEmpty()) { System.out.println("Tidak ada reservasi aktif untuk check-out."); return; }
            int idR = chooseFromList(scanner, "Pilih reservasi untuk check-out:", aktif);
            if (idR == -1) return;
            String struk = service.prosesCheckOut(idR);
            if (struk != null && !struk.isEmpty()) System.out.println("\n" + struk);
            else System.out.println("Check-out selesai (tidak ada struk).");
        } catch (Exception e) { System.out.println("Gagal proses check-out: " + e.getMessage()); }
    }

    private static void confirmPayment(Scanner scanner) {
        try {
            Vector<String> tagihan = service.getDropdownTagihan();
            if (tagihan.isEmpty()) { System.out.println("Tidak ada tagihan yang bisa dibayar."); return; }
            int idR = choosePaymentReservation(scanner, tagihan);
            if (idR == -1) return;
            int total = choosePaymentTotal(scanner, tagihan, idR);
            if (total == -1) return;
            String m = readStringSafe(scanner, "Metode Bayar (Tunai/Transfer/Kartu): ");
            service.konfirmasiPembayaran(idR, total, m);
            System.out.println("Pembayaran dicatat sebagai Lunas.");
        } catch (Exception e) { System.out.println("Gagal konfirmasi pembayaran: " + e.getMessage()); }
    }

    private static void showReports() {
        java.sql.ResultSet rs = null;
        try {
            int[] s = service.getStatusCounts();
            System.out.println("\n=== DASHBOARD ===");
            System.out.println("Total Kamar    : " + s[0]);
            System.out.println("Kamar Tersedia : " + s[1]);
            System.out.println("Kamar Terisi   : " + s[2]);
            System.out.println("Tamu In-House  : " + s[3]);
            System.out.println("\nLangkah berikutnya yang bisa dilakukan resepsionis:");
            System.out.println("1. Check-in tamu baru jika ada tamu datang.");
            System.out.println("2. Cek tagihan untuk tamu yang mau check-out.");
            System.out.println("3. Konfirmasi pembayaran jika invoice sudah siap.");
            System.out.println("4. Tambah kamar atau stok logistik bila diperlukan.");
            System.out.println("\nLaporan Reservasi Aktif (Ringkasan):");
            rs = service.getReservasiAktif();
            while (rs.next()) {
                System.out.printf("ID:%d | Tamu:%s | Kamar:%s | Check-in:%s | Status:%s\n",
                    rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
            }
            KoneksiDB.closeQuietly(rs);

            // Laporan agregasi: pendapatan per tipe kamar (GROUP BY + HAVING)
            System.out.println("\n=== LAPORAN PENDAPATAN PER TIPE KAMAR (Agregasi) ===");
            System.out.printf("%-28s | %-10s | %-15s | %-15s\n", "Tipe Kamar", "Jml Resv", "Total Pendapatan", "Rata-rata");
            System.out.println("---------------------------------------------------------------------------------");
            rs = service.getLaporanPendapatanPerTipe();
            boolean adaData = false;
            while (rs.next()) {
                adaData = true;
                System.out.printf("%-28s | %-10d | Rp %,12d | Rp %,11d\n",
                    rs.getString("nama_tipe"), rs.getInt("jumlah_reservasi"),
                    rs.getLong("total_pendapatan"), rs.getLong("rata_rata"));
            }
            if (!adaData) System.out.println("(Belum ada pendapatan tercatat)");
        } catch (Exception e) {
            System.out.println("Gagal menampilkan laporan: " + e.getMessage()); 
        } finally {
            KoneksiDB.closeQuietly(rs);
        }
    }

    private static int chooseFromList(Scanner scanner, String prompt, Vector<String> items) {
        System.out.println(prompt);
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + items.get(i));
        }
        System.out.println("  [0] Batal / Kembali");
        while (true) {
            System.out.print("Pilih nomor [0-" + items.size() + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() || input.equals("0")) {
                System.out.println("Aksi dibatalkan.");
                return -1;
            }
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < items.size()) {
                    String item = items.get(idx);
                    String[] parts = item.split(" - ");
                    return Integer.parseInt(parts[0].trim());
                }
            } catch (Exception e) {
                // Ignore and retry
            }
            System.out.println("[ERROR] Pilihan tidak valid! Masukkan angka antara 0 dan " + items.size());
        }
    }

    private static int choosePaymentReservation(Scanner scanner, Vector<String> tagihan) {
        System.out.println("Pilih tagihan yang akan dibayar:");
        for (int i = 0; i < tagihan.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + formatTagihan(tagihan.get(i)));
        }
        System.out.println("  [0] Batal / Kembali");
        while (true) {
            System.out.print("Pilih nomor [0-" + tagihan.size() + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() || input.equals("0")) {
                System.out.println("Aksi dibatalkan.");
                return -1;
            }
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < tagihan.size()) {
                    String[] parts = tagihan.get(idx).split("\\|");
                    return Integer.parseInt(parts[2].trim());
                }
            } catch (Exception e) {
                // Ignore and retry
            }
            System.out.println("[ERROR] Pilihan tidak valid! Masukkan angka antara 0 dan " + tagihan.size());
        }
    }

    private static int choosePaymentTotal(Scanner scanner, Vector<String> tagihan, int idReservasi) {
        for (String item : tagihan) {
            String[] parts = item.split("\\|");
            if (parts.length >= 5 && Integer.parseInt(parts[2]) == idReservasi) {
                return Integer.parseInt(parts[4]);
            }
        }
        return -1;
    }

    private static String formatTagihan(String raw) {
        String[] parts = raw.split("\\|");
        if (parts.length < 5) return raw;
        return "Invoice " + parts[1] + " | Tamu: " + parts[3] + " | Total: Rp " + parts[4] + " | Res: " + parts[2];
    }

    // Helper methods for safe user input loops
    private static int readIntSafe(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException nfe) {
                System.out.println("[ERROR] Input harus berupa angka bulat! Silakan coba lagi.");
            }
        }
    }

    private static int readIntSafe(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            int val = readIntSafe(scanner, prompt);
            if (val >= min && val <= max) {
                return val;
            }
            System.out.println("[ERROR] Input harus bernilai antara " + min + " dan " + max + "! Silakan coba lagi.");
        }
    }

    private static String readStringSafe(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[ERROR] Input tidak boleh kosong! Silakan coba lagi.");
        }
    }

    private static java.sql.Date readDateSafe(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("[ERROR] Tanggal tidak boleh kosong!");
                continue;
            }
            if (!input.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                System.out.println("[ERROR] Format tanggal salah! Gunakan format YYYY-MM-DD (Contoh: 2026-05-22).");
                continue;
            }
            try {
                return java.sql.Date.valueOf(input);
            } catch (IllegalArgumentException iae) {
                System.out.println("[ERROR] Tanggal tidak valid (misal bulan > 12 atau hari > 31)! Silakan coba lagi.");
            }
        }
    }
}
