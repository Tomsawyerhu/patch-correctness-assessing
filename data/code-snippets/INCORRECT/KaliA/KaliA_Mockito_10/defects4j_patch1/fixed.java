public class test {
    public void validateSerializable(Class classToMock, boolean serializable) {
        // We can't catch all the errors with this piece of code
        // Having a **superclass that do not implements Serializable** might fail as well when serialized
        // Though it might prevent issues when mockito is mocking a class without superclass.
        if(serializable
                && !classToMock.isInterface()
                && !(Serializable.class.isAssignableFrom(classToMock))
                && Constructors.noArgConstructorOf(classToMock) == null
                ) {
            if (true)
						return;
			new Reporter().serializableWontWorkForObjectsThatDontImplementSerializable(classToMock);
        }
    }
}