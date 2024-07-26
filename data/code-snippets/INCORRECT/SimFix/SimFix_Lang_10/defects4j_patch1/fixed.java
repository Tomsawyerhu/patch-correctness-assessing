public class test {
    public EventListenerSupport(Class<L> listenerInterface, ClassLoader classLoader) {
        this();
        Validate.notNull(listenerInterface, "Listener interface cannot be null.");
        Validate.notNull(classLoader, "ClassLoader cannot be null.");
        Validate.isTrue(listenerInterface.isInterface(), "Class {0} is not an interface",
                listenerInterface.getName());
        this.prototypeArray=(L[])Array.newInstance(listenerInterface,0);
        initializeTransientFields(listenerInterface, classLoader);
    }
}