public class test {
    private MockSettings propagateSerializationSettings(MockSettings mockSettings, MockCreationSettings parentMockSettings) {
        return mockSettings.serializable(parentMockSettings.getSerializableMode());
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
                newDeepStubMock(returnTypeGenericMetadata, invocation.getMock()),
                container
        );
    }
    private Object newDeepStubMock(GenericMetadataSupport returnTypeGenericMetadata, Object parentMock) {
        MockCreationSettings parentMockSettings = new MockUtil().getMockSettings(parentMock);
        return mockitoCore().mock(
                returnTypeGenericMetadata.rawType(),
                withSettingsUsing(returnTypeGenericMetadata, parentMockSettings)
        );
    }
    private MockSettings withSettingsUsing(GenericMetadataSupport returnTypeGenericMetadata, MockCreationSettings parentMockSettings) {
        MockSettings mockSettings = returnTypeGenericMetadata.hasRawExtraInterfaces() ?
                withSettings().extraInterfaces(returnTypeGenericMetadata.rawExtraInterfaces())
                : withSettings();

        return propagateSerializationSettings(mockSettings, parentMockSettings)
                .defaultAnswer(returnsDeepStubsAnswerUsing(returnTypeGenericMetadata));
    }
}