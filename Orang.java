public class Orang {
    protected String nama;

    public Orang() {
        this.nama = "";
    }

    public Orang(String nm) {
        this.nama = nm;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nm) {
        this.nama = nm;
    }

    public void tampilkanProfil() {
        System.out.println("Nama: " + nama);
    }
}
