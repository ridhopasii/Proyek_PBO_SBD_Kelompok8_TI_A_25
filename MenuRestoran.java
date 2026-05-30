// Merepresentasikan tabel menu_restoran (JOIN kategori_menu untuk namaKategori)
public class MenuRestoran {
    private int idMenu;
    private int idKategori;
    private String namaKategori;
    private String namaMenu;
    private int harga;

    public MenuRestoran() {}

    public MenuRestoran(int idMenu, int idKategori, String namaKategori, String namaMenu, int harga) {
        this.idMenu = idMenu;
        this.idKategori = idKategori;
        this.namaKategori = namaKategori;
        this.namaMenu = namaMenu;
        this.harga = harga;
    }

    public int getIdMenu() { return idMenu; }
    public void setIdMenu(int id) { this.idMenu = id; }

    public int getIdKategori() { return idKategori; }
    public void setIdKategori(int idKategori) { this.idKategori = idKategori; }

    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }

    public String getNamaMenu() { return namaMenu; }
    public void setNamaMenu(String nama) { this.namaMenu = nama; }

    public int getHarga() { return harga; }
    public void setHarga(int harga) { this.harga = harga; }

    public String getDisplayName() {
        return namaMenu + " (" + namaKategori + ") - Rp " + harga;
    }
}
