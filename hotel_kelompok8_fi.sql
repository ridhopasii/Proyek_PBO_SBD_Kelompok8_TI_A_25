drop database if exists hotel;
create database hotel;
\c hotel

create schema logistik;

create table jabatan (
    id_jabatan serial primary key,
    nama_jabatan varchar(50) not null,
    departemen varchar(50)
);

create table karyawan (
    id_karyawan serial primary key,
    nama_karyawan varchar(100) not null,
    id_jabatan int references jabatan(id_jabatan) on delete set null,
    username varchar(50) unique not null,
    password varchar(100) not null,
    status_aktif varchar(20) default 'aktif'
);

create table tamu (
    id_tamu serial primary key,
    nama varchar(100) not null,
    identitas_tipe varchar(20),
    identitas_no varchar(50) unique,
    no_hp varchar(20),
    email varchar(100),
    alamat text,
    username varchar(50) unique,
    password varchar(100)
);

create table tipe_kamar (
    id_tipe serial primary key,
    nama_tipe varchar(50) not null,
    kapasitas_orang int default 2,
    harga_per_malam numeric(12,2) not null
);

create table kamar (
    id_kamar serial primary key,
    id_tipe int references tipe_kamar(id_tipe) on delete restrict,
    nomor_kamar varchar(10) unique not null,
    lantai int,
    status varchar(20) default 'Tersedia'
);

create table reservasi (
    id_reservasi serial primary key,
    id_tamu int references tamu(id_tamu) on delete cascade,
    id_kamar int references kamar(id_kamar) on delete cascade,
    id_karyawan int references karyawan(id_karyawan) on delete set null,
    tanggal_checkin date not null,
    tanggal_checkout date not null,
    status_reservasi varchar(20) default 'Check-in' CHECK (status_reservasi IN ('Check-in','Selesai','Batal'))
);

create table kategori_menu (
    id_kategori serial primary key,
    nama_kategori varchar(50) not null
);

create table meja_restoran (
    id_meja serial primary key,
    nomor_meja varchar(10) not null,
    kapasitas int
);

create table menu_restoran (
    id_menu serial primary key,
    id_kategori int references kategori_menu(id_kategori),
    nama_menu varchar(100) not null,
    harga numeric(12,2) not null
);

create table pesanan_restoran (
    id_pesanan serial primary key,
    id_reservasi int references reservasi(id_reservasi),
    id_meja int references meja_restoran(id_meja),
    waktu_pesan timestamp default current_timestamp,
    total_harga numeric(12,2) default 0
);

create table detail_pesanan_restoran (
    id_detail_pesanan serial primary key,
    id_pesanan int references pesanan_restoran(id_pesanan),
    id_menu int references menu_restoran(id_menu),
    jumlah int not null,
    subtotal numeric(12,2)
);

create table fasilitas_umum (
    id_fasilitas serial primary key,
    nama_fasilitas varchar(100) not null,
    jam_buka time,
    jam_tutup time
);

create table akses_fasilitas (
    id_akses serial primary key,
    id_reservasi int references reservasi(id_reservasi),
    id_fasilitas int references fasilitas_umum(id_fasilitas),
    waktu_masuk timestamp default current_timestamp
);

create table layanan_hotel (
    id_layanan serial primary key,
    nama_layanan varchar(100) not null,
    harga numeric(12,2) not null
);

create table detail_layanan_reservasi (
    id_detail_layanan serial primary key,
    id_reservasi int references reservasi(id_reservasi),
    id_layanan int references layanan_hotel(id_layanan),
    jumlah int default 1,
    waktu_pesan timestamp default current_timestamp
);

create table logistik.inventaris (
    id_barang serial primary key,
    nama_barang varchar(100) not null,
    stok_sekarang int default 0,
    satuan varchar(20)
);

create table supplier (
    id_supplier serial primary key,
    nama_supplier varchar(100) not null,
    kontak_person varchar(100),
    no_hp varchar(20),
    email varchar(100),
    alamat text
);

create table purchase_order (
    id_po serial primary key,
    id_supplier int references supplier(id_supplier),
    id_karyawan int references karyawan(id_karyawan),
    tanggal_order date default current_date,
    total_biaya numeric(12,2) default 0,
    note text
);

create table detail_po (
    id_detail_po serial primary key,
    id_po int references purchase_order(id_po),
    id_barang int references logistik.inventaris(id_barang),
    jumlah int not null,
    harga_satuan numeric(12,2) not null
);

create table pembayaran (
    id_pembayaran serial primary key,
    id_reservasi int references reservasi(id_reservasi),
    tanggal_bayar timestamp default current_timestamp,
    jumlah_bayar numeric(12,2) not null,
    metode_bayar varchar(50),
    status_pembayaran varchar(20) default 'Lunas' CHECK (status_pembayaran IN ('Lunas','Belum Bayar','Pending'))
);

