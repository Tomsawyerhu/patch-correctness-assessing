public class test {
    public boolean canInstantiate() {
        return canCreateUsingDefault()
                || canCreateUsingDelegate() || canCreateUsingArrayDelegate()
                || canCreateFromObjectWith() || canCreateFromString()
                || canCreateFromInt() || canCreateFromLong()
                || canCreateFromDouble() || canCreateFromBoolean();
    }
}