public class Tamu extends Orang {
    private int idTamu;
    private String identitasTipe;
    private String identitasNo;
    private String noHp;
    private String email;
    private String alamat;

    public Tamu() {
        super();
    }

    public Tamu(int idTamu, String nama, String identitasTipe, String identitasNo,
                String noHp, String email, String alamat) {
        super(nama);
        this.idTamu = idTamu;
        this.identitasTipe = identitasTipe;
        this.identitasNo = identitasNo;
        this.noHp = noHp;
        this.email = email;
        this.alamat = alamat;
    }

    public int getIdTamu() { return idTamu; }
    public void setIdTamu(int idTamu) { this.idTamu = idTamu; }

    public String getIdentitasTipe() { return identitasTipe; }
    public void setIdentitasTipe(String identitasTipe) { this.identitasTipe = identitasTipe; }

    public String getIdentitasNo() { return identitasNo; }
    public void setIdentitasNo(String identitasNo) { this.identitasNo = identitasNo; }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    @Override
    public void tampilkanProfil() {
        System.out.println("Tamu: " + getNama() + " | " + identitasTipe + ": " + identitasNo + " | HP: " + noHp);
    }
}
