import javax.swing.*;
import java.awt.*;
// Main class
public class GraphVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;

    public GraphVisualizer(int[][] adjacencyMatrix, String[] labels) {
        setTitle("Graph Visualizer - Indonesian Cities Routes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graph = new Graph(adjacencyMatrix, labels);
        graphPanel = new GraphPanel(graph);

        add(graphPanel, BorderLayout.CENTER);

        // Add info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        JLabel infoLabel = new JLabel("Drag nodes to rearrange. Edges show route distances/weights.");
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
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
                {  0,   3,   4,   0,   6,   4,   0,   0,   0,   0  }, // CGK
                {  0,   0,   2,   6,   0,   3,   0,   0,   4,   0  }, // MLG
                {  0,   0,   0,   4,   3,   0,   2,   0,   0,   3  }, // DHS
                {  8,   1,   0,   0,   0,   2,   0,   3,   0,  10  }, // DPS
                {  0,   0,   0,   0,   0,   0,   3,   0,   0,   4  }, // YOG
                {  0,   0,   0,   0,   4,   0,   0,   0,   0,   2  }, // PDG
                {  0,   0,   0,   0,   0,   3,  10,   4,   2,   0  }  // BTM
        };

        SwingUtilities.invokeLater(() -> {
            GraphVisualizer visualizer = new GraphVisualizer(adjacencyMatrix, cityLabels);
            // Find and highlight shortest path from MKS (0) to BTM (9)
            visualizer.graph.findShortestPath(0, 9);
            visualizer.setVisible(true);
        });
    }
}