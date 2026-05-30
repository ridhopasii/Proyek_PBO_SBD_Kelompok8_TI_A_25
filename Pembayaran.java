import java.sql.Timestamp;

public class Pembayaran {
    private int idPembayaran;
    private int idReservasi;
    private Timestamp tanggalBayar;
    private int jumlahBayar;
    private String metodeBayar;
    private String statusBayar;

    public Pembayaran() {}

    public Pembayaran(int id, int idReservasi, Timestamp tanggal, int jumlah, String metode, String status) {
        this.idPembayaran = id;
        this.idReservasi = idReservasi;
        this.tanggalBayar = tanggal;
        this.jumlahBayar = jumlah;
        this.metodeBayar = metode;
        this.statusBayar = status;
    }

    public int getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(int id) {
        this.idPembayaran = id;
    }

    public int getIdReservasi() {
        return idReservasi;
    }

    public void setIdReservasi(int idReservasi) {
        this.idReservasi = idReservasi;
    }

    public Timestamp getTanggalBayar() {
        return tanggalBayar;
    }

    public void setTanggalBayar(Timestamp tanggal) {
        this.tanggalBayar = tanggal;
    }

    public int getJumlahBayar() {
        return jumlahBayar;
    }

    public void setJumlahBayar(int jumlah) {
        this.jumlahBayar = jumlah;
    }

    public String getMetodeBayar() {
        return metodeBayar;
    }

    public void setMetodeBayar(String metode) {
        this.metodeBayar = metode;
    }

    public String getStatusBayar() {
        return statusBayar;
    }

    public void setStatusBayar(String status) {
        this.statusBayar = status;
    }
}
