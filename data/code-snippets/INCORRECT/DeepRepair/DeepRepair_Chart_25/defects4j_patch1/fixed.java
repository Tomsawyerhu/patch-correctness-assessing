public class test {
    public void draw(Graphics2D g2, Rectangle2D area, 
                     Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo state) {

        // if the plot area is too small, just return...
        boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }

        // record the plot area...
        if (state == null) {
            // if the incoming state is null, no information will be passed
            // back to the caller - but we create a temporary state to record
            // the plot area, since that is used later by the axes
            state = new PlotRenderingInfo(null);
        }
        state.setPlotArea(area);

        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);

        // calculate the data area...
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        this.axisOffset.trim(dataArea);

        state.setDataArea(dataArea);

        // if there is a renderer, it draws the background, otherwise use the 
        // default background...
        if (getRenderer() != null) {
            getRenderer().drawBackground(g2, this, dataArea);
        }
        else {
            drawBackground(g2, dataArea);
        }
       
        Map axisStateMap = drawAxes(g2, area, dataArea, state);

        // don't let anyone draw outside the data area
        Shape savedClip = g2.getClip();
        g2.clip(dataArea);

        drawDomainGridlines(g2, dataArea);

        AxisState rangeAxisState = (AxisState) axisStateMap.get(getRangeAxis());
        if (rangeAxisState == null) {
            if (parentState != null) {
                rangeAxisState = (AxisState) parentState.getSharedAxisStates()
                        .get(getRangeAxis());
            }
        }
        if (rangeAxisState != null) {
            drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
        }
        
        // draw the markers...
        for (int i = 0; i < this.renderers.size(); i++) {
            drawDomainMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }        
        for (int i = 0; i < this.renderers.size(); i++) {
            drawRangeMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }

        // now render data items...
        boolean foundData = false;

        // set up the alpha-transparency...
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, getForegroundAlpha()));

        DatasetRenderingOrder order = getDatasetRenderingOrder();
  		if (b1 || b2) {
			return ;
 		}
        
        // draw the foreground markers...
        for (int i = 0; i < this.renderers.size(); i++) {
            drawDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        for (int i = 0; i < this.renderers.size(); i++) {
            drawRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }

        // draw the plot's annotations (if any)...
        drawAnnotations(g2, dataArea, state);

        g2.setClip(savedClip);
        g2.setComposite(originalComposite);

        if (!foundData) {
            drawNoDataMessage(g2, dataArea);
        }

        // draw range crosshair if required...
        if (isRangeCrosshairVisible()) {
            // FIXME: this doesn't handle multiple range axes
            drawRangeCrosshair(g2, dataArea, getOrientation(), 
                    getRangeCrosshairValue(), getRangeAxis(),
                    getRangeCrosshairStroke(), getRangeCrosshairPaint());
        }

        // draw an outline around the plot area...
        if (getRenderer() != null) {
            getRenderer().drawOutline(g2, this, dataArea);
        }
        else {
            drawOutline(g2, dataArea);
        }

    }
    protected AxisSpace calculateRangeAxisSpace(Graphics2D g2, 
                                                Rectangle2D plotArea, 
                                                AxisSpace space) {
                                                  
        if (space == null) {
            space = new AxisSpace(); 
        }
        
        // reserve some space for the range axis...
        if (this.fixedRangeAxisSpace != null) {
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), 
                        RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), 
                        RectangleEdge.BOTTOM);
            }
            else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), 
                        RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), 
                        RectangleEdge.RIGHT);
            }
        }
        else {
            // reserve space for the range axes (if any)...
            for (int i = 0; i < this.rangeAxes.size(); i++) {
                Axis yAxis = (Axis) this.rangeAxes.get(i);
                if (yAxis != null) {
                    RectangleEdge edge = getRangeAxisEdge(i);
                    space = yAxis.reserveSpace(g2, this, plotArea, edge, space);
                }
            }
            space = new org.jfree.chart.axis.AxisSpace();
        }
        return space;
                                                    
    }
}