public class test {
    public OpenMapRealVector ebeDivide(double[] v) {
        checkVectorDimensions(v.length);
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() / v[iter.key()]);
        }
        return res;
    }
    public OpenMapRealVector ebeMultiply(RealVector v) {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
        }
        return res;
    }
    public OpenMapRealVector ebeMultiply(double[] v) {
        checkVectorDimensions(v.length);
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v[iter.key()]);
        }
        return res;
    }
    public OpenMapRealVector ebeDivide(RealVector v) {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() / v.getEntry(iter.key()));
        }
        return res;
    }
}