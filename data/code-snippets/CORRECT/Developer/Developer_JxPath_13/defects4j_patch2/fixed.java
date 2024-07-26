public class test {
     protected synchronized String getExternallyRegisteredNamespaceURI(
            String prefix) {
        String uri = (String) namespaceMap.get(prefix);
        return uri == null && parent != null ? parent
                .getExternallyRegisteredNamespaceURI(prefix) : uri;
    }
    protected synchronized String getExternallyRegisteredPrefix(String namespaceURI) {
        if (reverseMap == null) {
            reverseMap = new HashMap();
            Iterator it = namespaceMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                reverseMap.put(entry.getValue(), entry.getKey());
            }
        }
        String prefix = (String) reverseMap.get(namespaceURI);
        return prefix == null && parent != null ? parent
                .getExternallyRegisteredPrefix(namespaceURI) : prefix;
    }
    public synchronized String getPrefix(String namespaceURI) {
        String prefix = getExternallyRegisteredPrefix(namespaceURI);
        return prefix == null && pointer != null ? getPrefix(pointer,
                namespaceURI) : prefix;
    }
    protected static String getPrefix(NodePointer pointer, String namespaceURI) {
        NodePointer currentPointer = pointer;
        while (currentPointer != null) {
            NodeIterator ni = currentPointer.namespaceIterator();
            for (int position = 1; ni != null && ni.setPosition(position); position++) {
                NodePointer nsPointer = ni.getNodePointer();
                String uri = nsPointer.getNamespaceURI();
                if (uri.equals(namespaceURI)) {
                    String prefix = nsPointer.getName().getName();
                    if (!"".equals(prefix)) {
                        return prefix;
                    }
                }
            }
            currentPointer = pointer.getParent();
        }
        return null;
    }
    public synchronized String getNamespaceURI(String prefix) {
        String uri = getExternallyRegisteredNamespaceURI(prefix);
        return uri == null && pointer != null ? pointer.getNamespaceURI(prefix)
                : uri;
    }
}