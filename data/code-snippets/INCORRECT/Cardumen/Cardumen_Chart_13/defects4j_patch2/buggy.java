public class test {
    public Size2D arrange(BlockContainer container,
                          Graphics2D g2,
                          RectangleConstraint constraint) {
        RectangleConstraint contentConstraint
                = container.toContentConstraint(constraint);
        Size2D contentSize = null;
        LengthConstraintType w = contentConstraint.getWidthConstraintType();
        LengthConstraintType h = contentConstraint.getHeightConstraintType();
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeNN(container, g2);
            }
            else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not implemented.");
            }
            else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");
            }
        }
        else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeFN(container, g2, constraint.getWidth());
            }
            else if (h == LengthConstraintType.FIXED) {
                contentSize = arrangeFF(container, g2, constraint);
            }
            else if (h == LengthConstraintType.RANGE) {
                contentSize = arrangeFR(container, g2, constraint);
            }
        }
        else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not implemented.");
            }
            else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not implemented.");
            }
            else if (h == LengthConstraintType.RANGE) {
                contentSize = arrangeRR(container, constraint.getWidthRange(),
                        constraint.getHeightRange(), g2);
            }
        }
        return new Size2D(container.calculateTotalWidth(contentSize.getWidth()),
                container.calculateTotalHeight(contentSize.getHeight()));
    }
}