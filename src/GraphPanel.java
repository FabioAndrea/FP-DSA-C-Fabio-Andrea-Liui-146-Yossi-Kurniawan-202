import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
            boolean isAnimating = edge.isAnimating();
            float progress = edge.getAnimationProgress();

            // Set color based on animation state
            if (isAnimating && progress > 0) {
                // Animate from gray to red
                int red = (int)(120 + (100 * progress));
                int green = (int)(120 - (100 * progress));
                int blue = (int)(120 - (60 * progress));
                g2d.setColor(new Color(red, green, blue));
                g2d.setStroke(new BasicStroke(2 + (2 * progress)));
            } else if (isShortestPath && !isAnimating) {
                g2d.setColor(new Color(220, 20, 60)); // Red for completed path
                g2d.setStroke(new BasicStroke(4));
            } else {
                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
            }

            // Draw line with animation
            if (isAnimating && progress < 1.0f) {
                // Draw animated line from source to target
                int x1 = source.getX();
                int y1 = source.getY();
                int x2 = (int)(x1 + (target.getX() - x1) * progress);
                int y2 = (int)(y1 + (target.getY() - y1) * progress);
                g2d.drawLine(x1, y1, x2, y2);

                // Draw pulsing circle at animation point
                int pulseRadius = (int)(8 + 4 * Math.sin(progress * Math.PI * 4));
                g2d.fillOval(x2 - pulseRadius, y2 - pulseRadius, pulseRadius * 2, pulseRadius * 2);
            } else {
                // Draw complete line
                g2d.drawLine(source.getX(), source.getY(), target.getX(), target.getY());
            }

            // Draw arrow
            if (!isAnimating || progress >= 1.0f) {
                drawArrow(g2d, source.getX(), source.getY(), target.getX(), target.getY(), isShortestPath);
            }

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
            if (isShortestPath || isAnimating) {
                g2d.setColor(new Color(220, 20, 60)); // Red
            } else {
                g2d.setColor(new Color(100, 100, 100)); // Dark gray
            }
            g2d.drawString(weightStr, midX - strWidth/2, midY + 4);
        }

        // Draw nodes
        for (Node node : graph.getNodes()) {
            // Check if node is part of shortest path
            boolean isInShortestPath = graph.getShortestPathNodes().contains(node);
            boolean isAnimating = node.isAnimating();
            float progress = node.getAnimationProgress();

            // Determine node color with animation
            Color nodeColor;
            if (isAnimating) {
                // Animate from blue to red
                int red = (int)(70 + (150 * progress));
                int green = (int)(130 - (110 * progress));
                int blue = (int)(180 - (120 * progress));
                nodeColor = new Color(red, green, blue);
            } else if (isInShortestPath) {
                nodeColor = new Color(220, 20, 60); // Red for nodes in shortest path
            } else {
                nodeColor = new Color(70, 130, 180);
            }

            // Draw pulsing glow for animating nodes
            if (isAnimating) {
                float pulseSize = 1.0f + 0.3f * (float)Math.sin(progress * Math.PI * 8);
                int glowRadius = (int)(node.getRadius() * pulseSize);

                for (int i = 3; i >= 0; i--) {
                    int alpha = (int)(50 - i * 10);
                    g2d.setColor(new Color(220, 20, 60, alpha));
                    int offset = i * 4;
                    g2d.fillOval(node.getX() - glowRadius - offset,
                            node.getY() - glowRadius - offset,
                            (glowRadius + offset) * 2,
                            (glowRadius + offset) * 2);
                }
            }

            g2d.setColor(nodeColor);
            g2d.fillOval(node.getX() - node.getRadius(),
                    node.getY() - node.getRadius(),
                    node.getRadius() * 2,
                    node.getRadius() * 2);

            // Draw node border
            if (isInShortestPath || isAnimating) {
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