create table invoice (
    id_invoice serial primary key,
    no_invoice varchar(20) unique not null,
    id_reservasi int references reservasi(id_reservasi),
    total_kamar numeric(12,2) default 0,
    total_restoran numeric(12,2) default 0,
    total_layanan numeric(12,2) default 0,
    total_tagihan numeric(12,2) default 0,
    tanggal_buat timestamp default current_timestamp,
    note text
);

-- Tambahan constraints/cek untuk beberapa tabel
ALTER TABLE kamar
    ADD CONSTRAINT chk_status_kamar CHECK (status IN ('Tersedia','Terisi','Maintenance','Dibersihkan'));

-- Indeks untuk performa query GUI/backend
CREATE INDEX IF NOT EXISTS idx_kamar_status ON kamar(status);
CREATE INDEX IF NOT EXISTS idx_reservasi_tanggal ON reservasi(tanggal_checkin);
CREATE INDEX IF NOT EXISTS idx_pesanan_waktu ON pesanan_restoran(waktu_pesan DESC);
CREATE INDEX IF NOT EXISTS idx_invoice_reservasi ON invoice(id_reservasi);

-- Trigger: setelah insert reservasi, set kamar menjadi Terisi
CREATE OR REPLACE FUNCTION fn_after_insert_reservasi()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE kamar SET status = 'Terisi' WHERE id_kamar = NEW.id_kamar;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_after_insert_reservasi
AFTER INSERT ON reservasi
FOR EACH ROW EXECUTE FUNCTION fn_after_insert_reservasi();

-- Trigger: setelah update status_reservasi -> jika Selesai, set kamar Tersedia
CREATE OR REPLACE FUNCTION fn_after_update_reservasi()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status_reservasi IN ('Selesai', 'Batal') THEN
        UPDATE kamar SET status = 'Tersedia' WHERE id_kamar = NEW.id_kamar;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_after_update_reservasi
AFTER UPDATE OF status_reservasi ON reservasi
FOR EACH ROW WHEN (OLD.status_reservasi IS DISTINCT FROM NEW.status_reservasi)
EXECUTE FUNCTION fn_after_update_reservasi();

-- View: detail invoice untuk GUI (dipakai di Java `getTagihan`)
CREATE OR REPLACE VIEW v_invoice_detail AS
SELECT
    i.id_invoice,
    i.no_invoice,
    i.id_reservasi,
    t.nama AS nama_tamu,
    i.total_tagihan,
    i.tanggal_buat,
    CASE 
        WHEN NOT EXISTS (SELECT 1 FROM pembayaran p WHERE p.id_reservasi = i.id_reservasi) THEN 'Belum Bayar'
        WHEN (SELECT COALESCE(SUM(p.jumlah_bayar), 0) FROM pembayaran p WHERE p.id_reservasi = i.id_reservasi AND p.status_pembayaran = 'Lunas') >= i.total_tagihan THEN 'Lunas'
        ELSE 'Sebagian'
    END AS status_bayar
FROM invoice i
JOIN reservasi r ON r.id_reservasi = i.id_reservasi
JOIN tamu t ON r.id_tamu = t.id_tamu
ORDER BY i.id_invoice DESC;

-- View: ringkasan dashboard kamar (dipakai di GUI)
CREATE OR REPLACE VIEW v_dashboard_hotel AS
SELECT 
    (SELECT COUNT(*) FROM kamar) AS total_kamar,
        (SELECT COUNT(*) FROM kamar WHERE status = 'Tersedia') AS kamar_kosong,
        (SELECT COUNT(*) FROM kamar WHERE status = 'Terisi') AS kamar_terisi,
    (SELECT COUNT(*) FROM tamu) AS total_tamu;


create view v_laporan_lengkap_reservasi as
select 
    r.id_reservasi,
    t.nama as nama_tamu,
    k.nomor_kamar,
    tk.nama_tipe as tipe_kamar,
    tk.harga_per_malam,
    r.tanggal_checkin,
    r.status_reservasi
from reservasi r
join tamu t on r.id_tamu = t.id_tamu
join kamar k on r.id_kamar = k.id_kamar
join tipe_kamar tk on k.id_tipe = tk.id_tipe;

-- View agregasi: pendapatan per tipe kamar (GROUP BY + HAVING + COUNT/SUM/AVG)
-- Dipakai aplikasi (Java getLaporanPendapatanPerTipe) untuk menu Laporan & tab GUI.
create or replace view v_pendapatan_per_tipe as
select
    tk.nama_tipe,
    count(r.id_reservasi) as jumlah_reservasi,
    sum(i.total_tagihan) as total_pendapatan,
    round(avg(i.total_tagihan), 0) as rata_rata
