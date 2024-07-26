public class test {
    private MockSettings withSettingsUsing(GenericMetadataSupport returnTypeGenericMetadata) {
        MockSettings mockSettings = returnTypeGenericMetadata.hasRawExtraInterfaces() ?
                withSettings().extraInterfaces(returnTypeGenericMetadata.rawExtraInterfaces())
                : withSettings();

        return mockSettings.serializable()
                .defaultAnswer(returnsDeepStubsAnswerUsing(returnTypeGenericMetadata));
    }
    private Object newDeepStubMock(GenericMetadataSupport returnTypeGenericMetadata) {
        return mockitoCore().mock(
                returnTypeGenericMetadata.rawType(),
                withSettingsUsing(returnTypeGenericMetadata)
        );
    }
    private Object deepStub(InvocationOnMock invocation, GenericMetadataSupport returnTypeGenericMetadata) throws Throwable {
        InternalMockHandler<Object> handler = new MockUtil().getMockHandler(invocation.getMock());
        InvocationContainerImpl container = (InvocationContainerImpl) handler.getInvocationContainer();

        // matches invocation for verification
        for (StubbedInvocationMatcher stubbedInvocationMatcher : container.getStubbedInvocations()) {
            if (container.getInvocationForStubbing().matches(stubbedInvocationMatcher.getInvocation())) {
                return stubbedInvocationMatcher.answer(invocation);
            }
        }

        // record deep stub answer
        return recordDeepStubAnswer(
                newDeepStubMock(returnTypeGenericMetadata),
                container
        );
    }
}