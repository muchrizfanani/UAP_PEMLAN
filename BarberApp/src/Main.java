
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Optimasi skala UI untuk Windows/Mac
        System.setProperty("sun.java2d.uiScale", "1.0");

        try {
            // Menggunakan Nimbus Look and Feel agar lebih modern
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new BarberFrame().setVisible(true);
        });
    }
}