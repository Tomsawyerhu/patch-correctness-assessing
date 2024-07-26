public class test {
    public static JFreeChart createPieChart3D(String title,
                                              PieDataset dataset,
                                              boolean legend,
                                              boolean tooltips,
                                              boolean urls) {

        PiePlot3D plot = new PiePlot3D(dataset);
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        if (tooltips) {
            plot.setToolTipGenerator(new StandardPieToolTipGenerator());
        }
        if (urls) {
            plot.setURLGenerator(new StandardPieURLGenerator());
        }
        return new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, 
                legend);

    }
    public PiePlotState initialise(Graphics2D g2, Rectangle2D plotArea,
            PiePlot plot, Integer index, PlotRenderingInfo info) {
     
        PiePlotState state = new PiePlotState(info);
        state.setPassesRequired(2);
            state.setTotal(DatasetUtilities.calculatePieDatasetTotal(
                    plot.getDataset()));
        state.setLatestAngle(plot.getStartAngle());
        return state;
        
    }
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();
        if (this.dataset == null) {
            return result;
        }
        List keys = this.dataset.getKeys();
        int section = 0;
        Shape shape = getLegendItemShape();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            Number n = this.dataset.getValue(key);
            boolean include = true;
            if (n == null) {
                include = !this.ignoreNullValues;   
            }
            else {
                double v = n.doubleValue();
                if (v == 0.0) {
                    include = !this.ignoreZeroValues;   
                }
                else {
                    include = v > 0.0;   
                }
            }
            if (include) {
                String label = this.legendLabelGenerator.generateSectionLabel(
                        this.dataset, key);
                if (label != null) {
                    String description = label;
                    String toolTipText = null;
                    if (this.legendLabelToolTipGenerator != null) {
                        toolTipText = this.legendLabelToolTipGenerator
                                .generateSectionLabel(this.dataset, key);
                    }
                    String urlText = null;
                    if (this.legendLabelURLGenerator != null) {
                        urlText = this.legendLabelURLGenerator.generateURL(
                                this.dataset, key, this.pieIndex);
                    }
                    Paint paint = lookupSectionPaint(key, true);
                    Paint outlinePaint = lookupSectionOutlinePaint(key);
                    Stroke outlineStroke = lookupSectionOutlineStroke(key);
                    LegendItem item = new LegendItem(label, description, 
                            toolTipText, urlText, true, shape, true, paint, 
                            true, outlinePaint, outlineStroke, 
                            false,          // line not visible
                            new Line2D.Float(), new BasicStroke(), Color.black);
                    item.setDataset(getDataset());
                    result.add(item);
                }
                section++;
            }
            else {
                section++;
            }
        }
        return result;
    }
}