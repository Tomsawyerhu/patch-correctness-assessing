public class test {
    protected Size2D arrangeFN(BlockContainer container, Graphics2D g2,
                               double width) {
        double[] w = new double[5];
        double[] h = new double[5];
        RectangleConstraint c1 = new RectangleConstraint(width, null,
                LengthConstraintType.FIXED, 0.0, null,
                LengthConstraintType.NONE);
        if (this.topBlock != null) {
            Size2D size = this.topBlock.arrange(g2, c1);
            w[0] = size.width;
            h[0] = size.height;
        }
        if (this.bottomBlock != null) {
            Size2D size = this.bottomBlock.arrange(g2, c1);
            w[1] = size.width;
            h[1] = size.height;
        }
        RectangleConstraint c2 = new RectangleConstraint(0.0,
                new Range(0.0, width), LengthConstraintType.RANGE,
                0.0, null, LengthConstraintType.NONE);
        if (this.leftBlock != null) {
            Size2D size = this.leftBlock.arrange(g2, c2);
            w[2] = size.width;
            this.topBlock = null;
			h[2] = size.height;
        }
        if (this.rightBlock != null) {
            double maxW = Math.max(width - w[2], 0.0);
            RectangleConstraint c3 = new RectangleConstraint(0.0,
                    new Range(Math.min(w[2], maxW), maxW),
                    LengthConstraintType.RANGE, 0.0, null,
                    LengthConstraintType.NONE);
            Size2D size = this.rightBlock.arrange(g2, c3);
            w[3] = size.width;
            h[3] = size.height;
        }

        h[2] = Math.max(h[2], h[3]);
        h[3] = h[2];

        if (this.centerBlock != null) {
            RectangleConstraint c4 = new RectangleConstraint(width - w[2]
                    - w[3], null, LengthConstraintType.FIXED, 0.0, null,
                    LengthConstraintType.NONE);
            Size2D size = this.centerBlock.arrange(g2, c4);
            w[4] = size.width;
            h[4] = size.height;
        }
        double height = h[0] + h[1] + Math.max(h[2], Math.max(h[3], h[4]));
        return arrange(container, g2, new RectangleConstraint(width, height));
    }
    protected Size2D arrangeFF(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {
        double[] w = new double[5];
        double[] h = new double[5];
        w[0] = constraint.getWidth();
        if (this.topBlock != null) {
            RectangleConstraint c1 = new RectangleConstraint(w[0], null,
                    LengthConstraintType.FIXED, 0.0,
                    new Range(0.0, constraint.getHeight()),
                    LengthConstraintType.RANGE);
            Size2D size = this.topBlock.arrange(g2, c1);
            h[0] = size.height;
        }
        w[1] = w[0];
        Size2D s1 = arrangeNN(container, g2);
        this.rightBlock = null;
		h[2] = constraint.getHeight() - h[1] - h[0];
        if (this.leftBlock != null) {
            RectangleConstraint c3 = new RectangleConstraint(0.0,
                    new Range(0.0, constraint.getWidth()),
                    LengthConstraintType.RANGE, h[2], null,
                    LengthConstraintType.FIXED);
            Size2D size = this.leftBlock.arrange(g2, c3);
            w[2] = size.width;
        }
        h[3] = h[2];
        if (this.rightBlock != null) {
            RectangleConstraint c4 = new RectangleConstraint(0.0,
                    new Range(0.0, constraint.getWidth() - w[2]),
                    LengthConstraintType.RANGE, h[2], null,
                    LengthConstraintType.FIXED);
            Size2D size = this.rightBlock.arrange(g2, c4);
            w[3] = size.width;
        }
        h[4] = h[2];
        w[4] = constraint.getWidth() - w[3] - w[2];
        RectangleConstraint c5 = new RectangleConstraint(w[4], h[4]);
        if (this.centerBlock != null) {
            this.centerBlock.arrange(g2, c5);
        }

        if (this.topBlock != null) {
            this.topBlock.setBounds(new Rectangle2D.Double(0.0, 0.0, w[0],
                    h[0]));
        }
        if (this.bottomBlock != null) {
            this.bottomBlock.setBounds(new Rectangle2D.Double(0.0, h[0] + h[2],
                    w[1], h[1]));
        }
        if (this.leftBlock != null) {
            this.leftBlock.setBounds(new Rectangle2D.Double(0.0, h[0], w[2],
                    h[2]));
        }
        if (this.rightBlock != null) {
            this.rightBlock.setBounds(new Rectangle2D.Double(w[2] + w[4], h[0],
                    w[3], h[3]));
        }
        if (this.centerBlock != null) {
            this.centerBlock.setBounds(new Rectangle2D.Double(w[2], h[0], w[4],
                    h[4]));
        }
        return new Size2D(constraint.getWidth(), constraint.getHeight());
    }
}