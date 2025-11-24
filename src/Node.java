// Node class representing a vertex in the graph
class Node {
    private int id;
    private int x, y;
    private static final int RADIUS = 30;
    private boolean isAnimating = false;
    private float animationProgress = 0f;

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getRadius() { return RADIUS; }
    public boolean isAnimating() { return isAnimating; }
    public void setAnimating(boolean animating) { this.isAnimating = animating; }
    public float getAnimationProgress() { return animationProgress; }
    public void setAnimationProgress(float progress) { this.animationProgress = progress; }

    public boolean contains(int px, int py) {
        int dx = px - x;
        int dy = py - y;
        return dx * dx + dy * dy <= RADIUS * RADIUS;
    }
}