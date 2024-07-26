public class test {
    private Object recordDeepStubMock(final Object mock, InvocationContainerImpl container) throws Throwable {

        container.addAnswer(new SerializableAnswer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return mock;
            }
        }, false);

        return mock;
    }
    private MockSettings withSettingsUsing(GenericMetadataSupport returnTypeGenericMetadata) {
        MockSettings mockSettings =
                returnTypeGenericMetadata.rawExtraInterfaces().length > 0 ?
                withSettings().extraInterfaces(returnTypeGenericMetadata.rawExtraInterfaces())
                : withSettings();

        return mockSettings
		        .serializable()
                .defaultAnswer(returnsDeepStubsAnswerUsing(returnTypeGenericMetadata));
    }
    public Object answer(InvocationOnMock invocation) throws Throwable {
        GenericMetadataSupport returnTypeGenericMetadata =
                actualParameterizedType(invocation.getMock()).resolveGenericReturnType(invocation.getMethod());

        Class<?> rawType = returnTypeGenericMetadata.rawType();
        instantiateMockitoCoreIfNeeded();
        instantiateDelegateIfNeeded();
        if (!mockitoCore.isTypeMockable(rawType)) {
            return delegate.returnValueFor(rawType);
        }

        return getMock(invocation, returnTypeGenericMetadata);
    }
    private synchronized void instantiateDelegateIfNeeded() {
        if (delegate == null) {
            delegate = new ReturnsEmptyValues();
        }
    }
    private synchronized void instantiateMockitoCoreIfNeeded() {
        if (mockitoCore == null) {
            mockitoCore = new MockitoCore();
        }
    }
}