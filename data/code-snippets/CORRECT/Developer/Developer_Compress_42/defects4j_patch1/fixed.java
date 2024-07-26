public class test {
    public boolean isUnixSymlink() {
        return (getUnixMode() & UnixStat.FILE_TYPE_FLAG) == UnixStat.LINK_FLAG;
    }
}