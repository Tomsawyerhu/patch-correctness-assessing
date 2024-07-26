public class test {
    public OpenMapRealVector ebeMultiply(RealVector v) {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
        }
        /*
         * MATH-803: the above loop assumes that 0d * x  = 0d for any double x,
         * which allows to consider only the non-zero entries of this. However,
         * this fails if this[i] == 0d and (v[i] = NaN or v[i] = Infinity).
         *
         * These special cases are handled below.
         */
        return res;
    }
    public OpenMapRealVector ebeDivide(RealVector v) {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        /*
         * MATH-803: it is not sufficient to loop through non zero entries of
         * this only. Indeed, if this[i] = 0d and v[i] = 0d, then
         * this[i] / v[i] = NaN, and not 0d.
         */
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() / v.getEntry(iter.key()));
        }
        return res;
    }
}