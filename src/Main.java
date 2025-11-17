import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

// Edge class representing a weighted edge
class Edge {
    private int source;
    private int destination;
    private double weight;

    public Edge(int source, int destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public int getSource() { return source; }
    public int getDestination() { return destination; }
    public double getWeight() { return weight; }
}

// Vertex class representing a graph node
class Vertex {
    private int id;
    private Point2D.Double position;
    private static final int RADIUS = 25;

    public Vertex(int id, Point2D.Double position) {
        this.id = id;
        this.position = position;
    }

    public int getId() { return id; }
    public Point2D.Double getPosition() { return position; }
    public int getRadius() { return RADIUS; }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }
}

// Graph class managing vertices and edges
class Graph {
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;
    private double[][] adjacencyMatrix;

    public Graph(double[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        buildGraph();
    }

    private void buildGraph() {
        int n = adjacencyMatrix.length;

        // Create vertices with circular layout
        for (int i = 0; i < n; i++) {
            vertices.add(new Vertex(i, new Point2D.Double()));
        }

        // Create edges from adjacency matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    edges.add(new Edge(i, j, adjacencyMatrix[i][j]));
                }
            }
        }
    }

    public void calculateLayout(int width, int height) {
        int n = vertices.size();
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double radius = Math.min(width, height) / 2.5;

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            vertices.get(i).setPosition(new Point2D.Double(x, y));
        }
    }

    public ArrayList<Vertex> getVertices() { return vertices; }
    public ArrayList<Edge> getEdges() { return edges; }
}

// Panel for rendering the graph
class GraphPanel extends JPanel {
    private Graph graph;
    private static final Color VERTEX_COLOR = new Color(70, 130, 180);
    private static final Color EDGE_COLOR = new Color(100, 100, 100);
    private static final Color ARROW_COLOR = new Color(80, 80, 80);
    private static final Color TEXT_COLOR = Color.WHITE;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graph.calculateLayout(getWidth(), getHeight());

        // Draw edges
        drawEdges(g2d);

        // Draw vertices
        drawVertices(g2d);
    }

    private void drawEdges(Graphics2D g2d) {
        for (Edge edge : graph.getEdges()) {
            Vertex source = graph.getVertices().get(edge.getSource());
            Vertex dest = graph.getVertices().get(edge.getDestination());

            Point2D.Double start = source.getPosition();
            Point2D.Double end = dest.getPosition();

            // Calculate edge offset for better visualization
            double dx = end.x - start.x;
            double dy = end.y - start.y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Adjust start and end points to avoid overlap with vertices
            double ratio = source.getRadius() / distance;
            double startX = start.x + dx * ratio;
            double startY = start.y + dy * ratio;

            ratio = (distance - dest.getRadius()) / distance;
            double endX = start.x + dx * ratio;
            double endY = start.y + dy * ratio;

            // Draw edge line
            g2d.setColor(EDGE_COLOR);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(new Line2D.Double(startX, startY, endX, endY));

            // Draw arrow
            drawArrow(g2d, startX, startY, endX, endY);

            // Draw weight label
            double midX = (startX + endX) / 2;
            double midY = (startY + endY) / 2;
            drawWeightLabel(g2d, edge.getWeight(), midX, midY);
        }
    }

    private void drawArrow(Graphics2D g2d, double x1, double y1, double x2, double y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 10;

        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        xPoints[0] = (int) x2;
        yPoints[0] = (int) y2;
        xPoints[1] = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        yPoints[1] = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
        xPoints[2] = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        yPoints[2] = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

        g2d.setColor(ARROW_COLOR);
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawWeightLabel(Graphics2D g2d, double weight, double x, double y) {
        String label = String.format("%.1f", weight);
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(label);

        // Draw background rectangle
        g2d.setColor(Color.WHITE);
        g2d.fillRect((int) x - width / 2 - 2, (int) y - 8, width + 4, 16);

        g2d.setColor(Color.RED);
        g2d.drawString(label, (int) x - width / 2, (int) y + 4);
    }

    private void drawVertices(Graphics2D g2d) {
        for (Vertex vertex : graph.getVertices()) {
            Point2D.Double pos = vertex.getPosition();
            int radius = vertex.getRadius();

            // Draw vertex circle
            g2d.setColor(VERTEX_COLOR);
            g2d.fillOval((int) (pos.x - radius), (int) (pos.y - radius),
                    radius * 2, radius * 2);

            // Draw vertex border
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval((int) (pos.x - radius), (int) (pos.y - radius),
                    radius * 2, radius * 2);

            // Draw vertex label
            String label = String.valueOf(vertex.getId());
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int width = fm.stringWidth(label);
            g2d.drawString(label, (int) pos.x - width / 2, (int) pos.y + 5);
        }
    }
}

// Main application class
    public class Main extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;

    public Main(double[][] adjacencyMatrix) {
        this.graph = new Graph(adjacencyMatrix);
        this.graphPanel = new GraphPanel(graph);

        setTitle("Weighted Directed Graph Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(graphPanel);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        // Example adjacency matrix (0 means no edge)
        double[][] adjacencyMatrix = {
                {0, 2.5, 0, 1.0, 0},
                {0, 0, 3.2, 2.1, 0},
                {0, 0, 0, 0, 1.8},
                {0, 0, 4.5, 0, 2.3},
                {1.5, 0, 0, 0, 0}
        };

        SwingUtilities.invokeLater(() -> {
            Main visualizer = new Main(adjacencyMatrix);
            visualizer.setVisible(true);
        });
    }
}