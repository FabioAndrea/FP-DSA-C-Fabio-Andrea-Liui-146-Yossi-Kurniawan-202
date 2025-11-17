import javax.swing.*;
import java.awt.*;

public class GraphVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;

    //UI Components untuk panel kontrol
    private JComboBox<String> startComboBox;
    private JComboBox<String> endComboBox;
    private JLabel pathLabel;
    private JLabel distanceLabel;

    //Kita simpan data kota di sini agar bisa diakses
    private String[] cityLabels;
    private String[] cityFullNames;

    /**
     * [MODIFIKASI] Constructor sekarang menerima nama lengkap kota
     */
    public GraphVisualizer(int[][] adjacencyMatrix, String[] labels, String[] fullNames) {
        this.cityLabels = labels;
        this.cityFullNames = fullNames;

        setTitle("Graph Visualizer - Indonesian Cities Routes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inisialisasi Graph dan GraphPanel (sama seperti sebelumnya)
        graph = new Graph(adjacencyMatrix, labels);
        graphPanel = new GraphPanel(graph);
        add(graphPanel, BorderLayout.CENTER);

        // --- [MODIFIKASI] Membuat Panel Kontrol di Atas ---
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(113, 165, 226));
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Buat dropdown (JComboBox) menggunakan array nama lengkap
        startComboBox = new JComboBox<>(cityFullNames);
        endComboBox = new JComboBox<>(cityFullNames);

        // Buat tombol "Cari"
        JButton findButton = new JButton("Cari Jalur Terpendek");

        // Tambahkan komponen ke panel kontrol
        controlPanel.add(new JLabel("Kota Awal:"));
        controlPanel.add(startComboBox);
        controlPanel.add(new JLabel("Kota Tujuan:"));
        controlPanel.add(endComboBox);
        controlPanel.add(findButton);

        // Tambahkan panel kontrol ke bagian atas (NORTH)
        add(controlPanel, BorderLayout.NORTH);

        // --- [MODIFIKASI] Membuat Panel Info di Bawah ---
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Inisialisasi label untuk hasil (awalnya kosong)
        pathLabel = new JLabel("Jalur: ---");
        distanceLabel = new JLabel("Total Jarak: --- km");

        // Atur font
        pathLabel.setFont(new Font("Arial", Font.BOLD, 16));
        distanceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        infoPanel.add(pathLabel);
        infoPanel.add(new JLabel("|"));
        infoPanel.add(distanceLabel);

        // Tambahkan panel info ke bagian bawah (SOUTH)
        add(infoPanel, BorderLayout.SOUTH);

        // --- [BARU] Tambahkan Aksi untuk Tombol ---
        findButton.addActionListener(e -> {
            // Dapatkan indeks (angka) dari dropdown yang dipilih
            int startIndex = startComboBox.getSelectedIndex();
            int endIndex = endComboBox.getSelectedIndex();

            // Panggil method untuk menghitung dan memperbarui UI
            calculateAndShowPath(startIndex, endIndex);
        });

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * [MODIFIKASI] Method ini menggantikan setShortestPathInfo
     * Ini menghitung path, memperbarui label info, dan menggambar ulang graph.
     */
    public void calculateAndShowPath(int startIndex, int endIndex) {
        // 1. Hitung jalur terpendek (ini akan memperbarui list 'shortestPathEdges' di dalam Graph)
        int totalDistance = graph.findShortestPath(startIndex, endIndex);

        // 2. Buat string label dari array berdasarkan indeks
        String startLabelStr = cityFullNames[startIndex] + " (" + cityLabels[startIndex] + ")";
        String endLabelStr = cityFullNames[endIndex] + " (" + cityLabels[endIndex] + ")";

        // 3. Perbarui teks pada label info di bagian bawah
        pathLabel.setText("Jalur Terpendek: " + startLabelStr + " → " + endLabelStr);
        distanceLabel.setText("Total Jarak: " + totalDistance + " km");

        // 4. Sinkronkan dropdown (jika dipanggil dari 'main' saat start)
        startComboBox.setSelectedIndex(startIndex);
        endComboBox.setSelectedIndex(endIndex);

        // 5. Perintahkan GraphPanel untuk menggambar ulang dirinya
        // Ini akan membaca 'shortestPathEdges' yang baru dan mewarnainya merah
        graphPanel.repaint();
    }

    /**
     * [MODIFIKASI] Method Main
     */
    public static void main(String[] args) {
        // City labels (Singkatan)
        String[] cityLabels = {"MKS", "SUB", "BDG", "CGK", "MLG", "DHS", "DPS", "YOG", "PDG", "BTM"};

        // Array untuk nama lengkap kota
        String[] cityFullNames = {
                "Makassar", "Surabaya", "Bandung", "Jakarta (Cengkareng)", "Malang",
                "Daha", "Denpasar", "Yogyakarta", "Padang", "Batam"
        };

        // Adjacency matrix (Tetap sama)
        int[][] adjacencyMatrix = {
                // MKS  SUB  BDG  CGK  MLG  DHS  DPS  YOG  PDG  BTM
                {  0,   2,   3,   0,   0,   0,   3,   0,   0,   0  }, // MKS
                {  2,   0,   0,   3,   0,   0,   1,   0,   0,   0  }, // SUB
                {  3,   0,   0,   4,   2,   0,   0,   0,   0,   0  }, // BDG
                {  0,   3,   4,   0,   0,   0,   0,   0,   0,   0  }, // CGK
                {  0,   0,   2,   0,   0,   3,   0,   0,   4,   0  }, // MLG
                {  0,   0,   0,   0,   3,   0,   2,   0,   0,   3  }, // DHS
                {  3,   1,   0,   0,   0,   2,   0,   3,   0,  10  }, // DPS
                {  0,   0,   0,   0,   0,   0,   3,   0,   0,   4  }, // YOG
                {  0,   0,   0,   0,   4,   0,   0,   0,   0,   2  }, // PDG
                {  0,   0,   0,   0,   0,   3,  10,   4,   2,   0  }  // BTM
        };

        // Tentukan jalur awal yang ingin ditampilkan saat program dibuka
        final int initialStartIndex = 0; // 0 = MKS
        final int initialEndIndex = 9;   // 9 = BTM

        SwingUtilities.invokeLater(() -> {
            // [MODIFIKASI] Kirim ketiga array ke constructor
            GraphVisualizer visualizer = new GraphVisualizer(adjacencyMatrix, cityLabels, cityFullNames);

            // Tampilkan jendela
            visualizer.setVisible(true);

            // [MODIFIKASI] Hitung dan tampilkan jalur awal
            visualizer.calculateAndShowPath(initialStartIndex, initialEndIndex);
        });
    }
}