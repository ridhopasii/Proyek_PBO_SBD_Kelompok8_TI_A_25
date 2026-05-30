import java.sql.Timestamp;

// Merepresentasikan tabel pesanan_restoran (order header)
// Detail item ada di tabel detail_pesanan_restoran
public class PesananRestoran {
    private int idPesanan;
    private int idReservasi;
    private int idMeja;
    private Timestamp waktuPesan;
    private int totalHarga;

    public PesananRestoran() {}

    public PesananRestoran(int idPesanan, int idReservasi, int idMeja, Timestamp waktuPesan, int totalHarga) {
        this.idPesanan = idPesanan;
        this.idReservasi = idReservasi;
        this.idMeja = idMeja;
        this.waktuPesan = waktuPesan;
        this.totalHarga = totalHarga;
    }

    public int getIdPesanan() { return idPesanan; }
    public void setIdPesanan(int id) { this.idPesanan = id; }

    public int getIdReservasi() { return idReservasi; }
    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }

    public int getIdMeja() { return idMeja; }
    public void setIdMeja(int idMeja) { this.idMeja = idMeja; }

    public Timestamp getWaktuPesan() { return waktuPesan; }
    public void setWaktuPesan(Timestamp waktuPesan) { this.waktuPesan = waktuPesan; }

    public int getTotalHarga() { return totalHarga; }
    public void setTotalHarga(int totalHarga) { this.totalHarga = totalHarga; }
}
