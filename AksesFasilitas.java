import java.sql.Timestamp;

public class AksesFasilitas {
    private int idAkses;
    private int idReservasi;
    private int idFasilitas;
    private Timestamp waktuMasuk;

    public AksesFasilitas() {}

    public AksesFasilitas(int id, int idReservasi, int idFasilitas, Timestamp waktu) {
        this.idAkses = id;
        this.idReservasi = idReservasi;
        this.idFasilitas = idFasilitas;
        this.waktuMasuk = waktu;
    }

    public int getIdAkses() {
        return idAkses;
    }

    public void setIdAkses(int id) {
        this.idAkses = id;
    }

    public int getIdReservasi() {
        return idReservasi;
    }

    public void setIdReservasi(int idReservasi) {
        this.idReservasi = idReservasi;
    }

    public int getIdFasilitas() {
        return idFasilitas;
    }

    public void setIdFasilitas(int idFasilitas) {
        this.idFasilitas = idFasilitas;
    }

    public Timestamp getWaktuMasuk() {
        return waktuMasuk;
    }

    public void setWaktuMasuk(Timestamp waktu) {
        this.waktuMasuk = waktu;
    }
}
