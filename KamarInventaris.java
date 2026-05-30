public class KamarInventaris {
    private Kamar kamar;
    private Inventaris inventaris;
    private int jumlahTerpasang;

    public KamarInventaris() {}

    public KamarInventaris(Kamar kamar, Inventaris inventaris, int jumlah) {
        this.kamar = kamar;
        this.inventaris = inventaris;
        this.jumlahTerpasang = jumlah;
    }

    public Kamar getKamar() {
        return kamar;
    }

    public void setKamar(Kamar kamar) {
        this.kamar = kamar;
    }

    public Inventaris getInventaris() {
        return inventaris;
    }

    public void setInventaris(Inventaris inventaris) {
        this.inventaris = inventaris;
    }

    public int getJumlahTerpasang() {
        return jumlahTerpasang;
    }

    public void setJumlahTerpasang(int jumlah) {
        this.jumlahTerpasang = jumlah;
    }
}
