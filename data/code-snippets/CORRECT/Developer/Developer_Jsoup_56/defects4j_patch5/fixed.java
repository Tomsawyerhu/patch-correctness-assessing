public class test {
        Token reset() {
            reset(name);
            pubSysKey = null;
            reset(publicIdentifier);
            reset(systemIdentifier);
            forceQuirks = false;
            return this;
        }
        String getPubSysKey() {
            return pubSysKey;
        }
}