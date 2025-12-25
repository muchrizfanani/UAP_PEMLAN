
import java.io.*;
import java.util.ArrayList;

public class DatabaseManager {
    private final String FILE_NAME = "database_barber.csv";

    // Menyimpan seluruh daftar transaksi ke file
    public void saveToFile(ArrayList<Transaksi> listData) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Transaksi t : listData) {
                bw.write(t.toCSV());
                bw.newLine();
            }
        }
    }

    // Memuat data dari file saat aplikasi dijalankan
    public ArrayList<Transaksi> loadFromFile() {
        ArrayList<Transaksi> listData = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return listData;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length == 4) {
                    listData.add(new Transaksi(d[0], d[1], Double.parseDouble(d[2]), d[3]));
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat data: " + e.getMessage());
        }
        return listData;
    }
}