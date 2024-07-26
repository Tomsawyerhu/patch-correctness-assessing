public class test {
    void clearStackToTableBodyContext() {
        clearStackToContext("tbody", "tfoot", "thead", "template");
    }
    void clearStackToTableRowContext() {
        clearStackToContext("tr", "template");
    }
}