import java.sql.Time;

public class Fasilitas {
    private int idFasilitas;
    private String namaFasilitas;
    private Time jamBuka;
    private Time jamTutup;

    public Fasilitas() {}

    public Fasilitas(int id, String nama, Time buka, Time tutup) {
        this.idFasilitas = id;
        this.namaFasilitas = nama;
        this.jamBuka = buka;
        this.jamTutup = tutup;
    }

    public int getIdFasilitas() {
        return idFasilitas;
    }

    public void setIdFasilitas(int id) {
        this.idFasilitas = id;
    }

    public String getNamaFasilitas() {
        return namaFasilitas;
    }

    public void setNamaFasilitas(String nama) {
        this.namaFasilitas = nama;
    }

    public Time getJamBuka() {
        return jamBuka;
    }

    public void setJamBuka(Time buka) {
        this.jamBuka = buka;
    }

    public Time getJamTutup() {
        return jamTutup;
    }

    public void setJamTutup(Time tutup) {
        this.jamTutup = tutup;
    }

    public String getJamOperasional() {
        return jamBuka + " - " + jamTutup;
    }
}
