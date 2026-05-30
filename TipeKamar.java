public class TipeKamar {
    private int idTipe;
    private String namaTipe;
    private int kapasitasOrang;
    private int hargaPerMalam;

    public TipeKamar() {}

    public TipeKamar(int id, String nama, int kapasitas, int harga) {
        this.idTipe = id;
        this.namaTipe = nama;
        this.kapasitasOrang = kapasitas;
        this.hargaPerMalam = harga;
    }

    public int getIdTipe() {
        return idTipe;
    }

    public void setIdTipe(int id) {
        this.idTipe = id;
    }

    public String getNamaTipe() {
        return namaTipe;
    }

    public void setNamaTipe(String nama) {
        this.namaTipe = nama;
    }

    public int getKapasitasOrang() {
        return kapasitasOrang;
    }

    public void setKapasitasOrang(int kapasitas) {
        this.kapasitasOrang = kapasitas;
    }

    public int getHargaPerMalam() {
        return hargaPerMalam;
    }

    public void setHargaPerMalam(int harga) {
        this.hargaPerMalam = harga;
    }

    public String getInfoTipe() {
        return namaTipe + " (" + kapasitasOrang + " orang) - Rp " + hargaPerMalam + "/malam";
    }
}
