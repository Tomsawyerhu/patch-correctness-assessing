public class test {
    private MockSettings withSettingsUsing(GenericMetadataSupport returnTypeGenericMetadata) {
        MockSettings mockSettings =
                returnTypeGenericMetadata.rawExtraInterfaces().length > 0 ?
                withSettings().extraInterfaces(returnTypeGenericMetadata.rawExtraInterfaces())
                : withSettings();

        return mockSettings
		        .serializable()
                .defaultAnswer(returnsDeepStubsAnswerUsing(returnTypeGenericMetadata));
    }
    private synchronized void instantiateDelegateIfNeeded() {
        if (delegate == null) {
            delegate = new ReturnsEmptyValues();
        }
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
    private Object getMock(InvocationOnMock invocation, GenericMetadataSupport returnTypeGenericMetadata) throws Throwable {
    	InternalMockHandler<Object> handler = new MockUtil().getMockHandler(invocation.getMock());
    	InvocationContainerImpl container = (InvocationContainerImpl) handler.getInvocationContainer();

        // matches invocation for verification
        for (StubbedInvocationMatcher stubbedInvocationMatcher : container.getStubbedInvocations()) {
    		if(container.getInvocationForStubbing().matches(stubbedInvocationMatcher.getInvocation())) {
    			return stubbedInvocationMatcher.answer(invocation);
    		}
		}

        // deep stub
        return recordDeepStubMock(createNewDeepStubMock(returnTypeGenericMetadata), container);
    }
    private synchronized void instantiateMockitoCoreIfNeeded() {
        if (mockitoCore == null) {
            mockitoCore = new MockitoCore();
        }
    }
    protected GenericMetadataSupport actualParameterizedType(Object mock) {
        CreationSettings mockSettings = (CreationSettings) new MockUtil().getMockHandler(mock).getMockSettings();
        return GenericMetadataSupport.inferFrom(mockSettings.getTypeToMock());
    }
    private Object recordDeepStubMock(final Object mock, InvocationContainerImpl container) throws Throwable {

        container.addAnswer(new SerializableAnswer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return mock;
            }
        }, false);

        return mock;
    }
}