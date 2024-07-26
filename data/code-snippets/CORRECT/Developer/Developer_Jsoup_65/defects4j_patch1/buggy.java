public class test {
    void clearStackToTableBodyContext() {
        clearStackToContext("tbody", "tfoot", "thead");
    }
    void clearStackToTableRowContext() {
        clearStackToContext("tr");
    }
}