class Edge {
    private Node source;
    private Node target;
    private int weight;
    private boolean isAnimating = false;
    private float animationProgress = 0f;

    public Edge(Node source, Node target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }
    public int getWeight() { return weight; }
    public boolean isAnimating() { return isAnimating; }
    public void setAnimating(boolean animating) { this.isAnimating = animating; }
    public float getAnimationProgress() { return animationProgress; }
    public void setAnimationProgress(float progress) { this.animationProgress = progress; }
}