public class test {
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof AbstractObjectList)) {
            return false;
        }

        AbstractObjectList other = (AbstractObjectList) obj;
        int listSize = size();
        for (int i = 0; i < listSize; i++) {
           if (!ObjectUtilities.equal(get(i), other.get(i))) {
               return false;
           }
        }
        return true;
    }
}