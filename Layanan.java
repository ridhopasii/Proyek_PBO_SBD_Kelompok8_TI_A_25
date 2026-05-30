public class Layanan {
    private int idLayanan;
    private String namaLayanan;
    private int harga;

    public Layanan() {}

    public Layanan(int id, String nama, int harga) {
        this.idLayanan = id;
        this.namaLayanan = nama;
        this.harga = harga;
    }

    public int getIdLayanan() {
        return idLayanan;
    }

    public void setIdLayanan(int id) {
        this.idLayanan = id;
    }

    public String getNamaLayanan() {
        return namaLayanan;
    }

    public void setNamaLayanan(String nama) {
        this.namaLayanan = nama;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    @Override
    public String toString() {
        return namaLayanan + " - Rp " + harga;
    }
}
