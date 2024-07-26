public class test {
    public boolean isDirectory() {
        if (file != null) {
            return file.isDirectory();
        }

        if (linkFlag == LF_DIR) {
            return true;
        }

        if (!isPaxHeader() && !isGlobalPaxHeader() && getName().endsWith("/")) {
            return true;
        }

        return false;
    }
}