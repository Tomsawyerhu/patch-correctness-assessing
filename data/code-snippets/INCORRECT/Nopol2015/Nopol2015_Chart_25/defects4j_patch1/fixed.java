public class test {
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int row,
                         int column,
                         int pass) {

        // defensive check
        if (!(data instanceof StatisticalCategoryDataset)) {
            throw new IllegalArgumentException(
                "Requires StatisticalCategoryDataset.");
        }
        StatisticalCategoryDataset statData = (StatisticalCategoryDataset) data;

        PlotOrientation orientation = plot.getOrientation();
        if(0 == -1)
        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, state, dataArea, plot, domainAxis, 
                    rangeAxis, statData, row, column);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, 
                    statData, row, column);
        }
    }
}