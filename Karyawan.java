public class Karyawan extends Orang {
    private int idKaryawan;
    private int idJabatan;
    private String namaJabatan;
    private String username;
    private String password;
    private String statusAktif;

    public Karyawan() {
        super();
    }

    public Karyawan(int idKaryawan, String nama, int idJabatan, String namaJabatan,
                    String username, String password, String statusAktif) {
        super(nama);
        this.idKaryawan = idKaryawan;
        this.idJabatan = idJabatan;
        this.namaJabatan = namaJabatan;
        this.username = username;
        this.password = password;
        this.statusAktif = statusAktif;
    }

    public int getIdKaryawan() { return idKaryawan; }
    public void setIdKaryawan(int id) { this.idKaryawan = id; }

    public int getIdJabatan() { return idJabatan; }
    public void setIdJabatan(int idJabatan) { this.idJabatan = idJabatan; }

    public String getNamaJabatan() { return namaJabatan; }
    public void setNamaJabatan(String namaJabatan) { this.namaJabatan = namaJabatan; }

    public String getNamaKaryawan() { return getNama(); }
    public void setNamaKaryawan(String nama) { setNama(nama); }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatusAktif() { return statusAktif; }
    public void setStatusAktif(String statusAktif) { this.statusAktif = statusAktif; }

    public boolean isAktif() { return "aktif".equalsIgnoreCase(statusAktif); }

    // Backward compatibility — dipakai di beberapa tempat lama
    public String getJabatan() { return namaJabatan; }
    public void setJabatan(String jabatan) { this.namaJabatan = jabatan; }

    @Override
    public void tampilkanProfil() {
        System.out.println("Karyawan: " + getNama() + " (" + namaJabatan + ")");
    }
}
