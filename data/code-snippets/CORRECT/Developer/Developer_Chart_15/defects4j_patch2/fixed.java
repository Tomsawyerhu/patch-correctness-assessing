public class test {
    public PiePlotState initialise(Graphics2D g2, Rectangle2D plotArea,
            PiePlot plot, Integer index, PlotRenderingInfo info) {
     
        PiePlotState state = new PiePlotState(info);
        state.setPassesRequired(2);
        if (this.dataset != null) {
            state.setTotal(DatasetUtilities.calculatePieDatasetTotal(
                    plot.getDataset()));
        }
        state.setLatestAngle(plot.getStartAngle());
        return state;
        
    }
    public double getMaximumExplodePercent() {
        if (this.dataset == null) {
            return 0.0;
        }
        double result = 0.0;
        Iterator iterator = this.dataset.getKeys().iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            Number explode = (Number) this.explodePercentages.get(key);
            if (explode != null) {
                result = Math.max(result, explode.doubleValue());   
            }
        }
        return result;
    }
}