import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GraphVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;
    private int totalDistance;
    private String startCity;
    private String endCity;
    private JLabel pathInfoLabel;
    private JLabel distanceLabel;
    private JComboBox<String> fromCombo;
    private JComboBox<String> toCombo;
    private JButton findPathButton;
    private JButton resetButton;
    private Timer animationTimer;
    private int currentAnimationStep = 0;

    public GraphVisualizer(int[][] adjacencyMatrix, String[] labels) {
        setTitle("Graph Visualizer - Indonesian Cities Routes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graph = new Graph(adjacencyMatrix, labels);
        graphPanel = new GraphPanel(graph);

        setLayout(new BorderLayout());

        // Create header panel with controls
        JPanel headerPanel = createHeaderPanel(labels);
        add(headerPanel, BorderLayout.NORTH);

        add(graphPanel, BorderLayout.CENTER);

        // Add info panel at bottom
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        JLabel infoLabel = new JLabel("🖱️ Drag nodes to rearrange | Edges show route distances/weights");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createHeaderPanel(String[] labels) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(70, 130, 180));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Title and path info panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        topPanel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("🗺️ Shortest Path Finder");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);

        pathInfoLabel = new JLabel("");
        pathInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pathInfoLabel.setForeground(Color.YELLOW);
        topPanel.add(pathInfoLabel);

        distanceLabel = new JLabel("");
        distanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        distanceLabel.setForeground(Color.WHITE);
        topPanel.add(distanceLabel);

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        controlsPanel.setBackground(new Color(70, 130, 180));

        // From dropdown
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setForeground(Color.WHITE);
        fromLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlsPanel.add(fromLabel);

        fromCombo = new JComboBox<>(labels);
        fromCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        fromCombo.setPreferredSize(new Dimension(100, 30));
        controlsPanel.add(fromCombo);

        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setForeground(Color.WHITE);
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 20));
        controlsPanel.add(arrowLabel);

        // To dropdown
        JLabel toLabel = new JLabel("To:");
        toLabel.setForeground(Color.WHITE);
        toLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlsPanel.add(toLabel);

        toCombo = new JComboBox<>(labels);
        toCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        toCombo.setPreferredSize(new Dimension(100, 30));
        toCombo.setSelectedIndex(labels.length - 1);
        controlsPanel.add(toCombo);

        // Find button
        findPathButton = new JButton("🔍 Find Shortest Path");
        findPathButton.setFont(new Font("Arial", Font.BOLD, 13));
        findPathButton.setBackground(new Color(34, 139, 34));
        findPathButton.setForeground(Color.WHITE);
        findPathButton.setFocusPainted(false);
        findPathButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        findPathButton.addActionListener(e -> startPathAnimation());
        controlsPanel.add(findPathButton);

        // Reset button
        resetButton = new JButton("🔄 Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 13));
        resetButton.setBackground(new Color(220, 20, 60));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetVisualization());
        controlsPanel.add(resetButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(controlsPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void startPathAnimation() {
        // Stop any existing animation
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        // Get selected cities
        int startIdx = fromCombo.getSelectedIndex();
        int endIdx = toCombo.getSelectedIndex();

        if (startIdx == endIdx) {
            JOptionPane.showMessageDialog(this,
                    "Please select different cities!",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Find shortest path
        totalDistance = graph.findShortestPath(startIdx, endIdx);

        if (totalDistance == Integer.MAX_VALUE) {
            JOptionPane.showMessageDialog(this,
                    "No path found between these cities!",
                    "Path Not Found",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        startCity = (String) fromCombo.getSelectedItem();
        endCity = (String) toCombo.getSelectedItem();

        // Reset animation
        graph.resetAnimation();
        currentAnimationStep = 0;

        // Update info labels
        pathInfoLabel.setText("Searching: " + startCity + " → " + endCity);
        distanceLabel.setText("");

        // Disable buttons during animation
        findPathButton.setEnabled(false);
        resetButton.setEnabled(false);

        // Start animation timer
        animationTimer = new Timer(50, new ActionListener() {
            private float stepProgress = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Node> pathNodes = graph.getShortestPathNodes();
                ArrayList<Edge> pathEdges = graph.getShortestPathEdges();

                if (currentAnimationStep < pathNodes.size()) {
                    // Animate current node
                    Node currentNode = pathNodes.get(currentAnimationStep);
                    currentNode.setAnimating(true);
                    currentNode.setAnimationProgress(stepProgress);

                    // Animate current edge
                    if (currentAnimationStep < pathEdges.size()) {
                        Edge currentEdge = pathEdges.get(currentAnimationStep);
                        currentEdge.setAnimating(true);
                        currentEdge.setAnimationProgress(stepProgress);
                    }

                    stepProgress += 0.08f;

                    if (stepProgress >= 1.0f) {
                        currentNode.setAnimationProgress(1.0f);
                        if (currentAnimationStep < pathEdges.size()) {
                            pathEdges.get(currentAnimationStep).setAnimationProgress(1.0f);
                        }
                        currentAnimationStep++;
                        stepProgress = 0f;
                    }

                    graphPanel.repaint();
                } else {
                    // Animation complete
                    animationTimer.stop();

                    // Update info labels
                    pathInfoLabel.setText("Path: " + startCity + " → " + endCity);
                    distanceLabel.setText("| Total: " + totalDistance + " km");

                    // Re-enable buttons
                    findPathButton.setEnabled(true);
                    resetButton.setEnabled(true);

                    // Show completion message
                    Timer messageTimer = new Timer(500, evt -> {
                        JOptionPane.showMessageDialog(GraphVisualizer.this,
                                "Shortest path found!\n" +
                                        "Route: " + startCity + " → " + endCity + "\n" +
                                        "Total Distance: " + totalDistance + " km",
                                "Path Complete",
                                JOptionPane.INFORMATION_MESSAGE);
                        ((Timer)evt.getSource()).stop();
                    });
                    messageTimer.setRepeats(false);
                    messageTimer.start();
                }
            }
        });

        animationTimer.start();
    }

    private void resetVisualization() {
        // Stop animation if running
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        // Reset graph
        graph.resetAnimation();
        graph.getShortestPathEdges().clear();
        graph.getShortestPathNodes().clear();

        // Reset UI
        pathInfoLabel.setText("");
        distanceLabel.setText("");
        currentAnimationStep = 0;

        // Re-enable buttons
        findPathButton.setEnabled(true);
        resetButton.setEnabled(true);

        graphPanel.repaint();
    }

    public static void main(String[] args) {
        // City labels
        String[] cityLabels = {"MKS", "SUB", "BDG", "CGK", "MLG", "DHS", "DPS", "YOG", "PDG", "BTM"};

        // Adjacency matrix based on the routes
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
            visualizer.setVisible(true);
        });
    }
}
