public class test {
    public static <T> T spy(T object) {
        return MOCKITO_CORE.mock((Class<T>) object.getClass(), withSettings()
                .spiedInstance(object)
                .defaultAnswer(CALLS_REAL_METHODS), true); 
    }
    public static <T> T mock(Class<T> classToMock, MockSettings mockSettings) {
        return MOCKITO_CORE.mock(classToMock, mockSettings, true);
    }
}