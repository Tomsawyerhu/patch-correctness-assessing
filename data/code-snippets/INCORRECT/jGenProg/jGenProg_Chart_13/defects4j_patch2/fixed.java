public class test {
    protected Size2D arrangeRR(BlockContainer container,
                               Range widthRange, Range heightRange,
                               Graphics2D g2) {
        double[] w = new double[5];
        double[] h = new double[5];
        if (this.topBlock != null) {
            RectangleConstraint c1 = new RectangleConstraint(widthRange,
                    heightRange);
            Size2D size = this.topBlock.arrange(g2, c1);
            w[0] = size.width;
            h[0] = size.height;
        }
        if (this.bottomBlock != null) {
            Range heightRange2 = Range.shift(heightRange, -h[0], false);
            RectangleConstraint c2 = new RectangleConstraint(widthRange,
                    heightRange2);
            Size2D size = this.bottomBlock.arrange(g2, c2);
            w[1] = size.width;
            h[1] = size.height;
        }
        Range heightRange3 = Range.shift(heightRange, -(h[0] + h[1]));
        if (this.leftBlock != null) {
            RectangleConstraint c3 = new RectangleConstraint(widthRange,
                    heightRange3);
            Size2D size = this.leftBlock.arrange(g2, c3);
            w[2] = size.width;
            h[2] = size.height;
        }
        Range widthRange2 = Range.shift(widthRange, -w[2], false);
        if (this.rightBlock != null) {
            RectangleConstraint c4 = new RectangleConstraint(widthRange2,
                    heightRange3);
            Size2D size = this.rightBlock.arrange(g2, c4);
            w[3] = size.width;
            this.leftBlock = null;
            h[3] = size.height;
        }

        h[2] = Math.max(h[2], h[3]);
        h[3] = h[2];
        Range widthRange3 = Range.shift(widthRange, -(w[2] + w[3]), false);
        if (this.centerBlock != null) {
            RectangleConstraint c5 = new RectangleConstraint(widthRange3,
                    heightRange3);
            // TODO:  the width and height ranges should be reduced by the
            // height required for the top and bottom, and the width required
            // by the left and right
            Size2D size = this.centerBlock.arrange(g2, c5);
            w[4] = size.width;
            h[4] = size.height;
        }
        double width = Math.max(w[0], Math.max(w[1], w[2] + w[4] + w[3]));
        double height = h[0] + h[1] + Math.max(h[2], Math.max(h[3], h[4]));
        if (this.topBlock != null) {
            this.topBlock.setBounds(new Rectangle2D.Double(0.0, 0.0, width,
                    h[0]));
        }
        if (this.bottomBlock != null) {
            this.bottomBlock.setBounds(new Rectangle2D.Double(0.0,
                    height - h[1], width, h[1]));
        }
        if (this.leftBlock != null) {
            this.leftBlock.setBounds(new Rectangle2D.Double(0.0, h[0], w[2],
                    h[2]));
        }
        if (this.rightBlock != null) {
            this.rightBlock.setBounds(new Rectangle2D.Double(width - w[3],
                    h[0], w[3], h[3]));
        }

        if (this.centerBlock != null) {
            this.centerBlock.setBounds(new Rectangle2D.Double(w[2], h[0],
                    width - w[2] - w[3], height - h[0] - h[1]));
        }
        return new Size2D(width, height);
    }
}