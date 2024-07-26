public class test {
        public boolean hasNext() {
            for (int i = 0; i < dimension; i++) {
                if (counter[i] != size[i] - 1) {
                    return true;
                }
            }
            return false;
        }
}