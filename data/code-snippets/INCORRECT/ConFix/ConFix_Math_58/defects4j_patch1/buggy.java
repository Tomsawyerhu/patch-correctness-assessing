public class test {
        private Comparator<WeightedObservedPoint> createWeightedObservedPointComparator() {
            return new Comparator<WeightedObservedPoint>() {
                public int compare(WeightedObservedPoint p1, WeightedObservedPoint p2) {
                    if (p1 == null && p2 == null) {
                        return 0;
                    }
                    if (p1 == null) {
                        return -1;
                    }
                    if (p2 == null) {
                        return 1;
                    }
                    if (p1.getX() < p2.getX()) {
                        return -1;
                    }
                    if (p1.getX() > p2.getX()) {
                        return 1;
                    }
                    if (p1.getY() < p2.getY()) {
                        return -1;
                    }
                    if (p1.getY() > p2.getY()) {
                        return 1;
                    }
                    if (p1.getWeight() < p2.getWeight()) {
                        return -1;
                    }
                    if (p1.getWeight() > p2.getWeight()) {
                        return 1;
                    }
                    return 0;
                }
            };
        }
}