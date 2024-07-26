public class test {
    public void writeEmbeddedObject(Object object) throws IOException {
        // 01-Sep-2016, tatu: As per [core#318], handle small number of cases
        if (object == null) {
            writeNull();
            return;
        }
        if (object instanceof byte[]) {
            writeBinary((byte[]) object);
            return;
        }
        throw new JsonGenerationException("No native support for writing embedded objects of type "
                +object.getClass().getName(),
                this);
    }
}