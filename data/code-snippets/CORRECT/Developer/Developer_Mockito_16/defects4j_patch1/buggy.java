public class test {
    public <T> T mock(Class<T> classToMock, MockSettings mockSettings, boolean shouldResetOngoingStubbing) { return mock(classToMock, mockSettings); }
    public <T> T mock(Class<T> classToMock, MockSettings mockSettings) {
        mockingProgress.validateState();
            mockingProgress.resetOngoingStubbing();
        return mockUtil.createMock(classToMock, (MockSettingsImpl) mockSettings);
    }
}