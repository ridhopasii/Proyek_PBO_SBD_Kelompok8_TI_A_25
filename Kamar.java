public class Kamar {
    private int idKamar;
    private TipeKamar tipeKamar;
    private String nomorKamar;
    private int lantai;
    private String status;

    public Kamar() {}

    public Kamar(int idKamar, TipeKamar tipeKamar, String nomorKamar, int lantai, String status) {
        this.idKamar = idKamar;
        this.tipeKamar = tipeKamar;
        this.nomorKamar = nomorKamar;
        this.lantai = lantai;
        this.status = status;
    }

    // Constructor tanpa lantai, untuk backward compatibility
    public Kamar(int idKamar, TipeKamar tipeKamar, String nomorKamar, String status) {
        this.idKamar = idKamar;
        this.tipeKamar = tipeKamar;
        this.nomorKamar = nomorKamar;
        this.status = status;
    }

    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int id) { this.idKamar = id; }

    public TipeKamar getTipeKamar() { return tipeKamar; }
    public void setTipeKamar(TipeKamar tipe) { this.tipeKamar = tipe; }

    public String getNomorKamar() { return nomorKamar; }
    public void setNomorKamar(String nomor) { this.nomorKamar = nomor; }

    public int getLantai() { return lantai; }
    public void setLantai(int lantai) { this.lantai = lantai; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
