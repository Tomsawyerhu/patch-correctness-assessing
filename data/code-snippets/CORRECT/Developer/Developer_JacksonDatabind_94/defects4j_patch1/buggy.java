public class test {
    public void validateSubType(DeserializationContext ctxt, JavaType type) throws JsonMappingException
    {
        // There are certain nasty classes that could cause problems, mostly
        // via default typing -- catch them here.
        final Class<?> raw = type.getRawClass();
        String full = raw.getName();

        main_check:
        do {
            if (_cfgIllegalClassNames.contains(full)) {
                break;
            }

            // 18-Dec-2017, tatu: As per [databind#1855], need bit more sophisticated handling
            //    for some Spring framework types
            // 05-Jan-2017, tatu: ... also, only applies to classes, not interfaces
            if (raw.isInterface()) {
                ;
            } else if (full.startsWith(PREFIX_SPRING)) {
                for (Class<?> cls = raw; (cls != null) && (cls != Object.class); cls = cls.getSuperclass()){
                    String name = cls.getSimpleName();
                    // looking for "AbstractBeanFactoryPointcutAdvisor" but no point to allow any is there?
                    if ("AbstractPointcutAdvisor".equals(name)
                            // ditto  for "FileSystemXmlApplicationContext": block all ApplicationContexts
                            || "AbstractApplicationContext".equals(name)) {
                        break main_check;
                    }
                // [databind#1737]; more 3rd party
                // s.add("com.mchange.v2.c3p0.JndiRefForwardingDataSource");
                // s.add("com.mchange.v2.c3p0.WrapperConnectionPoolDataSource");
                // [databind#1931]; more 3rd party
                // com.mchange.v2.c3p0.ComboPooledDataSource
                // com.mchange.v2.c3p0.debug.AfterCloseLoggingComboPooledDataSource 
                }
            }
            return;
        } while (false);

        throw JsonMappingException.from(ctxt,
                String.format("Illegal type (%s) to deserialize: prevented for security reasons", full));
    }
}