from reservasi r
join kamar k on r.id_kamar = k.id_kamar
join tipe_kamar tk on k.id_tipe = tk.id_tipe
join invoice i on i.id_reservasi = r.id_reservasi
group by tk.nama_tipe
having sum(i.total_tagihan) > 0
order by total_pendapatan desc;

insert into jabatan (nama_jabatan, departemen) values ('resepsionis', 'front office'), ('manager', 'management'), ('kasir', 'finance');
insert into karyawan (nama_karyawan, id_jabatan, username, password) values
('Nabil', 1, 'nabil', 'usu123'),
('Mutiara', 1, 'mutiara', 'usu123'),
('Sevin', 1, 'sevin', 'usu123'),
('Ridho', 1, 'ridho', 'usu123'),
('Nela', 1, 'nela', 'usu123');
insert into tipe_kamar (nama_tipe, kapasitas_orang, harga_per_malam) values ('standard room', 2, 350000), ('deluxe room', 2, 750000);
insert into kamar (id_tipe, nomor_kamar, lantai) values (1, '101', 1), (1, '102', 1), (2, '201', 2);
insert into kategori_menu (nama_kategori) values ('makanan'), ('minuman');
insert into menu_restoran (id_kategori, nama_menu, harga) values (1, 'nasi goreng', 25000), (2, 'es teh', 5000);
insert into logistik.inventaris (nama_barang, stok_sekarang, satuan) values ('sabun', 100, 'pcs');
insert into supplier (nama_supplier) values ('pt sumber makmur');
insert into tamu (nama, identitas_tipe, identitas_no) values ('hendra tarigan', 'ktp', '123456');

begin;
insert into reservasi (id_tamu, id_kamar, id_karyawan, tanggal_checkin, tanggal_checkout) 
values (1, 3, 1, '2026-06-01', '2026-06-03');
commit;

select * from v_laporan_lengkap_reservasi where nama_tamu like '%hendra%';

-- Data contoh tambahan: invoice & pembayaran untuk reservasi sampel (id_reservasi = 1)
INSERT INTO invoice (no_invoice, id_reservasi, total_kamar, total_restoran, total_layanan, total_tagihan)
VALUES ('INV-TEST0', 1, 700000, 50000, 100000, 850000);

INSERT INTO pembayaran (id_reservasi, jumlah_bayar, metode_bayar, status_pembayaran)
VALUES (1, 850000, 'Tunai', 'Lunas');

-- =========================================================
-- Dummy Data Sumatera Utara untuk demo GUI/CLI
-- Area: Medan, Karo, Samosir, Pematang Siantar, Binjai
-- =========================================================

INSERT INTO jabatan (id_jabatan, nama_jabatan, departemen) VALUES
(4, 'assistant resepsionis', 'front office'),
(5, 'security', 'security');

SELECT setval(pg_get_serial_sequence('jabatan', 'id_jabatan'), (SELECT MAX(id_jabatan) FROM jabatan));

INSERT INTO tipe_kamar (nama_tipe, kapasitas_orang, harga_per_malam) VALUES
('family suite danau toba', 4, 1200000),
('executive room medan', 2, 950000),
('lake view deluxe samosir', 2, 1100000);

INSERT INTO kamar (id_tipe, nomor_kamar, lantai) VALUES
(3, '202', 2),
(4, '203', 2),
(5, '301', 3),
(3, '302', 3),
(4, '303', 3);

INSERT INTO kategori_menu (nama_kategori) VALUES
('snack khas'),
('kopi & teh');

INSERT INTO menu_restoran (id_kategori, nama_menu, harga) VALUES
(1, 'bika ambon medan', 22000),
(1, 'arsik ikan mas toba', 65000),
(1, 'soto medan', 35000),
(2, 'kopi sidikalang', 18000),
(2, 'teh tarik medan', 15000);

INSERT INTO meja_restoran (nomor_meja, kapasitas) VALUES
('M01', 2),
('M02', 4),
('M03', 4),
('M04', 6);

INSERT INTO fasilitas_umum (nama_fasilitas, jam_buka, jam_tutup) VALUES
('Kolam Renang Panorama Toba', '07:00:00', '21:00:00'),
('Spa Tradisional Karo', '09:00:00', '22:00:00'),
('Fitness Center Medan', '06:00:00', '23:00:00'),
('Shuttle Bandara Kualanamu', '05:00:00', '23:30:00'),
('Rooftop Lounge Parapat', '16:00:00', '23:59:59');

INSERT INTO layanan_hotel (nama_layanan, harga) VALUES
('Laundry Express', 45000),
('Antar Jemput Kualanamu', 150000),
('Tur Danau Toba', 350000),
('Extra Bed', 175000),
('Dekorasi Honeymoon', 250000);

