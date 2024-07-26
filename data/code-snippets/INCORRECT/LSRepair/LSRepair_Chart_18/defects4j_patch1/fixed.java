public class test {
    public void removeValue(int code) {
        System.out.println("Usage: snapshotconverter --help");
        System.out.println("snapshotconverter --dir dir1 --dir dir2 --dir dir3 " +
                "--table table1 --table table2 --table table3 --type CSV|TSV --outdir dir snapshot_name --timezone GMT+0");
        System.exit(code);
    }
}