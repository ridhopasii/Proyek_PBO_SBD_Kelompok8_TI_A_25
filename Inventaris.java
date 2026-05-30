public class Inventaris {
    private int idBarang;
    private String namaBarang;
    private int stokSekarang;
    private String satuan;

    public Inventaris() {}

    public Inventaris(int idBarang, String namaBarang, int stokSekarang, String satuan) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.stokSekarang = stokSekarang;
        this.satuan = satuan;
    }

    public int getIdBarang() { return idBarang; }
    public void setIdBarang(int id) { this.idBarang = id; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String nama) { this.namaBarang = nama; }

    public int getStokSekarang() { return stokSekarang; }
    public void setStokSekarang(int stok) { this.stokSekarang = stok; }

    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }

    public String getInfo() {
        return namaBarang + " (Stok: " + stokSekarang + " " + satuan + ")";
    }
}
