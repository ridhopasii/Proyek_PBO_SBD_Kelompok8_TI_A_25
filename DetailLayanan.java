import java.sql.Timestamp;

// Merepresentasikan tabel detail_layanan_reservasi
public class DetailLayanan {
    private int idDetail;
    private int idReservasi;
    private int idLayanan;
    private int jumlah;
    private Timestamp waktuPesan;

    public DetailLayanan() {}

    public DetailLayanan(int idDetail, int idReservasi, int idLayanan, int jumlah, Timestamp waktuPesan) {
        this.idDetail = idDetail;
        this.idReservasi = idReservasi;
        this.idLayanan = idLayanan;
        this.jumlah = jumlah;
        this.waktuPesan = waktuPesan;
    }

    public int getIdDetail() { return idDetail; }
    public void setIdDetail(int id) { this.idDetail = id; }

    public int getIdReservasi() { return idReservasi; }
    public void setIdReservasi(int idReservasi) { this.idReservasi = idReservasi; }

    public int getIdLayanan() { return idLayanan; }
    public void setIdLayanan(int idLayanan) { this.idLayanan = idLayanan; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public Timestamp getWaktuPesan() { return waktuPesan; }
    public void setWaktuPesan(Timestamp waktuPesan) { this.waktuPesan = waktuPesan; }
}
