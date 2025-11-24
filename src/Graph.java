import java.util.ArrayList;

class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private int[][] adjacencyMatrix;
    private String[] label;
    private ArrayList<Edge> shortestPathEdges;
    private ArrayList<Node> shortestPathNodes;

    public Graph(int[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.shortestPathEdges = new ArrayList<>();
        this.shortestPathNodes = new ArrayList<>();
        initializeGraph();
    }

    public Graph(int[][] adjacencyMatrix, String[] l) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.label = l;
        this.shortestPathEdges = new ArrayList<>();
        this.shortestPathNodes = new ArrayList<>();
        initializeGraph();
    }

    private void initializeGraph() {
        int n = adjacencyMatrix.length;

        // Create nodes in circular layout
        int centerX = 500;
        int centerY = 350;
        int radius = 250;

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            nodes.add(new Node(i, x, y));
        }

        // Create edges from adjacency matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] != 0) {
                    edges.add(new Edge(nodes.get(i), nodes.get(j), adjacencyMatrix[i][j]));
                }
            }
        }
    }

    // Dijkstra's algorithm to find shortest path
    public int findShortestPath(int start, int end) {
        int n = adjacencyMatrix.length;
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];

        // Initialize
        for (int i = 0; i < n; i++) {
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
        }
        dist[start] = 0;

        // Dijkstra's algorithm
        for (int i = 0; i < n; i++) {
            int u = -1;
            int minDist = Integer.MAX_VALUE;

            // Find unvisited node with minimum distance
            for (int j = 0; j < n; j++) {
                if (!visited[j] && dist[j] < minDist) {
                    minDist = dist[j];
                    u = j;
                }
            }

            if (u == -1) break;
            visited[u] = true;

            // Update distances to neighbors
            for (int v = 0; v < n; v++) {
                if (adjacencyMatrix[u][v] != 0 && !visited[v]) {
                    int alt = dist[u] + adjacencyMatrix[u][v];
                    if (alt < dist[v]) {
                        dist[v] = alt;
                        prev[v] = u;
                    }
                }
            }
        }

        // Reconstruct path and mark edges
        shortestPathEdges.clear();
        shortestPathNodes.clear();

        if (dist[end] != Integer.MAX_VALUE) {
            // Build path nodes
            int current = end;
            while (current != -1) {
                shortestPathNodes.add(0, nodes.get(current));
                current = prev[current];
            }

            // Build path edges
            for (int i = 0; i < shortestPathNodes.size() - 1; i++) {
                int from = shortestPathNodes.get(i).getId();
                int to = shortestPathNodes.get(i + 1).getId();

                for (Edge edge : edges) {
                    if (edge.getSource().getId() == from && edge.getTarget().getId() == to) {
                        shortestPathEdges.add(edge);
                        break;
                    }
                }
            }
        }
        return dist[end];
    }

    public void resetAnimation() {
        for (Node node : nodes) {
            node.setAnimating(false);
            node.setAnimationProgress(0f);
        }
        for (Edge edge : edges) {
            edge.setAnimating(false);
            edge.setAnimationProgress(0f);
        }
    }

    public ArrayList<Node> getNodes() { return nodes; }
    public ArrayList<Edge> getEdges() { return edges; }
    public int[][] getAdjacencyMatrix() { return adjacencyMatrix; }
    public String[] getLabel() { return label; }
    public ArrayList<Edge> getShortestPathEdges() { return shortestPathEdges; }
    public ArrayList<Node> getShortestPathNodes() { return shortestPathNodes; }
}