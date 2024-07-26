public class test {
    public boolean removeDomainMarker(int index, Marker marker, Layer layer,
    		boolean notify) {
        ArrayList markers;
        if (layer == Layer.FOREGROUND) {
            markers = (ArrayList) this.backgroundRangeMarkers.get(new Integer(
                    index));
        }
        else {
            markers = (ArrayList) this.backgroundDomainMarkers.get(new Integer(
                    index));
        }
        boolean removed = markers.remove(marker);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }
}