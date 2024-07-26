public class test {
    private Object recordDeepStubMock(final Object mock, InvocationContainerImpl container) throws Throwable {

        container.addAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return mock;
            }
        }, false);

        return mock;
    }
    private Object createNewDeepStubMock(GenericMetadataSupport returnTypeGenericMetadata) {
        return mock(
                returnTypeGenericMetadata.rawType(),
                withSettingsUsing(returnTypeGenericMetadata)
        );
    }
    private MockSettings withSettingsUsing(GenericMetadataSupport returnTypeGenericMetadata) {
        MockSettings mockSettings =
                returnTypeGenericMetadata.rawExtraInterfaces().length > 0 ?
                withSettings().extraInterfaces(returnTypeGenericMetadata.rawExtraInterfaces())
                : withSettings();

        return mockSettings
                .defaultAnswer(returnsDeepStubsAnswerUsing(returnTypeGenericMetadata));
    }
    private ReturnsDeepStubs returnsDeepStubsAnswerUsing(final GenericMetadataSupport returnTypeGenericMetadata) {
        return new ReturnsDeepStubs() {
            @Override
            protected GenericMetadataSupport actualParameterizedType(Object mock) {
                return returnTypeGenericMetadata;
            }
        };
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
    public Object answer(InvocationOnMock invocation) throws Throwable {
        GenericMetadataSupport returnTypeGenericMetadata =
                actualParameterizedType(invocation.getMock()).resolveGenericReturnType(invocation.getMethod());

        Class<?> rawType = returnTypeGenericMetadata.rawType();
        if (!new MockCreationValidator().isTypeMockable(rawType)) {
            return delegate.returnValueFor(rawType);
        }

        return getMock(invocation, returnTypeGenericMetadata);
    }
}