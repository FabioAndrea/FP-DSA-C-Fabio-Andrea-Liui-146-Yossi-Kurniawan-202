import javax.swing.*;
import java.awt.*;
public class GraphVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;
    private int totalDistance;
    private String startCity;
    private String endCity;

    public GraphVisualizer(int[][] adjacencyMatrix, String[] labels) {
        setTitle("Graph Visualizer - Indonesian Cities Routes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graph = new Graph(adjacencyMatrix, labels);
        graphPanel = new GraphPanel(graph);

        add(graphPanel, BorderLayout.CENTER);

        // Add info panel at bottom
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        JLabel infoLabel = new JLabel("Drag nodes to rearrange. Edges show route distances/weights.");
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    public void setShortestPathInfo(int distance, String start, String end) {
        this.totalDistance = distance;
        this.startCity = start;
        this.endCity = end;

        // Add info panel at top
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(113, 165, 226));
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JLabel pathLabel = new JLabel("Jalur Terpendek: " + start + " → " + end);
        pathLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pathLabel.setForeground(Color.WHITE);

        JLabel distanceLabel = new JLabel("Total Jarak: " + distance + " km");
        distanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        distanceLabel.setForeground(Color.WHITE);

        topPanel.add(pathLabel);
        topPanel.add(new JLabel("|"));
        topPanel.add(distanceLabel);

        add(topPanel, BorderLayout.NORTH);
        revalidate();
    }
    public static void main(String[] args) {
        // City labels
        String[] cityLabels = {"MKS", "SUB", "BDG", "CGK", "MLG", "DHS", "DPS", "YOG", "PDG", "BTM"};

        // Adjacency matrix based on the routes (BIDIRECTIONAL - both directions with same weight)
        // Index: 0=MKS, 1=SUB, 2=BDG, 3=CGK, 4=MLG, 5=DHS, 6=DPS, 7=YOG, 8=PDG, 9=BTM
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

        SwingUtilities.invokeLater(() -> {
            GraphVisualizer visualizer = new GraphVisualizer(adjacencyMatrix, cityLabels);
            // Find and highlight shortest path from MKS (0) to BTM (9)
            int totalDistance = visualizer.graph.findShortestPath(0, 9);
            visualizer.setShortestPathInfo(totalDistance, "Makassar (MKS)", "Batam (BTM)");
            visualizer.setVisible(true);
        });
    }
}