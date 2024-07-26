public class test {
    public boolean isUnixSymlink() {
        return (getUnixMode() & UnixStat.LINK_FLAG) == UnixStat.LINK_FLAG;
    }
}