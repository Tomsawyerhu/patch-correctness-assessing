public class test {
    public void draw(Graphics2D g2, Rectangle2D plotArea, Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {

        // adjust for insets...
        RectangleInsets insets = getInsets();
        insets.trim(plotArea);

        Rectangle2D originalPlotArea = (Rectangle2D) plotArea.clone();
        if (info != null) {
            info.setPlotArea(plotArea);
            info.setDataArea(plotArea);
        }
		if (info == null) {
			return;
		}

        drawBackground(g2, plotArea);

        Shape savedClip = g2.getClip();
        g2.clip(plotArea);

        // adjust the plot area by the interior spacing value
        double gapPercent = getInteriorGap();
        double labelPercent = 0.0;
        if (getLabelGenerator() != null) {
            labelPercent = getLabelGap() + getMaximumLabelWidth();   
        }
        double gapHorizontal = plotArea.getWidth() * (gapPercent 
                + labelPercent) * 2.0;
        double gapVertical = plotArea.getHeight() * gapPercent * 2.0;

        if (DEBUG_DRAW_INTERIOR) {
            double hGap = plotArea.getWidth() * getInteriorGap();
            double vGap = plotArea.getHeight() * getInteriorGap();
            double igx1 = plotArea.getX() + hGap;
            double igx2 = plotArea.getMaxX() - hGap;
            double igy1 = plotArea.getY() + vGap;
            double igy2 = plotArea.getMaxY() - vGap;
            g2.setPaint(Color.lightGray);
            g2.draw(new Rectangle2D.Double(igx1, igy1, igx2 - igx1, 
                    igy2 - igy1));
        }

        double linkX = plotArea.getX() + gapHorizontal / 2;
        double linkY = plotArea.getY() + gapVertical / 2;
        double linkW = plotArea.getWidth() - gapHorizontal;
        double linkH = plotArea.getHeight() - gapVertical;
        
        // make the link area a square if the pie chart is to be circular...
        if (isCircular()) { // is circular?
            double min = Math.min(linkW, linkH) / 2;
            linkX = (linkX + linkX + linkW) / 2 - min;
            linkY = (linkY + linkY + linkH) / 2 - min;
            linkW = 2 * min;
            linkH = 2 * min;
        }
        
        PiePlotState state = initialise(g2, plotArea, this, null, info);

        // the link area defines the dog leg points for the linking lines to 
        // the labels
        Rectangle2D linkAreaXX = new Rectangle2D.Double(linkX, linkY, linkW, 
                linkH * (1 - this.depthFactor));
        state.setLinkArea(linkAreaXX);

        if (DEBUG_DRAW_LINK_AREA) {
            g2.setPaint(Color.blue);
            g2.draw(linkAreaXX);
            g2.setPaint(Color.yellow);
            g2.draw(new Ellipse2D.Double(linkAreaXX.getX(), linkAreaXX.getY(), 
                    linkAreaXX.getWidth(), linkAreaXX.getHeight()));
        }
        
        // the explode area defines the max circle/ellipse for the exploded pie 
        // sections.
        // it is defined by shrinking the linkArea by the linkMargin factor.
        double hh = linkW * getLabelLinkMargin();
        double vv = linkH * getLabelLinkMargin();
        Rectangle2D explodeArea = new Rectangle2D.Double(linkX + hh / 2.0, 
                linkY + vv / 2.0, linkW - hh, linkH - vv);
       
        state.setExplodedPieArea(explodeArea);
        
        // the pie area defines the circle/ellipse for regular pie sections.
        // it is defined by shrinking the explodeArea by the explodeMargin 
        // factor. 
        double maximumExplodePercent = getMaximumExplodePercent();
        double percent = maximumExplodePercent / (1.0 + maximumExplodePercent);
        
        double h1 = explodeArea.getWidth() * percent;
        double v1 = explodeArea.getHeight() * percent;
        Rectangle2D pieArea = new Rectangle2D.Double(explodeArea.getX() 
                + h1 / 2.0, explodeArea.getY() + v1 / 2.0,
                explodeArea.getWidth() - h1, explodeArea.getHeight() - v1);

        // the link area defines the dog-leg point for the linking lines to 
        // the labels
        int depth = (int) (pieArea.getHeight() * this.depthFactor);
        Rectangle2D linkArea = new Rectangle2D.Double(linkX, linkY, linkW, 
                linkH - depth);
        state.setLinkArea(linkArea);   

        state.setPieArea(pieArea);
        state.setPieCenterX(pieArea.getCenterX());
        state.setPieCenterY(pieArea.getCenterY() - depth / 2.0);
        state.setPieWRadius(pieArea.getWidth() / 2.0);
        state.setPieHRadius((pieArea.getHeight() - depth) / 2.0);

        // get the data source - return if null;
        PieDataset dataset = getDataset();
        if (DatasetUtilities.isEmptyOrNull(getDataset())) {
            drawNoDataMessage(g2, plotArea);
            g2.setClip(savedClip);
            drawOutline(g2, plotArea);
            return;
        }

        // if too any elements
        if (dataset.getKeys().size() > plotArea.getWidth()) {
            String text = "Too many elements";
            Font sfont = new Font("dialog", Font.BOLD, 10);
            g2.setFont(sfont);
            FontMetrics fm = g2.getFontMetrics(sfont);
            int stringWidth = fm.stringWidth(text);

            g2.drawString(text, (int) (plotArea.getX() + (plotArea.getWidth() 
                    - stringWidth) / 2), (int) (plotArea.getY() 
                    + (plotArea.getHeight() / 2)));
            return;
        }
        // if we are drawing a perfect circle, we need to readjust the top left
        // coordinates of the drawing area for the arcs to arrive at this
        // effect.
        if (isCircular()) {
            double min = Math.min(plotArea.getWidth(), 
                    plotArea.getHeight()) / 2;
            plotArea = new Rectangle2D.Double(plotArea.getCenterX() - min, 
                    plotArea.getCenterY() - min, 2 * min, 2 * min);
        }
        // get a list of keys...
        List sectionKeys = dataset.getKeys();

        if (sectionKeys.size() == 0) {
            return;
        }

        // establish the coordinates of the top left corner of the drawing area
        double arcX = pieArea.getX();
        double arcY = pieArea.getY();

        //g2.clip(clipArea);
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                getForegroundAlpha()));

        double totalValue = DatasetUtilities.calculatePieDatasetTotal(dataset);
        double runningTotal = 0;
        if (depth < 0) {
            return;  // if depth is negative don't draw anything
        }

        ArrayList arcList = new ArrayList();
        Arc2D.Double arc;
        Paint paint;
        Paint outlinePaint;
        Stroke outlineStroke;

        Iterator iterator = sectionKeys.iterator();
        while (iterator.hasNext()) {

            Comparable currentKey = (Comparable) iterator.next();
            Number dataValue = dataset.getValue(currentKey);
            if (dataValue == null) {
                arcList.add(null);
                continue;
            }
            double value = dataValue.doubleValue();
            if (value <= 0) {
                arcList.add(null);
                continue;
            }
            double startAngle = getStartAngle();
            double direction = getDirection().getFactor();
            double angle1 = startAngle + (direction * (runningTotal * 360)) 
                    / totalValue;
            double angle2 = startAngle + (direction * (runningTotal + value) 
                    * 360) / totalValue;
            if (Math.abs(angle2 - angle1) > getMinimumArcAngleToDraw()) {
                arcList.add(new Arc2D.Double(arcX, arcY + depth, 
                        pieArea.getWidth(), pieArea.getHeight() - depth,
                        angle1, angle2 - angle1, Arc2D.PIE));
            }
            else {
                arcList.add(null);
            }
            runningTotal += value;
        }

        Shape oldClip = g2.getClip();

        Ellipse2D top = new Ellipse2D.Double(pieArea.getX(), pieArea.getY(), 
                pieArea.getWidth(), pieArea.getHeight() - depth);

        Ellipse2D bottom = new Ellipse2D.Double(pieArea.getX(), pieArea.getY() 
                + depth, pieArea.getWidth(), pieArea.getHeight() - depth);

        Rectangle2D lower = new Rectangle2D.Double(top.getX(), 
                top.getCenterY(), pieArea.getWidth(), bottom.getMaxY() 
                - top.getCenterY());

        Rectangle2D upper = new Rectangle2D.Double(pieArea.getX(), top.getY(), 
                pieArea.getWidth(), bottom.getCenterY() - top.getY());

        Area a = new Area(top);
        a.add(new Area(lower));
        Area b = new Area(bottom);
        b.add(new Area(upper));
        Area pie = new Area(a);
        pie.intersect(b);

        Area front = new Area(pie);
        front.subtract(new Area(top));

        Area back = new Area(pie);
        back.subtract(new Area(bottom));

        // draw the bottom circle
        int[] xs;
        int[] ys;
        arc = new Arc2D.Double(arcX, arcY + depth, pieArea.getWidth(), 
                pieArea.getHeight() - depth, 0, 360, Arc2D.PIE);

        int categoryCount = arcList.size();
        for (int categoryIndex = 0; categoryIndex < categoryCount; 
                 categoryIndex++) {
            arc = (Arc2D.Double) arcList.get(categoryIndex);
            if (arc == null) {
                continue;
            }
            Comparable key = getSectionKey(categoryIndex);
            paint = lookupSectionPaint(key, true);
            outlinePaint = lookupSectionOutlinePaint(key);
            outlineStroke = lookupSectionOutlineStroke(key);
            g2.setPaint(paint);
            g2.fill(arc);
            g2.setPaint(outlinePaint);
            g2.setStroke(outlineStroke);
            g2.draw(arc);
            g2.setPaint(paint);

            Point2D p1 = arc.getStartPoint();

            // draw the height
            xs = new int[] {(int) arc.getCenterX(), (int) arc.getCenterX(),
                    (int) p1.getX(), (int) p1.getX()};
            ys = new int[] {(int) arc.getCenterY(), (int) arc.getCenterY() 
                    - depth, (int) p1.getY() - depth, (int) p1.getY()};
            Polygon polygon = new Polygon(xs, ys, 4);
            g2.setPaint(java.awt.Color.lightGray);
            g2.fill(polygon);
            g2.setPaint(outlinePaint);
            g2.setStroke(outlineStroke);
            g2.draw(polygon);
            g2.setPaint(paint);

        }

        g2.setPaint(Color.gray);
        g2.fill(back);
        g2.fill(front);

        // cycle through once drawing only the sides at the back...
        int cat = 0;
        iterator = arcList.iterator();
        while (iterator.hasNext()) {
            Arc2D segment = (Arc2D) iterator.next();
            if (segment != null) {
                Comparable key = getSectionKey(cat);
                paint = lookupSectionPaint(key, true);
                outlinePaint = lookupSectionOutlinePaint(key);
                outlineStroke = lookupSectionOutlineStroke(key);
                drawSide(g2, pieArea, segment, front, back, paint, 
                        outlinePaint, outlineStroke, false, true);
            }
            cat++;
        }

        // cycle through again drawing only the sides at the front...
        cat = 0;
        iterator = arcList.iterator();
        while (iterator.hasNext()) {
            Arc2D segment = (Arc2D) iterator.next();
            if (segment != null) {
                Comparable key = getSectionKey(cat);
                paint = lookupSectionPaint(key);
                outlinePaint = lookupSectionOutlinePaint(key);
                outlineStroke = lookupSectionOutlineStroke(key);
                drawSide(g2, pieArea, segment, front, back, paint, 
                        outlinePaint, outlineStroke, true, false);
            }
            cat++;
        }

        g2.setClip(oldClip);

        // draw the sections at the top of the pie (and set up tooltips)...
        Arc2D upperArc;
        for (int sectionIndex = 0; sectionIndex < categoryCount; 
                 sectionIndex++) {
            arc = (Arc2D.Double) arcList.get(sectionIndex);
            if (arc == null) {
                continue;
            }
            upperArc = new Arc2D.Double(arcX, arcY, pieArea.getWidth(),
                    pieArea.getHeight() - depth, arc.getAngleStart(), 
                    arc.getAngleExtent(), Arc2D.PIE);
            
            Comparable currentKey = (Comparable) sectionKeys.get(sectionIndex);
            paint = lookupSectionPaint(currentKey, true);
            outlinePaint = lookupSectionOutlinePaint(currentKey);
            outlineStroke = lookupSectionOutlineStroke(currentKey);
            g2.setPaint(paint);
            g2.fill(upperArc);
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(upperArc);

           // add a tooltip for the section...
            if (info != null) {
                EntityCollection entities 
                        = info.getOwner().getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    PieToolTipGenerator tipster = getToolTipGenerator();
                    if (tipster != null) {
                        // @mgs: using the method's return value was missing 
                        tip = tipster.generateToolTip(dataset, currentKey);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(dataset, currentKey,
                                getPieIndex());
                    }
                    PieSectionEntity entity = new PieSectionEntity(
                            upperArc, dataset, getPieIndex(), sectionIndex, 
                            currentKey, tip, url);
                    entities.add(entity);
                }
            }
            List keys = dataset.getKeys();
            Rectangle2D adjustedPlotArea = new Rectangle2D.Double(
                    originalPlotArea.getX(), originalPlotArea.getY(), 
                    originalPlotArea.getWidth(), originalPlotArea.getHeight() 
                    - depth);
            if (getSimpleLabels()) {
                drawSimpleLabels(g2, keys, totalValue, adjustedPlotArea, 
                        linkArea, state);
            }
            else {
                drawLabels(g2, keys, totalValue, adjustedPlotArea, linkArea, 
                        state);
            }
        }

        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, originalPlotArea);

    }
}