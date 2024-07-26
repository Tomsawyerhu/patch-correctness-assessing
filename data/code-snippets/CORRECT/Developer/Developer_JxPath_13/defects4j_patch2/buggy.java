public class test {
    public synchronized String getPrefix(String namespaceURI) {

    /**
     * Get the nearest prefix found that matches an externally-registered namespace. 
     * @param namespaceURI
     * @return String prefix if found.
     * @since JXPath 1.3
     */
        if (reverseMap == null) {
            reverseMap = new HashMap();
            NodeIterator ni = pointer.namespaceIterator();
            if (ni != null) {
                for (int position = 1; ni.setPosition(position); position++) {
                    NodePointer nsPointer = ni.getNodePointer();
                    String uri = nsPointer.getNamespaceURI();                    
                    String prefix = nsPointer.getName().getName();
                    if (!"".equals(prefix)) {
                        reverseMap.put(uri, prefix);
                    }
                }
            }
            Iterator it = namespaceMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                reverseMap.put(entry.getValue(), entry.getKey());
            }
        }
        String prefix = (String) reverseMap.get(namespaceURI);
        if (prefix == null && parent != null) {
            return parent.getPrefix(namespaceURI);
        }
        return prefix;
    }
    public synchronized String getNamespaceURI(String prefix) {

    /**
     * Given a prefix, returns an externally registered namespace URI.
     * 
     * @param prefix The namespace prefix to look up
     * @return namespace URI or null if the prefix is undefined.
     * @since JXPath 1.3
     */
        String uri = (String) namespaceMap.get(prefix);
        if (uri == null && pointer != null) {
            uri = pointer.getNamespaceURI(prefix);
        }
        if (uri == null && parent != null) {
            return parent.getNamespaceURI(prefix);
        }
        return uri;
    }
}