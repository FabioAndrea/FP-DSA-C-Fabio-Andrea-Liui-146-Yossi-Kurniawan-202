import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Panel for drawing the graph
class GraphPanel extends JPanel {
    private Graph graph;
    private Node draggedNode = null;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(1000, 700));
        setBackground(Color.WHITE);

        // Mouse listener for dragging nodes
        MouseAdapter mouseHandler = new MouseAdapter() {
            private int offsetX, offsetY;

            @Override
            public void mousePressed(MouseEvent e) {
                for (Node node : graph.getNodes()) {
                    if (node.contains(e.getX(), e.getY())) {
                        draggedNode = node;
                        offsetX = e.getX() - node.getX();
                        offsetY = e.getY() - node.getY();
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.setX(e.getX() - offsetX);
                    draggedNode.setY(e.getY() - offsetY);
                    repaint();
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges
        for (Edge edge : graph.getEdges()) {
            Node source = edge.getSource();
            Node target = edge.getTarget();

            // Check if this edge is part of shortest path
            boolean isShortestPath = graph.getShortestPathEdges().contains(edge);

            // Set color based on whether it's in shortest path
            if (isShortestPath) {
                g2d.setColor(new Color(220, 20, 60)); // Red for shortest path
                g2d.setStroke(new BasicStroke(4));
            } else {
                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
            }

            // Draw line
            g2d.drawLine(source.getX(), source.getY(), target.getX(), target.getY());

            // Draw arrow
            drawArrow(g2d, source.getX(), source.getY(), target.getX(), target.getY(), isShortestPath);

            // Draw weight
            int midX = (source.getX() + target.getX()) / 2;
            int midY = (source.getY() + target.getY()) / 2;

            // Draw weight background
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String weightStr = String.valueOf(edge.getWeight());
            FontMetrics fm = g2d.getFontMetrics();
            int strWidth = fm.stringWidth(weightStr);
            g2d.fillRect(midX - strWidth/2 - 3, midY - 10, strWidth + 6, 18);

            // Draw weight text
            if (isShortestPath) {
                g2d.setColor(new Color(220, 20, 60)); // Red for shortest path
            } else {
                g2d.setColor(new Color(100, 100, 100)); // Dark gray
            }
            g2d.drawString(weightStr, midX - strWidth/2, midY + 4);
        }

        // Draw nodes
        for (Node node : graph.getNodes()) {
            // Check if node is part of shortest path
            boolean isInShortestPath = false;
            for (Edge edge : graph.getShortestPathEdges()) {
                if (edge.getSource().getId() == node.getId() || edge.getTarget().getId() == node.getId()) {
                    isInShortestPath = true;
                    break;
                }
            }

            // Color nodes - no orange when dragging
            if (isInShortestPath) {
                g2d.setColor(new Color(220, 20, 60)); // Red for nodes in shortest path
            } else {
                g2d.setColor(new Color(70, 130, 180));
            }

            g2d.fillOval(node.getX() - node.getRadius(),
                    node.getY() - node.getRadius(),
                    node.getRadius() * 2,
                    node.getRadius() * 2);

            // Draw node border
            if (isInShortestPath) {
                g2d.setColor(new Color(139, 0, 0)); // Dark red border
                g2d.setStroke(new BasicStroke(3));
            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
            }

            g2d.drawOval(node.getX() - node.getRadius(),
                    node.getY() - node.getRadius(),
                    node.getRadius() * 2,
                    node.getRadius() * 2);

            // Draw node label
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String label = (graph.getLabel() != null) ?
                    graph.getLabel()[node.getId()] :
                    String.valueOf(node.getId());
            FontMetrics fm = g2d.getFontMetrics();
            int labelX = node.getX() - fm.stringWidth(label) / 2;
            int labelY = node.getY() + fm.getAscent() / 2 - 2;
            g2d.drawString(label, labelX, labelY);
        }
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, boolean isShortestPath) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = isShortestPath ? 14 : 12;

        // Calculate arrow position near target node
        int arrowX = x2 - (int)(30 * Math.cos(angle));
        int arrowY = y2 - (int)(30 * Math.sin(angle));

        int[] xPoints = {
                arrowX,
                arrowX - (int)(arrowSize * Math.cos(angle - Math.PI / 6)),
                arrowX - (int)(arrowSize * Math.cos(angle + Math.PI / 6))
        };

        int[] yPoints = {
                arrowY,
                arrowY - (int)(arrowSize * Math.sin(angle - Math.PI / 6)),
                arrowY - (int)(arrowSize * Math.sin(angle + Math.PI / 6))
        };

        g2d.fillPolygon(xPoints, yPoints, 3);
    }
}