INSERT INTO logistik.inventaris (nama_barang, stok_sekarang, satuan) VALUES
('handuk bordir hotel', 120, 'pcs'),
('sabun cair aromatik', 200, 'botol'),
('air mineral botol', 300, 'botol'),
('sprei putih premium', 75, 'set'),
('tissue bathroom', 180, 'roll');

INSERT INTO supplier (nama_supplier, kontak_person, no_hp, email, alamat) VALUES
('CV Danau Toba Supply', 'Rudi Nababan', '081234567801', 'admin@danautoba-supply.id', 'Jl. Parapat No. 12, Simalungun'),
('PT Karo Sejahtera', 'Maya Br Ginting', '081234567802', 'sales@karosejahtera.id', 'Jl. Jamin Ginting, Berastagi'),
('UD Medan Hospitality', 'Andi Tarigan', '081234567803', 'cs@medanhospitality.id', 'Jl. Gatot Subroto, Medan');

INSERT INTO tamu (nama, identitas_tipe, identitas_no, no_hp, email, alamat) VALUES
('Budi Tarigan', 'KTP', '1201010101010001', '081200000001', 'budi.tarigan@mail.id', 'Medan'),
('Maya Sihombing', 'KTP', '1201010101010002', '081200000002', 'maya.sihombing@mail.id', 'Pematang Siantar'),
('Rudi Ginting', 'SIM', 'SIM1201010003', '081200000003', 'rudi.ginting@mail.id', 'Berastagi'),
('Lina Nainggolan', 'KTP', '1201010101010004', '081200000004', 'lina.nainggolan@mail.id', 'Binjai'),
('Andi Siregar', 'KTP', '1201010101010005', '081200000005', 'andi.siregar@mail.id', 'Tebing Tinggi');

INSERT INTO reservasi (id_tamu, id_kamar, id_karyawan, tanggal_checkin, tanggal_checkout) VALUES
(2, 4, 1, '2026-06-10', '2026-06-12'),
(3, 5, 2, '2026-06-11', '2026-06-14'),
(4, 6, 1, '2026-06-15', '2026-06-16');

INSERT INTO pesanan_restoran (id_reservasi, id_meja, total_harga) VALUES
(1, 1, 87000),
(2, 2, 133000),
(3, 3, 71000);

INSERT INTO detail_pesanan_restoran (id_pesanan, id_menu, jumlah, subtotal) VALUES
(1, 1, 2, 44000),
(1, 4, 1, 18000),
(1, 5, 1, 15000),
(2, 2, 1, 65000),
(2, 3, 1, 35000),
(2, 4, 2, 36000),
(3, 1, 1, 22000),
(3, 3, 1, 35000),
(3, 5, 1, 15000);

INSERT INTO detail_layanan_reservasi (id_reservasi, id_layanan, jumlah) VALUES
(1, 1, 2),
(1, 4, 1),
(2, 3, 1),
(2, 2, 1),
(3, 5, 1);

INSERT INTO akses_fasilitas (id_reservasi, id_fasilitas) VALUES
(1, 1),
(1, 3),
(2, 2),
(3, 4),
(3, 5);

INSERT INTO invoice (no_invoice, id_reservasi, total_kamar, total_restoran, total_layanan, total_tagihan) VALUES
('INV-SU-001', 1, 700000, 77000, 465000, 1242000),
('INV-SU-002', 2, 2200000, 136000, 500000, 2836000),
('INV-SU-003', 3, 1100000, 72000, 250000, 1422000);

INSERT INTO pembayaran (id_reservasi, jumlah_bayar, metode_bayar, status_pembayaran) VALUES
(1, 1242000, 'Transfer Bank', 'Lunas'),
(2, 2836000, 'QRIS', 'Lunas'),
(3, 1422000, 'Tunai', 'Pending');

-- =========================================================
-- Alokasi Inventaris Kamar (Housekeeping Relasi)
-- =========================================================
CREATE TABLE IF NOT EXISTS kamar_inventaris (
    id_kamar int references kamar(id_kamar) on delete cascade,
    id_barang int references logistik.inventaris(id_barang) on delete cascade,
    jumlah_terpasang int default 2,
    primary key (id_kamar, id_barang)
);

INSERT INTO kamar_inventaris (id_kamar, id_barang, jumlah_terpasang) VALUES
(1, 1, 2), 
(1, 2, 2), 
(1, 4, 2), 
(2, 1, 2), 
(2, 2, 2), 
(2, 4, 2), 
(3, 3, 2), 
(3, 5, 1), 
(3, 6, 2)
ON CONFLICT DO NOTHING;
