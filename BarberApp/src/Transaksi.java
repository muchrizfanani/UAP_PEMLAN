
//merge
/**
 * Representasi data transaksi Barber Pro.
 * Mengimplementasikan Comparable untuk fitur sorting default.
 */
public class Transaksi implements Comparable<Transaksi> {
    private String nama, layanan, tanggal;
    private double harga;

    public Transaksi(String nama, String layanan, double harga, String tanggal) {
        this.nama = nama;
        this.layanan = layanan;
        this.harga = harga;
        this.tanggal = tanggal;
    }

    // Getters
    public String getNama() { return nama; }
    public String getLayanan() { return layanan; }
    public String getTanggal() { return tanggal; }
    public double getHarga() { return harga; }

    // Konversi ke format CSV untuk penyimpanan file
    public String toCSV() {
        return nama + "," + layanan + "," + harga + "," + tanggal;
    }

    // Format data untuk ditampilkan di JTable
    public Object[] toArray() {
        return new Object[]{nama, layanan, "Rp " + String.format("%,.0f", harga), tanggal};
    }

    @Override
    public int compareTo(Transaksi o) {
        // Urutan default: Tanggal terbaru di atas
        return o.getTanggal().compareTo(this.tanggal);
    }
}