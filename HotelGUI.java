import java.awt.*;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HotelGUI extends JFrame {
    private HotelService service = new HotelService();
    private Karyawan currentUser = null;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public HotelGUI() {
        setTitle("Manajemen Hotel Terintegrasi");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(23, 32, 42));
        sidebar.setPreferredSize(new Dimension(240, 800));
        sidebar.setLayout(new GridLayout(12, 1, 2, 2));

        JLabel title = new JLabel(" HOTEL MANAGER", JLabel.CENTER);
        title.setForeground(new Color(241, 196, 15));
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sidebar.add(title);

        String[] menus = {"Dashboard", "Manajemen Kamar", "Data Tamu", "Reservasi", "Menu Restoran", "Layanan Hotel", "Fasilitas Umum", "Logistik", "Tagihan & Pembayaran", "Laporan Agregasi"};
        for (String m : menus) {
            JButton btn = createSidebarButton(m);
            btn.addActionListener(e -> cardLayout.show(mainPanel, m));
            sidebar.add(btn);
        }
        add(sidebar, BorderLayout.WEST);

        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createWelcomePanel(), "Dashboard");
        mainPanel.add(createTablePanel("Daftar Kamar", new String[]{"ID","No.Kamar","Tipe","Harga","Lantai","Status","Pengisi"}, "Kamar"), "Manajemen Kamar");
        mainPanel.add(createTablePanel("Data Tamu Check-in", new String[]{"ID","Nama","Tipe ID","No ID","No HP","Alamat"}, "Tamu"), "Data Tamu");
        mainPanel.add(createTablePanel("Reservasi Aktif", new String[]{"ID","Nama Tamu","Kamar","Check-in","Status"}, "Reservasi"), "Reservasi");
        mainPanel.add(createTablePanel("Menu Restoran", new String[]{"ID","Menu","Kategori","Harga"}, "Restoran"), "Menu Restoran");
        mainPanel.add(createTablePanel("Layanan Hotel", new String[]{"ID","Nama Layanan","Harga"}, "Layanan"), "Layanan Hotel");
        mainPanel.add(createTablePanel("Fasilitas Umum", new String[]{"ID","Nama Fasilitas","Buka","Tutup"}, "Fasilitas"), "Fasilitas Umum");
        mainPanel.add(createTablePanel("Stok Logistik", new String[]{"ID","Nama Barang","Stok","Satuan"}, "Logistik"), "Logistik");
        mainPanel.add(createTablePanel("Histori Tagihan & Pembayaran", new String[]{"ID", "No Invoice", "ID Res", "Nama Tamu", "Total Rp", "Tanggal", "Status Bayar"}, "Tagihan"), "Tagihan & Pembayaran");
        mainPanel.add(createTablePanel("Laporan Pendapatan per Tipe Kamar (GROUP BY + HAVING)", new String[]{"Tipe Kamar", "Jumlah Reservasi", "Total Pendapatan (Rp)", "Rata-rata (Rp)"}, "LaporanAgregasi"), "Laporan Agregasi");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        
        
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        btn.setBackground(new Color(34, 47, 62)); 
        btn.setForeground(new Color(236, 240, 241));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 20));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185)); 
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(34, 47, 62)); 
            }
        });
        return btn;
    }

    private JPanel createWelcomePanel() {
        JPanel mainP = new JPanel(new BorderLayout());
        mainP.setBackground(new Color(242, 243, 244));
        mainP.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("HOTEL OVERVIEW DASHBOARD", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerPanel.add(title, BorderLayout.WEST);

        JButton btnRefreshDash = new JButton("🔄 Refresh Dashboard");
        btnRefreshDash.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefreshDash.setBackground(new Color(41, 128, 185));
        btnRefreshDash.setForeground(Color.WHITE);
        btnRefreshDash.setFocusPainted(false);
        btnRefreshDash.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefreshDash.addActionListener(e -> refreshDashboard());
        headerPanel.add(btnRefreshDash, BorderLayout.EAST);

        mainP.add(headerPanel, BorderLayout.NORTH);

        cardPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        cardPanel.setOpaque(false);

        int[] stats = service.getStatusCounts();
        cardPanel.add(createStatCard("TOTAL KAMAR", String.valueOf(stats[0]), new Color(52, 152, 219)));
        cardPanel.add(createStatCard("TERSEDIA", String.valueOf(stats[1]), new Color(46, 204, 113)));
        cardPanel.add(createStatCard("TERISI", String.valueOf(stats[2]), new Color(231, 76, 60)));
        cardPanel.add(createStatCard("TAMU AKTIF", String.valueOf(stats[3]), new Color(243, 156, 18)));

        mainP.add(cardPanel, BorderLayout.CENTER);
        return mainP;
    }

    private void refreshDashboard() {
        if (cardPanel != null) {
            cardPanel.removeAll();
            int[] stats = service.getStatusCounts();
            cardPanel.add(createStatCard("TOTAL KAMAR", String.valueOf(stats[0]), new Color(52, 152, 219)));
            cardPanel.add(createStatCard("TERSEDIA", String.valueOf(stats[1]), new Color(46, 204, 113)));
            cardPanel.add(createStatCard("TERISI", String.valueOf(stats[2]), new Color(231, 76, 60)));
            cardPanel.add(createStatCard("TAMU AKTIF", String.valueOf(stats[3]), new Color(243, 156, 18)));
            cardPanel.revalidate();
            cardPanel.repaint();
            JOptionPane.showMessageDialog(this, "Data dashboard berhasil diperbarui secara real-time!", "Dashboard Refreshed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void setCurrentUser(Karyawan user) {
        this.currentUser = user;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblVal = new JLabel(value);
        lblVal.setForeground(Color.WHITE);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblVal.setHorizontalAlignment(JLabel.RIGHT);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createTablePanel(String title, String[] columns, String type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        final DefaultTableModel logistikModel = new DefaultTableModel(new String[]{"ID Barang", "Nama Barang Logistik", "Sisa Stok", "Satuan"}, 0);
        final JTable logistikTable = new JTable(logistikModel);
        final JLabel lblLog = new JLabel(" Info Stok Logistik Tersedia (Gudang)", JLabel.LEFT);

        JLabel lbl = new JLabel(title, JLabel.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(lbl, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; } 
        };
        JTable table = new JTable(model);
        
        
        table.setRowHeight(35); 
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(46, 204, 113)); 
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230)); 
        table.setIntercellSpacing(new Dimension(0, 0));
        
        
        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(44, 62, 80));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        table.getTableHeader().setPreferredSize(new Dimension(100, 45));

        refreshData(type, model);

        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.addActionListener(e -> refreshData(type, model));
        bottomPanel.add(btnRefresh);

        if (type.equals("Kamar")) {
            JButton btnAdd = new JButton("Tambah Kamar");
            JButton btnEdit = new JButton("Edit Kamar");
            JButton btnDel = new JButton("Hapus Kamar");
            btnAdd.addActionListener(e -> {
                JComboBox<String> cbTipe = new JComboBox<>(service.getDropdownTipeKamar()); cbTipe.setEditable(true);
                JTextField noK = new JTextField(); JTextField lt = new JTextField();
                Object[] msg = {"Pilih/Ketik Jenis Kamar:", cbTipe, "Nomor Kamar Baru:", noK, "Lantai:", lt};
                if (JOptionPane.showConfirmDialog(null, msg, "Tambah Kamar", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    try {
                        int idTipe = Integer.parseInt(cbTipe.getSelectedItem().toString().split(" - ")[0].trim());
                        int lantaiNum = Integer.parseInt(lt.getText().trim());
                        service.tambahKamar(idTipe, noK.getText().trim(), lantaiNum);
                        refreshData(type, model);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(null, "Lantai harus berupa angka numerik!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Input tidak valid: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            btnEdit.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    try {
                        int idKamar = (int) table.getValueAt(row, 0);
                        Kamar kObj = service.getDetailKamarObj(idKamar);
                        if (kObj != null) {
                            System.out.println("Edit kamar: " + kObj.getNomorKamar() + " - " + kObj.getTipeKamar().getNamaTipe());
                        }
                        
                        String curNo = table.getValueAt(row, 1) != null ? table.getValueAt(row, 1).toString() : "";
                        String curTipe = table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "";
                        String curLantai = table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "";
                        String curStatus = table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "";
                        
                        JComboBox<String> cbTipe = new JComboBox<>(service.getDropdownTipeKamar());
                        for (int i = 0; i < cbTipe.getItemCount(); i++) {
                            if (cbTipe.getItemAt(i).contains(curTipe)) {
                                cbTipe.setSelectedIndex(i);
                                break;
                            }
                        }
                        JTextField noK = new JTextField(curNo);
                        JTextField lt = new JTextField(curLantai);
                        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Tersedia", "Terisi", "Maintenance", "Dibersihkan"});
                        cbStatus.setSelectedItem(curStatus);
                        
                        Object[] msg = {
                            "Pilih Jenis Kamar:", cbTipe, 
                            "Nomor Kamar:", noK, 
                            "Lantai:", lt,
                            "Status Kamar:", cbStatus
                        };
                        if (JOptionPane.showConfirmDialog(null, msg, "Edit Kamar", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                            int idTipe = Integer.parseInt(cbTipe.getSelectedItem().toString().split(" - ")[0].trim());
                            int lantaiNum = Integer.parseInt(lt.getText().trim());
                            service.updateKamar(idKamar, idTipe, noK.getText().trim(), lantaiNum, cbStatus.getSelectedItem().toString());
                            refreshData(type, model);
                        }
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(null, "Lantai harus berupa angka numerik!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Mengubah: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih kamar di tabel dulu!");
            });
            btnDel.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    try {
                        service.hapusKamar((int) table.getValueAt(row, 0));
                        refreshData(type, model);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Menghapus", JOptionPane.ERROR_MESSAGE);
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih kamar di tabel dulu!");
            });

            JButton btnAlokasi = new JButton("Alokasikan Barang");
            JButton btnHapusAlokasi = new JButton("Hapus Barang Kamar");
            
            btnAlokasi.addActionListener(e -> {
                int selRow = table.getSelectedRow();
                if (selRow != -1) {
                    int idKamar = (int) table.getValueAt(selRow, 0);
                    String noKamar = table.getValueAt(selRow, 1).toString();
                    
                    JComboBox<String> cbInv = new JComboBox<>(service.getDropdownInventaris());
                    cbInv.setEditable(true);
                    JTextField txtJml = new JTextField("2");
                    
                    Object[] msg = {"Pilih Barang Logistik:", cbInv, "Jumlah Terpasang:", txtJml};
                    if (JOptionPane.showConfirmDialog(null, msg, "Alokasikan Barang ke Kamar " + noKamar, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        try {
                            if (cbInv.getSelectedItem() == null) throw new Exception("Pilih barang logistik!");
                            int idBarang = Integer.parseInt(cbInv.getSelectedItem().toString().split(" - ")[0].trim());
                            int jml = Integer.parseInt(txtJml.getText().trim());
                            service.alokasikanInventarisKamar(idKamar, idBarang, jml);
                            
                            // Refresh logistik table
                            lblLog.setText(" 📦 Alokasi Inventaris di Kamar " + noKamar + " (Terpasang)");
                            logistikModel.setColumnIdentifiers(new String[]{"ID Barang", "Nama Barang Terpasang", "Jumlah Terpasang", "Satuan"});
                            logistikModel.setRowCount(0);
                            java.util.List<KamarInventaris> listKi = service.getInventarisKamar(idKamar);
                            for (KamarInventaris ki : listKi) {
                                logistikModel.addRow(new Object[]{
                                    ki.getInventaris().getIdBarang(),
                                    ki.getInventaris().getNamaBarang(),
                                    ki.getJumlahTerpasang(),
                                    ki.getInventaris().getSatuan()
                                });
                            }
                            JOptionPane.showMessageDialog(null, "Barang berhasil dialokasikan ke Kamar!");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Alokasi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Pilih kamar di tabel atas terlebih dahulu!");
                }
            });

            btnHapusAlokasi.addActionListener(e -> {
                int selRow = table.getSelectedRow();
                if (selRow != -1) {
                    int idKamar = (int) table.getValueAt(selRow, 0);
                    String noKamar = table.getValueAt(selRow, 1).toString();
                    
                    int selLog = logistikTable.getSelectedRow();
                    if (selLog != -1) {
                        int idBarang = (int) logistikTable.getValueAt(selLog, 0);
                        String namaBarang = logistikTable.getValueAt(selLog, 1).toString();
                        
                        if (JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus alokasi " + namaBarang + " dari Kamar " + noKamar + "?", "Hapus Alokasi Barang", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            try {
                                service.hapusAlokasiInventarisKamar(idKamar, idBarang);
                                
                                // Refresh logistik table
                                logistikModel.setRowCount(0);
                                java.util.List<KamarInventaris> listKi = service.getInventarisKamar(idKamar);
                                for (KamarInventaris ki : listKi) {
                                    logistikModel.addRow(new Object[]{
                                        ki.getInventaris().getIdBarang(),
                                        ki.getInventaris().getNamaBarang(),
                                        ki.getJumlahTerpasang(),
                                        ki.getInventaris().getSatuan()
                                    });
                                }
                                JOptionPane.showMessageDialog(null, "Alokasi barang berhasil dihapus!");
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Hapus", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Pilih barang logistik yang terpasang di tabel bawah!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Pilih kamar di tabel atas terlebih dahulu!");
                }
            });

            bottomPanel.add(btnAdd); bottomPanel.add(btnEdit); bottomPanel.add(btnDel);
            bottomPanel.add(btnAlokasi); bottomPanel.add(btnHapusAlokasi);
        } 
        else if (type.equals("Tamu")) {
            JButton btnAdd = new JButton("Tambah Tamu");
            JButton btnEdit = new JButton("Edit Tamu");
            JButton btnDel = new JButton("Hapus Tamu");
            btnAdd.addActionListener(e -> {
                JTextField n = new JTextField(); JTextField ti = new JTextField(); JTextField ni = new JTextField();
                JTextField hp = new JTextField(); JTextField em = new JTextField(); JTextField al = new JTextField();
                Object[] msg = {"Nama:", n, "Tipe Identitas:", ti, "No Identitas:", ni, "No HP:", hp, "Email:", em, "Alamat:", al};
                int opt = JOptionPane.showConfirmDialog(null, msg, "Tambah Tamu Baru", JOptionPane.OK_CANCEL_OPTION);
                if (opt == JOptionPane.OK_OPTION) {
                    service.tambahTamu(n.getText().trim(), ti.getText().trim(), ni.getText().trim(), hp.getText().trim(), em.getText().trim(), al.getText().trim());
                    refreshData(type, model);
                }
            });
            btnEdit.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    try {
                        int idTamu = (int) table.getValueAt(row, 0);
                        String curNama = table.getValueAt(row, 1) != null ? table.getValueAt(row, 1).toString() : "";
                        String curTipeId = table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "";
                        String curNoId = table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "";
                        String curNoHp = table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "";
                        String curAlamat = table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "";
                        // Ambil email dari database langsung agar tidak hilang
                        String curEmail = "";
                        try (java.sql.Connection dbConn = KoneksiDB.connect();
                             java.sql.PreparedStatement psEmail = dbConn.prepareStatement("SELECT email FROM tamu WHERE id_tamu = ?")) {
                            psEmail.setInt(1, idTamu);
                            try (java.sql.ResultSet rsEmail = psEmail.executeQuery()) {
                                if (rsEmail.next()) curEmail = rsEmail.getString("email") != null ? rsEmail.getString("email") : "";
                            }
                        } catch (Exception ignored) {}
                        
                        JTextField n = new JTextField(curNama);
                        JTextField ti = new JTextField(curTipeId);
                        JTextField ni = new JTextField(curNoId);
                        JTextField hp = new JTextField(curNoHp);
                        JTextField em = new JTextField(curEmail);
                        JTextField al = new JTextField(curAlamat);
                        
                        Object[] msg = {"Nama:", n, "Tipe Identitas:", ti, "No Identitas:", ni, "No HP:", hp, "Email:", em, "Alamat:", al};
                        int opt = JOptionPane.showConfirmDialog(null, msg, "Edit Tamu", JOptionPane.OK_CANCEL_OPTION);
                        if (opt == JOptionPane.OK_OPTION) {
                            service.updateTamu(idTamu, n.getText().trim(), ti.getText().trim(), ni.getText().trim(), hp.getText().trim(), em.getText().trim(), al.getText().trim());
                            refreshData(type, model);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Mengubah Tamu: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih tamu di tabel dulu!");
            });
            btnDel.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    try {
                        service.hapusTamu((int) table.getValueAt(row, 0));
                        refreshData(type, model);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Menghapus Tamu", JOptionPane.ERROR_MESSAGE);
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih tamu di tabel dulu!");
            });
            bottomPanel.add(btnAdd); bottomPanel.add(btnEdit); bottomPanel.add(btnDel);
        }
        else if (type.equals("Reservasi")) {
            JButton btnIn = new JButton("Check-in Tamu Baru");
            JButton btnOut = new JButton("Proses Check-out");
            
            btnIn.addActionListener(e -> {
                JComboBox<String> cbTamu = new JComboBox<>(service.getDropdownTamu()); cbTamu.setEditable(true);
                int choice = JOptionPane.showOptionDialog(null, "Pilih jenis tamu:", "Tamu Baru atau Lama",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Existing", "Tamu Baru"}, "Existing");
                if (choice == 1) {
                    JTextField n = new JTextField(); JTextField ti = new JTextField(); JTextField ni = new JTextField();
                    JTextField hp = new JTextField(); JTextField em = new JTextField(); JTextField al = new JTextField();
                    Object[] form = {"Nama Tamu:", n, "Tipe Identitas:", ti, "No Identitas:", ni, "No HP:", hp, "Email:", em, "Alamat:", al};
                    int ok = JOptionPane.showConfirmDialog(null, form, "Tambah Tamu Baru", JOptionPane.OK_CANCEL_OPTION);
                    if (ok == JOptionPane.OK_OPTION) {
                        int newId = service.tambahTamuReturnId(n.getText(), ti.getText(), ni.getText(), hp.getText(), em.getText(), al.getText());
                        if (newId > 0) {
                            String item = newId + " - " + n.getText();
                            ((javax.swing.DefaultComboBoxModel<String>)cbTamu.getModel()).addElement(item);
                            cbTamu.setSelectedItem(item);
                        } else { JOptionPane.showMessageDialog(null, "Gagal menambahkan tamu baru."); return; }
                    } else return;
                }
                JComboBox<String> cbKamar = new JComboBox<>(service.getDropdownKamarTersedia()); cbKamar.setEditable(true);
                JComboBox<String> cbKaryawan = new JComboBox<>(service.getDropdownKaryawan()); cbKaryawan.setEditable(true);
                if (currentUser != null) {
                    String sel = currentUser.getIdKaryawan() + " - " + currentUser.getNamaKaryawan();
                    cbKaryawan.setSelectedItem(sel);
                    cbKaryawan.setEnabled(false);
                }
                
                JTextField cin = new JTextField(java.time.LocalDate.now().toString()); 
                JTextField cout = new JTextField(java.time.LocalDate.now().plusDays(1).toString());
                
                Object[] msg = {
                    "Pilih/Ketik Tamu:", cbTamu, 
                    "Pilih/Ketik Kamar Tersedia:", cbKamar, 
                    "Pilih/Ketik Karyawan (Resepsionis):", cbKaryawan, 
                    "Tanggal Check-in (YYYY-MM-DD):", cin, 
                    "Tanggal Check-out (YYYY-MM-DD):", cout
                };
                
                int opt = JOptionPane.showConfirmDialog(null, msg, "Form Check-in Reservasi", JOptionPane.OK_CANCEL_OPTION);
                if (opt == JOptionPane.OK_OPTION) {
                    try {
                        
                        int idT = Integer.parseInt(cbTamu.getSelectedItem().toString().split(" - ")[0].trim());
                        int idK = Integer.parseInt(cbKamar.getSelectedItem().toString().split(" - ")[0].trim());
                        int idKar;
                        if (currentUser != null) idKar = currentUser.getIdKaryawan();
                        else idKar = Integer.parseInt(cbKaryawan.getSelectedItem().toString().split(" - ")[0].trim());

                        Tamu tamuObj = new Tamu();
                        tamuObj.setIdTamu(idT);
                        Kamar kamarObj = new Kamar();
                        kamarObj.setIdKamar(idK);
                        Karyawan karObj = new Karyawan();
                        karObj.setIdKaryawan(idKar);
                        
                        Reservasi reservasi = new Reservasi();
                        reservasi.setTamu(tamuObj);
                        reservasi.setKamar(kamarObj);
                        reservasi.setKaryawan(karObj);
                        reservasi.setTanggalCheckin(java.sql.Date.valueOf(cin.getText()));
                        reservasi.setTanggalCheckout(java.sql.Date.valueOf(cout.getText()));

                        int totalBiaya = service.hitungTotalKamar(idK, cin.getText(), cout.getText());
                        int lanjut = JOptionPane.showConfirmDialog(null, 
                            "Total biaya kamar dari " + cin.getText() + " s/d " + cout.getText() + 
                            " adalah: Rp " + String.format("%,d", totalBiaya) + "\n\nApakah Anda ingin melanjutkan pembayaran?", 
                            "Konfirmasi Pembayaran", JOptionPane.YES_NO_OPTION);
                            
                        if (lanjut == JOptionPane.YES_OPTION) {
                            String[] metode = {"Tunai", "Transfer Bank", "Kartu Kredit", "QRIS"};
                            String bayarPakai = (String) JOptionPane.showInputDialog(null, "Pilih Metode Pembayaran:", 
                                "Pembayaran Awal", JOptionPane.QUESTION_MESSAGE, null, metode, metode[0]);
                                
                            if (bayarPakai != null) {
                                String inv = service.buatReservasiBerbayar(reservasi, bayarPakai, totalBiaya);
                                refreshData(type, model);
                                
                                JTextArea ta = new JTextArea(inv);
                                ta.setFont(new Font("Consolas", Font.PLAIN, 14)); ta.setEditable(false);
                                ta.setBackground(new Color(250, 250, 250));
                                JOptionPane.showMessageDialog(null, new JScrollPane(ta), "Invoice Sementara (Berhasil Check-in)", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Pembayaran dibatalkan. Check-in gagal.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Check-in dibatalkan.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Check-in", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            btnOut.setBackground(new Color(231, 76, 60)); btnOut.setForeground(Color.WHITE);
            btnOut.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    try {
                        String struk = service.prosesCheckOut((int) table.getValueAt(row, 0));
                        JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea(struk)), "Struk Check-out", JOptionPane.INFORMATION_MESSAGE);
                        refreshData(type, model);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Check-out", JOptionPane.ERROR_MESSAGE);
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih reservasi di tabel dulu!");
            });

            JButton btnBill = new JButton("Tagihan Berjalan");
            btnBill.setBackground(new Color(52, 152, 219)); btnBill.setForeground(Color.WHITE);
            btnBill.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    try {
                        int idRes = (int) table.getValueAt(row, 0);
                        String runningBill = service.getTagihanBerjalan(idRes);
                        JTextArea txtArea = new JTextArea(runningBill);
                        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                        txtArea.setEditable(false);
                        JOptionPane.showMessageDialog(null, new JScrollPane(txtArea), "Rincian Tagihan Berjalan (Running Bill)", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Memuat Tagihan Berjalan", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Pilih reservasi aktif di tabel terlebih dahulu!");
                }
            });

            bottomPanel.add(btnIn); bottomPanel.add(btnBill); bottomPanel.add(btnOut);
        }
        else if (type.equals("Restoran")) {
            JButton btnPesan = new JButton("Pesan Untuk Kamar (Reservasi)");
            btnPesan.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    java.util.Vector<String> resList = service.getDropdownReservasiAktif();
                    if (resList.isEmpty()) { JOptionPane.showMessageDialog(null, "Tidak ada Tamu yang sedang Check-in!"); return; }
                    
                    JComboBox<String> cbRes = new JComboBox<>(resList); cbRes.setEditable(true);
                    JComboBox<String> cbMeja = new JComboBox<>(service.getDropdownMejaRestoran()); cbMeja.setEditable(true);
                    JTextField jumlah = new JTextField("1");
                    Object[] msg = {"Pilih Reservasi Tamu Aktif:", cbRes, "Pilih Meja Restoran:", cbMeja, "Berapa Porsi?:", jumlah};
                    if (JOptionPane.showConfirmDialog(null, msg, "Pesan Makanan", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        try {
                            if (cbRes.getSelectedItem() == null || cbMeja.getSelectedItem() == null) throw new Exception("Tamu atau meja harus dipilih.");
                            int idRes = Integer.parseInt(cbRes.getSelectedItem().toString().split(" - ")[0].trim());
                            int idMeja = Integer.parseInt(cbMeja.getSelectedItem().toString().split(" - ")[0].trim());
                            service.pesanRestoran(idRes, idMeja, (int) table.getValueAt(row, 0), Integer.parseInt(jumlah.getText()));
                            JOptionPane.showMessageDialog(null, "Pesanan Restoran Berhasil Masuk ke Tagihan Kamar!");
                        } catch (Exception ex) { JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Pesan Restoran", JOptionPane.ERROR_MESSAGE); }
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih menu makanan di tabel dulu!");
            });

            JButton btnAddMenu = new JButton("Tambah Menu");
            btnAddMenu.addActionListener(e -> {
                JComboBox<String> cbKat = new JComboBox<>(service.getDropdownKategoriMenu()); cbKat.setEditable(true);
                JTextField nama = new JTextField(); JTextField harga = new JTextField();
                Object[] msg = {"Kategori:", cbKat, "Nama Menu:", nama, "Harga (Rp):", harga};
                if (JOptionPane.showConfirmDialog(null, msg, "Tambah Menu", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    try {
                        int idKat = Integer.parseInt(cbKat.getSelectedItem().toString().split(" - ")[0].trim());
                        service.tambahMenuRestoran(idKat, nama.getText().trim(), Integer.parseInt(harga.getText().trim()));
                        refreshData(type, model);
                    } catch (Exception ex) { JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE); }
                }
            });

            JButton btnEditMenu = new JButton("Edit Menu");
            btnEditMenu.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row == -1) { JOptionPane.showMessageDialog(null, "Pilih menu di tabel dulu!"); return; }
                int idMenu = (int) table.getValueAt(row, 0);
                JComboBox<String> cbKat = new JComboBox<>(service.getDropdownKategoriMenu()); cbKat.setEditable(true);
                cbKat.setSelectedItem(table.getValueAt(row, 2).toString());
                JTextField nama = new JTextField(table.getValueAt(row, 1).toString());
                JTextField harga = new JTextField(table.getValueAt(row, 3).toString());
                Object[] msg = {"Kategori:", cbKat, "Nama Menu:", nama, "Harga (Rp):", harga};
                if (JOptionPane.showConfirmDialog(null, msg, "Edit Menu", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    try {
                        int idKat = Integer.parseInt(cbKat.getSelectedItem().toString().split(" - ")[0].trim());
                        service.updateMenuRestoran(idMenu, idKat, nama.getText().trim(), Integer.parseInt(harga.getText().trim()));
                        refreshData(type, model);
                    } catch (Exception ex) { JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE); }
                }
            });

            JButton btnDelMenu = new JButton("Hapus Menu");
            btnDelMenu.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row == -1) { JOptionPane.showMessageDialog(null, "Pilih menu di tabel dulu!"); return; }
                int idMenu = (int) table.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(null, "Hapus menu '" + table.getValueAt(row, 1) + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try {
                        service.hapusMenuRestoran(idMenu);
                        refreshData(type, model);
                    } catch (Exception ex) { JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE); }
                }
            });

            bottomPanel.add(btnPesan); bottomPanel.add(btnAddMenu); bottomPanel.add(btnEditMenu); bottomPanel.add(btnDelMenu);
        }
        else if (type.equals("Layanan")) {
            JButton btnLayanan = new JButton("Pesan Layanan Untuk Tamu");
            btnLayanan.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    java.util.Vector<String> resList = service.getDropdownReservasiAktif();
                    if (resList.isEmpty()) { JOptionPane.showMessageDialog(null, "Tidak ada Tamu yang sedang Check-in!"); return; }
                    
                    JComboBox<String> cbRes = new JComboBox<>(resList); cbRes.setEditable(true);
                    JTextField jumlah = new JTextField("1");
                    Object[] msg = {"Pilih Reservasi Tamu Aktif:", cbRes, "Jumlah (Hari/Kg/Kali):", jumlah};
                    if (JOptionPane.showConfirmDialog(null, msg, "Pesan Layanan", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        try {
                            if (cbRes.getSelectedItem() == null) throw new Exception("Pilih reservasi tamu aktif terlebih dahulu.");
                            int idRes = Integer.parseInt(cbRes.getSelectedItem().toString().split(" - ")[0].trim());
                            service.pesanLayanan(idRes, (int) table.getValueAt(row, 0), Integer.parseInt(jumlah.getText()));
                            JOptionPane.showMessageDialog(null, "Layanan Berhasil Ditambahkan ke Tagihan!");
                        } catch (Exception ex) { JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Pesan Layanan", JOptionPane.ERROR_MESSAGE); }
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih layanan di tabel dulu!");
            });
            bottomPanel.add(btnLayanan);
        }
        else if (type.equals("Fasilitas")) {
            JButton btnFasilitas = new JButton("Catat Penggunaan Fasilitas");
            btnFasilitas.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    java.util.Vector<String> resList = service.getDropdownReservasiAktif();
                    if (resList.isEmpty()) { JOptionPane.showMessageDialog(null, "Tidak ada Tamu yang sedang Check-in!"); return; }
                    
                    JComboBox<String> cbRes = new JComboBox<>(resList); cbRes.setEditable(true);
                    Object[] msg = {"Pilih Reservasi Tamu Aktif:", cbRes};
                    if (JOptionPane.showConfirmDialog(null, msg, "Akses Fasilitas", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        try {
                            if (cbRes.getSelectedItem() == null) throw new Exception("Pilih reservasi tamu aktif terlebih dahulu.");
                            int idRes = Integer.parseInt(cbRes.getSelectedItem().toString().split(" - ")[0].trim());
                            service.gunakanFasilitas(idRes, (int) table.getValueAt(row, 0));
                            JOptionPane.showMessageDialog(null, "Fasilitas Berhasil Digunakan oleh Tamu!");
                        } catch (Exception ex) { JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Catat Fasilitas", JOptionPane.ERROR_MESSAGE); }
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih fasilitas di tabel dulu!");
            });
            bottomPanel.add(btnFasilitas);
        }
        else if (type.equals("Logistik")) {
            JButton btnStok = new JButton("Tambah Stok Barang");
            btnStok.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    JTextField jumlah = new JTextField("1");
                    JTextField hargaSatuan = new JTextField("0");
                    JComboBox<String> cbSupplier = new JComboBox<>(service.getDropdownSupplier());
                    cbSupplier.setEditable(true);
                    Object[] msg = {"Pilih/Ketik Supplier:", cbSupplier, "Jumlah barang masuk:", jumlah, "Harga Satuan (Rp):", hargaSatuan};
                    if (JOptionPane.showConfirmDialog(null, msg, "Tambah Stok", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                        try {
                            int idSupplier = Integer.parseInt(cbSupplier.getSelectedItem().toString().split(" - ")[0].trim());
                            int idKaryawan = currentUser != null ? currentUser.getIdKaryawan() : 1;
                            service.tambahPengadaan((int) table.getValueAt(row, 0), Integer.parseInt(jumlah.getText()), idSupplier, idKaryawan, Integer.parseInt(hargaSatuan.getText().replaceAll("[^0-9]", "")));
                            refreshData(type, model);
                            JOptionPane.showMessageDialog(null, "Stok berhasil diperbarui!");
                        } catch (Exception ex) { JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Tambah Stok", JOptionPane.ERROR_MESSAGE); }
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih barang di tabel dulu!");
            });
            bottomPanel.add(btnStok);
        }
        else if (type.equals("Tagihan")) {
            JButton btnCetak = new JButton("Lihat Struk");
            JButton btnBayar = new JButton("Konfirmasi Bayar");
            JButton btnDel = new JButton("Hapus");

            btnCetak.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    try {
                        String struk = service.cetakInvoice((int) table.getValueAt(row, 2)); 
                        if (!struk.isEmpty()) {
                            JTextArea ta = new JTextArea(struk);
                            ta.setFont(new Font("Consolas", Font.PLAIN, 14)); ta.setEditable(false);
                            ta.setBackground(new Color(250, 250, 250));
                            JOptionPane.showMessageDialog(null, new JScrollPane(ta), "Detail Struk", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Cetak Invoice", JOptionPane.ERROR_MESSAGE);
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih tagihan di tabel dulu!");
            });

            btnBayar.setBackground(new Color(46, 204, 113)); btnBayar.setForeground(Color.WHITE);
            btnBayar.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    if (table.getValueAt(row, 6).toString().equalsIgnoreCase("Lunas")) {
                        JOptionPane.showMessageDialog(null, "Tagihan ini sudah LUNAS!"); return;
                    }
                    String[] metode = {"Tunai", "Transfer Bank", "Kartu Kredit", "QRIS"};
                    String pilihan = (String) JOptionPane.showInputDialog(null, "Pilih Metode Pembayaran:", "Konfirmasi Bayar", JOptionPane.QUESTION_MESSAGE, null, metode, metode[0]);
                    if (pilihan != null) {
                        try {
                            service.konfirmasiPembayaran((int) table.getValueAt(row, 2), (int) table.getValueAt(row, 4), pilihan);
                            refreshData(type, model);
                            JOptionPane.showMessageDialog(null, "Pembayaran LUNAS melalui " + pilihan + "!");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Gagal Konfirmasi Bayar", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih tagihan yang mau dibayar!");
            });

            btnDel.setBackground(new Color(231, 76, 60)); btnDel.setForeground(Color.WHITE);
            btnDel.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    if (JOptionPane.showConfirmDialog(null, "Hapus riwayat tagihan ini secara permanen?", "Hapus Tagihan", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        service.hapusTagihan((int) table.getValueAt(row, 0)); 
                        refreshData(type, model);
                    }
                } else JOptionPane.showMessageDialog(null, "Pilih tagihan!");
            });

            bottomPanel.add(btnCetak); bottomPanel.add(btnBayar); bottomPanel.add(btnDel);
        }

        if (type.equals("Kamar")) {
            logistikTable.setRowHeight(30); logistikTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            ResultSet rsInv = null;
            try {
                rsInv = service.getInventaris();
                while (rsInv.next()) logistikModel.addRow(new Object[]{rsInv.getInt(1), rsInv.getString(2), rsInv.getInt(3), rsInv.getString(4)});
            } catch (Exception ex) {
            } finally {
                KoneksiDB.closeQuietly(rsInv);
            }

            lblLog.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblLog.setOpaque(true); lblLog.setBackground(new Color(44, 62, 80)); lblLog.setForeground(Color.WHITE);

            table.getSelectionModel().addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        try {
                            int idKamar = (int) table.getValueAt(selectedRow, 0);
                            String noKamar = table.getValueAt(selectedRow, 1).toString();
                            lblLog.setText(" 📦 Alokasi Inventaris di Kamar " + noKamar + " (Terpasang)");
                            logistikModel.setColumnIdentifiers(new String[]{"ID Barang", "Nama Barang Terpasang", "Jumlah Terpasang", "Satuan"});
                            logistikModel.setRowCount(0);
                            java.util.List<KamarInventaris> listKi = service.getInventarisKamar(idKamar);
                            for (KamarInventaris ki : listKi) {
                                logistikModel.addRow(new Object[]{
                                    ki.getInventaris().getIdBarang(),
                                    ki.getInventaris().getNamaBarang(),
                                    ki.getJumlahTerpasang(),
                                    ki.getInventaris().getSatuan()
                                });
                            }
                        } catch (Exception ex) {}
                    }
                }
            });

            JPanel logPanel = new JPanel(new BorderLayout());
            logPanel.add(lblLog, BorderLayout.NORTH);
            logPanel.add(new JScrollPane(logistikTable), BorderLayout.CENTER);

            JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), logPanel);
            split.setResizeWeight(0.65);
            split.setBorder(null);

            panel.add(split, BorderLayout.CENTER);
            
            for (java.awt.event.ActionListener al : btnRefresh.getActionListeners()) btnRefresh.removeActionListener(al);
            btnRefresh.addActionListener(e -> {
                refreshData(type, model);
                table.clearSelection();
                lblLog.setText(" Info Stok Logistik Tersedia (Gudang)");
                logistikModel.setColumnIdentifiers(new String[]{"ID Barang", "Nama Barang Logistik", "Sisa Stok", "Satuan"});
                logistikModel.setRowCount(0);
                ResultSet rs = null;
                try {
                    rs = service.getInventaris();
                    while (rs.next()) logistikModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4)});
                } catch (Exception ex) {
                } finally {
                    KoneksiDB.closeQuietly(rs);
                }
            });
        } else {
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
        }
        
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshData(String type, DefaultTableModel model) {
        model.setRowCount(0);
        ResultSet rs = null;
        try {
            if (type.equals("Kamar")) rs = service.getTabelKamar();
            else if (type.equals("Tamu")) rs = service.getTamu();
            else if (type.equals("Reservasi")) rs = service.getReservasiAktif();
            else if (type.equals("Restoran")) rs = service.getMenuRestoran();
            else if (type.equals("Layanan")) rs = service.getLayananHotel();
            else if (type.equals("Fasilitas")) {
                rs = service.getFasilitasUmum();
                while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
            else if (type.equals("Logistik")) {
                rs = service.getInventaris();
                while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4)});
            }
            else if (type.equals("Tagihan")) {
                rs = service.getTagihan();
                while (rs.next()) model.addRow(new Object[]{rs.getInt("id_invoice"), rs.getString("no_invoice"), rs.getInt("id_reservasi"), rs.getString("nama"), rs.getInt("total_tagihan"), rs.getString("tanggal_buat"), rs.getString("status")});
            }
            else if (type.equals("LaporanAgregasi")) {
                rs = service.getLaporanPendapatanPerTipe();
                while (rs.next()) model.addRow(new Object[]{
                    rs.getString("nama_tipe"),
                    rs.getInt("jumlah_reservasi"),
                    String.format("Rp %,d", rs.getLong("total_pendapatan")),
                    String.format("Rp %,d", rs.getLong("rata_rata"))
                });
            }

            if (rs != null && !type.equals("Fasilitas") && !type.equals("Logistik") && !type.equals("Tagihan") && !type.equals("LaporanAgregasi")) {
                int cols = model.getColumnCount();
                while (rs.next()) {
                    Object[] row = new Object[cols];
                    for (int i = 0; i < cols; i++) row[i] = rs.getObject(i + 1);
                    model.addRow(row);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally {
            KoneksiDB.closeQuietly(rs);
        }
    }

    public static void main(String[] args) {
        
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    
                    
                    UIManager.put("nimbusBase", new Color(41, 128, 185));
                    UIManager.put("nimbusBlueGrey", new Color(236, 240, 241));
                    UIManager.put("control", new Color(255, 255, 255));
                    break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> {
            Main login = new Main();
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        });
    }
}
