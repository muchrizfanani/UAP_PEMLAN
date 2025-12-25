
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BarberFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel contentPanel = new JPanel(cardLayout);
    private ArrayList<Transaksi> listData;
    private DatabaseManager dbManager = new DatabaseManager();

    // COLOR PALETTE
    private final Color COLOR_BLACK = new Color(10, 10, 10);
    private final Color COLOR_GOLD  = new Color(194, 154, 80);
    private final Color COLOR_WHITE = new Color(255, 255, 255);

    private JTextField txtNama = createGoldTextField();
    private JComboBox<String> cbLayanan = new JComboBox<>(new String[]{"Gentleman Cut", "Beard Sculpt", "Premium Spa"});
    private JTextField txtHarga = createGoldTextField();
    private DefaultTableModel tableModel;
    private JTable table;
    private int indexUpdate = -1;

    public BarberFrame() {
        listData = dbManager.loadFromFile();
        setupFrame();
        initLayout();
    }

    private void setupFrame() {
        setTitle("BARBER PRO | THE GOLDEN STANDARD");
        setSize(1150, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BLACK);
    }

    private void initLayout() {
        setLayout(new BorderLayout());

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(COLOR_BLACK);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_GOLD));

        JLabel logo = new JLabel("BARBER PRO");
        logo.setFont(new Font("Serif", Font.BOLD, 32));
        logo.setForeground(COLOR_GOLD);
        logo.setBorder(new EmptyBorder(50, 40, 50, 0));

        sidebar.add(logo);
        sidebar.add(createNavBtn("DASHBOARD", "Dashboard"));
        sidebar.add(createNavBtn("NEW ORDER", "Input"));
        sidebar.add(createNavBtn("HISTORY", "List"));
        sidebar.add(createNavBtn("REPORT", "Laporan"));

        contentPanel.setOpaque(false);
        refreshContentPanels();

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void refreshContentPanels() {
        contentPanel.removeAll();
        contentPanel.add(createDashboard(), "Dashboard");
        contentPanel.add(createForm(), "Input");
        contentPanel.add(createHistory(), "List");
        contentPanel.add(createReport(), "Laporan");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // --- DASHBOARD ---
    private JPanel createDashboard() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(COLOR_BLACK);
        p.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblHeader = new JLabel("CONTROL CENTER");
        lblHeader.setFont(new Font("Serif", Font.BOLD, 28));
        lblHeader.setForeground(COLOR_GOLD);
        p.add(lblHeader, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(createStatCard("TOTAL TRANSACTIONS", String.valueOf(listData.size())));
        double total = listData.stream().mapToDouble(Transaksi::getHarga).sum();
        statsPanel.add(createStatCard("TOTAL REVENUE", "Rp " + String.format("%,.0f", total)));

        p.add(statsPanel, BorderLayout.CENTER);
        return p;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(COLOR_BLACK);
        card.setBorder(BorderFactory.createLineBorder(COLOR_GOLD, 2));
        JLabel t = new JLabel(title, JLabel.CENTER); t.setForeground(COLOR_WHITE);
        JLabel v = new JLabel(value, JLabel.CENTER); v.setForeground(COLOR_GOLD);
        v.setFont(new Font("Serif", Font.BOLD, 24));
        card.add(t); card.add(v);
        return card;
    }

    // --- FORM ---
    private JPanel createForm() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(COLOR_BLACK);
        JPanel frame = new JPanel(new GridBagLayout());
        frame.setBackground(COLOR_BLACK);
        frame.setBorder(BorderFactory.createLineBorder(COLOR_GOLD, 2));
        frame.setPreferredSize(new Dimension(500, 500));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 40, 10, 40); g.fill = GridBagConstraints.HORIZONTAL;
        JLabel head = new JLabel("NEW TRANSACTION", JLabel.CENTER);
        head.setFont(new Font("Serif", Font.BOLD, 22)); head.setForeground(COLOR_GOLD);
        g.gridy = 0; g.insets = new Insets(30,0,30,0); frame.add(head, g);
        g.insets = new Insets(5, 40, 5, 40);
        g.gridy = 1; frame.add(createFieldLabel("NAME"), g);
        g.gridy = 2; frame.add(txtNama, g);
        g.gridy = 3; frame.add(createFieldLabel("SERVICE"), g);
        g.gridy = 4; frame.add(cbLayanan, g);
        g.gridy = 5; frame.add(createFieldLabel("PRICE"), g);
        g.gridy = 6; frame.add(txtHarga, g);
        JButton btn = new JButton("SAVE RECORD");
        btn.setBackground(COLOR_GOLD); btn.addActionListener(e -> handleSave());
        g.gridy = 7; g.ipady = 15; g.insets = new Insets(30,40,30,40); frame.add(btn, g);
        wrapper.add(frame);
        return wrapper;
    }

    // --- HISTORY PANEL WITH SORTING ---
    private JPanel createHistory() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(COLOR_BLACK);
        p.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header Panel (Judul + Sorting Box)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("TRANSACTION HISTORY");
        title.setFont(new Font("Serif", Font.BOLD, 26));
        title.setForeground(COLOR_GOLD);
        topPanel.add(title, BorderLayout.WEST);

        // Sorting ComboBox
        String[] sortOptions = {"Sort: Newest", "Sort: Name A-Z", "Sort: Highest Price"};
        JComboBox<String> sortBox = new JComboBox<>(sortOptions);
        sortBox.setBackground(COLOR_BLACK);
        sortBox.setForeground(COLOR_GOLD);
        sortBox.setPreferredSize(new Dimension(180, 30));
        sortBox.addActionListener(e -> applySorting(sortBox.getSelectedIndex()));
        topPanel.add(sortBox, BorderLayout.EAST);

        p.add(topPanel, BorderLayout.NORTH);

        // Tabel Konfigurasi
        tableModel = new DefaultTableModel(new String[]{"Customer Name", "Service Type", "Price", "Date"}, 0);
        table = new JTable(tableModel);
        table.setBackground(COLOR_BLACK);
        table.setForeground(COLOR_WHITE);
        table.setGridColor(new Color(50, 50, 50));
        table.setRowHeight(35);
        table.setSelectionBackground(COLOR_GOLD);
        table.setSelectionForeground(COLOR_BLACK);

        // Aktifkan Sorting Klik Header Kolom
        table.setRowSorter(new TableRowSorter<>(tableModel));

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_BLACK);
        header.setForeground(COLOR_GOLD);
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_GOLD));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(COLOR_BLACK);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_GOLD, 1));

        // Action Buttons
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlAction.setOpaque(false);

        JButton btnEdit = createOutlineBtn("EDIT TRANSACTION");
        JButton btnDel = createOutlineBtn("DELETE RECORD");

        btnEdit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r != -1) {
                indexUpdate = table.convertRowIndexToModel(r);
                Transaksi t = listData.get(indexUpdate);
                txtNama.setText(t.getNama());
                txtHarga.setText(String.format("%.0f", t.getHarga()));
                cbLayanan.setSelectedItem(t.getLayanan());
                cardLayout.show(contentPanel, "Input");
            }
        });

        btnDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete record?", "Confirm", JOptionPane.YES_NO_OPTION);
                if(confirm == JOptionPane.YES_OPTION) {
                    listData.remove(table.convertRowIndexToModel(r));
                    try { dbManager.saveToFile(listData); refreshContentPanels(); } catch(Exception ex){}
                }
            }
        });

        pnlAction.add(btnEdit); pnlAction.add(btnDel);
        p.add(scroll, BorderLayout.CENTER);
        p.add(pnlAction, BorderLayout.SOUTH);

        refreshTable();
        return p;
    }

    // LOGIKA SORTING
    private void applySorting(int criteria) {
        switch (criteria) {
            case 0 -> Collections.sort(listData); // Berdasarkan Comparable (Terbaru)
            case 1 -> listData.sort(Comparator.comparing(Transaksi::getNama)); // A-Z Nama
            case 2 -> listData.sort(Comparator.comparingDouble(Transaksi::getHarga).reversed()); // Harga Termahal
        }
        refreshTable();
    }

    // --- REPORT ---
    private JPanel createReport() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(COLOR_BLACK); p.setBorder(new EmptyBorder(50, 50, 50, 50));
        JTextArea area = new JTextArea();
        area.setBackground(COLOR_BLACK); area.setForeground(COLOR_GOLD);
        area.setFont(new Font("Monospaced", Font.BOLD, 16));
        area.setBorder(BorderFactory.createLineBorder(COLOR_GOLD));
        JButton btn = new JButton("GENERATE REPORT");
        btn.setBackground(COLOR_GOLD);
        btn.addActionListener(e -> {
            double total = listData.stream().mapToDouble(Transaksi::getHarga).sum();
            area.setText("\n   BUSINESS REPORT\n   ---------------\n");
            area.append("   Total Orders  : " + listData.size() + "\n");
            area.append("   Total Revenue : Rp " + String.format("%,.0f", total));
        });
        p.add(btn, BorderLayout.NORTH); p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    // --- HELPERS ---
    private void handleSave() {
        try {
            double h = Double.parseDouble(txtHarga.getText());
            String tgl = (indexUpdate == -1) ? LocalDate.now().toString() : listData.get(indexUpdate).getTanggal();
            Transaksi t = new Transaksi(txtNama.getText(), (String)cbLayanan.getSelectedItem(), h, tgl);
            if(indexUpdate == -1) listData.add(t); else { listData.set(indexUpdate, t); indexUpdate = -1; }
            dbManager.saveToFile(listData);
            refreshContentPanels();
            cardLayout.show(contentPanel, "List");
        } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Check Data!"); }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for(Transaksi t : listData) tableModel.addRow(t.toArray());
    }

    private JTextField createGoldTextField() {
        JTextField f = new JTextField(); f.setBackground(COLOR_BLACK); f.setForeground(COLOR_WHITE);
        f.setCaretColor(COLOR_GOLD); f.setBorder(BorderFactory.createMatteBorder(0,0,2,0,COLOR_GOLD));
        f.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return f;
    }

    private JLabel createFieldLabel(String t) {
        JLabel l = new JLabel(t); l.setForeground(COLOR_GOLD); l.setFont(new Font("SansSerif", Font.BOLD, 11));
        return l;
    }

    private JButton createNavBtn(String t, String p) {
        JButton b = new JButton(t); b.setMaximumSize(new Dimension(280, 50));
        b.setBackground(COLOR_BLACK); b.setForeground(COLOR_WHITE); b.setBorderPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMargin(new Insets(0, 40, 0, 0));
        b.addActionListener(e -> { refreshContentPanels(); cardLayout.show(contentPanel, p); });
        return b;
    }

    private JButton createOutlineBtn(String t) {
        JButton b = new JButton(t); b.setBackground(COLOR_BLACK); b.setForeground(COLOR_GOLD);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(BorderFactory.createLineBorder(COLOR_GOLD, 1));
        b.setPreferredSize(new Dimension(160, 40));
        b.setFocusPainted(false);
        return b;
    }
}