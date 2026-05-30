import java.sql.Date;

public class Reservasi {
    private int idReservasi;
    private Tamu tamu;
    private Kamar kamar;
    private Karyawan karyawan;
    private Date tanggalCheckin;
    private Date tanggalCheckout;
    private String statusReservasi;

    public Reservasi() {}

    public Reservasi(int id, Tamu tamu, Kamar kamar, Karyawan karyawan,
                     Date checkin, Date checkout, String status) {
        this.idReservasi = id;
        this.tamu = tamu;
        this.kamar = kamar;
        this.karyawan = karyawan;
        this.tanggalCheckin = checkin;
        this.tanggalCheckout = checkout;
        this.statusReservasi = status;
    }

    public int getIdReservasi() {
        return idReservasi;
    }

    public void setIdReservasi(int id) {
        this.idReservasi = id;
    }

    public Tamu getTamu() {
        return tamu;
    }

    public void setTamu(Tamu tamu) {
        this.tamu = tamu;
    }

    public Kamar getKamar() {
        return kamar;
    }

    public void setKamar(Kamar kamar) {
        this.kamar = kamar;
    }

    public Karyawan getKaryawan() {
        return karyawan;
    }

    public void setKaryawan(Karyawan karyawan) {
        this.karyawan = karyawan;
    }

    public Date getTanggalCheckin() {
        return tanggalCheckin;
    }

    public void setTanggalCheckin(Date tgl) {
        this.tanggalCheckin = tgl;
    }

    public Date getTanggalCheckout() {
        return tanggalCheckout;
    }

    public void setTanggalCheckout(Date tgl) {
        this.tanggalCheckout = tgl;
    }

    public String getStatusReservasi() {
        return statusReservasi;
    }

    public void setStatusReservasi(String status) {
        this.statusReservasi = status;
    }
}
