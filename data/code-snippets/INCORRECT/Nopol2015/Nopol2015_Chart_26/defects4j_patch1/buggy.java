public class test {
    public void add(Axis axis, RectangleEdge edge) {
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");   
        }
        if (edge == null) {
            throw new IllegalArgumentException("Null 'edge' argument.");   
        }
        if (edge == RectangleEdge.TOP) {
            this.axesAtTop.add(axis);
        }
        else if (edge == RectangleEdge.BOTTOM) {
            this.axesAtBottom.add(axis);
        }
        else if (edge == RectangleEdge.LEFT) {
            this.axesAtLeft.add(axis);
        }
        else if (edge == RectangleEdge.RIGHT) {
            this.axesAtRight.add(axis);
        }
    }
}