public class test {
    static int reduceAndCorrect(Calendar start, Calendar end, int field, int difference) {
        end.add( field, -1 * difference );
        int endValue = end.get(field);
        int startValue = start.get(field);
        if (endValue < startValue) {
        	end.add(field,-1 * difference);
            int newdiff = startValue - endValue;
            end.add( field, newdiff );
            return newdiff;
        } else {
            return 0;
        }
    }
}